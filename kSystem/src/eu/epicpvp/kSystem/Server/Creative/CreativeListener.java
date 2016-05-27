package eu.epicpvp.kSystem.Server.Creative;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.epicpvp.kcore.Listener.kListener;

public class CreativeListener extends kListener{

	private Creative instance;
	
	public CreativeListener(Creative instance) {
		super(instance.getInstance(), "CreativeListener");
		this.instance=instance;
	}

	@EventHandler
	public void join(PlayerJoinEvent ev){
		ev.getPlayer().teleport(Bukkit.getWorld("plot").getSpawnLocation());
	}
}
