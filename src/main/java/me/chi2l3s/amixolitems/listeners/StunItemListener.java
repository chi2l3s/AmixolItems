package me.chi2l3s.amixolitems.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.StunItem;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import me.chi2l3s.amixolitems.utils.ItemCooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.List;

public class StunItemListener implements Listener {

    private final AmixolItems plugin;
    private final StunItem stunItem;
    private BlackListedRegionBlocker regionBlocker;

    public StunItemListener(AmixolItems plugin,BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.stunItem = new StunItem(plugin);
        this.regionBlocker = regionBlocker;
    }

    ItemCooldownManager cooldownManager = new ItemCooldownManager();

    public StateFlag getFlagByName(String name) {
        for (Field field : Flags.class.getFields()) {
            if (field.getType().equals(StateFlag.class) && field.getName().equalsIgnoreCase(name)) {
                try {
                    return (StateFlag) field.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        ItemStack item = e.getItem();
        Player player = e.getPlayer();
        ItemStack stun = stunItem.itemStack();
        String stunId = "stun";
        long cooldown = plugin.getConfig().getLong("stunitem.cooldown")*1000;
        cooldownManager.setItemCooldownTime(stunId,cooldown);
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (item != null && item.getType() == stun.getType() && item.getItemMeta().equals(stun.getItemMeta())) {
                if (regionBlocker.isPlayerInBlacklistedRegion(player, "snowManItem")) {
                    return;
                }
                World world = player.getWorld();
                String worldName = world.getName();
                List<String> blackListedWorlds = plugin.getConfig().getStringList("stunitem.black-listed-worlds");
                if (blackListedWorlds.contains(worldName)){
                    return;
                }
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
                ApplicableRegionSet set = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
                for (ProtectedRegion region : set) {
                    if (region.getId().contains("-stunregion")) {
                        String messageError = plugin.getMessagesConfig().getString("busy-zone");
                        player.sendMessage(ColorUtil.message(messageError));
                        player.playSound(player.getLocation(),Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF,1.0f,1.0f);
                        return;
                    }
                }
                if (!cooldownManager.checkCooldown(player, stunId)) {
                    long remainingTime = cooldownManager.getRemainingTime(player, stunId);
                    String message = plugin.getMessagesConfig().getString("cooldown");
                    if (message != null) {
                        message = message.replaceAll("%secondsLeft%", cooldownManager.formatTimeWithCorrectEnding(remainingTime / 1000));
                    }
                    player.sendMessage(ColorUtil.message(message));
                    return;
                }

                cooldownManager.setCooldown(player, stunId);
                player.getInventory().removeItem(stun);
                Location playerLocation = player.getLocation();
                String particle = plugin.getConfig().getString("stunitem.region.particle");
                /// Создание куба размером 30x30x30 и установка флагов
                int radius = plugin.getConfig().getInt("stunitem.region.radius");
                Location minPoint = playerLocation.clone().subtract(radius, radius, radius);
                Location maxPoint = playerLocation.clone().add(radius, radius, radius);
                String regionName = minPoint.getBlockX() + "x-" + minPoint.getBlockY() + "y-" + minPoint.getBlockZ() + "z_" + maxPoint.getBlockX() + "x-" + maxPoint.getBlockY() + "y-" + maxPoint.getBlockZ() + "z-stunregion";
                ProtectedRegion region = new ProtectedCuboidRegion(regionName,
                        BukkitAdapter.asBlockVector(minPoint),
                        BukkitAdapter.asBlockVector(maxPoint));
                WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).addRegion(region);

                String sound = plugin.getConfig().getString("stunitem.sound");
                world.playSound(playerLocation, Sound.valueOf(sound),0.8f,1.0f);
                // Отображение частиц по контуру региона
                int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
                        for (int y = minPoint.getBlockY(); y <= maxPoint.getBlockY(); y++) {
                            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                                if (x == minPoint.getBlockX() || x == maxPoint.getBlockX() || y == minPoint.getBlockY() || y == maxPoint.getBlockY() || z == minPoint.getBlockZ() || z == maxPoint.getBlockZ()) {
                                    player.getWorld().spawnParticle(Particle.valueOf(particle), x, y, z, 1);
                                    world.playSound(playerLocation, Sound.valueOf(sound),0.2f,1.0f);
                                }
                            }
                        }
                    }
                }, 0L, 20L);
                List<String> flagList = plugin.getConfig().getStringList("stunitem.region.flags");
                for (String flagSetting : flagList) {
                    String[] parts = flagSetting.split(";");
                    if (parts.length == 2) {
                        String flagName = parts[0];
                        String state = parts[1];
                        StateFlag flag = getFlagByName(flagName);
                        if (flag != null) {
                            if (state.equalsIgnoreCase("ALLOW")) {
                                region.setFlag(flag, StateFlag.State.ALLOW);
                            } else if (state.equalsIgnoreCase("DENY")) {
                                region.setFlag(flag, StateFlag.State.DENY);
                            }
                        }
                    }
                }
                DefaultDomain owners = new DefaultDomain();
                owners.addPlayer(player.getUniqueId());
                region.setOwners(owners);
                List<String> potionEffects = plugin.getConfig().getStringList("stunitem.region.potion-effects");
                for (String potionEffect : potionEffects) {
                    String[] parts = potionEffect.split(";");
                    if (parts.length == 3) {
                        String effectName = parts[0];
                        int duration = Integer.parseInt(parts[1]);
                        int amplifier = Integer.parseInt(parts[2]);
                        PotionEffectType effectType = PotionEffectType.getByName(effectName);
                        if (effectType != null) {
                            PotionEffect effect = new PotionEffect(effectType, duration, amplifier);
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (region.contains(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()) && !p.equals(player)) {
                                    p.addPotionEffect(effect);
                                }
                            }
                        }
                    }
                }

                // Удаление региона через 15 секунд
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Удаление эффектов зелья с игроков
                    Bukkit.getScheduler().cancelTask(taskId);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (region.contains(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ())) {
                            for (String potionEffect : potionEffects) {
                                String[] parts = potionEffect.split(";");
                                if (parts.length == 3) {
                                    String effectName = parts[0];
                                    PotionEffectType effectType = PotionEffectType.getByName(effectName);
                                    if (effectType != null) {
                                        p.removePotionEffect(effectType);
                                    }
                                }
                            }
                        }
                    }
                    // Удаление региона
                    WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld())).removeRegion(regionName);
                }, 20 * 15);
            }
        }
    }
}
