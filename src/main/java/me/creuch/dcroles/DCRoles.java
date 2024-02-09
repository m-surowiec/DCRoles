package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.Commands.GetCodeCommand;
import me.creuch.dcroles.Commands.ManageUserCommand;
import me.creuch.dcroles.Commands.ReloadCommand;
import me.creuch.dcroles.Discord.Bot;
import me.creuch.dcroles.Discord.BotListener;
import me.creuch.dcroles.Events.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.orphan.Orphans;

import java.util.Random;

@FieldDefaults(level= AccessLevel.PRIVATE)
public final class DCRoles extends JavaPlugin {

    DCRoles instance;
    @Getter @Setter YamlConfig yamlConfigClass;
    @Getter @Setter Database databaseClass;
    @Getter @Setter Boolean debug = false;
    @Getter Bot bot;

    @Getter @Setter String[] configFileNameList = new String[]{"config.yml", "lang.yml"};

    @Override
    public void onEnable() {
        new Message(this, new String[]{"[DCRoles] Włączanie &9&lDCRoles v1.0"}).colorize().send(Bukkit.getConsoleSender());
        instance = this;

        yamlConfigClass = new YamlConfig(instance);
        onEnableConfigs();

        debug = yamlConfigClass.getConfigList().get("config.yml").getBoolean("debug");

        databaseClass = new Database(instance);
        databaseClass.createDatabase();

        new PlayerListener(instance);

        BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        handler.register(Orphans.path(yamlConfigClass.getConfigList().get("config.yml").getString("commands.GETCODE.name")).handler(new GetCodeCommand(instance)));
        handler.register(Orphans.path(yamlConfigClass.getConfigList().get("config.yml").getString("commands.RELOAD.name")).handler(new ReloadCommand(instance)));
        handler.register(Orphans.path(yamlConfigClass.getConfigList().get("config.yml").getString("commands.MANAGEUSER.name")).handler(new ManageUserCommand(instance)));
        getCommand(yamlConfigClass.getConfigList().get("config.yml").getString("commands.MANAGEUSER.name")).setTabCompleter(new ManageUserCommand(instance));
        getCommand(yamlConfigClass.getConfigList().get("config.yml").getString("commands.GETCODE.name")).setTabCompleter(new GetCodeCommand(instance));
        registerPermissions();

        bot = new Bot(instance).loadBot();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PlaceHolderAPI(this).register();
        }

        new Message(this, new String[]{"[DCRoles] &9&lWłączono!"}).colorize().send(Bukkit.getConsoleSender());
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("[DCRoles] Wyłączanie...");
    }

    public void onEnableConfigs() {
        yamlConfigClass.loadConfigs(configFileNameList);
    }

    public String generateCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int randIdx = new Random().nextInt(alphabet.length());
            char randChar = alphabet.charAt(randIdx);
            b.append(randChar);
        }

        return b.toString();
    }

    public void registerPermissions() {
        YamlConfiguration config = yamlConfigClass.getConfigList().get("config.yml");
        for(String s : config.getConfigurationSection("commands").getKeys(false)) {
            if(config.getString("commands." + s + ".permission") != null) {
                Permission perm = new Permission(config.getString("commands." + s + ".permission"));
                Bukkit.getPluginManager().addPermission(perm);
            }
            if(config.getString("commands." + s + ".self.permission") != null) {
                Permission perm = new Permission(config.getString("commands." + s + ".self.permission"));
                Bukkit.getPluginManager().addPermission(perm);
            }
        }
    }

    public String replacePlaceholders(String s, OfflinePlayer p) {
        if (p != null) {
            MyPlayer mp = new MyPlayer(p, instance);
            if (mp.exists()) {
                s = s.replace("{ROLE}", mp.getRole());
                s = s.replace("{CODE}", mp.getCode());
                s = s.replace("{NICK}", p.getName());
                s = s.replace("{USED}", String.valueOf(mp.hasUsed()));
            }
        }
        return s;
    }
}
