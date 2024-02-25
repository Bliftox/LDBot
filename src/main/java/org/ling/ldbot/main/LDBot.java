package org.ling.ldbot.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.ling.ldbot.discord.CheckGuild;
import org.ling.ldbot.discord.DiscordCommands;
import org.ling.ldbot.discord.applications.Application;
import org.ling.ldbot.discord.applications.result.ApplicationAccept;
import org.ling.ldbot.discord.applications.result.ApplicationReject;
import org.ling.ldbot.discord.suggest.Suggest;
import org.ling.ldbot.minecraft.commands.MinecraftReloadCommand;

public final class LDBot extends JavaPlugin {
    private JDA jda;

    private static LDBot instance;

    public static LDBot getInstance() {
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
                    .addEventListeners(new ApplicationReject(this))
                    .addEventListeners(new CheckGuild(this))
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
