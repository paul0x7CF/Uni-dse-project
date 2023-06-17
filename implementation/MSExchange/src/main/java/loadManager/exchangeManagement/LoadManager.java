package loadManager.exchangeManagement;

import MSP.Exceptions.AllExchangesAtCapacityException;
import msExchange.MSExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LoadManager {
    private static final Logger logger = LogManager.getLogger(LoadManager.class);
    private final List<ExchangeServiceInformation> listExchangeServices = Collections.synchronizedList(new ArrayList<>());
    private int nextServiceID = 1;

    public void addExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        if (exchangeServiceInformation == null) {
            throw new IllegalArgumentException("LOAD_MANAGER: ExchangeServiceInformation is null");
        }

        if (!listExchangeServices.contains(exchangeServiceInformation)) {
            listExchangeServices.add(exchangeServiceInformation);
            logger.debug("LOAD_MANAGER: Added ExchangeServiceInformation with ID: {}", exchangeServiceInformation.getExchangeID());

        }

        //TODO: remove this statement after testing
        if ((nextServiceID + 4) % 5 == 0) {
            duplicateExchange();
        }
    }

    //TODO: remove Exchange Service
    public void removeExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        if (exchangeServiceInformation == null) {
            throw new IllegalArgumentException("LOAD_MANAGER: ExchangeServiceInformation is null");
        }

        if (!listExchangeServices.contains(exchangeServiceInformation)) {
            throw new IllegalArgumentException("LOAD_MANAGER: ExchangeServiceInformation does not exist");
        }

        listExchangeServices.remove(exchangeServiceInformation);
        logger.debug("LOAD_MANAGER: Removed ExchangeServiceInformation with ID: {}", exchangeServiceInformation.getExchangeID());
    }

    public void setExchangeCapacity(UUID exchangeID) {
        if (exchangeID == null) {
            throw new IllegalArgumentException("LOAD_MANAGER: ExchangeID is null");
        }

        boolean exchangeExists = false;
        boolean atCapacity = false;

        for (ExchangeServiceInformation exchangeServiceInformation : listExchangeServices) {
            if (exchangeServiceInformation.getExchangeID().equals(exchangeID)) {
                atCapacity = exchangeServiceInformation.flipAtCapacity();
                exchangeExists = true;
                break;
            }
        }

        if (!exchangeExists) {
            throw new IllegalArgumentException("LOAD_MANAGER: Exchange does not exist");
        }

        if (atCapacity) {
            duplicateExchange();
        }
    }

    public ExchangeServiceInformation getFreeExchange() throws AllExchangesAtCapacityException {
        // Returns the first ExchangeServiceInformation object in the list that is not at capacity.
        logger.trace("LOAD_MANAGER: Exchange Services: {}", listExchangeServices.size());
        for (ExchangeServiceInformation exchangeServiceInformation : listExchangeServices) {
            if (!exchangeServiceInformation.isAtCapacity()) {
                return exchangeServiceInformation;
            }
        }
        throw new AllExchangesAtCapacityException("LOAD_MANAGER: All exchanges are at capacity");
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
        /*try {
            String projectPath = System.getProperty("C://temp//DSE_jars//MSExchange.jar"); //TODO: Replace with the actual path to your JAR file
            String pomPath = projectPath + "C://Universität//DSE//Gruppenprojekt//DSE_Team_202//implementation//MSExchange//pom.xml"; //TODO: Replace with the actual path to your pom.xml file

            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(new File(pomPath));
            request.setGoals(Collections.singletonList("exec"));

            Properties properties = new Properties();
            properties.setProperty("exec", "-d" + ++nextServiceID);
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
        }*/

        //alternative
        logger.debug("LOAD_MANAGER: Duplicating Exchange Service");
        nextServiceID++;
        MSExchange msExchange = new MSExchange(true, nextServiceID++);
        msExchange.run();

    }
}
