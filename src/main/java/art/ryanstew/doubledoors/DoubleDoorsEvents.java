package art.ryanstew.doubledoors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class DoubleDoorsEvents implements Listener
{
    private final DoubleDoors plugin;
    private final ArrayList<Material> DOORS = new ArrayList<>(Arrays.asList(Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.ACACIA_DOOR, Material.JUNGLE_DOOR, Material.DARK_OAK_DOOR, Material.CRIMSON_DOOR, Material.WARPED_DOOR));

    public DoubleDoorsEvents(DoubleDoors plugin)
    {
        this.plugin = plugin;
    }

    private Location getPartnerDoorLocation(World world, Block block)
    {
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(world.getBlockAt(block.getLocation().add(1, 0, 0)));
        blocks.add(world.getBlockAt(block.getLocation().subtract(1, 0, 0)));
        blocks.add(world.getBlockAt(block.getLocation().add(0, 0, 1)));
        blocks.add(world.getBlockAt(block.getLocation().subtract(0, 0, 1)));

        for (Block b : blocks)
        {
            if (!DOORS.contains(b.getType())) continue;

            Door door = (Door) b.getBlockData();

            boolean hasOppositeHinge = !door.getHinge().name().equalsIgnoreCase(((Door) block.getBlockData()).getHinge().name());
            boolean isOnSameSide = door.getFacing().getDirection().equals(((Door) block.getBlockData()).getFacing().getDirection());
            boolean sameOpenStatus = door.isOpen() == ((Door) block.getBlockData()).isOpen();

            if (hasOppositeHinge && isOnSameSide && sameOpenStatus) return b.getLocation();
        }

        return null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Block block = e.getClickedBlock();
        if (block == null) return;

        Player player = e.getPlayer();

        boolean startsClosed = !((Door) block.getBlockData()).isOpen();

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !DOORS.contains(block.getType()) || !plugin.playerHasEnabled(player)) return;

        Location partnerDoorLoc = getPartnerDoorLocation(player.getWorld(), block);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
        {
            // if closed and startsclosed, restart
            // if open and not startsclosed, restart
            boolean doorOpenNow = ((Door) block.getBlockData()).isOpen();
            if (startsClosed && !doorOpenNow) return;
            if (!startsClosed && doorOpenNow) return;

            if (partnerDoorLoc == null) return;
            Block partnerDoor = player.getWorld().getBlockAt(partnerDoorLoc);
            Door door = (Door) partnerDoor.getBlockData();
            door.setOpen(doorOpenNow);
            partnerDoor.setBlockData(door);
        }, 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        if (!plugin.playerHasEnabled(player) && plugin.playerInConfig(player)) plugin.addPlayerToEnabled(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        Player player = e.getPlayer();

        if (plugin.playerHasEnabled(player) && !plugin.playerInConfig(player))
        {
            plugin.addPlayerToConfig(player);
        }
        else if (!plugin.playerHasEnabled(player) && plugin.playerInConfig(player))
        {
            plugin.removePlayerFromConfig(player);
        }
    }
}
