package me.sialim.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.*;

public final class PlayerHeads extends JavaPlugin {
    private List<PlayerHeadData> playerHeads = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("PlayerHeadsPlugin enabled!");
        loadPlayerHeads();
        getCommand("starthead").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                if (args.length != 1) {
                    sender.sendMessage("Usage: /starthead <index>");
                    return false;
                }

                int index = Integer.parseInt(args[0]);
                givePlayerHeads((Player) sender, index);
                return true;
            }
            return false;
        });
    }

    private void loadPlayerHeads() {
        File file = new File(getDataFolder(), "playerheads.csv");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[1];
                    String uuid = parts[2];
                    playerHeads.add(new PlayerHeadData(Integer.parseInt(parts[0]), name, uuid));
                }
            }
        } catch (IOException e) {
            getLogger().warning("Failed to load player heads from CSV file!");
            e.printStackTrace();
        }
    }

    private void givePlayerHeads(Player player, int startIndex) {
        int lastIndex = startIndex;

        for (int i = startIndex; i < playerHeads.size(); i++) {
            PlayerHeadData headData = playerHeads.get(i);
            if (player.getInventory().firstEmpty() == -1) break;  // No empty slots left

            String formattedUUID = formatUUID(headData.getUuid());
            String giveCommand = "/give " + player.getName() + " minecraft:player_head[profile={id:" + formattedUUID + "}]";

            // Execute the give command
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), giveCommand);

            // Add lore to the head
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(headData.getName() + " #" + headData.getIndex());
                List<String> lore = new ArrayList<>();
                lore.add(headData.getName()); // Line 1: Name from CSV
                lore.add("#" + headData.getIndex()); // Line 2: Index from CSV
                meta.setLore(lore);
                head.setItemMeta(meta);
                player.getInventory().addItem(head);
            }

            lastIndex = i;
        }

        player.sendMessage("Your inventory has been filled starting from index " + startIndex + " to " + lastIndex + "!");
    }

    private String formatUUID(String uuid) {
        int a = Integer.parseInt(uuid.substring(0, 7), 16) * 0x10 + Integer.parseInt(uuid.substring(7, 8), 16);
        int b = Integer.parseInt(uuid.substring(8, 15), 16) * 0x10 + Integer.parseInt(uuid.substring(15, 16), 16);
        int c = Integer.parseInt(uuid.substring(16, 23), 16) * 0x10 + Integer.parseInt(uuid.substring(23, 24), 16);
        int d = Integer.parseInt(uuid.substring(24, 31), 16) * 0x10 + Integer.parseInt(uuid.substring(31, 32), 16);
        return "[I;" + a + "," + b + "," + c + "," + d + "]";
    }

    private static class PlayerHeadData {
        private final int index;
        private final String name;
        private final String uuid;

        public PlayerHeadData(int index, String name, String uuid) {
            this.index = index;
            this.name = name;
            this.uuid = uuid;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getUuid() {
            return uuid;
        }
    }
}
