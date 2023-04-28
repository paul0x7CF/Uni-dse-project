package broker;

import sendable.EServiceType;
import sendable.MSData;

import java.util.*;

public class ServiceRegistry {
    MSData currentService;
    Map<EServiceType, List<MSData>> availableServices = new HashMap<>();

    public ServiceRegistry(MSData currentService) {
        this.currentService = currentService;
    }

    public MSData getCurrentService() {
        return currentService;
    }

    public Map<EServiceType, List<MSData>> getAvailableServices() {
        return availableServices;
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return availableServices.getOrDefault(serviceType, Collections.emptyList());
    }

    public void registerService(MSData msData) {
        EServiceType serviceType = msData.getType();
        List<MSData> services = availableServices.get(serviceType);

        if (services == null) {
            services = new ArrayList<>();
            availableServices.put(serviceType, services);
        }

        if (!services.contains(msData)) {
            services.add(msData);
        }
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
}
