package me.kingingo.kSystem.kServer.GunGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import lombok.Getter;
import me.kingingo.kcore.Enum.GameType;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketReceiveEvent;
import me.kingingo.kcore.Packet.Packets.PLAYER_VOTE;
import me.kingingo.kcore.Packet.Packets.TWITTER_PLAYER_FOLLOW;
import me.kingingo.kcore.Packet.Packets.WORLD_CHANGE_DATA;
import me.kingingo.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import me.kingingo.kcore.StatsManager.Stats;
import me.kingingo.kcore.StatsManager.Event.PlayerStatsCreateEvent;
import me.kingingo.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.TabTitle;
import me.kingingo.kcore.Util.UtilNumber;
import me.kingingo.kcore.Util.UtilPlayer;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

public class kGunGameListener extends kListener{

	@Getter
	private kGunGame instance;
	private ArrayList<UUID> vote_list;
	private HashMap<Player,Player> last_hit;
	
	public kGunGameListener(kGunGame instance) {
		super(instance.getInstance(), "kGunGameListener");
		this.instance=instance;
		this.last_hit=new HashMap<>();
		this.vote_list= new ArrayList<>();
	}
	
	Player player;
	@EventHandler
	public void PacketReceive(PacketReceiveEvent ev){
		if(ev.getPacket() instanceof WORLD_CHANGE_DATA){
			WORLD_CHANGE_DATA packet = (WORLD_CHANGE_DATA)ev.getPacket();
			UtilPlayer.setWorldChangeUUID(Bukkit.getWorld(packet.getWorldName()), packet.getOld_uuid(), packet.getNew_uuid());
		}else if(ev.getPacket() instanceof PLAYER_VOTE){
			PLAYER_VOTE vote = (PLAYER_VOTE)ev.getPacket();
			
			if(UtilPlayer.isOnline(vote.getPlayer())){
				player=Bukkit.getPlayer(vote.getPlayer());
				if(UtilServer.getDeliveryPet()!=null){
					UtilServer.getDeliveryPet().deliveryUSE(player, "§aVote for EpicPvP", true);
				}
				getInstance().getKit().setLevel(player, player.getLevel()+1);
				player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "VOTE_THX"));
			}else{
				vote_list.add(vote.getUuid());
			}
		}else if(ev.getPacket() instanceof TWITTER_PLAYER_FOLLOW){
			TWITTER_PLAYER_FOLLOW tw = (TWITTER_PLAYER_FOLLOW)ev.getPacket();
			
			if(UtilPlayer.isOnline(tw.getPlayer())){
				Player p = Bukkit.getPlayer(tw.getPlayer());
				if(!tw.isFollow()){
					getInstance().getInstance().getMysql().Update("DELETE FROM BG_TWITTER WHERE uuid='" + UtilPlayer.getRealUUID(p) + "'");
					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_FOLLOW_N"));
					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_REMOVE"));
				}else{
					UtilServer.getDeliveryPet().deliveryBlock(p, "§cTwitter Reward");
					getInstance().getKit().setLevel(player, player.getLevel()+1);
					p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","300"}));
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void foodLevelChange(FoodLevelChangeEvent ev){
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent ev){
		ev.setCancelled(true);
	}

	@EventHandler
	public void ex(ExplosionPrimeEvent ev){
		ev.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void spawn(CreatureSpawnEvent ev){
		if(ev.getSpawnReason()!=SpawnReason.CUSTOM){
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void loadedStats(PlayerStatsLoadedEvent ev){
		if(vote_list.contains( UtilPlayer.getRealUUID(ev.getPlayer()) )){
			 if(UtilServer.getDeliveryPet()!=null){
				 UtilServer.getDeliveryPet().deliveryUSE(ev.getPlayer(), "§aVote for EpicPvP", true);
			 }
			 vote_list.remove(UtilPlayer.getRealUUID(ev.getPlayer()));
			 getInstance().getKit().setLevel(player, player.getLevel()+1);
			 ev.getPlayer().sendMessage(Language.getText(ev.getPlayer(), "PREFIX")+Language.getText(ev.getPlayer(), "VOTE_THX"));
		 }
	}
	
	@EventHandler
	public void setboard(PlayerSetScoreboardEvent ev){
		UtilScoreboard.addBoard(ev.getPlayer().getScoreboard(), DisplaySlot.SIDEBAR, "§f§lEPICPVP - GUNGAME");
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "    ", DisplaySlot.SIDEBAR, 11);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§cKills:", DisplaySlot.SIDEBAR, 10);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.KILLS, ev.getPlayer())+" ", DisplaySlot.SIDEBAR, 9);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "   ", DisplaySlot.SIDEBAR, 8);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§cDeaths:", DisplaySlot.SIDEBAR, 7);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.DEATHS, ev.getPlayer()), DisplaySlot.SIDEBAR, 6);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "  ", DisplaySlot.SIDEBAR, 5);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§cMax Level:", DisplaySlot.SIDEBAR, 4);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.LEVEL, ev.getPlayer())+"  ", DisplaySlot.SIDEBAR, 3);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), " ", DisplaySlot.SIDEBAR, 2);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§cMap Change in", DisplaySlot.SIDEBAR, 1);
		UtilScoreboard.addLiveBoard(ev.getPlayer().getScoreboard(), ChatColor.RED+""+ChatColor.BOLD+"❤");
		getInstance().getKit().setLevel(ev.getPlayer(), ev.getPlayer().getLevel());
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent ev){
		ev.setQuitMessage(null);
		getInstance().getStatsManager().SaveAllPlayerData(ev.getPlayer());
	}
	
	@EventHandler
	public void join(PlayerJoinEvent ev){
		for(Player player : UtilServer.getPlayers()){
			player.hidePlayer(ev.getPlayer());
			player.showPlayer(ev.getPlayer());
			ev.getPlayer().showPlayer(player);
		}
		getInstance().getStatsManager().loadPlayerStats(ev.getPlayer());
		ev.setJoinMessage(null);
		ev.getPlayer().teleport(getInstance().getSpawn());
		TabTitle.setHeaderAndFooter(ev.getPlayer(), "§eEpicPvP§8.§eeu §8| §aGunGame-Server", "§aTeamSpeak: §7ts.EpicPvP.eu §8| §eWebsite: §7EpicPvP.eu");
	}
	
	@EventHandler
	public void water(UpdateEvent ev){
		if(ev.getType()==UpdateType.FASTER){
			try{
				for(Player player : UtilServer.getPlayers()){
					if(player.getLocation().getBlock().getType()==Material.STATIONARY_WATER){
						if(last_hit.containsKey(player)&&last_hit.get(player).getScoreboard()!=null&&player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)!=null){
							getInstance().getStatsManager().addInt(last_hit.get(player), 1, Stats.KILLS);
							UtilScoreboard.resetScore(last_hit.get(player).getScoreboard(), 9, DisplaySlot.SIDEBAR);
							UtilScoreboard.setScore(last_hit.get(player).getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.KILLS, last_hit.get(player))+" ", DisplaySlot.SIDEBAR,9);
							getInstance().getKit().setLevel(last_hit.get(player), last_hit.get(player).getLevel()+1);
							last_hit.remove(player);
						}

						getInstance().getStatsManager().addInt(player, 1, Stats.DEATHS);
						UtilScoreboard.resetScore(player.getScoreboard(), 6, DisplaySlot.SIDEBAR);
						UtilScoreboard.setScore(player.getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.DEATHS, player)+" ", DisplaySlot.SIDEBAR, 6);
						player.teleport(getInstance().getSpawn());
						getInstance().getKit().setLevel(player);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	Player v;
	Player a;
	@EventHandler(priority=EventPriority.LOWEST)
	public void damage(EntityDamageByEntityEvent ev){
		if(ev.isCancelled())return;
		if(ev.getEntity() instanceof Player){
			v=(Player)ev.getEntity();
			
			if(UtilPlayer.getHealth(v)-ev.getDamage() <=0){
				v.setHealth(1);
				ev.setCancelled(true);
				ev.setDamage(0);
				getInstance().getStatsManager().addInt(v, 1, Stats.DEATHS);
				UtilScoreboard.resetScore(v.getScoreboard(), 6, DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(v.getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.DEATHS, v)+" ", DisplaySlot.SIDEBAR, 6);
				v.teleport(getInstance().getSpawn());
				v.setHealth(v.getMaxHealth());
				getInstance().getKit().setLevel(v);
				last_hit.remove(v);

				
				if(ev.getDamager() instanceof Player){
					a=(Player)ev.getDamager();
					a.sendMessage(Language.getText(a,"PREFIX_GAME",GameType.GUNGAME.getTyp())+Language.getText(v,"KILL_BY", new String[]{v.getName(),a.getName()}));
					v.sendMessage(Language.getText(v,"PREFIX_GAME",GameType.GUNGAME.getTyp())+Language.getText(v,"KILL_BY", new String[]{v.getName(),a.getName()}));
					getInstance().getKit().setLevel(a, a.getLevel()+1);
					UtilPlayer.addPotionEffect(a, PotionEffectType.REGENERATION, 5, 2);
					getInstance().getStatsManager().addInt(a, 1, Stats.KILLS);
					UtilScoreboard.resetScore(a.getScoreboard(), 9, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(a.getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.KILLS, a)+" ", DisplaySlot.SIDEBAR,9);
				}else if(ev.getDamager() instanceof Projectile && ((Projectile)ev.getDamager()).getShooter() instanceof Player){
					a=(Player)((Projectile)ev.getDamager()).getShooter();
					a.sendMessage(Language.getText(a,"PREFIX_GAME",GameType.GUNGAME.getTyp())+Language.getText(v,"KILL_BY", new String[]{v.getName(),a.getName()}));
					v.sendMessage(Language.getText(v,"PREFIX_GAME",GameType.GUNGAME.getTyp())+Language.getText(v,"KILL_BY", new String[]{v.getName(),a.getName()}));
					getInstance().getKit().setLevel(a, a.getLevel()+1);
					UtilPlayer.addPotionEffect(a, PotionEffectType.REGENERATION, 5, 2);
					getInstance().getStatsManager().addInt(a, 1, Stats.KILLS);
					UtilScoreboard.resetScore(a.getScoreboard(),9, DisplaySlot.SIDEBAR);
					UtilScoreboard.setScore(a.getScoreboard(), "§f"+getInstance().getStatsManager().getInt(Stats.KILLS, a)+" ", DisplaySlot.SIDEBAR, 9);
				}else{
					v.sendMessage(Language.getText(v,"PREFIX_GAME",GameType.GUNGAME.getTyp())+Language.getText(v,"DEATH", v.getName()));
				}
				return;
			}

			if(v.getInventory().getHelmet()!=null && v.getInventory().getHelmet().getDurability() >= (v.getInventory().getHelmet().getType().getMaxDurability()-5)){
				v.getInventory().getHelmet().setDurability((short)0);
			}
			
			if(v.getInventory().getChestplate()!=null && v.getInventory().getChestplate().getDurability() >= (v.getInventory().getChestplate().getType().getMaxDurability()-5)){
				v.getInventory().getChestplate().setDurability((short)0);
			}
			
			if(v.getInventory().getLeggings()!=null && v.getInventory().getLeggings().getDurability() >= (v.getInventory().getLeggings().getType().getMaxDurability()-5)){
				v.getInventory().getLeggings().setDurability((short)0);
			}
			
			if(v.getInventory().getBoots()!=null && v.getInventory().getBoots().getDurability() >= (v.getInventory().getBoots().getType().getMaxDurability()-5)){
				v.getInventory().getBoots().setDurability((short)0);
			}

			if(ev.getDamager() instanceof Player){
				a=(Player)ev.getDamager();
				
				if(a.getItemInHand().getType()!=null && a.getItemInHand().getDurability() >= (a.getItemInHand().getType().getMaxDurability()-5)){
					a.getItemInHand().setDurability((short)0);
				}
				
				last_hit.put(v, a);
			}else if(ev.getDamager() instanceof Projectile && ((Projectile)ev.getDamager()).getShooter() instanceof Player){
				a=(Player)((Projectile)ev.getDamager()).getShooter();
				last_hit.put(v, a);
			}
		}
	}
}
