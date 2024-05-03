package me.chi2l3s.amixolitems.listeners;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.SnowManItem;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class SnowManItemListener implements Listener {

    private final AmixolItems plugin;
    private final SnowManItem snowManItem;
    private BlackListedRegionBlocker regionBlocker;

    public SnowManItemListener(AmixolItems plugin,BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.snowManItem = new SnowManItem(plugin);
        this.regionBlocker = regionBlocker;
    }

    @EventHandler
    public void onPlayerDamageListener(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        ItemStack itemStack = snowManItem.itemStack();
        if (damager instanceof Player player && target instanceof Player targetPlayer){
            int itemCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.isSimilar(itemStack)) {
                    itemCount += item.getAmount();
                }
            }
            if (itemCount >= 1) {
                if (player.getInventory().contains(itemStack)) {
                    if (regionBlocker.isPlayerInBlacklistedRegion(player, "snowManItem")) {
                        return;
                    }
                    World world = player.getWorld();
                    String worldName = world.getName();
                    List<String> blackListedWorlds = plugin.getConfig().getStringList("snowManItem.black-listed-worlds");
                    if (blackListedWorlds.contains(worldName)){
                        return;
                    }
                    Random rand = new Random();
                    int percentage = rand.nextInt(100);
                    int chance = plugin.getConfig().getInt("snowManItem.chance");
                    int time = plugin.getConfig().getInt("snowManItem.time") * 40;
                    if (percentage < chance) {
                        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, 1));
                        String actionbar = plugin.getConfig().getString("snowManItem.actionbar");
                        String message = ColorUtil.message(actionbar);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                    }
                }
            }
        }
    }
}
