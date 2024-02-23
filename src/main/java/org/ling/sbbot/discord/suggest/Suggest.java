package org.ling.sbbot.discord.suggest;

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
import org.ling.sbbot.discord.DiscordCommands;
import org.ling.sbbot.main.SBBot;

import java.awt.*;

public class Suggest extends ListenerAdapter {

    private final SBBot plugin;

    public Suggest(SBBot plugin) {
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

            TextChannel suggestChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("suggestChannelId"));

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(Color.decode("#58b9ff"))
                    .setTitle(event.getValue(getSuggestTitleId()).getAsString())
                    .setDescription("```text\n" + event.getValue(getSuggestTextId()).getAsString() + "\n```");


            suggestChannel.sendMessage("").setEmbeds(embedBuilder.build()).queue(sentMessage -> {
                // Добавление реакции к сообщению
                sentMessage.createThreadChannel("Обсуждение: " + event.getValue(getSuggestTitleId()).getAsString()).queue();
                sentMessage.addReaction(Emoji.fromUnicode("👍")).queue();
                sentMessage.addReaction(Emoji.fromUnicode("👎")).queue();
            }, throwable -> {
                // Обработка ошибки, если что-то пошло не так
                throwable.printStackTrace();
            });

            event.reply("✅ Успешно отправлено!").setEphemeral(true).queue();
        }
    }
}
