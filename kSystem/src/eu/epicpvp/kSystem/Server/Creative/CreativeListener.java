package eu.epicpvp.kSystem.Server.Creative;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.RestartScheduler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

public class CreativeListener extends kListener{

	private Creative instance;
	
	public CreativeListener(Creative instance) {
		super(instance.getInstance(), "CreativeListener");
		this.instance=instance;
	}

	@EventHandler
	public void Command(PlayerCommandPreprocessEvent ev){
		String cmd = "";
	    if (ev.getMessage().contains(" ")){
	      String[] parts = ev.getMessage().split(" ");
	      cmd = parts[0];
	    }else{
	      cmd = ev.getMessage();
	    }
	    
	    if(cmd.equalsIgnoreCase("/minecraft:")){
	    	ev.setCancelled(true);
	    	return;
	    }else if(cmd.equalsIgnoreCase("/p")
	    		|| cmd.equalsIgnoreCase("/plot")
	    		|| cmd.equalsIgnoreCase("/plots")
	    		|| cmd.equalsIgnoreCase("/plotsquared")
	    		|| cmd.equalsIgnoreCase("/ps")
	    		|| cmd.equalsIgnoreCase("/p2")
	    		|| cmd.equalsIgnoreCase("/2")){
				
	    	instance.getCreativeInventoryHandler().open(ev.getPlayer());
	    	ev.setCancelled(true);
	    	return;
		}else if(cmd.equalsIgnoreCase("/kp")){
			ev.setMessage(ev.getMessage().replaceAll("/kp", "/p"));
		}
	     
		if(ev.getPlayer().isOp()){
			if(cmd.equalsIgnoreCase("/reload")){
				ev.setCancelled(true);
				restart();
			}else if(cmd.equalsIgnoreCase("/restart")){
				ev.setCancelled(true);
				restart();
			}else if(cmd.equalsIgnoreCase("/stop")){
				ev.setCancelled(true);
				restart();
			}
		}
	}
	
//	@EventHandler
//	public void inv(InventoryCreativeEvent ev){
//		ev.setCancelled(false);
//	}
	
	public void restart(){
		RestartScheduler restart = new RestartScheduler(instance.getInstance());
		restart.setMoney(UtilServer.getGemsShop().getGems());
		restart.setStats(instance.getMoney());
		restart.start();
	}
	
	@EventHandler
	public void respawn(PlayerRespawnEvent ev){
		ev.setRespawnLocation(Bukkit.getWorld("plotworld").getSpawnLocation());
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		ev.setJoinMessage(null);
		UtilPlayer.setTab(ev.getPlayer(), "Creative-Server");
	}
}
