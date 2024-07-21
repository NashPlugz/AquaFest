package me.nashplugz.aquaf.listeners;

import me.nashplugz.aquaf.AquaFest;
import me.nashplugz.aquaf.Fish;
import org.bukkit.ChatColor;
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
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && plugin.getEventManager().isEventActive(event.getPlayer().getWorld())) {
            Player player = event.getPlayer();
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

            plugin.getEventManager().recordCatch(player, caughtFish);
            player.sendMessage(ChatColor.GREEN + "You caught a " + getTierColor(caughtFish.getTier()) + caughtFish.getName() +
                    ChatColor.GREEN + " worth $" + String.format("%.2f", caughtFish.getValue()) + "!");

            plugin.getScoreboardManager().updateScoreboard();
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
