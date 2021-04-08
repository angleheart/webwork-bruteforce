import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Launcher {


    public static void main(String[] args) {

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties config = new Properties();

            if (input == null) {
                System.out.println("Failed to load config");
                return;
            }

            config.load(input);
            String url = config.getProperty("url");
            int totalBoxes = Integer.parseInt(config.getProperty("totalBoxes"));
            int startBox = Integer.parseInt(config.getProperty("startBox"));
            int endBox = Integer.parseInt(config.getProperty("endBox"));
            boolean includeNegative = Boolean.parseBoolean(config.getProperty("includeNegative"));
            double initialPrecision = Double.parseDouble(config.getProperty("initialPrecision"));
            int increasePrecisionAt = Integer.parseInt(config.getProperty("increasePrecisionAt"));
            boolean showAttempts = Boolean.parseBoolean(config.getProperty("showAttempts"));
            boolean headless = Boolean.parseBoolean(config.getProperty("headless"));

            int instances = Integer.parseInt(config.getProperty("instances"));

            Main attacker = new Main(url, totalBoxes, startBox, endBox, includeNegative, initialPrecision, increasePrecisionAt, showAttempts, headless);
            attacker.launchInstances(instances);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
