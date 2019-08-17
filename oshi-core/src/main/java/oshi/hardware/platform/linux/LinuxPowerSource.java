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

    private static String psPathString = "/sys/class/power_supply/BAT0/";

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
        LinuxPowerSource powerSource = new LinuxPowerSource();
        if (Files.isDirectory(Paths.get(psPathString))) {
            powerSource.setName(parseBatteryName());
            powerSource.setEnergyRemaining(parseBatteryEnergyRemaining());
            powerSource.setEnergyFull(parseBatteryEnergyFull());
            powerSource.setEnergyDesign(parseBatteryEnergyDesign());
            powerSource.setPower(parseBatteryPower());
            powerSource.setVoltage(parseBatteryVoltage());
            powerSource.setCycleCount(parseBatteryCycleCount());
            powerSource.setState(parseBatteryState());
            powerSource.setTechnology(parseBatteryTechnology());
        }
        return powerSource;
    }

    private static String parseBatteryName() {
        Path path = Paths.get(psPathString, "model_name");
        return getContentsFromPath(path);
    }

    private static long parseBatteryEnergyRemaining() {
        Path path = Paths.get(psPathString,"energy_now");
        return Long.parseLong(getContentsFromPath(path));
    }

    private static long parseBatteryEnergyFull() {
        Path path = Paths.get(psPathString,"energy_full");
        return Long.parseLong(getContentsFromPath(path));
    }

    private static long parseBatteryEnergyDesign() {
        Path path = Paths.get(psPathString, "energy_full_design");
        return Long.parseLong(getContentsFromPath(path));
    }

    private static long parseBatteryPower() {
        Path path = Paths.get(psPathString, "power_now");
        return Long.parseLong(getContentsFromPath(path));
    }

    private static long parseBatteryVoltage() {
        Path path = Paths.get(psPathString, "voltage_now");
        return Long.parseLong(getContentsFromPath(path));
    }

    private static int parseBatteryCycleCount() {
        Path path = Paths.get(psPathString, "cycle_count");
        return Integer.parseInt(getContentsFromPath(path));
    }

    private static String parseBatteryState() {
        Path path = Paths.get(psPathString, "status");
        return getContentsFromPath(path);
    }

    private static String parseBatteryTechnology() {
        Path path = Paths.get(psPathString, "technology");
        return getContentsFromPath(path);
    }

    private static String getContentsFromPath(Path path) {
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
