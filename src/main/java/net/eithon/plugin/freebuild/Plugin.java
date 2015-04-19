package net.eithon.plugin.freebuild;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.event.Listener;

public final class Plugin extends EithonPlugin {
	private Controller _controller;

	@Override
	public void onEnable() {
		this._controller = new Controller(this);
		CommandHandler commandHandler = new CommandHandler(this, this._controller);
		Listener eventListener = new EventListener(this, this._controller);
		super.enable(commandHandler, eventListener);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		this._controller = null;
	}
}
