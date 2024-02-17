package me.creuch.dcroles.Commands;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.orphan.OrphanCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManageUserCommand implements OrphanCommand, TabCompleter {

    final DCRoles instance;
    final YamlConfig yamlConfigClass;

    public ManageUserCommand(DCRoles instance) {
        this.instance = instance;
        this.yamlConfigClass = instance.getYamlConfigClass();
    }

    @Description("Manage user's profile")
    @DefaultFor("~")
    public void command(
            BukkitCommandActor sender,
            @Optional String type,
            @Optional String player,
            @Optional String role
    ) {
        YamlConfiguration config = yamlConfigClass.getConfigList().get("config.yml"), lang = yamlConfigClass.getConfigList().get("lang.yml");
        if (!sender.getSender().hasPermission("commands.MANAGEUSER.permission")) {
            new Message(instance, yamlConfigClass.getMessage(lang, "noPermission")).send(sender.getSender());
            return;
        }
        if (type == null) {
            new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.noInput")).getFormatted(null).send(sender.getSender());
        } else {
            if (player == null) {
                new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.noPlayerInput")).getFormatted(null).send(sender.getSender());
                return;
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(player);
            MyPlayer p = new MyPlayer(op, instance);
            if (!p.exists()) {
                new Message(instance, yamlConfigClass.getMessage(lang, "playerDoesNotExist")).getFormatted(null).send(sender.getSender());
                return;
            }
            if (type.equalsIgnoreCase("resetCode")) {
                String code = instance.generateCode();
                new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.resetCodeSuccess")).getFormatted(op).send(sender.getSender());
                p.setCode(code);
                p.setUsage(false);
            } else if (type.equalsIgnoreCase("resetUsage")) {
                new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.resetUsageSuccess")).getFormatted(op).send(sender.getSender());
                p.setUsage(false);
            } else if (type.equalsIgnoreCase("viewProfile")) {
                new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.viewProfile")).getFormatted(op).send(sender.getSender());
            } else if (type.equalsIgnoreCase("setRole")) {
                if (role == null) {
                    new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.noRoleInput")).getFormatted(op).send(sender.getSender());
                    return;
                }
                if (!new ArrayList<>(config.getConfigurationSection("roles").getKeys(false)).contains(role) && !role.equalsIgnoreCase("default")) {
                    new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.invalidRoleInput")).getFormatted(op).send(sender.getSender());
                    return;
                }
                p.setRole(role);
                p.setUsage(false);
                new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.setRoleSuccess")).getFormatted(op).send(sender.getSender());
            } else {
                new Message(instance, yamlConfigClass.getMessage(lang, "commands.MANAGEUSER.invalidType")).getFormatted(op).send(sender.getSender());
            }
        }
        return;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        YamlConfiguration config = yamlConfigClass.getConfigList().get("config.yml");
        if (!sender.hasPermission(config.getString("commands.MANAGEUSER.permission"))) return null;
        if (strings.length == 1) {
            String[] args = new String[]{"resetCode", "resetUsage", "viewProfile", "setRole"};
            return List.of(args);
        } else if (strings.length == 2) {
            List<Player> players = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(p -> p.getName().startsWith(strings[1]))
                    .collect(Collectors.toList());
            List<String> names = new ArrayList<>();
            for (Player p : players) {
                names.add(p.getName());
            }
            return names;
        } else if (strings.length == 3 && strings[0].equalsIgnoreCase("setRole")) {
            List<String> args = new ArrayList<>();
            args.add("default");
            args.addAll(config.getConfigurationSection("roles").getKeys(false));
            return args;
        }
        return null;
    }
}
