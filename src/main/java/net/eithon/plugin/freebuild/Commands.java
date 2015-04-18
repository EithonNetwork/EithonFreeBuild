package net.eithon.plugin.freebuild;

import java.util.List;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.json.PlayerCollection;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.time.CoolDown;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {
	private static Commands singleton = null;
	private static final String ON_COMMAND = "/freebuild on";
	private static final String OFF_COMMAND = "/freebuild off";
	private ConfigurableMessage mustBeInFreebuildWordMessage;
	private ConfigurableMessage waitForCoolDownMessage;
	private List<String> applicableWorlds;
	private int _coolDownTimeInMinutes;
	private CoolDown _coolDown;
	private EithonPlugin _eithonPlugin = null;

	private PlayerCollection<FreeBuilderInfo> freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());

	private Commands() {
	}

	static Commands get()
	{
		if (singleton == null) {
			singleton = new Commands();
		}
		return singleton;
	}

	void enable(EithonPlugin eithonPlugin){
		this._eithonPlugin = eithonPlugin;
		this.freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());
		Configuration config = eithonPlugin.getConfiguration();
		this.applicableWorlds = config.getStringList("FreebuildWorldNames");
		this._coolDownTimeInMinutes = config.getInt("CoolDownTimeInMinutes", 30);
		this.mustBeInFreebuildWordMessage = config.getConfigurableMessage(
				"messages.MustBeInFreebuildWord", 0, 
				"You can only switch between survival and freebuild in the SurvivalFreebuild world.");
		this.waitForCoolDownMessage = config.getConfigurableMessage(
				"messages.WaitForCoolDown", 2, 
				"The remaining cool down period for switching Freebuild mode is %d minutes and %d seconds.");
		this._coolDown = new CoolDown("freebuild", this._coolDownTimeInMinutes*60);
	}

	void disable() {
		this.freeBuilders = null;
	}

	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player!");
			return false;
		}
		if (args.length < 1) {
			sender.sendMessage("Incomplete command...");
			return false;
		}

		Player player = (Player) sender;

		String command = args[0].toLowerCase();
		if (command.equals("on")) {
			Commands.get().onCommand(player, args);
		} else if (command.equals("off")) {
			Commands.get().offCommand(player, args);
		} else {
			sender.sendMessage("Could not understand command.");
			return false;
		}
		return true;
	}

	public boolean canUseFreebuilderProtection(Player player)
	{
		return canUseFreebuilderProtection(player, false);
	}

	public boolean canUseFreebuilderProtection(Player player, boolean warnIfNotInFreebuildWorld)
	{
		this._eithonPlugin.getLogger().debug(DebugPrintLevel.VERBOSE, 
				"canUseFreebuilderProtection: Return false if the current player is not a freebuilder.");
		if (!isFreeBuilder(player)) return false;
		this._eithonPlugin.getLogger().debug(DebugPrintLevel.VERBOSE,
				"canUseFreebuilderProtection: Return false if the player's current world is not a freebuilder world.");
		if (!inFreebuildWorld(player, warnIfNotInFreebuildWorld)) return false;
		this._eithonPlugin.getLogger().debug(DebugPrintLevel.VERBOSE,
				"canUseFreebuilderProtection: Returns true.");
		return true;
	}

	boolean isFreeBuilder(Player player)
	{
		return this.freeBuilders.get(player) != null;
	}

	boolean inFreebuildWorld(Player player, boolean mustBeInFreeBuildWord) {
		String currentWorldName = player.getWorld().getName();
		for (String worldName : this.applicableWorlds) {
			if (currentWorldName.equalsIgnoreCase(worldName)) return true;
		}
		if (mustBeInFreeBuildWord) this.mustBeInFreebuildWordMessage.sendMessage(player);
		return false;
	}

	void onCommand(Player player, String[] args)
	{
		EithonPlayer eithonPlayer = new EithonPlayer(player);
		if (!eithonPlayer.hasPermissionOrWarn("freebuild.on")) return;
		if (!arrayLengthIsWithinInterval(args, 1, 1)) {
			player.sendMessage(ON_COMMAND);
			return;
		}

		if (isFreeBuilder(player))
		{
			player.sendMessage("Freebuild mode is already active.");
			return;
		}

		if (!inFreebuildWorld(player, true)) {
			this.mustBeInFreebuildWordMessage.sendMessage(player);
			return;
		}

		if (!verifyCoolDown(player)) return;

		this.freeBuilders.put(player, new FreeBuilderInfo(player));
		player.sendMessage("Freebuild mode is now active.");
		this._coolDown.addPlayer(player);
	}

	private boolean verifyCoolDown(Player player) {
		if (player.hasPermission("freebuild.nocooldown")) return true;

		int secondsLeft = this._coolDown.secondsLeft(player);
		if (secondsLeft == 0) return true;

		int minutes = secondsLeft/60;
		int seconds = secondsLeft - 60 * minutes;
		this.waitForCoolDownMessage.sendMessage(player, minutes, seconds);
		return false;
	}

	void offCommand(Player player, String[] args)
	{
		EithonPlayer eithonPlayer = new EithonPlayer(player);
		if (!eithonPlayer.hasPermissionOrWarn("freebuild.off")) return;
		
		if (!arrayLengthIsWithinInterval(args, 1, 1)) {
			player.sendMessage(OFF_COMMAND);
			return;
		}

		if (!isFreeBuilder(player))
		{
			player.sendMessage("Survival mode is already active (freebuild is OFF).");
			return;
		}

		if (!verifyCoolDown(player)) return;

		this.freeBuilders.remove(player);
		player.sendMessage("Survival mode is now active (freebuild is OFF).");	
		this._coolDown.addPlayer(player);	
	}


	private boolean arrayLengthIsWithinInterval(Object[] args, int min, int max) {
		return (args.length >= min) && (args.length <= max);
	}
}
