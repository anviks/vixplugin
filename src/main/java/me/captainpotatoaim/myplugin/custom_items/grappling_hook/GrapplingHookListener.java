package me.captainpotatoaim.myplugin.custom_items.grappling_hook;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import static org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.util.Vector;

public class GrapplingHookListener implements Listener {

    @EventHandler
    private void thrown(PlayerFishEvent event) {
        State state = event.getState();

        if (!(state.equals(State.REEL_IN) || state.equals(State.IN_GROUND))) {
            return;
        }

        Player player = event.getPlayer();

        if (!CustomItem.isOfType(player.getInventory().getItemInMainHand(), GrapplingHook.class)) {
            return;
        }

        Location hookLocation = event.getHook().getLocation();
        Location playerLocation = player.getLocation();

        Vector launchDirection = (hookLocation.subtract(playerLocation)).toVector();
        player.setVelocity(launchDirection.multiply(0.5f));
    }
}
