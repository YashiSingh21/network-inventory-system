package com.yashi.netinventory.model;

import java.sql.Timestamp;

/**
 * Represents one maintenance event (repair, inspection, firmware upgrade)
 * performed on a device - used for maintenance tracking / audit history.
 */
public class MaintenanceLog {
    private int logId;
    private int deviceId;
    private String description;
    private String performedBy;
    private Timestamp performedOn;

    public MaintenanceLog() {
    }

    public MaintenanceLog(int deviceId, String description, String performedBy) {
        this.deviceId = deviceId;
        this.description = description;
        this.performedBy = performedBy;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public Timestamp getPerformedOn() {
        return performedOn;
    }

    public void setPerformedOn(Timestamp performedOn) {
        this.performedOn = performedOn;
    }

    @Override
    public String toString() {
        return String.format("[log #%d] device=%d  \"%s\" by %s on %s",
                logId, deviceId, description, performedBy, performedOn);
    }
}
