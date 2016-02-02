package me.kingingo.kSystem.kServer.GunGame.Commands;

import java.io.File;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kSystem.kServer.GunGame.kGunGame;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.kConfig.kConfig;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.DisplaySlot;

public class CommandMap extends kListener implements CommandExecutor{
	@Getter
	private kConfig config;
	@Getter
	private kGunGame instance;
	@Getter
	@Setter
	private int counter=0;
	@Getter
	@Setter
	private String nextMap;
	@Getter
	@Setter
	private int time=10;
	@Getter
	private HashMap<String,Location> maps;
	
	public CommandMap(kGunGame instance){
		super(instance.getInstance(),"CommandMap");
		this.instance=instance;
		this.config=new kConfig(new File("plugins"+File.separator+instance.getInstance().getPlugin(instance.getInstance().getClass()).getName()+File.separator+"maps.yml"));
		
		this.maps=new HashMap<>();
		for(String map : config.getPathList("maps").keySet()){
			if(instance.getSpawn()==null)instance.setSpawn(config.getLocation("maps."+map.toLowerCase()+".Spawn"));
			getMaps().put(map.toLowerCase(), config.getLocation("maps."+map.toLowerCase()+".Spawn"));
		}
	}
	
	String btime;
	@EventHandler
	public void next(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC&&!this.maps.isEmpty()){
			this.time--;
			
			btime=UtilTime.formatSeconds(time);
			for(Player player : UtilServer.getPlayers()){
				if(player.getScoreboard()!=null){
					if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)!=null){
						UtilScoreboard.resetScore(player.getScoreboard(), 0, DisplaySlot.SIDEBAR);
						UtilScoreboard.setScore(player.getScoreboard(), "§f"+btime, DisplaySlot.SIDEBAR, 0);
					}
				}
			}
			
			switch(this.time){
			case 5:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 4:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 3:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 2:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 1:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 0:
				if(nextMap==null){
					this.nextMap=(String)this.maps.keySet().toArray()[this.counter];
					this.counter++;
					
					if(this.counter==this.maps.size()){
						this.counter=0;
					}
				}
				
				this.time=60*45;
				getInstance().setSpawn(this.maps.get(this.nextMap));
				for(Player player : UtilServer.getPlayers())player.teleport(getInstance().getSpawn());
				
				if(UtilServer.getGemsShop()!=null){
					if(config.contains("maps."+nextMap.toLowerCase()+".GemShop")){
						UtilServer.getGemsShop().setCreature(config.getLocation("maps."+nextMap.toLowerCase()+".GemShop"));
					}
				}
				
				if(UtilServer.getDeliveryPet()!=null){
					if(config.contains("maps."+nextMap.toLowerCase()+".DeliveryPet")){
						UtilServer.getDeliveryPet().teleportPet(config.getLocation("maps."+nextMap.toLowerCase()+".DeliveryPet"));
					}
				}
				
				this.nextMap=null;
				UtilServer.broadcastLanguage("GUNGAME_MAP_CHANGE");
				break;
			}
		}
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "map", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		Player player=(Player)sender;
		
		if(player.isOp()){
			if(args.length==0){
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map set [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map setgems [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map setdeliverypet [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map next [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map settime [Time]");
			}else{
				if(args[0].equalsIgnoreCase("set")){
					getMaps().put(args[1].toLowerCase(), player.getLocation());
					config.setLocation("maps."+args[1].toLowerCase()+".Spawn", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("setgems")){
					config.setLocation("maps."+args[1].toLowerCase()+".GemShop", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("setdeliverypet")){
					config.setLocation("maps."+args[1].toLowerCase()+".DeliveryPet", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("next")){
					if(getMaps().containsKey(args[1].toLowerCase())){
						this.nextMap=args[1].toLowerCase();
						player.sendMessage(Language.getText(player, "PREFIX")+"§aDie nächste Map wird §7"+this.nextMap+"§a sein!");
					}
				}else if(args[0].equalsIgnoreCase("settime")){
					try{
						int time = Integer.valueOf(args[1]);
						
						setTime(time);
						player.sendMessage(Language.getText(player, "PREFIX")+"§aDie Zeit wurd auf §e"+getTime()+"§a geändert");
					}catch(NumberFormatException e){
						player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "NO_INTEGER",args[1]));
					}
				}else if(args[0].equalsIgnoreCase("list")){
					String maps ="";
					for(String map : getMaps().keySet())maps+=map;
					player.sendMessage(Language.getText(player, "PREFIX")+maps);
				}
			}
		}
		return false;
	}
}
