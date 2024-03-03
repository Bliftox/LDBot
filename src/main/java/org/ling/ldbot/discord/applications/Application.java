package org.ling.ldbot.discord.applications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
import org.json.simple.JSONObject;
import org.ling.ldbot.discord.DiscordCommands;
import org.ling.ldbot.main.DataBase;
import org.ling.ldbot.main.LDBot;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class Application extends ListenerAdapter {

    private final LDBot plugin;

    public Application(LDBot plugin) {
        this.plugin = plugin;
    }

    // Constants
    private static final String RESUME_BUTTON_ID = "resume";

    private static final String FIELD_ONE_ID = "fieldOne";
    private static final String FIELD_TWO_ID = "fieldTwo";
    private static final String FIELD_THREE_ID = "fieldThree";
    private static final String FIELD_FOUR_ID = "fieldFour";
    private static final String FIELD_FIVE_ID = "fieldFive";

    private static final String APPLICATION_MODAL_ID = "applicationModal";

    private static final String BUTTON_ACCEPT_LABEL = "Принять";
    private static final String BUTTON_ACCEPT_ID = "ACCEPT";

    private static final String BUTTON_REJECT_LABEL = "Отказать";
    private static final String BUTTON_REJECT_ID = "REJECT";

    public static String getButtonAcceptId() {
        return BUTTON_ACCEPT_ID;
    }

    public static String getButtonRejectId() {
        return BUTTON_REJECT_ID;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(DiscordCommands.getResumeCommandId())) {
            EmbedBuilder resumeBuilder = new EmbedBuilder()
                    .setTitle("📄 Заявки на сервер")
                    .setDescription("- Чтобы подать заявку на сервер, нажмите\n на кнопку ниже и заполните заявку по форме.")
                    .setColor(Color.decode("#ffcc00"))
                    .setImage("https://cdn.discordapp.com/attachments/1197957820562296895/1211049362566684772/f5f8879f0c89bfef5317d29abc54b41c.png?ex=65ecc89c&is=65da539c&hm=07310e9c16495a612f63e68836a8bd9242f111806a9af26fecb12419b775aa39&");

            TextChannel textChannel = event.getChannel().asTextChannel();

            textChannel.sendMessage("").setEmbeds(resumeBuilder.build()).addActionRow(Button.of(
                            ButtonStyle.DANGER, RESUME_BUTTON_ID, "Подать", Emoji.fromUnicode("🍄")))
                    .queue();

            event.reply("✅ Успешное создание резюме").setEphemeral(true).queue();
        }

    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals(RESUME_BUTTON_ID)) {

            if (plugin.getDataBase().isUserInDatabase(event.getUser().getId())) {
                event.reply("⚠️ Вы уже подали заявку!").setEphemeral(true).queue();
                return;
            }

            if (plugin.getConfig().getBoolean("applications.applicationsEnable")) {

                TextInput fieldOne = TextInput.create(FIELD_ONE_ID, "[🎗️] Ваш ник в игре", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setMaxLength(16)
                        .build();

                TextInput fieldTwo = TextInput.create(FIELD_TWO_ID, "[🎨] Сколько вам лет?", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setMaxLength(2)
                        .build();

                TextInput fieldThree = TextInput.create(FIELD_THREE_ID, "[📜] Немного о себе", TextInputStyle.PARAGRAPH)
                        .setRequired(true)
                        .setMaxLength(500)
                        .build();

                TextInput fieldFour = TextInput.create(FIELD_FOUR_ID, "[🍄] Почему именно на сервер?", TextInputStyle.PARAGRAPH)
                        .setRequired(true)
                        .setMaxLength(150)
                        .build();

                TextInput fieldFive = TextInput.create(FIELD_FIVE_ID, "[⚠️] Любите играть с читами?", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setMaxLength(50)
                        .build();

                Modal applicationModal = Modal.create(APPLICATION_MODAL_ID, "[🐸] Заявка на сервер")
                        .addActionRows(
                                ActionRow.of(fieldOne),
                                ActionRow.of(fieldTwo),
                                ActionRow.of(fieldThree),
                                ActionRow.of(fieldFour),
                                ActionRow.of(fieldFive))
                        .build();

                event.replyModal(applicationModal).queue();
            } else event.reply("⚠️ На данный момент заявки на данном сервере прекращены").setEphemeral(true).queue();
        }
    }


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals(APPLICATION_MODAL_ID)) {

            if (Objects.equals(plugin.getConfig().getString("applications.channelId"), null) || plugin.getConfig().getString("applications.channelId").isEmpty()) {
                Bukkit.getLogger().warning("The value for [applications.channelId] is incorrect.");
                return;
            }

            String fieldOneValue = event.getValue(FIELD_ONE_ID).getAsString();
            String fieldTwoValue = event.getValue(FIELD_TWO_ID).getAsString();
            String fieldThreeValue = event.getValue(FIELD_THREE_ID).getAsString();
            String fieldFourValue = event.getValue(FIELD_FOUR_ID).getAsString();
            String fieldFiveValue = event.getValue(FIELD_FIVE_ID).getAsString();

            EmbedBuilder applicationEmbed = new EmbedBuilder()
                    .setDescription(
                            "## [📋] Заявка от " + event.getUser().getName() + "\n" +
                                    "### 🎗️ Ваш ник в игре" + "\n```text\n" + fieldOneValue + "\n```" +
                                    "\n\n### 🎨 Сколько вам лет?" + "\n```text\n" + fieldTwoValue + "\n```" +
                                    "\n\n### 📜 Немного о себе" + "\n```text\n" + fieldThreeValue + "\n```" +
                                    "\n\n### 🍄 Почему именно на сервер?" + "\n```text\n" + fieldFourValue + "\n```" +
                                    "\n\n### ⚠️ Любите играть с читами?" + "\n```text\n" + fieldFiveValue + "\n```"
                    )

                    .setColor(Color.decode("#ff9933"))
                    .setImage("https://cdn.discordapp.com/attachments/890237163151695892/1210709512693223465/-27-12-2023.png?ex=65eb8c19&is=65d91719&hm=86049861ffecbe6ed389859e3faf5d37e9d2a103c5eafd824255c7f8fe457586&")
                    .setTimestamp(Instant.now());



            try {
                TextChannel textChannel = plugin.getJda().getTextChannelById(plugin.getConfig().getString("applications.channelId"));

                textChannel.sendMessage(plugin.getJda().getRoleById(plugin.getConfig().getString("applications.notificationRoleId")).getAsMention() + event.getUser().getAsMention() + event.getUser().getName() + " " + event.getUser().getGlobalName())
                        .setEmbeds(applicationEmbed.build())
                        .addActionRow(
                                Button.of(ButtonStyle.SUCCESS, BUTTON_ACCEPT_ID, BUTTON_ACCEPT_LABEL, Emoji.fromUnicode("✅")),
                                Button.of(ButtonStyle.DANGER, BUTTON_REJECT_ID, BUTTON_REJECT_LABEL, Emoji.fromUnicode("⛔")))

                        .queue(message -> {
                            try {
                                plugin.getDataBase().saveApplication(
                                        message.getId(),
                                        event.getUser(),
                                        fieldOneValue,
                                        fieldTwoValue,
                                        fieldThreeValue,
                                        fieldFourValue,
                                        fieldFiveValue
                                        );
                            } catch (
                                    SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (NullPointerException e) {
                Bukkit.getLogger().warning("It's impossible to send the application because the channel doesn't exist.");
            }



            event.reply("✅ Успешно создано!").setEphemeral(true).queue();
        }
    }



}
