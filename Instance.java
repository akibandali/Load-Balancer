package com.iptiq.loadbalancer;

public class Instance {
    private String id;
    private String status;
    private boolean active;

    public Instance(String id, String status, boolean active) {
        this.id = id;
        this.status = status;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
