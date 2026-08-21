package com.yashi.netinventory.dao;

import com.yashi.netinventory.model.Device;
import com.yashi.netinventory.util.AppException;
import com.yashi.netinventory.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the devices table. All raw SQL for devices lives
 * here, behind PreparedStatements, so the service/UI layers never build
 * SQL strings by hand (which is what keeps the app safe from SQL injection).
 */
public class DeviceDAO {

    public int addDevice(Device d) throws AppException {
        String sql = "INSERT INTO devices (device_name, device_type, ip_address, location, status, vendor, installed_on) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, d.getDeviceName());
            ps.setString(2, d.getDeviceType().name());
            ps.setString(3, d.getIpAddress());
            ps.setString(4, d.getLocation());
            ps.setString(5, d.getStatus().name());
            ps.setString(6, d.getVendor());
            ps.setDate(7, d.getInstalledOn());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new AppException("A device with IP '" + d.getIpAddress() + "' already exists.", dup);
        } catch (SQLException e) {
            throw new AppException("Could not add device '" + d.getDeviceName() + "'.", e);
        }
    }

    public List<Device> getAllDevices() throws AppException {
        String sql = "SELECT * FROM devices ORDER BY device_name";
        List<Device> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new AppException("Could not load device list.", e);
        }
    }

    public Device getById(int deviceId) throws AppException {
        String sql = "SELECT * FROM devices WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deviceId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new AppException("Could not fetch device #" + deviceId + ".", e);
        }
    }

    public List<Device> searchByNameOrIp(String keyword) throws AppException {
        String sql = "SELECT * FROM devices WHERE device_name LIKE ? OR ip_address LIKE ? ORDER BY device_name";
        List<Device> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new AppException("Search failed for '" + keyword + "'.", e);
        }
    }

    public List<Device> filterByStatus(Device.Status status) throws AppException {
        String sql = "SELECT * FROM devices WHERE status = ? ORDER BY device_name";
        List<Device> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new AppException("Could not filter devices by status " + status + ".", e);
        }
    }

    public boolean updateDevice(Device d) throws AppException {
        String sql = "UPDATE devices SET device_name=?, device_type=?, ip_address=?, location=?, " +
                "status=?, vendor=?, installed_on=? WHERE device_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getDeviceName());
            ps.setString(2, d.getDeviceType().name());
            ps.setString(3, d.getIpAddress());
            ps.setString(4, d.getLocation());
            ps.setString(5, d.getStatus().name());
            ps.setString(6, d.getVendor());
            ps.setDate(7, d.getInstalledOn());
            ps.setInt(8, d.getDeviceId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Could not update device #" + d.getDeviceId() + ".", e);
        }
    }

    /** Used by the "status monitoring" feature to flip a device UP/DOWN/MAINTENANCE quickly. */
    public boolean updateStatus(int deviceId, Device.Status newStatus) throws AppException {
        String sql = "UPDATE devices SET status = ? WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.name());
            ps.setInt(2, deviceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Could not update status for device #" + deviceId + ".", e);
        }
    }

    public boolean deleteDevice(int deviceId) throws AppException {
        String sql = "DELETE FROM devices WHERE device_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deviceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Could not delete device #" + deviceId + ".", e);
        }
    }

    /** Used for a quick dashboard-style count grouped by status (up/down/maintenance). */
    public java.util.Map<String, Integer> countByStatus() throws AppException {
        String sql = "SELECT status, COUNT(*) AS cnt FROM devices GROUP BY status";
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("cnt"));
            }
            return counts;
        } catch (SQLException e) {
            throw new AppException("Could not compute status summary.", e);
        }
    }

    private Device mapRow(ResultSet rs) throws SQLException {
        Device d = new Device();
        d.setDeviceId(rs.getInt("device_id"));
        d.setDeviceName(rs.getString("device_name"));
        d.setDeviceType(Device.DeviceType.valueOf(rs.getString("device_type")));
        d.setIpAddress(rs.getString("ip_address"));
        d.setLocation(rs.getString("location"));
        d.setStatus(Device.Status.valueOf(rs.getString("status")));
        d.setVendor(rs.getString("vendor"));
        d.setInstalledOn(rs.getDate("installed_on"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));
        return d;
    }
}
