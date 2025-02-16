package me.sialim.playerheads;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class headCommand implements TabExecutor, CommandExecutor {

    private final JavaPlugin plugin;
    private final playerHeadsHandler playerHeadsHandler;
    public headCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        playerHeadsHandler = new playerHeadsHandler();
        playerHeadsHandler.loadPlayerHeads(plugin);
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false; // null is not parseable
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            if (args.length == 0) {
                commandSender.sendMessage("Usage: /starthead <index>");
                return false;
            }
            int index = Integer.parseInt(args[0]);
            plugin.getLogger().info(String.valueOf(index));


            playerHeadsHandler.givePlayerHeads((Player) commandSender, index,plugin);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> possible = new java.util.ArrayList<>(List.of());
        for(int i=0;i< playerHeadsHandler.getPlayerHeadsCount();i++)
        {
            me.sialim.playerheads.playerHeadsHandler.PlayerHeadData PlayerHead=playerHeadsHandler.getPlayerHeadData(i);
            String name=PlayerHead.getName();
            int indexPlayer=PlayerHead.getIndex();
            if(name.toLowerCase().contains(args[0].toLowerCase()))
            {
                possible.add(indexPlayer + " - " +name);
            }
            if(isInteger(args[0]))
            {
                if(indexPlayer==Integer.parseInt(args[0]))
                {
                    possible.add(indexPlayer + " - "+name);
                }
            }
        }
        return possible;
    }

}
