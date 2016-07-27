package eu.epicpvp.kSystem.Server.GunGame.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kSystem.Server.GunGame.GunGame;
import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

public class CommandGunGame implements CommandExecutor {

	private Player player;
	private GunGame gungame;

	public CommandGunGame(GunGame gungame) {
		this.gungame = gungame;
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "gungame", alias = { "gg" }, sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		player = (Player) sender;

		if (player.isOp()) {
			if (args.length == 0) {
				player.sendMessage("/gungame reset [ALL/ONLINE/PLAYER]");
			} else {
				if (args[0].equalsIgnoreCase("reset") && args.length == 2) {
					if (args[1].equalsIgnoreCase("all")) {
						for (Player player : UtilServer.getPlayers()) {
							gungame.getKit().setLevel(player, 1);
						}

						gungame.getInstance().getConfig().set("LevelResetAll", System.currentTimeMillis());
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDer Levelstand von allen Spielern wurde resetet!");
					} else if (args[1].equalsIgnoreCase("online")) {
						for (Player player : UtilServer.getPlayers()) {
							gungame.getKit().setLevel(player, 1);
						}
						player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDer Levelstand von allen Online Spielern wurde resetet!");
					} else {
						Player target = null;

						if (UtilPlayer.isOnline(args[1])) {
							target = Bukkit.getPlayer(args[1]);
						} else {
							target = UtilPlayer.loadPlayer(args[1]);
						}

						if (target != null) {
							gungame.getKit().setLevel(target, 1);
							player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDie Level von diesem Spieler wurden resetet!");
						} else {
							player.sendMessage(TranslationHandler.getText(player, "PREFIX") + "§cDieser Spieler wurde nicht gefunden!");
						}
					}
				}
			}
		}
		return false;
	}

}
