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

    protected String name = Constants.UNKNOWN;
    protected double remainingCapacity = -1;
    protected double timeRemaining = -1;
    protected long energyRemaining = -1;
    protected long energyFull = -1;
    protected long energyDesign = -1;
    protected double percentRemaining = -1;
    protected double health = -1;
    protected long power = -1;
    protected long voltage = -1;
    protected int cycleCount = -1;
    protected String state = Constants.UNKNOWN;
    protected String technology = Constants.UNKNOWN;

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
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public double getRemainingCapacity() {
        return this.getPercentRemaining();
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
        return this.energyRemaining;
    }

    /** {@inheritDoc} */
    @Override
    public long getEnergyFull() {
        return this.energyFull;
    }

    /** {@inheritDoc} */
    @Override
    public long getEnergyDesign() {
        return this.energyDesign;
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
        return this.power;
    }

    /** {@inheritDoc} */
    @Override
    public long getVoltage() {
        return this.voltage;
    }

    /** {@inheritDoc} */
    @Override
    public int getCycleCount() {
        return this.cycleCount;
    }

    /** {@inheritDoc} */
    @Override
    public String getState() {
        return this.state;
    }

    /** {@inheritDoc} */
    @Override
    public String getTechnology() {
        return this.technology;
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
