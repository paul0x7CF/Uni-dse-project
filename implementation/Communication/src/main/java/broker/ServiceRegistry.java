package broker;

import sendable.EServiceType;
import sendable.MSData;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceRegistry {
    private MSData currentService;
    private Map<EServiceType, List<MSData>> availableServices = new HashMap<>();

    public ServiceRegistry(MSData currentService) {
        this.currentService = currentService;
    }

    public void registerService(MSData msData) {
        if (findService(msData) != null) {
            return;
        }
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
        for (List<MSData> services : availableServices.values()) {
            for (MSData msData : services) {
                if (msData.getId().equals(serviceId)) {
                    return msData;
                }
            }
        }
        return null;
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
        return availableServices.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return availableServices.getOrDefault(serviceType, Collections.emptyList());
    }
}
