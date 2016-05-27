package eu.epicpvp.kSystem.Server.Creative.Inventory.Buttons;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;

import dev.wolveringer.client.LoadedPlayer;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryYesNo;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonForMultiButtonsCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonMultiCopy;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

public class InviteButton extends ButtonMultiCopy{

	private static LoadingCache<UUID, ArrayList<Plot>> plotCache = CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<UUID, ArrayList<Plot>>() {
		public ArrayList<Plot> load(UUID uuid) throws Exception {
			ArrayList<Plot> plots = new ArrayList<>(PS.get().getPlots());
			plots.removeIf(plot -> !plot.getMembers().contains(uuid));
			return plots;
		};
	});
	
	public InviteButton(InventoryPageBase page,int slot,int index, PlotAPI api) {
		super(new ButtonForMultiButtonsCopy[]{new ButtonForMultiButtonsCopy(page, slot+9, new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				if(((ItemStack)object).getTypeId() == 101)return;
				PlotPlayer pplayer = api.wrapPlayer(player);
				
				
				if(plotCache!=null){
					try {
						ArrayList<Plot> plots = plotCache.get(player.getUniqueId());
						
						if(plots.size() > index){
							player.closeInventory();
							pplayer.teleport(plots.get(index).getHome());
						}
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					
				}
			}
			
		}, 
		UtilItem.RenameItem(new ItemStack(Material.ENDER_PEARL), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §6Teleportiere dich zu der §eInsel§6."),
		new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {

				try {
					ArrayList<Plot> plots = plotCache.get(player.getUniqueId());
					if(plots.size() > index){
						Plot plot = plots.get(index);
						LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(((UUID)plot.getOwners().toArray()[0]));		
						((InventoryPageBase)object).setItem(slot, UtilItem.RenameItem(UtilItem.Head(loadedplayer.getName()), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 "+loadedplayer.getName()+"'s PLot"));
						return;
					}
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				((InventoryPageBase)object).setItem(slot, UtilItem.RenameItem(new ItemStack(Material.SKULL_ITEM,1,(byte)3), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Nicht belegt."));
				((InventoryPageBase)object).setItem(slot+9, UtilItem.RenameItem(new ItemStack(101), " "));
			}
			
		}),
				new ButtonForMultiButtonsCopy(page, slot+18, new Click(){

					@Override
					public void onClick(Player player, ActionType type, Object object) {
						if(((ItemStack)object).getTypeId() == 101)return;
						InventoryYesNo q = new InventoryYesNo("Sicher?", new Click(){

							@Override
							public void onClick(Player player, ActionType type, Object object) {

								try {
									ArrayList<Plot> plots = plotCache.get(player.getUniqueId());
									if(plots.size() > index){
										Plot plot = plots.get(index);
										String playername = UtilServer.getClient().getPlayerAndLoad(((UUID)plot.getOwners().toArray()[0])).getName();
										player.sendMessage(TranslationHandler.getPrefixAndText(player, "SKYBLOCK_MEMBER_LEAVE_SELF",playername));
											
										if(UtilPlayer.isOnline(playername)){
											Player owner = Bukkit.getPlayer(playername);
											owner.sendMessage(TranslationHandler.getPrefixAndText(owner, "SKYBLOCK_MEMBER_LEAVE",player.getName()));
										}
											
										plot.removeMember(player.getUniqueId());
										player.closeInventory();
									}
								} catch (ExecutionException e) {
									e.printStackTrace();
								}
							}
							
						}, new Click(){

							@Override
							public void onClick(Player player, ActionType type, Object object) {
								player.closeInventory();
							}
							
						});
						UtilInv.getBase().addAnother(q);
						player.openInventory(q);
					}
					
				}, 
				UtilItem.RenameItem(new ItemStack(351,1,(byte)1), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §cKlicke, um die Insel zu verlassen."),
				new Click(){

					@Override
					public void onClick(Player player, ActionType type, Object object) {

						try {
							ArrayList<Plot> plots = plotCache.get(player.getUniqueId());
							if(plots.size() > index){
								return;
							}
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						((InventoryPageBase)object).setItem(slot+18, UtilItem.RenameItem(new ItemStack(101), " "));
					}
					
				})});
	}



}
