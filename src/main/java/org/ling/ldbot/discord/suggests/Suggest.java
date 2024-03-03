package org.ling.ldbot.discord.suggests;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.ling.ldbot.discord.DiscordCommands;
import org.ling.ldbot.main.LDBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;

public class Suggest extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Suggest.class);
    private static final String SUGGEST_TITLE_ID = "suggestTitleId";
    private static final String SUGGEST_TEXT_ID = "suggestTextId";
    private static final String SUGGEST_MODAL_ID = "suggestModalId";
    private static final String SUGGEST_CHANNEL_CONFIG_KEY = "suggests.suggestChannelId";
    private static final String SUGGEST_EMBED_COLOR = "#0099ff";

    private final LDBot plugin;
    private static TextChannel suggestChannel;

    public TextChannel getSuggestChannel() {
        return suggestChannel;
    }

    public String getSuggestEmbedColor() {
        return SUGGEST_EMBED_COLOR;
    }

    public void setSuggestChannel(TextChannel suggestChannel) {
        this.suggestChannel = suggestChannel;
    }

    public Suggest(LDBot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        setSuggestChannel(plugin.getJda().getTextChannelById(plugin.getConfig().getString("suggests.suggestChannelId")));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(DiscordCommands.getSuggestCommandId())) {

            TextInput suggestTitle = TextInput.create(SUGGEST_TITLE_ID, "[📌] Заголовок предложения", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setMaxLength(25)
                    .build();

            TextInput suggestText = TextInput.create(SUGGEST_TEXT_ID, "[📄] Ваше предложение", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMaxLength(500)
                    .build();

            event.replyModal(Modal.create(SUGGEST_MODAL_ID, "[💡] Предложение")
                    .addActionRows(ActionRow.of(suggestTitle), ActionRow.of(suggestText))
                    .build()).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals(SUGGEST_MODAL_ID)) {
            String suggestChannelId = plugin.getConfig().getString(SUGGEST_CHANNEL_CONFIG_KEY);
            if (suggestChannelId == null || suggestChannelId.isEmpty()) {
                logger.warn("The value for [{}] is incorrect.", SUGGEST_CHANNEL_CONFIG_KEY);
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTimestamp(Instant.now())
                    .setColor(Color.decode(SUGGEST_EMBED_COLOR))
                    .setAuthor("[💡] Предложение от - " + event.getUser().getName())
                    .setDescription("\n" + event.getValue(SUGGEST_TITLE_ID).getAsString() + "\n```text\n" + event.getValue(SUGGEST_TEXT_ID).getAsString() + "\n```\n");

            try {
                suggestChannel.sendMessage(event.getUser().getAsMention())
                        .setEmbeds(embedBuilder.build())
                        .queue(message -> {
                            message.createThreadChannel("Обсуждение: " + event.getValue(SUGGEST_TITLE_ID).getAsString()).queue();
                            message.addReaction(Emoji.fromUnicode("👍")).queue(); // 👍
                            message.addReaction(Emoji.fromUnicode("👎")).queue(); // 👎
                        }, throwable -> logger.error("Error while sending suggestion.", throwable));
            } catch (NullPointerException e) {
                logger.warn("It's impossible to send the suggest, because the channel doesn't exist.");
            }

            event.reply("✅ Успешно отправлено!").setEphemeral(true).queue();
        }
    }
}
