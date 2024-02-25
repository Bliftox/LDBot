package org.ling.ldbot.discord;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.ling.ldbot.main.LDBot;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommands extends ListenerAdapter {

    private final LDBot plugin;

    public DiscordCommands(LDBot plugin) {
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
