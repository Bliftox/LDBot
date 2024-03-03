package org.ling.ldbot.discord.applications.result;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.ling.ldbot.discord.applications.Application;
import org.ling.ldbot.main.LDBot;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

public class ApplicationAccept extends ListenerAdapter {
    private final LDBot plugin;
    private static final Color ACCEPT_COLOR = Color.decode("#00e600");

    public ApplicationAccept(LDBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().equals(Application.getButtonAcceptId())) {
            String acceptChannelId = plugin.getConfig().getString("applications.acceptChannelId");
            if (acceptChannelId == null || acceptChannelId.isEmpty()) {
                return;
            }

            String id;
            String nickname;
            try {
                id = plugin.getDataBase().getApplicationUserId(event.getMessageId());
                nickname = plugin.getDataBase().getFieldsValue(event.getMessageId()).get(0);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            TextChannel textChannel = plugin.getJda().getTextChannelById(acceptChannelId);
            if (textChannel != null) {
                sendMessageAndEmbed(event, id, textChannel);
            } else {
                Bukkit.getLogger().warning("It's impossible to send the accept notification because the channel doesn't exist.");
            }

            whitelistPlayer(nickname);
            changeNickname(event, id, nickname);
            manageRoles(event, id);

            event.getMessage().delete().queue();
            Bukkit.getLogger().info("[LDBot] The application from " + event.getGuild().getMemberById(id).getEffectiveName() + " has been successfully accepted.");
        }
    }

    private void sendMessageAndEmbed(ButtonInteractionEvent event, String id, TextChannel textChannel) {
        EmbedBuilder acceptEmbed = new EmbedBuilder()
                .setColor(ACCEPT_COLOR)
                .setTitle("✅ Принят")
                .setTimestamp(Instant.now());

        textChannel.sendMessage(event.getGuild().getMemberById(id).getAsMention()).setEmbeds(acceptEmbed.build()).queue();
    }

    private void whitelistPlayer(String nickname) {
        Bukkit.getServer().getScheduler().callSyncMethod(plugin.getInstance(), () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + nickname));
    }

    private void changeNickname(ButtonInteractionEvent event, String id, String nickname) {
        if (!plugin.getConfig().getBoolean("applications.changeNickname")) {
            try {
                event.getGuild().getMemberById(id).modifyNickname(nickname).queue();
            } catch (HierarchyException e) {
                Bukkit.getLogger().warning("Cannot change user role higher than bot");
            }
        }
    }

    private void manageRoles(ButtonInteractionEvent event, String id) {
        plugin.getConfig().getStringList("applications.addAcceptRolesIds").forEach(roleId -> {
            if (roleId != null && !roleId.isEmpty()) {
                try {
                    event.getGuild().addRoleToMember(UserSnowflake.fromId(id), event.getGuild().getRoleById(roleId)).queue();
                } catch (HierarchyException e) {
                    Bukkit.getLogger().warning("Cannot change user role higher than bot");
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Role does not exist");
                } catch (NullPointerException e) {
                    Bukkit.getLogger().warning("Cannot change user role higher than bot");
                }
            }
        });

        plugin.getConfig().getStringList("applications.removeAcceptRolesIds").forEach(roleId -> {
            if (roleId != null && !roleId.isEmpty()) {
                try {
                    event.getGuild().removeRoleFromMember(UserSnowflake.fromId(id), event.getGuild().getRoleById(roleId)).queue();
                } catch (HierarchyException e) {
                    Bukkit.getLogger().warning("Cannot change user role higher than bot");
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Role does not exist");
                } catch (NullPointerException e) {
                    Bukkit.getLogger().warning("Cannot change user role higher than bot");
                }
            }
        });
    }
}
