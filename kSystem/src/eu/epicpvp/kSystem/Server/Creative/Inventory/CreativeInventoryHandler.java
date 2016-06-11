package eu.epicpvp.kSystem.Server.Creative.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;

import eu.epicpvp.kSystem.Server.Creative.Creative;
import eu.epicpvp.kSystem.Server.Creative.Inventory.Buttons.BiomeChangeButton;
import eu.epicpvp.kSystem.Server.Creative.Inventory.Buttons.InviteButton;
import eu.epicpvp.kSystem.Server.Creative.Inventory.Buttons.MemberButton;
import eu.epicpvp.kcore.Enum.Zeichen;
import eu.epicpvp.kcore.Inventory.InventoryPageBase;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryCopy;
import eu.epicpvp.kcore.Inventory.Inventory.InventoryYesNo;
import eu.epicpvp.kcore.Inventory.Item.Click;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonBase;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonCopy;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventory;
import eu.epicpvp.kcore.Inventory.Item.Buttons.ButtonOpenInventoryCopy;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.AnvilGUI;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilClickEvent;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilClickEventHandler;
import eu.epicpvp.kcore.Util.AnvilGUI.AnvilSlot;
import eu.epicpvp.kcore.Util.InventorySize;
import eu.epicpvp.kcore.Util.InventorySplit;
import eu.epicpvp.kcore.Util.UtilEvent.ActionType;
import eu.epicpvp.kcore.Util.UtilInv;
import eu.epicpvp.kcore.Util.UtilItem;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;

public class CreativeInventoryHandler {
	@Getter
	private PlotAPI plotApi;
	@Getter
	private Creative instance;
	private InventoryPageBase create;
	private InventoryPageBase menue;
	private InventoryCopy options;
	private InventoryPageBase biomeChange;
	private InventoryCopy member;
	private InventoryCopy invited;
	
	public CreativeInventoryHandler(Creative instance){
		this.plotApi=instance.getPlotApi();
		this.instance=instance;
		
		init();
	}
	
	public void open(Player player){
		PlotPlayer pplayer = getPlotApi().wrapPlayer(player);
		
		if(!pplayer.getPlots().isEmpty()){
			player.openInventory(this.menue);
		}else{
			player.openInventory(this.create);
		}
	}
	
	public void init(){
		this.member = new InventoryCopy(InventorySize._45, "Member Page");
		UtilInv.getBase().addPage(member);
		this.invited = new InventoryCopy(InventorySize._54, "Member Page");
		UtilInv.getBase().addPage(invited);
		this.biomeChange = new InventoryPageBase(InventorySize._54, "Biome ändern");
		this.options = new InventoryCopy(InventorySize._54, "Einstellungen");
		UtilInv.getBase().addPage(options);
		this.menue = new InventoryPageBase(InventorySize._45, "Plot Menue");
		UtilInv.getBase().addPage(this.menue);
		this.create = new InventoryPageBase(InventorySize._27, "Plot Create");
		UtilInv.getBase().addPage(this.create);
		
		//CREATE START
		this.create.addButton(0, new ButtonBase(new Click(){
			@Override
		public void onClick(Player player, ActionType type, Object object) {
			player.closeInventory();
		}}, UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cSchließen")));
		
		this.create.addButton(14, new ButtonBase(new Click(){
			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Bukkit.dispatchCommand(player, "plot auto");
				player.closeInventory();
		}},UtilItem.RenameItem(new ItemStack(Material.SIGN), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Erstelle ein §eCreative§6 Plot!")));
		
		this.create.addButton(12, new ButtonBase(new Click(){
			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Bukkit.dispatchCommand(player, "plot claim");
				player.closeInventory();
		}},UtilItem.Item(new ItemStack(Material.IRON_BOOTS), new String[]{"","§7Klicke, um das Plot auf dem du dich","§7befindest für dich zu beanspruchen."},"§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Beanspruche dieses §eCreative§6 Plot!")));
		//CREATE END
		
		//MENUE START
		this.menue.addButton(10, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				PlotPlayer pplayer = getPlotApi().wrapPlayer(player);
				pplayer.teleport(((Plot)pplayer.getPlots().toArray()[0]).getHome());
			}},UtilItem.Item(new ItemStack(Material.BED), new String[]{}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Teleportiere dich zu deinem §ePlot§6.")));
		this.menue.addButton(12, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				PlotPlayer pplayer = getPlotApi().wrapPlayer(player);
				pplayer.teleport( ((Plot)pplayer.getPlots().toArray()[0]).getCenter() );
			}},UtilItem.Item(new ItemStack(Material.FIREWORK_CHARGE), new String[]{}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Teleportiere dich in die §eMitte§6 deines Plots.")));
		this.menue.addButton(16, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				player.teleport(Bukkit.getWorld("plotworld").getSpawnLocation());
			}},UtilItem.Item(new ItemStack(Material.ENDER_PEARL), new String[]{}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Teleportiere dich zum §eSpawn")));
		this.menue.addButton(20, new ButtonOpenInventoryCopy(this.member, UtilInv.getBase(), UtilItem.Item(new ItemStack(Material.SKULL_ITEM,1,(byte)SkullType.PLAYER.ordinal()), new String[]{}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§e Mitglieder §6Verwaltung")));
		this.menue.addButton(24, new ButtonOpenInventoryCopy(invited, UtilInv.getBase(), UtilItem.Item(new ItemStack(Material.EMERALD), new String[]{}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Plots auf denen du §eMitglied§6 bist")));
		this.menue.addButton(30, new ButtonOpenInventoryCopy(UtilServer.getAchievementsHandler().getInventory(), UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.BOOK),"§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Erfolge")));
		this.menue.addButton(14, new ButtonOpenInventoryCopy(this.options, UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(356), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Einstellungen")));
		this.menue.addButton(32, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				InventoryYesNo qu = new InventoryYesNo("Plot löschen", new Click(){

					@Override
					public void onClick(Player player, ActionType type, Object object) {
						PlotPlayer pplayer = getPlotApi().wrapPlayer(player);
						Plot plot = ((Plot)pplayer.getPlots().toArray()[0]);
						player.closeInventory();
						plot.deletePlot(new Runnable(){

							@Override
							public void run() {
								open(player);
							}
						});
					}
					
				}, new Click(){

					@Override
					public void onClick(Player player, ActionType type, Object object) {
						player.openInventory(menue);
					}
					
				});
				
				UtilInv.getBase().addAnother(qu);
				player.openInventory(qu);
			}},UtilItem.RenameItem(new ItemStack(Material.STAINED_CLAY,1,(byte)14), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§c Plot löschen")));

		this.menue.addButton(0, new ButtonBase(new Click(){
			@Override
		public void onClick(Player player, ActionType type, Object object) {
			player.closeInventory();
		}}, UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cSchließen")));
		//MENUE END
		
		//OPTIONS START
		this.options.addButton(11, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				Bukkit.dispatchCommand(player, "plot sethome");
			}
			
		}, UtilItem.RenameItem(new ItemStack(Material.BED), ""+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Homepunkt setzen")));
		
		this.options.addButton(15, new ButtonBase(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				player.openInventory(biomeChange);
			}
			
		}, UtilItem.RenameItem(new ItemStack(Material.GRASS), ""+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Plot Biom ändern")));
		
		this.options.setItem(InventorySplit._27.getMiddle(), UtilItem.Item(new ItemStack(Material.EYE_OF_ENDER), new String[]{" ","§7Hier kannst du deinen Besucher-" ,"§7Warp aktivieren und deaktivieren."}, "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+"§6 Besucher Warp bearbeiten"));
		this.options.addButton(InventorySplit._36.getMiddle(), new ButtonCopy(new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				kConfig config = UtilServer.getUserData().getConfig(player);
				
				if(config.getBoolean("Plot.Visit")){
					((InventoryPageBase)object).setItem(InventorySplit._36.getMiddle(), UtilItem.RenameItem(new ItemStack(351,1,(byte)10), "§aOn"));
				}else{
					((InventoryPageBase)object).setItem(InventorySplit._36.getMiddle(), UtilItem.RenameItem(new ItemStack(351,1,(byte)8), "§cOff"));
				}
			}
			
		}, new Click(){

			@Override
			public void onClick(Player player, ActionType type, Object object) {
				kConfig config = UtilServer.getUserData().getConfig(player);
				
				if(config.getBoolean("Plot.Visit")){
					config.set("Plot.Visit", false);
					((ItemStack)object).setDurability((short)8);
					ItemMeta im = ((ItemStack)object).getItemMeta();
					im.setDisplayName("§cOff");
					((ItemStack)object).setItemMeta(im);
				}else{
					config.set("Plot.Visit", true);
					((ItemStack)object).setDurability((short)10);
					ItemMeta im = ((ItemStack)object).getItemMeta();
					im.setDisplayName("§aOn");
					((ItemStack)object).setItemMeta(im);
				}
				config.save();
			}
			
		}, new ItemStack(351,1,(byte)8)));

		this.options.addButton(0, new ButtonOpenInventory(this.menue,UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
		//OPTIONS END
		
		//Biome start
			this.biomeChange.addButton(0, new ButtonOpenInventoryCopy(this.options,UtilInv.getBase(), UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
			this.biomeChange.addButton(20, new BiomeChangeButton(Biome.FOREST,getPlotApi(), UtilItem.RenameItem(new ItemStack(Material.SAPLING), "§6Wald")));
			this.biomeChange.addButton(21, new BiomeChangeButton(Biome.TAIGA,getPlotApi(), UtilItem.RenameItem(new ItemStack(Material.SAPLING,1,(byte)1), "§6Taiga")));
			this.biomeChange.addButton(22, new BiomeChangeButton(Biome.DESERT,getPlotApi(), UtilItem.RenameItem(new ItemStack(32), "§6Wüste")));
			this.biomeChange.addButton(23, new BiomeChangeButton(Biome.SWAMPLAND,getPlotApi(), UtilItem.RenameItem(new ItemStack(111), "§6Sumpf")));
			this.biomeChange.addButton(24, new BiomeChangeButton(Biome.JUNGLE,getPlotApi(), UtilItem.RenameItem(new ItemStack(106), "§6Dschungel")));
				
			this.biomeChange.addButton(29, new BiomeChangeButton(Biome.BEACH,getPlotApi(), UtilItem.RenameItem(new ItemStack(Material.SAND), "§6Strand")));
			this.biomeChange.addButton(30, new BiomeChangeButton(Biome.MESA,getPlotApi(), UtilItem.RenameItem(new ItemStack(Material.RED_SANDSTONE), "§6Mesa")));
			this.biomeChange.addButton(31, new BiomeChangeButton(Biome.SKY,getPlotApi(), UtilItem.RenameItem(new ItemStack(Material.ENDER_STONE), "§6End")));
			this.biomeChange.addButton(32, new BiomeChangeButton(Biome.PLAINS,getPlotApi(), UtilItem.RenameItem(new ItemStack(Material.GRASS), "§6Graslandschaft")));
			this.biomeChange.addButton(33, new BiomeChangeButton(Biome.SAVANNA,getPlotApi(), UtilItem.RenameItem(new ItemStack(6,1,(byte)4), "§6Savanne")));
			UtilInv.getBase().addPage(biomeChange);
		//Biome end
		
		//Invited start
			this.invited.setCreate_new_inv(true);
			this.invited.addButton(0, new ButtonOpenInventory(this.menue,UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
			int s = 20;
			for(int i = 0; i < 3; i++){
				this.invited.addButton(s, new InviteButton(this.invited, s, i, getPlotApi()));
				s+=2;
			}
		//Invited END
			
		//Member START
			this.member.setCreate_new_inv(true);
			this.member.addButton(0, new ButtonOpenInventory(this.menue,UtilItem.RenameItem(new ItemStack(Material.BARRIER), "§cZurück")));
			this.member.addButton(8, new ButtonBase(new Click(){

				@Override
				public void onClick(Player player, ActionType type, Object object) {
					AnvilGUI gui = new AnvilGUI(player, UtilServer.getPermissionManager().getInstance(), new AnvilClickEventHandler() {
						
						@Override
						public void onAnvilClick(AnvilClickEvent event) {
							if(event.getSlot() == AnvilSlot.OUTPUT){
								player.closeInventory();
								if(UtilPlayer.isOnline(event.getName())){
									Player target = (Player)Bukkit.getPlayer(event.getName());
									getInstance().getInvite().put(target.getName(), player.getName());
									TextComponent text = new TextComponent(TranslationHandler.getPrefixAndText(target, "SKYBLOCK_INVITE_GET",player.getName()));
									text.addExtra(UtilPlayer.createClickableText(" §a[ACCEPT]", "/kplot accept "+player.getName()));
									target.spigot().sendMessage(text);
									player.sendMessage(TranslationHandler.getPrefixAndText(player, "SKYBLOCK_INVITE_SEND",target.getName()));
								}else{
									player.sendMessage(TranslationHandler.getPrefixAndText(player, "PLAYER_IS_OFFLINE", event.getName()));
								}
							}
						}
					});
					gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "Name"));
					gui.setSlot(AnvilGUI.AnvilSlot.OUTPUT, UtilItem.RenameItem(new ItemStack(Material.NAME_TAG), "§aFertig"));
					gui.open();
				}
				
			}, UtilItem.RenameItem(new ItemStack(Material.BOOK_AND_QUILL), "§7"+Zeichen.DOUBLE_ARROWS_R.getIcon()+" §6Mitglieder§e hinzufügen§6.")));
			
			s = 20;
			for(int i = 0; i < 3; i++){
				this.member.addButton(s, new MemberButton(s, i, this.member, getPlotApi()));
				s+=2;
			}
		//Member END

		this.biomeChange.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.create.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.menue.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.member.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.invited.fillBorder(Material.STAINED_GLASS_PANE, 7);
		this.options.fillBorder(Material.STAINED_GLASS_PANE, 7);
	}
	
}
