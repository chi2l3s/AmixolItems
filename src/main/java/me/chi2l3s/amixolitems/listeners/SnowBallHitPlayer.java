package me.chi2l3s.amixolitems.listeners;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.SlowingSnowBall;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import me.chi2l3s.amixolitems.utils.ItemCooldownManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SnowBallHitPlayer implements Listener {

    private final AmixolItems plugin;
    private final SlowingSnowBall slowingSnowBall;
    private BlackListedRegionBlocker regionBlocker;

    public SnowBallHitPlayer(AmixolItems plugin,BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.slowingSnowBall = new SlowingSnowBall(plugin);
        this.regionBlocker = regionBlocker;
    }

    ItemCooldownManager cooldownManager = new ItemCooldownManager();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        ItemStack snowballPre = slowingSnowBall.itemStack();

        if (item != null && item.getType() == Material.SNOWBALL) {
            if (item.isSimilar(snowballPre)){
                String snowBall = "snowBall";
                long cooldown = plugin.getConfig().getLong("slowingsnowball.cooldown")*1000;
                cooldownManager.setItemCooldownTime(snowBall,cooldown);
                if (regionBlocker.isPlayerInBlacklistedRegion(player, "slowingsnowball")) {
                    return;
                }
                World world = player.getWorld();
                String worldName = world.getName();
                List<String> blackListedWorlds = plugin.getConfig().getStringList("slowingsnowball.black-listed-worlds");
                if (blackListedWorlds.contains(worldName)){
                    return;
                }
                if (!cooldownManager.checkCooldown(player, snowBall)) {
                    long remainingTime = cooldownManager.getRemainingTime(player, snowBall);
                    String message = plugin.getMessagesConfig().getString("cooldown");
                    message = message.replaceAll("%secondsLeft%",cooldownManager.formatTimeWithCorrectEnding(remainingTime / 1000));
                    player.sendMessage(ColorUtil.message(message));
                    event.setCancelled(true);
                    return;
                }

                Snowball snowball = player.launchProjectile(Snowball.class);
                snowball.setCustomName(ColorUtil.message(plugin.getConfig().getString("slowingsnowball.display-name")));
                snowball.setCustomNameVisible(false);

                cooldownManager.setCooldown(player, snowBall);
            }
        }
    }

    @EventHandler
    public void onProjectileHitPlayer(ProjectileHitEvent e) {
        Entity projectile = e.getEntity();
        Entity target = e.getHitEntity();
        if (target instanceof Player player && projectile instanceof Snowball snowball) {
            if (snowball.getCustomName() != null && snowball.getCustomName().equalsIgnoreCase(ColorUtil.message(plugin.getConfig().getString("slowingsnowball.display-name")))){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 150,5));
            }
        }
    }
}
