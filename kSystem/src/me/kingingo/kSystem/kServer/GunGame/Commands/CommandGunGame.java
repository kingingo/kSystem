package me.kingingo.kSystem.kServer.GunGame.Commands;

import java.io.File;
import java.util.HashMap;

import lombok.Getter;
import me.kingingo.kSystem.kServer.kServer;
import me.kingingo.kSystem.kServer.GunGame.kGunGame;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Command.Commands.CommandDelKit;
import me.kingingo.kcore.Command.Commands.Events.AddKitEvent;
import me.kingingo.kcore.Command.Commands.Events.DeleteKitEvent;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.Util.UtilNumber;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.kConfig.kConfig;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class CommandGunGame implements CommandExecutor{

	private Player player;
	private kGunGame gungame;
	
	public CommandGunGame(kGunGame gungame){
		this.gungame=gungame;
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "gungame",alias={"gg"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		player=(Player)sender;
		
		if(player.isOp()){
			if(args.length==0){
				player.sendMessage("/gungame reset [ALL/ONLINE/PLAYER]");
			}else{
				if(args[0].equalsIgnoreCase("reset")&&args.length==2){
					if(args[1].equalsIgnoreCase("all")){
						for(Player player : UtilServer.getPlayers()){
							gungame.getKit().setLevel(player, 1);
						}
						
						gungame.getInstance().getConfig().set("LevelResetAll", System.currentTimeMillis());
						player.sendMessage(Language.getText(player, "PREFIX")+"§cDer Levelstand von allen Spielern wurde resetet!");
					}else if(args[1].equalsIgnoreCase("online")){
						for(Player player : UtilServer.getPlayers()){
							gungame.getKit().setLevel(player, 1);
						}
						player.sendMessage(Language.getText(player, "PREFIX")+"§cDer Levelstand von allen Online Spielern wurde resetet!");
					}else{
						Player target = null;
						
						if(UtilPlayer.isOnline(args[1])){
							target=Bukkit.getPlayer(args[1]);
						}else{
							target=UtilPlayer.loadPlayer(UtilPlayer.getUUID(args[1], gungame.getStatsManager().getMysql()));
						}
						
						if(target!=null){
							gungame.getKit().setLevel(target, 1);
							player.sendMessage(Language.getText(player, "PREFIX")+"§cDie Level von diesem Spieler wurden resetet!");
						}else{
							player.sendMessage(Language.getText(player, "PREFIX")+"§cDieser Spieler wurde nicht gefunden!");
						}
					}
				}
			}
		}	
		return false;
	}

}
