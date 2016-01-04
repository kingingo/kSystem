package me.kingingo.kSystem.kServer;

import lombok.Getter;
import me.kingingo.kSystem.kSystem;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.GemsShop.GemsShop;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import me.kingingo.kcore.Listener.Chat.ChatListener;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.StatsManager.StatsManager;
import me.kingingo.kcore.TeleportManager.TeleportManager;
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
	
	public kServer(kSystem instance){
		this.instance=instance;
		this.permissionManager=new PermissionManager(getInstance(),getInstance().getServerType().getGroupType(),getInstance().getPacketManager(),getInstance().getMysql());
		this.statsManager=new StatsManager(getInstance(), getInstance().getMysql(),getInstance().getServerType().getGameType());
		this.commandHandler=new CommandHandler(instance);
		this.hologram=new Hologram(instance);
		this.teleportManager=new TeleportManager(getCommandHandler(), getPermissionManager(), 3);
		

		this.chatListener=new ChatListener(getInstance(), getPermissionManager());
		this.chatListener.setUserData(getInstance().getUserData());
		new BungeeCordFirewallListener(getInstance().getMysql(), getInstance().getServerType().getName());
		UtilServer.createLagListener(getCommandHandler());
		UtilInv.getBase(getInstance());
		UtilServer.createGemsShop(new GemsShop(getHologram(),getCommandHandler(), UtilInv.getBase(),getPermissionManager(), getInstance().getServerType()));
	}
	
	public void onDisable(){}
}
