package me.captainpotatoaim.myplugin.custom_items.grenade;

import me.captainpotatoaim.myplugin.Initializer;
import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

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

        if (CustomItem.isOfType(itemDrop.getItemStack(), Grenade.class)) {
            Runnable task = () -> event.getPlayer().getWorld().createExplosion(itemDrop.getLocation(), 10);
            UUID uuid = itemDrop.getUniqueId();
            int taskId = Bukkit.getScheduler()
                    .runTaskLater(Initializer.plugin, task, 100)
                    .getTaskId();

            liveGrenades.put(uuid, taskId);
        }
    }

    @EventHandler
    public void onItemPicked(EntityPickupItemEvent event) {
        Item item = event.getItem();

        if (CustomItem.isOfType(item.getItemStack(), Grenade.class)) {
            this.tryCancelGrenadeExplosion(item);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item) {
            ItemStack itemStack = item.getItemStack();
            if (CustomItem.isOfType(itemStack, Grenade.class)) {
                Bukkit.getScheduler().runTaskLater(Initializer.plugin, () -> {
                    if (item.isDead()) {
                        this.tryCancelGrenadeExplosion(item);
                    }
                }, 1);
            }
        }
    }

    @EventHandler
    public void onXPBottleThrown(ExpBottleEvent event) {
        if (CustomItem.isOfType(event.getEntity().getItem(), Grenade.class)) {
            event.setExperience(0);
            event.setShowEffect(false);
            event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 5);
        }
    }

    private void tryCancelGrenadeExplosion(Item item) {
        UUID itemId = item.getUniqueId();
        if (liveGrenades.containsKey(itemId)) {
            Bukkit.getScheduler().cancelTask(liveGrenades.get(itemId));
            liveGrenades.remove(itemId);
        }
    }
}
