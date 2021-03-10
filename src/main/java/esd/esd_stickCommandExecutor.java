package esd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class esd_stickCommandExecutor implements CommandExecutor{
	private main plugin;
 public esd_stickCommandExecutor(main plugin)
 {
	 this.plugin = plugin;
 }
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
	
		if(cmd.getName().equalsIgnoreCase("esd_locator_stick"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player)sender;
				if(player.isOp())
				{
					player.getInventory().addItem(plugin.stick);
					player.sendMessage(ChatColor.GREEN+"Magic stick added into your inventory !!! right click block with it will reveal its location .");
					return true;	
				}
			}
		}
		return false;
	}
}
