package org.ling.sbbot.discord.applications;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.discord.DiscordCommands;
import org.ling.sbbot.main.SBBot;

import java.awt.*;
import java.time.Instant;

public class Application extends ListenerAdapter {

    private final SBBot plugin;

    public Application(SBBot plugin) {
        this.plugin = plugin;
    }

    public static final String getResumeButtonId() {
        return "resume";
    }

    public static final String getFieldOneId() {
        return "fieldOne";
    }

    public static final String getFieldTwoId() {
        return "fieldTwo";
    }

    public static final String getFieldThreeId() {
        return "fieldThree";
    }

    public static final String getFieldFourId() {
        return "fieldFour";
    }

    public static final String getFieldFiveId() {
        return "fieldFive";
    }

    public static final String getApplicationModalId() {
        return "applicationModal";
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(DiscordCommands.getResumeCommandId())) {

            TextChannel textChannel = event.getChannel().asTextChannel();

            textChannel.sendMessage("gfdggggggggg").addActionRow(Button.of(
                    ButtonStyle.DANGER, getResumeButtonId(), "gjjb", Emoji.fromUnicode("🍄")))
                    .queue();

            event.reply("Успешно").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals(getResumeButtonId())) {

            TextInput fieldOne = TextInput.create(getFieldOneId(), "[🎗️] Ваш ник в игре", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setMaxLength(16)
                    .build();

            TextInput fieldTwo = TextInput.create(getFieldTwoId(), "[🎨] Сколько вам лет?", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setMaxLength(2)
                    .build();

            TextInput fieldThree = TextInput.create(getFieldThreeId(), "[📜] Немного о себе", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setMaxLength(500)
                    .build();

            TextInput fieldFour = TextInput.create(getFieldFourId(), "[🍄] Почему именно на сервер?", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setMaxLength(150)
                    .build();

            TextInput fieldFive = TextInput.create(getFieldFiveId(), "[⚠️] Любите играть с читами?", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setMaxLength(50)
                    .build();

            Modal applicationModal = Modal.create(getApplicationModalId(), "[🐸] Заявка на сервер")
                    .addActionRows(
                            ActionRow.of(fieldOne),
                            ActionRow.of(fieldTwo),
                            ActionRow.of(fieldThree),
                            ActionRow.of(fieldFour),
                            ActionRow.of(fieldFive))
                    .build();

            event.replyModal(applicationModal).queue();
        }
    }


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals(getApplicationModalId())) {

            EmbedBuilder applicationEmbed = new EmbedBuilder()

                    /*.addField("### [🎗️] Ваш ник в игре", "```text\n" + event.getValue(getFieldOneId()).getAsString() + "\n```", false)
                    .addField("### [🎨] Сколько вам лет?", "```text\n" + event.getValue(getFieldTwoId()).getAsString() + "\n```", false)
                    .addField("### [📜] Немного о себе", "```text\n" + event.getValue(getFieldThreeId()).getAsString() + "\n```", false)
                    .addField("### [🍄] Почему именно на сервер?", "```text\n" + event.getValue(getFieldFourId()).getAsString() + "\n```", false)
                    .addField("### [⚠️] Любите играть с читами?", "```text\n" + event.getValue(getFieldFiveId()).getAsString() + "\n```", false)*/

                    .setDescription(
                            "## [📋] Заявка от " + event.getUser().getName() + "\n" +
                            "### 🎗️ Ваш ник в игре" + "\n```text\n" + event.getValue(getFieldOneId()).getAsString() + "\n```" +
                            "\n\n### 🎨 Сколько вам лет?" + "\n```text\n" + event.getValue(getFieldTwoId()).getAsString() + "\n```" +
                            "\n\n### 📜 Немного о себе" + "\n```text\n" + event.getValue(getFieldThreeId()).getAsString() + "\n```" +
                            "\n\n### 🍄 Почему именно на сервер?" + "\n```text\n" + event.getValue(getFieldFourId()).getAsString() + "\n```" +
                            "\n\n### ⚠️ Любите играть с читами?" + "\n```text\n" + event.getValue(getFieldFiveId()).getAsString() + "\n```"
                    )

                    .setColor(Color.decode("#ff9933"))
                    .setImage("https://cdn.discordapp.com/attachments/890237163151695892/1210709512693223465/-27-12-2023.png?ex=65eb8c19&is=65d91719&hm=86049861ffecbe6ed389859e3faf5d37e9d2a103c5eafd824255c7f8fe457586&")
                    .setTimestamp(Instant.now());

            TextChannel textChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("applications.channelId"));

            textChannel.sendMessage("<@959296276279726130>   " + "<@" + event.getUser().getId() + "> " + event.getUser().getName() + " " + event.getUser().getGlobalName())
                    .setEmbeds(applicationEmbed.build())
                    .addActionRow(
                            Button.of(ButtonStyle.SUCCESS, event.getUser().getId(), getButtonAcceptLabel(), Emoji.fromUnicode("✅")),
                            Button.of(ButtonStyle.DANGER, String.valueOf(event.getUser().getIdLong() + 1), getButtonRejectLabel(), Emoji.fromUnicode("⛔")))
                    .queue();

            try {
                event.getMember().modifyNickname(event.getValue(getFieldOneId()).getAsString()).queue();
            } catch (HierarchyException e) {
                Bukkit.getLogger().warning("Cannot change user role higher than bot");
            }


            event.reply("Успешно").setEphemeral(true).queue();
        }
    }

    public static final String getButtonAcceptLabel() {
        return "Принять";
    }

    public static final String getButtonRejectLabel() {
        return "Отклонить";
    }
}
