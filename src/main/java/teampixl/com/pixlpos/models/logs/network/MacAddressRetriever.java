package teampixl.com.pixlpos.models.logs.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MacAddressRetriever {
    public static String getMacAddress() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            if (network == null) {
                return "Network Interface for the specified IP not found.";
            }

            byte[] mac = network.getHardwareAddress();

            if (mac == null) {
                return "MAC address not found.";
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            return sb.toString();
        } catch (UnknownHostException e) {
            return "Unknown Host Exception: " + e.getMessage();
        } catch (SocketException e) {
            return "Socket Exception: " + e.getMessage();
        }
    }
}

