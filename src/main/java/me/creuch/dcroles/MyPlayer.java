package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyPlayer {

    OfflinePlayer p;
    DCRoles instance;

    Boolean exists, used;
    String role, code;

    public MyPlayer(final OfflinePlayer p, final DCRoles instance) {
        this.instance = instance;
        this.p = p;
    }

    public Boolean exists() {
        if(exists != null) return exists;
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT code FROM userData WHERE username = ?")) { // Prepare inside the method
            stmt.setString(1, p.getName());
            try (ResultSet profile = stmt.executeQuery()) {
                exists = profile.next();
                return profile.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public String getCode() {
        if(code != null) return code;
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT code FROM userData WHERE username = ?")) { // Prepare inside the method
            stmt.setString(1, p.getName());
            try (ResultSet profile = stmt.executeQuery()) {
                if (profile.next()) {
                    code = profile.getString("code");
                    return profile.getString("code");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRole() {
        if(role != null) return role;
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT role FROM userData WHERE username = ?")) { // Prepare inside the method
            stmt.setString(1, p.getName());
            try (ResultSet profile = stmt.executeQuery()) {
                if (profile.next()) {
                    role = profile.getString("role");
                    return profile.getString("role");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Boolean hasUsed() {
        if(used != null) return used;
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT used FROM userData WHERE username = ?")) { // Prepare inside the method
            stmt.setString(1, p.getName());
            try (ResultSet profile = stmt.executeQuery()) {
                if (profile.next()) {
                    used = profile.getBoolean("used");
                    return profile.getBoolean("used");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(String role, String code) {
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO userData(username, role, code, used) VALUES(?, ?, ?, ?)")) {
            stmt.setString(1, p.getName());
            stmt.setString(2, role);
            stmt.setString(3, code);
            stmt.setBoolean(4, false);
            stmt.executeUpdate();
            this.role = role;
            this.code = code;
            this.used = false;
            this.exists = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCode(String value) {
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE userData SET code = ? WHERE username = ?")) { // Prepare inside the method
            stmt.setString(1, value);
            stmt.setString(2, p.getName());
            stmt.executeUpdate();
            this.code = value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void setRole(String value) {
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE userData SET role = ? WHERE username = ?")) { // Prepare inside the method
            stmt.setString(1, value);
            stmt.setString(2, p.getName());
            stmt.executeUpdate();
            this.role = value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsed(Boolean value) {
        try (Connection conn = instance.getDatabaseClass().getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE userData SET used = ? WHERE username = ?")) { // Prepare inside the method
            stmt.setBoolean(1, value);
            stmt.setString(2, p.getName());
            stmt.executeUpdate();
            this.used = value;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
