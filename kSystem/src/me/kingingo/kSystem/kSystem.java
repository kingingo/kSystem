package me.kingingo.kSystem;

import lombok.Getter;
import me.kingingo.kSystem.kManager.kPvP.kPvP;
import me.kingingo.kSystem.kManager.kSkyBlock.kSkyBlock;
import me.kingingo.kSystem.kServer.kServer;
import me.kingingo.kSystem.kServer.kWarZ.kWarZ;
import me.kingingo.kcore.Client.Client;
import me.kingingo.kcore.Command.CommandHandler;
import me.kingingo.kcore.Enum.ServerType;
import me.kingingo.kcore.Hologram.Hologram;
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
	private UserDataConfig userData;
	@Getter
	private ServerType serverType;
	private Updater updater;
	private kServer server;
	
	public void onEnable(){
		loadConfig();
		
		this.mysql=new MySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
		this.updater=new Updater(this);
		this.client = new Client(this,getConfig().getString("Config.Client.Host"),getConfig().getInt("Config.Client.Port"),"WarZ");
		this.packetManager=new PacketManager(this, this.client);
		this.userData=new UserDataConfig(this);
		new MemoryFix(this);
		new ListenerCMD(this);
		
		switch(getConfig().getString("Config.Server")){
		case "PvP": 
			serverType=ServerType.PVP;
			this.server=new kPvP(this);
			break;
		case "SkyBlock": 
			serverType=ServerType.SKYBLOCK;
			this.server=new kSkyBlock(this);
			break;
		case "WarZ": 
			serverType=ServerType.WARZ;
			this.server=new kWarZ(this);
			break;
		default:
			System.err.println("[kSystem]: ServerType nicht erkannt ("+getConfig().getString("Config.Server")+")");
			mysql.close();
			client.disconnect(true);
			System.exit(0);
		}
	}
	
	public void onDisable(){
		this.server.onDisable();
		this.userData.saveAllConfigs();
		this.mysql.close();
		this.client.disconnect(true);
		this.updater.stop();
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
