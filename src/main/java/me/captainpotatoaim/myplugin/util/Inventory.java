package me.captainpotatoaim.myplugin.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Inventory {
    public static boolean isShootableArrow(ItemStack arrow, Player player) {
        ItemStack shootableArrow = getShootableArrow(player).orElse(null);

        return arrow.isSimilar(shootableArrow);
    }

    public static Optional<ItemStack> getShootableArrow(Player player) {
        List<Material> arrowTypes = List.of(Material.ARROW, Material.SPECTRAL_ARROW, Material.TIPPED_ARROW);
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(itemStack -> arrowTypes.contains(itemStack.getType()))
                .findFirst();
    }
}
