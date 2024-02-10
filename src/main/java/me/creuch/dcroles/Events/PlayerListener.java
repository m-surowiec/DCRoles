package me.creuch.dcroles.Events;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Message;
import me.creuch.dcroles.MyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@FieldDefaults(level= AccessLevel.PRIVATE)
public class PlayerListener implements Listener {

    final DCRoles instance;

    public PlayerListener(DCRoles instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if(!e.getPlayer().isOnline()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                MyPlayer p = new MyPlayer(e.getPlayer(), instance);
                if(p.exists()) return;
                String code = instance.generateCode();
                p.createUser("default", code);
                Message msg = new Message(instance, instance.getYamlConfigClass().getMessage(instance.getYamlConfigClass().getConfigList().get("lang.yml"), "firstJoinPlayer")).getFormatted(e.getPlayer());
                msg.send(e.getPlayer());
            }
        }.runTaskAsynchronously(instance);
    }
}
