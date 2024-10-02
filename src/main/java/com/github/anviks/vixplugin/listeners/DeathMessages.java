package com.github.anviks.vixplugin.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.bukkit.Bukkit.getServer;

public class DeathMessages implements Listener {

    /**
     * Replace almost all minecraft's default death messages with custom ones.<br><br>
     * Unchanged messages:
     * <ul>
     *     <li>&lt;player&gt; was pummeled by &lt;player/mob&gt;</li>
     *     <li>&lt;player&gt; was pummeled by &lt;player/mob&gt; using &lt;item&gt;</li>
     *     <li>&lt;player&gt; was killed by [Intentional Game Design]</li>
     *     <li>death.fell.accident.water</li>
     *     <li>&lt;player&gt; went off with a bang due to a firework fired from &lt;item&gt; by &lt;player/mob&gt;</li>
     *     <li>&lt;player&gt; was doomed to fall</li>
     * </ul>
     *
     * @param event player's death event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String player = event.getEntity().getDisplayName();
        String killer = "";
        String weapon = "";
        String[] mobs = {"Spectral Arrow", "Arrow", "Bee", "Cave Spider", "Dolphin", "Enderman", "Goat", "Iron Golem", "Trader Llama", "Panda", "Zombified Piglin", "Polar Bear", "Spider", "Llama", "Wolf", "Piglin Brute", "Blaze", "Creeper", "Drowned", "Elder Guardian", "Endermite", "Evoker", "Ghast", "Guardian", "Hoglin", "Husk", "Magma Cube", "Phantom", "Piglin", "Pillager", "Ravager", "Shulker", "Silverfish", "Wither Skeleton", "Slime", "Stray", "Vex", "Vindicator", "Warden", "Witch", "Skeleton", "Zoglin", "Zombie Villager", "Zombie", "Ender Dragon", "Wither"};
        String msg = event.getDeathMessage().replaceFirst(player, "");

        if (event.getEntity().getKiller() != null) {
            killer = event.getEntity().getKiller().getDisplayName();
        } else {
            for (String mob : mobs) {
                if (msg.contains(mob)) {
                    killer = mob;
                    break;
                }
            }
        }

        if (msg.contains("[") && msg.contains("using")) {
            weapon = msg.substring(msg.indexOf(" using ") + 7);
            msg = msg.substring(0, msg.indexOf(" using ") + 7);
        }

        msg = msg.replaceFirst(killer, "");

        switch (msg) {
            case " was shot by " -> shotByEntity(event, player, killer);
            case " was shot by  using " -> shotByEntityHoldingNamedWeapon(event, player, killer, weapon);
            case " was pricked to death" -> killedByCactus(event, player);
            case " walked into a cactus whilst trying to escape " -> killedByCactusByEntity(event, player, killer);
            case " drowned" -> drowned(event, player);
            case " drowned whilst trying to escape " -> drownedByEntity(event, player, killer);
            case " experienced kinetic energy" -> flewAgainstSurface(event, player);
            case " experienced kinetic energy whilst trying to escape " ->
                    flewAgainstSurfaceByEntity(event, player, killer);
            case " blew up" -> blewUp(event, player);
            case " was blown up by ", " was blown up by  using " -> blownUpByEntity(event, player, killer);
            case " was blown up by Creeper" -> blownUpByCreeper(event, player, killer);
            case " hit the ground too hard" -> hitGround(event, player);
            case " hit the ground too hard whilst trying to escape " -> hitGroundByEntity(event, player, killer);
            case " fell from a high place" -> fell(event, player);
            case " fell off a ladder" -> fellLadder(event, player);
            case " fell off some vines" -> fellVines(event, player);
            case " fell off some weeping vines" -> fellWeepingVines(event, player);
            case " fell off some twisting vines" -> fellTwistingVines(event, player);
            case " fell off scaffolding" -> fellScaffolding(event, player);
            case " fell while climbing" -> fellClimbing(event, player);
            case " was impaled on a stalagmite" -> fellOnStalagmite(event, player);
            case " was impaled on a stalagmite whilst fighting " -> fellOnStalagmiteByEntity(event, player, killer);
            case " was squashed by a falling anvil" -> fallingAnvil(event, player);
            case " was squashed by a falling block" -> fallingBlock(event, player);
            case " was skewered by a falling stalactite" -> fallingStalactite(event, player);
            case " went up in flames" -> steppedInFire(event, player);
            case " walked into fire whilst fighting " -> steppedInFireByEntity(event, player, killer);
            case " burned to death" -> burned(event, player);
            case " was burnt to a crisp whilst fighting " -> burnedByEntity(event, player, killer);
            case " went off with a bang" -> fireworkDeath(event, player);
            case " tried to swim in lava" -> swamInLava(event, player);
            case " tried to swim in lava to escape " -> swamInLavaByEntity(event, player, killer, mobs);
            case " was struck by lightning" -> struckByLightning(event, player);
            case " was struck by lightning whilst fighting " -> struckByLightningByEntity(event, player, killer);
            case " discovered the floor was lava" -> magmaBlockDeath(event, player);
            case " walked into the danger zone due to " -> magmaBlockDeathByEntity(event, player, killer);
            case " was killed by magic" -> event.setDeathMessage("%s stopped existing thanks to some wizardry");
            default -> System.out.println("idk bro");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="methods">
    private static void shotByEntity(PlayerDeathEvent event, String player, @NotNull String killer) {
        if (killer.equals("Arrow") || killer.equals("Spectral Arrow")) {
            event.setDeathMessage("A random %s suddenly appeared out of nowhere and killed %s".formatted(killer, player));
        } else if (killer.equals(player)) {
            event.setDeathMessage("%s didn't want to live anymore".formatted(player));
        } else {
            event.setDeathMessage("%s was REALLY accurate and killed %s".formatted(killer, player));
        }
    }

    private static void shotByEntityHoldingNamedWeapon(PlayerDeathEvent event, String player, @NotNull String killer, String weapon) {
        if (killer.equals(player)) {
            event.setDeathMessage("%s used %s to shoot himself".formatted(player, weapon));
        } else {
            event.setDeathMessage("%s tested %s's dodging skills with his %s".formatted(killer, player, weapon));
        }
    }

    private static void killedByCactus(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("%s tried to hug a cactus".formatted(player));
    }

    private static void killedByCactusByEntity(@NotNull PlayerDeathEvent event, String player, @NotNull String killer) {
        String article;
        article = ("aeiouõäöüAEIOUÕÄÖÜ".contains(killer.substring(0, 1))) ? "an" : "a";
        event.setDeathMessage("\"I'd rather die to a cactus, than to %s %s\" - %s".formatted(article, killer, player));
    }

    private static void drowned(PlayerDeathEvent event, String player) {
        int randomNumber;
        randomNumber = (int) (Math.random() * 2);
        switch (randomNumber) {
            case 0 -> event.setDeathMessage(player + " thought they could extract the O₂ from H₂O using their lungs");
            case 1 -> event.setDeathMessage(player + " forgot their oxygen tank at home");
        }
    }

    private static void drownedByEntity(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage("%s went underwater to hide from %s, but was then reminded, that they aren't amphibious".formatted(player, killer));
    }

    private static void flewAgainstSurface(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " skipped his flight lessons");
    }

    private static void flewAgainstSurfaceByEntity(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage(killer + " made " + player + " panic and fly into a wall");
    }

    private static void blewUp(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("Rest In Pieces, " + player);
    }

    private static void blownUpByEntity(PlayerDeathEvent event, String player, @NotNull String killer) {
        if (killer.equals("Creeper")) {
            event.setDeathMessage(player + "'s last words were \"Oh, man!\"");
        } else {
            event.setDeathMessage(killer + " nuked " + player + " to the orbit");
        }
    }

    private static void blownUpByCreeper(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage(player + " was sacrificed to a Creeper by " + killer);
    }

    private static void hitGround(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " wasn't paying attention to his health when performing that stunt");
    }

    private static void hitGroundByEntity(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage(killer + " is dying of laughter because of how " + player + " just died");
    }

    private static void fell(PlayerDeathEvent event, String player) {
        if (getServer().getPlayer(player).getInventory().contains(new ItemStack(Material.WATER_BUCKET))) {
            event.setDeathMessage(player + " didn't know how to use their water bucket");
        } else {
            event.setDeathMessage(player + " didn't have a water bucket for some reason");
        }
    }

    private static void fellLadder(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " doesn't know how to use a ladder");
    }

    private static void fellVines(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("A vine was too slippery for " + player + " to climb it");
    }

    private static void fellWeepingVines(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("A weeping vine was too slippery for " + player + " to climb it");
    }

    private static void fellTwistingVines(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("A twisting vine was too slippery for " + player + " to climb it");
    }

    private static void fellScaffolding(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " didn't wear a safety helmet when on scaffolding");
    }

    private static void fellClimbing(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " started climbing on some random thing and fell to their death");
    }

    private static void fellOnStalagmite(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " became a shashlik");
    }

    private static void fellOnStalagmiteByEntity(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage("Thanks to " + killer + ", " + player + " now has a huge stalagmite in them.");
    }

    private static void fallingAnvil(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("Someone dropped an anvil on " + player + ", OUCH!");
    }

    private static void fallingBlock(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " lost his life to some falling block");
    }

    private static void fallingStalactite(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("Something sharp fell from the sky and took " + player + "'s life");
    }

    private static void steppedInFire(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage(player + " was cremated");
    }

    private static void steppedInFireByEntity(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage(killer + " made " + player + "-kebab");
    }

    private static void burned(PlayerDeathEvent event, String player) {
        if (getServer().getPlayer(player).getInventory().contains(new ItemStack(Material.WATER_BUCKET))) {
            event.setDeathMessage(player + " forgot that they had water in their inventory and burned to death");
        } else {
            event.setDeathMessage(player + " didn't find water soon enough and burned to death");
        }
    }

    private static void burnedByEntity(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage(killer + " lit up " + player);
    }

    private static void fireworkDeath(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("After a firework didn't go off, " + player + " stuck their head in to see what's wrong");
    }

    private static void swamInLava(@NotNull PlayerDeathEvent event, String player) {
        if (event.getKeepInventory()) {
            event.setDeathMessage("Thank god keepInventory is enabled, because " + player + "just drowned in lava");
        }
        if (getServer().getPlayer(player).getInventory().isEmpty()) {
            event.setDeathMessage(player + " emptied their inventory before going to swim in lava, smart");
        } else {
            event.setDeathMessage(player + " suddenly found themselves in lava, F");
        }
    }

    private static void swamInLavaByEntity(PlayerDeathEvent event, String player, String killer, String[] mobs) {
        if (!Arrays.asList(mobs).contains(killer)) {
            event.setDeathMessage(killer + " just pushed " + player + " in lava, be careful around them");
        } else {
            event.setDeathMessage(player + " tried to run away from " + killer + " but ended up inhaling lava");
        }
    }
    // </editor-fold>

    private static void struckByLightning(@NotNull PlayerDeathEvent event, String player) {
        event.setDeathMessage("%s got a shocking revelation from the heavens".formatted(player));
    }

    private static void struckByLightningByEntity(@NotNull PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage("%s got caught in the middle of a high-voltage showdown with %s and ended up fried by lightning."
                .formatted(player, killer));
    }

    private static void magmaBlockDeath(PlayerDeathEvent event, String player) {
        event.setDeathMessage("%s got roasted like a marshmallow on a sizzling magma block"
                .formatted(player));
    }

    private static void magmaBlockDeathByEntity(PlayerDeathEvent event, String player, String killer) {
        event.setDeathMessage("%s engaged in a fierce battle with %s, but the ultimate victor turned out to be the magma block"
                .formatted(player, killer));
    }
}
