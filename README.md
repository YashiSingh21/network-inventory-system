# Network Device Inventory & Monitoring System

A Java console application to centrally manage routers, switches, firewalls, and other network
devices — with inventory tracking, status monitoring, and maintenance history — backed by MySQL.

## Features
- Register devices (router, switch, firewall, access point, server, other) with IPv4 validation
- View, search (by name/IP), and filter devices by status
- Status monitoring: flip a device between UP / DOWN / MAINTENANCE, auto-logged
- Maintenance tracking: log activity per device and view its full history
- Status summary dashboard (counts by UP/DOWN/MAINTENANCE)
- Layered architecture: `model` → `dao` → `service` → `ui`, with custom exception handling throughout

## Tech Stack
Java (JDK 17+) · MySQL 8 · JDBC · OOP

## Project Structure
```
NetworkInventorySystem/
├── src/com/yashi/netinventory/
│   ├── model/        # Device (with DeviceType/Status enums), MaintenanceLog
│   ├── dao/           # DeviceDAO, MaintenanceLogDAO — all raw SQL lives here
│   ├── service/        # DeviceService — validation & business rules
│   ├── util/           # DBConnection, AppException
│   ├── ui/              # ConsoleUI — menu-driven interface
│   └── Main.java
├── sql/schema.sql       # run this first to create the database & tables
└── README.md
```

## Setup

1. **Create the database**
   ```bash
   mysql -u root -p < sql/schema.sql
   ```

2. **Create an app user** (or edit `DBConnection.java` to use your own credentials)
   ```sql
   CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'AppPass123!';
   GRANT ALL PRIVILEGES ON network_inventory_db.* TO 'appuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Download the MySQL Connector/J jar** and place it in a `lib/` folder:
   https://dev.mysql.com/downloads/connector/j/

4. **Compile**
   ```bash
   javac -cp "lib/mysql-connector-j-x.x.x.jar" -d bin $(find src -name "*.java")
   ```

5. **Run**
   ```bash
   java -cp "bin:lib/mysql-connector-j-x.x.x.jar" com.yashi.netinventory.Main
   ```
   (On Windows, use `;` instead of `:` in the classpath.)
