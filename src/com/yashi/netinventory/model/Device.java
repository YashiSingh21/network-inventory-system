package com.yashi.netinventory.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents one network device (router, switch, firewall, etc.) in the inventory.
 * Uses Object-Oriented Programming: encapsulated fields with getters/setters,
 * and two nested enums so device type and status can never be an invalid string.
 */
public class Device {

    public enum DeviceType {
        ROUTER, SWITCH, FIREWALL, ACCESS_POINT, SERVER, OTHER
    }

    public enum Status {
        UP, DOWN, MAINTENANCE
    }

    private int deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private String ipAddress;
    private String location;
    private Status status;
    private String vendor;
    private Date installedOn;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Device() {
    }

    public Device(String deviceName, DeviceType deviceType, String ipAddress,
                  String location, Status status, String vendor, Date installedOn) {
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.ipAddress = ipAddress;
        this.location = location;
        this.status = status;
        this.vendor = vendor;
        this.installedOn = installedOn;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Date getInstalledOn() {
        return installedOn;
    }

    public void setInstalledOn(Date installedOn) {
        this.installedOn = installedOn;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("[#%d] %-18s %-12s %-15s %-10s status=%s vendor=%s",
                deviceId, deviceName, deviceType, ipAddress, location, status, vendor);
    }
}
