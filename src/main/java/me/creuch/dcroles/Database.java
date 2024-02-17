package me.creuch.dcroles;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Database {

    final DCRoles instance;
    YamlConfiguration config;
    HikariDataSource dataSource;

    public Database(DCRoles instance) {
        this.instance = instance;
        config = instance.getYamlConfigClass().getConfigList().get("config.yml");
        initializeDataSource();
    }

    private void initializeDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        if(config.getString("database.type").equalsIgnoreCase("mysql")) {
            hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("database.url"));
            hikariConfig.setUsername(config.getString("database.username"));
            hikariConfig.setPassword(config.getString("database.password"));
        } else {
            if(!new File(instance.getDataFolder(), "DCRoles.db").exists()) {
                try {
                    new File(instance.getDataFolder(), "DCRoles.db").createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            hikariConfig.setDriverClassName("org.sqlite.JDBC");
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + instance.getDataFolder().toString().replace("\\", "/") + "/DCRoles.db");
        }
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setMaxLifetime(60000); // 60 Sec
        hikariConfig.setIdleTimeout(45000); // 45 Sec
        hikariConfig.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void createDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS userData(username varchar(16), role varchar(16), code varchar(16), used boolean);";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
