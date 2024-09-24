package me.captainpotatoaim.myplugin.custom_items

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack

class GrapplingHook : CustomItem(), Listener {

    override fun getItem(count: Int): ItemStack {
        val hook = ItemStack(Material.FISHING_ROD, count)
        val meta = checkNotNull(hook.itemMeta)
        meta.displayName(Component.text("Grappling hook"))
        meta.isUnbreakable = true
        hook.setItemMeta(meta)
        setType(hook, GrapplingHook::class.java)

        return hook
    }

    @EventHandler
    private fun thrown(event: PlayerFishEvent) {
        val state = event.state

        if (!(state == PlayerFishEvent.State.REEL_IN || state == PlayerFishEvent.State.IN_GROUND)) {
            return
        }

        val player = event.player

        if (!isOfType(player.inventory.itemInMainHand, GrapplingHook::class.java)) {
            return
        }

        val hookLocation = event.hook.location
        val playerLocation = player.location

        val launchDirection = (hookLocation.subtract(playerLocation)).toVector()
        player.velocity = launchDirection.multiply(0.5f)
    }
}