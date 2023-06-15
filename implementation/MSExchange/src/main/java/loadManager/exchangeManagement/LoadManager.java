package loadManager.exchangeManagement;

import CF.exceptions.MessageProcessingException;
import MSP.Exceptions.AllExchangesAtCapacityException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public class LoadManager {
    private static final Logger logger = LogManager.getLogger(LoadManager.class);
    private int nextServiceID = 1;
    private List<ExchangeServiceInformation> listExchangeServices = Collections.synchronizedList(new ArrayList<>());

    public void addExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        // Adds a new MicroserviceInformation object to the list.
        if (exchangeServiceInformation != null) {
            if (!listExchangeServices.contains(exchangeServiceInformation)) {
                listExchangeServices.add(exchangeServiceInformation);
                logger.info("Added ExchangeServiceInformation with ID: " + exchangeServiceInformation.getExchangeID());
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
            if (listExchangeServices.contains(exchangeServiceInformation)) {
                listExchangeServices.remove(exchangeServiceInformation);
                logger.info("Removed ExchangeServiceInformation with ID: " + exchangeServiceInformation.getExchangeID());
            } else {
                throw new IllegalArgumentException("ExchangeServiceInformation does not exist");
            }
        } else {
            throw new IllegalArgumentException("ExchangeServiceInformation is null");
        }
    }

    public void setExchangeAtCapacity(UUID exchangeID) throws MessageProcessingException {
        // Sets the ExchangeServiceInformation object with the given ID to at capacity.
        boolean exchangeExists = false;
        if (exchangeID != null) {
            for (ExchangeServiceInformation exchangeServiceInformation : listExchangeServices) {
                if (exchangeServiceInformation.getExchangeID().equals(exchangeID)) {
                    exchangeServiceInformation.setAtCapacity(true);
                    exchangeExists = true;
                }
            }
        } else {
            throw new MessageProcessingException("ExchangeID is null");
        }
        if (!exchangeExists) {
            throw new MessageProcessingException("Exchange does not exist");
        }
        duplicateExchange();
    }

    public ExchangeServiceInformation getFreeExchange() throws AllExchangesAtCapacityException {
        // Returns the first ExchangeServiceInformation object in the list that is not at capacity.

        for (ExchangeServiceInformation exchangeServiceInformation : listExchangeServices) {
            if (!exchangeServiceInformation.isAtCapacity()) {
                return exchangeServiceInformation;
            }
        }
        throw new AllExchangesAtCapacityException("All exchanges are at capacity");
    }

    /**
     * The method duplicateExchange takes an integer parameter serviceInstanceID, which is used as an argument in the
     * subsequent execution of the JAR file. It begins by retrieving the project path using
     * System.getProperty("user.dir") and sets the pomPath by appending /pom.xml to the project path.
     * <p>
     * An InvocationRequest object is created, and its pomFile property is set to the pomPath obtained earlier. This
     * tells Maven which POM (Project Object Model) file to use. The goals property is set to a singleton list
     * containing the string "exec". This specifies that the goal to be executed is the "exec" goal, which typically
     * executes an arbitrary Java class within the Maven project.
     * <p>
     * A Properties object is created to hold the properties to be passed to the Maven execution. The "exec" property is
     * set with a value of "-d" appended with the serviceInstanceID parameter. This sets the desired command-line
     * arguments for the JAR execution.
     * <p>
     * An Invoker object is created, and the execute method is called with the request object as an argument. This
     * executes the Maven invocation with the provided request, triggering the execution of the JAR file using the
     * specified properties and goals.
     */
    private void duplicateExchange() {
        // Executes the jar-File again using Maven.
        try {
            String projectPath = System.getProperty("C://temp//DSE_jars//MSExchange.jar"); //TODO: Replace with the actual path to your JAR file
            String pomPath = projectPath + "C://Universität//DSE//Gruppenprojekt//DSE_Team_202//implementation//MSExchange//pom.xml"; //TODO: Replace with the actual path to your pom.xml file

            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(new File(pomPath));
            request.setGoals(Collections.singletonList("exec"));

            Properties properties = new Properties();
            properties.setProperty("exec", "-s" + ++nextServiceID);
            request.setProperties(properties);

            Invoker invoker = new DefaultInvoker();
            invoker.setMavenHome(new File("C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2022.2\\plugins\\maven\\lib\\maven3\n"));
            invoker.setMavenExecutable(new File(invoker.getMavenHome(), "bin\\mvn.cmd"));

            //configure input and output streams
            InputStream inputStream = System.in;
            OutputStream outputStream = System.out;
            PrintStream printStream = new PrintStream(outputStream);
            invoker.setErrorHandler(new PrintStreamHandler(printStream, true));
            invoker.setOutputHandler(new PrintStreamHandler(printStream, true));
            invoker.setInputStream(inputStream);


            InvocationResult result = invoker.execute(request);

            if (result.getExitCode() == 0) {
                System.out.println("JAR file executed successfully.");
            } else {
                System.out.println("JAR file execution failed. Exit code: " + result.getExitCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
