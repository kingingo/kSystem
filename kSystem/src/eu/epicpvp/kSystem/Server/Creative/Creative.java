package eu.epicpvp.kSystem.Server.Creative;

import java.util.HashMap;

import com.intellectualcrafters.plot.api.PlotAPI;

import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.datenserver.definitions.permissions.GroupTyp;
import eu.epicpvp.kSystem.kServerSystem;
import eu.epicpvp.kSystem.Server.Server;
import eu.epicpvp.kSystem.Server.Creative.Commands.CommandkPlot;
import eu.epicpvp.kSystem.Server.Creative.Inventory.CreativeInventoryHandler;
import eu.epicpvp.kcore.Achievements.BlockForBlock;
import eu.epicpvp.kcore.Achievements.BobTheDestroyer;
import eu.epicpvp.kcore.Achievements.Obsession;
import eu.epicpvp.kcore.Achievements.Handler.Achievement;
import eu.epicpvp.kcore.Achievements.Handler.AchievementsHandler;
import eu.epicpvp.kcore.Addons.AddonDay;
import eu.epicpvp.kcore.Addons.AddonSun;
import eu.epicpvp.kcore.Command.Admin.CommandBroadcast;
import eu.epicpvp.kcore.Command.Admin.CommandCMDMute;
import eu.epicpvp.kcore.Command.Admin.CommandChatMute;
import eu.epicpvp.kcore.Command.Admin.CommandDebug;
import eu.epicpvp.kcore.Command.Admin.CommandFly;
import eu.epicpvp.kcore.Command.Admin.CommandFlyspeed;
import eu.epicpvp.kcore.Command.Admin.CommandGive;
import eu.epicpvp.kcore.Command.Admin.CommandGiveAll;
import eu.epicpvp.kcore.Command.Admin.CommandItem;
import eu.epicpvp.kcore.Command.Admin.CommandK;
import eu.epicpvp.kcore.Command.Admin.CommandLocations;
import eu.epicpvp.kcore.Command.Admin.CommandMore;
import eu.epicpvp.kcore.Command.Admin.CommandPacketToggle;
import eu.epicpvp.kcore.Command.Admin.CommandSocialspy;
import eu.epicpvp.kcore.Command.Admin.CommandToggle;
import eu.epicpvp.kcore.Command.Admin.CommandTp;
import eu.epicpvp.kcore.Command.Admin.CommandTpHere;
import eu.epicpvp.kcore.Command.Admin.CommandTppos;
import eu.epicpvp.kcore.Command.Admin.CommandTrackingRange;
import eu.epicpvp.kcore.Command.Admin.CommandVanish;
import eu.epicpvp.kcore.Command.Admin.CommandgBroadcast;
import eu.epicpvp.kcore.Command.Commands.CommandBack;
import eu.epicpvp.kcore.Command.Commands.CommandClearInventory;
import eu.epicpvp.kcore.Command.Commands.CommandDelHome;
import eu.epicpvp.kcore.Command.Commands.CommandEnderchest;
import eu.epicpvp.kcore.Command.Commands.CommandExt;
import eu.epicpvp.kcore.Command.Commands.CommandFeed;
import eu.epicpvp.kcore.Command.Commands.CommandHead;
import eu.epicpvp.kcore.Command.Commands.CommandHeal;
import eu.epicpvp.kcore.Command.Commands.CommandHome;
import eu.epicpvp.kcore.Command.Commands.CommandInvsee;
import eu.epicpvp.kcore.Command.Commands.CommandMsg;
import eu.epicpvp.kcore.Command.Commands.CommandNacht;
import eu.epicpvp.kcore.Command.Commands.CommandR;
import eu.epicpvp.kcore.Command.Commands.CommandRenameItem;
import eu.epicpvp.kcore.Command.Commands.CommandSetHome;
import eu.epicpvp.kcore.Command.Commands.CommandSonne;
import eu.epicpvp.kcore.Command.Commands.CommandSpawn;
import eu.epicpvp.kcore.Command.Commands.CommandSpawner;
import eu.epicpvp.kcore.Command.Commands.CommandSpawnmob;
import eu.epicpvp.kcore.Command.Commands.CommandSuffix;
import eu.epicpvp.kcore.Command.Commands.CommandTag;
import eu.epicpvp.kcore.Command.Commands.CommandWarp;
import eu.epicpvp.kcore.Command.Commands.CommandWorkbench;
import eu.epicpvp.kcore.Particle.WingShop;
import eu.epicpvp.kcore.TimeManager.TimeManager;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class Creative extends Server{

	@Getter
	private PlotAPI plotApi;
	@Getter
	private HashMap<String, String> invite;
	@Getter
	private CreativeInventoryHandler creativeInventoryHandler;
	@Getter
	private WingShop wing;

	public Creative(kServerSystem instance) {
		super(instance,GroupTyp.CREATIVE);
		this.invite=new HashMap<>();
		this.plotApi = new PlotAPI();
		new TimeManager(getPermissionManager());
		new AchievementsHandler(instance, new Achievement[]{new BobTheDestroyer(),new BlockForBlock(),new Obsession(StatsKey.CREATIVE_TIME)});

		getCommandHandler().register(CommandkPlot.class, new CommandkPlot(this));
		getCommandHandler().register(CommandDebug.class, new CommandDebug());
		getCommandHandler().register(CommandFly.class, new CommandFly(instance));
		getCommandHandler().register(CommandR.class, new CommandR(instance));
		getCommandHandler().register(CommandSocialspy.class, new CommandSocialspy(instance));
		getCommandHandler().register(CommandCMDMute.class, new CommandCMDMute(instance));
		getCommandHandler().register(CommandChatMute.class, new CommandChatMute(instance));
		getCommandHandler().register(CommandToggle.class, new CommandToggle(instance));
		getCommandHandler().register(CommandTrackingRange.class, new CommandTrackingRange());
		getCommandHandler().register(CommandGiveAll.class, new CommandGiveAll());
		getCommandHandler().register(CommandgBroadcast.class, new CommandgBroadcast(UtilServer.getClient()));
		getCommandHandler().register(CommandMsg.class, new CommandMsg());
		getCommandHandler().register(CommandFeed.class, new CommandFeed());
		getCommandHandler().register(CommandTag.class, new CommandTag());
		getCommandHandler().register(CommandNacht.class, new CommandNacht());
		getCommandHandler().register(CommandWarp.class, new CommandWarp(UtilServer.getTeleportManager()));
		getCommandHandler().register(CommandClearInventory.class, new CommandClearInventory());
		getCommandHandler().register(CommandInvsee.class, new CommandInvsee(UtilServer.getMysql()));
		getCommandHandler().register(CommandEnderchest.class, new CommandEnderchest(UtilServer.getMysql()));
		getCommandHandler().register(CommandBroadcast.class, new CommandBroadcast());
		getCommandHandler().register(CommandTppos.class, new CommandTppos());
		getCommandHandler().register(CommandItem.class, new CommandItem());
		getCommandHandler().register(CommandTp.class, new CommandTp());
		getCommandHandler().register(CommandTpHere.class, new CommandTpHere());
		getCommandHandler().register(CommandVanish.class, new CommandVanish(instance));
		getCommandHandler().register(CommandMore.class, new CommandMore());
		getCommandHandler().register(CommandFlyspeed.class, new CommandFlyspeed());
		getCommandHandler().register(CommandBack.class, new CommandBack(instance));
		getCommandHandler().register(CommandGive.class, new CommandGive());
		getCommandHandler().register(CommandLocations.class, new CommandLocations(instance));
		getCommandHandler().register(CommandHeal.class, new CommandHeal());
		getCommandHandler().register(CommandHome.class, new CommandHome(UtilServer.getUserData(),UtilServer.getTeleportManager(),getCommandHandler()));
		getCommandHandler().register(CommandSpawnmob.class, new CommandSpawnmob());
		getCommandHandler().register(CommandSpawner.class, new CommandSpawner());
		getCommandHandler().register(CommandSetHome.class, new CommandSetHome(UtilServer.getUserData(), UtilServer.getPermissionManager()));
		getCommandHandler().register(CommandSonne.class, new CommandSonne());
		getCommandHandler().register(CommandDelHome.class, new CommandDelHome(UtilServer.getUserData()));
		getCommandHandler().register(CommandRenameItem.class, new CommandRenameItem());
		getCommandHandler().register(CommandExt.class, new CommandExt());
		getCommandHandler().register(CommandHead.class, new CommandHead());
		getCommandHandler().register(CommandWorkbench.class, new CommandWorkbench());
		getCommandHandler().register(CommandSuffix.class, new CommandSuffix(UtilServer.getUserData()));
		getCommandHandler().register(CommandPacketToggle.class, new CommandPacketToggle(instance));
		getCommandHandler().register(CommandK.class, new CommandK());
		getCommandHandler().register(CommandSpawn.class, new CommandSpawn(UtilServer.getTeleportManager()));

		this.wing = new WingShop(instance);
		new CreativeListener(this);
		this.creativeInventoryHandler=new CreativeInventoryHandler(this);
		new PlotSquarePrepare();
		new AddonSun(instance);
		new AddonDay(instance);
	}
}
