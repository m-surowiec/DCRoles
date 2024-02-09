package me.creuch.dcroles.Discord;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.Database;
import me.creuch.dcroles.Message;
import me.creuch.dcroles.YamlConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bot {

    final DCRoles instance;
    final YamlConfig yamlConfig;
    final Database database;
    @Getter JDA jda;

    public Bot(DCRoles instance) {
        this.instance = instance;
        this.yamlConfig = instance.getYamlConfigClass();
        this.database = instance.getDatabaseClass();
    }

    public Bot loadBot() {
        try {
            jda = JDABuilder.createDefault(yamlConfig.getConfigList().get("config.yml").getString("bot.token"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                    .addEventListeners(new BotListener(instance))
                    .build().awaitReady();
        } catch(InvalidTokenException e) {
            new Message(instance, yamlConfig.getMessage(yamlConfig.getConfigList().get("lang.yml"), "bot.invalidToken")).colorize().send(Bukkit.getConsoleSender());
            instance.getPluginLoader().disablePlugin(instance);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

}
