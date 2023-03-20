package me.captainpotatoaim.myplugin.sandbox;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Arrays;
import java.util.List;

public class Inventory implements Listener {

    @EventHandler
    public void onPlayerInvChange(InventoryOpenEvent event) {

        if (event.getInventory().equals(event.getPlayer().getEnderChest())) {

            event.getPlayer().sendMessage(Arrays.toString(SandboxJoinCommand.sandboxedPlayers.get(event.getPlayer().getUniqueId()).enderChest));


        }

    }


}
