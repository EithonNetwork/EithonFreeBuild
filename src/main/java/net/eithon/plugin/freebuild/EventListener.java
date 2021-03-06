package net.eithon.plugin.freebuild;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.plugin.freebuild.logic.Controller;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.projectiles.ProjectileSource;

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
		Player player = null;
		if (damager instanceof Player) player = (Player) damager;
		else if (damager instanceof Projectile) {
			Projectile projectile = (Projectile) damager;
			ProjectileSource source = projectile.getShooter();
			if (source instanceof Player) player = (Player) source; 
		}

		if (player == null) return;
		
		if (!(event.getEntity() instanceof Monster)) return;
		Monster monster = (Monster) event.getEntity();

		if (!this._controller.canUseFreebuilderProtection(player)) return;

		// You can attack monsters that targets you
		if (monster.getTarget() == player) return;

		event.setCancelled(true);
	}

	// No damage on self
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
		if (shooter == null) return;
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
		verbose("onPlayerToggleFlightEvent", "Enter");
		if (event.isCancelled()) {
			verbose("onPlayerToggleFlightEvent", "Event has already been cancelled. Return.");
			return;
		}
		if (!event.isFlying()) {
			verbose("onPlayerToggleFlightEvent", "Not flying, rather landing. Return.");
			return;
		}
		
		Player player = event.getPlayer();
		if (!this._controller.inFreebuildWorld(player, false)) {
			verbose("onPlayerToggleFlightEvent", "Not in a freebuilder world. Return.");
			return;
		}
		
		// Allow players with permission freebuild.canfly to fly
		if (player.hasPermission("freebuild.canfly")) {
			verbose("onPlayerToggleFlightEvent", "User has freebuild.canfly permission. Return.");
			return;
		}
		
		if (this._controller.isFreeBuilder(player)) {
			verbose("onPlayerToggleFlightEvent", "The player is a freebuilder. Set fly speed and return.");
			Config.C.setSpeed.execute(Config.V.flySpeed, player.getName());
			return;
		}
		verbose("onPlayerToggleFlightEvent", "The player is not allowed to fly. Cancel the event and return.");
		player.sendMessage("You are currently not allowed to fly.");
		event.setCancelled(true);
		Config.C.stopFly.executeAs(event.getPlayer());
	}
	
	private void verbose(String method, String format, Object... args)
	{
		this._eithonPlugin.dbgVerbose("EventListener", method, format, args);
	}
}
