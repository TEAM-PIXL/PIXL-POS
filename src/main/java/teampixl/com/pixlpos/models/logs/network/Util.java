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
}
