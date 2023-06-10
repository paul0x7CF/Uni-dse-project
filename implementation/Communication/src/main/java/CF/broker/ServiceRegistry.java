package CF.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.EServiceType;
import CF.sendable.MSData;

import java.util.*;

public class ServiceRegistry {
    private static final Logger log = LogManager.getLogger(ServiceRegistry.class);
    private final MSData currentService;
    private final Map<EServiceType, List<MSData>> availableServices = new HashMap<>();

    public ServiceRegistry(MSData currentService) {
        log.debug("New {} initialized with ID: {}", currentService.getType(), currentService.getId());
        this.currentService = currentService;
    }

    /**
     * Registers a service in the registry. Return whether the service was registered or not.
     *
     * @param msData
     * @return true if the service was registered, false if the service was not.
     */
    public boolean registerService(MSData msData) {
        if (msData.equals(currentService) || findService(msData) != null) {
            log.trace("{}, Service already registered: {}, equals: {}", currentService.getType(), msData.getId(), msData.equals(currentService));
            return false;
        }

        // ExchangeWorker only registers Exchange because they only talk through the LoadManager.
        if (currentService.getType() == EServiceType.ExchangeWorker && msData.getType() != EServiceType.Exchange) {
            log.trace("{}, ExchangeWorker only registers Exchange", currentService.getType());
            return false;
        }

        log.info("{} registering service {}", currentService.getPort(), msData.getPort());

        EServiceType serviceType = msData.getType();
        List<MSData> services = availableServices.get(serviceType);

        if (services == null) {
            services = new ArrayList<>();
            availableServices.put(serviceType, services);
        }

        if (!services.contains(msData)) {
            services.add(msData);
        }
        return true;
    }

    public void unregisterService(MSData msData) {
        EServiceType serviceType = msData.getType();
        List<MSData> services = availableServices.get(serviceType);

        if (services != null) {
            services.remove(msData);

            if (services.isEmpty()) {
                availableServices.remove(serviceType);
            }
        }
    }

    public MSData getCurrentService() {
        return currentService;
    }

    public MSData findService(UUID serviceId) {
        for (List<MSData> services : availableServices.values()) {
            for (MSData msData : services) {
                if (msData.getId().equals(serviceId)) {
                    log.trace("Found service: {}", msData.getId());
                    return msData;
                }
            }
        }
        return null;
    }

    /** Used by DiscoveryService to check if a service is already registered, stopping resending of registration
     */
    public boolean serviceExists(int port) {
        for (List<MSData> services : availableServices.values()) {
            for (MSData msData : services) {
                if (msData.getPort() == port) {
                    return true;
                }
            }
        }
        return false;
    }

    public MSData findService(MSData msData) {
        EServiceType serviceType = msData.getType();
        List<MSData> services = availableServices.get(serviceType);

        if (services != null && services.contains(msData)) {
            return msData;
        }

        return null;
    }

    public List<MSData> getAvailableServices() {
        List<MSData> list = new ArrayList<>();
        for (List<MSData> services : availableServices.values()) {
            list.addAll(services);
        }

        return list;
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return availableServices.getOrDefault(serviceType, Collections.emptyList());
    }
}
