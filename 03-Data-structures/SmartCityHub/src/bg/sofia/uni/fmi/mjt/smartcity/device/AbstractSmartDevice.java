package bg.sofia.uni.fmi.mjt.smartcity.device;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractSmartDevice implements SmartDevice {

    private final String name;
    private final double powerConsumption;
    private final LocalDateTime installationDateTime;
    private final String id;

    protected AbstractSmartDevice(String name, double powerConsumption, LocalDateTime installationDateTime) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
        this.id = DeviceIdGenerator.generate(getType(), name);
    }

    @Override
    public String getId() {
        return id;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractSmartDevice that = (AbstractSmartDevice) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbstractSmartDevice{" +
                "name='" + name + '\'' +
                ", powerConsumption=" + powerConsumption +
                ", installationDateTime=" + installationDateTime +
                ", id='" + id + '\'' +
                '}';
    }
}
