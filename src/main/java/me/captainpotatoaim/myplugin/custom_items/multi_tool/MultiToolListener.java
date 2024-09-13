package me.captainpotatoaim.myplugin.custom_items.multi_tool;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Material.*;

public class MultiToolListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack eventItem = event.getItem();

        if (eventItem == null) {
            return;
        }

        if (!CustomItem.isOfType(eventItem, MultiTool.class)) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            handleLeftClick(event, eventItem);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleRightClick(event, eventItem);
        }
    }

    private void handleLeftClick(PlayerInteractEvent event, ItemStack tool) {
        Material blockMaterial = event.getClickedBlock().getType();

        if (Tag.MINEABLE_AXE.isTagged(blockMaterial)) {
            ensureMaterial(tool, DIAMOND_AXE);
        } else if (Tag.MINEABLE_PICKAXE.isTagged(blockMaterial)) {
            ensureMaterial(tool, DIAMOND_PICKAXE);
        } else if (Tag.MINEABLE_SHOVEL.isTagged(blockMaterial)) {
            ensureMaterial(tool, DIAMOND_SHOVEL);
        } else if (Tag.MINEABLE_HOE.isTagged(blockMaterial)) {
            ensureMaterial(tool, DIAMOND_HOE);
        }
    }

    private void handleRightClick(PlayerInteractEvent event, ItemStack itemStack) {

    }

    private void ensureMaterial(ItemStack tool, Material toolMaterial) {
        if (tool.getType() != toolMaterial) {
            tool.setType(toolMaterial);
        }
    }
}
