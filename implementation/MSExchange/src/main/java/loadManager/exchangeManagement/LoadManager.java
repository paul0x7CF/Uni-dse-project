package loadManager.exchangeManagement;

import MSP.Exceptions.AllExchangesAtCapacityException;
import CF.exceptions.MessageProcessingException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;

import java.io.File;
import java.util.*;

public class LoadManager {
    private int nextServiceID = 1;
    private List<ExchangeServiceInformation> ListExchangeServices = Collections.synchronizedList(new ArrayList<>());

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

    public void setExchangeAtCapacity(UUID exchangeID) throws MessageProcessingException {
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
            throw new MessageProcessingException("ExchangeID is null");
        }
        if (!exchangeExists) {
            throw new MessageProcessingException("Exchange does not exist");
        }
        duplicateExchange();
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
            String projectPath = System.getProperty("user.dir"); //TODO: Replace with the actual path to your JAR file
            String pomPath = projectPath + "/pom.xml"; //TODO: Replace with the actual path to your pom.xml file

            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(new File(pomPath));
            request.setGoals(Collections.singletonList("exec"));

            Properties properties = new Properties();
            properties.setProperty("exec", "-s" + ++nextServiceID);
            request.setProperties(properties);

            Invoker invoker = new DefaultInvoker();
            invoker.execute(request);

            System.out.println("JAR file executed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
