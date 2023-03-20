package me.captainpotatoaim.myplugin.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static org.bukkit.Bukkit.getServer;

public class DeathMessages implements Listener {

    /**
     * Replace almost all minecraft's default death messages with custom ones.
     * <p>
     * Unchanged messages:
     * <p>
     * <player> was pummeled by <player/mob>
     * <player> was pummeled by <player/mob> using <item>
     * <player> was killed by [Intentional Game Design]
     * death.fell.accident.water
     * <player> went off with a bang due to a firework fired from <item> by <player/mob>
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

        int randomNumber;
        switch (msg) {
            case " was shot by " -> {
                if (killer.equals("Arrow") || killer.equals("Spectral Arrow")) {
                    event.setDeathMessage("A random " + killer + " suddenly appeared out of nowhere and killed " + player);
                } else if (killer.equals(player)) {
                    event.setDeathMessage(player + " didn't want to live anymore");
                } else {
                    event.setDeathMessage(killer + " was REALLY accurate and killed " + player);
                }
            }
            case " was shot by  using " -> {
                if (killer.equals(player)) {
                    event.setDeathMessage(player + " used " + weapon + " to shoot himself");
                } else {
                    event.setDeathMessage(killer + " tested " + player + "'s dodging skills with his " + weapon);
                }
            }
            case " was pricked to death" -> event.setDeathMessage(player + " tried to hug a cactus");
            case " walked into a cactus whilst trying to escape " -> {
                String article = "";
                article = ("aeiouõäöüAEIOUÕÄÖÜ".contains(killer.substring(0, 1))) ? "an" : "a";
                event.setDeathMessage("\"I'd rather die to a cactus, than to " + article + " " + killer + "\" - " + player);
            }
            case " drowned" -> {
                randomNumber = (int) (Math.random() * 2);
                switch (randomNumber) {
                    case 0 ->
                            event.setDeathMessage(player + " thought they could extract the O₂ from H₂O using their lungs");
                    case 1 -> event.setDeathMessage(player + " forgot their oxygen tank at home");
                }
            }
            case " drowned whilst trying to escape " ->
                    event.setDeathMessage(player + " went underwater to hide from " + killer + ", but was then reminded, that they aren't amphibious");
            case " experienced kinetic energy" -> event.setDeathMessage(player + " skipped his flight lessons");
            case " experienced kinetic energy whilst trying to escape " ->
                    event.setDeathMessage(killer + " made " + player + " panic and fly into a wall");
            case " blew up" -> event.setDeathMessage("Rest In Pieces, " + player);
            case " was blown up by ", " was blown up by  using " -> {
                if (killer.equals("Creeper")) {
                    event.setDeathMessage(player + "'s last words were \"Oh, man!\"");
                } else {
                    event.setDeathMessage(killer + " nuked " + player + " to the orbit");
                }
            }
            case " was blown up by Creeper" ->
                    event.setDeathMessage(player + " was sacrificed to a Creeper by " + killer);
            case " hit the ground too hard" ->
                    event.setDeathMessage(player + " wasn't paying attention to his health when performing that stunt");
            case " hit the ground too hard whilst trying to escape " ->
                    event.setDeathMessage(killer + " is dying of laughter because of how " + player + " just died");
            case " fell from a high place" -> {
                if (getServer().getPlayer(player).getInventory().contains(new ItemStack(Material.WATER_BUCKET))) {
                    event.setDeathMessage(player + " didn't know how to use their water bucket");
                } else {
                    event.setDeathMessage(player + " didn't have a water bucket for some reason");
                }
            }
            case " fell off a ladder" -> event.setDeathMessage(player + " doesn't know how to use a ladder");
            case " fell off some vines" ->
                    event.setDeathMessage("A vine was too slippery for " + player + " to climb it");
            case " fell off some weeping vines" ->
                    event.setDeathMessage("A weeping vine was too slippery for " + player + " to climb it");
            case " fell off some twisting vines" ->
                    event.setDeathMessage("A twisting vine was too slippery for " + player + " to climb it");
            case " fell off scaffolding" ->
                    event.setDeathMessage(player + " didn't wear a safety helmet when on scaffolding");
            case " fell while climbing" ->
                    event.setDeathMessage(player + " started climbing on some random thing and fell to their death");
            case " was impaled on a stalagmite" -> event.setDeathMessage(player + " became a shashlik");
            case " was impaled on a stalagmite whilst fighting " ->
                    event.setDeathMessage(player + " was shoved down a ditch by " + killer);
            case " was squashed by a falling anvil" ->
                    event.setDeathMessage("Someone dropped an anvil on " + player + ", OUCH!");
            case " was squashed by a falling block" ->
                    event.setDeathMessage(player + " lost his life to some falling block");
            case " was skewered by a falling stalactite" ->
                    event.setDeathMessage("Something sharp fell from the sky and took " + player + "'s life");
            case " went up in flames" -> event.setDeathMessage(player + " was cremated");
            case " walked into fire whilst fighting " -> event.setDeathMessage(killer + " made " + player + "-kebab");
            case " burned to death" -> {
                if (getServer().getPlayer(player).getInventory().contains(new ItemStack(Material.WATER_BUCKET))) {
                    event.setDeathMessage(player + " forgot that they had water in their inventory and burned to death");
                } else {
                    event.setDeathMessage(player + " didn't find water soon enough and burned to death");
                }
            }
            case " was burnt to a crisp whilst fighting " -> event.setDeathMessage(killer + " lit up " + player);
            case " went off with a bang" ->
                    event.setDeathMessage("After a firework didn't go off, " + player + " stuck their head in to see what's wrong");
            case " tried to swim in lava" -> {
                if (event.getKeepInventory()) {
                    event.setDeathMessage("Thank god keepInventory is enabled, because " + player + "just drowned in lava");
                }
                if (getServer().getPlayer(player).getInventory().isEmpty()) {
                    event.setDeathMessage(player + " emptied their inventory before going to swim in lava, smart");
                } else {
                    event.setDeathMessage(player + " suddenly found themselves in lava, F");
                }
            }
            case " tried to swim in lava to escape " -> {
                if (!Arrays.asList(mobs).contains(killer)) {
                    event.setDeathMessage(killer + " just pushed " + player + " in lava, be careful around them");
                } else {
                    event.setDeathMessage(player + " tried to run away from " + killer + " but then ended up inhaling lava");
                }
            }
            default -> System.out.println("idk bro");
        }
    }
}
