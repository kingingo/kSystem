package eu.epicpvp.kSystem.Server.GunGame.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Command.Commands.Events.AddKitEvent;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.kConfig.kConfig;

public class CommandSetKit implements CommandExecutor{

	private Player player;
	private kConfig config;
	
	public CommandSetKit(kConfig config){
		this.config=config;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "setkit", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		player=(Player)sender;
		
		if(player.isOp()){
			if(args.length==0){
				player.sendMessage(Language.getText(player, "PREFIX")+"/setkit [LEVEL]");
			}else{
				try{
					int i = Integer.valueOf(args[0]);
					String kit = "kit"+i;
					config.setItemStackArray("kits."+kit+".Content", player.getInventory().getContents());
					config.setItemStackArray("kits."+kit+".ArmorContent", player.getInventory().getArmorContents());
					
					AddKitEvent ev = new AddKitEvent(player, kit,0);
					Bukkit.getPluginManager().callEvent(ev);
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "KIT_SET",kit));
				}catch(NumberFormatException e){
					player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "NO_INTEGER",args[0]));
				}
				
			}
		}
		return false;
	}

}
