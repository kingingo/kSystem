package eu.epicpvp.kSystem.Server.Creative;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.epicpvp.kcore.Listener.kListener;
import eu.epicpvp.kcore.Util.RestartScheduler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class CreativeListener extends kListener {

	private Creative instance;

	public CreativeListener(Creative instance) {
		super(instance.getInstance(), "CreativeListener");
		this.instance = instance;
	}

	@EventHandler
	public void Sign(SignChangeEvent ev) {
		ev.setLine(0, ev.getLine(0).replaceAll("&", "§"));
		ev.setLine(1, ev.getLine(1).replaceAll("&", "§"));
		ev.setLine(2, ev.getLine(2).replaceAll("&", "§"));
		ev.setLine(3, ev.getLine(3).replaceAll("&", "§"));
	}

	@EventHandler
	public void book(PlayerEditBookEvent ev) {
		BookMeta bookMeta = ev.getNewBookMeta();
		if (bookMeta.getPageCount() > 200 && ev.getPreviousBookMeta().getPageCount() <= 200) {
			ev.setCancelled(true);
		}
		bookMeta.setAuthor(ChatColor.stripColor(bookMeta.getAuthor()).replace("§", ""));
		List<String> pages = bookMeta.getPages();
		for (int i = 0; i < pages.size(); i++) {
			pages.set(i, ChatColor.stripColor(pages.get(i)).replace("§", ""));
		}
		bookMeta.setPages(pages);
		bookMeta.setTitle(ChatColor.stripColor(bookMeta.getTitle()).replace("§", ""));
		List<String> lore = bookMeta.getLore();
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, ChatColor.stripColor(lore.get(i)).replace("§", ""));
		}
		bookMeta.setLore(lore);
		ev.setNewBookMeta(bookMeta);
	}

	@EventHandler
	public void PlayerItemConsume(PlayerItemConsumeEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler
	public void splash(PotionSplashEvent ev) {
		ev.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreativeInventory(InventoryCreativeEvent event) {
		event.setCancelled(false);

		Player player = (Player) event.getWhoClicked();
		if (player.isOp()) {
			return;
		}
		event.setCursor(checkAndEditItem(event.getCursor(), player));
		event.setCurrentItem(checkAndEditItem(event.getCurrentItem(), player));
		checkAndEditPlayerInv(player);
	}

	@SuppressWarnings({"SetReplaceableByEnumSet", "unchecked"})
	public ItemStack checkAndEditItem(ItemStack item, Player player) {
		if (item == null) {
			return null;
		}
		if (item.getType() == Material.ANVIL) {
			MaterialData data = item.getData();
			byte dataVal = data.getData();
			if (dataVal < 0 || dataVal > 2) {
				data.setData((byte) 2);
				item.setData(data);
				item.setDurability((short) 2);
				System.out.println("Removed invalid anvil data in inv of player " + player.getName() + " item: " + item);
			}
		}
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			String displayName = meta.getDisplayName();
			if (displayName != null) {
				if (displayName.length() > 60) {
					displayName = displayName.substring(0, 60);
				}
				meta.setDisplayName(ChatColor.stripColor(displayName).replace("§", ""));
			}
			List<String> lore = meta.getLore();
			if (lore != null) {
				for (int i = 0; i < lore.size(); i++) {
					String line = lore.get(i);
					if (line.length() > 60) {
						line = line.substring(0, 60);
					}
					lore.set(i, ChatColor.stripColor(line).replace("§", ""));
				}
			}
			Set<ItemFlag> itemFlags = meta.getItemFlags();
			if (itemFlags != null) {
				new HashSet<>(itemFlags).forEach(meta::removeItemFlags);
			}
			Map<Enchantment, Integer> enchants = meta.getEnchants();
			if (enchants != null) {
				new HashMap<>(enchants).forEach((enchantment, lvl) -> {
					if (lvl > 5 || lvl < 0) {
						meta.removeEnchant(enchantment);
						System.out.println("Removed invalid enchantment " + enchantment + ":" + lvl + " in inv of player " + player.getName() + " item: " + item);
					}
				});
			}
			item.setItemMeta(meta);
		}
		if (item instanceof CraftItemStack) {
			CraftItemStack citem = (CraftItemStack) item;
			try {
				Field handleField = CraftItemStack.class.getDeclaredField("handle");
				handleField.setAccessible(true);
				net.minecraft.server.v1_8_R3.ItemStack handle = (net.minecraft.server.v1_8_R3.ItemStack) handleField.get(citem);
				if (handle != null) {
					NBTTagCompound nbt = handle.getTag();
					if (nbt != null) {
						nbt.remove("AttributeModifiers");
						System.out.println("Removed AttributeModifiers NBT from " + item + " found in inv of player " + player.getName());
					}
				}
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
		return item;
	}

	@EventHandler
	public void Command(PlayerCommandPreprocessEvent ev) {
		String cmd = "";
		if (ev.getMessage().contains(" ")) {
			String[] parts = ev.getMessage().split(" ");
			cmd = parts[0];
		} else {
			cmd = ev.getMessage();
		}

		if (cmd.startsWith("/me")) {
			ev.setCancelled(true);
			return;
		} else if (cmd.startsWith("/bukkit")) {
			ev.setCancelled(true);
			return;
		} else if (cmd.equalsIgnoreCase("/minecraft:")) {
			ev.setCancelled(true);
			return;
		} else if (cmd.equalsIgnoreCase("/p")
				|| cmd.equalsIgnoreCase("/plot")
				|| cmd.equalsIgnoreCase("/plots")
				|| cmd.equalsIgnoreCase("/plotsquared")
				|| cmd.equalsIgnoreCase("/ps")
				|| cmd.equalsIgnoreCase("/p2")
				|| cmd.equalsIgnoreCase("/2")) {

			instance.getCreativeInventoryHandler().open(ev.getPlayer());
			ev.setCancelled(true);
			return;
		} else if (cmd.equalsIgnoreCase("/kp")) {
			ev.setMessage(ev.getMessage().replaceAll("/kp", "/p"));
		}

		if (ev.getPlayer().isOp()) {
			if (cmd.equalsIgnoreCase("/reload")) {
				ev.setCancelled(true);
				restart();
			} else if (cmd.equalsIgnoreCase("/restart")) {
				ev.setCancelled(true);
				restart();
			} else if (cmd.equalsIgnoreCase("/stop")) {
				ev.setCancelled(true);
				restart();
			}
		}
	}

	public void restart() {
		RestartScheduler restart = new RestartScheduler(instance.getInstance());
		restart.setMoney(UtilServer.getGemsShop().getGems());
		restart.setStats(instance.getMoney());
		restart.start();
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent ev) {
		ev.setRespawnLocation(Bukkit.getWorld("plotworld").getSpawnLocation());
	}

	@EventHandler
	public void quit(PlayerQuitEvent ev) {
		ev.setQuitMessage(null);
	}

	@EventHandler
	public void join(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		UtilPlayer.setTab(player, "Creative-Server");
		if (!player.isOp()) {
			checkAndEditPlayerInv(player);
			Bukkit.getScheduler().runTaskLater(instance.getInstance(), player::updateInventory, 1);
		}
	}

	public void checkAndEditPlayerInv(Player player) {
		PlayerInventory inv = player.getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			item = checkAndEditItem(item, player);
			inv.setItem(i, item);
		}
		inv.setBoots(checkAndEditItem(inv.getBoots(), player));
		inv.setLeggings(checkAndEditItem(inv.getLeggings(), player));
		inv.setChestplate(checkAndEditItem(inv.getChestplate(), player));
		inv.setHelmet(checkAndEditItem(inv.getHelmet(), player));
	}
}
