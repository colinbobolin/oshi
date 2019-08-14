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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

/**
 * A Power Source
 */
public class LinuxPowerSource extends AbstractPowerSource {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(LinuxPowerSource.class);

    private static final String PS_PATH = "/sys/class/power_supply/";

    /**
     * Gets Battery Information
     *
     * @return An array of PowerSource objects representing batteries, etc.
     */
    public static PowerSource[] getPowerSources() throws IOException {
        ArrayList<LinuxPowerSource> powerSourcesArrayList = new ArrayList<>();
        powerSourcesArrayList.add(getSystemBattery());
        return powerSourcesArrayList.toArray(new LinuxPowerSource[0]);
    }

    private static LinuxPowerSource getSystemBattery() {
        try {
            FileReader f = new FileReader(PS_PATH + "BAT0/uevent");
            HashMap<String, String> psInfoHashMap = new HashMap<>();
            try (BufferedReader br = new BufferedReader(f)) {
                for (String line; (line = br.readLine()) != null; ) {
                    String[] psSplit = line.split("=");
                    psInfoHashMap.put(psSplit[0], psSplit[1]);
                }
            }
            return parseLinuxBatteryInfoMapIntoPowerSourceObject(psInfoHashMap);
        }
        catch (IOException e) {
            LOG.error("IOException occurred", e); //TODO write better error logs
            return new LinuxPowerSource();
        }
    }

    //TODO change this logic so it writes from the individual files rather than the uevent file
    private static LinuxPowerSource parseLinuxBatteryInfoMapIntoPowerSourceObject(HashMap<String, String> map) {
        LinuxPowerSource powerSource = new LinuxPowerSource();
        powerSource.setName(map.get("POWER_SUPPLY_NAME"));
        powerSource.setRemainingCapacity(map.get("POWER_SUPPLY_CAPACITY"));
        powerSource.setMaximumCapacity(map.get("POWER_SUPPLY_ENERGY_FULL"));
        powerSource.setEnergyRemaining(map.get("POWER_SUPPLY_ENERGY_NOW"));
        powerSource.setEnergyDesign(map.get("POWER_SUPPLY_ENERGY_FULL_DESIGN"));
        powerSource.setCycleCount(map.get("POWER_SUPPLY_CYCLE_COUNT"));
        powerSource.setState(map.get("POWER_SUPPLY_STATUS"));
        powerSource.setTechnology(map.get("POWER_SUPPLY_TECHNOLOGY"));
        powerSource.setVoltage(map.get("POWER_SUPPLY_VOLTAGE_NOW"));
        powerSource.setTimeRemaining(powerSource.getState() == "Charging" ? -2d :
                3600d * powerSource.getEnergyRemaining() / powerSource.getVoltage());

        return powerSource;
    }

//        String[] psNames = f.list();
//        // Empty directory will give null rather than empty array, so fix
//        if (psNames == null) {
//            psNames = new String[0];
//        }
//        ArrayList<LinuxPowerSource> psList = new ArrayList<>(psNames.length);
//        // For each power source, output various info
//        for (String psName : psNames) {
//            // Skip if name is ADP* or AC* (AC power supply)
//            if (psName.startsWith("ADP") || psName.startsWith("AC")) {
//                continue;
//            }
//            // Skip if can't read uevent file
//            List<String> psInfo;
//            psInfo = FileUtil.readFile(PS_PATH + psName + "/uevent", false);
//            if (psInfo.isEmpty()) {
//                continue;
//            }
//            // Initialize defaults
//            boolean isPresent = false;
//            boolean isCharging = false;
//            String name = "Unknown";
//            int energyNow = 0;
//            int energyFull = 1;
//            int powerNow = 1;
//            for (String checkLine : psInfo) {
//                if (checkLine.startsWith("POWER_SUPPLY_PRESENT")) {
//                    // Skip if not present
//                    String[] psSplit = checkLine.split("=");
//                    if (psSplit.length > 1) {
//                        isPresent = ParseUtil.parseIntOrDefault(psSplit[1], 0) > 0;
//                    }
//                    if (!isPresent) {
//                        break;
//                    }
//                } else if (checkLine.startsWith("POWER_SUPPLY_NAME")) {
//                    // Name
//                    String[] psSplit = checkLine.split("=");
//                    if (psSplit.length > 1) {
//                        name = psSplit[1];
//                    }
//                } else if (checkLine.startsWith("POWER_SUPPLY_ENERGY_NOW")
//                        || checkLine.startsWith("POWER_SUPPLY_CHARGE_NOW")) {
//                    // Remaining Capacity = energyNow / energyFull
//                    String[] psSplit = checkLine.split("=");
//                    if (psSplit.length > 1) {
//                        energyNow = ParseUtil.parseIntOrDefault(psSplit[1], 0);
//                    }
//                } else if (checkLine.startsWith("POWER_SUPPLY_ENERGY_FULL")
//                        || checkLine.startsWith("POWER_SUPPLY_CHARGE_FULL")) {
//                    String[] psSplit = checkLine.split("=");
//                    if (psSplit.length > 1) {
//                        energyFull = ParseUtil.parseIntOrDefault(psSplit[1], 1);
//                        if (energyFull < 1) {
//                            energyFull = 1;
//                        }
//                    }
//                } else if (checkLine.startsWith("POWER_SUPPLY_STATUS")) {
//                    // Check if charging
//                    String[] psSplit = checkLine.split("=");
//                    if (psSplit.length > 1 && "Charging".equals(psSplit[1])) {
//                        isCharging = true;
//                    }
//                } else if (checkLine.startsWith("POWER_SUPPLY_POWER_NOW")
//                        || checkLine.startsWith("POWER_SUPPLY_CURRENT_NOW")) {
//                    // Time Remaining = energyNow / powerNow (hours)
//                    String[] psSplit = checkLine.split("=");
//                    if (psSplit.length > 1) {
//                        powerNow = ParseUtil.parseIntOrDefault(psSplit[1], 1);
//                    }
//                    if (powerNow < 1) {
//                        isCharging = true;
//                    }
//                }
//            }
//            if (isPresent) {
//                psList.add(new LinuxPowerSource(name, (double) energyNow / energyFull,
//                        isCharging ? -2d : 3600d * energyNow / powerNow));
//            }
//        }
//        return psList;
//    }

    /** {@inheritDoc} */
    @Override
    public void updateAttributes() {
        PowerSource[] psArr = getPowerSources();
        for (PowerSource ps : psArr) {
            if (ps.getName().equals(this.name)) {
                this.remainingCapacity = ps.getRemainingCapacity();
                this.timeRemaining = ps.getTimeRemaining();
                return;
            }
        }
        // Didn't find this battery
        this.remainingCapacity = 0d;
        this.timeRemaining = -1d;
    }
}
