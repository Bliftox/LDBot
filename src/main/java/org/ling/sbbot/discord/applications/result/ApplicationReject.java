package org.ling.sbbot.discord.applications.result;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.discord.applications.Application;
import org.ling.sbbot.main.SBBot;

import java.awt.*;
import java.time.Instant;

public class ApplicationReject extends ListenerAdapter {
    private final SBBot plugin;

    public ApplicationReject(SBBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getLabel().equals(Application.getButtonRejectLabel())) {
            String id = String.valueOf(Long.valueOf(event.getButton().getId()) - 1);


            TextChannel textChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("applications.rejectChannelId"));

            EmbedBuilder rejectEmbed = new EmbedBuilder()
                    .setColor(Color.decode("#ff0000"))

                    .setTitle("⛔ Отклонено")

                    .setTimestamp(Instant.now());

            textChannel.sendMessage("<@" + id + ">").setEmbeds(rejectEmbed.build()).queue();

            //event.reply(event.getMessage().getEmbeds().get(0).toString()).setEphemeral(true).queue();
            //plugin.getServer().getWhitelistedPlayers().
            //Bukkit.getOfflinePlayer().setWhitelisted(true);
        }
    }
}
