package me.kingingo.kSystem.kManager.kPvP;

import lombok.Getter;
import me.kingingo.kSystem.kSystem;
import me.kingingo.kSystem.kServer.kServer;
import me.kingingo.kSystem.kServer.Listener.PerkListener;
import me.kingingo.kcore.AntiLogout.AntiLogoutManager;
import me.kingingo.kcore.AntiLogout.AntiLogoutType;
import me.kingingo.kcore.Gilden.GildenManager;
import me.kingingo.kcore.Gilden.GildenType;
import me.kingingo.kcore.Kit.Perk;
import me.kingingo.kcore.Kit.PerkManager;
import me.kingingo.kcore.Kit.Perks.PerkArrowPotionEffect;
import me.kingingo.kcore.Kit.Perks.PerkDoubleJump;
import me.kingingo.kcore.Kit.Perks.PerkDoubleXP;
import me.kingingo.kcore.Kit.Perks.PerkDropper;
import me.kingingo.kcore.Kit.Perks.PerkGetXP;
import me.kingingo.kcore.Kit.Perks.PerkGoldenApple;
import me.kingingo.kcore.Kit.Perks.PerkHat;
import me.kingingo.kcore.Kit.Perks.PerkHealPotion;
import me.kingingo.kcore.Kit.Perks.PerkItemName;
import me.kingingo.kcore.Kit.Perks.PerkNoFiredamage;
import me.kingingo.kcore.Kit.Perks.PerkNoHunger;
import me.kingingo.kcore.Kit.Perks.PerkNoWaterdamage;
import me.kingingo.kcore.Kit.Perks.PerkPotionClear;
import me.kingingo.kcore.Kit.Perks.PerkRunner;
import me.kingingo.kcore.Neuling.NeulingManager;
import me.kingingo.kcore.Pet.PetManager;
import me.kingingo.kcore.Pet.Shop.PlayerPetHandler;
import me.kingingo.kcore.SignShop.SignShop;
import me.kingingo.kcore.Util.UtilInv;
import me.kingingo.kcore.friend.FriendManager;

public class kPvP extends kServer{

	@Getter
	private GildenManager gildenManager;
	@Getter
	private FriendManager friendManager;
	@Getter
	private NeulingManager neulingManager;
	@Getter
	private AntiLogoutManager antiLogoutManager;
	@Getter
	private PerkManager perkManager;
	@Getter
	private SignShop signShop;
	@Getter
	private PetManager petManager;
	@Getter
	private PlayerPetHandler petHandler;
	
	public kPvP(kSystem instance){
		super(instance);
		this.petManager=new PetManager(getInstance());
		this.petHandler=new PlayerPetHandler(getInstance().getServerType(),getPetManager(),UtilInv.getBase(), getPermissionManager());
		this.signShop=new SignShop(getInstance(), getCommandHandler(), getStatsManager());
		this.antiLogoutManager=new AntiLogoutManager(getInstance(), AntiLogoutType.DROP_AMOR, 20);
		this.neulingManager=new NeulingManager(getInstance(), getCommandHandler(), 30);
		this.friendManager=new FriendManager(getInstance(), getInstance().getMysql(), getCommandHandler());
		this.gildenManager=new GildenManager(getInstance().getMysql(), GildenType.PVP, getCommandHandler(), getStatsManager());
		getChatListener().setGildenmanager(getGildenManager());
		this.perkManager=new PerkManager(getInstance(), getInstance().getUserData(), new Perk[]{new PerkArrowPotionEffect(),new PerkNoWaterdamage(),new PerkGoldenApple(),new PerkHat(),new PerkNoHunger(),new PerkHealPotion(1),new PerkNoFiredamage(),new PerkRunner(0.35F),new PerkDoubleJump(),new PerkDoubleXP(),new PerkDropper(),new PerkGetXP(),new PerkPotionClear(),new PerkItemName(getCommandHandler())});
		new PerkListener(this.perkManager);
		
		
	}
	
}
