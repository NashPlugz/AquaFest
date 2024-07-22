package me.nashplugz.aquaf;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FishingCooldownManager {
    private final Map<String, Long> lastFishingTimes;
    private final Map<UUID, Long> lastMessageTimes;
    private final double exhaustionRadius;
    private final long cooldownTime;
    private final long messageCooldown;

    public FishingCooldownManager(double exhaustionRadius, long cooldownTime, long messageCooldown) {
        this.lastFishingTimes = new HashMap<>();
        this.lastMessageTimes = new HashMap<>();
        this.exhaustionRadius = exhaustionRadius;
        this.cooldownTime = cooldownTime;
        this.messageCooldown = messageCooldown;
    }

    public boolean canFish(Location location) {
        String key = getLocationKey(location);
        long currentTime = System.currentTimeMillis();
        Long lastFishingTime = lastFishingTimes.get(key);

        if (lastFishingTime == null || currentTime - lastFishingTime > cooldownTime) {
            lastFishingTimes.put(key, currentTime);
            return true;
        }
        return false;
    }

    public boolean canSendMessage(Player player) {
        long currentTime = System.currentTimeMillis();
        Long lastMessageTime = lastMessageTimes.get(player.getUniqueId());

        if (lastMessageTime == null || currentTime - lastMessageTime > messageCooldown) {
            lastMessageTimes.put(player.getUniqueId(), currentTime);
            return true;
        }
        return false;
    }

    private String getLocationKey(Location location) {
        int x = (int) (location.getX() / exhaustionRadius);
        int y = (int) (location.getY() / exhaustionRadius);
        int z = (int) (location.getZ() / exhaustionRadius);
        return location.getWorld().getName() + ":" + x + ":" + y + ":" + z;
    }

    public double getExhaustionRadius() {
        return exhaustionRadius;
    }
}
