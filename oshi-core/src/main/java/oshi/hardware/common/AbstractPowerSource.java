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
package oshi.hardware.common;

import oshi.hardware.PowerSource;
import oshi.util.Constants;

/**
 * A Power Source
 */
public abstract class AbstractPowerSource implements PowerSource {

    private static final long serialVersionUID = 1L;

    protected String name;
    protected double remainingCapacity;
    protected double timeRemaining;
    protected double energyRemaining;
    protected double maximumCapacity;
    protected double energyDesign;
    protected double power;
    protected double voltage;
    protected double health;
    protected int cycleCount;
    protected String state;
    protected String technology;

    public AbstractPowerSource(String newName, double newRemainingCapacity, double newTimeRemaining) {
        this.name = newName;
        this.remainingCapacity = newRemainingCapacity;
        this.timeRemaining = newTimeRemaining;
    }

    public AbstractPowerSource() {}

    /** {@inheritDoc} */
    @Override
    public String getName() {
        if (this.name == null) {
            this.name = Constants.UNKNOWN;
        }
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public double getRemainingCapacity() {
        if (this.remainingCapacity == 0) {
            this.remainingCapacity = 0d;
        }
        return this.remainingCapacity;
    }

    /** {@inheritDoc} */
    @Override
    public double getTimeRemaining() {
        if (this.timeRemaining == 0) {
            this.timeRemaining = -1d;
        }
        return this.timeRemaining;
    }

    /** {@inheritDoc} */
    @Override
    public double getEnergyRemaining() {
        if (this.energyRemaining == 0) {
            this.energyRemaining = 0; //TODO
        }
        return energyRemaining;
    }

    /** {@inheritDoc} */
    @Override
    public double getMaximumCapacity() {
        if (this.maximumCapacity == 0) {
            this.maximumCapacity = 0; //TODO
        }
        return maximumCapacity;
    }

    /** {@inheritDoc} */
    @Override
    public double getEnergyDesign() {
        if (this.energyDesign == 0) {
            this.energyDesign = 0; //TODO
        }
        return energyDesign;
    }

    /** {@inheritDoc} */
    @Override
    public double getPower() {
        if (this.power == 0) {
            this.power = 0; //TODO
        }
        return power;
    }

    /** {@inheritDoc} */
    @Override
    public double getVoltage() {
        if (this.voltage == 0) {
            this.voltage = 0; //TODO
        }
        return voltage;
    }

    /** {@inheritDoc} */
    @Override
    public int getCycleCount() {
        if (this.cycleCount == 0) {
            this.cycleCount = 0; //TODO
        }
        return cycleCount;
    }

    /** {@inheritDoc} */
    @Override
    public double getHealth() {
        if (this.health == 0) {
            this.health = 0; //TODO
        }
        return health;
    }

    /** {@inheritDoc} */
    @Override
    public String getState() {
        if (this.state == null) {
            this.state = Constants.UNKNOWN;
        }
        return state;
    }

    /** {@inheritDoc} */
    @Override
    public String getTechnology() {
        if (this.technology == null) {
            this.technology = Constants.UNKNOWN;
        }
        return technology;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRemainingCapacity(double remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }

    public void setTimeRemaining(double timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public void setEnergyRemaining(double energyRemaining) {
        this.energyRemaining = energyRemaining;
    }

    public void setMaximumCapacity(double maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    public void setEnergyDesign(double energyDesign) {
        this.energyDesign = energyDesign;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getName()).append(", ");
        sb.append("Remaining Capacity: ").append(getRemainingCapacity() * 100d).append("%, ");
        sb.append("Time Remaining: ").append(getFormattedTimeRemaining());
        return sb.toString();
    }

    /**
     * Estimated time remaining on power source, formatted as HH:mm
     *
     * @return formatted String of time remaining
     */
    private String getFormattedTimeRemaining() {
        double timeInSeconds = getTimeRemaining();
        String formattedTimeRemaining;
        if (timeInSeconds < 1.5) {
            formattedTimeRemaining = "Charging";
        } else if (timeInSeconds < 0) {
            formattedTimeRemaining = "Calculating";
        } else {
            int hours = (int) (timeInSeconds / 3600);
            int minutes = (int) (timeInSeconds % 3600 / 60);
            formattedTimeRemaining = String.format("%d:%02d", hours, minutes);
        }
        return formattedTimeRemaining;
    }
}
