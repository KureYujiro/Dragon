package me.yujiro.dragon;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;


public class Methods {

	public static void playFireBall(Location loc, double radii, double density, int amount, Boolean bluefire) {
		for (double i = 0; i <= Math.PI; i += Math.PI / density) {
			double radius = Math.sin(i) * radii;
			double y = Math.cos(i) * radii;
			for (double a = 0; a < Math.PI * 2; a+= Math.PI*2 / density) {
				double x = Math.cos(a) * radius;
				double z = Math.sin(a) * radius;
				if(!bluefire) {
					ParticleEffect.FLAME.display(loc.clone().add(x,y,z), amount, 0.1, 0.1, 0.1);
				}
				if (bluefire) {
					ParticleEffect.SOUL_FIRE_FLAME.display(loc.clone().add(x,y,z), amount, 0.1, 0.1, 0.1);
				}
			}
		}
	}

	public static LivingEntity getAffected(Location loc, double radii, Player player) {
		LivingEntity e = null;
		for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc, radii)) {
			if ((entity instanceof LivingEntity) && entity.getUniqueId() != player.getUniqueId()) {
				e = (LivingEntity) entity;
			}
		}
		return e;
	}

	//testing raytracing - it works
	public static Block getFacingBlock(Location start, Vector direction, double maxdistance, FluidCollisionMode fluidcollision, Boolean ignorepassableblocks) {
		RayTraceResult rayresult = start.getWorld().rayTraceBlocks(start, direction, maxdistance, fluidcollision, ignorepassableblocks);
		if (rayresult != null) {
			return rayresult.getHitBlock();
		}
		else {
			return null;
		}


	}

	public static void playExplosion(Location loc, Double radius, long revert, Boolean explode) {
		loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		ParticleEffect.EXPLOSION_NORMAL.display(loc, 20, radius, radius, radius);
		if (explode) {
			for (Block b : GeneralMethods.getBlocksAroundPoint(loc, radius)) {
				if (!b.getType().equals(Material.AIR) && !b.getType().equals(Material.WATER) && !b.getType().equals(Material.LAVA)) {
					TempBlock tb = new TempBlock(b, Material.AIR);
					tb.setRevertTime(revert);
				}
			}
		}
	}




}
