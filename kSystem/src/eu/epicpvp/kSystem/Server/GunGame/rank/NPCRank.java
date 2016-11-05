package eu.epicpvp.kSystem.Server.GunGame.rank;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.kcore.Hologram.nametags.NameTagMessage;
import eu.epicpvp.kcore.Hologram.nametags.NameTagType;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilServer;
import lombok.Getter;
import lombok.Setter;

@Getter
public class NPCRank {
	private ArmorStand npc;
	@Setter
	private Location location;
	private Player player;
	private NameTagMessage nametag;
	private int rank;

	public NPCRank(Location location, int rank) {
		this.location = location;
		this.rank = rank;
		update();
	}

	private void update() {
		if (npc == null || npc.isDead()) {
			if (!location.getChunk().isLoaded())
				location.getChunk().load();
			npc = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
			npc.setArms(true);
			npc.setBasePlate(false);
			npc.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			npc.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
			npc.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
			npc.setItemInHand(new ItemStack(Material.IRON_SWORD));
			npc.setCustomNameVisible(true);
		}

		if (nametag == null) {
			nametag = new NameTagMessage(NameTagType.PACKET, location.clone().add(0, 2.3, 0), "§c§lPlatz " + rank);
		}
		nametag.send();

		if (this.player == null) {
			npc.setHelmet(UtilItem.Head(null));
			npc.setCustomName("§aNo player");
		} else {
			LoadedPlayer lplayer = UtilServer.getClient().getPlayerAndLoad(player.getName());
			npc.setHelmet(UtilItem.Head(player.getName()));
			npc.setCustomName("§e§l" + lplayer.getFinalName() + " §7|§7 Lvl. §a" + player.getLevel());
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		update();
	}

	public void remove() {
		npc.remove();
		nametag.remove();
	}
}
