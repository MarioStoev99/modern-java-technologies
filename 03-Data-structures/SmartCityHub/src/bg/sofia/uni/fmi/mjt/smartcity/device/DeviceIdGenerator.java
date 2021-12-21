package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.util.EnumMap;
import java.util.Map;

public class DeviceIdGenerator {

    private static final char SEPARATOR = '-';

    private static final Map<DeviceType, Integer> deviceIds = new EnumMap<>(DeviceType.class);

    public static String generate(DeviceType type,String name) {
        if (!deviceIds.containsKey(type)) {
            deviceIds.put(type, 0);
        }
        int deviceIdCount = deviceIds.get(type);
        deviceIds.put(type, deviceIds.get(type) + 1);
        return type.getShortName() + SEPARATOR + name + SEPARATOR + deviceIdCount;
    }

}
