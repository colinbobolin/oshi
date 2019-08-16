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
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;

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

    //private static final String PS_PATH = "/sys/class/power_supply/";

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
        HashMap<String, String> attributeMap = getLinuxBatteryAttributes();
        return getLinuxPowerSourceFromAttributeMap(attributeMap);
    }

    private static HashMap<String, String> getLinuxBatteryAttributes() {
        HashMap<String, String> batteryAttributes = new HashMap<>();
        Path psPath = Paths.get("/sys/class/power_supply/BAT0/");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(psPath)) {
            for (Path filePath: stream) {
                putFileContentsInMapIfNotDirectory(filePath, batteryAttributes);
            }
            return batteryAttributes;
        }
        catch (IOException e) {
            LOG.error("IOException occurred", e); //TODO write better error logs
            return batteryAttributes;
        }
    }

    private static void putFileContentsInMapIfNotDirectory(Path path, HashMap<String, String> map) {
        if (!Files.isDirectory(path)) {
            String name = path.getFileName().toString();
            String contents = getFileContentsFromPath(path);
            map.put(name, contents);
        }
    }

    private static String getFileContentsFromPath(Path path) {
        try {
            return Files.readAllLines(path).get(0);
        }
        catch (IOException e) {
            return "";
        }
    }

//    protected String name; STRING
//    protected double remainingCapacity; DEPRECATED, POINTS TO PERCENT REMAINING
//    protected double timeRemaining; DOUBLE, DERIVED
//    protected long energyRemaining; LONG
//    protected long energyFull; LONG
//    protected long energyDesign; LONG
//    protected double percentRemaining; DOUBLE DERIVED
//    protected double health; DOUBLE DERIVED
//    protected long power; LONG
//    protected long voltage; LONG
//    protected int cycleCount; INT
//    protected String state; STRING
//    protected String technology; STRING

    private static LinuxPowerSource getLinuxPowerSourceFromAttributeMap(HashMap<String, String> map) {
        LinuxPowerSource powerSource = new LinuxPowerSource();
        powerSource.setName(map.get("model_name"));
        powerSource.setEnergyRemaining(Long.parseLong(map.get("energy_now")));
        powerSource.setEnergyFull(Long.parseLong(map.get("energy_full")));
        powerSource.setEnergyDesign(Long.parseLong(map.get("energy_full_design")));
        powerSource.setPower(Long.parseLong(map.get("power_now")));
        powerSource.setVoltage(Long.parseLong(map.get("voltage_now")));
        powerSource.setCycleCount(Integer.parseInt(map.get("cycle_count")));
        powerSource.setState(map.get("status"));
        powerSource.setTechnology(map.get("technology"));
        return powerSource;
    }

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
