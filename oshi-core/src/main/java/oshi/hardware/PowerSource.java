/**
 * OSHI (https://github.com/oshi/oshi)
 *
 * Copyright (c) 2010 - 2019 The OSHI Project Team:
 * https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package oshi.hardware;

import java.io.Serializable;

/**
 * The Power Source is one or more batteries with some capacity, and some state
 * of charge/discharge
 */
public interface PowerSource extends Serializable {
    /**
     * Name of the power source (e.g., InternalBattery-0)
     *
     * @return The power source name
     */
    String getName();

    /**
     * Remaining capacity as a fraction of max capacity.
     *
     * @return A value between 0.0 (fully drained) and 1.0 (fully charged)
     */
    double getRemainingCapacity();

    /**
     * Estimated time remaining on the power source, in seconds.
     *
     * @return If positive, seconds remaining. If negative, -1.0 (calculating) or
     *         -2.0 (unlimited)
     */
    double getTimeRemaining();

    /**
     *
     * @return Energy remaining in mWh
     */
    double getEnergyRemaining();

    /**
     *
     * @return Charge capacity when full in mWh
     */
    double getMaximumCapacity();

    /**
     *
     * @return Designed charge capacity in mWh
     */
    double getEnergyDesign();

    /**
     *
     * @return Power output at time of method call in mV
     */
    //TODO see if this is power in mV or current in A
    double getPower();

    /**
     *
     * @return Voltage output at time of method call in mV
     */
    double getVoltage();

    /**
     *
     * @return Health as a fraction of maximumCapacity/energyDesign
     */
    double getHealth();

    /**
     *
     * @return Cycle count of Power Source
     */
    int getCycleCount();

    /**
     * Power source status: Charging, Discharging, AC, or Unknown
     *
     * @return State of Power Source
     */
    String getState();

    /**
     *
     * @return Technology of Power Source
     */
    String getTechnology();
}
