package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class IoTDeviceBase implements IoTDevice {
    private static int uniqueNumberDevice = 0;
    private final String name;
    private final double powerConsumption;
    private final LocalDateTime installationDateTime;
    private LocalDateTime registration;
    private final String deviceID;
    private final DeviceType type;

    public IoTDeviceBase(String name, double powerConsumption, LocalDateTime installationDateTime, DeviceType type) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
        this.type = type;

        this.deviceID = STR."\{type.getShortName()}-\{name}-\{uniqueNumberDevice}";
        ++uniqueNumberDevice;
    }

    @Override
    public String getId(){
        return deviceID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public DeviceType getType(){
        return type;
    }

    @Override
    public long getRegistration() {
        return Duration.between(registration, LocalDateTime.now()).toHours();
    }

    @Override
    public void setRegistration(LocalDateTime registration) {
        this.registration = registration;
    }

    @Override
    public long getPowerConsumptionKWh() {
        Duration duration = Duration.between(getInstallationDateTime(), LocalDateTime.now());

        return (long) (duration.toHours() *  powerConsumption);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IoTDeviceBase that = (IoTDeviceBase) o;
        return Objects.equals(deviceID, that.deviceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceID);
    }
}
