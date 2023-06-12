package MSF.historicData;

import MSF.propertyHandler.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to read the historic data from a CSV file.
 */
public class HistoricDataReader {
    private static final Logger logger = LogManager.getLogger(HistoricDataReader.class);
    private static final String HISTORIC_DATA_FILE_PATH = "implementation/MSForecast/src/main/resources/";
    public static String readHistoricData() {
        //TODO: read the historic data from the CSV file
        return "DATA";
    }
}
