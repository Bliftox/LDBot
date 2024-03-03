package org.ling.ldbot.discord.suggests;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.ling.ldbot.main.LDBot;

import java.awt.*;

public class ThreadNotification extends ListenerAdapter {
    private final LDBot plugin;
    private final Suggest suggest;

    public ThreadNotification(LDBot plugin) {
        this.plugin = plugin;
        this.suggest = new Suggest(plugin);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!plugin.getConfig().getBoolean("suggests.threadNotificationEnable")) {return;}

        TextChannel channel = event.getChannel().asTextChannel();
        if (!channel.equals(suggest.getSuggestChannel())) {
            return;
        }

        channel.retrieveMessageById(event.getMessageId()).queue(message -> {
            ThreadChannel threadChannel = message.getStartedThread();
            if (threadChannel == null || threadChannel.isArchived() || threadChannel.isLocked()) {
                return;
            }

            User user = event.getUser();
            if (event.getReaction().getEmoji().equals(Emoji.fromUnicode("👍"))) {
                EmbedBuilder reactionAddEmbed = new EmbedBuilder()
                        .setDescription(user.getGlobalName() + " 👍")
                        .setColor(Color.decode(suggest.getSuggestEmbedColor()));

                threadChannel.sendMessage("").setEmbeds(reactionAddEmbed.build()).queue();


            } else if (event.getReaction().getEmoji().equals(Emoji.fromUnicode("👎"))) {
                EmbedBuilder reactionAddEmbed = new EmbedBuilder()
                        .setDescription(user.getGlobalName() + " 👎")
                        .setColor(Color.decode("#ff4d4d"));

                threadChannel.sendMessage("").setEmbeds(reactionAddEmbed.build()).queue();
            } else return;

        });
    }
}
