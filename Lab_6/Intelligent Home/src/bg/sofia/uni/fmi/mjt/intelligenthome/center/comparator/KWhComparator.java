package bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class KWhComparator implements Comparator<IoTDevice> {
    private final static double THRESHOLD = 0.0001;

    public int compare(IoTDevice firstDevice, IoTDevice secondDevice) {
        long hoursFirstDevice = Duration.between(firstDevice.getInstallationDateTime(), LocalDateTime.now()).toHours();
        long hoursSecondDevice = Duration.between(secondDevice.getInstallationDateTime(), LocalDateTime.now()).toHours();

        double firstDeviceConsumption = firstDevice.getPowerConsumption();
        double secondDeviceConsumption = secondDevice.getPowerConsumption();

        double firstDevicePowerConsumption = firstDeviceConsumption * hoursFirstDevice;
        double secondDevicePowerConsumption = secondDeviceConsumption * hoursSecondDevice;

        if (firstDevicePowerConsumption - secondDevicePowerConsumption < THRESHOLD ) {
            return 0;
        }

        return (int) (firstDevicePowerConsumption - secondDevicePowerConsumption);
    }
}
