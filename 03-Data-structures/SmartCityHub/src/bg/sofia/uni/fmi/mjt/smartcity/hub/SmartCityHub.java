package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.comparator.IncreasingPowerConsumptionComparator;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.util.*;

public class SmartCityHub {

    private final Map<String, SmartDevice> devices;
    private final Map<DeviceType, Integer> deviceTypeCounter;

    public SmartCityHub() {
        this.devices = new LinkedHashMap<>();
        this.deviceTypeCounter = new EnumMap<>(DeviceType.class);
    }

    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        checkIsNotNull(device, "device");

        if (devices.containsKey(device.getId())) {
            throw new DeviceAlreadyRegisteredException("You have already registered this device!");
        }

        if (!deviceTypeCounter.containsKey(device.getType())) {
            deviceTypeCounter.put(device.getType(), 0);
        }
        deviceTypeCounter.put(device.getType(), deviceTypeCounter.get(device.getType()) + 1);

        devices.put(device.getId(), device);
    }

    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        checkIsNotNull(device, "device");
        if (!devices.containsKey(device.getId())) {
            throw new DeviceNotFoundException("Such device does not exist in the system");
        }

        if (deviceTypeCounter.get(device.getType()) == 1) {
            deviceTypeCounter.remove(device.getType());
        } else {
            deviceTypeCounter.put(device.getType(), deviceTypeCounter.get(device.getType()) - 1);
        }

        devices.remove(device.getId());
    }

    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        checkIsNotNull(id, "Id");
        checkIsNotNull(devices.get(id), "device");

        return devices.get(id);
    }

    public int getDeviceQuantityPerType(DeviceType type) {
        checkIsNotNull(type, "Type");

        return deviceTypeCounter.get(type);
    }

    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        checkIfNIsNegative(n);

        IncreasingPowerConsumptionComparator comparator = new IncreasingPowerConsumptionComparator();
        List<SmartDevice> devicesSortedByPower = new LinkedList<>(devices.values());
        Collections.sort(devicesSortedByPower, comparator);
        return getIdsOfDevicesSortedByPower(devicesSortedByPower, n);
    }

    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {
        checkIfNIsNegative(n);

        if (n > devices.size()) {
            n = devices.size();
        }

        List<SmartDevice> smartDevices = new ArrayList<>(devices.values());
        smartDevices = smartDevices.subList(devices.size() - n, devices.size());
        Collections.reverse(smartDevices);

        return smartDevices;
    }

    private Collection<String> getIdsOfDevicesSortedByPower(List<SmartDevice> devicesSortedByPower, int n) {
        List<String> IdsOfDevicesSortedByPower = new ArrayList<>();

        for (SmartDevice device : devicesSortedByPower) {
            if (n-- == 0) {
                break;
            }
            IdsOfDevicesSortedByPower.add(device.getId());
        }
        return IdsOfDevicesSortedByPower;
    }

    private void checkIsNotNull(Object key, String keyDescription) {
        if (key == null) {
            throw new IllegalArgumentException(keyDescription + " cannot be null!");
        }
    }

    private void checkIfNIsNegative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N cannot be negative!");
        }
    }

}