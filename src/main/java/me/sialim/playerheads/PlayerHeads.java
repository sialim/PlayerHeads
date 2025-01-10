package me.sialim.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PlayerHeads extends JavaPlugin implements TabExecutor {
    private List<HeadEntry> headEntries = new ArrayList<>();

    @Override
    public void onEnable() {
        createDataFolder();
        loadHeadsFromFile("playerheads.txt");
        getCommand("starthead").setExecutor(this);
    }

    private void createDataFolder() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                getLogger().info("Data folder created.");
            } else {
                getLogger().warning("Failed to create data folder.");
            }
        }
    }

    private void loadHeadsFromFile(String fileName) {
        try {
            File file = new File(getDataFolder(), fileName);
            if (!file.exists()) {
                getLogger().warning("Heads file not found: " + fileName);
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int index = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String uuid = formatUUID(parts[2]);
                    headEntries.add(new HeadEntry(index, name, uuid));
                }
            }
            reader.close();
        } catch (Exception e) {
            getLogger().severe("Error reading heads file: " + e.getMessage());
        }
    }

    private String formatUUID(String uuid) {
        long msb = Long.parseLong(uuid.substring(0, 16), 16);
        long lsb = Long.parseLong(uuid.substring(16, 31), 16) * 0x10 + Long.parseLong(uuid.substring(31, 32), 16);

        long a = msb >> 32;
        long b = -(-msb & 0xFFFFFFFFL);
        long c = lsb >> 32;
        long d = lsb & 0xFFFFFFFFL;
        return "[I;" + a + "," + b + "," + c + "," + d + "]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /starthead <index>");
            return true;
        }

        try {
            int startIndex = Integer.parseInt(args[0]);
            int lastIndex = givePlayerHeads(player, startIndex);
            player.sendMessage(ChatColor.GREEN + "Heads given up to index: " + lastIndex);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid index provided.");
        }

        return true;
    }

    private int givePlayerHeads(Player player, int startIndex) {
        int lastIndex = startIndex;

        for (int i = startIndex - 1; i < headEntries.size(); i++) {
            HeadEntry entry = headEntries.get(i);

            String command = "/give @s minecraft:player_head[profile={id:" + entry.uuid + "}]";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            lastIndex = entry.index;
        }

        return lastIndex;
    }

    private static class HeadEntry {
        int index;
        String name;
        String uuid;

        HeadEntry(int index, String name, String uuid) {
            this.index = index;
            this.name = name;
            this.uuid = uuid;
        }
    }

}
