package me.chi2l3s.amixolitems.listeners;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.ExplosiveStick;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import me.chi2l3s.amixolitems.utils.ItemCooldownManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExplosiveStickListener implements Listener {

    private final AmixolItems plugin;
    private final ExplosiveStick explosiveStick;
    private BlackListedRegionBlocker regionBlocker;

    public ExplosiveStickListener(AmixolItems plugin, BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.explosiveStick = new ExplosiveStick(plugin);
        this.regionBlocker = regionBlocker;
    }

    ItemCooldownManager cooldownManager = new ItemCooldownManager();

    @EventHandler
    public void PlayerInteractListener(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player player = e.getPlayer();
            ItemStack itemStack = e.getItem();


            // Проверка на null
            if (itemStack == null) {
                return;
            }

            ItemStack fireBall = explosiveStick.itemStack();
            String fireStick = "fireStick";

            long cooldown = plugin.getConfig().getLong("explosivestick.cooldown")*1000;
            cooldownManager.setItemCooldownTime(fireStick,cooldown);

            if (itemStack.getType() == fireBall.getType() && itemStack.getItemMeta().equals(fireBall.getItemMeta())){
                if (regionBlocker.isPlayerInBlacklistedRegion(player, "explosivestick")) {
                    return; // Если игрок находится в запрещенном регионе, отменяем действие
                }
                World world = player.getWorld();
                String worldName = world.getName();
                List<String> blackListedWorlds = plugin.getConfig().getStringList("explosivestick.black-listed-worlds");
                if (blackListedWorlds.contains(worldName)){
                    return;
                }
                if (!cooldownManager.checkCooldown(player, fireStick)) {
                    long remainingTime = cooldownManager.getRemainingTime(player, fireStick);
                    String message = plugin.getMessagesConfig().getString("cooldown");
                    message = message.replaceAll("%secondsLeft%",cooldownManager.formatTimeWithCorrectEnding(remainingTime / 1000));
                    player.sendMessage(ColorUtil.message(message));
                    return;
                }
                cooldownManager.setCooldown(player, fireStick);
                Fireball fireballEntity = (Fireball) world.spawnEntity(player.getEyeLocation(), EntityType.FIREBALL);
                fireballEntity.setDirection(player.getLocation().getDirection());
                player.getInventory().removeItem(fireBall);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Fireball) {
            event.setYield(3.0f);
        }
    }
}