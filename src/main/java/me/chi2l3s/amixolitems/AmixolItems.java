package me.chi2l3s.amixolitems;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.chi2l3s.amixolitems.commands.MainCommand;
import me.chi2l3s.amixolitems.listeners.*;
import me.chi2l3s.amixolitems.utils.BlackListedRegionBlocker;
import me.chi2l3s.amixolitems.utils.VanillaEventBlocker;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class AmixolItems extends JavaPlugin {

    private File messagesConfigFile;
    private FileConfiguration messagesConfig;
    public static boolean isFAWELoaded;



    @Override
    public void onEnable() {
        saveDefaultConfig();
        createMessagesConfig();

        File schematicsFolder = new File(getDataFolder(), "schematics");
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdir();
        }

        BlackListedRegionBlocker regionBlocker = new BlackListedRegionBlocker(this);


        getCommand("ai").setExecutor(new MainCommand(this));
        getServer().getPluginManager().registerEvents(new ExplosiveTrapItemListener(this,regionBlocker),this);
        getServer().getPluginManager().registerEvents(new JusticeItemEffectListener(this,regionBlocker),this);
        getServer().getPluginManager().registerEvents(new SnowBallHitPlayer(this,regionBlocker),this);
        getServer().getPluginManager().registerEvents(new ExplosiveStickListener(this,regionBlocker),this);
        getServer().getPluginManager().registerEvents(new FarewellRumbleUse(this,regionBlocker),this);
        getServer().getPluginManager().registerEvents(new StunItemListener(this,regionBlocker),this);
        getServer().getPluginManager().registerEvents(new GravitationItemListener(this,regionBlocker),this);
        getServer().getPluginManager().registerEvents(new PortHoleListener(this,regionBlocker),this);

        getServer().getPluginManager().registerEvents(new BlindnessItemListener(this,regionBlocker), this);

        getServer().getPluginManager().registerEvents(new VanillaEventBlocker(),this);
        getServer().getPluginManager().registerEvents(new SnowManItemListener(this,regionBlocker),this);
        if (getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") != null){
            getLogger().info("Трапки отключены! Скачайте WorldEdit вместо FAWE");
            getLogger().info("""
                    \s
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▒▒▒▒▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▒░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░░░░░▒░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░▒▒▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░▒▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▒▒░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░▒▓░░░░░▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▒░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░░░░▒▓▓▓░░░░░▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░░▓▓▓▓▓▓░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░
                    ░░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░
                    ░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▒░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▒░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▓▓▒░░░░░░░░░░░▒▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░▓▒░░░░░░░░▒▒▒▒▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░▒▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▒▒▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░
                    ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒░░░░░░░░░
                    ░███░░░░░▒██▒░░░░░███░░░░░░░░░░░░░░░░░░░░░░░██▓░░░░░░░░░░▓██▒░░███████████▒░░░░░░░░▓██▒░▓██▓░░░░░░░░
                    ░▓██▒░░░░███▓░░░░▒██▓░░░░░░░░░░░░░░░░░░░░░░░██▓░░░░░░░░░░▓██▒░░███▓▒▒▒▒▒▒▒░░░░░░░░░▓██▒░░▒▒░░░░██▓░░
                    ░░███░░░▒████▒░░░███░░░░▒▒▓▓▒▒░░░░▒▓▓▒▒▒▓▒▒░██▓░░░░░▒▓▓▒▒▓██▒░░███▒░░░░░░░░░░▒▒▓▓▒▒▓██▒░░▓▓▒░░▒██▓▒▒
                    ░░▓██▒░▒██▓███░░▒██▒░░░▓██▓▓▓██▓░░▓███▓▓▓▓▒░██▓░░░▓███▓▓████▒░░███▓▒▒▒▒▒▒░░░▓███▓▓████▒░▒██▒░░▓███▓▒
                    ░░░███░███▒▒███░███░░░▓██░░░░▒██▒░▓██▓░░░░░░██▓░░░███░░░░███▒░░██████████▒░░███░░░░███▒░▒██▒░░░██▓░░
                    ░░░▓██▓██▓░░▓██▓██▓░░░▓██░░░░▒██▒░▓██▒░░░░░░██▓░░▒███░░░░▓██▒░░███▒░░░░░░░░▒███░░░░▓██▒░▒██▒░░░██▓░░
                    ░░░░█████░░░▒█████░░░░▓██░░░░▒██▒░▓██▒░░░░░░██▓░░░███░░░░███▒░░███▒░░░░░░░░░███░░░░███▒░▒██▒░░░██▓░░
                    ░░░░▓███▒░░░░▓███▒░░░░░▓██▓▓███▓░░▓██▒░░░░░░▓██▓▓░▒███▓▓████▒░░████▓▓▓▓▓▓▓▒░▓███▓▓████▒░▒██▒░░░▓██▓▓
                    """);
            for (int i = 0;i<10;i++){
                getLogger().info("Трапки отключены! Скачайте WorldEdit вместо FAWE");
            }
            isFAWELoaded = true;
        }
    }


    private void createMessagesConfig() {
        messagesConfigFile = new File(getDataFolder(), "messages.yml");
        if (!messagesConfigFile.exists()) {
            messagesConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig = new YamlConfiguration();
        try {
            messagesConfig.load(messagesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    public void reloadMessagesConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        try {
            messagesConfig.load(messagesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin instanceof WorldEditPlugin) {
            return (WorldEditPlugin) plugin;
        } else {
            return null;
        }
    }

}
