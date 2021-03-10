package esd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class commandBlockCommandExecutor implements CommandExecutor{
	  private final main plugin;
	  public commandBlockCommandExecutor(main plugin) {
		this.plugin=plugin;
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(cmd.getName().equalsIgnoreCase("esd_command_block"))
		{
			 if (sender instanceof Player) {
			     Player player = (Player) sender;
			     if (player.isOp()) {
			    	 if(arg.length==4&&checkfloat(arg[1])&&checkfloat(arg[2])&&checkfloat(arg[3]))
			    	 {
			    		switch (arg[0]) {
			    		case"spawn_location":
			    			change_block_location(4, arg,player);
			    			break;
						case "block_one":
							change_block_location(0,arg,player);
							break;
						case "block_two":
							change_block_location(1,arg,player);
							break;
						case "block_three":
							change_block_location(2,arg,player);
							break;
						case "block_four":
							change_block_location(3,arg,player);
							break;

						default:
							player.sendMessage(ChatColor.RED+"Command error at "+arg[0]+", no such argument!!");
							return false;
						} 
			    		player.sendMessage(ChatColor.GREEN+"Command Block location record sucessfully !!!");
			    		return true;
			    	 }
			    	 }
			     else {
			    	 player.sendMessage(ChatColor.RED+"You do not have permission to this command !!");
			    	return false; 
			     }
			     }
		}
		return false;
	}
	public boolean checkfloat(String location)
	{
		try {
			Double.parseDouble(location);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public void change_block_location(int index,String[] arg, Player player)
	{
		int [] tmp= new int[3] ;
		if(index==4)
		{
			for(int i =0; i<tmp.length;i++)
			{
				tmp[i]=Integer.parseInt(arg[i+1]);

			}
		plugin.GetCustomConfig().set("spawn_block.x", tmp[0]);
		plugin.GetCustomConfig().set("spawn_block.y", tmp[1]);	
		plugin.GetCustomConfig().set("spawn_block.z", tmp[2]);
		plugin.GetCustomConfig().set("spawn_block.world",player.getWorld().getUID().toString());
		plugin.spawnblock = tmp;
		plugin.spawnblockLocation = new Location(player.getWorld(),  tmp[0],  tmp[1],  tmp[2]);
		return;
		}
		for(int i =0; i<tmp.length;i++)
		{
			tmp[i]=Integer.parseInt(arg[i+1]);

		}
		plugin.commandblock_position[index] = tmp;
	}
}
