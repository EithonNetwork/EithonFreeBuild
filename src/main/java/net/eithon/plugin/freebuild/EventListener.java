package net.eithon.plugin.freebuild;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class EventListener implements Listener {
	private EithonPlugin _eithonPlugin = null;
	private Controller _controller;

	public EventListener(EithonPlugin eithonPlugin, Controller controller) {
		this._controller = controller;
		this._eithonPlugin = eithonPlugin;
	}

	// Avoid becoming a target
	@EventHandler
	public void onEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
		if (event.isCancelled()) return;

		Entity target = event.getTarget();
		if (!(target instanceof Player)) return;
		Player player = (Player) target;

		if (!(event.getEntity() instanceof Monster)) return;

		if (!this._controller.canUseFreebuilderProtection(player)) return;

		event.setCancelled(true);
	}

	// Don't attack monsters
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;

		Entity damager = event.getDamager();
		if (!(damager instanceof Player)) return;
		Player player = (Player) damager;

		if (!(event.getEntity() instanceof Monster)) return;
		Monster monster = (Monster) event.getEntity();

		if (!this._controller.canUseFreebuilderProtection(player)) return;

		// You can attack monsters that targets you
		if (monster.getTarget() == player) return;

		event.setCancelled(true);
	}

	// No damage
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.isCancelled()) return;

		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) return;
		Player player = (Player) entity;

		if (!this._controller.canUseFreebuilderProtection(player)) return;

		event.setCancelled(true);
	}

	// No potion effects on free build players
	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (event.isCancelled()) return;
		Player shooter = null;
		boolean shooterIsFreeBuilder = false;
		if(event.getPotion().getShooter() instanceof Player) {
			shooter = (Player) event.getPotion().getShooter();
			shooterIsFreeBuilder = this._controller.canUseFreebuilderProtection(shooter);
		}
		for (LivingEntity livingEntity : event.getAffectedEntities()) {
			if (!(livingEntity instanceof Player)) {
				// FreeBuilders can't affect non-players
				if (shooterIsFreeBuilder) event.setIntensity(livingEntity, 0.0);
				continue;
			}
			Player affectedPlayer = (Player) livingEntity;
			boolean shooterAndAffectedAreTheSamePlayer = affectedPlayer.getUniqueId() == shooter.getUniqueId();
			boolean affectedIsFreebuilder = this._controller.canUseFreebuilderProtection(affectedPlayer);
			// Freebuilders can affect themselves
			if (shooterIsFreeBuilder && shooterAndAffectedAreTheSamePlayer) continue;
			// Non freebuilders can affect each other
			if (!shooterIsFreeBuilder && !affectedIsFreebuilder) continue;
			event.setIntensity(livingEntity, 0.0);
		}
	}
	
	// Survival players can't fly
	@EventHandler
	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent event) {
		Logger debug = this._eithonPlugin.getEithonLogger();
		debug.debug(DebugPrintLevel.VERBOSE, "1. Entered onPlayerToggleFlightEvent");
		debug.debug(DebugPrintLevel.VERBOSE, "2. Cancel if event already has been cancelled.");
		if (event.isCancelled()) return;
		debug.debug(DebugPrintLevel.VERBOSE, "3. Give OK if the player doesn't try to fly, i.e. is landing.");
		if (!event.isFlying()) return;
		
		debug.debug(DebugPrintLevel.VERBOSE, "4. Give OK if the player is not in any of the free builder worlds.");
		if (!this._controller.inFreebuildWorld(event.getPlayer(), false)) return;
		
		// Allow players with permission freebuild.canfly to fly
		debug.debug(DebugPrintLevel.VERBOSE, "5. Give OK if user has freebuild.canfly permission.");
		if (event.getPlayer().hasPermission("freebuild.canfly")) return;
		
		debug.debug(DebugPrintLevel.VERBOSE, "5. Give OK if the player is a freebuilder.");
		if (this._controller.isFreeBuilder(event.getPlayer())) return;
		debug.debug(DebugPrintLevel.VERBOSE, "6. Final decision: The player is not allowed to fly.");
		event.getPlayer().sendMessage("You are currently not allowed to fly.");
		event.setCancelled(true);
	}
}