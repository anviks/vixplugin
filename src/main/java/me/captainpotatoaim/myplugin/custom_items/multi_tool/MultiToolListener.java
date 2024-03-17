package me.captainpotatoaim.myplugin.custom_items.multi_tool;

import me.captainpotatoaim.myplugin.util.Tagger;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static org.bukkit.Material.*;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class MultiToolListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack eventItem = event.getItem();

        if (eventItem == null) {
            return;
        }

        String heldItemId = eventItem.getItemMeta().getPersistentDataContainer().get(Tagger.KEY, PersistentDataType.STRING);

        if (heldItemId == null || !heldItemId.equals(MultiTool.IDENTIFIER)) {
            return;
        }

        if (event.getAction() == LEFT_CLICK_BLOCK) {
            handleLeftClick(event, eventItem);
        } else if (event.getAction() == RIGHT_CLICK_BLOCK) {
            handleRightClick(event, eventItem);
        }
    }

    private void handleLeftClick(PlayerInteractEvent event, ItemStack tool) {
        Material blockMaterial = event.getClickedBlock().getType();

        if (Tag.MINEABLE_AXE.isTagged(blockMaterial)) {
            ensure(tool, DIAMOND_AXE);
        } else if (Tag.MINEABLE_PICKAXE.isTagged(blockMaterial)) {
            ensure(tool, DIAMOND_PICKAXE);
        } else if (Tag.MINEABLE_SHOVEL.isTagged(blockMaterial)) {
            ensure(tool, DIAMOND_SHOVEL);
        } else if (Tag.MINEABLE_HOE.isTagged(blockMaterial)) {
            ensure(tool, DIAMOND_HOE);
        }
    }

    private void handleRightClick(PlayerInteractEvent event, ItemStack itemStack) {

    }

    private void ensure(ItemStack tool, Material toolMaterial) {
        if (tool.getType() != toolMaterial) {
            tool.setType(toolMaterial);
        }
    }
}
