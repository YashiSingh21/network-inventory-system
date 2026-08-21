package com.yashi.netinventory.dao;

import com.yashi.netinventory.model.MaintenanceLog;
import com.yashi.netinventory.util.AppException;
import com.yashi.netinventory.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object for maintenance_logs - the maintenance-tracking feature. */
public class MaintenanceLogDAO {

    public int addLog(MaintenanceLog log) throws AppException {
        String sql = "INSERT INTO maintenance_logs (device_id, description, performed_by) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, log.getDeviceId());
            ps.setString(2, log.getDescription());
            ps.setString(3, log.getPerformedBy());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new AppException("Could not log maintenance for device #" + log.getDeviceId() + ".", e);
        }
    }

    public List<MaintenanceLog> getLogsForDevice(int deviceId) throws AppException {
        String sql = "SELECT * FROM maintenance_logs WHERE device_id = ? ORDER BY performed_on DESC";
        List<MaintenanceLog> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deviceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new AppException("Could not load maintenance history for device #" + deviceId + ".", e);
        }
    }

    private MaintenanceLog mapRow(ResultSet rs) throws SQLException {
        MaintenanceLog log = new MaintenanceLog();
        log.setLogId(rs.getInt("log_id"));
        log.setDeviceId(rs.getInt("device_id"));
        log.setDescription(rs.getString("description"));
        log.setPerformedBy(rs.getString("performed_by"));
        log.setPerformedOn(rs.getTimestamp("performed_on"));
        return log;
    }
}
