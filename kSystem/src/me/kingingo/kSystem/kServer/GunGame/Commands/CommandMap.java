package me.kingingo.kSystem.kServer.GunGame.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import me.kingingo.kSystem.kServer.GunGame.kGunGame;
import me.kingingo.kcore.Command.CommandHandler.Sender;
import me.kingingo.kcore.Hologram.nametags.NameTagMessage;
import me.kingingo.kcore.Hologram.nametags.NameTagType;
import me.kingingo.kcore.Language.Language;
import me.kingingo.kcore.Listener.kListener;
import me.kingingo.kcore.Packet.Events.PacketSendEvent;
import me.kingingo.kcore.PacketAPI.packetlistener.event.PacketListenerSendEvent;
import me.kingingo.kcore.Update.UpdateType;
import me.kingingo.kcore.Update.Event.UpdateEvent;
import me.kingingo.kcore.Util.UtilItem;
import me.kingingo.kcore.Util.UtilScoreboard;
import me.kingingo.kcore.Util.UtilServer;
import me.kingingo.kcore.Util.UtilTime;
import me.kingingo.kcore.kConfig.kConfig;
import me.kingingo.kcore.kListen.kRank;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class CommandMap extends kListener implements CommandExecutor{
	@Getter
	private kConfig config;
	@Getter
	private kGunGame instance;
	@Getter
	@Setter
	private int counter=0;
	@Getter
	@Setter
	private String nextMap;
	@Getter
	@Setter
	private String lastMap;
	@Getter
	@Setter
	private int time=2;
	@Getter
	private HashMap<String,Location> maps;
	private ArrayList<kRank> ranking;

	@Getter
	private ArmorStand npc1;
	private Player player1;
	private NameTagMessage ntm1;

	@Getter
	private ArmorStand npc2;
	private Player player2;
	private NameTagMessage ntm2;

	@Getter
	private ArmorStand npc3;
	private Player player3;
	private NameTagMessage ntm3;
	
	public CommandMap(kGunGame instance){
		super(instance.getInstance(),"CommandMap");

		this.instance=instance;
		this.ranking=new ArrayList<>();
		this.config=new kConfig(new File("plugins"+File.separator+instance.getInstance().getPlugin(instance.getInstance().getClass()).getName()+File.separator+"maps.yml"));
		
		this.maps=new HashMap<>();
		for(String map : config.getPathList("maps").keySet()){
			if(instance.getSpawn()==null)instance.setSpawn(config.getLocation("maps."+map.toLowerCase()+".Spawn"));
			getMaps().put(map.toLowerCase(), config.getLocation("maps."+map.toLowerCase()+".Spawn"));
		}

	}
	
	public void clear(){
			for(Entity e : Bukkit.getWorld("gungame").getEntities()){
            	if(!(e instanceof Player)&&!(e instanceof ItemFrame)){
					if(UtilServer.getDeliveryPet()!=null){
						if(UtilServer.getDeliveryPet().getJockey()!=null&&UtilServer.getDeliveryPet().getJockey().getEntityId()==e.getEntityId())continue;
						if(UtilServer.getDeliveryPet().getEntity()!=null&&UtilServer.getDeliveryPet().getEntity().getEntityId()==e.getEntityId())continue;
					}
					
					if(UtilServer.getPerkManager()!=null&&UtilServer.getPerkManager().getEntity()!=null&&UtilServer.getPerkManager().getEntity().getEntityId()==e.getEntityId())continue;
					
					if(UtilServer.getGemsShop()!=null&&UtilServer.getGemsShop().getListener()!=null&&UtilServer.getGemsShop().getListener().getEntity()!=null){
						if(UtilServer.getGemsShop().getListener().getEntity().getEntityId()==e.getEntityId())continue;
					}
					
					if(npc1!=null&&npc1.getEntityId()==e.getEntityId())continue;
					if(npc2!=null&&npc2.getEntityId()==e.getEntityId())continue;
					if(npc3!=null&&npc3.getEntityId()==e.getEntityId())continue;
					
            		e.remove();
            	}
			}
	}
	
	public void check(){
		ranking.clear();
		for(Player player : UtilServer.getPlayers())ranking.add(new kRank(player.getName(),player.getLevel()));

		Collections.sort(ranking,kRank.DESCENDING);
		clear();
		if(!ranking.isEmpty()){
			if(ranking.size()>=1){
				this.player1=Bukkit.getPlayer(ranking.get(0).getPlayer());
				if(npc1==null||npc1.isDead()){
					getNPCL1().getChunk().load();
					npc1=(ArmorStand)getNPCL1().getWorld().spawnEntity(getNPCL1(), EntityType.ARMOR_STAND);
					npc1.setArms(true);
					npc1.setBasePlate(false);
					npc1.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
					npc1.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
					npc1.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
					npc1.setItemInHand(new ItemStack(Material.IRON_SWORD));
					npc1.setCustomNameVisible(true);
				}
				
				if(ntm1==null){
					ntm1=new NameTagMessage(NameTagType.PACKET, getNPCL1().clone().add(0, 2.3, 0), new String[]{"§c§lPlatz 1"});
				}
				
				if(npc1.getCustomName()==null || !npc1.getCustomName().equalsIgnoreCase("§e§l"+player1.getName()+" §7|§7 Lvl. §a"+player1.getLevel())){
					npc1.setHelmet(UtilItem.Head(player1.getName()));
					npc1.setCustomName("§e§l"+player1.getName()+" §7|§7 Lvl. §a"+player1.getLevel());
				}
				ntm1.send();
				
				if(ranking.size()>=2){
					this.player2=Bukkit.getPlayer(ranking.get(1).getPlayer());
					if(npc2==null||npc2.isDead()){
						npc2=(ArmorStand)getNPCL2().getWorld().spawnEntity(getNPCL2(), EntityType.ARMOR_STAND);
						npc2.setArms(true);
						npc2.setBasePlate(false);
						npc2.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
						npc2.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
						npc2.setBoots(new ItemStack(Material.IRON_BOOTS));
						npc2.setItemInHand(new ItemStack(Material.GOLD_AXE));
						npc2.setCustomNameVisible(true);
					}
					
					if(ntm2==null){
						ntm2=new NameTagMessage(NameTagType.PACKET, getNPCL2().clone().add(0, 2.3, 0), new String[]{"§c§lPlatz 2"});
					}
					
					if(npc2.getCustomName()==null || !npc2.getCustomName().equalsIgnoreCase("§e§l"+player2.getName()+" §7|§7 Lvl. §a"+player2.getLevel())){
						npc2.setHelmet(UtilItem.Head(player2.getName()));
						npc2.setCustomName("§e§l"+player2.getName()+" §7|§7 Lvl. §a"+player2.getLevel());
					}
					ntm2.send();
					
					if(ranking.size()>=3){
						this.player3=Bukkit.getPlayer(ranking.get(2).getPlayer());
						
						if(npc3==null||npc3.isDead()){
							npc3=(ArmorStand)getNPCL3().getWorld().spawnEntity(getNPCL3(), EntityType.ARMOR_STAND);
							npc3.setArms(true);
							npc3.setBasePlate(false);
							npc3.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
							npc3.setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
							npc3.setBoots(new ItemStack(Material.GOLD_BOOTS));
							npc3.setItemInHand(new ItemStack(Material.BOW));
							npc3.setCustomNameVisible(true);
						}
						
						if(ntm3==null){
							ntm3=new NameTagMessage(NameTagType.PACKET, getNPCL3().clone().add(0, 2.3, 0), new String[]{"§c§lPlatz 3"});
						}
						
						if(npc3.getCustomName()==null || !npc3.getCustomName().equalsIgnoreCase("§e§l"+player3.getName()+" §7|§7 Lvl. §a"+player3.getLevel())){
							npc3.setHelmet(UtilItem.Head(player3.getName()));
							npc3.setCustomName("§e§l"+player3.getName()+" §7|§7 Lvl. §a"+player3.getLevel());
						}
						ntm3.send();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void joine(PlayerJoinEvent ev){
		if(ntm1!=null){
			ntm1.sendToPlayer(ev.getPlayer());
		}
		
		if(ntm2!=null){
			ntm2.sendToPlayer(ev.getPlayer());
		}
		
		if(ntm3!=null){
			ntm3.sendToPlayer(ev.getPlayer());
		}
	}
	
	@EventHandler
	public void checkNPC(UpdateEvent ev){
		if(ev.getType()==UpdateType.MIN_005){
			check();
		}
	}
	
	public Location getNPCL3(){
		return config.getLocation("maps."+lastMap.toLowerCase()+".NPC3");
	}
	
	public Location getNPCL2(){
		return config.getLocation("maps."+lastMap.toLowerCase()+".NPC2");
	}
	
	public Location getNPCL1(){
		return config.getLocation("maps."+lastMap.toLowerCase()+".NPC1");
	}
	
	String lastbtime;
	String btime;
	@EventHandler
	public void next(UpdateEvent ev){
		if(ev.getType()==UpdateType.SEC&&!this.maps.isEmpty()){
			this.time--;
			
			btime=UtilTime.formatSeconds(time);
			for(Player player : UtilServer.getPlayers()){
				if(player.getScoreboard()!=null){
					if(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)!=null){
						if(lastbtime==null){
							UtilScoreboard.resetScore(player.getScoreboard(), 1, DisplaySlot.SIDEBAR);
						}else{
							UtilScoreboard.resetScore(player.getScoreboard(), "§6Map wechseln in §f"+lastbtime, DisplaySlot.SIDEBAR);
						}
						UtilScoreboard.setScore(player.getScoreboard(), "§6Map wechseln in §f"+btime, DisplaySlot.SIDEBAR, 1);
					}
				}
			}
			lastbtime=btime;
			switch(this.time){
			case 5:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 4:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 3:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 2:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 1:UtilServer.broadcastLanguage("GUNGAME_MAP_COUNTING", this.time);break;
			case 0:
				if(nextMap==null){
					this.nextMap=(String)this.maps.keySet().toArray()[this.counter];
					this.counter++;
					
					if(this.counter==this.maps.size()){
						this.counter=0;
					}
				}
				this.lastMap=nextMap;
				
				this.time=60*30;
				if(npc1!=null){
					ntm1.remove();
					ntm1=null;
					npc1.remove();
				}
				if(npc2!=null){
					ntm2.remove();
					ntm2=null;
					npc2.remove();
				}
				if(npc3!=null){
					ntm3.remove();
					ntm3=null;
					npc3.remove();
				}
				this.maps.get(this.nextMap).getChunk().load();
				getInstance().setSpawn(this.maps.get(this.nextMap));
				for(Player player : UtilServer.getPlayers())player.teleport(getInstance().getSpawn());
				
				clear();
				UtilServer.getLagMeter().unloadChunks(null, null);
				
				if(UtilServer.getGemsShop()!=null){
					if(config.contains("maps."+nextMap.toLowerCase()+".GemShop")){
						UtilServer.getGemsShop().setCreature(config.getLocation("maps."+nextMap.toLowerCase()+".GemShop"));
					}
				}
				
				if(UtilServer.getDeliveryPet()!=null){
					if(config.contains("maps."+nextMap.toLowerCase()+".DeliveryPet")){
						UtilServer.getDeliveryPet().setLocation(config.getLocation("maps."+nextMap.toLowerCase()+".DeliveryPet"));
						UtilServer.getDeliveryPet().getEntity().remove();
						UtilServer.getDeliveryPet().getJockey().remove();
						UtilServer.getDeliveryPet().createPet();
					}
				}
				this.nextMap=null;
				check();
				UtilServer.broadcastLanguage("GUNGAME_MAP_CHANGE");
				break;
			}
		}
	}
	
	@me.kingingo.kcore.Command.CommandHandler.Command(command = "map", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		Player player=(Player)sender;
		
		if(player.isOp()){
			if(args.length==0){
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map set [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map setgems [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map setdeliverypet [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map next [Name]");
				player.sendMessage(Language.getText(player, "PREFIX")+"§7/map settime [Time]");
			}else{
				if(args[0].equalsIgnoreCase("set")){
					getMaps().put(args[1].toLowerCase(), player.getLocation());
					config.setLocation("maps."+args[1].toLowerCase()+".Spawn", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("setgems")){
					config.setLocation("maps."+args[1].toLowerCase()+".GemShop", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("npc1")){
					config.setLocation("maps."+args[1].toLowerCase()+".NPC1", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("npc2")){
					config.setLocation("maps."+args[1].toLowerCase()+".NPC2", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("npc3")){
					config.setLocation("maps."+args[1].toLowerCase()+".NPC3", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("setdeliverypet")){
					config.setLocation("maps."+args[1].toLowerCase()+".DeliveryPet", player.getLocation());
					config.save();
					player.sendMessage(Language.getText(player, "PREFIX")+"Die Map wurde gespeichert");
				}else if(args[0].equalsIgnoreCase("next")){
					if(getMaps().containsKey(args[1].toLowerCase())){
						this.nextMap=args[1].toLowerCase();
						player.sendMessage(Language.getText(player, "PREFIX")+"§aDie nächste Map wird §7"+this.nextMap+"§a sein!");
					}
				}else if(args[0].equalsIgnoreCase("settime")){
					try{
						int time = Integer.valueOf(args[1]);
						
						setTime(time);
						player.sendMessage(Language.getText(player, "PREFIX")+"§aDie Zeit wurd auf §e"+getTime()+"§a geändert");
					}catch(NumberFormatException e){
						player.sendMessage(Language.getText(player, "PREFIX")+Language.getText(player, "NO_INTEGER",args[1]));
					}
				}else if(args[0].equalsIgnoreCase("list")){
					String maps ="";
					for(String map : getMaps().keySet())maps+=map;
					player.sendMessage(Language.getText(player, "PREFIX")+maps);
				}
			}
		}
		return false;
	}
}
