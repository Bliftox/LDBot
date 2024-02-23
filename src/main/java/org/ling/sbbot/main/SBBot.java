package org.ling.sbbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SBBot extends JavaPlugin {
    private JDA jda;

    private static SBBot instance;

    public static SBBot getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        try {
            jda = JDABuilder.createDefault(getConfig().getString("token"))
                    .enableIntents(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_WEBHOOKS)

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
