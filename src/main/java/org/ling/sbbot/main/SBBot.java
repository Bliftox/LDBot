package org.ling.sbbot.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ling.sbbot.discord.DiscordCommands;
import org.ling.sbbot.discord.applications.Application;
import org.ling.sbbot.discord.applications.result.ApplicationAccept;
import org.ling.sbbot.discord.applications.result.ApplicationReject;
import org.ling.sbbot.discord.globalchat.GlobalChat;
import org.ling.sbbot.discord.suggest.Suggest;
import org.ling.sbbot.minecraft.commands.MinecraftReloadCommand;

public final class SBBot extends JavaPlugin {
    private JDA jda;

    private static SBBot instance;

    public static SBBot getInstance() {
        return instance;
    }

    public JDA getJda() {
        return jda;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        startBot();
        new MinecraftReloadCommand(this);

        //getServer().getPluginManager().registerEvents(new GlobalChat(this), this);
    }

    public void startBot() {
        try {
            jda = JDABuilder.createDefault(getConfig().getString("token"))
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)

                    .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.GUILD_WEBHOOKS)

                    .addEventListeners(new Suggest(this))
                    .addEventListeners(new DiscordCommands(this))
                    .addEventListeners(new DiscordReloadCommand(this))
                    .addEventListeners(new Application(this))
                    .addEventListeners(new ApplicationAccept(this))
                    // .addEventListeners(new ApplicationReject(this))
                    // .addEventListeners(new GlobalChat(this))
                    .build();
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Change the token to yours in the config!");
        } catch (Exception e) {
            Bukkit.getLogger().warning("It seems that your bot did not start( \nBe sure to report this issue to the developer!");
            e.printStackTrace();
        }
    }


    @Override
    public void onDisable() {
        if (jda != null) {
            try {
                jda.shutdown();
                jda.shutdownNow();
            } catch (NullPointerException e) {
                Bukkit.getLogger().info("Bot can not shutdown because it already shutdown");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
