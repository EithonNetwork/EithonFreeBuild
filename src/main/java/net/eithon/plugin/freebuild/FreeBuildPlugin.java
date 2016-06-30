package net.eithon.plugin.freebuild;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.plugin.freebuild.logic.Controller;

import org.bukkit.event.Listener;

public final class FreeBuildPlugin extends EithonPlugin {
	private Controller _controller;

	@Override
	public void onEnable() {
		super.onEnable();
		Config.load(this);
		this._controller = new Controller(this);
		CommandHandler commandHandler = new CommandHandler(this, this._controller);
		Listener eventListener = new EventListener(this, this._controller);
		super.activate(commandHandler.getCommandSyntax(), eventListener);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		this._controller = null;
	}
}
