package teampixl.com.pixlpos.models.logs.network;

public class Util {
    public static String getIp() throws Exception {
        String IP = "Unknown";
        try {
            IP = IpChecker.getIp();
        } catch (Exception e) {
            return IP;
        }
        return IP;
    }

    public static String getLocation(String IP) throws Exception {
        String location = "Unknown";
        try {
            ServerLocation serverLocation = GeoLocation.getLocation(IP);
            location = GeoLocation.displayLocation(serverLocation);
        } catch (Exception e) {
            return location;
        }
        return location;
    }

    public static String checkOS() {
        OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
        return switch (ostype) {
            case Windows -> OsCheck.OSType.Windows.toString();
            case MacOS -> OsCheck.OSType.MacOS.toString();
            case Linux -> OsCheck.OSType.Linux.toString();
            case Other -> "Unknown";
        };
    }

    public static String getDeviceInfo() {
        String deviceInfo = "Unknown";
        try {
            deviceInfo = DeviceInfoRetriever.getDeviceModel();
        } catch (Exception e) {
            return deviceInfo;
        }
        return deviceInfo;
    }

    public static String getMacAddress() {
        String macAddress = "Unknown";
        try {
            macAddress = MacAddressRetriever.getMacAddress();
        } catch (Exception e) {
            return macAddress;
        }
        return macAddress;
    }

}
