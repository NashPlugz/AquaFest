package me.nashplugz.aquaf;

import me.nashplugz.aquaf.commands.AquaFestCommand;
import me.nashplugz.aquaf.commands.AquaSellCommand;
import me.nashplugz.aquaf.events.FishingEventManager;
import me.nashplugz.aquaf.listeners.FishingListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AquaFest extends JavaPlugin implements Listener {
    private ConfigManager configManager;
    private FishingEventManager eventManager;
    private EventBossBarManager eventBossBarManager;
    private LeaderboardManager leaderboardManager;
    private FishGenerator fishGenerator;
    private Economy economy;
    private ScoreboardManager scoreboardManager;
    private FishingCooldownManager fishingCooldownManager;

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

        // Setup fishing cooldown manager
        double exhaustionRadius = getConfig().getDouble("fish_exhaustion_radius", 5.0);
        long cooldownTime = parseDuration(getConfig().getString("fish_exhaustion_cooldown", "5m"));
        long messageCooldown = 10000; // 10 seconds in milliseconds
        fishingCooldownManager = new FishingCooldownManager(exhaustionRadius, cooldownTime, messageCooldown);

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
        getServer().getPluginManager().registerEvents(this, this);

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

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        eventBossBarManager.updatePlayerBossBar(player);
        if (eventManager.isEventActive(player.getWorld())) {
            scoreboardManager.updateScoreboard(player.getWorld().getName());
        } else if (eventManager.isWorldwideEventActive()) {
            scoreboardManager.updateGlobalScoreboard();
        } else {
            scoreboardManager.removePlayerScoreboard(player);
        }
    }

    private long parseDuration(String duration) {
        long multiplier = 1000; // Default to milliseconds
        if (duration.endsWith("s")) {
            multiplier = 1000;
            duration = duration.substring(0, duration.length() - 1);
        } else if (duration.endsWith("m")) {
            multiplier = 60000;
            duration = duration.substring(0, duration.length() - 1);
        }
        try {
            return Long.parseLong(duration) * multiplier;
        } catch (NumberFormatException e) {
            getLogger().warning("Invalid duration format: " + duration + ". Using default of 5 minutes.");
            return 300000; // 5 minutes in milliseconds
        }
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

    public FishingCooldownManager getFishingCooldownManager() {
        return fishingCooldownManager;
    }
}
