package org.ling.sbbot.main;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.ling.sbbot.discord.DiscordCommands;

public class DiscordReloadCommand extends ListenerAdapter {

    private final SBBot plugin;

    public DiscordReloadCommand(SBBot plugin) {
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
