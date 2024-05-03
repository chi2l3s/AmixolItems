package me.chi2l3s.amixolitems.listeners;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.FarewellRumble;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import me.chi2l3s.amixolitems.utils.ItemCooldownManager;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class FarewellRumbleUse implements Listener {

    private final AmixolItems plugin;
    private final FarewellRumble farewellRumble;
    private BlackListedRegionBlocker regionBlocker;

    public FarewellRumbleUse(AmixolItems plugin,BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.farewellRumble = new FarewellRumble(plugin);
        this.regionBlocker = regionBlocker;
    }

    ItemCooldownManager cooldownManager = new ItemCooldownManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        ItemStack itemStack = e.getItem();
        ItemStack farewellrumble = farewellRumble.itemStack();
        Player player = e.getPlayer();
        if (itemStack == null) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
            String farewell_rumble = "farewellrumble";
            long cooldown = plugin.getConfig().getLong("farewellrumble.cooldown")*1000;
            cooldownManager.setItemCooldownTime(farewell_rumble,cooldown);
            if (itemStack.getType() == farewellrumble.getType() && itemStack.getItemMeta().equals(farewellrumble.getItemMeta())) {
                if (regionBlocker.isPlayerInBlacklistedRegion(player, "farewellrumble")) {
                    return;
                }
                World world = player.getWorld();
                String worldName = world.getName();
                List<String> blackListedWorlds = plugin.getConfig().getStringList("farewellrumble.black-listed-worlds");
                if (blackListedWorlds.contains(worldName)){
                    return;
                }
                if (!cooldownManager.checkCooldown(player, farewell_rumble)) {
                    long remainingTime = cooldownManager.getRemainingTime(player, farewell_rumble);
                    String message = plugin.getMessagesConfig().getString("cooldown");
                    if (message != null) {
                        message = message.replaceAll("%secondsLeft%", cooldownManager.formatTimeWithCorrectEnding(remainingTime / 1000));
                    }
                    player.sendMessage(ColorUtil.message(message));
                    return;
                }
                cooldownManager.setCooldown(player, farewell_rumble);
                player.getInventory().removeItem(farewellRumble.itemStack());
                Location playerLocation = player.getLocation();
                Sound sound = Sound.valueOf(plugin.getConfig().getString("farewellrumble.sound"));
                world.playSound(playerLocation, sound, 2.0f, 1.0f);
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof Player) {
                        Player otherPlayer = (Player) entity;
                        Location otherPlayerLocation = otherPlayer.getLocation();

                        Vector direction = otherPlayerLocation.subtract(playerLocation).toVector();
                        direction.normalize();
                        int power = plugin.getConfig().getInt("farewellrumble.power");
                        direction.multiply(power);

                        otherPlayer.setVelocity(direction);
                    }
                }
            }
        }
    }
}
