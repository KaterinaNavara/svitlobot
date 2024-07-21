package com.navara;

public enum PowerStatus {
    ON("On"),
    OFF("Off");

    private final String status;

    PowerStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
