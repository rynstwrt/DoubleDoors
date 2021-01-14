package art.ryanstew.doubledoors;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoubleDoorsCommand implements CommandExecutor
{
    private final DoubleDoors plugin;

    public DoubleDoorsCommand(DoubleDoors plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("\n");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + "&cOnly players can run this command!"));
            sender.sendMessage("\n");
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getDoubleDoorsEvents().playerHasEnabled(player))
        {
            plugin.getDoubleDoorsEvents().removePlayerFromEnabled(player);
            sender.sendMessage("\n");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + "&aSuccessfully disabled double doors!"));
        }
        else
        {
            plugin.getDoubleDoorsEvents().addPlayerToEnabled(player);
            sender.sendMessage("\n");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() + "&aSuccessfully enabled double doors!"));
        }
        sender.sendMessage("\n");
        return true;
    }
}
