package org.ling.sbbot.main;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.discord.DiscordCommands;
import org.ling.sbbot.main.SBBot;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand extends ListenerAdapter {

    private final SBBot plugin;

    public ReloadCommand(SBBot plugin) {
        this.plugin = plugin;
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(DiscordCommands.getReloadCommandId())) {
            event.reply("✅ Successfully reload Bot!").setEphemeral(true).queue();
            plugin.getJda().shutdown();
            SBBot.getInstance().reloadConfig();
            Bukkit.getScheduler().cancelTasks(SBBot.getInstance());
            plugin.startBot();
        }
    }


}
