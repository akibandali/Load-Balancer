package com.iptiq.loadbalancer;

import java.util.List;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private static  Integer current = 0;

    @Override
    public  synchronized String getProviderInstance() {
        List<String> activeInstances = LoadBalancerService.getActiveInstanceIds();
        String instance = null;
        synchronized (current){
        if (activeInstances.size() == 0) {
            throw new RuntimeException("No active instance is available");
        }
        if (current > activeInstances.size() - 1) {
            current=0;
        }
        instance = activeInstances.get(current);
        current++;
        return instance;
    }}
}
