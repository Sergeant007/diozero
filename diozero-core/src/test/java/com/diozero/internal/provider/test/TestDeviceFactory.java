package com.diozero.internal.provider.test;

import java.util.HashMap;
import java.util.Map;

/*
 * #%L
 * Device I/O Zero - Core
 * %%
 * Copyright (C) 2016 diozero
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.diozero.api.*;
import com.diozero.internal.DeviceStates;
import com.diozero.internal.spi.*;
import com.diozero.util.RuntimeIOException;

public class TestDeviceFactory extends BaseNativeDeviceFactory {
	private static Class<? extends GpioAnalogInputDeviceInterface> analogInputDeviceClass;
	private static Class<? extends GpioDigitalInputDeviceInterface> digitalInputDeviceClass = TestDigitalInputDevice.class;
	private static Class<? extends GpioDigitalOutputDeviceInterface> digitalOutputDeviceClass = TestDigitalOutputDevice.class;
	private static Class<? extends PwmOutputDeviceInterface> pwmOutputDeviceClass;
	private static Class<? extends SpiDeviceInterface> spiDeviceClass;
	private static Class<? extends I2CDeviceInterface> i2cDeviceClass = TestI2CDevice.class;
	
	public static void setAnalogInputDeviceClass(Class<? extends GpioAnalogInputDeviceInterface> clz) {
		analogInputDeviceClass = clz;
	}

	public static void setDigitalInputDeviceClass(Class<? extends GpioDigitalInputDeviceInterface> clz) {
		digitalInputDeviceClass = clz;
	}
	
	public static void setDigitalOutputDeviceClass(Class<? extends GpioDigitalOutputDeviceInterface> clz) {
		digitalOutputDeviceClass = clz;
	}
	
	public static void setPwmOutputDeviceClass(Class<? extends PwmOutputDeviceInterface> clz) {
		pwmOutputDeviceClass = clz;
	}
	
	public static void setSpiDeviceClass(Class<? extends SpiDeviceInterface> clz) {
		spiDeviceClass = clz;
	}
	
	public static void setI2CDeviceClass(Class<? extends I2CDeviceInterface> clz) {
		i2cDeviceClass = clz;
	}
	
	private static final int DEFAULT_PWM_FREQUENCY = 100;
	private Map<Integer, Integer> pwmFrequencies = new HashMap<>();
	
	// Added for testing purposes only
	public DeviceStates getDeviceStates() {
		return deviceStates;
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	protected GpioAnalogInputDeviceInterface createAnalogInputPin(String key, int pinNumber) throws RuntimeIOException {
		if (analogInputDeviceClass == null) {
			throw new UnsupportedOperationException("Analog input implementation class hasn't been set");
		}
		
		try {
			return analogInputDeviceClass.getConstructor(String.class, DeviceFactoryInterface.class, int.class)
				.newInstance(key, this, Integer.valueOf(pinNumber));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected GpioDigitalInputDeviceInterface createDigitalInputPin(String key, int pinNumber, GpioPullUpDown pud,
			GpioEventTrigger trigger) throws RuntimeIOException {
		if (digitalInputDeviceClass == null) {
			throw new UnsupportedOperationException("Digital input implementation class hasn't been set");
		}
		
		try {
			return digitalInputDeviceClass.getConstructor(String.class, DeviceFactoryInterface.class, int.class, GpioPullUpDown.class, GpioEventTrigger.class)
				.newInstance(key, this, Integer.valueOf(pinNumber), pud, trigger);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected GpioDigitalOutputDeviceInterface createDigitalOutputPin(String key, int pinNumber, boolean initialValue)
			throws RuntimeIOException {
		if (digitalOutputDeviceClass == null) {
			throw new UnsupportedOperationException("Digital output implementation class hasn't been set");
		}
		
		try {
			return digitalOutputDeviceClass.getConstructor(String.class, DeviceFactoryInterface.class, int.class, boolean.class)
				.newInstance(key, this, Integer.valueOf(pinNumber), Boolean.valueOf(initialValue));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected PwmOutputDeviceInterface createPwmOutputPin(String key, int pinNumber,
			float initialValue, PwmType pwmType) throws RuntimeIOException {
		if (pwmOutputDeviceClass == null) {
			throw new IllegalArgumentException("PWM output implementation class hasn't been set");
		}
		
		try {
			return pwmOutputDeviceClass.getConstructor(String.class, DeviceFactoryInterface.class, int.class, float.class, PwmType.class)
				.newInstance(key, this, Integer.valueOf(pinNumber), Float.valueOf(initialValue), pwmType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected SpiDeviceInterface createSpiDevice(String key, int controller, int chipSelect, int frequency,
			SpiClockMode spiClockMode) throws RuntimeIOException {
		if (spiDeviceClass == null) {
			throw new IllegalArgumentException("SPI Device implementation class hasn't been set");
		}
		
		try {
			return spiDeviceClass.getConstructor(String.class, DeviceFactoryInterface.class, int.class, int.class, int.class, SpiClockMode.class)
				.newInstance(key, this, Integer.valueOf(controller), Integer.valueOf(chipSelect), Integer.valueOf(frequency), spiClockMode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected I2CDeviceInterface createI2CDevice(String key, int controller, int address, int addressSize,
			int clockFrequency) throws RuntimeIOException {
		if (i2cDeviceClass == null) {
			throw new IllegalArgumentException("I2C Device implementation class hasn't been set");
		}
		
		try {
			return i2cDeviceClass.getConstructor(String.class, DeviceFactoryInterface.class, int.class, int.class, int.class, int.class)
				.newInstance(key, this, Integer.valueOf(controller), Integer.valueOf(address), Integer.valueOf(addressSize), Integer.valueOf(clockFrequency));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getPwmFrequency(int pinNumber) {
		Integer key = Integer.valueOf(pinNumber);
		Integer pwm_freq = pwmFrequencies.get(key);
		if (pwm_freq == null) {
			pwm_freq = new Integer(DEFAULT_PWM_FREQUENCY);
			pwmFrequencies.put(key, pwm_freq);
		}
		
		return pwm_freq.intValue();
	}

	@Override
	public void setPwmFrequency(int pinNumber, int pwmFrequency) {
		this.pwmFrequencies.put(Integer.valueOf(pinNumber), Integer.valueOf(pwmFrequency));
	}
}
