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

public class ApplicationReject extends ListenerAdapter {
    private final LDBot plugin;
    private static final Color REJECT_COLOR = Color.decode("#e60000");

    public ApplicationReject(LDBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().equals(Application.getButtonRejectId())) {
            String rejectChannelId = plugin.getConfig().getString("applications.rejectChannelId");
            if (rejectChannelId == null || rejectChannelId.isEmpty()) {
                return;
            }

            String id;
            try {
                id = plugin.getDataBase().getApplicationUserId(event.getMessageId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            TextChannel textChannel = plugin.getJda().getTextChannelById(rejectChannelId);
            if (textChannel != null) {
                sendMessageAndEmbed(event, id, textChannel);
            } else {
                Bukkit.getLogger().warning("It's impossible to send the reject notification because the channel doesn't exist.");
            }

            manageRoles(event, id);

            event.getMessage().delete().queue();
            Bukkit.getLogger().info("[LDBot] The application from " + event.getGuild().getMemberById(id).getEffectiveName() + " has been successfully rejected.");
        }
    }

    private void sendMessageAndEmbed(ButtonInteractionEvent event, String id, TextChannel textChannel) {
        EmbedBuilder rejectEmbed = new EmbedBuilder()
                .setColor(REJECT_COLOR)
                .setTitle("⛔ Отказано")
                .setTimestamp(Instant.now());

        textChannel.sendMessage(event.getGuild().getMemberById(id).getAsMention()).setEmbeds(rejectEmbed.build()).queue();
    }
    

    private void manageRoles(ButtonInteractionEvent event, String id) {
        plugin.getConfig().getStringList("applications.addRejectRolesIds").forEach(roleId -> {
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

        plugin.getConfig().getStringList("applications.removeRejectRolesIds").forEach(roleId -> {
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
