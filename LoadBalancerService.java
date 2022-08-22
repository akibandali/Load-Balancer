package com.iptiq.loadbalancer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LoadBalancerService {
    private static List<Instance> activeInstances = Collections.synchronizedList(new LinkedList<>());
    private static List<Instance> inActiveInstances = Collections.synchronizedList(new LinkedList<>());
    private static final Integer MAX_LIMIt = 10;

    public static synchronized void registerActiveInstance (Instance instance) {
        if (activeInstances.size() == MAX_LIMIt) {
            throw new RuntimeException(String.format("More than %s instances can not be registered", MAX_LIMIt));
        }

        if (isInstanceExists(instance.getId())) {
            throw new RuntimeException(String.format("Instance with id- %s already exists", instance.getId()));
        }
        activeInstances.add(instance);
        System.out.println(String.format("New Instance Registered->>> %s",instance.getId()));
    }

    private synchronized static boolean isInstanceExists (String id) {
        return activeInstances.stream().anyMatch(instance -> instance.getId().equals(id));
    }

    public static synchronized List<String> getActiveInstanceIds () {
        List<String> activeInstanceIds = activeInstances.stream()
                                                        .filter(Instance::isActive)
                                                        .map(Instance::getId)
                                                        .collect(Collectors.toCollection(LinkedList::new));
        return activeInstanceIds;
    }

    public static synchronized void excludeActiveProviderById (String id) {
        activeInstances.removeIf(instance -> instance.getId().equals(id));
        System.out.printf("Removed provider with id %s%n", id);
    }

    public static synchronized void excludeInactiveProviderById (String id) {
        inActiveInstances.removeIf(instance -> instance.getId().equals(id));
        System.out.printf("Instance with id  %s become healthy again and registered", id);
    }

    public static void checkAndRemoveInactiveProviders () {
        List<Instance> inActiveList = activeInstances.stream()
                                                     .filter(instance -> !instance.isActive())
                                                     .collect(Collectors.toCollection(LinkedList::new));
        if(inActiveList.size()==0){
            System.out.println("No inactive instance found in health check");
            return;
        }
        inActiveList.forEach(instance -> {
            excludeActiveProviderById(instance.getId());
            inActiveInstances.add(instance);
        });

    }

    public synchronized static void doInstanceRegistration () {
        Instance instance = ProviderGenerator.getUniqueProvider();
        LoadBalancerService.registerActiveInstance(instance);
    }

    public static void checkAndAddBackActiveProviders () {
        List<Instance> activeInstances = inActiveInstances.stream()
                                                          .filter(Instance::isActive)
                                                          .collect(Collectors.toCollection(LinkedList::new));
        if(activeInstances.size()==0){
            System.out.println("No active instance found in health check");
            return;
        }
        activeInstances.forEach(instance -> {
            registerActiveInstance(instance);
            excludeInactiveProviderById(instance.getId());
        });
    }

    public static void makeInstanceInactiveRandomly () {
        if(activeInstances.size()==0){
            System.out.println("No active instance found in health check");
        }
        int index = new Random().nextInt(activeInstances.size());
        Instance instance = activeInstances.get(index);
        System.out.println(String.format("Making instance Inactive %s", instance.getId()));
        instance.setActive(false);
    }

    public static void makeInstanceActiveAgain () {
        if(inActiveInstances.size()==0){
            System.out.println("No active instance found in health check in previously inactive list");
         return;
        }
        int index = new Random().nextInt(inActiveInstances.size());
        Instance instance = inActiveInstances.get(index);
        System.out.println(String.format("Unhealthy instance %s becoming healthy again", instance.getId()));
        instance.setActive(true);
    }
}
