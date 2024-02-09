package me.creuch.dcroles.Commands;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.Message;
import me.creuch.dcroles.YamlConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.orphan.OrphanCommand;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReloadCommand implements OrphanCommand {

    final DCRoles instance;

    public ReloadCommand(DCRoles instance) {
        this.instance = instance;
    }

    @Description("Reload the plugin")
    @DefaultFor("~")
    public void command(
            BukkitCommandActor sender
    ) {
        YamlConfiguration config = instance.getYamlConfigClass().getConfigList().get("config.yml");
        YamlConfiguration lang   = instance.getYamlConfigClass().getConfigList().get("lang.yml");
        if(!sender.getSender().hasPermission("commands.RELOAD.permission")) { new Message(instance, instance.getYamlConfigClass().getMessage(lang, "noPermission")).send(sender.getSender()); return;}
        Message message = new Message(instance, instance.getYamlConfigClass().getMessage(lang, "commands.RELOAD.reloading")).getFormatted(null);
        message.send(sender.getSender());
        instance.setYamlConfigClass(new YamlConfig(instance));
        instance.onEnableConfigs();
        instance.setDebug(instance.getYamlConfigClass().getConfigList().get("config.yml").getBoolean("debug"));
        instance.setDatabaseClass(new Database(instance));
        instance.getDatabaseClass().createDatabase();
        message = new Message(instance, instance.getYamlConfigClass().getMessage(lang, "commands.RELOAD.reloadedSuccess")).getFormatted(null);
        message.send(sender.getSender());
    }

}
