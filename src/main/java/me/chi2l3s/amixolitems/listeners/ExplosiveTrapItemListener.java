package me.chi2l3s.amixolitems.listeners;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.ExplosiveTrapItem;

import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import me.chi2l3s.amixolitems.utils.ItemCooldownManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;


public class ExplosiveTrapItemListener implements Listener {

    private final AmixolItems plugin;
    private final ExplosiveTrapItem explosiveTrapItem;
    private BlackListedRegionBlocker regionBlocker;

    public ExplosiveTrapItemListener(AmixolItems plugin, BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.explosiveTrapItem = new ExplosiveTrapItem(plugin);
        this.regionBlocker = regionBlocker;
    }

    ItemCooldownManager cooldownManager = new ItemCooldownManager();

    public static String message(String text) {
        if (text == null) {
            return ChatColor.RED + "Ошибка: сообщение отсутствует в файле конфигурации";
        }

        return text;
    }

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
    public void onPlayerClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack itemStack = e.getItem();
        ItemStack trap = explosiveTrapItem.itemStack();

        String trapId = "trap";
        long cooldown = plugin.getConfig().getLong("explosivetrapitem.cooldown")*1000;
        cooldownManager.setItemCooldownTime(trapId,cooldown);
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (itemStack != null && itemStack.getType() == trap.getType() && itemStack.getItemMeta().equals(trap.getItemMeta())) {
                if (regionBlocker.isPlayerInBlacklistedRegion(player, "explosivetrapitem")) {
                    return;
                }
                World world = player.getWorld();
                String worldName = world.getName();
                List<String> blackListedWorlds = plugin.getConfig().getStringList("explosivetrapitem.black-listed-worlds");
                if (blackListedWorlds.contains(worldName)){
                    return;
                }
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regionManager = container.get(BukkitAdapter.adapt(player.getWorld()));
                ApplicableRegionSet set = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
                if (!cooldownManager.checkCooldown(player, trapId)) {
                    long remainingTime = cooldownManager.getRemainingTime(player, trapId);
                    String message = plugin.getMessagesConfig().getString("cooldown");
                    message = message.replaceAll("%secondsLeft%",cooldownManager.formatTimeWithCorrectEnding(remainingTime / 1000));
                    player.sendMessage(ColorUtil.message(message));
                    player.playSound(player.getLocation(),Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF,1.0f,1.0f);
                    return;
                }
                for (ProtectedRegion region : set) {
                    if (region.getId().contains("-trapregion")) {
                        String messageError = plugin.getMessagesConfig().getString("busy-zone");
                        player.sendMessage(ColorUtil.message(messageError));
                        player.playSound(player.getLocation(),Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF,1.0f,1.0f);
                        return;
                    }
                }

                cooldownManager.setCooldown(player, trapId);

                player.getInventory().removeItem(trap);

                Sound sound = Sound.valueOf(plugin.getConfig().getString("explosivetrapitem.sound"));
                world.playSound(player.getLocation(), sound,3.0f,1.0f);

                Block blockUnderPlayer = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                Material materialUnderPlayer = blockUnderPlayer.getType();

                File schematic;
                long delay;
                long delayTrap = plugin.getConfig().getLong("explosivetrapitem.time")*40;

                List<String> schematics = plugin.getConfig().getStringList("explosivetrapitem.schematics");

                if (materialUnderPlayer == Material.AIR || materialUnderPlayer == Material.WATER || materialUnderPlayer == Material.LAVA) {
                    schematic = new File(plugin.getDataFolder(), "schematics/" + schematics.get(0));
                    delay = delayTrap;

                } else {
                    schematic = new File(plugin.getDataFolder(), "schematics/" + schematics.get(1));
                    delay = delayTrap-20;
                }
                if (!schematic.exists()){
                    player.sendMessage(ColorUtil.message(plugin.getMessagesConfig().getString("no-schematic")));
                    player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_PLACE,2.0f,1.0f);
                }

                ClipboardFormat format = ClipboardFormats.findByFile(schematic);
                try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
                    Clipboard clipboard = reader.read();
                    try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(player.getWorld()), -1)) {
                        Operation operation = new ClipboardHolder(clipboard)
                                .createPaste(editSession)
                                .to(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
                                .ignoreAirBlocks(false)
                                .build();

                        Operations.complete(operation); // Для FAWE

                        int radius = plugin.getConfig().getInt("explosivetrapitem.region.radius");

                        BlockVector3 minPoint = BlockVector3.at(player.getLocation().getX() - radius, player.getLocation().getY() - radius, player.getLocation().getZ() - radius);
                        BlockVector3 maxPoint = BlockVector3.at(player.getLocation().getX() + radius, player.getLocation().getY() + radius, player.getLocation().getZ() + radius);

                        String regionName = minPoint.getX() + "x-" + minPoint.getY() + "y-" + maxPoint.getZ() + "z_" + maxPoint.getX() + "x-" + maxPoint.getY() + "y-" + maxPoint.getZ() + "z-trapregion";
                        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, minPoint, maxPoint);

                        // Добавление региона
                        regionManager.addRegion(region);

                        List<String> flagList = plugin.getConfig().getStringList("explosivetrapitem.region.flags");
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

                        region.setPriority(1);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            for (Player playerIn : Bukkit.getOnlinePlayers()) {
                                if (region.contains(BukkitAdapter.asBlockVector(playerIn.getLocation()))) {
                                    Vector velocity = playerIn.getVelocity();
                                    double power = plugin.getConfig().getDouble("explosivetrapitem.force-throwing");
                                    velocity.setY(power);
                                    playerIn.setVelocity(velocity);
                                }
                            }
                        }, delay);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            regionManager.removeRegion(regionName);
                            for (BlockVector3 point : clipboard.getRegion()) {
                                editSession.undo(editSession);
                            }
                        }, delayTrap); // Задержка в тиках (40 тиков = 2 секунды)
                    }

                    } catch (WorldEditException ex) {
                        throw new RuntimeException(ex);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}