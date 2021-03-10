package esd;

import java.time.LocalDateTime;

import java.util.ArrayList;



import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class Match {
	private ArrayList<UUID>player_list =new ArrayList<UUID>();
	private int money=40;
	private HashMap<String, Integer>spawn_rate_task = new HashMap<String, Integer>();
	private HashMap<String, ItemStack>spawn_rate = new HashMap<String, ItemStack>();
	private HashMap<String, Integer>Amount_of_upgrade = new HashMap<String, Integer>();
	private HashMap<String, Long>task_start_time = new HashMap<String, Long>();
	private BukkitTask initialBukkitTask=null;
	private main plugin;
	private int match_num;
	private Location setupblock_location;

	private ArrayList<Location>commandblock_location_list = new ArrayList<Location>();
	private BukkitTask timerTask= null;
	private boolean lock_on = false;
	private boolean clear_match = false;
	private String stateString = "reset";
	private org.bukkit.scoreboard.Scoreboard scoreboard;
	public Match()
	{
		
	}
	public Match(main plugin, int match_num,String worldname) // init stuff, setup location of command block and sample command block
	{
		this.plugin=plugin;
		spawn_rate.put("egg", new ItemStack(Material.EGG,1));
		spawn_rate.put("bonemeal",new ItemStack(Material.BONE_MEAL,5));
		this.match_num = match_num;
		int[] location = plugin.commandblock_position[match_num-1];
		setupblock_location = new Location(Bukkit.getWorld(worldname), location[0],  location[1],  location[2]);
		CommandBlock commandBlock = (CommandBlock) setupblock_location.getBlock().getState();
		String[] location_array= commandBlock.getCommand().split(",");
		for(String locationString: location_array)
		{
			String[]coorStrings = locationString.split(" ");
			Location commandblock_Location = new Location(setupblock_location.getWorld(), Double.parseDouble(coorStrings[0]),Double.parseDouble(coorStrings[1]), Double.parseDouble(coorStrings[2]));
			commandblock_location_list.add(commandblock_Location);
		}
		Bukkit.getWorld(worldname);
	}
	public boolean get_lock_on()
	{
		return lock_on;
	}
	
	public boolean get_clear_match()
	{
		return clear_match;
	}
	public ArrayList<UUID> get_player_list()
	{
		return player_list;
	}
	public void PlayerJoin(UUID playerUuid,int esd_match_num) {
		if(player_list.size()==4||lock_on==true)
		{
			Bukkit.getPlayer(playerUuid).sendMessage("This match has started or is full!! ");
			return;
		}
		player_list.add(playerUuid);
		plugin.player_match_num.put(playerUuid, match_num);
		Player player = Bukkit.getPlayer(playerUuid);
		int[] tmp = plugin.commandblock_position[match_num-1];
		player.teleport(new Location(player.getWorld(),tmp[0] ,tmp[1]+2.1, tmp[2]));
			for(UUID uuid : player_list)
			{
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW+Bukkit.getPlayer(playerUuid).getName()+" has enter. ("+player_list.size()+"/4)");
			}
		
	}
	public void Player_leave(UUID playerUuid) {
		if(initialBukkitTask!=null)
		{
			Bukkit.getScheduler().cancelTask(initialBukkitTask.getTaskId());
			initialBukkitTask=null;
			lock_on=false;
			plugin.emtpy_match.add("Barn "+match_num);
		}
		if(player_list.size()==4&&lock_on==false)
		{
			plugin.emtpy_match.add("Barn "+match_num);
		}
		Bukkit.getPlayer(playerUuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		Bukkit.getPlayer(playerUuid).getInventory().clear();
		Bukkit.getPlayer(playerUuid).setGameMode(GameMode.ADVENTURE);
		player_list.remove(playerUuid);
		plugin.player_match_num.remove(playerUuid);
		if(player_list.size()==0)
		{
			reset_match();
		}
		else  {
			update_scoreboard("player_list");
			for(UUID uuid : player_list)
			{
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW+Bukkit.getPlayer(playerUuid).getName()+" has quit. ("+player_list.size()+"/4)");
			}	
		}
		
	}
	public void Player_want_start_game() {
		if(lock_on==false)
		{
			lock_on=true;
			for(UUID uuid : player_list)
			{
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW+"Match will start in 10 seconds !");
			}
			initialBukkitTask= Bukkit.getScheduler().runTaskLater(plugin, ()->{start_match();}, 200);
			plugin.emtpy_match.remove("Barn "+match_num);	
		}
	}
	public void start_match() {
		initialBukkitTask=null;
		opengate();
		Scoreboard();
		for(UUID uuid : player_list)
		{
			Player player = Bukkit.getPlayer(uuid);
			player.getInventory().clear();
			player.sendTitle( ChatColor.YELLOW+"START!!!!",null, 10, 10, 10);
			player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_PLING, (float)0.3,(float) 1);
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().addItem(new ItemStack(Material.BUCKET,3));
			player.setScoreboard(scoreboard);
		}
		Block redstone_torch = commandblock_location_list.get(3).getBlock();
		redstone_torch.setType(Material.AIR);
		BukkitTask bonemealBukkitTask= Bukkit.getScheduler().runTaskTimer(plugin,()->{spawnbonemeal();}, 1,200);
		BukkitTask eggBukkitTask= Bukkit.getScheduler().runTaskTimer(plugin,()->{spawnegg();}, 1, 200);
		spawn_rate_task.put("bonemeal", bonemealBukkitTask.getTaskId());
		spawn_rate_task.put("egg", eggBukkitTask.getTaskId());
		initialBukkitTask=Bukkit.getScheduler().runTaskLater(plugin,()->{countdown(10);}, 9400);
		timerTask = Bukkit.getScheduler().runTaskTimer(plugin,()->{update_scoreboard("time");}, 20, 20);
		
		
	}
	
	public void Scoreboard()
	{
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		 Objective obj = scoreboard.registerNewObjective("esd_"+match_num, "dummy", "Test Server");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score playerScore = obj.getScore(ChatColor.GRAY + "PLayer:");
        playerScore.setScore(player_list.size());
        Score money = obj.getScore(ChatColor.GRAY + "Money:");
        money.setScore(this.money);
        Score time = obj.getScore(ChatColor.GRAY + "Time Remaining:");
        time.setScore(480);
        obj.setDisplayName(ChatColor.GREEN +""+ChatColor.ITALIC+"===SPEED BAKERS===");
        Team team = scoreboard.registerNewTeam("esd");
        team.addEntry(ChatColor.GREEN+"ok");
	}
	
	public void update_scoreboard(String section)
	{
		switch (section) {
		case "time":
			Score timerScore =scoreboard.getObjective("esd_"+match_num).getScore(ChatColor.GRAY + "Time Remaining:");
			timerScore.setScore(timerScore.getScore()-1);
			for(UUID uuid: player_list)
			{
				Bukkit.getPlayer(uuid).setScoreboard(scoreboard);
			}	
			break;
		case "player_list":
			Score money =scoreboard.getObjective("esd_"+match_num).getScore(ChatColor.GRAY + "PLayer:");
			money.setScore(player_list.size());
			for(UUID uuid: player_list)
			{
				Bukkit.getPlayer(uuid).setScoreboard(scoreboard);
			}
		case "money":
			Score playerScore =scoreboard.getObjective("esd_"+match_num).getScore(ChatColor.GRAY + "Money:");
			playerScore.setScore(this.money);
			for(UUID uuid: player_list)
			{
				Bukkit.getPlayer(uuid).setScoreboard(scoreboard);
			}
		default:
			break;
		}
	}
	public void opengate()
	{
		CommandBlock commandBlock=(CommandBlock)commandblock_location_list.get(0).getBlock().getState();
		commandBlock.setCommand(commandBlock.getCommand().replace("iron_bars", "air"));
		commandBlock.update();
	}
	public void spawnbonemeal()
	{
		Location button=commandblock_location_list.get(1).clone().add(0, 2, 0);
		button.getWorld().dropItem(button, spawn_rate.get("bonemeal"));
		task_start_time.put("bonemeal", System.currentTimeMillis());
	}
	public void spawnegg()
	{
		Location button=commandblock_location_list.get(2).clone().add(0, 2, 0);
		button.getWorld().dropItem(button, spawn_rate.get("egg"));
		task_start_time.put("egg", System.currentTimeMillis());
	}
	
	public void upgrade_bonemeal(Player player) {
		if(!Amount_of_upgrade.containsKey("bonemeal"))
		{
			Amount_of_upgrade.put("bonemeal", 0);
		}
		int upgrade_level = Amount_of_upgrade.get("bonemeal")+1;
		int diff = 200-20*upgrade_level;
		if(money<plugin.Upgrade_price.get("bonemeal")*upgrade_level)
		{
			player.sendMessage(ChatColor.RED+"you do not have enough money to upgrade!! ");
			return;
		}
		Amount_of_upgrade.put("bonemeal", upgrade_level);
		Bukkit.getScheduler().cancelTask(spawn_rate_task.get("bonemeal"));
		BukkitTask tasktaskBukkitTask = Bukkit.getScheduler().runTaskTimer(plugin,()->{spawnbonemeal();} , (long)((System.currentTimeMillis()-task_start_time.get("bonemeal"))/50),diff);
		spawn_rate_task.put("bonemeal", tasktaskBukkitTask.getTaskId());
		money-= plugin.Upgrade_price.get("bonemeal")*upgrade_level;
		notify_player(player,"upgrade bonemeal spawner");
		update_scoreboard("money");
	}
	
	public void upgrade_egg(Player player) {
		if(!Amount_of_upgrade.containsKey("egg"))
		{
			Amount_of_upgrade.put("egg", 0);
		}
		int upgrade_level = Amount_of_upgrade.get("egg")+1;
		int diff = 200-40*upgrade_level;
		if(money<plugin.Upgrade_price.get("egg")*upgrade_level)
		{
			player.sendMessage(ChatColor.RED+"you do not have enough money to upgrade!! ");
			return;
		}
		Amount_of_upgrade.put("egg", upgrade_level);
		Bukkit.getScheduler().cancelTask(spawn_rate_task.get("egg"));
		BukkitTask tasktaskBukkitTask= Bukkit.getScheduler().runTaskTimer(plugin,()->{spawnegg();} , (long)((System.currentTimeMillis()-task_start_time.get("egg"))/50),diff);
		spawn_rate_task.put("egg",tasktaskBukkitTask.getTaskId() );
		money-=plugin.Upgrade_price.get("egg")*upgrade_level;
		notify_player(player,"upgrade egg spawner");
		update_scoreboard("money");
	}
	
	public void buybucket(Player player,String upgrade_item) {
		int price = plugin.Upgrade_price.get(upgrade_item);
		if(!Amount_of_upgrade.containsKey("bucket"))
		{Amount_of_upgrade.put("bucket", 0);}
		int buy_amount = Amount_of_upgrade.get("bucket")+1;
		if(buy_amount>3)
		{
			price+=30;
		}
		Amount_of_upgrade.put("bucket", buy_amount);
		if(money<price)
		{
			player.sendMessage(ChatColor.RED+"You do not have enough money to buy this !!!");
			return;
		}
			money-=price;
			player.getInventory().addItem(new ItemStack(Material.BUCKET));
			notify_player(player,"buy a bucket");
			update_scoreboard("money");
		
	}
	
	public void buypotion(Player player) {
		if(money<plugin.Upgrade_price.get("speed_potion"))
		{
			player.sendMessage(ChatColor.RED+"You do not have enough money to buy this !!!");
			return;
		}
		money-= plugin.Upgrade_price.get("speed_potion");
		player.getInventory().addItem(plugin.potion);
		notify_player(player,"buy a speed potion");
		update_scoreboard("money");
		
	}
	
	public void sellcake(Player player)
	{
		if(player.getInventory().contains(Material.CAKE))
		{
			HashMap<Integer, ? extends ItemStack> cakeHashMap = player.getInventory().all(Material.CAKE);
			int total_cake=0;
			for(ItemStack cake : cakeHashMap.values())
			{
				total_cake+=cake.getAmount();
			}
			player.getInventory().remove(Material.CAKE);
			money+=plugin.cake_price*total_cake;
			notify_player(player, "earn $"+plugin.cake_price*total_cake);
			update_scoreboard("money");
			
		}
		else {
			player.sendMessage(ChatColor.RED+"You do not have cake !!");
		}
	}
	public void reset_match() {
		if(stateString=="reset")
		{
			lock_on=false;
			if(initialBukkitTask!=null)
			{Bukkit.getScheduler().cancelTask(initialBukkitTask.getTaskId());}
			initialBukkitTask=null;
			player_list.clear();
			clear_match=true;
			if(timerTask!=null)
			{Bukkit.getScheduler().cancelTask(timerTask.getTaskId());}
			timerTask=null;
			Amount_of_upgrade.put("egg", 0);
			Amount_of_upgrade.put("bonemeal", 0);
			Amount_of_upgrade.put("bucket", 0);
			if(spawn_rate_task.size()!=0)
			{
				for(int i : spawn_rate_task.values())
				{
					Bukkit.getScheduler().cancelTask(i);
				}	
			}
			spawn_rate_task.clear();
			CommandBlock commandBlock=(CommandBlock)commandblock_location_list.get(0).getBlock().getState();
			commandBlock.setCommand(commandBlock.getCommand().replace("air", "iron_bars"));
			commandBlock.update();
			money=40;
			plugin.emtpy_match.add("Barn "+match_num);	
			Block redstone_torch = commandblock_location_list.get(3).getBlock();
			redstone_torch.setType(Material.REDSTONE_TORCH);
			Scoreboard();
		}
		
	}
	public void notify_player(Player player,String item)
	{
		for(UUID playerUuid:player_list)
		{
			Player iPlayer = Bukkit.getPlayer(playerUuid);
			iPlayer.sendMessage(ChatColor.GREEN+player.getName()+" has "+item+" !!!");
			iPlayer.playSound(iPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, (float)0.3, (float)1.0);
		}
	}
	public void countdown(int countdown)
	{
		for(UUID playerUuid : player_list)
		{
			Bukkit.getPlayer(playerUuid).sendMessage(ChatColor.YELLOW+""+countdown+" seconds left !!!");
		}
		initialBukkitTask = Bukkit.getScheduler().runTaskLater(plugin,()->{end_match();}, 200);
	}
	public void end_match() {
		Bukkit.getScheduler().cancelTask(timerTask.getTaskId());
		timerTask=null;
		int i=1;
		
		long currenttime= System.currentTimeMillis();
		for(UUID playerUuid : player_list)
		{
			Player player = Bukkit.getPlayer(playerUuid);
			player.getInventory().clear();
			player.setGameMode(GameMode.SPECTATOR);
			player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP, (float)0.3,(float) 1);
			player.sendTitle(ChatColor.YELLOW+" TIMES UP !", null, 10, 10, 10);
			player.sendMessage(ChatColor.YELLOW+"you will be teleport out in 10 seconds. ");
			Bukkit.getScheduler().runTaskLater(plugin, ()->{player.sendTitle(ChatColor.YELLOW+"your profit is :"+money, null, 10, 20, 10); }, 60);
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			stateString="ended";
			plugin.GetScoreConfig().set(Long.toString(currenttime)+".team.player_"+i, Bukkit.getPlayer(playerUuid).getName());
			plugin.GetScoreConfig().set(Long.toString(currenttime)+".team.uuid_"+i, playerUuid.toString());
			i+=1;
			Bukkit.getScheduler().runTaskLater(plugin,()->{
			plugin.player_match_num.remove(playerUuid);
			player.teleport(plugin.spawnblockLocation);
			player.setGameMode(GameMode.ADVENTURE);
			}, 200);
		}
		plugin.GetScoreConfig().set(Long.toString(currenttime)+".score",money);
		plugin.GetScoreConfig().set(Long.toString(currenttime)+".time",LocalDateTime.now().toString());
		plugin.SaveConfig(plugin.GetScoreConfig(), plugin.GetScoreFile());
		Bukkit.getScheduler().runTaskLater(plugin,()-> {stateString="reset";reset_match();}, 210);
	}


	public double power(double x, double y,double x1, double y1)
	{
		return Math.sqrt(Math.pow(x1-x,2)+Math.pow(y1-y,2));
	}
}
