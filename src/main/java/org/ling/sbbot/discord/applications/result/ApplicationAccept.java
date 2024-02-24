package org.ling.sbbot.discord.applications.result;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.discord.applications.Application;
import org.ling.sbbot.main.SBBot;

import java.awt.*;
import java.time.Instant;

public class ApplicationAccept extends ListenerAdapter {
    private final SBBot plugin;

    public ApplicationAccept(SBBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getLabel().equals(Application.getButtonAcceptLabel())) {
            final String id = event.getButton().getId();
            TextChannel textChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("applications.acceptChannelId"));

            sendMessageAndEmbed(id, textChannel);

            whitelistPlayer(id);
            changeNickname(event, id);
            manageRoles(event, id);

            event.getMessage().delete().queue();
            Bukkit.getLogger().info("[SBBot] The application from " + event.getGuild().getMemberById(id).getEffectiveName() + " has been successfully accepted.");
        }
    }

    private void sendMessageAndEmbed(String id, TextChannel textChannel) {
        EmbedBuilder acceptEmbed = new EmbedBuilder()
                .setColor(Color.decode("#00e600"))
                .setTitle("✅ Принят")
                .setTimestamp(Instant.now());
        textChannel.sendMessage("<@" + id + ">").setEmbeds(acceptEmbed.build()).queue();
    }

    private void whitelistPlayer(String id) {
        Bukkit.getServer().getScheduler().callSyncMethod(SBBot.getInstance(), () -> Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "whitelist add " +
                        plugin.getJda().getUserById(id).getGlobalName()));
    }

    private void changeNickname(ButtonInteractionEvent event, String id) {
        if (!plugin.getConfig().getBoolean("applications.changeNickname")) {
            String userDefaultName = plugin.getJda().getUserById(id).getName();
            try {
                event.getGuild().getMemberById(id).modifyNickname(userDefaultName).queue();
            } catch (HierarchyException e) {
                Bukkit.getLogger().warning("Cannot change user role higher than bot");
            }
        }
    }

    private void manageRoles(ButtonInteractionEvent event, String id) {
        plugin.getConfig().getStringList("applications.addAcceptRolesIds").forEach(roleId -> {
            try {
                event.getGuild().addRoleToMember(UserSnowflake.fromId(id), event.getGuild().getRoleById(roleId)).queue();
            } catch (HierarchyException e) {
                Bukkit.getLogger().warning("Cannot change user role higher than bot");
            }
        });

        plugin.getConfig().getStringList("applications.removeAcceptRolesIds").forEach(roleId -> {
            try {
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(id), event.getGuild().getRoleById(roleId)).queue();
            } catch (HierarchyException e) {
                Bukkit.getLogger().warning("Cannot change user role higher than bot");
            }
        });
    }
}
