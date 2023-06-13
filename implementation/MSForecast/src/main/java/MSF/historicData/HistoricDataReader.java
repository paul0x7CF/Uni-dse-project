package MSF.historicData;

import CF.sendable.TimeSlot;
import MSF.propertyHandler.PropertiesReader;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Class to read the historic data from a CSV file.
 */
public class HistoricDataReader {
    private static final Logger logger = LogManager.getLogger(HistoricDataReader.class);
    private static final String HISTORIC_DATA_FILE_PATH = "implementation/MSForecast/src/main/resources/APOLIS Data.csv";
    public static String getHistoricData(TimeSlot currentTime) {
        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH))) {
            List<String[]> rows = reader.readAll();

            // Process the data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] data = rows.get(i);

                if (currentTime.getStartTime().toString().substring(0, 10).equals(data[0]))
                    return data[1] + ", " + data[2];
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }
}
