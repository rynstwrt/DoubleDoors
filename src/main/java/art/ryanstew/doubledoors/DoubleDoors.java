package art.ryanstew.doubledoors;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DoubleDoors extends JavaPlugin implements Listener
{
    private DoubleDoorsEvents doubleDoorsEvents;

    @Override
    public void onEnable()
    {
        doubleDoorsEvents = new DoubleDoorsEvents(this);
        doubleDoorsEvents.setDefaults();

        Objects.requireNonNull(getCommand("doubledoors")).setExecutor(new DoubleDoorsCommand(this));
        getServer().getPluginManager().registerEvents(doubleDoorsEvents, this);
    }

    @Override
    public void onDisable()
    {
        saveConfig();
    }

    public String getPrefix()
    {
        return "&7&l[&a&lOTBDD&7&l]&r ";
    }

    public DoubleDoorsEvents getDoubleDoorsEvents()
    {
        return doubleDoorsEvents;
    }
}
