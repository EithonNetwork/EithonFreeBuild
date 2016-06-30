package net.eithon.plugin.freebuild;

import net.eithon.library.command.CommandSyntaxException;
import net.eithon.library.command.EithonCommand;
import net.eithon.library.command.EithonCommandUtilities;
import net.eithon.library.command.ICommandSyntax;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.time.CoolDown;
import net.eithon.plugin.freebuild.logic.Controller;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler {
	private CoolDown _coolDown;
	private Controller _controller;
	private ICommandSyntax _commandSyntax;

	public CommandHandler(EithonPlugin eithonPlugin, Controller controller) {
		this._controller = controller;
		this._coolDown = new CoolDown("freebuild", Config.V.coolDownTimeInSeconds);
	}

	public ICommandSyntax getCommandSyntax() {
		if (this._commandSyntax != null) return this._commandSyntax;

		ICommandSyntax commandSyntax = EithonCommand.createRootCommand("freebuild");
		commandSyntax.setPermissionsAutomatically();

		try {
			setupBaseCommands(commandSyntax);
			setupPlayerCommands(commandSyntax);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
			return null;
		}
		this._commandSyntax = commandSyntax;
		return this._commandSyntax;
	}

	public void setupBaseCommands(ICommandSyntax commandSyntax)
			throws CommandSyntaxException {
		commandSyntax.parseCommandSyntax("on")
		.setCommandExecutor(ec->freeBuildOnCommand(ec));

		commandSyntax.parseCommandSyntax("off")
		.setCommandExecutor(ec->freeBuildOffCommand(ec));
	}

	public void setupPlayerCommands(ICommandSyntax commandSyntax)
			throws CommandSyntaxException {
		// register
		ICommandSyntax cmd = commandSyntax.parseCommandSyntax("release <player>")
				.setCommandExecutor(ec->releaseCommand(ec));

		cmd
		.getParameterSyntax("player")
		.setMandatoryValues(ec -> EithonCommandUtilities.getOnlinePlayerNames(ec))
		.setDefaultGetter(ec -> { 
			Player player = ec.getPlayer(); 
			return player == null ? null : player.getName();
		});
	}

	private void freeBuildOnCommand(EithonCommand ec)
	{
		Player player = ec.getPlayer();
		if (this._controller.isFreeBuilder(player))
		{
			Config.M.alreadyOn.sendMessage(player);
			return;
		}

		if (!this._controller.inFreebuildWorld(player, true)) {
			return;
		}

		if (!verifyCoolDown(player)) return;
		this._controller.addToFreeBuilders(player);
		
		Config.M.activated.sendMessage(player);
		this._coolDown.addIncident(player);
	}

	private void freeBuildOffCommand(EithonCommand ec)
	{
		Player player = ec.getPlayer();

		if (!this._controller.isFreeBuilder(player))
		{
			Config.M.alreadyOff.sendMessage(player);
			return;
		}

		if (!verifyCoolDown(player)) return;

		this._controller.removeFromFreeBuilders(player);
		Config.M.deactivated.sendMessage(player);
		this._coolDown.addIncident(player);	
	}

	void releaseCommand(EithonCommand ec)
	{		
		Player player = ec.getArgument("player").asPlayer();
		final CommandSender sender = ec.getSender();
		
		if (verifyCoolDown(player)) {
			Config.M.notInCoolDown.sendMessage(sender, player.getName());
			return;
		}
		
		releaseFromCoolDown(player);
		Config.M.releasedFromCoolDown.sendMessage(sender, player.getName());
	}

	private void releaseFromCoolDown(Player player) {
		this._coolDown.removePlayer(player);		
	}

	private boolean verifyCoolDown(Player player) {
		if (player.hasPermission("freebuild.nocooldown")) return true;

		long secondsLeft = this._coolDown.secondsLeft(player);
		if (secondsLeft == 0) return true;

		long minutes = secondsLeft/60;
		long seconds = secondsLeft - 60 * minutes;
		Config.M.waitForCoolDown.sendMessage(player, minutes, seconds);
		return false;
	}
}
