package com.iptiq.loadbalancer;

import java.util.stream.IntStream;

import static com.iptiq.loadbalancer.LoadBalancerRunner.testInvocation;
import static com.iptiq.loadbalancer.LoadBalancerService.*;

public class HeartBeatChecker {
    private final static long INTERVAL = 10000;

    public static void main (String[] args) {

        // Registering 10 instances for testing.
        IntStream.range(0, 10).parallel().forEach(c -> {
            doInstanceRegistration();
        });
        System.out.println(String.format("All Registered Instances %s", LoadBalancerService.getActiveInstanceIds()));

        // making one instance randomly inactive and performing health check
        makeInstanceInactiveRandomly();
        testInvocation(5, "Random");
        // Health check
        check();

    }

    public static void check () {

        while (true) {
            checkAndRemoveInactiveProviders();
            // Please uncomment/comment in order to make instance active again and add back
            makeInstanceActiveAgain();
            checkAndAddBackActiveProviders();
            try {
                System.out.println();
                System.out.println(String.format("sleeping for %s secs", INTERVAL / 1000));
                Thread.sleep(INTERVAL);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
