

// MCP4725 is a Digital-to-Analog converter using I2C
// datasheet: http://ww1.microchip.com/downloads/en/DeviceDoc/22039d.pdf

// also see DigitalAnalog_I2C_MCP4725 for how to write the
// same sketch in an object-oriented way
private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}
byte address=0x29;

void writeReg(byte reg, byte val)
{
   i2c.beginTransmission(0x29);
  i2c.write(reg);
  i2c.write(val);
  i2c.endTransmission();
}
byte [] readRegs(byte reg, int cnt)
{
   i2c.beginTransmission(0x29);
   i2c.write(reg);
   byte[] in = i2c.read(cnt);
   i2c.endTransmission();
   return in;
}
byte [] readRegs(int reg, int cnt)
{
  return readRegs((byte)reg, cnt);
}
byte readReg(byte reg)
{
  return readRegs(reg,1)[0];
}
short readReg16Bit(byte reg)
{
   int value;
   int msb;
   int lsb;
   byte[] res = readRegs(reg, 2);
   msb = res[0] & 0xFF;
   lsb = res[1] & 0xFF;
   value = msb;
   value = (value << 8);
   value |= lsb;	
   return (short)value;
}
short readReg16BitPrint(byte reg)
{
   int value;
   int msb;
   int lsb;
   byte[] res = readRegs(reg, 2);
   msb = res[0] & 0xFF;
   lsb = res[1] & 0xFF;
   value = msb;
   //print(msb); print(" "); print(lsb); print(" ");
   value = (value << 8);
   value |= lsb;  
   return (short)value;
}
byte readReg(int reg)
{
  return readReg((byte)reg);
}
void writeReg(int reg, int val)
{
  writeReg((byte)reg, (byte)val);
}

void writeMulti(byte reg, byte [] src, int count)
{
  i2c.beginTransmission(address);
  i2c.write(reg);

  for (int i = 0; i < count; i++)
  {
    i2c.write(src[i]);
  }

  i2c.endTransmission();
}

void writeReg16Bit(byte reg, short value)
{
  i2c.beginTransmission(address);
  i2c.write(reg);
  i2c.write((value >> 8) & 0xFF); // value high byte
  i2c.write( value       & 0xFF); // value low byte
  i2c.endTransmission();
}
void writeReg32Bit(byte reg, int value)
{
  i2c.beginTransmission(address);
  i2c.write(reg);
  i2c.write((value >> 24) & 0xFF); // value highest byte
  i2c.write((value >> 16) & 0xFF);
  i2c.write((value >>  8) & 0xFF);
  i2c.write( value        & 0xFF); // value lowest byte
  i2c.endTransmission();
}
byte stop_variable;
byte MSRC_CONFIG_CONTROL = (byte)0x60;

short encodeTimeout(short timeout_mclks)
{
  // format: "(LSByte * 2^MSByte) + 1"

  int ls_byte = 0;
  short ms_byte = 0;

  if (timeout_mclks > 0)
  {
    ls_byte = timeout_mclks - 1;

    while ((ls_byte & 0xFFFFFF00) > 0)
    {
      ls_byte >>= 1;
      ms_byte++;
    }

    return (short)((ms_byte << 8) | (ls_byte & 0xFF));
  }
  else { return 0; }
}


int measurement_timing_budget_us;
int  calcMacroPeriod(short vcsel_period_pclks)
{
 return ((((int)2304 * vcsel_period_pclks  * 1655) + 500) / 1000);
}
int timeoutMclksToMicroseconds(short timeout_period_mclks, short vcsel_period_pclks)
{
 int macro_period_ns = calcMacroPeriod(vcsel_period_pclks);

  return (((int)timeout_period_mclks * macro_period_ns) + (macro_period_ns / 2)) / 1000;
}

int timeoutMicrosecondsToMclks(int timeout_period_us, short vcsel_period_pclks)
{
  int macro_period_ns = calcMacroPeriod(vcsel_period_pclks);

  return (((timeout_period_us * 1000) + (macro_period_ns / 2)) / macro_period_ns);
}


int decodeTimeout(byte [] reg_val)
{
  // format: "(LSByte * 2^MSByte) + 1"
return ((reg_val[0] & 0xFF)) + ((reg_val[1] & 0xFF) << 8) + 1;
/*  return (uint16_t)((reg_val & 0x00FF) <<
         (uint16_t)((reg_val & 0xFF00) >> 8)) + 1;*/
}


short decodeVcselPeriod(byte reg_val)
{
 return  (short) (((reg_val & 0xFF) + 1) << 1);
}
int getMeasurementTimingBudget()
{
	//SequenceStepEnables enables;
 boolean tcc, msrc, dss, pre_range, final_range;


//  SequenceStepTimeouts timeouts;
 short pre_range_vcsel_period_pclks, final_range_vcsel_period_pclks;

      short msrc_dss_tcc_mclks, pre_range_mclks, final_range_mclks;
      int msrc_dss_tcc_us,    pre_range_us,    final_range_us;
// end SequenceStepTimeouts

  short StartOverhead     = 1910; // note that this is different than the value in set_
  short  EndOverhead        = 960;
  short  MsrcOverhead       = 660;
  short  TccOverhead        = 590;
  short  DssOverhead        = 690;
  short  PreRangeOverhead   = 660;
  short  FinalRangeOverhead = 550;

  // "Start and end overhead times always present"
  int budget_us = StartOverhead + EndOverhead;

  //getSequenceStepEnables(&enables);
  byte sequence_config = readReg((byte)0x01); //SYSTEM_SEQUENCE_CONFIG

	tcc          = ((sequence_config >> 4) & (byte) 0x1) == (byte) 0x01;
	dss          = ((sequence_config >> 3) & (byte) 0x1) == (byte) 0x01;
	msrc         = ((sequence_config >> 2) & (byte) 0x1) == (byte) 0x01;
	pre_range    = ((sequence_config >> 6) & (byte) 0x1) == (byte) 0x01;
	final_range  = ((sequence_config >> 7) & (byte) 0x1) == (byte) 0x01;
  //end getSequenceStepEnables(&enables);

//  getSequenceStepTimeouts(&enables, &timeouts);
 pre_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x50));// PRE_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodPreRange);

  msrc_dss_tcc_mclks = (short) (readReg((byte)0x46) + 1); //MSRC_CONFIG_TIMEOUT_MACROP
  msrc_dss_tcc_us =
    (short)timeoutMclksToMicroseconds(msrc_dss_tcc_mclks,
                               pre_range_vcsel_period_pclks);

  pre_range_mclks =
    (short) decodeTimeout(readRegs((byte)0x51, 2));//PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI
  pre_range_us =
    timeoutMclksToMicroseconds(pre_range_mclks,
                              pre_range_vcsel_period_pclks);

  final_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x70));//FINAL_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodFinalRange);

  final_range_mclks =
    (short) decodeTimeout(readRegs(0x71, 2));//FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI

  if (pre_range)
  {
    final_range_mclks -= pre_range_mclks;
  }

  final_range_us =
     timeoutMclksToMicroseconds(final_range_mclks,
                                final_range_vcsel_period_pclks);
//end getSequenceStepTimeouts



  if (tcc)
  {
    budget_us += (msrc_dss_tcc_us + TccOverhead);
  }

  if (dss)
  {
    budget_us += 2 * (msrc_dss_tcc_us + DssOverhead);
  }
  else if (msrc)
  {
    budget_us += (msrc_dss_tcc_us + MsrcOverhead);
  }

  if (pre_range)
  {
    budget_us += (pre_range_us + PreRangeOverhead);
  }

  if (final_range)
  {
    budget_us += (final_range_us + FinalRangeOverhead);
  }

  measurement_timing_budget_us = budget_us; // store for internal reuse
  return budget_us;
}

boolean setMeasurementTimingBudget(int budget_us)
{
	//SequenceStepEnables enables;
 boolean tcc, msrc, dss, pre_range, final_range;


//  SequenceStepTimeouts timeouts;
 short pre_range_vcsel_period_pclks, final_range_vcsel_period_pclks;

      short msrc_dss_tcc_mclks, pre_range_mclks, final_range_mclks;
      int msrc_dss_tcc_us,    pre_range_us,    final_range_us;
// end SequenceStepTimeouts

  short StartOverhead     = 1320; // note that this is different than the value in get_
  short  EndOverhead        = 960;
  short  MsrcOverhead       = 660;
  short  TccOverhead        = 590;
  short  DssOverhead        = 690;
  short  PreRangeOverhead   = 660;
  short  FinalRangeOverhead = 550;

  int MinTimingBudget = 20000;

  if (budget_us < MinTimingBudget) { return false; }

  int used_budget_us = StartOverhead + EndOverhead;


  //getSequenceStepEnables(&enables);
  byte sequence_config = readReg((byte)0x01); //SYSTEM_SEQUENCE_CONFIG

	tcc          = ((sequence_config >> 4) & (byte) 0x1) == (byte) 0x01;
	dss          = ((sequence_config >> 3) & (byte) 0x1) == (byte) 0x01;
	msrc         = ((sequence_config >> 2) & (byte) 0x1) == (byte) 0x01;
	pre_range    = ((sequence_config >> 6) & (byte) 0x1) == (byte) 0x01;
	final_range  = ((sequence_config >> 7) & (byte) 0x1) == (byte) 0x01;
  //end getSequenceStepEnables(&enables);

//  getSequenceStepTimeouts(&enables, &timeouts);
// pre_range_vcsel_period_pclks = getVcselPulsePeriod(VcselPeriodPreRange);

 pre_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x50));// PRE_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodPreRange);

  msrc_dss_tcc_mclks = (short) ((int)readReg((byte)0x46) + 1); //MSRC_CONFIG_TIMEOUT_MACROP
  msrc_dss_tcc_us =
    timeoutMclksToMicroseconds(msrc_dss_tcc_mclks,
                               pre_range_vcsel_period_pclks);

  pre_range_mclks =
    (short) decodeTimeout(readRegs((byte)0x51, 2));//PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI
  pre_range_us =
    timeoutMclksToMicroseconds(pre_range_mclks,
                               pre_range_vcsel_period_pclks);

//  final_range_vcsel_period_pclks = getVcselPulsePeriod(VcselPeriodFinalRange);
  final_range_vcsel_period_pclks = decodeVcselPeriod(readReg(0x70));//FINAL_RANGE_CONFIG_VCSEL_PERIOD getVcselPulsePeriod(VcselPeriodFinalRange);

  final_range_mclks =
   (short) decodeTimeout(readRegs(0x71, 2));//FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI

  if (pre_range)
  {
    final_range_mclks -= pre_range_mclks;
  }

  final_range_us =
    timeoutMclksToMicroseconds(final_range_mclks,
                               final_range_vcsel_period_pclks);
//end getSequenceStepTimeouts

  if (tcc)
  {
    used_budget_us += (msrc_dss_tcc_us + TccOverhead);
  }

  if (dss)
  {
    used_budget_us += 2 * (msrc_dss_tcc_us + DssOverhead);
  }
  else if (msrc)
  {
    used_budget_us += (msrc_dss_tcc_us + MsrcOverhead);
  }

  if (pre_range)
  {
    used_budget_us += (pre_range_us + PreRangeOverhead);
  }

  if (final_range)
  {
    used_budget_us += FinalRangeOverhead;

    // "Note that the final range timeout is determined by the timing
    // budget and the sum of all other timeouts within the sequence.
    // If there is no room for the final range timeout, then an error
    // will be set. Otherwise the remaining time will be applied to
    // the final range."

    if (used_budget_us > budget_us)
    {
      // "Requested timeout too big."
      return false;
    }

    int final_range_timeout_us = budget_us - used_budget_us;

    // set_sequence_step_timeout() begin
    // (SequenceStepId == VL53L0X_SEQUENCESTEP_FINAL_RANGE)

    // "For the final range timeout, the pre-range timeout
    //  must be added. To do this both final and pre-range
    //  timeouts must be expressed in macro periods MClks
    //  because they have different vcsel periods."

	short final_range_timeout_mclks =
     (short) timeoutMicrosecondsToMclks(final_range_timeout_us,
                                 final_range_vcsel_period_pclks);

    if (pre_range)
    {
      final_range_timeout_mclks += pre_range_mclks;
    }

    writeReg16Bit((byte)0x71,
      encodeTimeout(final_range_timeout_mclks)); //FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI

    // set_sequence_step_timeout() end

    measurement_timing_budget_us = budget_us; // store for internal reuse
  }
  return true;
}
boolean setSignalRateLimit(float limit_Mcps)
{
  if (limit_Mcps < 0 || limit_Mcps > 511.99) { return false; }

  // Q9.7 fixed point format (9 integer bits, 7 fractional bits)
  writeReg16Bit((byte)0x44, (short) (limit_Mcps * (1 << 7))); //FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT
  return true;
}
int count;
boolean type_is_aperture;
int timeout_start_ms;
int io_timeout = 0;
void startTimeout()
{
  timeout_start_ms = millis();
}
boolean checkTimeoutExpired()
{
  return (io_timeout > 0) && ((millis() - timeout_start_ms) > io_timeout);
}
boolean getSpadInfo()
{
  byte tmp;

  writeReg(0x80, 0x01);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);

  writeReg(0xFF, 0x06);
  writeReg(0x83, readReg(0x83) | 0x04);
  writeReg(0xFF, 0x07);
  writeReg(0x81, 0x01);

  writeReg(0x80, 0x01);

  writeReg(0x94, 0x6b);
  writeReg(0x83, 0x00);
  startTimeout();
  while (readReg(0x83) == 0x00)
  {
    if (checkTimeoutExpired()) { return false; }
  }
  writeReg(0x83, 0x01);
  tmp = readReg(0x92);

  count = tmp & (byte)0x7f;
  type_is_aperture = ((tmp >> (byte)7) & (byte)0x01) == (byte)0x1;

  writeReg(0x81, 0x00);
  writeReg(0xFF, 0x06);
  writeReg(0x83, readReg(0x83)  & ~0x04);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x01);

  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);

  return true;
}

// based on VL53L0X_perform_single_ref_calibration()
boolean performSingleRefCalibration(byte vhv_init_byte)
{
//SYSRANGE_START 0x00
  writeReg(0x00, 0x01 | vhv_init_byte); // VL53L0X_REG_SYSRANGE_MODE_START_STOP

  startTimeout();
  while (((int)readReg(0x13) & 0x07) == 0) //RESULT_INTERRUPT_STATUS
  {
    if (checkTimeoutExpired()) { return false; }
  }

  writeReg(0x0B, 0x01); //SYSTEM_INTERRUPT_CLEAR

  writeReg(0x00, 0x00); //SYSRANGE_START

  return true;
}


boolean initVL53LOX()
{
   writeReg(0x88, 0x00);

  writeReg(0x80, 0x01);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);
  stop_variable = readReg(0x91);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);
  
  
  

  // disable SIGNAL_RATE_MSRC (bit 1) and SIGNAL_RATE_PRE_RANGE (bit 4) limit checks
  writeReg(MSRC_CONFIG_CONTROL, readReg(MSRC_CONFIG_CONTROL) | 0x12);
  
  // set final range signal rate limit to 0.25 MCPS (million counts per second)
  setSignalRateLimit(0.25);
  
  writeReg(0x01, 0xFF); //SYSTEM_SEQUENCE_CONFIG
    // VL53L0X_DataInit() end

  // VL53L0X_StaticInit() begin
  
   if (!getSpadInfo()) { return false; }
  byte spad_count = (byte)count;
  boolean spad_type_is_aperture = type_is_aperture;
  // The SPAD map (RefGoodSpadMap) is read by VL53L0X_get_info_from_device() in
  // the API, but the same data seems to be more easily readable from
  // GLOBAL_CONFIG_SPAD_ENABLES_REF_0 through _6, so read it from there
  byte [] ref_spad_map = readRegs((byte)0xB0, 6); //GLOBAL_CONFIG_SPAD_ENABLES_REF_0

  writeReg(0xFF, 0x01);
  writeReg(0x4F, 0x00); //DYNAMIC_SPAD_REF_EN_START_OFFSET
  writeReg(0x4E, 0x2C); //DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD
  writeReg(0xFF, 0x00);
  writeReg(0xB6, 0xB4); //GLOBAL_CONFIG_REF_EN_START_SELECT

  byte first_spad_to_enable = (byte)(spad_type_is_aperture ? 12 : 0); // 12 is the first aperture spad
  byte spads_enabled = 0;
  
  for (int i = 0; i < 48; i++)
  {
    if (i < first_spad_to_enable || spads_enabled == spad_count)
    {
      // This bit is lower than the first one that should be enabled, or
      // (reference_spad_count) bits have already been enabled, so zero this bit
      ref_spad_map[i / 8] &= ~(1 << (i % 8));
    }
    else if (((ref_spad_map[i / 8] >> (byte)(i % 8)) & (byte)0x1) == (byte)0x01)
    {
      spads_enabled++;
    }
  }
  writeMulti((byte)0xB0, ref_spad_map, 6);//GLOBAL_CONFIG_SPAD_ENABLES_REF_0

// -- VL53L0X_set_reference_spads() end

  // -- VL53L0X_load_tuning_settings() begin
  // DefaultTuningSettings from vl53l0x_tuning.h

  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);

  writeReg(0xFF, 0x00);
  writeReg(0x09, 0x00);
  writeReg(0x10, 0x00);
  writeReg(0x11, 0x00);

  writeReg(0x24, 0x01);
  writeReg(0x25, 0xFF);
  writeReg(0x75, 0x00);

  writeReg(0xFF, 0x01);
  writeReg(0x4E, 0x2C);
  writeReg(0x48, 0x00);
  writeReg(0x30, 0x20);

  writeReg(0xFF, 0x00);
  writeReg(0x30, 0x09);
  writeReg(0x54, 0x00);
  writeReg(0x31, 0x04);
  writeReg(0x32, 0x03);
  writeReg(0x40, 0x83);
  writeReg(0x46, 0x25);
  writeReg(0x60, 0x00);
  writeReg(0x27, 0x00);
  writeReg(0x50, 0x06);
  writeReg(0x51, 0x00);
  writeReg(0x52, 0x96);
  writeReg(0x56, 0x08);
  writeReg(0x57, 0x30);
  writeReg(0x61, 0x00);
  writeReg(0x62, 0x00);
  writeReg(0x64, 0x00);
  writeReg(0x65, 0x00);
  writeReg(0x66, 0xA0);

  writeReg(0xFF, 0x01);
  writeReg(0x22, 0x32);
  writeReg(0x47, 0x14);
  writeReg(0x49, 0xFF);
  writeReg(0x4A, 0x00);

  writeReg(0xFF, 0x00);
  writeReg(0x7A, 0x0A);
  writeReg(0x7B, 0x00);
  writeReg(0x78, 0x21);

  writeReg(0xFF, 0x01);
  writeReg(0x23, 0x34);
  writeReg(0x42, 0x00);
  writeReg(0x44, 0xFF);
  writeReg(0x45, 0x26);
  writeReg(0x46, 0x05);
  writeReg(0x40, 0x40);
  writeReg(0x0E, 0x06);
  writeReg(0x20, 0x1A);
  writeReg(0x43, 0x40);

  writeReg(0xFF, 0x00);
  writeReg(0x34, 0x03);
  writeReg(0x35, 0x44);

  writeReg(0xFF, 0x01);
  writeReg(0x31, 0x04);
  writeReg(0x4B, 0x09);
  writeReg(0x4C, 0x05);
  writeReg(0x4D, 0x04);

  writeReg(0xFF, 0x00);
  writeReg(0x44, 0x00);
  writeReg(0x45, 0x20);
  writeReg(0x47, 0x08);
  writeReg(0x48, 0x28);
  writeReg(0x67, 0x00);
  writeReg(0x70, 0x04);
  writeReg(0x71, 0x01);
  writeReg(0x72, 0xFE);
  writeReg(0x76, 0x00);
  writeReg(0x77, 0x00);

  writeReg(0xFF, 0x01);
  writeReg(0x0D, 0x01);

  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x01);
  writeReg(0x01, 0xF8);

  writeReg(0xFF, 0x01);
  writeReg(0x8E, 0x01);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);

  // -- VL53L0X_load_tuning_settings() end

  // "Set interrupt config to new sample ready"
  // -- VL53L0X_SetGpioConfig() begin

  writeReg(0x0A, 0x04);//SYSTEM_INTERRUPT_CONFIG_GPIO
  writeReg(0x84, readReg(0x84) & ~0x10); // active low GPIO_HV_MUX_ACTIVE_HIGH
  writeReg(0x0B, 0x01); //SYSTEM_INTERRUPT_CLEAR

  // -- VL53L0X_SetGpioConfig() end
  
  
  measurement_timing_budget_us = getMeasurementTimingBudget();
  // "Disable MSRC and TCC by default"
  // MSRC = Minimum Signal Rate Check
  // TCC = Target CentreCheck
  // -- VL53L0X_SetSequenceStepEnable() begin

  writeReg(0x01, 0xE8); //SYSTEM_SEQUENCE_CONFIG

  // -- VL53L0X_SetSequenceStepEnable() end

  // "Recalculate timing budget"
  setMeasurementTimingBudget(measurement_timing_budget_us);

  // VL53L0X_StaticInit() end

  // VL53L0X_PerformRefCalibration() begin (VL53L0X_perform_ref_calibration())

  // -- VL53L0X_perform_vhv_calibration() begin

  writeReg(0x01, 0x01); //SYSTEM_SEQUENCE_CONFIG
  if (!performSingleRefCalibration((byte)0x40)) { return false; }

  // -- VL53L0X_perform_vhv_calibration() end

  // -- VL53L0X_perform_phase_calibration() begin

  writeReg(0x01, 0x02); //SYSTEM_SEQUENCE_CONFIG
  if (!performSingleRefCalibration((byte)0x00)) { return false; }

  // -- VL53L0X_perform_phase_calibration() end

  // "restore the previous Sequence Config"
  writeReg(0x01, 0xE8); //SYSTEM_SEQUENCE_CONFIG

  // VL53L0X_PerformRefCalibration() end
  return true;

}

int period_ms = 0;
void VL53LOX_startContinuous()
{
  writeReg(0x80, 0x01);
  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);
  writeReg(0x91, stop_variable);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
  writeReg(0x80, 0x00);

  if (period_ms != 0)
  {
    // continuous timed mode

    // VL53L0X_SetInterMeasurementPeriodMilliSeconds() begin

    short osc_calibrate_val = readReg16Bit((byte)0xF8); //OSC_CALIBRATE_VAL

    if (osc_calibrate_val != 0)
    {
      period_ms *= osc_calibrate_val;
    }

    writeReg32Bit((byte)0x04, period_ms); //SYSTEM_INTERMEASUREMENT_PERIOD

    // VL53L0X_SetInterMeasurementPeriodMilliSeconds() end

    writeReg(0x00, 0x04); // VL53L0X_REG_SYSRANGE_MODE_TIMED //SYSRANGE_START
  }
  else
  {
    // continuous back-to-back mode
    writeReg(0x00, 0x02); // VL53L0X_REG_SYSRANGE_MODE_BACKTOBACK //SYSRANGE_START
  }
}

// Stop continuous measurements
// based on VL53L0X_StopMeasurement()
void stopContinuous()
{
  writeReg(0x00, 0x01); // VL53L0X_REG_SYSRANGE_MODE_SINGLESHOT //SYSRANGE_START

  writeReg(0xFF, 0x01);
  writeReg(0x00, 0x00);
  writeReg(0x91, 0x00);
  writeReg(0x00, 0x01);
  writeReg(0xFF, 0x00);
}

boolean did_timeout = false;
// Returns a range reading in millimeters when continuous mode is active
// (readRangeSingleMillimeters() also calls this function after starting a
// single-shot range measurement)


int readRangeContinuousMillimeters()
{
  startTimeout();
  while (((int)readReg(0x13) & 0x07) == 0) //RESULT_INTERRUPT_STATUS
  {
    if (checkTimeoutExpired())
    {
      did_timeout = true;
      return 65535;
    }
  }

  // assumptions: Linearity Corrective Gain is 1000 (default);
  // fractional ranging is not enabled
  short range = readReg16BitPrint((byte)0x1E);//0x14 + (byte)10);//RESULT_RANGE_STATUS 

  writeReg(0x0B, 0x01); //SYSTEM_INTERRUPT_CLEAR

  return (int)range;
}


void setupVlx() {
  //printArray(I2C.list());
  print("Hello World");
  i2c = new I2C(I2C.list()[0]);
  printArray(I2C.list());
  
  println("scanning");
  
  try
  {
  i2c.beginTransmission(0x29);
  i2c.write(0x00);
  byte[] in = i2c.read(256);
   i2c.endTransmission();

    println("found device: ");
    int i =0;
    print("   : ");
    for (i=0;i<16;i++)
    {
      
      print(bytesToHex(new byte[] {(byte)i}));
      print(" ");
    }
    for (i=0;i<=255;i++)
    {
      if (i % 16 == 0)
      {
        println(": ");
        print(bytesToHex(new byte[] {(byte)i}));
        print(" : "); 
      }
   
     print( bytesToHex(new byte[] { in[i] }));
      print(" ");

    }

    println("!");
    
}
  catch (RuntimeException e)
  {
    print("exception ");
    
    println(e);
  }

boolean initresult = initVL53LOX();


if (initresult)
{
println("initVL53LOX return true");
}
else
{
println("initVL53LOX return false");
}

io_timeout  = 500; //setTimeout

VL53LOX_startContinuous();
  
  print("Goodbye world"); 
}

void drawVlx() {
 //print(readRangeContinuousMillimeters());
  //background(map(mouseX, 0, width, 0, 255));
 boolean tmp = did_timeout;
  did_timeout = false;
	if (tmp) { print(" TIMEOUT"); }
	//println(" ");
if (vlxEnable == true){
    //int vlxIn;
    int vlxDist;
    
    //if (initresult == true) {
    vlxDist = readRangeContinuousMillimeters();
    //println(vlxDist);
    if (vlxDist < 1250) {
    float vlxDistF = Float.valueOf(vlxDist);
    //float distF = Float.valueOf(dist).floatValue();
    float vlxIn = map(vlxDistF, 0, 1250, 0, 1);
    float[] vlxTouch = {vlxIn, .5};
    s.setArrayValue(vlxTouch);
    oscMessageOut = new OscMessage("/luminous/xy");
    oscMessageOutFloat = vlxIn;
    oscMessageOut.add(oscMessageOutFloat);
    oscMessageOutFloat = .5;
    oscMessageOut.add(oscMessageOutFloat);
    oscP5.send(oscMessageOut, myRemoteLocation);
    }
  }
}
