package me.sialim.playerheads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PlayerHeads extends JavaPlugin {

    playerHeadsHandler playerHeadsHandler = new playerHeadsHandler();
    @Override
    public void onEnable() {
        getLogger().info("PlayerHeadsPlugin enabled!");
        playerHeadsHandler.loadPlayerHeads(this);
        //getCommand("starthead").setExecutor(new headCommand());
        Objects.requireNonNull(getCommand("starthead")).setExecutor((sender, command, label, args) -> {
                if (sender instanceof Player) {
                    if (args.length != 1) {
                        sender.sendMessage("Usage: /starthead <index>");
                        return false;
                    }

                    int index = Integer.parseInt(args[0]);
                    playerHeadsHandler.givePlayerHeads((Player) sender, index,this);
                }
            return true;
        });
    }


//    private String formatUUID(String uuid) {
//        int a = Integer.parseInt(uuid.substring(0, 7), 16) * 0x10 + Integer.parseInt(uuid.substring(7, 8), 16);
//        int b = Integer.parseInt(uuid.substring(8, 15), 16) * 0x10 + Integer.parseInt(uuid.substring(15, 16), 16);
//        int c = Integer.parseInt(uuid.substring(16, 23), 16) * 0x10 + Integer.parseInt(uuid.substring(23, 24), 16);
//        int d = Integer.parseInt(uuid.substring(24, 31), 16) * 0x10 + Integer.parseInt(uuid.substring(31, 32), 16);
//        return "[I;" + a + "," + b + "," + c + "," + d + "]";
//    }

}
