package com.yashi.netinventory.ui;

import com.yashi.netinventory.model.Device;
import com.yashi.netinventory.model.MaintenanceLog;
import com.yashi.netinventory.service.DeviceService;
import com.yashi.netinventory.util.AppException;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Text-based menu that drives the Network Device Inventory & Monitoring System.
 */
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private final DeviceService deviceService = new DeviceService();

    public void start() {
        System.out.println("=================================================");
        System.out.println("   NETWORK DEVICE INVENTORY & MONITORING SYSTEM");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": registerDevice(); break;
                    case "2": listAll(); break;
                    case "3": search(); break;
                    case "4": filterByStatus(); break;
                    case "5": updateStatus(); break;
                    case "6": logMaintenance(); break;
                    case "7": viewMaintenanceHistory(); break;
                    case "8": updateDevice(); break;
                    case "9": deleteDevice(); break;
                    case "10": statusSummary(); break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option, try again.");
                }
            } catch (AppException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--------------- MAIN MENU ---------------");
        System.out.println(" 1. Register new device");
        System.out.println(" 2. View all devices");
        System.out.println(" 3. Search device (name or IP)");
        System.out.println(" 4. Filter devices by status");
        System.out.println(" 5. Update device status (monitoring)");
        System.out.println(" 6. Log maintenance activity");
        System.out.println(" 7. View maintenance history");
        System.out.println(" 8. Update device details");
        System.out.println(" 9. Delete device");
        System.out.println("10. Status summary (dashboard)");
        System.out.println(" 0. Exit");
        System.out.print("Choose an option: ");
    }

    private void registerDevice() throws AppException {
        System.out.print("Device name: ");
        String name = scanner.nextLine().trim();
        Device.DeviceType type = askDeviceType();
        System.out.print("IP address (IPv4): ");
        String ip = scanner.nextLine().trim();
        System.out.print("Location: ");
        String location = scanner.nextLine().trim();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().trim();
        System.out.print("Installed on (YYYY-MM-DD, blank = today): ");
        String dateStr = scanner.nextLine().trim();
        Date installed = dateStr.isEmpty() ? new Date(System.currentTimeMillis()) : Date.valueOf(dateStr);

        Device d = deviceService.registerDevice(name, type, ip, location, vendor, installed);
        System.out.println("Registered! " + d);
    }

    private Device.DeviceType askDeviceType() {
        System.out.println("Device type: 1.ROUTER 2.SWITCH 3.FIREWALL 4.ACCESS_POINT 5.SERVER 6.OTHER");
        System.out.print("Choose: ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "1": return Device.DeviceType.ROUTER;
            case "2": return Device.DeviceType.SWITCH;
            case "3": return Device.DeviceType.FIREWALL;
            case "4": return Device.DeviceType.ACCESS_POINT;
            case "5": return Device.DeviceType.SERVER;
            default: return Device.DeviceType.OTHER;
        }
    }

    private void listAll() throws AppException {
        printDevices(deviceService.listAll());
    }

    private void search() throws AppException {
        System.out.print("Search keyword (name or IP): ");
        String keyword = scanner.nextLine().trim();
        printDevices(deviceService.search(keyword));
    }

    private void filterByStatus() throws AppException {
        Device.Status status = askStatus();
        printDevices(deviceService.filterByStatus(status));
    }

    private Device.Status askStatus() {
        System.out.print("Status (1.UP 2.DOWN 3.MAINTENANCE): ");
        String c = scanner.nextLine().trim();
        switch (c) {
            case "2": return Device.Status.DOWN;
            case "3": return Device.Status.MAINTENANCE;
            default: return Device.Status.UP;
        }
    }

    private void updateStatus() throws AppException {
        System.out.print("Device ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Device.Status status = askStatus();
        System.out.print("Changed by (your name): ");
        String by = scanner.nextLine().trim();
        deviceService.setStatus(id, status, by);
        System.out.println("Status updated to " + status + ".");
    }

    private void logMaintenance() throws AppException {
        System.out.print("Device ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Description of work done: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Performed by: ");
        String by = scanner.nextLine().trim();
        deviceService.logMaintenance(id, desc, by);
        System.out.println("Maintenance logged.");
    }

    private void viewMaintenanceHistory() throws AppException {
        System.out.print("Device ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        List<MaintenanceLog> logs = deviceService.maintenanceHistory(id);
        if (logs.isEmpty()) {
            System.out.println("No maintenance history for device #" + id + ".");
        } else {
            logs.forEach(System.out::println);
        }
    }

    private void updateDevice() throws AppException {
        System.out.print("Device ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Device existing = deviceService.getById(id);

        System.out.print("New name (" + existing.getDeviceName() + "): ");
        String name = scanner.nextLine().trim();
        System.out.print("New IP (" + existing.getIpAddress() + "): ");
        String ip = scanner.nextLine().trim();
        System.out.print("New location (" + existing.getLocation() + "): ");
        String location = scanner.nextLine().trim();
        System.out.print("New vendor (" + existing.getVendor() + "): ");
        String vendor = scanner.nextLine().trim();

        existing.setDeviceName(name.isEmpty() ? existing.getDeviceName() : name);
        existing.setIpAddress(ip.isEmpty() ? existing.getIpAddress() : ip);
        existing.setLocation(location.isEmpty() ? existing.getLocation() : location);
        existing.setVendor(vendor.isEmpty() ? existing.getVendor() : vendor);

        deviceService.updateDevice(existing);
        System.out.println("Device updated.");
    }

    private void deleteDevice() throws AppException {
        System.out.print("Device ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        deviceService.deleteDevice(id);
        System.out.println("Device deleted.");
    }

    private void statusSummary() throws AppException {
        Map<String, Integer> summary = deviceService.statusSummary();
        if (summary.isEmpty()) {
            System.out.println("No devices registered yet.");
            return;
        }
        System.out.println("\n--- Status Summary ---");
        summary.forEach((status, count) -> System.out.println(status + ": " + count));
    }

    private void printDevices(List<Device> list) {
        if (list.isEmpty()) {
            System.out.println("No devices found.");
            return;
        }
        list.forEach(System.out::println);
    }
}
