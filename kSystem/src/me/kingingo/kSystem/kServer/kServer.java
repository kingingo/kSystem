package me.kingingo.kSystem.kServer;

import net.minecraft.server.v1_8_R3.CommandEnchant;
import lombok.Getter;
import me.kingingo.kSystem.kSystem;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Command.Admin.CommandCMDMute;
import me.kingingo.kcore.Command.Admin.CommandChatMute;
import me.kingingo.kcore.Command.Admin.CommandDebug;
import me.kingingo.kcore.Command.Admin.CommandFly;
import me.kingingo.kcore.Command.Admin.CommandFlyspeed;
import me.kingingo.kcore.Command.Admin.CommandLocations;
import me.kingingo.kcore.Command.Admin.CommandPvPMute;
import me.kingingo.kcore.Command.Admin.CommandStatsAdmin;
import me.kingingo.kcore.Command.Admin.CommandToggle;
import me.kingingo.kcore.Command.Admin.CommandTp;
import me.kingingo.kcore.Command.Admin.CommandTpHere;
import me.kingingo.kcore.Command.Admin.CommandTppos;
import me.kingingo.kcore.Command.Admin.CommandVanish;
import me.kingingo.kcore.Command.Commands.CommandClearInventory;
import me.kingingo.kcore.Command.Commands.CommandFeed;
import me.kingingo.kcore.Command.Commands.CommandHeal;
import me.kingingo.kcore.Command.Commands.CommandMsg;
import me.kingingo.kcore.Command.Commands.CommandNacht;
import me.kingingo.kcore.Command.Commands.CommandR;
import me.kingingo.kcore.Command.Commands.CommandSonne;
import me.kingingo.kcore.Command.Commands.CommandTag;
import me.kingingo.kcore.GemsShop.GemsShop;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.TeleportManager.TeleportManager;
import me.kingingo.kcore.UserDataConfig.UserDataConfig;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.Util.UtilServer;

public class kServer{

	@Getter
	private kSystem instance;
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
	private ChatListener chatListener;
	@Getter
	private UserDataConfig userData;
	
	public kServer(kSystem instance){
		this.instance=instance;
		this.permissionManager=new PermissionManager(getInstance(),getInstance().getServerType().getGroupType(),getInstance().getPacketManager(),getInstance().getMysql());
		this.statsManager=new StatsManager(getInstance(), getInstance().getMysql(),getInstance().getServerType().getGameType());
		this.statsManager.setAsync(true);
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
		this.commandHandler.register(CommandStatsAdmin.class, new CommandStatsAdmin(statsManager));
		
		new BungeeCordFirewallListener(getInstance().getMysql(),commandHandler, getInstance().getServerType().getName());
		UtilServer.createLagListener(getCommandHandler());
		UtilInv.getBase(getInstance());
	}
	
	public void onDisable(){}
}
