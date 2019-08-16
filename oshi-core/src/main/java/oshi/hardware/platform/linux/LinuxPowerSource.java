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
package oshi.hardware.platform.linux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;

/**
 * A Power Source
 */
public class LinuxPowerSource extends AbstractPowerSource {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(LinuxPowerSource.class);

    /**
     * Gets Battery Information
     *
     * @return An array of PowerSource objects representing batteries, etc.
     */
    public static PowerSource[] getPowerSources() {
        ArrayList<LinuxPowerSource> powerSourcesArrayList = new ArrayList<>();
        powerSourcesArrayList.add(getLinuxBattery());
        return powerSourcesArrayList.toArray(new LinuxPowerSource[0]);
    }

    private static LinuxPowerSource getLinuxBattery() {
        String psPathString = "/sys/class/power_supply/BAT0/";
        LinuxPowerSource powerSource = new LinuxPowerSource();
        powerSource.setName(getContentsFromPathString(psPathString + "model_name"));
        powerSource.setEnergyRemaining(Long.parseLong(getContentsFromPathString(psPathString + "energy_now")));
        powerSource.setEnergyFull(Long.parseLong(getContentsFromPathString(psPathString + "energy_full")));
        powerSource.setEnergyDesign(Long.parseLong(getContentsFromPathString(psPathString + "energy_full_design")));
        powerSource.setPower(Long.parseLong(getContentsFromPathString(psPathString + "power_now")));
        powerSource.setVoltage(Long.parseLong(getContentsFromPathString(psPathString + "voltage_now")));
        powerSource.setCycleCount(Integer.parseInt(getContentsFromPathString(psPathString + "cycle_count")));
        powerSource.setState(getContentsFromPathString(psPathString + "status"));
        powerSource.setTechnology(getContentsFromPathString(psPathString + "technology"));
        return powerSource;
    }

    private static String getContentsFromPathString(String pathString) {
        Path path = Paths.get(pathString);
        String contents;
        try {
            contents = Files.readAllLines(path).get(0);
        }
        catch (IOException e) {
            LOG.error("IOException", e);
            contents = null;
        }
        return contents;
    }

    /** {@inheritDoc} */
    @Override
    public void updateAttributes() {
        PowerSource[] psArr = getPowerSources();
        for (PowerSource ps : psArr) {
            if (ps.equals(this)) {
                getLinuxBattery();
            }
        }
        // Didn't find this battery
        this.remainingCapacity = 0d;
        this.timeRemaining = -1d;
    }
}
