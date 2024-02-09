package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Database {

    final DCRoles instance;
    YamlConfiguration config;
    public static Connection conn;

    public Database(DCRoles instance) {
        this.instance = instance;
        config = instance.getYamlConfigClass().getConfigList().get("config.yml");
    }

    public Connection getConnection() {
        if ("mysql".equalsIgnoreCase(config.getString("database.type"))) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://" + config.getString("database.url"), config.getString("database.username"), config.getString("database.password"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:" + instance.getDataFolder() + "/data.db");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        return conn;
    }

    public void createDatabase() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS userData(username varchar(16), role varchar(16), code varchar(16), used boolean);";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
