package me.chi2l3s.amixolitems.commands;

import me.chi2l3s.amixolitems.AmixolItems;
import me.chi2l3s.amixolitems.items.*;
import me.chi2l3s.amixolitems.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final AmixolItems plugin;
    private final ExplosiveTrapItem explosiveTrapItem;
    private final SlowingSnowBall slowingSnowBall;
    private final ExplosiveStick explosiveStick;
    private final FarewellRumble farewellRumble;
    private final StunItem stunItem;
    private final GravitationItem gravitationItem;
    private final JusticeItem justiceItem;
    private final PortHoleItem portHoleItem;
    private final BlindnessItem blindnessItem;
    private final SnowManItem snowManItem;

    public MainCommand(AmixolItems plugin) {
        this.plugin = plugin;
        this.explosiveTrapItem = new ExplosiveTrapItem(plugin);
        this.slowingSnowBall = new SlowingSnowBall(plugin);
        this.explosiveStick = new ExplosiveStick(plugin);
        this.farewellRumble = new FarewellRumble(plugin);
        this.stunItem = new StunItem(plugin);
        this.gravitationItem = new GravitationItem(plugin);
        this.justiceItem = new JusticeItem(plugin);
        this.portHoleItem = new PortHoleItem(plugin);
        this.blindnessItem = new BlindnessItem(plugin);
        this.snowManItem = new SnowManItem(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("amixolitems.give")){
            if (args.length > 0){
                if (args[0].equalsIgnoreCase("give")){
                    if (args.length < 4){
                        List<String> noArgsList = plugin.getMessagesConfig().getStringList("no-args");
                        for (String noArgsMessage: noArgsList){
                            String noArgs = ColorUtil.message(noArgsMessage);
                            sender.sendMessage(noArgs);
                        }
                    }
                    String playerName = args[2];
                    Player player = Bukkit.getPlayerExact(playerName);
                    if (player != null){
                        int amount = Integer.parseInt(args[3]);
                        if (args[1].equalsIgnoreCase("explosivetrapsitem")){
                            if (!AmixolItems.isFAWELoaded){
                                for (int i = 0;i<amount;i++) {
                                    player.getInventory().addItem(explosiveTrapItem.itemStack());
                                }
                            }else{
                                sender.sendMessage(ChatColor.RED + "Трапки не поддерживают зависимость от FAWE, пожалуйста установить WorldEdit для полной работоспосбности плагина.");
                                sender.sendMessage(ChatColor.GREEN + "https://dev.bukkit.org/projects/worldedit");
                            }
                        }else if (args[1].equalsIgnoreCase("slowingsnowball")){
                            for (int i = 0;i<amount;i++){
                                player.getInventory().addItem(slowingSnowBall.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("explosivestick")) {
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(explosiveStick.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("farewellrumble")){
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(farewellRumble.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("stunitem")){
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(stunItem.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("gravitationItem")){
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(gravitationItem.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("justiceItem")){
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(justiceItem.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("portHoleItem")){
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(portHoleItem.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("blindnessItem")){
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(blindnessItem.itemStack());
                            }
                        }else if (args[1].equalsIgnoreCase("snowManItem")){
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().addItem(snowManItem.itemStack());
                            }
                        }
                    }else{
                        String unknownPlayer = plugin.getMessagesConfig().getString("unknown-player");
                        sender.sendMessage(unknownPlayer);
                    }
                }else if (args[0].equalsIgnoreCase("reload")){
                    plugin.reloadConfig();
                    plugin.reloadMessagesConfig();
                    sender.sendMessage(ColorUtil.message(plugin.getMessagesConfig().getString("config-reload")));
                }else{
                    List<String> noArgsList = plugin.getMessagesConfig().getStringList("no-args");
                    for (String noArgsMessage: noArgsList){
                        String noArgs = ColorUtil.message(noArgsMessage);
                        sender.sendMessage(noArgs);
                    }
                }
            }
        }else {
            sender.sendMessage(ColorUtil.message(plugin.getMessagesConfig().getString("no-permission")));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Arrays.asList("explosivetrapsitem", "slowingsnowball", "explosivestick", "farewellrumble", "stunitem", "gravitationItem", "justiceItem", "portHoleItem", "blindnessItem", "snowManItem");
        } else {
            return null;
        }
    }
}
