package eu.epicpvp.kSystem.Server.GunGame.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class PlayerGunGameLevelUpEvent extends Event{
	private static HandlerList handlers = new HandlerList();
	@Getter
	private Player player;
	@Getter
	private int level;
	
	public PlayerGunGameLevelUpEvent(Player player,int level){
		this.level=level;
		this.player=player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
        return handlers;
    }

}
