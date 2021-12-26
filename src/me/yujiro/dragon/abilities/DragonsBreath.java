package me.yujiro.dragon.abilities;


import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import com.projectkorra.projectkorra.util.TempBlock;

import me.yujiro.dragon.Dragon;
import me.yujiro.dragon.Methods;


public class DragonsBreath extends FireAbility{

	//Config 
	private long cooldown;
	private long chargetime;
	private long duration;
	private double damage;
	private double range;
	private double speed;
	private int firehelixes;


	//set variables
	private long chargestarttime;
	private long breathstarttime;
	private Location origin;
	private Location loc;
	private Vector dir;
	private Boolean charged;
	private Boolean started;
	private double arbitraryangleincrement;
	private double angle;
	private double angledifference;
	private BossBar barduration;

	private Boolean hasbluefire;

	public DragonsBreath(Player player) {
		super(player);

		if (!bPlayer.canBend(this) || CoreAbility.hasAbility(player, this.getClass()) || bPlayer.isOnCooldown(this) || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			return;
		}

		setFields();
		start();
	}

	public void setFields() {

		//Config
		this.cooldown = Dragon.plugin.getConfig().getLong("Dragon.DragonsBreath.Cooldown");
		this.chargetime = Dragon.plugin.getConfig().getLong("Dragon.DragonsBreath.ChargeTime");
		this.duration = Dragon.plugin.getConfig().getLong("Dragon.DragonsBreath.Duration");
		this.damage = Dragon.plugin.getConfig().getDouble("Dragon.DragonsBreath.Damage");
		this.speed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsBreath.Speed");
		this.range = Dragon.plugin.getConfig().getDouble("Dragon.DragonsBreath.Range");
		this.firehelixes = Dragon.plugin.getConfig().getInt("Dragon.DragonsBreath.FireHelixes");

		//Set variables
		this.chargestarttime = System.currentTimeMillis();
		this.charged = false;
		this.started = false;
		if (firehelixes!=0) {
			this.angledifference = 360/(firehelixes+1);
		}
		this.arbitraryangleincrement = 0;

		barduration = Bukkit.getServer().createBossBar("DragonsBreath",BarColor.RED, BarStyle.SEGMENTED_10);

		this.hasbluefire = bPlayer.hasElement(Element.BLUE_FIRE);
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public String getName() {
		return "DragonsBreath";
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

		Long timecharged = System.currentTimeMillis() - chargestarttime;

		if (timecharged < chargetime && !player.isSneaking()) {
			this.remove();
		}

		if (timecharged > chargetime) {
			charged = true;
		}

		if (started) {
			player.setFireTicks(0);
			Long timeelapsed = System.currentTimeMillis() - breathstarttime;
			Double progress = 1 - (double) timeelapsed / (double) duration;
			if (progress < 0) {
				barduration.setProgress(0);
			}
			else {
				barduration.setProgress(progress);
			}

			if (timeelapsed > duration) {
				this.remove();
			}

			this.origin = player.getEyeLocation();
			this.dir = origin.getDirection().normalize();

			arbitraryangleincrement+=5;

			for (double d = 0; d < range; d+=0.5) {

				angle = arbitraryangleincrement + 10*d;
				this.loc = origin.clone().add(dir.clone().multiply(d));

				if (d < 1) {
					Vector smokeorthagonal = GeneralMethods.getOrthogonalVector(dir, 90,1);
					for (double smokeangle = 0; smokeangle < 360; smokeangle +=20) {
						Location smokeloc = loc.clone().add(dir.clone().multiply(2)).add(smokeorthagonal.clone().multiply(d+0.5).rotateAroundAxis(dir, Math.toRadians(smokeangle)));
						//ParticleEffect.SPELL_MOB_AMBIENT.display(smokeloc, 0, 244/255, 243/255, 239/255, 1);
						ParticleEffect.SMOKE_NORMAL.display(smokeloc, 1, 0.1, 0.1, 0.1);
					}
				}

				for (int i = 0; i < firehelixes; i++) {
					Vector orthagonal = GeneralMethods.getOrthogonalVector(dir, 90, Math.log(d+2) * Math.log(d+2));
					Location helixloc = loc.clone().add(dir.clone().multiply(2)).add(orthagonal.clone().rotateAroundAxis(dir, Math.toRadians(angle + angledifference*i)));
					//Location helixloc = loc.clone().add(dir.clone()).add(new Vector(0,Math.cos(angle + angledifference*i) + Math.log(distance+2) * Math.log(distance+2) ,0).rotateAroundAxis(dir, Math.toRadians(angle + angledifference*i)));

					if (!GeneralMethods.isObstructed(loc, helixloc)){
						if (hasbluefire) {
							ParticleEffect.SOUL_FIRE_FLAME.display(helixloc, 1, 0.1, 0.1, 0.1);
						}
						else {	
							ParticleEffect.FLAME.display(helixloc, 1, 0.1 ,0.1 ,0.1);
							//ParticleEffect.REDSTONE.display(helixloc, 1, 0, 0, 0, 0.005, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1));
						}	
					}
					/*
					else {
						if (helixloc.getBlock().getType().isSolid()) {
							if (helixloc.clone().add(0,1,0).getBlock().getType().isAir()){
								if (!TempBlock.isTempBlock(helixloc.clone().add(0,1,0).getBlock())) {
									//Methods.playFireBall(helixloc.clone().add(0,1,0), 0.5, 10, 5, bPlayer.hasElement(Element.BLUE_FIRE));
									if (hasbluefire) {
										TempBlock tb = new TempBlock(helixloc.clone().add(0,1,0).getBlock(), Material.SOUL_FIRE);
										tb.setRevertTime(10000);
									}
									else {
										TempBlock tb = new TempBlock(helixloc.clone().add(0,1,0).getBlock(), Material.FIRE);
										tb.setRevertTime(10000);
									}

								}
							}
						}
					}
					*/


				}

				if (hasbluefire) {
					ParticleEffect.SOUL_FIRE_FLAME.display(loc.clone().add(dir.clone().multiply(2)), 1, d/3, d/3, d/3);
				}
				else {	
					ParticleEffect.FLAME.display(loc.clone().add(dir.clone().multiply(2)), 1, d/3, d/3, d/3);
				}

				if (Methods.getAffected(loc, d, player) != null) {
					DamageHandler.damageEntity(Methods.getAffected(loc, d, player), damage, this);
				}
			}
		}

	}



	public void onClick() {
		if (charged && !started) {
			started = true;
			breathstarttime = System.currentTimeMillis();
			this.barduration.addPlayer(player);
		}
	}





	@Override
	public void remove() {

		barduration.removeAll();
		if (this.started) {
			bPlayer.addCooldown(this);
		}
		super.remove();
		return;
	}


	@Override
	public String getDescription() {
		return "By: __Yujiro\n"
				+ "Phoenix King Ozai showed us a breath which rivalled even that of the dragons. Scorch the earth."; 
	}

	@Override
	public String getInstructions() {
		return "Hold shift to charge, left click to fire."; 
	}
}















