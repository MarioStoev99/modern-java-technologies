package bg.sofia.uni.fmi.mjt.smartcity.comparator;

import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class IncreasingPowerConsumptionComparator implements Comparator<SmartDevice> {

    @Override
    public int compare(SmartDevice first, SmartDevice second) {
        long firstInstallationTimeInHours = Duration.between(first.getInstallationDateTime(), LocalDateTime.now()).toHours();
        long secondInstallationTimeInHours = Duration.between(second.getInstallationDateTime(), LocalDateTime.now()).toHours();
        return Double.compare(second.getPowerConsumption() * secondInstallationTimeInHours ,first.getPowerConsumption() * firstInstallationTimeInHours);
    }

}

