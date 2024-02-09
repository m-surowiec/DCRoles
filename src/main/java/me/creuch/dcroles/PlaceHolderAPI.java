package me.creuch.dcroles;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderAPI extends PlaceholderExpansion {

    DCRoles instance;

    public PlaceHolderAPI(DCRoles instance) {
        this.instance = instance;
    }

    @Override
    public String getAuthor() {
        return "_Creuch"; //
    }

    @Override
    public String getIdentifier() {
        return "dcr"; //
    }

    @Override
    public String getVersion() {
        return "v1.0"; //
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        MyPlayer p = new MyPlayer(player, instance);
        if(!p.exists()) return "null";
        if (params.equalsIgnoreCase("role")) {
            return p.getRole();
        }

        if (params.equalsIgnoreCase("code")) {
            return p.getCode();
        }

        if(params.equalsIgnoreCase("used")) {
            return p.hasUsed().toString();
        }

        return null;
    }
}
