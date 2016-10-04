package eu.epicpvp.kSystem.Server.Creative.Inventory.Buttons;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;

import eu.epicpvp.datenclient.client.LoadedPlayer;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryYesNo;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilServer;

public class MemberButton extends ButtonCopy{

	public MemberButton(int slot,int index,InventoryCopy memberPage, PlotAPI api) {
		super(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				PlotPlayer pplayer = api.wrapPlayer(player);
				Plot plot = ((Plot)pplayer.getPlots().toArray()[0]);

				if(plot.getMembers().size() > index){
					UUID uuid = (UUID) plot.getMembers().toArray()[index];
					LoadedPlayer loadedplayer = UtilServer.getClient().getPlayerAndLoad(uuid);
					((InventoryPageBase)object).setItem(slot, UtilItem.Item(UtilItem.Head(loadedplayer.getName()),new String[]{"§7Klicke, um "+loadedplayer.getName()+" von","§7deinem Plot zu kicken."}, "§e"+loadedplayer.getName()));
				}
			}

		}, new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				if(((ItemStack) object).getType()==Material.SKULL_ITEM){
					InventoryYesNo qu = new InventoryYesNo("Spieler kicken?", new Click(){

						@Override
						public void onClick(Player player, ActionType type, Object object) {
							PlotPlayer pplayer = api.wrapPlayer(player);
							Plot plot = ((Plot)pplayer.getPlots().toArray()[0]);
							UUID uuid = (UUID) plot.getMembers().toArray()[index];
							plot.removeMember(uuid);
							InviteButton.plotCache.refresh(uuid);
							memberPage.open(player, UtilInv.getBase());
						}

					}, new Click(){

						@Override
						public void onClick(Player player, ActionType type, Object object) {
							memberPage.open(player, UtilInv.getBase());
						}

					});
					UtilInv.getBase().addAnother(qu);
					player.openInventory(qu);
				}
			}

		}, UtilItem.RenameItem(new ItemStack(101), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §6Dieser Slot ist frei"));
	}

}
