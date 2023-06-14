package MSF.historicData;

import CF.sendable.TimeSlot;
import MSF.data.EForecastType;
import MSF.exceptions.UnknownForecastTypeException;
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
    private static final String HISTORIC_DATA_FILE_PATH = "implementation/MSForecast/src/main/resources/";
    public static String getHistoricData(TimeSlot currentTime, EForecastType forecastType) throws UnknownForecastTypeException {
        switch (forecastType)
        {
            case GROUNDSTATION:
                return getGroundstationData(currentTime);
            case APOLIS:
                return getApolisData(currentTime);
            case HISTALP:
                return getHistalpData(currentTime);
            case INCA_L:
                return getIncaLData(currentTime);
            default:
                logger.error("ForecastType not found");
                throw new UnknownForecastTypeException();
        }
    }

    private static String getApolisData(TimeSlot currentTime) {
        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH + "APOLIS Data.csv"))) {
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

    private static String getGroundstationData(TimeSlot currentTime) {
        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH + "GROUNDSTATION Data.csv"))) {
            List<String[]> rows = reader.readAll();

            // Process the data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] data = rows.get(i);

                if (currentTime.getStartTime().toString().substring(0, 10).equals(data[0]))
                    return data[2] + ", " + data[3];
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }

    private static String getHistalpData(TimeSlot currentTime) {
        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH + "HISTALP Data.csv"))) {
            List<String[]> rows = reader.readAll();

            // Skip the header comments
            int dataStartIndex = 0;
            for (int i = 0; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length > 0 && !row[0].startsWith("#")) {
                    dataStartIndex = i;
                    break;
                }
            }

            int sunshineHours = 0;
            // Process the data rows
            for (int i = dataStartIndex + 1; i < rows.size(); i++) {
                String[] data = rows.get(i);

                for (int j = 0; j < data.length; j++) {
                    if (data[j].equals("999999"))
                        data[j] = "0";
                }

                switch (currentTime.getStartTime().toString().substring(5, 6)) {
                    case "01":
                        sunshineHours += Integer.parseInt(data[1]);
                    case "02":
                        sunshineHours += Integer.parseInt(data[2]);
                    case "03":
                        sunshineHours += Integer.parseInt(data[3]);
                    case "04":
                        sunshineHours += Integer.parseInt(data[4]);
                    case "05":
                        sunshineHours += Integer.parseInt(data[5]);
                    case "06":
                        sunshineHours += Integer.parseInt(data[6]);
                    case "07":
                        sunshineHours += Integer.parseInt(data[7]);
                    case "08":
                        sunshineHours += Integer.parseInt(data[8]);
                    case "09":
                        sunshineHours += Integer.parseInt(data[9]);
                    case "10":
                        sunshineHours += Integer.parseInt(data[10]);
                    case "11":
                        sunshineHours += Integer.parseInt(data[11]);
                    case "12":
                        sunshineHours += Integer.parseInt(data[12]);
                }
            }

            return String.valueOf(sunshineHours / 5);
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }

    private static String getIncaLData(TimeSlot currentTime) {
        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH + "INCA_L Data.csv"))) {
            List<String[]> rows = reader.readAll();

            // Process the data rows
            for (int i = 1; i < rows.size(); i++) {
                String[] data = rows.get(i);

                if (currentTime.getStartTime().toString().substring(0, 10).equals(data[0]))
                    return data[1];
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }
}
