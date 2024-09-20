package me.captainpotatoaim.myplugin.custom_items.multi_tool;

import me.captainpotatoaim.myplugin.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MultiToolListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack eventItem = event.getItem();

        if (eventItem == null) return;
        if (!CustomItem.isOfType(eventItem, MultiTool.class)) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            assert block != null;
            handleLeftClick(player, block, eventItem);
        }
    }

    private void handleLeftClick(Player player, Block clickedBlock, ItemStack tool) {
        Material blockMaterial = clickedBlock.getType();
        boolean isNetherite = tool.getType().toString().startsWith("NETHERITE_");
        Material toolForBlock = getToolForBlock(blockMaterial, isNetherite);
        ensureMaterial(player, tool, toolForBlock);
    }

    private Material getToolForBlock(Material blockMaterial, boolean isNetherite) {
        if (Tag.MINEABLE_AXE.isTagged(blockMaterial)) {
            return isNetherite ? Material.NETHERITE_AXE : Material.DIAMOND_AXE;
        } else if (Tag.MINEABLE_PICKAXE.isTagged(blockMaterial)) {
            return isNetherite ? Material.NETHERITE_PICKAXE : Material.DIAMOND_PICKAXE;
        } else if (Tag.MINEABLE_SHOVEL.isTagged(blockMaterial)) {
            return isNetherite ? Material.NETHERITE_SHOVEL : Material.DIAMOND_SHOVEL;
        } else if (Tag.MINEABLE_HOE.isTagged(blockMaterial)) {
            return isNetherite ? Material.NETHERITE_HOE : Material.DIAMOND_HOE;
        }

        return null;
    }

    private void ensureMaterial(Player player, ItemStack tool, Material toolMaterial) {
        if (tool.getType() != toolMaterial) {
            ItemStack newTool = new ItemStack(toolMaterial);
            newTool.setItemMeta(tool.getItemMeta());
            player.getInventory().setItemInMainHand(newTool);
        }
    }
}
