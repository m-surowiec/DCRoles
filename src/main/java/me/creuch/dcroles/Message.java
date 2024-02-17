package me.creuch.dcroles;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {

    final DCRoles instance;
    YamlConfiguration lang;

    String[] s;
    Component[] msg;

    private static final HashMap<Character, String> colorMap = new HashMap<>();

    static {
        colorMap.put('0', "<black>");
        colorMap.put('1', "<dark_blue>");
        colorMap.put('2', "<dark_green>");
        colorMap.put('3', "<dark_aqua>");
        colorMap.put('4', "<dark_red>");
        colorMap.put('5', "<dark_purple>");
        colorMap.put('6', "<gold>");
        colorMap.put('7', "<gray>");
        colorMap.put('8', "<dark_gray>");
        colorMap.put('9', "<blue>");
        colorMap.put('a', "<green>");
        colorMap.put('b', "<aqua>");
        colorMap.put('c', "<red>");
        colorMap.put('d', "<light_purple>");
        colorMap.put('e', "<yellow>");
        colorMap.put('f', "<white>");
        colorMap.put('k', "<obfuscated>");
        colorMap.put('l', "<b>");
        colorMap.put('m', "<strikethrough>");
        colorMap.put('n', "<u>");
        colorMap.put('o', "<i>");
        colorMap.put('r', "<reset>");
    }

    public Message(DCRoles instance, String[] msg) {
        this.instance = instance;
        if (instance.getYamlConfigClass() != null && !instance.getYamlConfigClass().getConfigList().isEmpty()) {
            YamlConfig yamlConfigClass = instance.getYamlConfigClass();
            this.lang = yamlConfigClass.getConfigList().get("lang.yml");
        }
        this.s = msg;
        this.msg = new Component[msg.length];
    }

    public String toString() {
        StringBuilder s = new StringBuilder(MiniMessage.miniMessage().serialize(msg[0]));
        for (int i = 1; i < msg.length; i++) {
            s.append(", ").append(MiniMessage.miniMessage().serialize(msg[i]));
        }
        return s.toString();
    }

    public Message getFormatted(OfflinePlayer p) {
        lang = instance.getYamlConfigClass().getConfigList().get("lang.yml");
        for (int i = 0; i < msg.length; i++) {
            s[i] = s[i].replace("{P}", lang.getString("prefix"));
            s[i] = translate(s[i]);
            msg[i] = MiniMessage.miniMessage().deserialize(s[i]);
        }
        if (p != null) {
            MyPlayer mp = new MyPlayer(p, instance);
            if (mp.exists()) {
                for (int i = 0; i < msg.length; i++) {
                    s[i] = s[i].replace("{ROLE}", mp.getRole());
                    s[i] = s[i].replace("{CODE}", mp.getCode());
                    s[i] = s[i].replace("{NICK}", p.getName());
                    s[i] = s[i].replace("{USED}", String.valueOf(mp.hasUsed()));
                    s[i] = translate(s[i]);
                    msg[i] = MiniMessage.miniMessage().deserialize(s[i]);
                }
            }
        }
        return this;
    }

    public Message getFormattedP(@NotNull MyPlayer p) {
        lang = instance.getYamlConfigClass().getConfigList().get("lang.yml");
        for (int i = 0; i < msg.length; i++) {
            s[i] = s[i].replace("{P}", lang.getString("prefix"));
            s[i] = translate(s[i]);
            msg[i] = MiniMessage.miniMessage().deserialize(s[i]);
        }
        if (p.exists()) {
            for (int i = 0; i < msg.length; i++) {
                s[i] = s[i].replace("{ROLE}", p.getRole());
                s[i] = s[i].replace("{CODE}", p.getCode());
                s[i] = s[i].replace("{NICK}", p.getP().getName());
                s[i] = s[i].replace("{USED}", String.valueOf(p.hasUsed()));
                s[i] = translate(s[i]);
                msg[i] = MiniMessage.miniMessage().deserialize(s[i]);
            }
        }
        return this;
    }

    public Message colorize() {
        for (int i = 0; i < msg.length; i++) {
            s = translate(s);
            msg[i] = MiniMessage.miniMessage().deserialize(s[i]);
        }
        return this;
    }

    public void send(CommandSender p) {
        for (Component c : msg) {
            p.sendMessage(c);
        }
    }

    public void send(Player p) {
        for (Component c : msg) {
            p.sendMessage(c);
        }
    }

    public String translate(String legacyText) {
        for (Map.Entry<Character, String> entry : colorMap.entrySet()) {
            legacyText = legacyText.replace("&" + entry.getKey(), entry.getValue());
        }
        return legacyText;
    }

    public String[] translate(String[] legacyText) {

        for (int i = 0; i < legacyText.length; i++) {
            for (Map.Entry<Character, String> entry : colorMap.entrySet()) {
                legacyText[i] = legacyText[i].replace("&" + entry.getKey(), entry.getValue());
            }
        }
        return legacyText;
    }
}
