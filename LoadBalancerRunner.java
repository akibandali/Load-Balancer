package com.iptiq.loadbalancer;

import java.util.List;
import java.util.stream.IntStream;

import static com.iptiq.loadbalancer.LoadBalancerService.doInstanceRegistration;

public class LoadBalancerRunner {
    private static final Integer REQUEST_LIMIt = 2;
    private static int requestCounter = 0;

    public static void main (String[] args) {
        // Registering 10 instances for testing.
        IntStream.range(0, 10).parallel().forEach(c -> {
            doInstanceRegistration();
        });

        System.out.println(String.format("All Registered Instances %s", LoadBalancerService.getActiveInstanceIds()));

        // -->>Use invocation for testing
        // -->> Increase /Decrease the no_of_request- for testing the capacity limit
        testInvocation(10,"Random");
        testInvocation(5, "RoundRobin");

        // Test exclude of provider, I assumed we have the id of node and removing based on id.
        // Removing first active node and doing the invocation again
        List<String> activeInstanceIds = LoadBalancerService.getActiveInstanceIds();

        if (activeInstanceIds.size() > 0) {
            excludeNewProvider(activeInstanceIds.get(0));
            testInvocation(5, "Random");
        }

        // test include new node
         includeNewProvider();
        testInvocation(5, "RoundRobin");
    }

    public static String get (LoadBalancer balancer) {
        String instance="";
        int activeInstanceCount = LoadBalancerService.getActiveInstanceIds().size();
        int capacity = activeInstanceCount * REQUEST_LIMIt;
        if (requestCounter >= capacity) {
            System.out.println(requestCounter);
            throw new RuntimeException("Load Balancer is occupied and can not accept more request. Sorry for inconvenience");
        }
        requestCounter++;
       instance= balancer.getProviderInstance();
       requestCounter--;
      return instance;
    }

    private static void doRandomInvocation () {
        String instance = get(new RandomLoadBalancer());
        System.out.println(String.format("Random invocation of instance->>> %s", instance));
    }

    public synchronized static void doRoundRobinInvocation () {
        String instance = get(new RoundRobinLoadBalancer());
        System.out.println(String.format("Round Robin invocation of instance->>> %s", instance));
    }

    private synchronized static void includeNewProvider () {
        doInstanceRegistration();
    }

    private synchronized static void excludeNewProvider (String id) {
        LoadBalancerService.excludeActiveProviderById(id);
    }

    public static void testInvocation (int no_of_request, String technique) {

        if ("Random".equals(technique)) {
            IntStream.range(0, no_of_request).parallel().forEach(c -> {
                doRandomInvocation();
            });
        }
        else {
            IntStream.range(0, no_of_request).parallel().forEach(c -> {
                doRoundRobinInvocation();
            });
            doRoundRobinInvocation();
        }
    }
}
