package me.creuch.dcroles;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

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
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("database.url")); // Adjust if using SQLite
        hikariConfig.setUsername(config.getString("database.username"));
        hikariConfig.setPassword(config.getString("database.password"));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(5);

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
