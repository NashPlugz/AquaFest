package me.nashplugz.aquaf.commands;

import me.nashplugz.aquaf.AquaFest;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AquaFestCommand implements CommandExecutor {
    private AquaFest plugin;

    public AquaFestCommand(AquaFest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                showHelp(sender);
                break;
            case "start":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /aquafest start <eventname> [worldwide]");
                    return true;
                }
                startEvent(sender, args[1], args.length > 2 && args[2].equalsIgnoreCase("worldwide"));
                break;
            case "stop":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /aquafest stop <eventname> [worldwide]");
                    return true;
                }
                stopEvent(sender, args[1], args.length > 2 && args[2].equalsIgnoreCase("worldwide"));
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command. Use /aquafest help for a list of commands.");
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== AquaFest Help ===");
        sender.sendMessage(ChatColor.YELLOW + "/aquafest help" + ChatColor.WHITE + " - Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/aquafest start <eventname> [worldwide]" + ChatColor.WHITE + " - Start a fishing event");
        sender.sendMessage(ChatColor.YELLOW + "/aquafest stop <eventname> [worldwide]" + ChatColor.WHITE + " - Stop a fishing event");
        sender.sendMessage(ChatColor.GOLD + "=== How to Create an Event ===");
        sender.sendMessage(ChatColor.WHITE + "1. Use " + ChatColor.YELLOW + "/aquafest start <eventname>" + ChatColor.WHITE + " to begin a new fishing event in the current world");
        sender.sendMessage(ChatColor.WHITE + "2. Use " + ChatColor.YELLOW + "/aquafest start <eventname> worldwide" + ChatColor.WHITE + " to begin a new worldwide fishing event");
        sender.sendMessage(ChatColor.WHITE + "3. Players can now fish for special catches");
        sender.sendMessage(ChatColor.WHITE + "4. Use " + ChatColor.YELLOW + "/aquafest stop <eventname>" + ChatColor.WHITE + " to end the event in the current world");
        sender.sendMessage(ChatColor.WHITE + "5. Use " + ChatColor.YELLOW + "/aquafest stop <eventname> worldwide" + ChatColor.WHITE + " to end a worldwide event");
    }

    private void startEvent(CommandSender sender, String eventName, boolean worldwide) {
        if (!sender.hasPermission("aquafest.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to start events.");
            return;
        }

        boolean success;
        if (worldwide) {
            success = plugin.getEventManager().startWorldwideEvent(eventName);
            if (success) {
                plugin.getServer().broadcastMessage(ChatColor.GOLD + "A worldwide fishing event '" + eventName + "' has started! Get your rods ready!");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to start worldwide event. Make sure no other events are active.");
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to start a world-specific event.");
                return;
            }
            World world = ((Player) sender).getWorld();
            success = plugin.getEventManager().startEvent(eventName, world);
            if (success) {
                plugin.getServer().broadcastMessage(ChatColor.GOLD + "A fishing event '" + eventName + "' has started in world '" + world.getName() + "'! Get your rods ready!");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to start event. Make sure no other events are active in this world.");
            }
        }
    }

    private void stopEvent(CommandSender sender, String eventName, boolean worldwide) {
        if (!sender.hasPermission("aquafest.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to stop events.");
            return;
        }

        boolean success;
        if (worldwide) {
            success = plugin.getEventManager().stopWorldwideEvent(eventName);
            if (success) {
                plugin.getServer().broadcastMessage(ChatColor.GOLD + "The worldwide fishing event '" + eventName + "' has ended! Thanks for participating!");
                // Display worldwide leaderboard
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to stop worldwide event. Make sure the event name is correct and a worldwide event is active.");
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to stop a world-specific event.");
                return;
            }
            World world = ((Player) sender).getWorld();
            success = plugin.getEventManager().stopEvent(eventName, world);
            if (success) {
                plugin.getServer().broadcastMessage(ChatColor.GOLD + "The fishing event '" + eventName + "' in world '" + world.getName() + "' has ended! Thanks for participating!");
                // Display world-specific leaderboard
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to stop event. Make sure the event name is correct and an event is active in this world.");
            }
        }
    }
}