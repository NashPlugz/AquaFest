package me.nashplugz.aquaf.listeners;

import me.nashplugz.aquaf.AquaFest;
import me.nashplugz.aquaf.Fish;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FishingListener implements Listener {
    private AquaFest plugin;

    public FishingListener(AquaFest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Location fishingLocation = event.getHook().getLocation();

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            // Player is casting their line
            if (!plugin.getFishingCooldownManager().canFish(fishingLocation)) {
                event.setCancelled(true);
                if (plugin.getFishingCooldownManager().canSendMessage(player)) {
                    double distance = plugin.getFishingCooldownManager().getExhaustionRadius();
                    player.sendMessage(ChatColor.RED + "The fish in this area are exhausted. " +
                            ChatColor.YELLOW + "Move at least " + String.format("%.1f", distance) + " blocks away to find more fish!");
                }
                return;
            }
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            // Player has caught a fish
            String worldName = player.getWorld().getName();

            if (plugin.getEventManager().isEventActive(player.getWorld()) || plugin.getEventManager().isWorldwideEventActive()) {
                Fish caughtFish = plugin.getFishGenerator().generateRandomFish();

                ItemStack fishItem = new ItemStack(caughtFish.getMaterial());
                ItemMeta meta = fishItem.getItemMeta();
                meta.setDisplayName(getTierColor(caughtFish.getTier()) + caughtFish.getName());
                meta.setLore(Arrays.asList(
                        ChatColor.YELLOW + "Value: $" + String.format("%.2f", caughtFish.getValue()),
                        ChatColor.GRAY + "Tier: " + getTierColor(caughtFish.getTier()) + caughtFish.getTier().name()
                ));
                fishItem.setItemMeta(meta);

                event.getCaught().remove();
                player.getInventory().addItem(fishItem);

                if (plugin.getEventManager().isWorldwideEventActive()) {
                    plugin.getLeaderboardManager().addScore(player, (int) caughtFish.getValue(), "worldwide");
                    plugin.getScoreboardManager().updateGlobalScoreboard();
                } else {
                    plugin.getLeaderboardManager().addScore(player, (int) caughtFish.getValue(), worldName);
                    plugin.getScoreboardManager().updateScoreboard(worldName);
                }

                player.sendMessage(ChatColor.GREEN + "You caught a " + getTierColor(caughtFish.getTier()) + caughtFish.getName() +
                        ChatColor.GREEN + " worth $" + String.format("%.2f", caughtFish.getValue()) + "!");
            }
        }
    }

    private ChatColor getTierColor(Fish.Tier tier) {
        switch (tier) {
            case COMMON: return ChatColor.WHITE;
            case UNCOMMON: return ChatColor.GREEN;
            case RARE: return ChatColor.BLUE;
            case EPIC: return ChatColor.DARK_PURPLE;
            case LEGENDARY: return ChatColor.GOLD;
            default: return ChatColor.WHITE;
        }
    }
}
