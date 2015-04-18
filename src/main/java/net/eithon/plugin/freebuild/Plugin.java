package net.eithon.plugin.freebuild;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {


	@Override
	public void onEnable() {
		EithonPlugin eithonPlugin = EithonPlugin.get(this);
		eithonPlugin.enable();
		Commands.get().enable(eithonPlugin);
		getServer().getPluginManager().registerEvents(Events.get(), this);	
	}

	@Override
	public void onDisable() {
		Commands.get().disable();
		EithonPlugin eithonPlugin = EithonPlugin.get(this);
		eithonPlugin.disable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return Commands.get().onCommand(sender, args);
	}
}
