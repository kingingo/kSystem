package me.kingingo.kSystem.kServer.GunGame;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import me.kingingo.kSystem.kServer.GunGame.Events.PlayerGunGameLevelUpEvent;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Util.TimeSpan;

public class AntiKillFarm extends kListener{

	private HashMap<Player,Long> level_time;
	
	public AntiKillFarm(JavaPlugin instance) {
		super(instance, "AntiKillFarm");
		this.level_time=new HashMap<>();
	}

	@EventHandler
	public void levelup(PlayerGunGameLevelUpEvent ev){
		if(level_time.containsKey(ev.getPlayer())){
			if((((float)ev.getLevel())%5)==0){
				if(System.currentTimeMillis() - level_time.get(ev.getPlayer()) > TimeSpan.MINUTE*1){
					
				}
			}
		}else{
			this.level_time.put(ev.getPlayer(), System.currentTimeMillis());
		}
	}
}
