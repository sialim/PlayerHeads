package me.sialim.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PlayerHeads extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("PlayerHeadsPlugin enabled!");
        getCommand("starthead").setExecutor(new headCommand(this));
    }


//    private String formatUUID(String uuid) {
//        int a = Integer.parseInt(uuid.substring(0, 7), 16) * 0x10 + Integer.parseInt(uuid.substring(7, 8), 16);
//        int b = Integer.parseInt(uuid.substring(8, 15), 16) * 0x10 + Integer.parseInt(uuid.substring(15, 16), 16);
//        int c = Integer.parseInt(uuid.substring(16, 23), 16) * 0x10 + Integer.parseInt(uuid.substring(23, 24), 16);
//        int d = Integer.parseInt(uuid.substring(24, 31), 16) * 0x10 + Integer.parseInt(uuid.substring(31, 32), 16);
//        return "[I;" + a + "," + b + "," + c + "," + d + "]";
//    }

}
