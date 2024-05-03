package me.chi2l3s.amixolitems.listeners;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.GravitationItem;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GravitationItemListener implements Listener {

    private final AmixolItems plugin;
    private final GravitationItem gravitationItem;
    private BlackListedRegionBlocker regionBlocker;

    public GravitationItemListener(AmixolItems plugin,BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.gravitationItem = new GravitationItem(plugin);
        this.regionBlocker = regionBlocker;
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            int itemCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.isSimilar(gravitationItem.itemStack())) {
                    itemCount += item.getAmount();
                }
            }
            if (itemCount >= 1) {
                if (regionBlocker.isPlayerInBlacklistedRegion(player, "gravitationItem")) {
                    return;
                }
                World world = player.getWorld();
                String worldName = world.getName();
                List<String> blackListedWorlds = plugin.getConfig().getStringList("gravitationItem.black-listed-worlds");
                if (blackListedWorlds.contains(worldName)){
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    String subtitle = plugin.getConfig().getString("gravitationItem.actionbar");
                    String message = ColorUtil.message(subtitle);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(message));
                }
            }
        }
    }
}
