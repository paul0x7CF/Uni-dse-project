package loadManager.exchangeManagement;

import loadManager.Exceptions.AllExchangesAtCapacityException;

import java.util.List;
import java.util.UUID;

public class LoadManager {

    private List<ExchangeServiceInformation> ListExchangeServices;

    public void addExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        // Adds a new MicroserviceInformation object to the list.
        if (exchangeServiceInformation != null) {
            if (!ListExchangeServices.contains(exchangeServiceInformation)) {
                ListExchangeServices.add(exchangeServiceInformation);
            } else {
                throw new IllegalArgumentException("ExchangeServiceInformation already exists");
            }
        } else {
            throw new IllegalArgumentException("ExchangeServiceInformation is null");
        }

    }

    public void removeExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        // Removes the MicroserviceInformation object with the given ID from the list.
        if (exchangeServiceInformation != null) {
            if (ListExchangeServices.contains(exchangeServiceInformation)) {
                ListExchangeServices.remove(exchangeServiceInformation);
            } else {
                throw new IllegalArgumentException("ExchangeServiceInformation does not exist");
            }
        } else {
            throw new IllegalArgumentException("ExchangeServiceInformation is null");
        }
    }

    public void setExchangeAtCapacity(UUID exchangeID) {
        // Sets the ExchangeServiceInformation object with the given ID to at capacity.
        boolean exchangeExists = false;
        if (exchangeID != null) {
            for (ExchangeServiceInformation exchangeServiceInformation : ListExchangeServices) {
                if (exchangeServiceInformation.getExchangeID().equals(exchangeID)) {
                    exchangeServiceInformation.setAtCapacity(true);
                    exchangeExists = true;
                }
            }
        } else {
            throw new IllegalArgumentException("ExchangeID is null");
        }
        if (!exchangeExists) {
            throw new IllegalArgumentException("Exchange does not exist");
        }
    }

    public ExchangeServiceInformation getFreeExchange() throws AllExchangesAtCapacityException {
        // Returns the first ExchangeServiceInformation object in the list that is not at capacity.

        for (ExchangeServiceInformation exchangeServiceInformation : ListExchangeServices) {
            if (!exchangeServiceInformation.isAtCapacity()) {
                return exchangeServiceInformation;
            }
        }
        throw new AllExchangesAtCapacityException("All exchanges are at capacity");
    }

    public void duplicateExchange() {
        // Executes the jar-File again.
        try {
            String jarPath = "/path/to/your/jarfile.jar"; //TODO: Replace with the actual path to your JAR file
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarPath, "-d"); //-d: duplicated
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("JAR file executed successfully.");
            } else {
                System.out.println("Failed to execute JAR file. Exit code: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
