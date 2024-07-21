package me.nashplugz.aquaf.commands;

import me.nashplugz.aquaf.AquaFest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class AquaSellCommand implements CommandExecutor {
    private AquaFest plugin;

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
        double totalValue = 0;
        int fishSold = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                ItemMeta meta = item.getItemMeta();
                String fishName = ChatColor.stripColor(meta.getDisplayName());
                if (plugin.getFishGenerator().getFishByName(fishName) != null) {
                    double value = getValueFromLore(meta.getLore());
                    if (value > 0) {
                        totalValue += value * item.getAmount();
                        fishSold += item.getAmount();
                        player.getInventory().remove(item);
                    }
                }
            }
        }

        if (fishSold > 0) {
            plugin.getEconomy().depositPlayer(player, totalValue);
            player.sendMessage(ChatColor.GREEN + "You sold " + fishSold + " fish for $" + String.format("%.2f", totalValue) + "!");
        } else {
            player.sendMessage(ChatColor.RED + "You don't have any event fish to sell.");
        }

        return true;
    }

    private double getValueFromLore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) {
            String valueLine = ChatColor.stripColor(lore.get(0));
            if (valueLine.startsWith("Value: $")) {
                try {
                    return Double.parseDouble(valueLine.substring(8));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
