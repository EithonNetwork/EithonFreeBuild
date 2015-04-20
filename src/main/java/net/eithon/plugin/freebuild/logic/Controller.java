package net.eithon.plugin.freebuild.logic;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.json.PlayerCollection;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.plugin.freebuild.Config;

import org.bukkit.entity.Player;

public class Controller {
	private PlayerCollection<FreeBuilderInfo> _freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());
	private EithonPlugin _eithonPlugin;

	public Controller(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		this._freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());
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

	public boolean inFreebuildWorld(Player player, boolean mustBeInFreeBuildWord) {
		String currentWorldName = player.getWorld().getName();
		for (String worldName : Config.V.applicableWorlds) {
			if (currentWorldName.equalsIgnoreCase(worldName)) return true;
		}
		if (mustBeInFreeBuildWord) Config.M.mustBeInFreebuildWord.sendMessage(player);
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
