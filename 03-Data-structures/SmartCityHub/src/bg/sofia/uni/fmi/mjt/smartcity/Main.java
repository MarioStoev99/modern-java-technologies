package bg.sofia.uni.fmi.mjt.smartcity;

import bg.sofia.uni.fmi.mjt.smartcity.device.SmartCamera;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartLamp;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartTrafficLight;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;
import bg.sofia.uni.fmi.mjt.smartcity.hub.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.smartcity.hub.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.smartcity.hub.SmartCityHub;

import java.time.LocalDateTime;
import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        LocalDateTime localDateTime1 = LocalDateTime.of(1999, 1, 2, 11, 11);
        LocalDateTime localDateTime2 = LocalDateTime.of(1999, 1, 3, 11, 11);
        LocalDateTime localDateTime3 = LocalDateTime.of(1999, 1, 4, 11, 11);
        LocalDateTime localDateTime4 = LocalDateTime.of(1999, 1, 5, 11, 11);
        LocalDateTime localDateTime5 = LocalDateTime.of(1999, 1, 6, 11, 11);
        LocalDateTime localDateTime6 = LocalDateTime.of(1999, 1, 7, 11, 11);
        SmartDevice device1 = new SmartCamera("SmartCamera", 20, localDateTime4);
        SmartDevice device2 = new SmartTrafficLight("SmartTrafficLight", 1, localDateTime5);
        SmartDevice device3 = new SmartLamp("SmartLamp", 5, localDateTime1);
        SmartDevice device4 = new SmartCamera("SmartCamera1", 2, localDateTime3);
        SmartDevice device5 = new SmartTrafficLight("SmartTrafficLight1", 4, localDateTime2);
        SmartDevice device6 = new SmartLamp("SmartLamp1", 3, localDateTime6);
        SmartCityHub smartCityHub = new SmartCityHub();
        try {
            smartCityHub.register(device1);
            smartCityHub.register(device2);
            smartCityHub.register(device3);
            smartCityHub.unregister(device3);
            smartCityHub.register(device4);
            smartCityHub.register(device5);
            smartCityHub.register(device6);

            Collection<String> theMostPowerDevices = smartCityHub.getTopNDevicesByPowerConsumption(6);
            for (String deviceId : theMostPowerDevices) {
                System.out.println(deviceId);
            }
            System.out.println("----------------------------");
            Collection<SmartDevice> smartDevices = smartCityHub.getFirstNDevicesByRegistration(3);
            for (SmartDevice device : smartDevices) {
                System.out.println(device.getName());
            }
            System.out.println("----------------------------");
            System.out.println(smartCityHub.getDeviceById("CM-SmartCamera-0"));
            System.out.println(smartCityHub.getDeviceQuantityPerType(DeviceType.CAMERA));
            System.out.println(smartCityHub.getDeviceQuantityPerType(DeviceType.TRAFFIC_LIGHT));
            System.out.println(smartCityHub.getDeviceQuantityPerType(DeviceType.LAMP));

        } catch (DeviceAlreadyRegisteredException e) {
            System.out.println(e.getMessage());
        } catch (DeviceNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
