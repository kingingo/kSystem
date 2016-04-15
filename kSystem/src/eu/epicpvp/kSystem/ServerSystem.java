package eu.epicpvp.kSystem;

import org.bukkit.plugin.java.JavaPlugin;

import dev.wolveringer.client.ClientWrapper;
import dev.wolveringer.client.connection.ClientType;
import dev.wolveringer.dataserver.gamestats.ServerType;
import eu.epicpvp.kSystem.Server.Server;
import eu.epicpvp.kSystem.Server.GunGame.GunGame;
import eu.epicpvp.kcore.Listener.Command.ListenerCMD;
import eu.epicpvp.kcore.MySQL.MySQL;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.Updater;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;

public class ServerSystem extends JavaPlugin{

	@Getter
	private MySQL mysql;
	@Getter
	private ClientWrapper client;
	@Getter
	private ServerType serverType;
	private Updater updater;
	private Server server;
	
	public void onEnable(){
		loadConfig();
		this.client = UtilServer.createClient(this,ClientType.OTHER, getConfig().getString("Config.Client.Host"), getConfig().getInt("Config.Client.Port"), getConfig().getString("Config.Server")+getConfig().getString("Config.ID"));
		this.mysql=UtilServer.createMySQL(getConfig().getString("Config.MySQL.User"),getConfig().getString("Config.MySQL.Password"),getConfig().getString("Config.MySQL.Host"),getConfig().getString("Config.MySQL.DB"),this);
		this.updater=UtilServer.createUpdater(this);
		new ListenerCMD(this);
		
		switch(getConfig().getString("Config.Server").toLowerCase()){
		case "gungame": 
			serverType=ServerType.GUNGAME;
			this.server=new GunGame(this);
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
	    getConfig().addDefault("Config.ID", "1");
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
