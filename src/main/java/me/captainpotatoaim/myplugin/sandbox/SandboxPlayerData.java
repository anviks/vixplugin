package me.captainpotatoaim.myplugin.sandbox;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SandboxPlayerData {

    final Player player;
    final GameMode gameMode;
    final Location location;
    final Location respawnPoint;
    final ItemStack[] inventory;
    final double health;
    final int hunger;
    final int starvationRate;
    final int experience;
    final ItemStack[] enderChest;

    public SandboxPlayerData(Player player) {

        this.player = player;
        this.gameMode = player.getGameMode();
        this.location = player.getLocation();
        this.respawnPoint = player.getBedSpawnLocation();
        this.inventory = Arrays.copyOf(player.getInventory().getContents(), player.getInventory().getContents().length);
        this.health = player.getHealth();
        this.hunger = player.getFoodLevel();
        this.starvationRate = player.getStarvationRate();
        this.experience = (player.getTotalExperience());
        this.enderChest = Arrays.copyOf(player.getEnderChest().getContents(), player.getEnderChest().getContents().length);

    }

    public void revertPlayerState() {

        // TODO: achievements and effects

        player.sendMessage(Arrays.toString(inventory));
        player.setGameMode(gameMode);
        player.teleport(location);
        player.setBedSpawnLocation(respawnPoint, true);
        player.getInventory().setContents(inventory);
        player.setHealth(health);
        player.setFoodLevel(hunger);
        player.setStarvationRate(starvationRate);
        player.setTotalExperience(experience);
        player.getEnderChest().setContents(enderChest);

    }
    
}
