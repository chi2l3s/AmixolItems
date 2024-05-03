package me.chi2l3s.amixolitems.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.chi2l3s.amixolitems.AmixolItems;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BlackListedRegionBlocker {

    private final AmixolItems plugin;

    public BlackListedRegionBlocker(AmixolItems plugin) {
        this.plugin = plugin;
    }

    public boolean isPlayerInBlacklistedRegion(Player player, String itemName) {
        Plugin worldGuardPlugin = player.getServer().getPluginManager().getPlugin("WorldGuard");

        if (worldGuardPlugin == null || !(worldGuardPlugin instanceof WorldGuardPlugin)) {
            return false; // WorldGuard is not present
        }

        List<String> blacklistedRegions = plugin.getConfig().getStringList(itemName + ".black-listed-regions");
        WorldGuardPlugin worldGuard = (WorldGuardPlugin) worldGuardPlugin;
        Location location = player.getLocation(); // Замените на актуальное местоположение
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));
        BlockVector3 playerVector = BukkitAdapter.asBlockVector(player.getLocation());
        ApplicableRegionSet set = regionManager.getApplicableRegions(playerVector);

        for (ProtectedRegion region : set) {
            String regionName = region.getId();
            if (blacklistedRegions.contains(regionName)) {
                return true;
            }
        }


        return false;
    }

}
