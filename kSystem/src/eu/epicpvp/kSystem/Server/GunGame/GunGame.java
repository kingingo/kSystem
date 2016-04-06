package eu.epicpvp.kSystem.Server.GunGame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import dev.wolveringer.dataserver.gamestats.ServerType;
import eu.epicpvp.kSystem.ServerSystem;
import eu.epicpvp.kSystem.Server.Server;
import eu.epicpvp.kSystem.Server.GunGame.Commands.CommandGunGame;
import eu.epicpvp.kSystem.Server.GunGame.Commands.CommandKit;
import eu.epicpvp.kSystem.Server.GunGame.Commands.CommandMap;
import eu.epicpvp.kcore.ChunkGenerator.CleanroomChunkGenerator;
import eu.epicpvp.kcore.Command.Commands.CommandStats;
import eu.epicpvp.kcore.Command.Commands.CommandWarp;
import eu.epicpvp.kcore.DeliveryPet.DeliveryObject;
import eu.epicpvp.kcore.DeliveryPet.DeliveryPet;
import eu.epicpvp.kcore.GemsShop.GemsShop;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Language.Language;
import eu.epicpvp.kcore.Permission.PermissionType;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilWorld;
import lombok.Getter;
import lombok.Setter;

public class GunGame extends Server{
	
	@Getter
	private CommandKit kit;
	@Getter
	private CommandMap map;
	@Getter
	@Setter
	private Location spawn;
	@Getter
	private GunGameListener listener;
	
	public GunGame(ServerSystem instance){
		super(instance);
    	UtilWorld.LoadWorld(new WorldCreator("gungame"), new CleanroomChunkGenerator("64,WATER"));
    	
//    	new AACHack("gungame", getPermissionManager().getMysql(),instance.getPacketManager());
    	
		this.kit=new CommandKit(this);
		this.map=new CommandMap(this);
		getCommandHandler().register(CommandKit.class, kit);
		getCommandHandler().register(CommandMap.class, map);
		getCommandHandler().register(CommandWarp.class, new CommandWarp(getTeleportManager()));
		getCommandHandler().register(CommandGunGame.class, new CommandGunGame(this));
		getCommandHandler().register(CommandStats.class, new CommandStats(getStatsManager()));
		
		this.listener=new GunGameListener(this);
		UtilServer.createGemsShop(new GemsShop(getHologram(),getMoney(),getCommandHandler(), UtilInv.getBase(),getPermissionManager(), getInstance().getServerType()));
		
		UtilServer.createDeliveryPet(new DeliveryPet(UtilInv.getBase(),null,new DeliveryObject[]{
			new DeliveryObject(new String[]{"","§7Click for Vote!","","§eGunGame Rewards:","2 Level Up","§ePvP Rewards:","§7   200 Epics","§7   1x Inventory Repair","","§eGame Rewards:","§7   25 Gems","§7   100 Coins","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},PermissionType.DELIVERY_PET_VOTE,false,28,"§aVote for EpicPvP",Material.PAPER,Material.REDSTONE_BLOCK,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						p.closeInventory();
						p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
						p.sendMessage(Language.getText(p,"PREFIX")+" ");
						p.sendMessage(Language.getText(p,"PREFIX")+"Vote Link:§a http://goo.gl/wxdAj4");
						p.sendMessage(Language.getText(p,"PREFIX")+" ");
						p.sendMessage(Language.getText(p,"PREFIX")+"§7-----------------------------------------");
					}
					
				},-1),
				new DeliveryObject(new String[]{"§aOnly for §eVIP§a!","","§eGunGame Rewards:","§7   2 Level Up","","§ePvP Rewards:","§7   200 Epics","§7   10 Level","","§eGame Rewards:","§7   200 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   200 Epics","§7   2x Diamonds","§7   2x Iron Ingot","§7   2x Gold Ingot"},PermissionType.DELIVERY_PET_VIP_WEEK,true,11,"§cRank §eVIP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getKit().setLevel(p, p.getLevel()+2);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §6ULTRA§a!","","§eGunGame Rewards:","§7   2 Level Up","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","§7   2x TTT Paesse","","§eSkyBlock Rewards:","§7   300 Epics","§7   4x Diamonds","§7   4x Iron Ingot","§7   4x Gold Ingot"},PermissionType.DELIVERY_PET_ULTRA_WEEK,true,12,"§cRank §6ULTRA§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getKit().setLevel(p, p.getLevel()+2);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §aLEGEND§a!","","§eGunGame Rewards:","§7   2 Level Up","","§ePvP Rewards:","§7   400 Epics","§7   20 Level","","§eGame Rewards:","§7   400 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   400 Epics","§7   6x Diamonds","§7   6x Iron Ingot","§7   6x Gold Ingot"},PermissionType.DELIVERY_PET_LEGEND_WEEK,true,13,"§cRank §aLEGEND§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getKit().setLevel(p, p.getLevel()+2);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§a!","","§eGunGame Rewards:","§7   2 Level Up","","§ePvP Rewards:","§7   500 Epics","§7   25 Level","","§eGame Rewards:","§7   500 Coins","§7   3x TTT Paesse","","§eSkyBlock Rewards:","§7   500 Epics","§7   8x Diamonds","§7   8x Iron Ingot","§7   8x Gold Ingot"},PermissionType.DELIVERY_PET_MVP_WEEK,true,14,"§cRank §bMVP§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getKit().setLevel(p, p.getLevel()+2);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§aOnly for §bMVP§c+§a!","","§eGunGame Rewards:","§7   2 Level Up","","§ePvP Rewards:","§7   600 Epics","§7   30 Level","","§eGame Rewards:","§7   600 Coins","§7   4x TTT Paesse","","§eSkyBlock Rewards:","§7   600 Epics","§7   10x Diamonds","§7   10x Iron Ingot","§7   10x Gold Ingot"},PermissionType.DELIVERY_PET_MVPPLUS_WEEK,true,15,"§cRank §bMVP§c+§c Reward",Material.getMaterial(342),Material.MINECART,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
						getKit().setLevel(p, p.getLevel()+2);
					}
					
				},TimeSpan.DAY*7),
				new DeliveryObject(new String[]{"§7/twitter [TwitterName]","","§eGunGame Rewards:","§7   2 Level Up","","§ePvP Rewards:","§7   300 Epics","§7   15 Level","","§eGame Rewards:","§7   300 Coins","","§eSkyBlock Rewards:","§7   300 Epics","§7   15 Level"},PermissionType.DELIVERY_PET_TWITTER,false,34,"§cTwitter Reward",Material.getMaterial(351),4,new Click(){

					@Override
					public void onClick(Player p, ActionType a,Object obj) {
//						String s1 = getInstance().getMysql().getString("SELECT twitter FROM BG_TWITTER WHERE uuid='"+UtilPlayer.getRealUUID(p)+"'");
//						if(s1.equalsIgnoreCase("null")){
//							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_ACC_NOT"));
//						}else{
//							getInstance().getPacketManager().SendPacket("DATA", new TWIITTER_IS_PLAYER_FOLLOWER(s1, p.getName()));
//							p.sendMessage(Language.getText(p,"PREFIX")+Language.getText(p, "TWITTER_CHECK"));
//						}
					}
					
				},TimeSpan.DAY*7),
		},"§bThe Delivery Jockey!",EntityType.CHICKEN,UtilServer.getGemsShop().getListener().getEntity().getLocation(),ServerType.GUNGAME,getHologram(),getInstance().getMysql())
		);
	}

	public void onDisable(){
		if(getMap().getNpc1()!=null)getMap().getNpc1().remove();
		if(getMap().getNpc2()!=null)getMap().getNpc2().remove();
		if(getMap().getNpc3()!=null)getMap().getNpc3().remove();
//		getInstance().getPacketManager().SendPacket("hub", new SERVER_STATUS(GameState.Restart, 0, Bukkit.getMaxPlayers(),"MAP",GameType.GUNGAME,getInstance().getConfig().getString("Config.ID"), false));
	}
	
}
