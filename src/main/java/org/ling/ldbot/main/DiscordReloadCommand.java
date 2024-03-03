package org.ling.ldbot.main;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.ling.ldbot.discord.DiscordCommands;

public class DiscordReloadCommand extends ListenerAdapter {

    private final LDBot plugin;

    public DiscordReloadCommand(LDBot plugin) {
        this.plugin = plugin;
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals(DiscordCommands.getReloadCommandId())) {
            event.reply("✅ Successfully reload Bot!").setEphemeral(true).queue();
            plugin.getJda().shutdown();
            plugin.getInstance().reloadConfig();
            Bukkit.getScheduler().cancelTasks(plugin.getInstance());
            plugin.startBot();
        }
    }


}
