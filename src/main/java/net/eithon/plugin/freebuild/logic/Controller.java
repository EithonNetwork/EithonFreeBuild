package net.eithon.plugin.freebuild.logic;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.json.PlayerCollection;
import net.eithon.plugin.freebuild.Config;

import org.bukkit.entity.Player;

public class Controller {
	private PlayerCollection<FreeBuilderInfo> _freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());
	private EithonPlugin _eithonPlugin;

	public Controller(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		this._freeBuilders = new PlayerCollection<FreeBuilderInfo>(new FreeBuilderInfo());
		delayedLoad(eithonPlugin);
	}

	public void addToFreeBuilders(Player player) {
		this._freeBuilders.put(player, new FreeBuilderInfo(player));
		delayedSave();
	}

	public void removeFromFreeBuilders(Player player) {
		this._freeBuilders.remove(player);
		delayedSave();
	}

	public boolean isFreeBuilder(Player player)
	{
		return this._freeBuilders.hasInformation(player);
	}

	public boolean inFreebuildWorld(Player player, boolean mustBeInFreeBuildWord) {
		if (Config.V.applicableWorlds != null) {
			String currentWorldName = player.getWorld().getName();
			for (String worldName : Config.V.applicableWorlds) {
				if (currentWorldName.equalsIgnoreCase(worldName)) return true;
			}
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
		verbose("canUseFreebuilderProtection", "Return false if the current player is not a freebuilder.");
		if (!isFreeBuilder(player)) return false;
		verbose("canUseFreebuilderProtection", "Return false if the player's current world is not a freebuilder world.");
		if (!inFreebuildWorld(player, warnIfNotInFreebuildWorld)) return false;
		verbose("canUseFreebuilderProtection", "Returns true.");
		return true;
	}

	private void delayedSave() {
		this._freeBuilders.delayedSave(this._eithonPlugin, "freebuilders.json", "FreeBuilders", 0);
	}

	private void delayedLoad(EithonPlugin eithonPlugin) {
		this._freeBuilders.delayedLoad(eithonPlugin, "freebuilders.json", 0);
	}
	
	private void verbose(String method, String format, Object... args)
	{
		this._eithonPlugin.dbgVerbose("Controller", method, format, args);
	}
}
