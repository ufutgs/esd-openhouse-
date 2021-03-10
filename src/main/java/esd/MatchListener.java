package esd;

import java.util.HashMap;



import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Cow;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public final class MatchListener  implements Listener{
	private main plugin;
	private HashMap<Integer, Match>match_register = new HashMap<Integer, Match>();
	private Location location;
	public MatchListener(main plugin)
	{
		this.plugin=plugin; 
	
	}
	
	@EventHandler
	public void disableDamage(EntityDamageEvent e) {

		if(e.getEntity() instanceof Cow)
		{
			e.setCancelled(true);
		}

	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent e)
	{
		if(e.getClickedBlock()!=null)
		{
			if(e.getPlayer().getInventory().getItemInMainHand().equals(plugin.stick)&&e.getAction()==Action.RIGHT_CLICK_BLOCK)
			{
					Location tmp = e.getClickedBlock().getLocation();
					if(location==null||(location.getX()!=tmp.getX()||location.getY()!=tmp.getY()||location.getZ()!=tmp.getZ()))
					{
						 location = e.getClickedBlock().getLocation();
							e.getPlayer().sendMessage(location.getX()+" "+location.getY()+" "+location.getZ());
							return;		
					}
				else {
					return;
				}
			}
			else if(!plugin.player_match_num.containsKey(e.getPlayer().getUniqueId()))
			{
				if(e.getClickedBlock().getState() instanceof Sign&& e.getAction()==Action.RIGHT_CLICK_BLOCK)
				{
					Sign esd_join_sign = (Sign) e.getClickedBlock().getState();
					switch (esd_join_sign.getLine(1)) {
					case "be a labour":
						if(plugin.emtpy_match.size()==0)
						{e.getPlayer().sendMessage(ChatColor.RED+"Too much people already!! Go social distancing !");
						return;
						}
						Match match;
						int room_num = Integer.parseInt(esd_join_sign.getLine(3).substring(esd_join_sign.getLine(3).length()-1));
						if(plugin.emtpy_match.contains(esd_join_sign.getLine(3)))
						{
							if(!match_register.containsKey(room_num))
							{	 match = new Match(plugin,room_num,e.getPlayer().getWorld().getName());
								match_register.put(room_num, match);}
							else {match=match_register.get(room_num);
								match.PlayerJoin(e.getPlayer().getUniqueId(),room_num);
						}
						}
						else {
							e.getPlayer().sendMessage("This match has started or is full!! you can go Room " + plugin.emtpy_match.get(0));
						}	
						break;
					default:
						break;
				}
			}
			}
			else {
				if(e.getClickedBlock().getState() instanceof Sign&& e.getAction()==Action.RIGHT_CLICK_BLOCK)
				{
					Sign esd_join_sign = (Sign) e.getClickedBlock().getState();
					Match match2 = match_register.get(plugin.player_match_num.get(e.getPlayer().getUniqueId()));
						switch (esd_join_sign.getLine(1)) {
						case "Start game":
							match2.Player_want_start_game();break;
						case "Quit game":
							match2.Player_leave(e.getPlayer().getUniqueId());
							e.getPlayer().teleport(plugin.spawnblockLocation);
							break;
						case "Buy Bucket":
							match2.buybucket(e.getPlayer(),"bucket");
							break;
						case "egg Spawner":
							match2.upgrade_egg(e.getPlayer());
							break;
						case "bonemeal++":
							match2.upgrade_bonemeal(e.getPlayer());
							break;
						case "Buy Speed Potion":
							match2.buypotion(e.getPlayer());
							break;
						case "Sell Cake":
							match2.sellcake(e.getPlayer());
							break;
						default:
							break;
						}
					
					
				}
				else if(e.getClickedBlock().getType()==Material.WHEAT)
				{
					
				}
				else if(e.getAction()==Action.RIGHT_CLICK_BLOCK&&e.getClickedBlock().getType()==Material.FARMLAND)
				{
					return;
//				
				}
				else if(e.getClickedBlock().getType()==Material.SUGAR_CANE)
				{
					Block sugarcane = e.getClickedBlock();
//			
					 if(e.getAction()==Action.RIGHT_CLICK_BLOCK&&e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BONE_MEAL)&&sugarcane.getRelative(BlockFace.DOWN).getType()!=Material.SUGAR_CANE)
					{
						for(int i=0;i<2;i++)
						{
							sugarcane.getRelative(BlockFace.UP,i+1).setType(Material.SUGAR_CANE);
						}
						ItemStack bonemeal = e.getPlayer().getInventory().getItemInMainHand();
						int item_amount = bonemeal.getAmount();
						if(item_amount<=1)
						{
							e.getPlayer().getInventory().removeItem(bonemeal);
						}
						else {
							bonemeal.setAmount(item_amount-1);
							e.getPlayer().getInventory().setItemInMainHand(bonemeal);
						}
						sugarcane.getWorld().playEffect(sugarcane.getLocation(), Effect.VILLAGER_PLANT_GROW, 20);
						return;
					}
				}
				else if (e.getClickedBlock().getType()==Material.BARREL)
				{
					e.getPlayer().sendMessage(ChatColor.RED+"Hey!! That is illegal !!");
					e.setCancelled(true);
				}
		}	
		}
		
}
	
	@EventHandler
	public void DisableItemFrame(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof ItemFrame)

		{
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void OnBlockBreak(BlockBreakEvent e)
	{
		if(plugin.player_match_num.containsKey(e.getPlayer().getUniqueId()))
		{	Material blockMaterial = e.getBlock().getType();
			if(blockMaterial!=Material.WHEAT&&blockMaterial!=Material.WHEAT_SEEDS&&blockMaterial!=Material.SUGAR_CANE)
			{
				e.setCancelled(true);
			}

		}
	}
	@EventHandler
	public void OnPlayerLeave(PlayerQuitEvent e)
	{
		if(plugin.player_match_num.containsKey(e.getPlayer().getUniqueId()))
		{
			match_register.get(plugin.player_match_num.get(e.getPlayer().getUniqueId())).Player_leave(e.getPlayer().getUniqueId());
			e.getPlayer().teleport(plugin.spawnblockLocation);
		}
	}
}
