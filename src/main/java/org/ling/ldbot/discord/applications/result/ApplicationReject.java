package org.ling.ldbot.discord.applications.result;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.ling.ldbot.discord.applications.Application;
import org.ling.ldbot.main.LDBot;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class ApplicationReject extends ListenerAdapter {
    private final LDBot plugin;

    public ApplicationReject(LDBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getLabel().equals(Application.getButtonRejectLabel())) {

            if (Objects.equals(plugin.getConfig().getString("applications.rejectChannelId"), null) || plugin.getConfig().getString("applications.rejectChannelId").isEmpty()) {
                Bukkit.getLogger().warning("The value for [applications.rejectChannelId] is incorrect.");
                return;
            }

            final String id = String.valueOf(Long.valueOf(event.getButton().getId()) - 1);
            TextChannel textChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("applications.rejectChannelId"));

            sendMessageAndEmbed(id, textChannel);

            changeNickname(event, id);
            manageRoles(event, id);

            //event.getMessage().delete().queue();
            Bukkit.getLogger().info("[LDBot] The application from " + event.getGuild().getMemberById(id).getEffectiveName() + " has been successfully rejected.");
        }
    }

    private void sendMessageAndEmbed(String id, TextChannel textChannel) {
        EmbedBuilder rejectEmbed = new EmbedBuilder()
                .setColor(Color.decode("#e60000"))
                .setTitle("⛔ Отказано")
                .setTimestamp(Instant.now());
        textChannel.sendMessage("<@" + id + ">").setEmbeds(rejectEmbed.build()).queue();
    }

    private void changeNickname(ButtonInteractionEvent event, String id) {
        String userDefaultName = plugin.getJda().getUserById(id).getName();
        try {
            event.getGuild().getMemberById(id).modifyNickname(userDefaultName).queue();
        } catch (HierarchyException e) {
            Bukkit.getLogger().warning("Cannot change user role higher than bot");
        }

    }

    private void manageRoles(ButtonInteractionEvent event, String id) {
        plugin.getConfig().getStringList("applications.addRejectRolesIds").forEach(roleId -> {
            if (roleId != null && !roleId.isEmpty()) {
                try {
                    event.getGuild().addRoleToMember(UserSnowflake.fromId(id), event.getGuild().getRoleById(roleId)).queue();
                } catch (HierarchyException e) {
                    Bukkit.getLogger().warning("Cannot change user role higher than bot");
                }
            } else {
                Bukkit.getLogger().warning("Role ID is null or empty. Skipping role assignment.");
            }
        });

        plugin.getConfig().getStringList("applications.removeRejectRolesIds").forEach(roleId -> {
            if (roleId != null && !roleId.isEmpty()) {
                try {
                    event.getGuild().removeRoleFromMember(UserSnowflake.fromId(id), event.getGuild().getRoleById(roleId)).queue();
                } catch (HierarchyException e) {
                    Bukkit.getLogger().warning("Cannot change user role higher than bot");
                }
            } else {
                Bukkit.getLogger().warning("Role ID is null or empty. Skipping role removal.");
            }
        });
    }
}
