package eu.epicpvp.kSystem.Server;

import dev.wolveringer.dataserver.gamestats.GameType;
import eu.epicpvp.kSystem.kServerSystem;
import eu.epicpvp.kcore.Command.CommandHandler;
import eu.epicpvp.kcore.Command.Admin.CommandCMDMute;
import eu.epicpvp.kcore.Command.Admin.CommandChatMute;
import eu.epicpvp.kcore.Command.Admin.CommandDebug;
import eu.epicpvp.kcore.Command.Admin.CommandFly;
import eu.epicpvp.kcore.Command.Admin.CommandFlyspeed;
import eu.epicpvp.kcore.Command.Admin.CommandLocations;
import eu.epicpvp.kcore.Command.Admin.CommandPvPMute;
import eu.epicpvp.kcore.Command.Admin.CommandToggle;
import eu.epicpvp.kcore.Command.Admin.CommandTp;
import eu.epicpvp.kcore.Command.Admin.CommandTpHere;
import eu.epicpvp.kcore.Command.Admin.CommandTppos;
import eu.epicpvp.kcore.Command.Admin.CommandVanish;
import eu.epicpvp.kcore.Command.Commands.CommandClearInventory;
import eu.epicpvp.kcore.Command.Commands.CommandFeed;
import eu.epicpvp.kcore.Command.Commands.CommandHeal;
import eu.epicpvp.kcore.Command.Commands.CommandMsg;
import eu.epicpvp.kcore.Command.Commands.CommandNacht;
import eu.epicpvp.kcore.Command.Commands.CommandR;
import eu.epicpvp.kcore.Command.Commands.CommandSonne;
import eu.epicpvp.kcore.Command.Commands.CommandTag;
import eu.epicpvp.kcore.Hologram.Hologram;
import eu.epicpvp.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import eu.epicpvp.kcore.Listener.Chat.ChatListener;
import eu.epicpvp.kcore.Permission.PermissionManager;
import eu.epicpvp.kcore.Permission.Group.GroupTyp;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.TeleportManager.TeleportManager;
import eu.epicpvp.kcore.UserDataConfig.UserDataConfig;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class Server{

	@Getter
	private kServerSystem instance;
	@Getter
	private Hologram hologram;
	@Getter
	private CommandHandler commandHandler;
	@Getter
	private TeleportManager teleportManager;
	@Getter
	private PermissionManager permissionManager;
	@Getter
	private StatsManager statsManager;
	@Getter
	private StatsManager money;
	@Getter
	private ChatListener chatListener;
	@Getter
	private UserDataConfig userData;
	
	public Server(kServerSystem instance){
		this.instance=instance;
		this.permissionManager=new PermissionManager(instance,GroupTyp.GUNGAME);
		this.statsManager=new StatsManager(getInstance(), getInstance().getClient(), GameType.GUNGAME);
		this.money=new StatsManager(getInstance(), getInstance().getClient(), GameType.Money);
		this.commandHandler=UtilServer.createCommandHandler(getInstance());
		this.hologram=new Hologram(instance);
		this.teleportManager=new TeleportManager(getCommandHandler(), getPermissionManager(), 3);
		this.chatListener=new ChatListener(getInstance(), getPermissionManager());
		this.userData=new UserDataConfig(getInstance());
		
		this.commandHandler.register(CommandTp.class, new CommandTp());
		this.commandHandler.register(CommandTpHere.class, new CommandTpHere());
		this.commandHandler.register(CommandVanish.class, new CommandVanish(instance));
		this.commandHandler.register(CommandFly.class, new CommandFly(instance));
		this.commandHandler.register(CommandFlyspeed.class, new CommandFlyspeed());
		this.commandHandler.register(CommandChatMute.class, new CommandChatMute(instance));
		this.commandHandler.register(CommandCMDMute.class, new CommandCMDMute(instance));
		this.commandHandler.register(CommandPvPMute.class, new CommandPvPMute(instance));
		this.commandHandler.register(CommandDebug.class, new CommandDebug());
		this.commandHandler.register(CommandTppos.class, new CommandTppos());
		this.commandHandler.register(CommandTag.class, new CommandTag());
		this.commandHandler.register(CommandNacht.class, new CommandNacht());
		this.commandHandler.register(CommandSonne.class, new CommandSonne());
		this.commandHandler.register(CommandClearInventory.class, new CommandClearInventory());
		this.commandHandler.register(CommandFeed.class, new CommandFeed());
		this.commandHandler.register(CommandHeal.class, new CommandHeal());
		this.commandHandler.register(CommandMsg.class, new CommandMsg());
		this.commandHandler.register(CommandR.class, new CommandR(instance));
		this.commandHandler.register(CommandToggle.class, new CommandToggle(instance));
		this.commandHandler.register(CommandLocations.class, new CommandLocations(instance));
//		this.commandHandler.register(CommandStatsAdmin.class, new CommandStatsAdmin(statsManager));
		
		new BungeeCordFirewallListener(commandHandler);
		UtilServer.createLagListener(getCommandHandler());
	}
	
	public void onDisable(){}
}
