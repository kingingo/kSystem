package eu.epicpvp.kSystem.Server.GunGame;

import org.bukkit.Location;
import org.bukkit.WorldCreator;

import dev.wolveringer.bukkit.permissions.GroupTyp;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.ServerType;
import eu.epicpvp.kSystem.kServerSystem;
import eu.epicpvp.kSystem.Server.Server;
import eu.epicpvp.kSystem.Server.GunGame.Commands.CommandGunGame;
import eu.epicpvp.kSystem.Server.GunGame.Commands.CommandKit;
import eu.epicpvp.kSystem.Server.GunGame.Commands.CommandMap;
import eu.epicpvp.kcore.AACHack.AACHack;
import eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator;
import eu.epicpvp.kcore.Command.Commands.CommandStats;
import eu.epicpvp.kcore.Command.Commands.CommandWarp;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Util.UtilWorld;
import lombok.Getter;
import lombok.Setter;

public class GunGame extends Server {

	@Getter
	private CommandKit kit;
	@Getter
	private CommandMap map;
	@Getter
	@Setter
	private Location spawn;
	@Getter
	private GunGameListener listener;
	@Getter
	private StatsManager statsManager;

	public GunGame(kServerSystem instance) {
		super(instance, GroupTyp.GAME);
		UtilWorld.LoadWorld(new WorldCreator("gungame"), new CleanroomChunkGenerator("64,WATER"));

		this.statsManager = StatsManagerRepository.getStatsManager(GameType.GUNGAME);
		this.kit = new CommandKit(this);
		this.map = new CommandMap(this);
		getCommandHandler().register(CommandKit.class, kit);
		getCommandHandler().register(CommandMap.class, map);
		getCommandHandler().register(CommandWarp.class, new CommandWarp(getTeleportManager()));
		getCommandHandler().register(CommandGunGame.class, new CommandGunGame(this));
		getCommandHandler().register(CommandStats.class, new CommandStats(getStatsManager()));

		this.listener = new GunGameListener(this);
		new AACHack(ServerType.GUNGAME.getName());
	}

	public void onDisable() {
		if (getMap().getNpc1() != null)
			getMap().getNpc1().remove();
		if (getMap().getNpc2() != null)
			getMap().getNpc2().remove();
		if (getMap().getNpc3() != null)
			getMap().getNpc3().remove();
	}

}
