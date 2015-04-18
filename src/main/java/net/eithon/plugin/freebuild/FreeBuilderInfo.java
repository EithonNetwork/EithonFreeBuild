package net.eithon.plugin.freebuild;

import java.util.UUID;

import net.eithon.library.core.IUuidAndName;
import net.eithon.library.json.Converter;
import net.eithon.library.json.IJson;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

class FreeBuilderInfo implements IJson<FreeBuilderInfo>, IUuidAndName {
	private UUID playerId;
	private String playerName;
	
	FreeBuilderInfo(Player player)
	{
		this.playerId = player.getUniqueId();
		this.playerName = player.getName();
	}
	
	FreeBuilderInfo() {
	}
	
	Player getPlayer()
	{
		return Bukkit.getServer().getPlayer(this.playerId);
	}
	
	public String getName()
	{
		return this.playerName;
	}

	public String toString() {
		return String.format("%s", this.getName());
	}

	@Override
	public FreeBuilderInfo factory() {
		return new FreeBuilderInfo();
	}

	@Override
	public UUID getUniqueId() {
		return this.playerId;
	}

	@Override
	public Object toJson() {
		return Converter.fromPlayer(this.playerId, this.playerName);
	}

	@Override
	public void fromJson(Object json) {
		this.playerId = Converter.toPlayerId((JSONObject) json);
		this.playerName = Converter.toPlayerName((JSONObject) json);
		
	}
}
