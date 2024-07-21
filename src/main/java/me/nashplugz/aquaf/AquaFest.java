package me.nashplugz.aquaf;

import me.nashplugz.aquaf.commands.AquaFestCommand;
import me.nashplugz.aquaf.commands.AquaSellCommand;
import me.nashplugz.aquaf.events.FishingEventManager;
import me.nashplugz.aquaf.listeners.FishingListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AquaFest extends JavaPlugin {
    private ConfigManager configManager;
    private FishingEventManager eventManager;
    private EventBossBarManager eventBossBarManager;
    private LeaderboardManager leaderboardManager;
    private FishGenerator fishGenerator;
    private Economy economy;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        // Load configuration
        saveDefaultConfig();
        configManager = new ConfigManager(this);

        // Initialize managers
        eventManager = new FishingEventManager(this);
        eventBossBarManager = new EventBossBarManager(this);
        leaderboardManager = new LeaderboardManager(this);
        fishGenerator = new FishGenerator(getConfig());
        scoreboardManager = new ScoreboardManager(this);

        // Setup economy
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands
        getCommand("aquafest").setExecutor(new AquaFestCommand(this));
        getCommand("aquasell").setExecutor(new AquaSellCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);

        getLogger().info("AquaFest has been enabled!");
    }

    @Override
    public void onDisable() {
        // Perform any cleanup tasks here
        getLogger().info("AquaFest has been disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FishingEventManager getEventManager() {
        return eventManager;
    }

    public EventBossBarManager getEventBossBarManager() {
        return eventBossBarManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public FishGenerator getFishGenerator() {
        return fishGenerator;
    }

    public Economy getEconomy() {
        return economy;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
