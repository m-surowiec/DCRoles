package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@FieldDefaults(level= AccessLevel.PRIVATE)
public class MyPlayer {

    OfflinePlayer p;
    DCRoles instance;

    public MyPlayer(final OfflinePlayer p, final DCRoles instance) {
        this.instance = instance;
        this.p = p;
    }

    public Boolean exists() {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT * FROM userData WHERE username = '%s'", p.getName()));
            boolean value = profile.getString(3) != null;
            conn.close();
            return value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCode() {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT * FROM userData WHERE username = '%s'", p.getName()));
            String value = profile.getString("code");
            conn.close();
            return value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRole() {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT * FROM userData WHERE username = '%s'", p.getName()));
            String value = profile.getString("role");
            conn.close();
            return value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean hasUsed() {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT * FROM userData WHERE username = '%s'", p.getName()));
            boolean value = profile.getBoolean("used");
            conn.close();
            return value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(String role, String code) {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            Integer sql = conn.createStatement().executeUpdate(String.format("INSERT INTO userData(username, role, code, used) VALUES('%s', '%s', '%s', false)", p.getName(), role, code));
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCode(String value) {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            Integer sql = conn.createStatement().executeUpdate(String.format("UPDATE userData SET code = '%s' WHERE username = '%s'", value, p.getName()));
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRole(String value) {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            Integer sql = conn.createStatement().executeUpdate(String.format("UPDATE userData SET role = '%s' WHERE username = '%s'", value, p.getName()));
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsed(boolean value) {
        try {
            Connection conn = instance.getDatabaseClass().getConnection();
            Integer sql = conn.createStatement().executeUpdate(String.format("UPDATE userData SET used = %s WHERE username = '%s'", value, p.getName()));
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
