package org.ling.ldbot.discord.suggest;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.ling.ldbot.discord.DiscordCommands;
import org.ling.ldbot.main.LDBot;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class Suggest extends ListenerAdapter {

    private final LDBot plugin;

    public Suggest(LDBot plugin) {
        this.plugin = plugin;
    }


    public static final String getSuggestTitleId() {
        return "suggestTitleId";
    }

    public static final String getSuggestTextId() {
        return "suggestTextId";
    }

    public static final String getSuggestModalId() {
        return "suggestModalId";
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(DiscordCommands.getSuggestCommandId())) {

            TextInput suggestTitle = TextInput.create(getSuggestTitleId(), "[📌] Заголовок предложения", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setMaxLength(25)
                    .build();

            TextInput suggestText = TextInput.create(getSuggestTextId(), "[📄] Ваше предложение", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMaxLength(500)
                    .build();

            Modal modal = Modal.create(getSuggestModalId(), "[💡] Предложение")
                    .addActionRows(
                            ActionRow.of(suggestTitle),
                            ActionRow.of(suggestText))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals(getSuggestModalId())) {
            if (Objects.equals(plugin.getConfig().getString("suggests.suggestChannelId"), null) || plugin.getConfig().getString("suggests.suggestChannelId").isEmpty()) {
                Bukkit.getLogger().warning("The value for [suggests.suggestChannelId] is incorrect.");
                return;
            }

            TextChannel suggestChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("suggests.suggestChannelId"));

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTimestamp(Instant.now())
                    .setColor(Color.decode("#0099ff"))
                    .setTitle("[💡] Предложение от - " + event.getUser().getName())
                    .setDescription(event.getValue(getSuggestTitleId()).getAsString() + "\n```text\n" + event.getValue(getSuggestTextId()).getAsString() + "\n```");


            suggestChannel.sendMessage("").setEmbeds(embedBuilder.build()).queue(message -> {
                // Добавление реакции к сообщению
                message.createThreadChannel("Обсуждение: " + event.getValue(getSuggestTitleId()).getAsString()).queue();
                message.addReaction(Emoji.fromUnicode("👍")).queue();
                message.addReaction(Emoji.fromUnicode("👎")).queue();
            }, throwable -> {
                // Обработка ошибки, если что-то пошло не так
                throwable.printStackTrace();
            });

            event.reply("✅ Успешно отправлено!").setEphemeral(true).queue();
        }
    }
}
