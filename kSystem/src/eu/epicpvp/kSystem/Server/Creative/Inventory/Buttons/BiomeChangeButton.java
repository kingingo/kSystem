package eu.epicpvp.kSystem.Server.Creative.Inventory.Buttons;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;

import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;

public class BiomeChangeButton extends ButtonBase{

	public BiomeChangeButton(Biome biome,PlotAPI api, ItemStack item) {
		super(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				PlotPlayer pplayer = api.wrapPlayer(player);
				Plot plot = pplayer.getLocation().getPlot();
				
				plot.addRunning();
			    plot.setBiome(biome.name(), new Runnable()
			    {
			      public void run() {
			        plot.removeRunning();
			      }
			    });
			}
			
		}, item);
	}

}
