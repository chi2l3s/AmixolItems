package me.chi2l3s.amixolitems.items;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import me.chi2l3s.amixolitems.utils.HeadDbUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class JusticeItem {

    private final AmixolItems plugin;

    public JusticeItem(AmixolItems plugin) {
        this.plugin = plugin;
    }

    public ItemStack itemStack(){
        String materialString = plugin.getConfig().getString("justiceItem.material");
        String name = plugin.getConfig().getString(ColorUtil.message(ChatColor.translateAlternateColorCodes('&',"justiceItem.display-name")));
        List<String> lore = plugin.getConfig().getStringList("justiceItem.lore");
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(ColorUtil.message(ChatColor.translateAlternateColorCodes('&', line)));
        }

        ItemStack itemStack;
        if (materialString.startsWith("basehead:")) {
            String base64 = materialString.substring(9); // Получаем Base64 текстуру головы
            itemStack = HeadDbUtil.createHead(base64);
        } else {
            Material material = Material.valueOf(materialString);
            itemStack = new ItemStack(material, 1);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorUtil.message(ChatColor.translateAlternateColorCodes('&',name)));
        itemMeta.setLore(coloredLore);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean("AmixolCustomItem", true);

        // Возвращаем предмет с NBT-тегом
        return nbtItem.getItem();
    }

}
