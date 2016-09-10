package eu.epicpvp.kSystem.Server.GunGame.rank;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagType;
import eu.epicpvp.kcore.PacketAPI.PacketWrapper;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperGameProfile;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutEntityDestroy;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutEntityEquipment;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutEntityTeleport;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutNamedEntitySpawn;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutPlayerInfo;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPacketPlayOutRelEntityMoveLook;
import eu.epicpvp.kcore.PacketAPI.Packets.WrapperPlayerInfoData;
import eu.epicpvp.kcore.PacketAPI.UtilPacket;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

@Getter
public class NPCRank {
	private static final AtomicInteger entityIds = new AtomicInteger(10000);
	private static final DataWatcher defaultDataWatcher = new DataWatcher(null) {
		@Override
		public void a(PacketDataSerializer packetdataserializer) throws IOException {
			packetdataserializer.writeByte(127); //No entries //May crash the client. We will see
		}
	};

	private ArmorStand npc;
	@Setter
	private Location location;
	private Player player;
	private NameTagMessage nametag;
	private int rank;

	private int entityId;
	private WrapperPacketPlayOutNamedEntitySpawn packetSpawn;
	private WrapperPacketPlayOutEntityTeleport packetTeleport;
	private WrapperPacketPlayOutRelEntityMoveLook packetRelEntityMoveLook;
	private WrapperPacketPlayOutEntityDestroy packetDestroy;
	private WrapperPacketPlayOutEntityEquipment[] packetsEquipment;
	private WrapperPacketPlayOutPlayerInfo packetTabAdd;
	private WrapperPacketPlayOutPlayerInfo packetTabRemove;

	public NPCRank(Location location, int rank) {
		this.location = location;
		this.rank = rank;
		update();
	}

	private void update() {
		UUID uuid = UUID.randomUUID();
		entityId = entityIds.addAndGet(1);
		Location loc = location.clone();
		BlockVector blockVector = loc.toVector().toBlockVector();
		loc.setX(blockVector.getX() + .5);
		loc.setY(blockVector.getY() + .5);
		loc.setZ(blockVector.getZ() + .5);
		packetTeleport = new WrapperPacketPlayOutEntityTeleport();
		packetTeleport.setLocation(loc);
		packetTeleport.setEntityID(entityId);
		packetTeleport.setOnGround(true);

		packetRelEntityMoveLook = new WrapperPacketPlayOutRelEntityMoveLook(entityId, (byte) 0, (byte) 0, (byte) 0,
				UtilPacket.toPackedByte(loc.getYaw()), UtilPacket.toPackedByte(loc.getPitch()), true);

		packetSpawn = new WrapperPacketPlayOutNamedEntitySpawn();
		packetSpawn.setLocation(loc);
		packetSpawn.setDataWatcher(player != null ? UtilPlayer.getCraftPlayer(player).getHandle().getDataWatcher() : defaultDataWatcher);
		packetSpawn.setEntityID(entityId);
		packetSpawn.setUUID(uuid);

		WrapperGameProfile profile = player != null ? new WrapperGameProfile(UtilPlayer.getCraftPlayer(player).getHandle().getProfile()) : new WrapperGameProfile(uuid, "Nobody");
		profile.setUUID(uuid);

		packetTabAdd = new WrapperPacketPlayOutPlayerInfo();
		WrapperPlayerInfoData npcData = new WrapperPlayerInfoData(packetTabAdd, profile, player == null ? "§aNobody" : "§b" + player.getName());
		packetTabAdd.setEnumPlayerInfoAction(EnumPlayerInfoAction.ADD_PLAYER);
		packetTabAdd.setEntries(Arrays.asList(npcData));

		packetTabRemove = new WrapperPacketPlayOutPlayerInfo();
		packetTabRemove.setEnumPlayerInfoAction(EnumPlayerInfoAction.REMOVE_PLAYER);
		packetTabRemove.setEntries(Arrays.asList(npcData));

		net.minecraft.server.v1_8_R3.ItemStack[] equipment = player != null ? UtilPlayer.getCraftPlayer(player).getHandle().getEquipment() : new net.minecraft.server.v1_8_R3.ItemStack[1];
		packetsEquipment = new WrapperPacketPlayOutEntityEquipment[equipment.length];
		for (int i = 0; i < equipment.length; i++)
			packetsEquipment[i] = new WrapperPacketPlayOutEntityEquipment(entityId, i, equipment[i]);

//		if (npc == null || npc.isDead()) {
//			if (!location.getChunk().isLoaded())
//				location.getChunk().load();
//			npc = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
//			npc.setArms(true);
//			npc.setBasePlate(false);
//			npc.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
//			npc.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
//			npc.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
//			npc.setItemInHand(new ItemStack(Material.IRON_SWORD));
//			npc.setCustomNameVisible(true);
//		}

		if (nametag == null) {
			nametag = new NameTagMessage(NameTagType.PACKET, loc.add(0, 2.3, 0), "Platz -1");
		}
		if (player != null)
			nametag.setLines(new String[]{"§c§lPlatz " + rank + " §7| §aLevel: §b" + player.getLevel()});
		else
			nametag.setLines(new String[]{"§c§lPlatz " + rank + " §7| §aLevel: §b0"});
		nametag.send();

//		if(this.player == null){
//			npc.setHelmet(UtilItem.Head(null));
//			npc.setCustomName("§aNo player");
//		}
//		else
//		{
//			LoadedPlayer lplayer = UtilServer.getClient().getPlayerAndLoad(player.getName());
//			npc.setHelmet(UtilItem.Head(player.getName()));
//			npc.setCustomName("§e§l"+lplayer.getNickname()+" §7|§7 Lvl. §a" + player.getLevel());
//		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			update(p);
		}

		packetDestroy = new WrapperPacketPlayOutEntityDestroy(entityId);
	}

	private void update(Player player) {
		UtilPlayer.sendPacket(player, packetDestroy);
		UtilPlayer.sendPacket(player, packetTabAdd);
		UtilServer.runSyncLater(() -> {
			UtilPlayer.sendPacket(player, packetSpawn);
			UtilPlayer.sendPacket(player, packetTeleport);
			UtilPlayer.sendPacket(player, packetRelEntityMoveLook); //required for head rotation
			for (PacketWrapper packet : packetsEquipment)
				UtilPlayer.sendPacket(player, packet);
			if (player != null)
				UtilServer.runSyncLater(() -> {
					UtilPlayer.sendPacket(player, packetTabRemove);
				}, 200);
		}, 100);
	}

	public void setPlayer(Player player) {
		if (player != null && !Objects.equals(player, this.player)) {
			this.player = player;
			update();
		}
	}

	public void remove() {
//		npc.remove();
		nametag.remove();
		if (packetDestroy != null)
			for (Player p : Bukkit.getOnlinePlayers())
				UtilPlayer.sendPacket(p, packetDestroy);
	}
}
