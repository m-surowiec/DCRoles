package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

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
            Connection conn = Database.conn;
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT code FROM userData WHERE username = '%s'", p.getName()));
            return profile.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCode() {
        try {
            Connection conn = Database.conn;
            System.out.println("got conn " + Instant.now().getNano());
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT code FROM userData WHERE username = '%s'", p.getName()));
            System.out.println("got profile " + Instant.now().getNano());
            profile.next();
            System.out.println("got returning " + Instant.now().getNano());
            return profile.getString("code");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRole() {
        try {
            Connection conn = Database.conn;
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT role FROM userData WHERE username = '%s'", p.getName()));
            profile.next();
            return profile.getString("role");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean hasUsed() {
        try {
            Connection conn = Database.conn;
            ResultSet profile = conn.createStatement().executeQuery(String.format("SELECT used FROM userData WHERE username = '%s'", p.getName()));
            profile.next();
            return profile.getBoolean("used");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(String role, String code) {
        try {
            Connection conn = Database.conn;
            Integer sql = conn.createStatement().executeUpdate(String.format("INSERT INTO userData(username, role, code, used) VALUES('%s', '%s', '%s', false)", p.getName(), role, code));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCode(String value) {
        try {
            Connection conn = Database.conn;
            Integer sql = conn.createStatement().executeUpdate(String.format("UPDATE userData SET code = '%s' WHERE username = '%s'", value, p.getName()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRole(String value) {
        try {
            Connection conn = Database.conn;
            Integer sql = conn.createStatement().executeUpdate(String.format("UPDATE userData SET role = '%s' WHERE username = '%s'", value, p.getName()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsed(boolean value) {
        try {
            Connection conn = Database.conn;
            Integer sql = conn.createStatement().executeUpdate(String.format("UPDATE userData SET used = %s WHERE username = '%s'", value, p.getName()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
