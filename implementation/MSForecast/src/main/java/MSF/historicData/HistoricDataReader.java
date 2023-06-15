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
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to read the historic data from a CSV file.
 */
public class HistoricDataReader {
    private static final Logger logger = LogManager.getLogger(HistoricDataReader.class);
    private static final String HISTORIC_DATA_FILE_PATH = "implementation/MSForecast/src/main/resources/";
    public static List<String> getHistoricData(TimeSlot currentTime, EForecastType forecastType) throws UnknownForecastTypeException {
        switch (forecastType) {
            case GROUNDSTATION -> {
                return getGroundstationData(currentTime);
            }
            case APOLIS -> {
                return getApolisData(currentTime);
            }
            case INCA_L -> {
                return getIncaLData(currentTime);
            }
            default -> {
                logger.error("ForecastType not found");
                throw new UnknownForecastTypeException();
            }
        }
    }

    private static List<String> getApolisData(TimeSlot currentTime) {
        List<String> values = new LinkedList<>();

        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH + "APOLIS Data.csv"))) {
            List<String[]> rows = reader.readAll();

            LocalDateTime truncatedDateTime = LocalDateTime.of(
                    currentTime.getStartTime().getYear(),
                    currentTime.getStartTime().getMonth(),
                    currentTime.getStartTime().getDayOfMonth(),
                    currentTime.getStartTime().getHour(),
                    currentTime.getStartTime().getMinute(),
                    currentTime.getStartTime().getSecond());

            for (int i = 1; i < rows.size(); i++) {
                String[] data = rows.get(i);

                String dateTimeString = data[0].substring(0, 16);
                LocalDateTime rowDateTime = LocalDateTime.parse(dateTimeString);

                LocalDateTime truncatedRowDateTime = LocalDateTime.of(
                        rowDateTime.getYear(),
                        rowDateTime.getMonth(),
                        rowDateTime.getDayOfMonth(),
                        rowDateTime.getHour(),
                        rowDateTime.getMinute(),
                        rowDateTime.getSecond());

                if (truncatedRowDateTime.getYear() != truncatedDateTime.getYear() && truncatedRowDateTime.getMonth() == truncatedDateTime.getMonth() && truncatedRowDateTime.getDayOfMonth() == truncatedDateTime.getDayOfMonth() && truncatedDateTime.getHour() > 8 && truncatedDateTime.getHour() < 20)
                    values.add(String.valueOf(Double.parseDouble(data[2]) * Double.parseDouble(data[1]) * 1000 / 24));

                if (truncatedRowDateTime.isAfter(truncatedDateTime.minusDays(14)) && truncatedRowDateTime.isBefore(truncatedDateTime) && truncatedDateTime.getHour() > 8 && truncatedDateTime.getHour() < 20)
                    values.add(String.valueOf(Double.parseDouble(data[2]) * Double.parseDouble(data[1]) * 1000 / 24));
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return values;
    }

    private static List<String> getGroundstationData(TimeSlot currentTime) {
        List<String> values = new LinkedList<>();

        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH + "GROUNDSTATION Data.csv"))) {
            List<String[]> rows = reader.readAll();

            LocalDateTime truncatedDateTime = LocalDateTime.of(
                    currentTime.getStartTime().getYear(),
                    currentTime.getStartTime().getMonth(),
                    currentTime.getStartTime().getDayOfMonth(),
                    currentTime.getStartTime().getHour(),
                    currentTime.getStartTime().getMinute(),
                    currentTime.getStartTime().getSecond());

            for (int i = 1; i < rows.size(); i++) {
                String[] data = rows.get(i);

                String dateTimeString = data[0].substring(0, 16);
                LocalDateTime rowDateTime = LocalDateTime.parse(dateTimeString);

                LocalDateTime truncatedRowDateTime = LocalDateTime.of(
                        rowDateTime.getYear(),
                        rowDateTime.getMonth(),
                        rowDateTime.getDayOfMonth(),
                        rowDateTime.getHour(),
                        rowDateTime.getMinute(),
                        rowDateTime.getSecond());

                if (truncatedRowDateTime.getYear() != truncatedDateTime.getYear() && truncatedRowDateTime.getMonth() == truncatedDateTime.getMonth() && truncatedRowDateTime.getDayOfMonth() == truncatedDateTime.getDayOfMonth() && truncatedRowDateTime.getHour() == truncatedDateTime.getHour())
                    values.add(String.valueOf(Double.parseDouble(data[2]) * Double.parseDouble(data[3])));

                if (truncatedRowDateTime.isAfter(truncatedDateTime.minusDays(14)) && truncatedRowDateTime.isBefore(truncatedDateTime)) {
                    if (truncatedRowDateTime.getHour() == truncatedDateTime.getHour())
                        values.add(String.valueOf(Double.parseDouble(data[2]) * Double.parseDouble(data[3])));
                }
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return values;
    }

    private static List<String> getIncaLData(TimeSlot currentTime) {
        List<String> values = new LinkedList<>();

        try (CSVReader reader = new CSVReader(new FileReader(HISTORIC_DATA_FILE_PATH + "INCA_L Data.csv"))) {
            List<String[]> rows = reader.readAll();

            LocalDateTime truncatedDateTime = LocalDateTime.of(
                    currentTime.getStartTime().getYear(),
                    currentTime.getStartTime().getMonth(),
                    currentTime.getStartTime().getDayOfMonth(),
                    currentTime.getStartTime().getHour(),
                    currentTime.getStartTime().getMinute(),
                    currentTime.getStartTime().getSecond());

            for (int i = 1; i < rows.size(); i++) {
                String[] data = rows.get(i);

                String dateTimeString = data[0].substring(0, 16);
                LocalDateTime rowDateTime = LocalDateTime.parse(dateTimeString);

                LocalDateTime truncatedRowDateTime = LocalDateTime.of(
                        rowDateTime.getYear(),
                        rowDateTime.getMonth(),
                        rowDateTime.getDayOfMonth(),
                        rowDateTime.getHour(),
                        rowDateTime.getMinute(),
                        rowDateTime.getSecond());

                if (truncatedRowDateTime.getYear() != truncatedDateTime.getYear() && truncatedRowDateTime.getMonth() == truncatedDateTime.getMonth() && truncatedRowDateTime.getDayOfMonth() == truncatedDateTime.getDayOfMonth() && truncatedRowDateTime.getHour() == truncatedDateTime.getHour())
                    values.add(data[1]);

                if (truncatedRowDateTime.isAfter(truncatedDateTime.minusDays(14)) && truncatedRowDateTime.isBefore(truncatedDateTime)) {
                    if (truncatedRowDateTime.getHour() == truncatedDateTime.getHour())
                        values.add(data[1]);
                }
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return values;
    }
}
