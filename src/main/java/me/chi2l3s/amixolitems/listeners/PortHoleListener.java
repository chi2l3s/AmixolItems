package me.chi2l3s.amixolitems.listeners;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.PortHoleItem;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class PortHoleListener implements Listener {

    private final AmixolItems plugin;
    private final PortHoleItem portHoleItem;
    private BlackListedRegionBlocker regionBlocker;

    public PortHoleListener(AmixolItems plugin,BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.portHoleItem = new PortHoleItem(plugin);
        this.regionBlocker = regionBlocker;
    }

    @EventHandler
    public void onPlayerDamageListener(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        ItemStack itemStack = portHoleItem.itemStack();
        if (damager instanceof Player player && target instanceof Player targetPlayer){
            int itemCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.isSimilar(itemStack)) {
                    itemCount += item.getAmount();
                }
            }
            if (itemCount >= 1) {
                if (player.getInventory().contains(itemStack)) {
                    if (regionBlocker.isPlayerInBlacklistedRegion(player, "portHoleItem")) {
                        return;
                    }
                    World world = player.getWorld();
                    String worldName = world.getName();
                    List<String> blackListedWorlds = plugin.getConfig().getStringList("portHoleItem.black-listed-worlds");
                    if (blackListedWorlds.contains(worldName)){
                        return;
                    }
                    Random rand = new Random();
                    int percentage = rand.nextInt(100);
                    int chance = plugin.getConfig().getInt("portHoleItem.chance");
                    int time = plugin.getConfig().getInt("portHoleItem.time") * 40;

                    if (percentage < chance) {
                        boolean bedrockSupport = plugin.getConfig().getBoolean("portHoleItem.bedrockSupport.enable");
                        if (bedrockSupport && targetPlayer.getInventory().getChestplate() == null && targetPlayer.getInventory().getBoots() == null && targetPlayer.getInventory().getHelmet() == null && targetPlayer.getInventory().getLeggings() == null) {
                            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

                            String hexColor = plugin.getConfig().getString("portHoleItem.armorColor");
                            int red = Integer.valueOf(hexColor.substring(1, 3), 16);
                            int green = Integer.valueOf(hexColor.substring(3, 5), 16);
                            int blue = Integer.valueOf(hexColor.substring(5, 7), 16);


                            LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
                            meta.setColor(Color.fromRGB(red, green, blue));
                            helmet.setItemMeta(meta);
                            chestplate.setItemMeta(meta);
                            leggings.setItemMeta(meta);
                            boots.setItemMeta(meta);

                            targetPlayer.getInventory().setHelmet(helmet);
                            targetPlayer.getInventory().setChestplate(chestplate);
                            targetPlayer.getInventory().setLeggings(leggings);
                            targetPlayer.getInventory().setBoots(boots);

                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                targetPlayer.getInventory().setHelmet(null);
                                targetPlayer.getInventory().setChestplate(null);
                                targetPlayer.getInventory().setLeggings(null);
                                targetPlayer.getInventory().setBoots(null);
                            }, time);
                        } else if (!bedrockSupport) {
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, time, 1));
                        }
                        String actionbar = plugin.getConfig().getString("portHoleItem.actionbar");
                        String message = ColorUtil.message(actionbar);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                    }

                }
            }
        }
    }


}
