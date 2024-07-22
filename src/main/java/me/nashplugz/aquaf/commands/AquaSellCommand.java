package me.nashplugz.aquaf.commands;

import me.nashplugz.aquaf.AquaFest;
import me.nashplugz.aquaf.Fish;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AquaSellCommand implements CommandExecutor {
    private final AquaFest plugin;

    public AquaSellCommand(AquaFest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        double totalValue = 0.0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String fishName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                Fish fish = plugin.getFishGenerator().getFishByName(fishName);
                if (fish != null) {
                    totalValue += fish.getValue() * item.getAmount();
                    player.getInventory().remove(item);
                }
            }
        }

        if (totalValue > 0) {
            plugin.getEconomy().depositPlayer(player, totalValue);
            player.sendMessage(ChatColor.GREEN + "You sold your fish for $" + String.format("%.2f", totalValue) + "!");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have any fish to sell.");
        }

        return true;
    }
}
