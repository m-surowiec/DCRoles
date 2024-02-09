package me.creuch.dcroles.Commands;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.orphan.OrphanCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCodeCommand implements OrphanCommand, TabCompleter {

    final DCRoles instance;
    final YamlConfig yamlConfigClass;

    public GetCodeCommand(DCRoles instance) {
        this.instance = instance;
        this.yamlConfigClass = instance.getYamlConfigClass();
    }

    @Description("Get player's code")
    @DefaultFor("~")
    public void command(
            BukkitCommandActor sender,
            @Default("def") String target
    ) {
        YamlConfiguration config = yamlConfigClass.getConfigList().get("config.yml"), lang = yamlConfigClass.getConfigList().get("lang.yml");
        OfflinePlayer player = Bukkit.getOfflinePlayer(target);
        if (target.equals("def")) {
            if (sender.getAsPlayer() == null) {
                new Message(instance, yamlConfigClass.getMessage(lang, "executorIsNotHuman")).getFormatted(null).send(sender.getSender());
                return;
            }
            if (!sender.getAsPlayer().hasPermission(config.getString("commands.GETCODE.self.permission"))) {
                new Message(instance, yamlConfigClass.getMessage(lang, "noPermission")).getFormatted(sender.getAsPlayer()).send(sender.getAsPlayer());
                return;
            }
            MyPlayer p = new MyPlayer(sender.getAsPlayer(), instance);
            if (!p.exists()) {
                new Message(instance, yamlConfigClass.getMessage(lang, "playerDoesNotExist")).getFormatted(sender.getAsPlayer()).send(sender.getAsPlayer());
                return;
            }

            new Message(instance, yamlConfigClass.getMessage(lang, "commands.GETCODE.self")).getFormatted(sender.getAsPlayer()).send(sender.getAsPlayer());
        } else {
            if (!sender.getSender().hasPermission(config.getString("commands.GETCODE.permission"))) {
                new Message(instance, yamlConfigClass.getMessage(lang, "noPermission")).getFormatted(player).send(sender.getSender());
                return;
            }
            MyPlayer p = new MyPlayer(player, instance);
            if (!p.exists()) {
                new Message(instance, yamlConfigClass.getMessage(lang, "playerDoesNotExist")).getFormatted(player).send(sender.getSender());
                return;
            }

            new Message(instance, yamlConfigClass.getMessage(lang, "commands.GETCODE.other")).getFormatted(player).send(sender.getSender());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        YamlConfiguration config = yamlConfigClass.getConfigList().get("config.yml");
        if (!sender.hasPermission(config.getString("commands.GETCODE.permission"))) return null;
        if (strings.length == 1) {
            List<Player> players = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(p -> p.getName().startsWith(strings[0]))
                    .collect(Collectors.toList());
            List<String> names = new ArrayList<>();
            for (Player p : players) {
                names.add(p.getName());
            }
            return names;
        }
        return null;
    }
}
