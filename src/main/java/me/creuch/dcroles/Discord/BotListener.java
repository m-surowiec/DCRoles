package me.creuch.dcroles.Discord;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.creuch.dcroles.DCRoles;
import me.creuch.dcroles.MyPlayer;
import me.creuch.dcroles.YamlConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotListener extends ListenerAdapter {

    final DCRoles instance;

    public BotListener(DCRoles instance) {
        this.instance = instance;
    }


    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        YamlConfiguration config = instance.getYamlConfigClass().getConfigList().get("config.yml");
        if(config.getString("bot.getType").equalsIgnoreCase("command") || config.getString("bot.getType").equalsIgnoreCase("both")) {
            OptionData nick = new OptionData(OptionType.STRING, config.getString("bot.command.commandOptions.NICK.name"), config.getString("bot.command.commandOptions.NICK.desc"), true);
            OptionData code = new OptionData(OptionType.STRING, config.getString("bot.command.commandOptions.CODE.name"), config.getString("bot.command.commandOptions.CODE.desc"), true);
            List<CommandData> data = new ArrayList<>();
            data.add(Commands.slash(config.getString("bot.command.name"), config.getString("bot.command.description")).addOptions(nick, code));
            event.getGuild().updateCommands().addCommands(data).queue();
        } if(config.getString("bot.getType").equalsIgnoreCase("form") || config.getString("bot.getType").equalsIgnoreCase("both")) {
            if(!config.getBoolean("bot.form.embedSent")) {
                TextChannel channel = event.getGuild().getTextChannelById(config.getString("bot.form.channelID"));
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(config.getString("bot.form.embed.title"));
                embedBuilder.setDescription(config.getString("bot.form.embed.desc"));
                embedBuilder.setColor(Color.decode(config.getString("bot.form.embed.color")));
                Button button = Button.primary(config.getString("bot.form.embed.buttonID"), config.getString("bot.form.embed.buttonText")).withEmoji(Emoji.fromFormatted(config.getString("bot.form.embed.buttonEmoji") + ""));
                ActionRow actionRow = ActionRow.of(button);
                MessageEmbed embed = embedBuilder.build();
                channel.sendMessageEmbeds(embed)
                        .setComponents(actionRow)
                        .queue((message) -> {
                            config.set("bot.form.embedSent", true);
                            config.set("bot.form.embedID", message.getId() + "");
                            try {
                                File f = new File(instance.getDataFolder(), "config.yml");
                                config.save(f);
                                instance.getYamlConfigClass().loadConfigs(instance.getConfigFileNameList());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } else {
                Message msg = event.getGuild().getTextChannelById(config.getString("bot.form.channelID")).retrieveMessageById(config.getString("bot.form.embedID")).complete();
                for(LayoutComponent c : msg.getComponents()) {
                    for(Button b : c.getButtons()) {
                        if(b.getId().equalsIgnoreCase(config.getString("bot.form.embed.buttonID"))) return;
                    }
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(config.getString("bot.form.embed.title"));
                embedBuilder.setDescription(config.getString("bot.form.embed.desc"));
                embedBuilder.setColor(Color.decode(config.getString("bot.form.embed.color")));
                Button button = Button.primary(config.getString("bot.form.embed.buttonID"), config.getString("bot.form.embed.buttonText")).withEmoji(Emoji.fromFormatted(config.getString("bot.form.embed.buttonEmoji") + ""));
                ActionRow actionRow = ActionRow.of(button);
                msg.getComponents().forEach(lc -> actionRow.getComponents().addAll(lc.getComponents()));
                MessageEmbed embed = embedBuilder.build();
                msg.editMessageEmbeds(embed).setComponents(actionRow).queue();
            }
        }

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        YamlConfiguration config = instance.getYamlConfigClass().getConfigList().get("config.yml");
        if(!event.getName().equalsIgnoreCase(config.getString("bot.command.name"))) return;

        User user = event.getUser();
        Member member = event.getGuild().getMember(user);
        if(member == null) return;
        String nickOption = event.getOption(config.getString("bot.command.commandOptions.NICK.name")).getAsString();
        String codeOption = event.getOption(config.getString("bot.command.commandOptions.CODE.name")).getAsString();
        OfflinePlayer p = Bukkit.getOfflinePlayer(nickOption);
        MyPlayer myPlayer = new MyPlayer(p, instance);
        YamlConfiguration lang = instance.getYamlConfigClass().getConfigList().get("lang.yml");
        boolean embed = lang.getBoolean("discord.embed");
        System.out.println("embed = " + embed);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if(embed) {
            embedBuilder.setTitle(instance.replacePlaceholders(lang.getString("discord.embedTitle"), p));
            embedBuilder.setColor(Color.decode(lang.getString("discord.embedColor")));
            embedBuilder.setDescription(instance.replacePlaceholders(config.getString("bot.logEmbed.desc"), p).replace("{DISCORD}", user.getName()));
        }
        if(!myPlayer.exists()) {
            if(embed) {
                embedBuilder.setDescription(lang.getString("discord.command.doesNotExist"));
                MessageEmbed embedMessage = embedBuilder.build();
                event.replyEmbeds(embedMessage).setEphemeral(true).queue();
            } else {
                event.reply(lang.getString("discord.command.doesNotExist")).setEphemeral(true).queue();
            }
            return;
        }
        if(!codeOption.equalsIgnoreCase(myPlayer.getCode())) {
            if(embed) {
                embedBuilder.setDescription(lang.getString("discord.command.invalidCode"));
                MessageEmbed embedMessage = embedBuilder.build();
                event.replyEmbeds(embedMessage).setEphemeral(true).queue();
            } else {
                event.reply(lang.getString("discord.command.invalidCode")).setEphemeral(true).queue();
            }
            return;
        }
        if(myPlayer.getRole().equalsIgnoreCase("default")) {
            if(embed) {
                embedBuilder.setDescription(lang.getString("discord.command.defaultRole"));
                MessageEmbed embedMessage = embedBuilder.build();
                event.replyEmbeds(embedMessage).setEphemeral(true).queue();
            } else {
                event.reply(lang.getString("discord.command.defaultRole")).setEphemeral(true).queue();
            }
            return;
        }
        if(myPlayer.hasUsed()) {
            if(embed) {
                embedBuilder.setDescription(lang.getString("discord.command.alreadyUsed"));
                MessageEmbed embedMessage = embedBuilder.build();
                event.replyEmbeds(embedMessage).setEphemeral(true).queue();
            } else {
                event.reply(lang.getString("discord.command.alreadyUsed")).setEphemeral(true).queue();
            }
            return;
        }
        myPlayer.setUsed(true);
        List<String> IDs = config.getStringList("roles." + myPlayer.getRole() + ".id");
        for(String roleID : IDs) {
            if(!member.getRoles().contains(event.getGuild().getRoleById(roleID))) {
                event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(roleID)).queue();
            }
        }
        if(embed) {
            embedBuilder.setDescription(lang.getString("discord.command.success").replace("{AMOUNT}", IDs.size() + "").replace("{ROLE}", myPlayer.getRole()));
            MessageEmbed embedMessage = embedBuilder.build();
            event.replyEmbeds(embedMessage).setEphemeral(true).queue();
        } else {
            event.reply(lang.getString("discord.command.success").replace("{AMOUNT}", IDs.size() + "").replace("{ROLE}", myPlayer.getRole())).setEphemeral(true).queue();
        }

        TextChannel channel = event.getGuild().getTextChannelById(config.getString("bot.logEmbed.channelID"));
        embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(instance.replacePlaceholders(config.getString("bot.logEmbed.title"), p));
        embedBuilder.setDescription(instance.replacePlaceholders(config.getString("bot.logEmbed.desc"), p).replace("{DISCORD}", user.getName()));
        embedBuilder.setColor(Color.decode(config.getString("bot.logEmbed.color")));
        MessageEmbed embedMessage = embedBuilder.build();
        channel.sendMessageEmbeds(embedMessage).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        YamlConfiguration config = instance.getYamlConfigClass().getConfigList().get("config.yml");
        if (event.getComponentId().equals(config.getString("bot.form.embed.buttonID"))) {
            TextInput nick = TextInput.create("nick", "Nick Minecraft", TextInputStyle.SHORT)
                    .setPlaceholder("Twój nick z minecraft")
                    .setMinLength(3)
                    .setMaxLength(16) // or setRequiredRange(10, 100)
                    .build();

            TextInput code = TextInput.create("code", "Twój Kod", TextInputStyle.SHORT)
                    .setPlaceholder("Kod z /dcgetcode")
                    .setMinLength(16)
                    .setMaxLength(16)
                    .build();

            Modal modal = Modal.create(config.getString("bot.form.formID"), config.getString("bot.form.formTitle"))
                    .addComponents(ActionRow.of(nick), ActionRow.of(code))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        YamlConfiguration config = instance.getYamlConfigClass().getConfigList().get("config.yml");
        if (event.getModalId().equals(config.getString("bot.form.formID"))) {
            String nickOption = event.getValue("nick").getAsString();
            String codeOption = event.getValue("code").getAsString();
            User user = event.getUser();
            Member member = event.getGuild().getMember(user);
            if(member == null) return;
            OfflinePlayer p = Bukkit.getOfflinePlayer(nickOption);
            MyPlayer myPlayer = new MyPlayer(p, instance);
            YamlConfiguration lang = instance.getYamlConfigClass().getConfigList().get("lang.yml");
            boolean embed = lang.getBoolean("discord.embed");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if(embed) {
                embedBuilder.setTitle(instance.replacePlaceholders(lang.getString("discord.embedTitle"), p));
                embedBuilder.setColor(Color.decode(lang.getString("discord.embedColor")));
                embedBuilder.setDescription(instance.replacePlaceholders(config.getString("bot.logEmbed.desc"), p).replace("{DISCORD}", user.getName()));
            }
            if(!myPlayer.exists()) {
                if(embed) {
                    embedBuilder.setDescription(lang.getString("discord.command.doesNotExist"));
                    MessageEmbed embedMessage = embedBuilder.build();
                    event.replyEmbeds(embedMessage).setEphemeral(true).queue();
                } else {
                    event.reply(lang.getString("discord.command.doesNotExist")).setEphemeral(true).queue();
                }
                return;
            }
            if(!codeOption.equalsIgnoreCase(myPlayer.getCode())) {
                if(embed) {
                    embedBuilder.setDescription(lang.getString("discord.command.invalidCode"));
                    MessageEmbed embedMessage = embedBuilder.build();
                    event.replyEmbeds(embedMessage).setEphemeral(true).queue();
                } else {
                    event.reply(lang.getString("discord.command.invalidCode")).setEphemeral(true).queue();
                }
                return;
            }
            if(myPlayer.getRole().equalsIgnoreCase("default")) {
                if(embed) {
                    embedBuilder.setDescription(lang.getString("discord.command.defaultRole"));
                    MessageEmbed embedMessage = embedBuilder.build();
                    event.replyEmbeds(embedMessage).setEphemeral(true).queue();
                } else {
                    event.reply(lang.getString("discord.command.defaultRole")).setEphemeral(true).queue();
                }
                return;
            }
            if(myPlayer.hasUsed()) {
                if(embed) {
                    embedBuilder.setDescription(lang.getString("discord.command.alreadyUsed"));
                    MessageEmbed embedMessage = embedBuilder.build();
                    event.replyEmbeds(embedMessage).setEphemeral(true).queue();
                } else {
                    event.reply(lang.getString("discord.command.alreadyUsed")).setEphemeral(true).queue();
                }
                return;
            }
            myPlayer.setUsed(true);
            List<String> IDs = config.getStringList("roles." + myPlayer.getRole() + ".id");
            for(String roleID : IDs) {
                if(!member.getRoles().contains(event.getGuild().getRoleById(roleID))) {
                    event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(roleID)).queue();
                }
            }
            if(embed) {
                embedBuilder.setDescription(lang.getString("discord.command.success").replace("{AMOUNT}", IDs.size() + "").replace("{ROLE}", myPlayer.getRole()));
                MessageEmbed embedMessage = embedBuilder.build();
                event.replyEmbeds(embedMessage).setEphemeral(true).queue();
            } else {
                event.reply(lang.getString("discord.command.success").replace("{AMOUNT}", IDs.size() + "").replace("{ROLE}", myPlayer.getRole())).setEphemeral(true).queue();
            }

            TextChannel channel = event.getGuild().getTextChannelById(config.getString("bot.logEmbed.channelID"));
            embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(instance.replacePlaceholders(config.getString("bot.logEmbed.title"), p));
            embedBuilder.setDescription(instance.replacePlaceholders(config.getString("bot.logEmbed.desc"), p).replace("{DISCORD}", user.getName()));
            embedBuilder.setColor(Color.decode(config.getString("bot.logEmbed.color")));
            MessageEmbed embedMessage = embedBuilder.build();
            channel.sendMessageEmbeds(embedMessage).queue();
        }
    }



}
