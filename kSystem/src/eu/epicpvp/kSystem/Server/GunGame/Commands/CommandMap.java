package eu.epicpvp.kSystem.Server.GunGame.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scoreboard.DisplaySlot;

import eu.epicpvp.kSystem.Server.GunGame.GunGame;
import eu.epicpvp.kSystem.Server.GunGame.rank.NPCRank;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Command.Admin.CommandVanish;
import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Lists.kSort;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilTime;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;
import lombok.Setter;

public class CommandMap extends kListener implements CommandExecutor {
	@Getter
	private kConfig config;
	@Getter
	private GunGame instance;
	@Getter
	@Setter
	private int counter = 0;
	@Getter
	@Setter
	private String nextMap;
	@Getter
	@Setter
	private String lastMap;
	@Getter
	@Setter
	private int time = 1;
	@Getter
	private HashMap<String, Location> maps;
	private ArrayList<kSort<String>> ranking;

	@Getter
	private NPCRank npc1;
	@Getter
	private NPCRank npc2;
	@Getter
	private NPCRank npc3;

	@SuppressWarnings("static-access")
	public CommandMap(GunGame instance) {
		super(instance.getInstance(), "CommandMap");

		this.instance = instance;
		this.ranking = new ArrayList<>();
		this.config = new kConfig(new File(instance.getInstance().getDataFolder(), "maps.yml"));

		this.maps = new HashMap<>();
		for (String map : config.getPathList("maps").keySet()) {
			if (instance.getSpawn() == null)
				instance.setSpawn(config.getLocation("maps." + map.toLowerCase() + ".Spawn"));
			getMaps().put(map.toLowerCase(), config.getLocation("maps." + map.toLowerCase() + ".Spawn"));
		}
	}

	public void clear() {
		for (Entity e : Bukkit.getWorld("gungame").getEntities()) {
			if (!(e instanceof Player) && !(e instanceof ItemFrame)) {
				if (UtilServer.getDeliveryPet() != null) {
					if (UtilServer.getDeliveryPet().getJockey() != null && UtilServer.getDeliveryPet().getJockey().getEntityId() == e.getEntityId())
						continue;
					if (UtilServer.getDeliveryPet().getEntity() != null && UtilServer.getDeliveryPet().getEntity().getEntityId() == e.getEntityId())
						continue;
				}

				if (UtilServer.getPerkManager() != null && UtilServer.getPerkManager().getEntity() != null && UtilServer.getPerkManager().getEntity().getEntityId() == e.getEntityId())
					continue;

				if (UtilServer.getGemsShop() != null && UtilServer.getGemsShop().getListener() != null && UtilServer.getGemsShop().getListener().getEntity() != null) {
					if (UtilServer.getGemsShop().getListener().getEntity().getEntityId() == e.getEntityId())
						continue;
				}

//				if (npc1 != null && npc1.getNpc() != null && npc1.getNpc().getEntityId() == e.getEntityId())
//					continue;
//				if (npc2 != null && npc2.getNpc() != null && npc2.getNpc().getEntityId() == e.getEntityId())
//					continue;
//				if (npc3 != null && npc3.getNpc() != null && npc3.getNpc().getEntityId() == e.getEntityId())
//					continue;

				e.remove();
			}
		}
	}

	public void updateNPCS() {
		ranking.clear();
		ArrayList<Player> invisible = CommandVanish.getInvisible();
		ranking.addAll(
				UtilServer.getPlayers().stream()
						.filter(player -> !invisible.contains(player))
						.map(player -> new kSort<>(player.getName(), player.getLevel()))
						.collect(Collectors.toList()));

		Collections.sort(ranking, kSort.DESCENDING);
		clear();
		if (npc1 == null)
			npc1 = new NPCRank(getNPCL1(), 1);
		if (npc2 == null)
			npc2 = new NPCRank(getNPCL2(), 2);
		if (npc3 == null)
			npc3 = new NPCRank(getNPCL3(), 3);
		if (!ranking.isEmpty()) {
			if (ranking.size() >= 1) {
				npc1.setPlayer(Bukkit.getPlayer(ranking.get(0).getObject()));
				if (ranking.size() >= 2) {
					npc2.setPlayer(Bukkit.getPlayer(ranking.get(1).getObject()));
					if (ranking.size() >= 3) {
						npc3.setPlayer(Bukkit.getPlayer(ranking.get(2).getObject()));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void checkNPC(UpdateEvent ev) {
		if (ev.getType() == UpdateType.SEC) {
			if (lastMap != null) {
				updateNPCS();
			}
		}
	}

	public Location getNPCL3() {
		return config.getLocation("maps." + lastMap.toLowerCase() + ".NPC3");
	}

	public Location getNPCL2() {
		return config.getLocation("maps." + lastMap.toLowerCase() + ".NPC2");
	}

	public Location getNPCL1() {
		return config.getLocation("maps." + lastMap.toLowerCase() + ".NPC1");
	}

	private String lastbtime;

	@EventHandler
	public void next(UpdateEvent ev) {
		if (ev.getType() == UpdateType.SEC && !this.maps.isEmpty()) {
			this.time--;

			String btime = UtilTime.formatSeconds(time);
			for (Player player : UtilServer.getPlayers()) {
				if (player.getScoreboard() != null) {
					if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
						if (lastbtime == null) {
							UtilScoreboard.resetScore(player.getScoreboard(), 1, DisplaySlot.SIDEBAR);
						} else {
							UtilScoreboard.resetScore(player.getScoreboard(), "§6Map wechseln in §f" + lastbtime, DisplaySlot.SIDEBAR);
						}
						UtilScoreboard.setScore(player.getScoreboard(), "§6Map wechseln in §f" + btime, DisplaySlot.SIDEBAR, 1);
					}
				}
			}
			lastbtime = btime;
			switch (this.time) {
				case 5:
					UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);
					break;
				case 4:
					UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);
					break;
				case 3:
					UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);
					break;
				case 2:
					UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);
					break;
				case 1:
					UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);
					break;
				case 0:
					if (nextMap == null) {
						this.nextMap = (String) this.maps.keySet().toArray()[this.counter];
						this.counter++;

						if (this.counter == this.maps.size()) {
							this.counter = 0;
						}
					}
					this.lastMap = nextMap;

					this.time = 60 * 30;
					if (npc1 != null) {
						npc1.remove();
					}
					if (npc2 != null) {
						npc2.remove();
					}
					if (npc3 != null) {
						npc3.remove();
					}
					npc1 = null;
					npc2 = null;
					npc3 = null;
					this.maps.get(this.nextMap).getChunk().load();
					getInstance().setSpawn(this.maps.get(this.nextMap));
					for (Player player : UtilServer.getPlayers())
						player.teleport(getInstance().getSpawn());

					clear();
					UtilServer.getLagMeter().unloadChunks(null, null);

					if (UtilServer.getGemsShop() != null) {
						if (config.contains("maps." + nextMap.toLowerCase() + ".GemShop")) {
							UtilServer.getGemsShop().setCreature(config.getLocation("maps." + nextMap.toLowerCase() + ".GemShop"));
						}
					}

					if (UtilServer.getDeliveryPet() != null) {
						if (config.contains("maps." + nextMap.toLowerCase() + ".DeliveryPet")) {
							UtilServer.getDeliveryPet().setLocation(config.getLocation("maps." + nextMap.toLowerCase() + ".DeliveryPet"));
							UtilServer.getDeliveryPet().getEntity().remove();
							UtilServer.getDeliveryPet().getJockey().remove();
							UtilServer.getDeliveryPet().createPet();
						}
					}
					this.nextMap = null;
					updateNPCS();
					UtilServer.broadcastLanguage("GUNGAME_MAP_CHANGE");
					break;
			}
		}
	}

	@CommandHandler.Command(command = "map", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		Player player = (Player) sender;

		if (player.isOp()) {
			if (args.length == 0) {
				player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§7/map set [Name]");
				player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§7/map setgems [Name]");
				player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§7/map setdeliverypet [Name]");
				player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§7/map next [Name]");
				player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§7/map settime [Time]");
			} else {
				if (args[0].equalsIgnoreCase("set")) {
					getMaps().put(args[1].toLowerCase(), player.getLocation());
					config.setLocation("maps." + args[1].toLowerCase() + ".Spawn", player.getLocation());
					config.save();
					player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "Die Map wurde gespeichert");
				} else if (args[0].equalsIgnoreCase("setgems")) {
					config.setLocation("maps." + args[1].toLowerCase() + ".GemShop", player.getLocation());
					config.save();
					player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "Die Map wurde gespeichert");
				} else if (args[0].equalsIgnoreCase("npc1")) {
					config.setLocation("maps." + args[1].toLowerCase() + ".NPC1", player.getLocation());
					config.save();
					player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "Die Map wurde gespeichert");
				} else if (args[0].equalsIgnoreCase("npc2")) {
					config.setLocation("maps." + args[1].toLowerCase() + ".NPC2", player.getLocation());
					config.save();
					player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "Die Map wurde gespeichert");
				} else if (args[0].equalsIgnoreCase("npc3")) {
					config.setLocation("maps." + args[1].toLowerCase() + ".NPC3", player.getLocation());
					config.save();
					player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "Die Map wurde gespeichert");
				} else if (args[0].equalsIgnoreCase("setdeliverypet")) {
					config.setLocation("maps." + args[1].toLowerCase() + ".DeliveryPet", player.getLocation());
					config.save();
					player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "Die Map wurde gespeichert");
				} else if (args[0].equalsIgnoreCase("next")) {
					if (getMaps().containsKey(args[1].toLowerCase())) {
						this.nextMap = args[1].toLowerCase();
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§aDie n§chste Map wird §7" + this.nextMap + "§a sein!");
					}
				} else if (args[0].equalsIgnoreCase("settime")) {
					try {
						int time = Integer.valueOf(args[1]);

						setTime(time);
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§aDie Zeit wurd auf §e" + getTime() + "§a ge§ndert");
					} catch (NumberFormatException e) {
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + TranslationHandler.getText(player, "NO_INTEGER", args[1]));
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					String maps = "";
					for (String map : getMaps().keySet())
						maps += map;
					player.sendMessage(TranslationHandler.getText(player, "PREFIX") + maps);
				}
			}
		}
		return false;
	}
}
