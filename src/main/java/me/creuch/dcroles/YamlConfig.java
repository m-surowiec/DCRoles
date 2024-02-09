package me.creuch.dcroles;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

@FieldDefaults(level=AccessLevel.PRIVATE)
public class YamlConfig {

    final DCRoles instance;
    @Getter HashMap<String, YamlConfiguration> configList = new HashMap<>();

    public YamlConfig(DCRoles instance) {
        this.instance = instance;
    }

    public void loadConfigs(String[] fileList) {
        File f;
        YamlConfiguration config;
        for(String fName : fileList) {
            f = new File(instance.getDataFolder(), fName);
            config = YamlConfiguration.loadConfiguration(f);
            if(!f.exists() || config.getKeys(true).isEmpty()) {
                instance.saveResource(fName, true);
                Message message = new Message(instance, new String[]{"[DCRoles] Wygenerowano config:&9 " + fName}).colorize();
                message.send(Bukkit.getConsoleSender());
            }
            f = new File(instance.getDataFolder(), fName);
            config = YamlConfiguration.loadConfiguration(f);
            configList.put(fName, config);
        }
    }

    public String[] getMessage(YamlConfiguration config, String path) {
        config.getStringList(path);
        if(!config.getStringList(path).isEmpty()) {
            String[] msg = new String[config.getStringList(path).size()];
            for(int i = 0; i < config.getStringList(path).size(); i++) {
                msg[i] = config.getStringList(path).get(i);
            }
            return msg;
        } else {
            String[] msg = new String[1];
            msg[0] = config.getString(path);
            return msg;
        }
    }

}
