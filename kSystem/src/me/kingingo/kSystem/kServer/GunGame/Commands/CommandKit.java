package me.kingingo.kSystem.kServer.GunGame.Commands;

import java.io.File;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kSystem.kServer.kServer;
import me.kingingo.kSystem.kServer.GunGame.Events.PlayerGunGameLevelUpEvent;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Command.Commands.CommandDelKit;
import me.kingingo.kcore.Command.Commands.Events.AddKitEvent;
import me.kingingo.kcore.Command.Commands.Events.DeleteKitEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Permission.kPermission;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Util.UtilEffect;
import me.kingingo.kcore.Util.UtilNumber;
import me.kingingo.kcore.Util.UtilParticle;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.kConfig.kConfig;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class CommandKit implements CommandExecutor{

	private Player player;
	@Getter
	private kConfig config;
	private kConfig userconfig;
	private String kit;
	@Getter
	private HashMap<String,ItemStack[]> kits_content = new HashMap<>();
	@Getter
	private HashMap<String,ItemStack[]> kits_armor = new HashMap<>();
	@Getter
	private kServer instance;
	
	public CommandKit(kServer instance){
		this.config=new kConfig(new File("plugins"+File.separator+instance.getInstance().getPlugin(instance.getInstance().getClass()).getName()+File.separator+"kits.yml"));
		this.instance=instance;
		for(String kit : config.getPathList("kits").keySet()){
			getKits_armor().put(kit, config.getItemStackArray("kits."+kit+".ArmorContent"));
			getKits_content().put(kit, config.getItemStackArray("kits."+kit+".Content"));
		}	
		
		instance.getCommandHandler().register(CommandDelKit.class, new CommandDelKit(config));
		instance.getCommandHandler().register(CommandSetKit.class, new CommandSetKit(config));
	}
	
	public void death(Player player){
		setLevel(player, Math.round((((float)player.getLevel())/100)*60));
	}
	
	public void setLevel(Player player, int level){
		if(level<=0){
			level=1;
			getInstance().getUserData().getConfig(player).set("Last_LevelUP", System.currentTimeMillis());
		}
		
		if(level<3&&player.hasPermission(kPermission.GUNGAME_START_LEVEL_3.getPermissionToString())){
			level=3;
			getInstance().getUserData().getConfig(player).set("Last_LevelUP", System.currentTimeMillis());
		}
		
		if(player.getScoreboard()!=null&&player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)!=null&&getInstance().getStatsManager().getInt(Stats.LEVEL, player)<level){
			getInstance().getStatsManager().setInt(player, level, Stats.LEVEL);
			UtilScoreboard.resetScore(player.getScoreboard(),3, DisplaySlot.SIDEBAR);
			UtilScoreboard.setScore(player.getScoreboard(), "�f"+getInstance().getStatsManager().getInt(Stats.LEVEL, player)+"  ", DisplaySlot.SIDEBAR, 3);
		}
		
		Bukkit.getPluginManager().callEvent(new PlayerGunGameLevelUpEvent(player, level));
		
		if(level == 10||level == 20||level == 30||level == 40||level == 50||level == 60||level == 70||level == 80||level == 90||level == 100||level == 110||level == 120||level == 130){
			UtilServer.broadcastLanguage("GUNGAME_LEVEL_UP", new Object[]{player.getName(),level,UtilTime.formatMili( (System.currentTimeMillis() - getInstance().getUserData().getConfig(player).getLong("Last_LevelUP")) )});

			UtilEffect.playHelix(player.getLocation(),UtilParticle.FIREWORKS_SPARK);
			getInstance().getUserData().getConfig(player).set("Last_LevelUP", System.currentTimeMillis());
			getInstance().getUserData().getConfig(player).set("Last_Level", level);
		}
		
		if(player.hasPermission(kPermission.GUNGAME_SKIP_LEVEL_10.getPermissionToString())&&level == 10||level == 20||level == 30||level == 40||level == 50||level == 60||level == 70||level == 80||level == 90||level == 100||level == 110||level == 120||level == 130){
			level++;
		}else if(player.hasPermission(kPermission.GUNGAME_SKIP_LEVEL_15.getPermissionToString()) && (level == 15||level == 30||level == 45||level == 60||level == 75||level == 90||level == 105||level == 120||level == 135)){
			level++;
		}else if(player.hasPermission(kPermission.GUNGAME_SKIP_LEVEL_20.getPermissionToString())&&(level == 20||level == 40||level == 60||level == 80||level == 100||level == 120||level == 140)){
			level++;
		}else if(player.hasPermission(kPermission.GUNGAME_SKIP_LEVEL_25.getPermissionToString()) && (level == 25||level == 50||level == 75||level == 100||level == 125||level == 150) ){
			level++;
		}
		
		player.setExp(0);
		player.setLevel(level);
		player.setFoodLevel(20);
		if(kits_content.containsKey("kit"+level)){
			player.getInventory().setContents(kits_content.get("kit"+level));
			player.getInventory().setArmorContents(kits_armor.get("kit"+level));
		}
	}
	
	@EventHandler
	public void added(AddKitEvent ev){
		getKits_content().put(ev.getKit(), config.getItemStackArray("kits."+ev.getKit()+".Content"));
		getKits_armor().put(ev.getKit(), config.getItemStackArray("kits."+ev.getKit()+".ArmorContent"));
	}
	
	@EventHandler
	public void delete(DeleteKitEvent ev){
		if(getKits_content().containsKey(ev.getKit())){
			getKits_content().remove(ev.getKit());
			getKits_armor().remove(ev.getKit());
		}
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "kit", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		player=(Player)sender;
		
		if(player.isOp()){
			if(args.length==0){
				player.sendMessage(Language.getText(player, "PREFIX")+"/kit [Level]");
				String kits="";
				for(String kit : this.kits_content.keySet())kits+=kit+",";
				player.sendMessage(Language.getText(player, "PREFIX")+"Kits: "+(kits.equalsIgnoreCase("") ? Language.getText(player, "KITS_EMPTY") : kits.substring(0, kits.length()-1)));
			}else{
					if(args[0].equalsIgnoreCase("all")){
						try{
							int l = Integer.valueOf(args[1].toLowerCase());
							if(kits_content.containsKey("kit"+l)){
								for(Player p : UtilServer.getPlayers()){
									setLevel(p, l);
									p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "KIT_USE",kit));
								}
							}else{
								player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "KIT_EXIST"));
							}
							
						}catch(NumberFormatException e){
							player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "KIT_EXIST"));
						}
					}else{
						try{
							int l = Integer.valueOf(args[0].toLowerCase());

							if(kits_content.containsKey("kit"+l)){
								setLevel(player, l);
								player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "KIT_USE",kit));
							}else{
								player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "KIT_EXIST"));
							}
							
						}catch(NumberFormatException e){
							player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "KIT_EXIST"));
						}
					}
			}
		}	
		return false;
	}

}
