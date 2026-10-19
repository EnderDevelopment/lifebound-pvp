package com.enderdevelopment.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin implements Listener {
    private Map<Player, Integer> playerLives = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("life")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                int lives = playerLives.getOrDefault(player, 3);
                player.sendMessage("You have " + lives + " lives left.");
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("spectate")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    Player target = getServer().getPlayer(args[0]);
                    if (target != null && playerLives.getOrDefault(target, 3) > 0) {
                        player.teleport(target.getLocation());
                        player.sendMessage("Teleported to " + target.getName());
                        return true;
                    } else {
                        player.sendMessage("Player not found or is not alive.");
                    }
                } else {
                    player.sendMessage("Usage: /spectate <player>");
                }
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!playerLives.containsKey(player)) {
            playerLives.put(player, 3);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        int lives = playerLives.getOrDefault(player, 3);
        lives--;
        playerLives.put(player, lives);
        if (lives <= 0) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("You have no lives left. You are now in spectator mode.");
        } else {
            player.sendMessage("You have " + lives + " lives left.");
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (playerLives.getOrDefault(player, 3) <= 0) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
}