package me.yujiro.dragon.abilities;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.yujiro.dragon.Dragon;
import me.yujiro.dragon.Methods;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class DragonsJet extends FireAbility{

	//Config 
	private long cooldown;

	private double manatotal;
	private double manaregen;
	private double manausehover;
	private double manauseleap;
	private double manauseslowfly;
	private double manausefastfly;

	private double leapspeed;
	private double fastflyspeed;
	private double slowflyspeed;


	//set variables
	private Location playerloc;
	private Vector dir;
	private double manaleft;
	private double progress;
	private BossBar barduration;
	
	private Boolean bluefire = bPlayer.hasElement(Element.BLUE_FIRE);;

	private enum jetstate {
		OFF, LEAP, HOVER, FLY;
	}

	private jetstate dragonsjetstate;

	public DragonsJet(Player player) {
		super(player);

		if (!bPlayer.canBend(this) || CoreAbility.hasAbility(player, this.getClass()) || bPlayer.isOnCooldown(this) || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			return;
		}

		setFields();
		start();
		bPlayer.addCooldown(this);
	}

	public void setFields() {

		//Config
		this.cooldown = Dragon.plugin.getConfig().getLong("Dragon.DragonsJet.Cooldown");

		this.manatotal = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.ManaTotal");
		this.manaregen = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.ManaRegenPerSecond");
		this.manausehover = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.ManaUseHoverPerSecond");
		this.manauseleap = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.ManaUseLeap");
		this.manauseslowfly = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.ManaUseSlowFlyPerSecond");
		this.manausefastfly = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.ManaUseFastFlyPerSecond");

		this.leapspeed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.LeapSpeed");
		this.slowflyspeed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.SlowFlySpeed");
		this.fastflyspeed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsJet.FastFlySpeed");

		//Set variables
		this.playerloc = player.getLocation();
		this.dir = playerloc.getDirection().normalize();
		this.manaleft = manatotal;
		this.onRightClick();

		barduration = Bukkit.getServer().createBossBar("DragonsJet",BarColor.RED, BarStyle.SOLID);
		this.barduration.addPlayer(player);
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return playerloc;
	}

	@Override
	public String getName() {
		return "DragonsJet";
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public void progress() {
		if (player.isDead() || !player.isOnline() || bPlayer.isChiBlocked() || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())){
			this.remove();
		}

		this.playerloc = player.getLocation();
		this.dir = playerloc.getDirection().normalize();

		if (manaleft <= 0) {
			this.remove();
		}

		progress = (manaleft / manatotal);

		if (progress < 0 ) {
			this.remove();
		}
		else {
			barduration.setProgress(progress);
		}

		if (dragonsjetstate != jetstate.FLY || !player.isSneaking()) {
			if (player.isGliding()) {
				player.setGliding(false);
			}
		}


		if (dragonsjetstate == jetstate.OFF) {

			if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsJet")) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_RED  + "Off"));
			}


			if ((manaleft+manaregen/20) < manatotal ) {
				manaleft += manaregen/20;
			}

			if (!bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsJet")) {
				barduration.setVisible(false);
			}	
		}
		else {
			if (!barduration.isVisible()) {
				barduration.setVisible(true);
			}
		}


		if (dragonsjetstate == jetstate.LEAP) {
			playParticles(false, true, jetstate.LEAP);
			
			if (player.getVelocity().getY() <= 0); {
				dragonsjetstate = jetstate.FLY;
			}
		}


		if (dragonsjetstate == jetstate.HOVER) {
			player.setFallDistance(0);
			playParticles(true, false, dragonsjetstate);
			ParticleEffect.SMOKE_NORMAL.display(playerloc.clone().subtract(0,0.2,0), 3, 0.1, 0.1, 0.1);
			if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsJet")) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_RED  + "Hover"));
			}

			player.setVelocity(new Vector (0,0,0));
			manaleft -= manausehover/20;
		}

		if (dragonsjetstate == jetstate.FLY) {
			player.setFallDistance(0);
			playParticles(true, true, dragonsjetstate);
			
			if (bPlayer.getBoundAbilityName().equalsIgnoreCase("DragonsJet")) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_RED  + "Jet"));
			}

			if (player.isSneaking()) {
				manaleft -= manausefastfly/20;
				player.setVelocity(dir.clone().multiply(fastflyspeed));
				player.setGliding(true);
			}
			else {
				player.setGliding(false);
				manaleft -= manauseslowfly/20;
				player.setVelocity(dir.clone().multiply(slowflyspeed));
			}
		}



	}

	public void playParticles(Boolean feet, Boolean hands, jetstate position) {

		Location ploc = playerloc.clone();
		if (position.equals(jetstate.FLY)) {
			if (feet) {
				if (!player.isSneaking()) {
				Methods.playFireBall(GeneralMethods.getRightSide(ploc.clone().subtract(dir), 0.2), 0.5, 4, 1, bluefire);
				Methods.playFireBall(GeneralMethods.getLeftSide(ploc.clone().subtract(dir), 0.2), 0.5, 4, 1, bluefire);
				}
				else {
					Methods.playFireBall(GeneralMethods.getRightSide(ploc, 0.2), 0.5, 4, 1, bluefire);
					Methods.playFireBall(GeneralMethods.getLeftSide(ploc, 0.2), 0.5, 4, 1, bluefire);
				}
			}
			if (hands) {
				if (!player.isSneaking()) {
					Methods.playFireBall(GeneralMethods.getRightSide(ploc.clone().add(0,1.2,0), 0.2), 0.5, 4, 1, bluefire);
					Methods.playFireBall(GeneralMethods.getLeftSide(ploc.clone().add(0,1.2,0), 0.2), 0.5, 4, 1, bluefire);
				}
				if (player.isSneaking()) {
					Methods.playFireBall(GeneralMethods.getRightSide(ploc.clone().add(dir.clone().multiply(1.2)), 0.2), 0.5, 4, 1, bluefire);
					Methods.playFireBall(GeneralMethods.getLeftSide(ploc.clone().add(dir.clone().multiply(1.2)), 0.2), 0.5, 4, 1, bluefire);
				}
			}
		}
		else {
			if (feet) {
				Methods.playFireBall(GeneralMethods.getRightSide(ploc, 0.2), 0.5, 4, 1, bluefire);
				Methods.playFireBall(GeneralMethods.getLeftSide(ploc, 0.2), 0.5, 4, 1, bluefire);
			}
			if (hands) {
				Methods.playFireBall(GeneralMethods.getRightSide(ploc.clone().add(0,1,0), 0.2), 0.5, 4, 1, bluefire);
				Methods.playFireBall(GeneralMethods.getLeftSide(ploc.clone().add(0,1,0), 0.2), 0.5, 4, 1, bluefire);
			}
		}
	}



	public void onLeftClick() {
		if (player.isSneaking()) {
			if (dragonsjetstate == jetstate.OFF) {
				dragonsjetstate = jetstate.HOVER;
				return;
			}
			if (dragonsjetstate == jetstate.HOVER || dragonsjetstate == jetstate.FLY) {
				dragonsjetstate = jetstate.OFF;
				return;
			}
		}
		else {
			if (dragonsjetstate == jetstate.FLY) {
				dragonsjetstate = jetstate.HOVER;
				return;
			}
			if (dragonsjetstate == jetstate.HOVER ) {
				dragonsjetstate = jetstate.FLY;
				return;
			}	
		}
	}


	public void onRightClick() {
		Methods.playExplosion(playerloc, 2.0, 0, false);
		player.setVelocity(new Vector (0,1,0).multiply(leapspeed));
		this.dragonsjetstate = jetstate.LEAP;
		
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_RED  + "Leap"));
		manaleft -= manauseleap;
		
	}



	@Override
	public void remove() {
		barduration.removeAll();
		bPlayer.addCooldown(this);
		super.remove();
		return;
	}


	@Override
	public String getDescription() {
		return "By: __Yujiro\n"
				+ "Phoenix King Ozai demonstrated the power of DragonsJet during Sozin's Comet against Avatar Aang."; 
	}

	@Override
	public String getInstructions() {
		return "DragonsLeap: Shift + right click a block to leap in the opposite direction.\n"+
				"Hold shift and left click to toggle DragonsJet on and off, whilst off duration will regenerate.\n"+
				"Alternatively, left click to alternate between DragonsHover and DragonsFly.\n"+
				"DragonsHover: the most practical variation - useful for aiming midair. \n"+
				"DragonsFly: Traverse ground quickly, hold shift to go even faster.";
	}
}















