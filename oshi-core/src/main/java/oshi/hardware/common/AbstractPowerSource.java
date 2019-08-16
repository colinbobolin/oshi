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
    protected long energyRemaining;
    protected long energyFull;
    protected long energyDesign;
    protected double percentRemaining;
    protected double health;
    protected long power;
    protected long voltage;
    protected int cycleCount;
    protected String state;
    protected String technology;

    //TODO remove this after implementation of no-arg constructors
    public AbstractPowerSource(String newName, double percentRemaining, double newTimeRemaining) {
        this.name = newName;
        this.percentRemaining = percentRemaining;
        this.timeRemaining = newTimeRemaining;
    }

    //TODO remove this after implementation of no-arg constructor (this will be redundant)
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
        return getPercentRemaining();
    }

    /** {@inheritDoc} */
    @Override
    public double getTimeRemaining() {
        double value;
        if (getState().equals("Charging")) value = -2d;
        else if (getVoltage() == 0) value = 0d;
        else value = 3600d * getEnergyRemaining() / getVoltage();
        return value;
        //TODO Let's talk about -1 = "Calculating." This doesn't seem accurate to me.
    }

    /** {@inheritDoc} */
    @Override
    public long getEnergyRemaining() {
        if (this.energyRemaining == 0) {
            this.energyRemaining = 0;
        }
        return energyRemaining;
    }

    /** {@inheritDoc} */
    @Override
    public long getEnergyFull() {
        if (this.energyFull == 0) {
            this.energyFull = 0;
        }
        return energyFull;
    }

    /** {@inheritDoc} */
    @Override
    public long getEnergyDesign() {
        if (this.energyDesign == 0) {
            this.energyDesign = 0;
        }
        return energyDesign;
    }

    /** {@inheritDoc} */
    @Override
    public double getPercentRemaining() {
        return getEnergyFull() == 0 ? 0d : (double) getEnergyRemaining() / getEnergyFull();
    }

    /** {@inheritDoc} */
    @Override
    public double getHealth() {
        return getEnergyDesign() == 0 ? 0d : (double) getEnergyFull() / getEnergyDesign();
    }

    /** {@inheritDoc} */
    @Override
    public long getPower() {
        if (this.power == 0) {
            this.power = 0;
        }
        return power;
    }

    /** {@inheritDoc} */
    @Override
    public long getVoltage() {
        if (this.voltage == 0) {
            this.voltage = 0;
        }
        return voltage;
    }

    /** {@inheritDoc} */
    @Override
    public int getCycleCount() {
        if (this.cycleCount == 0) {
            this.cycleCount = 0;
        }
        return cycleCount;
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

    public void setEnergyRemaining(long energyRemaining) {
        this.energyRemaining = energyRemaining;
    }

    public void setEnergyFull(long energyFull) {
        this.energyFull = energyFull;
    }

    public void setEnergyDesign(long energyDesign) {
        this.energyDesign = energyDesign;
    }

    public void setPercentRemaining(double percentRemaining) {
        this.percentRemaining = percentRemaining;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public void setVoltage(long voltage) {
        this.voltage = voltage;
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
        sb.append(String.format("%s %s, ", "Name:", getName()));
        sb.append(String.format("%s %s, ", "Time Remaining:", getFormattedTimeRemaining()));
        sb.append(String.format("%s %d, ", "Energy Remaining:", getEnergyRemaining()));
        sb.append(String.format("%s %d, ", "Energy When Full:", getEnergyFull()));
        sb.append(String.format("%s %d, ", "Designed Energy:", getEnergyDesign()));
        sb.append(String.format("%s %.2f, ", "Percent Remaining:", getPercentRemaining()*100d));
        sb.append(String.format("%s %.2f, ", "Health:", getHealth()));
        sb.append(String.format("%s %d, ", "Power:", getPower()));
        sb.append(String.format("%s %d, ", "Voltage:", getVoltage()));
        sb.append(String.format("%s %d, ", "Cycle Count:", getCycleCount()));
        sb.append(String.format("%s %s, ", "State:", getState()));
        sb.append(String.format("%s %s", "Technology:", getTechnology()));
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
