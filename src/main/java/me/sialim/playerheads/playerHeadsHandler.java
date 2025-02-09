package me.sialim.playerheads;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class playerHeadsHandler {
    private final List<PlayerHeadData> playerHeads = new ArrayList<>();

    public void loadPlayerHeads(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "playerheads.csv");
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
            plugin.getLogger().warning("Failed to load player heads from CSV file!");
            e.printStackTrace();
        }
    }

    public void givePlayerHeads(Player player, int startIndex,JavaPlugin plugin) {

        int lastIndex = startIndex;

        for (int i = startIndex; i < playerHeads.size(); i++) {
            if (player.getInventory().firstEmpty() == -1) break;  // No empty slots left
            PlayerHeadData headData = playerHeads.get(i);
            Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
            String texture=null;

            //String formattedUUID = formatUUID(headData.getUuid());
            //String giveCommand = "/give " + player.getName() + " minecraft:player_head[profile={id:" + formattedUUID + "}]";

            // Execute the give command
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), giveCommand);

            // Add lore to the head
            try{
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + headData.uuid);
                InputStreamReader reader_1 = new InputStreamReader(url.openStream());
                JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
                texture = textureProperty.get("value").getAsString();

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }


            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta != null) {
                try {
                    // 2) Get 'profile' field on the meta class
                    Class<?> skullMetaClass = meta.getClass();
                    Field profileField = skullMetaClass.getDeclaredField("profile");
                    profileField.setAccessible(true);

                    // 3) Construct a new GameProfile (reflection)
                    Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                    Constructor<?> gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
                    Object gameProfile = gameProfileConstructor.newInstance(UUID.randomUUID(), "");

                    // 4) Add the texture property to the profile
                    Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                    Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class);
                    Object textureProperty = propertyConstructor.newInstance("textures", texture);

                    // gameProfile.getProperties().put("textures", textureProperty)
                    Method getPropertiesMethod = gameProfileClass.getMethod("getProperties");
                    Object properties = getPropertiesMethod.invoke(gameProfile);
                    properties.getClass().getMethod("put", Object.class, Object.class)
                            .invoke(properties, "textures", textureProperty);

                    // 5) Assign that profile back into the skull meta
                    profileField.set(meta, gameProfile);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                meta.setDisplayName(headData.getName() + " #" + headData.getIndex());
                List<String> lore = new ArrayList<>();
                lore.add(headData.getName());
                lore.add("#" + headData.getIndex());
                meta.setLore(lore);
                head.setItemMeta(meta);
                player.getInventory().addItem(head);
            }
            });

            lastIndex = i;
        }

        player.sendMessage("Your inventory has been filled starting from index " + startIndex + " to " + lastIndex + "!");
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
