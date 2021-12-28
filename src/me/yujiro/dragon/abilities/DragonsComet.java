package me.yujiro.dragon.abilities;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

import me.yujiro.dragon.Dragon;
import me.yujiro.dragon.Methods;


public class DragonsComet extends FireAbility{

	//Config variables
	private long cooldown, chargetime;
	private double cometradius, chargeradius, hitboxradius, speed, damage, knockback, range;


	//Set variables
	private long starttime;
	private Location origin, loc;
	private Vector dir;
	private Boolean hasbluefire, charged, cometstarted;


	//Set variables

	public DragonsComet(Player player) {
		super(player);

		if (!bPlayer.canBend(this) || CoreAbility.hasAbility(player, this.getClass()) || bPlayer.isOnCooldown(this) || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			return;
		}

		setFields();
		start();

	}


	public void setFields() {

		//Config
		this.cooldown = Dragon.plugin.getConfig().getLong("Dragon.DragonsComet.Cooldown");
		this.chargetime = Dragon.plugin.getConfig().getLong("Dragon.DragonsComet.ChargeTime");
	
		this.cometradius = Dragon.plugin.getConfig().getDouble("Dragon.DragonsComet.CometRadius");
		this.chargeradius = Dragon.plugin.getConfig().getDouble("Dragon.DragonsComet.ChargeRadius");
		this.hitboxradius = Dragon.plugin.getConfig().getDouble("Dragon.DragonsComet.HitboxRadius");


		this.speed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsComet.Speed");
		this.damage = Dragon.plugin.getConfig().getDouble("Dragon.DragonsComet.Damage");
		this.knockback = Dragon.plugin.getConfig().getDouble("Dragon.DragonsComet.Knockback");
		this.range = Dragon.plugin.getConfig().getDouble("Dragon.DragonsComet.Range");

		//Set variables
		this.starttime = System.currentTimeMillis();
		this.origin = player.getLocation();
		this.dir = origin.getDirection();
		this.loc = origin.clone();

		this.hasbluefire = bPlayer.hasElement(Element.BLUE_FIRE);
		this.charged = false;
		this.cometstarted = false;

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
		return "DragonsComet";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
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
		
		if (System.currentTimeMillis() - starttime > chargetime && !charged) {
			charged = true;
		}

		if (!cometstarted && !player.isSneaking()) {
			this.remove();
		}
		
		if (!charged) {
			
			this.origin = player.getLocation();
			this.dir = origin.getDirection();
			this.loc = origin.clone();
			
			for (double theta = 0; theta < 360; theta += 360/(2*Math.PI*chargeradius)){
				Vector zpositive = new Vector (0,0,chargeradius);
				Location temploc = loc.clone().add(zpositive.clone().rotateAroundY(Math.toRadians(theta)));
				if (Methods.getFacingBlock(temploc, new Vector(0,-1,0), 3, FluidCollisionMode.ALWAYS, true) != null) {
					/*
					if (!hasbluefire) {
						TempBlock firetb = new TempBlock(Methods.getFacingBlock(temploc, new Vector(0,-1,0), 3, FluidCollisionMode.ALWAYS, true).getLocation().add(0,1,0).getBlock(), Material.FIRE);
						firetb.setRevertTime(1000);
					}
					else {
						TempBlock firetb = new TempBlock(Methods.getFacingBlock(temploc, new Vector(0,-1,0), 3, FluidCollisionMode.ALWAYS, true).getLocation().add(0,1,0).getBlock(), Material.SOUL_FIRE);
						firetb.setRevertTime(1000);
					}
					*/
				}
				
			
			}
			
			for (double theta = 0; theta < 360; theta += 5){
				Vector zpositive = new Vector (0,0,chargeradius);
				Location temploc = loc.clone().add(zpositive.clone().rotateAroundY(Math.toRadians(theta))).add(0, Math.cos(Math.toRadians(theta * 4)), 0).add(0,1,0);
				if (!hasbluefire) {
					ParticleEffect.FLAME.display(temploc, 3, 0.1, 0.3, 0.1);
				}
				else {
					ParticleEffect.SOUL_FIRE_FLAME.display(temploc, 3, 0.1, 0.3, 0.1);
				}
			}
			
			
		}
		
		
		
		else {
			if (!cometstarted) {
				origin = player.getEyeLocation();
				dir = origin.getDirection();
				loc = player.getEyeLocation().clone().add(dir.clone().multiply(cometradius + 1));
				Methods.playFireBall(loc, cometradius, 20, 1, hasbluefire);
			}
			else {
				FireAbility.playFirebendingSound(loc);
				loc.add(dir.clone().multiply(speed));
				Methods.playFireBall(loc, cometradius, 20, 1, hasbluefire);
				ParticleEffect.SMOKE_NORMAL.display(loc, 5, 0.2, 0.2, 0.2);
				
				if (Methods.getAffected(loc, hitboxradius, player) != null) {
					Methods.playExplosion(loc, cometradius, 5000, true);
					DamageHandler.damageEntity(Methods.getAffected(loc, hitboxradius, player), damage, this);
					this.remove();
				}
				
				if (loc.distance(origin) > range) {
					this.remove();
				}
				
				if (loc.getBlock().getType().isSolid()) {
					Methods.playExplosion(loc, cometradius, 5000, true);
					this.remove();
				}
				
			}
			
		}
	}
	
	public void onClick() {
		if (charged && !cometstarted) {
			cometstarted = true;
		}
	}





	@Override
	public void remove() {
		if (cometstarted) {
			bPlayer.addCooldown(this);
		}
		super.remove();
		return;
	}


	@Override
	public String getDescription() {
		return "By: __Yujiro\n"
				+ "The Dragon of the West, General Iroh obliterated the mighty walls of Ba Sing Se using DragonsComet."; 
	}

	@Override
	public String getInstructions() {
		return "Hold shift to charge, left click to fire."; 
	}

}



































