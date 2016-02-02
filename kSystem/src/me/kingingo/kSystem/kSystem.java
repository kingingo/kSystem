package me.kingingo.kSystem;

import lombok.Getter;
import me.kingingo.kSystem.kServer.kServer;
import me.kingingo.kSystem.kServer.GunGame.kGunGame;
import me.kingingo.kSystem.kServer.kPvP.kPvP;
import me.kingingo.kSystem.kServer.kSkyBlock.kSkyBlock;
import me.kingingo.kSystem.kServer.kWarZ.kWarZ;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.Hologram.Hologram;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.BungeeCordFirewall.BungeeCordFirewallListener;
import me.kingingo.kcore.Listener.Command.ListenerCMD;
import me.kingingo.kcore.MySQL.MySQL;
import me.kingingo.kcore.Packet.PacketManager;
import me.kingingo.kcore.Permission.PermissionManager;
import me.kingingo.kcore.Update.Updater;
import me.kingingo.kcore.UserDataConfig.UserDataConfig;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.memory.MemoryFix;

import org.bukkit.plugin.java.JavaPlugin;

public class kSystem extends JavaPlugin{

	@Getter
	private MySQL mysql;
	@Getter
	private Client client;
	@Getter
	private PacketManager packetManager;
	@Getter
	private ServerType serverType;
	private Updater updater;
	private kServer server;
	
	public void onEnable(){
		loadConfig();
		this.mysql=UtilServer.createMySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
		this.updater=UtilServer.createUpdater(this);
		this.client = UtilServer.createClient(this,getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),getConfig().getString("Config.Server"));
		this.packetManager=UtilServer.createPacketManager(this);
		Language.load(getMysql());
		new ListenerCMD(this);
		
		switch(getConfig().getString("Config.Server").toLowerCase()){
		case "pvp": 
			serverType=ServerType.PVP;
			this.server=new kPvP(this);
			break;
		case "skyblock": 
			serverType=ServerType.SKYBLOCK;
			this.server=new kSkyBlock(this);
			break;
		case "warz": 
			serverType=ServerType.WARZ;
			this.server=new kWarZ(this);
			break;
		case "gungame": 
			serverType=ServerType.GUNGAME;
			this.server=new kGunGame(this);
			break;
		default:
			System.err.println("[kSystem]: ServerType nicht erkannt ("+getConfig().getString("Config.Server")+")");
			UtilServer.disable();
			System.exit(0);
		}
	}
	
	public void onDisable(){
		this.server.onDisable();
		UtilServer.disable();
	}
	
	public void loadConfig(){
	    getConfig().addDefault("Config.Server", "PvP");
		getConfig().addDefault("Config.MySQL.Host", "NONE");
	    getConfig().addDefault("Config.MySQL.DB", "NONE");
	    getConfig().addDefault("Config.MySQL.User", "NONE");
	    getConfig().addDefault("Config.MySQL.Password", "NONE");
	    getConfig().addDefault("Config.Client.Host", "data.connect-handler.net");
	    getConfig().addDefault("Config.Client.Port", 9051);
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	  }
}
