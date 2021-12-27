package me.yujiro.dragon.abilities;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.yujiro.dragon.Dragon;
import me.yujiro.dragon.Methods;

public class DragonsBolt extends LightningAbility{

	//Config variables
	private long cooldown;
	private double speed, damage, range, hitbox;

	//Set variables
	private Location origin, locleft, locright, loc, endloc, closestleft, closestright;
	private Vector dir;

	private ArrayList<Location>leftarcs;
	private ArrayList<Location>rightarcs;

	private Entity e;
	private Player target;
	private BendingPlayer btarget;
	private double distance;


	public DragonsBolt(Player player) {
		super(player);

		if (CoreAbility.hasAbility(player, this.getClass()) || bPlayer.isOnCooldown(this) || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			return;
		}

		setFields();
		start();
		bPlayer.addCooldown(this);

	}

	public void setFields() {

		//Config
		this.cooldown = Dragon.plugin.getConfig().getLong("Dragon.DragonsBolt.Cooldown");

		this.speed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsBolt.Speed");
		this.damage = Dragon.plugin.getConfig().getDouble("Dragon.DragonsBolt.Damage");
		this.range = Dragon.plugin.getConfig().getDouble("Dragon.DragonsBolt.Range");
		this.hitbox = Dragon.plugin.getConfig().getDouble("Dragon.DragonsBolt.Hitbox");

		//Set variables

		this.origin = player.getEyeLocation();
		this.dir = origin.getDirection();
		this.loc = origin.clone().add(dir);

		if (Methods.getFacingBlock(loc, dir, range, FluidCollisionMode.ALWAYS, true) != null) {
			this.endloc = Methods.getFacingBlock(loc, dir, range, FluidCollisionMode.ALWAYS, true).getLocation();
		}
		else {
			this.endloc = origin.clone().add(dir.clone().multiply(range));
		}

		this.locleft = GeneralMethods.getLeftSide(loc, 0.5);
		this.closestleft = locleft.clone();
		this.locright = GeneralMethods.getRightSide(loc, 0.5);
		this.closestright = locright.clone();




		this.leftarcs = new ArrayList<Location>();
		leftarcs.add(closestleft);
		this.rightarcs = new ArrayList<Location>();
		rightarcs.add(closestright);
		
		this.distance = 0;
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public String getName() {
		return "DragonsBolt";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void progress() {

		if (player.isDead() || !player.isOnline() || bPlayer.isChiBlocked() || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())){
			this.remove();
		}

		distance = loc.distance(origin);

		if (distance > range) {
			this.remove();
		}

		if (loc.getBlock().getType().isSolid()) {
			this.remove();
		}

		loc.add(dir.clone().multiply(speed));
		
		LightningAbility.playLightningbendingSound(loc);

		if (Methods.getAffected(loc, hitbox, player) != null) {
			e = Methods.getAffected(loc, hitbox, player);
			if (e instanceof Player) {
				target = (Player) e;
				btarget = BendingPlayer.getBendingPlayer(target);
				if (btarget != null){
					if (btarget.getBoundAbilityName().equalsIgnoreCase("DragonsBolt") && target.isSneaking()) {
						ParticleEffect.REDSTONE.display(target.getLocation(), 10, 1, 1, 1, 0.005, new Particle.DustOptions(Color.fromRGB(1, 225, 255), 1));
						new DragonsBolt(target);
						this.remove();
					}
					else {
						DamageHandler.damageEntity(e, damage, this);
						this.remove();
					}
				}
				else {
					DamageHandler.damageEntity(e, damage, this);
					this.remove();
				}
			}
			else {
				DamageHandler.damageEntity(e, damage, this);
				this.remove();
			}
		}

		locleft.add(dir.clone().multiply(speed));
		if (locleft.distance(closestleft) > 1) {
			closestleft = randomMidwayVertex(locleft, closestleft);
			leftarcs.add(closestleft);
		}

		for (int i = 0; i < leftarcs.size() - 2; i++) {
			playParticlesBetweenPoints(leftarcs.get(i), leftarcs.get(i+1));
		}


		locright.add(dir.clone().multiply(speed));
		if (locright.distance(closestright) > 1) {
			closestright = randomMidwayVertex(locright, closestright);
			rightarcs.add(closestright);
		}

		for (int i = 0; i < rightarcs.size() - 2; i++) {
			playParticlesBetweenPoints(rightarcs.get(i), rightarcs.get(i+1));
		}

		ParticleEffect.REDSTONE.display(locleft, 1, 0.05, 0.05, 0.05, 0.005, new Particle.DustOptions(Color.fromRGB(1, 225, 255), 1));
		ParticleEffect.REDSTONE.display(locright, 1, 0.05, 0.05, 0.05, 0.005, new Particle.DustOptions(Color.fromRGB(1, 225, 255), 1));


	}

	public Location randomMidwayVertex(Location start, Location end) {
		Vector midpoint = end.clone().subtract(start.clone()).toVector().multiply(0.5);
		Vector random = new Vector (Math.random()-0.5, Math.random() - 0.5, Math.random() -0.5).normalize().multiply(Math.log(distance));
		return (start.clone().add(midpoint).add(random));
	}

	public void playParticlesBetweenPoints(Location start, Location end) {
		Vector difference = end.clone().subtract(start.clone()).toVector();
		Double distance = difference.length();
		Vector normalised = difference.normalize();

		for (double d = 0 ; d < distance; d += 0.2) {
			Location temploc = start.clone().add(normalised.clone().multiply(d));

			ParticleEffect.REDSTONE.display(temploc, 1, 0, 0, 0, 0.005, new Particle.DustOptions(Color.fromRGB(1, 225, 255), 1));
		}
	}




	@Override
	public void remove() {
		super.remove();
		return;
	}


	@Override
	public String getDescription() {
		return "By: __Yujiro\n"
				+ "Fire Lord Ozai was shown to be able to generate a powerful lightning strike with great speed during Sozin's Comet."; 
	}

	@Override
	public String getInstructions() {
		return "Left click to fire."; 
	}



















}
