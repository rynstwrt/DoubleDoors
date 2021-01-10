package art.ryanstew.doubledoors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DoubleDoors extends JavaPlugin implements Listener
{
    private final String PREFIX = "&7&l[&a&lOTBDD&7&l]&r ";
    private ArrayList<UUID> enabledPlayers = new ArrayList<>();
    private FileConfiguration config = getConfig();

    @Override
    public void onEnable()
    {
        config.addDefault("enabled-players", "");
        config.options().copyDefaults(true);
        saveConfig();

        getCommand("doubledoors").setExecutor(new DoubleDoorsCommand(this));
        getServer().getPluginManager().registerEvents(new DoubleDoorsEvents(this), this);
    }

    @Override
    public void onDisable()
    {
        saveConfig();
    }

    public String getPrefix()
    {
        return PREFIX;
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

    public boolean playerInConfig(Player player)
    {
        return config.getStringList("enabled-players").contains(player.getUniqueId().toString());
    }

    public void addPlayerToConfig(Player player)
    {
        List<String> configEnabledPlayers = config.getStringList("enabled-players");
        configEnabledPlayers.add(player.getUniqueId().toString());
        config.set("enabled-players", configEnabledPlayers);
        saveConfig();
    }

    public void removePlayerFromConfig(Player player)
    {
        List<String> configEnabledPlayers = config.getStringList("enabled-players");
        configEnabledPlayers.remove(player.getUniqueId().toString());
        config.set("enabled-players", configEnabledPlayers);
        saveConfig();
    }
}
