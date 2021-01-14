package art.ryanstew.doubledoors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class DoubleDoorsEvents implements Listener
{
    private final DoubleDoors plugin;
    private final FileConfiguration config;
    private final Set<UUID> enabledPlayers = new HashSet<>();

    public DoubleDoorsEvents(DoubleDoors plugin)
    {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    private boolean blockIsADoor(Block block)
    {
        return Tag.WOODEN_DOORS.isTagged(block.getType());
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
            if (!blockIsADoor(b)) continue;

            Door door = (Door) b.getBlockData();

            boolean hasOppositeHinge = !door.getHinge().name().equalsIgnoreCase(((Door) block.getBlockData()).getHinge().name());
            boolean isOnSameSide = door.getFacing().getDirection().equals(((Door) block.getBlockData()).getFacing().getDirection());
            boolean sameOpenStatus = door.isOpen() == ((Door) block.getBlockData()).isOpen();

            if (hasOppositeHinge && isOnSameSide && sameOpenStatus) return b.getLocation();
        }

        return null;
    }

    public void setDefaults()
    {
        config.addDefault("enabled-players", "");
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public boolean playerHasEnabled(Player player)
    {
        return enabledPlayers.contains(player.getUniqueId());
    }

    public void addPlayerToEnabled(Player player)
    {
        enabledPlayers.add(player.getUniqueId());
    }

    public void removePlayerFromEnabled(Player player)
    {
        enabledPlayers.remove(player.getUniqueId());
    }

    private boolean playerInConfig(Player player)
    {
        return config.getStringList("enabled-players").contains(player.getUniqueId().toString());
    }

    private void addPlayerToConfig(Player player)
    {
        List<String> configEnabledPlayers = config.getStringList("enabled-players");
        configEnabledPlayers.add(player.getUniqueId().toString());
        config.set("enabled-players", configEnabledPlayers);
        plugin.saveConfig();
    }

    private void removePlayerFromConfig(Player player)
    {
        List<String> configEnabledPlayers = config.getStringList("enabled-players");
        configEnabledPlayers.remove(player.getUniqueId().toString());
        config.set("enabled-players", configEnabledPlayers);
        plugin.saveConfig();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        Block block = e.getClickedBlock();
        if (block == null) return;

        Player player = e.getPlayer();

        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !blockIsADoor(block) || !playerHasEnabled(player)) return;

        boolean startsClosed = !((Door) block.getBlockData()).isOpen();

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
        if (!playerHasEnabled(player) && playerInConfig(player)) addPlayerToEnabled(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        Player player = e.getPlayer();

        if (playerHasEnabled(player) && !playerInConfig(player))
        {
            addPlayerToConfig(player);
        }
        else if (!playerHasEnabled(player) && playerInConfig(player))
        {
            removePlayerFromConfig(player);
        }
    }
}
