package com.iptiq.loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public String getProviderInstance() {
        List<String> activeInstances = LoadBalancerService.getActiveInstanceIds();
        if (activeInstances.size() == 0) {
            throw new RuntimeException("No active instance is available");
        }
        Random random = new Random();
        int index = random.nextInt(activeInstances.size());
        return activeInstances.get(index);
    }
}
