package MSP.Logic.Prosumer;

import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import MSP.Data.EProducerType;
import MSP.Data.Producer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

/**
 * Interface for the Prosumer classes to forward itself to the REST API.
 * <p>
 *     The {@link MSP.Communication.Communication} class will have a reference to this interface.
 *     This way, the producers and consumers can be accessed.
 * </p>
 *
 * @see MSP.Communication.Communication
 */
public interface RESTData {
    final Logger logger = LogManager.getLogger(RESTData.class);

    // Create
    default boolean createProducer(EProducerType producerType) {
        logger.error("createProducer() not implemented");
        return false;
    }

    default boolean createConsumer(EConsumerType consumerType) {
        logger.error("createConsumer() not implemented");
        return false;
    }


    // Read
    default HashSet<Producer> getProducers() {
        logger.error("getProducers() not implemented");
        return new HashSet<>();
    }

    default HashSet<Consumer> getConsumers() {
        logger.error("getConsumers() not implemented");
        return new HashSet<>();
    }


    // Update
    default boolean updateProducer(EProducerType producerType, int efficiency) {
        logger.error("updateProducer() not implemented");
        return false;
    }

    default boolean updateConsumer(EConsumerType consumerType, int efficiency) {
        logger.error("updateConsumer() not implemented");
        return false;
    }

    // Delete
    default boolean deleteProducer(EProducerType producerType) {
        logger.error("deleteProducer() not implemented");
        return false;
    }

    default boolean deleteConsumer(EConsumerType consumerType) {
        logger.error("deleteConsumer() not implemented");
        return false;
    }
}
