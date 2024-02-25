package org.ling.sbbot.minecraft.commands;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.ling.sbbot.main.SBBot;

import java.util.List;

public class MinecraftReloadCommand extends AbstractCommands{

    private final SBBot plugin;

    public MinecraftReloadCommand(SBBot plugin) {
        super("sbbot");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) return;

        if (args[0].equals("reload")) {
            // Проверяет есть ли права у пользователя
            if (!sender.hasPermission("sbbot.reload")) {
                sender.sendMessage(ChatColor.RED + "Unknown command. Try /help for a list of commands.\n");
                return;
            }

            // Перезагрузка плагина
            plugin.getJda().shutdown();
            SBBot.getInstance().reloadConfig();
            Bukkit.getScheduler().cancelTasks(plugin);
            plugin.startBot();

            sender.sendMessage(ChatColor.GREEN + "[SBBot] Успешная перезагрузка плагина!");
            return;
        }

        // Если не соблюдены все условия, то пользователь получает "не известная команда"
        sender.sendMessage("Unknown command. Try /help for a list of commands.");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        // Подсказка при попытке написать команду
        if (args.length == 1) return Lists.newArrayList("reload");
        return Lists.newArrayList();
    }
}
