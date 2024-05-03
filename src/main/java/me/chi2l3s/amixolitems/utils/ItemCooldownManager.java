package me.chi2l3s.amixolitems.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ItemCooldownManager {

    private final HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();
    private final HashMap<String, Long> itemCooldownTimes = new HashMap<>(); // Задержка для каждого предмета

    public void setItemCooldownTime(String itemId, long time) {
        itemCooldownTimes.put(itemId, time);
    }

    public void setCooldown(Player player, String itemId) {
        long delayTime = System.currentTimeMillis() + itemCooldownTimes.getOrDefault(itemId, 0L);
        HashMap<String, Long> playerCooldowns = cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        playerCooldowns.put(itemId, delayTime);
        cooldowns.put(player.getUniqueId(), playerCooldowns);
    }

    public boolean checkCooldown(Player player, String itemId) {
        HashMap<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null || !playerCooldowns.containsKey(itemId)) {
            return true;
        }

        long remainingTime = playerCooldowns.get(itemId) - System.currentTimeMillis();
        return remainingTime <= 0;
    }

    public long getRemainingTime(Player player, String itemId) {
        HashMap<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null || !playerCooldowns.containsKey(itemId)) {
            return 0;
        }

        long remainingTime = playerCooldowns.get(itemId) - System.currentTimeMillis();
        return Math.max(remainingTime, 0);
    }

    public String formatTimeWithCorrectEnding(long timeInSeconds) {
        long lastTwoDigits = timeInSeconds % 100;
        long lastDigit = timeInSeconds % 10;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return timeInSeconds + " секунд";
        } else if (lastDigit == 1) {
            return timeInSeconds + " секундy";
        } else if (lastDigit >= 2 && lastDigit <= 4) {
            return timeInSeconds + " секунды";
        } else {
            return timeInSeconds + " секунд";
        }
    }

}
