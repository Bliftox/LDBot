package org.ling.sbbot.discord.globalchat;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.main.SBBot;

public class GlobalChat extends ListenerAdapter implements Listener {
    private final SBBot plugin;

    public GlobalChat(SBBot plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onMessageReceived(PlayerJoinEvent event) {
        TextChannel globalChannel = SBBot.getInstance().getJda().getTextChannelById(SBBot.getInstance().getConfig().getString("globalChannelId"));
        if (globalChannel != null) {
            EmbedBuilder playerJoinEmbed = new EmbedBuilder()
                    .addField("", "[ ]", true)
                    .addField("", "Игрок " + event.getPlayer().getName() + " вошёл в игру", true);

            globalChannel.sendMessage("").setEmbeds(playerJoinEmbed.build()).queue();
        }
    }

}
