package net.eithon.plugin.freebuild;

import java.util.List;

import org.bukkit.entity.Player;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.json.PlayerCollection;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

public class Controller {
	private ConfigurableMessage _mustBeInFreebuildWordMessage;
	private PlayerCollection<FreeBuilderInfo> _freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());
	private List<String> _applicableWorlds;
	private EithonPlugin _eithonPlugin;

	public Controller(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		Configuration config = eithonPlugin.getConfiguration();
		this._freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());
		this._applicableWorlds = config.getStringList("FreebuildWorldNames");	
		this._mustBeInFreebuildWordMessage = config.getConfigurableMessage(
				"messages.MustBeInFreebuildWord", 0, 
				"You can only switch between survival and freebuild in the SurvivalFreebuild world.");
	}

	public void addToFreeBuilders(Player player) {
		this._freeBuilders.put(player, new FreeBuilderInfo(player));
	}

	public void removeFromFreeBuilders(Player player) {
		this._freeBuilders.remove(player);
	}

	public boolean isFreeBuilder(Player player)
	{
		return this._freeBuilders.hasInformation(player);
	}

	boolean inFreebuildWorld(Player player, boolean mustBeInFreeBuildWord) {
		String currentWorldName = player.getWorld().getName();
		for (String worldName : this._applicableWorlds) {
			if (currentWorldName.equalsIgnoreCase(worldName)) return true;
		}
		if (mustBeInFreeBuildWord) this._mustBeInFreebuildWordMessage.sendMessage(player);
		return false;
	}

	public boolean canUseFreebuilderProtection(Player player)
	{
		return canUseFreebuilderProtection(player, false);
	}

	public boolean canUseFreebuilderProtection(Player player, boolean warnIfNotInFreebuildWorld)
	{
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, 
				"canUseFreebuilderProtection: Return false if the current player is not a freebuilder.");
		if (!isFreeBuilder(player)) return false;
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE,
				"canUseFreebuilderProtection: Return false if the player's current world is not a freebuilder world.");
		if (!inFreebuildWorld(player, warnIfNotInFreebuildWorld)) return false;
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE,
				"canUseFreebuilderProtection: Returns true.");
		return true;
	}
}
