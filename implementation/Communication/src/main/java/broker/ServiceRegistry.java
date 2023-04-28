package broker;

import sendable.EServiceType;
import sendable.MSData;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceRegistry {
    MSData currentService;
    Map<EServiceType, List<MSData>> availableServices = new HashMap<>();

    public ServiceRegistry(MSData currentService) {
        this.currentService = currentService;
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

    public MSData getCurrentService() {
        return currentService;
    }

    public MSData findService(UUID serviceId) {
        return availableServices.values().stream()
                .flatMap(Collection::stream)
                .filter(msData -> msData.getId().equals(serviceId))
                .findFirst()
                .orElse(null);
    }

    public List<MSData> getAvailableServices() {
        return availableServices.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return availableServices.getOrDefault(serviceType, Collections.emptyList());
    }
}
