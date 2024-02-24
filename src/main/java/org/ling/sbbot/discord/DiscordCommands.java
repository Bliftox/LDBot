package org.ling.sbbot.discord;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.main.SBBot;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommands extends ListenerAdapter {

    private final SBBot plugin;

    public DiscordCommands(SBBot plugin) {
        this.plugin = plugin;
    }

    public static final String getSuggestCommandId() {
        return "suggest";
    }

    public static final String getReloadCommandId() {
        return "reload";
    }

    public static final String getResumeCommandId() {
        return "resume";
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandDataList = new ArrayList<>();
        commandDataList.add(Commands.slash(getSuggestCommandId(), "Create a suggestion"));

        commandDataList.add(Commands.slash(getReloadCommandId(), "Reload plugin"));

        commandDataList.add(Commands.slash(getResumeCommandId(), "Set resume"));

        event.getGuild().updateCommands().addCommands(commandDataList).queue();
        for (CommandData commandData : commandDataList) {
            plugin.getJda().upsertCommand(commandData).queue();
        }
    }
}
