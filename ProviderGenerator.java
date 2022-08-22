package com.iptiq.loadbalancer;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ProviderGenerator {
    private static final AtomicInteger instanceId = new AtomicInteger(0);

    public static synchronized Instance getUniqueProvider() {
        String id = UUID.randomUUID() + "-" + instanceId.getAndIncrement();
        return new Instance(id, "", true);
    }
}
