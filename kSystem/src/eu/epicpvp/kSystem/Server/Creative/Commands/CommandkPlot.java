package eu.epicpvp.kSystem.Server.Creative.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;

import eu.epicpvp.kSystem.Server.Creative.Creative;
import eu.epicpvp.kSystem.Server.Creative.Inventory.Buttons.InviteButton;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Util.UtilPlayer;
import lombok.Getter;

public class CommandkPlot implements CommandExecutor{
	@Getter
	private Creative instance;
	
	public CommandkPlot(Creative instance){
		this.instance=instance;
	}
	
	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "kplot", alias={"gs","plot"}, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		Player player = (Player)sender;
		
		if(args.length==0){
			if(instance == null)System.out.println("INSTANCE == NULL");
			if(instance.getCreativeInventoryHandler() == null)System.out.println("instance.getCreativeInventoryHandler() == NULL");
			
			instance.getCreativeInventoryHandler().open(player);
		}else{
			if(args[0].equalsIgnoreCase("accept")&&args.length==2){
				if(getInstance().getInvite().containsKey(player.getName())){
					if(getInstance().getInvite().get(player.getName()).equalsIgnoreCase(args[1])){
						getInstance().getInvite().remove(player.getName());
						
						if(UtilPlayer.isOnline(args[1])){
							PlotPlayer owner = getInstance().getPlotApi().wrapPlayer(Bukkit.getPlayer(args[1]));
							Plot plot = owner.getLocation().getPlot();
							if(plot.getMembers().size()>=3)return false;
							
							plot.addMember(player.getUniqueId());
							InviteButton.plotCache.refresh(player.getUniqueId());
						}
					}
				}
			}
		}
		
		return true;
	}

}
