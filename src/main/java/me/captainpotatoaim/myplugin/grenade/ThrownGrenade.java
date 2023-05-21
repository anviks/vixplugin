package me.captainpotatoaim.myplugin.grenade;

import me.captainpotatoaim.myplugin.Initializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.HashMap;
import java.util.UUID;

public class ThrownGrenade implements Listener {
    public HashMap<UUID, Integer> liveGrenades = new HashMap<>();

//    public ThrownGrenade() {
//        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> onEvent(event), EventPriority.NORMAL, JavaPlugin.getPlugin(Initializer.class), false);
//        for (HandlerList handler : HandlerList.getHandlerLists())
//            handler.register(registeredListener);
//    }
//
//    public void onEvent(Event event) {
//        if (event instanceof BroadcastMessageEvent || event instanceof GenericGameEvent || event instanceof StriderTemperatureChangeEvent) {
//            return;
//        }
//        Bukkit.broadcastMessage(event.getEventName());
//    }

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event) {
        Item itemDrop = event.getItemDrop();

        if (itemDrop.getItemStack().isSimilar(GrenadeItem.grenadeItem(1))) {
            Runnable task = () -> event.getPlayer().getWorld().createExplosion(itemDrop.getLocation(), 10);
            UUID uuid = itemDrop.getUniqueId();
            int taskId = Bukkit.getScheduler()
                    .runTaskLater(Initializer.plugin, task, 100).getTaskId();

            liveGrenades.put(uuid, taskId);
        }
    }

    @EventHandler
    public void onItemPicked(EntityPickupItemEvent event) {
        Item item = event.getItem();
        if (item.getItemStack().isSimilar(GrenadeItem.grenadeItem(1))) {
            Bukkit.getScheduler().cancelTask(liveGrenades.get(item.getUniqueId()));
        }
    }

    @EventHandler
    public void onGrenadeDestruction(ItemDespawnEvent event) {

    }

    @EventHandler
    public void onGrenadeDestructionByCactus(EntityDamageByBlockEvent event) {

    }

    @EventHandler
    public void onXPBottleThrown(ExpBottleEvent event) {
        if (event.getEntity().getItem().isSimilar(GrenadeItem.grenadeItem(1))) {
            event.setExperience(0);
            event.setShowEffect(false);
            event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 5);
        }
    }
}
