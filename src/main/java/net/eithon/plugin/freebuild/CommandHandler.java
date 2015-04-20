package net.eithon.plugin.freebuild;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.CommandParser;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.ICommandHandler;
import net.eithon.library.time.CoolDown;
import net.eithon.plugin.freebuild.logic.Controller;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements ICommandHandler {
	private static final String ON_COMMAND = "/freebuild on";
	private static final String OFF_COMMAND = "/freebuild off";
	private CoolDown _coolDown;
	private Controller _controller;

	public CommandHandler(EithonPlugin eithonPlugin, Controller controller) {
		this._controller = controller;
		this._coolDown = new CoolDown("freebuild", Config.V.coolDownTimeInMinutes*60);
	}

	public boolean onCommand(CommandParser commandParser) {
		if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1)) return true;
		EithonPlayer eithonPlayer = commandParser.getEithonPlayerOrInformSender();
		if (eithonPlayer == null) return true;
		
		String command = commandParser.getArgumentCommand();
		if (command.equals("on")) {
			freeBuildOnCommand(commandParser);
		} else if (command.equals("off")) {
			freeBuildOffCommand(commandParser);
		} else {
			commandParser.showCommandSyntax();
		}
		return true;
	}

	void freeBuildOnCommand(CommandParser commandParser)
	{
		if (!commandParser.hasPermissionOrInformSender("freebuild.on")) return;
		if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1, 1)) return;
		Player player = commandParser.getPlayer();
		if (this._controller.isFreeBuilder(player))
		{
			player.sendMessage("Freebuild mode is already active.");
			return;
		}

		if (!this._controller.inFreebuildWorld(player, true)) {
			return;
		}

		if (!verifyCoolDown(player)) return;
		this._controller.addToFreeBuilders(player);
		
		player.sendMessage("Freebuild mode is now active.");
		this._coolDown.addPlayer(player);
	}

	void freeBuildOffCommand(CommandParser commandParser)
	{
		if (!commandParser.hasPermissionOrInformSender("freebuild.off")) return;
		if (!commandParser.hasCorrectNumberOfArgumentsOrShowSyntax(1, 1)) return;
		
		Player player = commandParser.getPlayer();

		if (!this._controller.isFreeBuilder(player))
		{
			player.sendMessage("Survival mode is already active (freebuild is OFF).");
			return;
		}

		if (!verifyCoolDown(player)) return;

		this._controller.removeFromFreeBuilders(player);
		player.sendMessage("Survival mode is now active (freebuild is OFF).");	
		this._coolDown.addPlayer(player);	
	}

	private boolean verifyCoolDown(Player player) {
		if (player.hasPermission("freebuild.nocooldown")) return true;

		int secondsLeft = this._coolDown.secondsLeft(player);
		if (secondsLeft == 0) return true;

		int minutes = secondsLeft/60;
		int seconds = secondsLeft - 60 * minutes;
		Config.M.waitForCoolDown.sendMessage(player, minutes, seconds);
		return false;
	}

	@Override
	public void showCommandSyntax(CommandSender sender, String command) {
		if (command.equals("on")) {
			sender.sendMessage(ON_COMMAND);
		} else if (command.equals("off")) {
			sender.sendMessage(OFF_COMMAND);
		} else {
			sender.sendMessage(String.format("Unknown command: %s.", command));
		}
	}
}
