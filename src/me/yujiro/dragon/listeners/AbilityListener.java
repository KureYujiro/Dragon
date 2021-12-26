package me.yujiro.dragon.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

import me.yujiro.dragon.abilities.DragonsBolt;
import me.yujiro.dragon.abilities.DragonsBreath;
import me.yujiro.dragon.abilities.DragonsComet;
import me.yujiro.dragon.abilities.DragonsJet;
import me.yujiro.dragon.abilities.DragonsScales;



public class AbilityListener implements Listener {

	@EventHandler
	public void onSwing(PlayerAnimationEvent event) {

		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (event.isCancelled() || bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsJet")) {
			if (!CoreAbility.hasAbility(player, DragonsJet.class)) {
				new DragonsJet(player);
			}
			else {
				CoreAbility.getAbility(player, DragonsJet.class).onLeftClick();
			}

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsBreath")) {
			if (CoreAbility.hasAbility(player, DragonsBreath.class)){
				CoreAbility.getAbility(player, DragonsBreath.class).onClick();
			}
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsComet")) {
			if (CoreAbility.hasAbility(player, DragonsComet.class)){
				CoreAbility.getAbility(player, DragonsComet.class).onClick();
			}
			
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsScales")) {
			if (CoreAbility.hasAbility(player, DragonsScales.class)){
				CoreAbility.getAbility(player, DragonsScales.class).onClick();
			}
			else {
				new DragonsScales(player);
			}
		
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsBolt")) {
			new DragonsBolt(player);
		}



	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {

		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (event.isCancelled() || bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsBreath")) {
			new DragonsBreath(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsComet")) {
			new DragonsComet(player);

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsScales")) {
			if (CoreAbility.hasAbility(player, DragonsScales.class)){
				CoreAbility.getAbility(player, DragonsScales.class).onShift();
			}
		}


	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		if (event.isCancelled() || bPlayer == null) {
			return;

		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;

		} if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsJet") && CoreAbility.hasAbility(player, DragonsJet.class)) {
				CoreAbility.getAbility(player, DragonsJet.class).onRightClick();
			}
		}
	}

	@EventHandler
	public void onGlide(EntityToggleGlideEvent event) {

		Player player = (Player) event.getEntity();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		if (CoreAbility.hasAbility(player, DragonsJet.class)) {
			event.setCancelled(true);
		}
		else {
			return;
		}
	}
}








