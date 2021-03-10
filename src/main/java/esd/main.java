package esd;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;



import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class main extends JavaPlugin {
	public ArrayList<String>emtpy_match = new ArrayList<String>();
	public HashMap<String,Integer>Upgrade_price = new HashMap<String, Integer>();
	public HashMap<UUID, Integer>player_match_num = new HashMap<UUID, Integer>();
	public int[][] commandblock_position = {{1,1,1},{1,1,1},{1,1,1},{1,1,1}};
	public int[] spawnblock = {0,0,0};
	public Location spawnblockLocation;
	public ItemStack potion;
	public int cake_price=100;
	public ItemStack stick = new ItemStack(Material.STICK);
	private File customConfigFile;
	private File scoreFile;
	private YamlConfiguration customConfig;
	private YamlConfiguration scoreconfig;
	  
	@Override
	public void onEnable() {
		potion=new ItemStack(Material.POTION);
		PotionMeta potionData=(PotionMeta) potion.getItemMeta();
		potionData.setDisplayName(ChatColor.LIGHT_PURPLE+"Fast Potion");
		potionData.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 120, 1), true);
		potionData.setColor(Color.LIME);
		potion.setItemMeta(potionData);
		ItemMeta stickItemMeta = stick.getItemMeta();
		stickItemMeta.setDisplayName(ChatColor.BLUE+"right click to reveal location");
		stickItemMeta.setLocalizedName("locator stick");
		stick.setItemMeta(stickItemMeta);
		try {
			createCustomConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<4;i++)
		{emtpy_match.add("Barn "+Integer.toString(i+1));
		 switch (i) {
		case 0:
			Upgrade_price.put("egg", 20);
			break;
		case 1:
			Upgrade_price.put("bonemeal", 20);
			break;
		case 2:
			Upgrade_price.put("bucket", 30);
			break;
		case 3:
			Upgrade_price.put("speed_potion", 50);
			break;
		default:
			break;
			
		}
		 int[] tmp = new int[3];
			 tmp[0] = customConfig.getInt("location_"+(i+1)+".x");
			 tmp[1] = customConfig.getInt("location_"+(i+1)+".y");
			 tmp[2] = customConfig.getInt("location_"+(i+1)+".z");
			 commandblock_position[i] = tmp;
		}
		spawnblock[0] = customConfig.getInt("spawn_block.x");
		spawnblock[1] = customConfig.getInt("spawn_block.y");
		spawnblock[2] = customConfig.getInt("spawn_block.z");
		if(customConfig.contains("spawn_block.world"))
		{
			spawnblockLocation = new Location(Bukkit.getWorld(UUID.fromString(customConfig.getString("spawn_block.world"))), spawnblock[0], spawnblock[1], spawnblock[2]);
		}

		 this.getCommand("esd_command_block").setExecutor(new commandBlockCommandExecutor(this));
			this.getCommand("esd_locator_stick").setExecutor(new esd_stickCommandExecutor(this));
			getServer().getPluginManager().registerEvents(new MatchListener(this), this);
			
	}
	@Override
	public void onDisable() {
		int i =1;
		for(int[] tmp: commandblock_position)
		{
			customConfig.set("location_"+i+".x", tmp[0]);
			customConfig.set("location_"+i+".y", tmp[1]);
			customConfig.set("location_"+i+".z", tmp[2]);
			i+=1;
		}
		SaveConfig(customConfig,customConfigFile);
		SaveConfig(scoreconfig, scoreFile);
	}
	public YamlConfiguration GetCustomConfig()
	{
		return customConfig;
	}
	public YamlConfiguration GetScoreConfig()
	{
		return scoreconfig;
	}
	public File GetScoreFile()
	{
		return scoreFile;
	}
	public void SaveConfig(YamlConfiguration config,File file)
	{
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	  public void createCustomConfig() throws IOException {
	    	 customConfigFile = new File(getDataFolder(), "location.yml");
	         if (!customConfigFile.exists()) {
	             customConfigFile.getParentFile().mkdirs();
	             saveResource("location.yml", false);
	             customConfig= new YamlConfiguration();
	             try {
	                 customConfig.load(customConfigFile);
	             } catch (Exception e) {
	    			e.printStackTrace();
	    		}
	          }
	         else {
	        	 customConfig= new YamlConfiguration();
	             try {
	                 customConfig.load(customConfigFile);
	             } catch (Exception e) {
	    			e.printStackTrace();
	    		}
	         }
	         scoreFile = new File(getDataFolder(), "score.yml");
	         if (!scoreFile.exists()) {
	             scoreFile.getParentFile().mkdirs();
	             saveResource("score.yml", false);
	             scoreconfig= new YamlConfiguration();
	             try {
	                 scoreconfig.load(scoreFile);
	             } catch (Exception e) {
	    			e.printStackTrace();
	    		}
	          }
	         else {
	        	 scoreconfig= new YamlConfiguration();
	             try {
	                 scoreconfig.load(scoreFile);
	             } catch (Exception e) {
	    			e.printStackTrace();
	    		}
	         }
	  
		}
	    

}
