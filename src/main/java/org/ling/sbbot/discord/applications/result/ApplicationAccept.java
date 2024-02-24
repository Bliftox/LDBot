package org.ling.sbbot.discord.applications.result;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.discord.applications.Application;
import org.ling.sbbot.main.SBBot;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class ApplicationAccept extends ListenerAdapter {
    private final SBBot plugin;

    public ApplicationAccept(SBBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        // В кнопку передается айди игрока
        if (event.getButton().getLabel().equals(Application.getButtonAcceptLabel())) {
            final String id = event.getButton().getId();
            
           // event.reply(id).setEphemeral(true).queue();

            TextChannel textChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("applications.acceptChannelId"));

            EmbedBuilder acceptEmbed = new EmbedBuilder()
                    .setColor(Color.decode("#00e600"))
                    .setTitle("✅ Принят")
                    .setTimestamp(Instant.now());

            textChannel.sendMessage("<@" + id + ">").setEmbeds(acceptEmbed.build()).queue();





            /*Bukkit.getLogger().warning(
                    plugin.getJda().getUserById(id).getName()
                            + " " +
                            plugin.getJda().getUserById(event.getButton().getId())
                                    .getGlobalName());*/

            // Добавление в вайтлист
            plugin.getServer().getScheduler().callSyncMethod(SBBot.getInstance(), () -> Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "whitelist add " +
                            plugin.getJda().getUserById(
                                            event.getButton().getId())
                                    .getGlobalName()));




            // Смена имени игроку в дискорд на прежнее
            if (plugin.getJda().getUserById(id) != null) {
                String userDefaultName = plugin.getJda().getUserById(id).getName();
                event.getGuild().getMemberById(id).modifyNickname(userDefaultName).queue();
            } else {
                // Обработка случая, когда пользователь не найден
                Bukkit.getLogger().warning("User not found.");
            }


            try {
                if (plugin.getConfig().getStringList("applications.acceptRoles") != null || !plugin.getConfig().getStringList("applications.acceptRoles").isEmpty()) {
                    for (String roleId : plugin.getConfig().getStringList("applications.acceptRolesIds")) {
                        event.getGuild().addRoleToMember(UserSnowflake.fromId(id), event.getGuild().getRoleById(roleId)).queue();
                    }
                }
            } catch (HierarchyException e) {
                Bukkit.getLogger().warning("Cannot change user role higher than bot");
            }

            Bukkit.getLogger().info("[SBBot] The application from " + event.getGuild().getMemberById(id).getNickname() + "has been successfully accepted.");
        }
    }
}
