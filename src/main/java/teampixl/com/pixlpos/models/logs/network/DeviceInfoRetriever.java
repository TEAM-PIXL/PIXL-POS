package teampixl.com.pixlpos.models.logs.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DeviceInfoRetriever {
    public static String getDeviceModel() {
        String os = System.getProperty("os.name").toLowerCase();
        String model = null;
        try {
            Process process = null;
            if (os.contains("mac")) {
                /* macOS */
                process = new ProcessBuilder("system_profiler", "SPHardwareDataType").start();
            } else if (os.contains("win")) {
                /* Windows */
                process = new ProcessBuilder("wmic", "computersystem", "get", "model").start();
            } else if (os.contains("nux") || os.contains("nix")) {
                /* Linux */
                process = new ProcessBuilder("cat", "/sys/devices/virtual/dmi/id/product_name").start();
            } else {
                return "Unsupported operating system.";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (os.contains("win")) {
                    if (!line.toLowerCase().contains("model")) {
                        model = line;
                        break;
                    }
                } else if (os.contains("mac")) {
                    if (line.startsWith("Model Identifier:")) {
                        model = line.split(":")[1].trim();
                        break;
                    }
                } else {
                    model = line;
                    break;
                }
            }
            reader.close();
            return (model != null) ? model : "Device model not found.";
        } catch (Exception e) {
            return "Error retrieving device model: " + e.getMessage();
        }
    }
}

