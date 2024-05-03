package me.chi2l3s.amixolitems.listeners;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.JusticeItem;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class JusticeItemEffectListener implements Listener {

    private final AmixolItems plugin;
    private final JusticeItem justiceItem;
    private BlackListedRegionBlocker regionBlocker;

    public JusticeItemEffectListener(AmixolItems plugin,BlackListedRegionBlocker regionBlocker) {
        this.plugin = plugin;
        this.justiceItem = new JusticeItem(plugin);
        this.regionBlocker = regionBlocker;
    }

    private boolean processingEvent = false;

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event){
        if (processingEvent) {
            return;
        }
        List<String> disabledEffects = plugin.getConfig().getStringList("justiceItem.disabled-effects");
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            int itemCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.isSimilar(justiceItem.itemStack())) {
                    itemCount += item.getAmount();
                }
            }
            if (itemCount >= 1) {
                if (player.getInventory().contains(justiceItem.itemStack())) {
                    if (regionBlocker.isPlayerInBlacklistedRegion(player, "justiceItem")) {
                        return;
                    }
                    World world = player.getWorld();
                    String worldName = world.getName();
                    List<String> blackListedWorlds = plugin.getConfig().getStringList("justiceItem.black-listed-worlds");
                    if (blackListedWorlds.contains(worldName)){
                        return;
                    }
                    if (event.getNewEffect() != null){
                        PotionEffectType effectType = event.getNewEffect().getType();
                        // Проверяем, является ли эффект одним из тех, которые мы хотим отменить
                        if (disabledEffects.contains(effectType.getName())) {
                            event.setCancelled(true);
                            String actionBar = plugin.getConfig().getString("justiceItem.actionbar");
                            String message = ColorUtil.message(actionBar);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));

                            // Если зелье - это зелье черепащьей мощи, применяем эффекты сопротивления и замедления вручную
                            if (plugin.getConfig().getBoolean("justiceItem.exceptionForTurtlePowerPotion")){
                                if (effectType.equals(PotionEffectType.SLOW) || effectType.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, event.getNewEffect().getDuration(), event.getNewEffect().getAmplifier()));
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, event.getNewEffect().getDuration(), event.getNewEffect().getAmplifier()));
                                }
                            }
                            processingEvent = true;
                            processingEvent = false;
                        }
                    }
                }
            }
        }
    }
}
