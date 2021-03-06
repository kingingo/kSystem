package eu.epicpvp.kSystem.Server.GunGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.kcore.Events.ServerStatusUpdateEvent;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsChangedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.UserDataConfig.Events.UserDataConfigLoadEvent;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilEvent;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public class GunGameListener extends kListener {

	@Getter
	private GunGame instance;
	private ArrayList<UUID> vote_list;
	private HashMap<Player, Player> last_hit;
	private HashMap<Player, Location> last_hit_loc;

	private HashMap<Player, String> kills_score;
	private HashMap<Player, String> deaths_score;
	private ArrayList<Player> kills_update;
	private ArrayList<Player> deaths_update;

	public GunGameListener(GunGame instance) {
		super(instance.getInstance(), "kGunGameListener");
		this.instance = instance;
		this.last_hit = new HashMap<>();
		this.last_hit_loc = new HashMap<>();
		this.vote_list = new ArrayList<>();
		this.kills_score = new HashMap<>();
		this.deaths_score = new HashMap<>();
		this.kills_update = new ArrayList<>();
		this.deaths_update = new ArrayList<>();
	}

	@EventHandler
	public void update(ServerStatusUpdateEvent ev) {
		ev.getPacket().setPlayers(UtilServer.getPlayers().size());
		ev.getPacket().setTyp(GameType.GUNGAME);
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		int id = event.getBlock().getTypeId();
		if (id == 8 || id == 9) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void destroyCAN(PlayerInteractEvent ev) {
		if ((UtilEvent.isAction(ev, ActionType.PHYSICAL) && (ev.getClickedBlock().getType() == Material.SOIL))) {
			ev.setCancelled(true);
		}
	}

//	Player player;
//	@EventHandler
//	public void PacketReceive(PacketReceiveEvent ev){
//		if(ev.getPacket() instanceof WORLD_CHANGE_DATA){
//			WORLD_CHANGE_DATA packet = (WORLD_CHANGE_DATA)ev.getPacket();
//			UtilPlayer.setWorldChangeUUID(Bukkit.getWorld(packet.getWorldName()), packet.getOld_uuid(), packet.getNew_uuid());
//		}else if(ev.getPacket() instanceof PLAYER_VOTE){
//			PLAYER_VOTE vote = (PLAYER_VOTE)ev.getPacket();
//
//			if(UtilPlayer.isOnline(vote.getPlayer())){
//				player=Bukkit.getPlayer(vote.getPlayer());
//				if(UtilServer.getDeliveryPet()!=null){
//					UtilServer.getDeliveryPet().deliveryUSE(player, "§aVote for EpicPvP", true);
//				}
//				getInstance().getKit().setLevel(player, player.getLevel()+2);
//				player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "VOTE_THX"));
//			}else{
//				vote_list.add(vote.getUuid());
//			}
//		}else if(ev.getPacket() instanceof TWITTER_PLAYER_FOLLOW){
//			TWITTER_PLAYER_FOLLOW tw = (TWITTER_PLAYER_FOLLOW)ev.getPacket();
//
//			if(UtilPlayer.isOnline(tw.getPlayer())){
//				Player p = Bukkit.getPlayer(tw.getPlayer());
//				if(!tw.isFollow()){
//					getInstance().getInstance().getMysql().Update("DELETE FROM BG_TWITTER WHERE uuid='" + UtilPlayer.getRealUUID(p) + "'");
//					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_FOLLOW_N"));
//					p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_REMOVE"));
//				}else{
//					UtilServer.getDeliveryPet().deliveryBlock(p, "§cTwitter Reward");
//					getInstance().getKit().setLevel(p, p.getLevel()+1);
//					p.sendMessage(Language.getText(p, "PREFIX")+Language.getText(p, "MONEY_RECEIVE_FROM", new String[]{"§bThe Delivery Jockey!","300"}));
//				}
//			}
//		}
//	}

	@EventHandler
	public void PlayerArmorStandManipulate(PlayerArmorStandManipulateEvent ev) {
		if (!ev.getPlayer().isOp()) ev.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void foodLevelChange(FoodLevelChangeEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler
	public void drop(PlayerDropItemEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler
	public void ex(ExplosionPrimeEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void spawn(CreatureSpawnEvent ev) {
		if (ev.getSpawnReason() != SpawnReason.CUSTOM) {
			ev.setCancelled(true);
		}
	}

//	@EventHandler
//	public void loadedStats(PlayerStatsLoadedEvent ev){
//		if(UtilPlayer.isOnline(ev.getPlayerId())){
//			Player player = UtilPlayer.searchExact(ev.getPlayerId());
//
//			if(ev.getManager().getType() != GameType.Money){
//				if(vote_list.contains( UtilPlayer.getRealUUID(player) )){
//					 if(UtilServer.getDeliveryPet()!=null){
//						 UtilServer.getDeliveryPet().deliveryUSE(player, "§aVote for EpicPvP", true);
//					 }
//					 vote_list.remove(UtilPlayer.getRealUUID(player));
//					 getInstance().getKit().setLevel(player, player.getLevel()+1);
//					 player.sendMessage(TranslationManager.getText(player, "PREFIX")+TranslationManager.getText(player, "VOTE_THX"));
//				 }
//			}
//		}
//	}

	@EventHandler
	public void statsUpdate(UpdateEvent ev) {
		if (ev.getType() == UpdateType.SEC_3) {
			for (Player player : kills_update) {
				try {
					if (player.getScoreboard() == null) continue;
					if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) continue;
					if (kills_score.containsKey(player)) {
						UtilScoreboard.resetScore(player.getScoreboard(), kills_score.get(player), DisplaySlot.SIDEBAR);
					} else {
						UtilScoreboard.resetScore(player.getScoreboard(), 9, DisplaySlot.SIDEBAR);
					}

					kills_score.put(player, "§f" + getInstance().getStatsManager().getInt(player, StatsKey.KILLS) + " ");
					UtilScoreboard.setScore(player.getScoreboard(), "§f" + getInstance().getStatsManager().getInt(player, StatsKey.KILLS) + " ", DisplaySlot.SIDEBAR, 9);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			kills_update.clear();

			for (Player player : deaths_update) {
				try {
					if (player.getScoreboard() == null) continue;
					if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) continue;
					if (deaths_score.containsKey(player)) {
						UtilScoreboard.resetScore(player.getScoreboard(), deaths_score.get(player), DisplaySlot.SIDEBAR);
					} else {
						UtilScoreboard.resetScore(player.getScoreboard(), 6, DisplaySlot.SIDEBAR);
					}

					deaths_score.put(player, "§f" + getInstance().getStatsManager().getInt(player, StatsKey.DEATHS) + " ");
					UtilScoreboard.setScore(player.getScoreboard(), "§f" + getInstance().getStatsManager().getInt(player, StatsKey.DEATHS) + " ", DisplaySlot.SIDEBAR, 6);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			deaths_update.clear();
		}
	}

	@EventHandler
	public void statsChange(PlayerStatsChangedEvent ev) {
		if (UtilPlayer.isOnline(ev.getPlayerId())) {
			Player player = UtilPlayer.searchExact(ev.getPlayerId());
			if (ev.getManager().getType() != GameType.Money) {
				if (player.getScoreboard() == null) return;
				if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) == null) return;
				if (ev.getStats() == StatsKey.KILLS && !kills_update.contains(player)) {
					kills_update.add(player);
				} else if (ev.getStats() == StatsKey.DEATHS && !deaths_update.contains(player)) {
					deaths_update.add(player);
				}
			}
		}
	}

	@EventHandler
	public void setboard(PlayerSetScoreboardEvent ev) {
		UtilScoreboard.addBoard(ev.getPlayer().getScoreboard(), DisplaySlot.SIDEBAR, UtilScoreboard.getScoreboardDisplayname() + " - GUNGAME");
		int i = 14;
		i++;
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "    ", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§bTeaming / Farming verboten.", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§bSpawncamping ab Level 15 verboten.", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§0", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§aKills:", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§f" + getInstance().getStatsManager().getInt(ev.getPlayer(), StatsKey.KILLS) + " ", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "   ", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§aDeaths:", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§f" + getInstance().getStatsManager().getInt(ev.getPlayer(), StatsKey.DEATHS), DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "  ", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§aHöchstes Level:", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), "§f" + getInstance().getStatsManager().getInt(ev.getPlayer(), StatsKey.LEVEL) + "  ", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.setScore(ev.getPlayer().getScoreboard(), " ", DisplaySlot.SIDEBAR, --i);
		UtilScoreboard.addLiveBoard(ev.getPlayer().getScoreboard(), ChatColor.RED + "" + ChatColor.BOLD + "§¤");
		getInstance().getKit().setLevel(ev.getPlayer(), ev.getPlayer().getLevel());
	}

	@EventHandler
	public void config(UserDataConfigLoadEvent ev) {
		if (getInstance().getInstance().getConfig().getLong("LevelResetAll") > ev.getConfig().getLong("lastLogin")) {
			getInstance().getKit().setLevel(ev.getPlayer(), 1);
		} else {
			if ((System.currentTimeMillis() - ev.getConfig().getLong("lastLogin")) > TimeSpan.HOUR * 2) {
				getInstance().getKit().setLevel(ev.getPlayer(), 1);
			} else {
				getInstance().getKit().setLevel(ev.getPlayer(), ev.getPlayer().getLevel());
			}
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent ev) {
		if (this.last_hit.containsKey(ev.getPlayer())) this.last_hit.remove(ev.getPlayer());

		getInstance().getUserData().getConfig(ev.getPlayer()).set("lastLogin", System.currentTimeMillis());
		ev.setQuitMessage(null);
	}

	@EventHandler
	public void join(PlayerJoinEvent ev) {
		ev.getPlayer().setGameMode(GameMode.ADVENTURE);
		getInstance().getStatsManager().loadPlayer(ev.getPlayer());
		getInstance().getMoney().loadPlayer(ev.getPlayer());
		ev.setJoinMessage(null);
		ev.getPlayer().teleport(getInstance().getSpawn());
		UtilPlayer.setTab(ev.getPlayer(), "GunGame-Server");
	}

//	Packet s;
//	@EventHandler
//	public void send(UpdateAsyncEvent ev){
//		if(ev.getType()==UpdateAsyncType.SEC_4){
//			if(s==null)s= new SERVER_STATUS(GameState.LobbyPhase, UtilServer.getPlayers().size(), Bukkit.getMaxPlayers(),"MAP",GameType.GUNGAME,getInstance().getInstance().getConfig().getString("Config.ID"), false);
//			s.setOnline(UtilServer.getPlayers().size());
//			s.setMap(getInstance().getMap().getLastMap());
//			getInstance().getInstance().getPacketManager().SendPacket("hub",s);
//		}
//	}

	@EventHandler
	public void water(PlayerMoveEvent ev) {
		try {
			Player player = ev.getPlayer();
			if (player.getGameMode() == GameMode.CREATIVE) {
				return;
			}
			if (player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
				if (last_hit.containsKey(player) && last_hit.get(player).getScoreboard() != null && player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
					if (UtilWorldGuard.RegionFlag(last_hit_loc.get(player), DefaultFlag.PVP)) {
						last_hit.get(player).sendMessage(TranslationHandler.getText(last_hit.get(player), "PREFIX_GAME", GameType.GUNGAME.getTyp()) + TranslationHandler.getText(last_hit.get(player), "GUNGAME_KILL", player.getName()));
						player.sendMessage(TranslationHandler.getText(player, "PREFIX_GAME", GameType.GUNGAME.getTyp()) + TranslationHandler.getText(player, "GUNGAME_KILLED_BY", last_hit.get(player).getName()));
						player.sendMessage(TranslationHandler.getText(player, "PREFIX_GAME", GameType.GUNGAME.getTyp()) + TranslationHandler.getText(player, "HEART", new String[]{last_hit.get(player).getName(), UtilPlayer.getHealthBar(last_hit.get(player))}));

						UtilPlayer.addPotionEffect(last_hit.get(player), PotionEffectType.REGENERATION, 3, 4);
						getInstance().getStatsManager().add(last_hit.get(player), StatsKey.KILLS, 1);
						getInstance().getKit().setLevel(last_hit.get(player), last_hit.get(player).getLevel() + 1);
						last_hit.get(player).playSound(last_hit.get(player).getLocation(), Sound.LEVEL_UP, 1f, 1f);
					}
					last_hit_loc.remove(player);
					last_hit.remove(player);
				}

				getInstance().getStatsManager().add(player, StatsKey.DEATHS, 1);
				player.teleport(getInstance().getSpawn());
				player.setHealth(player.getMaxHealth());
				getInstance().getKit().death(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent ev) {
		if (ev.getEntity() instanceof Player) {
			Player damaged = (Player) ev.getEntity();

			Player damamger;
			if (ev.getDamager() instanceof Player) {
				damamger = (Player) ev.getDamager();

				if (damamger.getItemInHand().getType() != null && damamger.getItemInHand().getDurability() >= (damamger.getItemInHand().getType().getMaxDurability() - 5)) {
					damamger.getItemInHand().setDurability((short) 0);
				}

				last_hit_loc.put(damaged, damamger.getLocation());
				last_hit.put(damaged, damamger);
			} else if (ev.getDamager() instanceof Projectile && ((Projectile) ev.getDamager()).getShooter() instanceof Player) {
				damamger = (Player) ((Projectile) ev.getDamager()).getShooter();
				last_hit.put(damaged, damamger);
				last_hit_loc.put(damaged, damamger.getLocation());
			}

			if (damaged.getInventory().getHelmet() != null && damaged.getInventory().getHelmet().getDurability() >= (damaged.getInventory().getHelmet().getType().getMaxDurability() - 5)) {
				damaged.getInventory().getHelmet().setDurability((short) 0);
			}

			if (damaged.getInventory().getChestplate() != null && damaged.getInventory().getChestplate().getDurability() >= (damaged.getInventory().getChestplate().getType().getMaxDurability() - 5)) {
				damaged.getInventory().getChestplate().setDurability((short) 0);
			}

			if (damaged.getInventory().getLeggings() != null && damaged.getInventory().getLeggings().getDurability() >= (damaged.getInventory().getLeggings().getType().getMaxDurability() - 5)) {
				damaged.getInventory().getLeggings().setDurability((short) 0);
			}

			if (damaged.getInventory().getBoots() != null && damaged.getInventory().getBoots().getDurability() >= (damaged.getInventory().getBoots().getType().getMaxDurability() - 5)) {
				damaged.getInventory().getBoots().setDurability((short) 0);
			}
		}
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent ev) {
		ev.setRespawnLocation(getInstance().getSpawn());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void death(PlayerDeathEvent ev) {
		Player deadPlr = ev.getEntity();
		ev.setDeathMessage(null);
		ev.setDroppedExp(0);
		ev.setKeepInventory(true);
		ev.setKeepLevel(true);
		getInstance().getKit().death(deadPlr);

		getInstance().getStatsManager().add(deadPlr, StatsKey.DEATHS, 1);
		last_hit_loc.remove(deadPlr);
		last_hit.remove(deadPlr);
		UtilPlayer.RespawnNow(deadPlr, getInstance().getInstance());

		if (deadPlr.getKiller() != null) {
			Player killer = deadPlr.getKiller();
			killer.sendMessage(TranslationHandler.getText(killer, "PREFIX_GAME", GameType.GUNGAME.getTyp()) + TranslationHandler.getText(killer, "GUNGAME_KILL", deadPlr.getName()));
			killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1f, 1f);
			deadPlr.sendMessage(TranslationHandler.getText(deadPlr, "PREFIX_GAME", GameType.GUNGAME.getTyp()) + TranslationHandler.getText(deadPlr, "GUNGAME_KILLED_BY", killer.getName()));
			deadPlr.sendMessage(TranslationHandler.getText(deadPlr, "PREFIX_GAME", GameType.GUNGAME.getTyp()) + TranslationHandler.getText(deadPlr, "HEART", new String[]{killer.getName(), UtilPlayer.getHealthBar(killer)}));
			getInstance().getKit().setLevel(killer, killer.getLevel() + 1);
			UtilPlayer.addPotionEffect(killer, PotionEffectType.REGENERATION, 3, 4);
			getInstance().getStatsManager().add(killer, StatsKey.KILLS, 1);
		} else {
			deadPlr.sendMessage(TranslationHandler.getText(deadPlr, "PREFIX_GAME", GameType.GUNGAME.getTyp()) + TranslationHandler.getText(deadPlr, "DEATH", deadPlr.getName()));
		}
	}
}
