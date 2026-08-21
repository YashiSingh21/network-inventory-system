package com.yashi.netinventory.service;

import com.yashi.netinventory.dao.DeviceDAO;
import com.yashi.netinventory.dao.MaintenanceLogDAO;
import com.yashi.netinventory.model.Device;
import com.yashi.netinventory.model.MaintenanceLog;
import com.yashi.netinventory.util.AppException;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Business logic for the device inventory: validation rules live here so the
 * DAO stays a pure data-access layer and the UI stays a pure display layer.
 */
public class DeviceService {

    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)$");

    private final DeviceDAO deviceDAO = new DeviceDAO();
    private final MaintenanceLogDAO logDAO = new MaintenanceLogDAO();

    public Device registerDevice(String name, Device.DeviceType type, String ip, String location,
                                  String vendor, Date installedOn) throws AppException {
        if (name == null || name.trim().isEmpty()) {
            throw new AppException("Device name is required.");
        }
        if (!IPV4_PATTERN.matcher(ip).matches()) {
            throw new AppException("'" + ip + "' is not a valid IPv4 address.");
        }

        Device d = new Device(name, type, ip, location, Device.Status.UP, vendor, installedOn);
        int id = deviceDAO.addDevice(d);
        d.setDeviceId(id);
        return d;
    }

    public List<Device> listAll() throws AppException {
        return deviceDAO.getAllDevices();
    }

    public Device getById(int id) throws AppException {
        Device d = deviceDAO.getById(id);
        if (d == null) {
            throw new AppException("No device found with id " + id + ".");
        }
        return d;
    }

    public List<Device> search(String keyword) throws AppException {
        return deviceDAO.searchByNameOrIp(keyword);
    }

    public List<Device> filterByStatus(Device.Status status) throws AppException {
        return deviceDAO.filterByStatus(status);
    }

    public void updateDevice(Device d) throws AppException {
        if (!IPV4_PATTERN.matcher(d.getIpAddress()).matches()) {
            throw new AppException("'" + d.getIpAddress() + "' is not a valid IPv4 address.");
        }
        boolean updated = deviceDAO.updateDevice(d);
        if (!updated) {
            throw new AppException("No device found with id " + d.getDeviceId() + ".");
        }
    }

    /** Status monitoring: flip a device's status and automatically log the change. */
    public void setStatus(int deviceId, Device.Status newStatus, String changedBy) throws AppException {
        boolean updated = deviceDAO.updateStatus(deviceId, newStatus);
        if (!updated) {
            throw new AppException("No device found with id " + deviceId + ".");
        }
        logDAO.addLog(new MaintenanceLog(deviceId, "Status changed to " + newStatus, changedBy));
    }

    public void deleteDevice(int deviceId) throws AppException {
        boolean deleted = deviceDAO.deleteDevice(deviceId);
        if (!deleted) {
            throw new AppException("No device found with id " + deviceId + ".");
        }
    }

    public void logMaintenance(int deviceId, String description, String performedBy) throws AppException {
        // Confirm the device exists first, so we never log against a bad id.
        getById(deviceId);
        logDAO.addLog(new MaintenanceLog(deviceId, description, performedBy));
    }

    public List<MaintenanceLog> maintenanceHistory(int deviceId) throws AppException {
        return logDAO.getLogsForDevice(deviceId);
    }

    public Map<String, Integer> statusSummary() throws AppException {
        return deviceDAO.countByStatus();
    }
}
