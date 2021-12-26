package me.yujiro.dragon.abilities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.yujiro.dragon.Dragon;

public class DragonsScales extends FireAbility{

	//Config variables
	private long cooldown, duration;
	private double clickradius, shiftrange, shiftlength, shiftgrowspeed, shiftheight, collisionradius;

	//Set variables
	private long starttime;
	private Location origin, loc;
	private Vector dir;
	private Boolean hasbluefire, hasshifted;
	private double currentshiftrange;
	private ArrayList<Location> conelocs;
	private ArrayList<Location> walllocs;

	private Vector orthagonalleft, orthagonalright;
	
	
	public DragonsScales(Player player) {
		super(player);

		if (!bPlayer.canBend(this) || CoreAbility.hasAbility(player, this.getClass()) || bPlayer.isOnCooldown(this) || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			return;
		}

		setFields();
		start();


	}


	public void setFields() {

		//Config
		this.cooldown = Dragon.plugin.getConfig().getLong("Dragon.DragonsScales.Cooldown");
		this.duration = Dragon.plugin.getConfig().getLong("Dragon.DragonsScales.Duration");

		this.clickradius = Dragon.plugin.getConfig().getDouble("Dragon.DragonsScales.ClickRadius");
		this.shiftrange = Dragon.plugin.getConfig().getDouble("Dragon.DragonsScales.ShiftRange");
		this.shiftlength = Dragon.plugin.getConfig().getDouble("Dragon.DragonsScales.ShiftLength");
		this.shiftgrowspeed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsScales.ShiftGrowSpeed");
		this.shiftheight = Dragon.plugin.getConfig().getDouble("Dragon.DragonsScales.ShiftHeight");
		this.collisionradius = Dragon.plugin.getConfig().getDouble("Dragon.DragonsScales.CollisionRadius");

		//Set variables
		this.starttime = System.currentTimeMillis();
		this.origin = player.getEyeLocation();
		this.dir = origin.getDirection();
		this.loc = origin.clone().add(dir.clone());
		this.hasbluefire = bPlayer.hasElement(Element.BLUE_FIRE);
		this.hasshifted = false;

		ProjectKorra.getCollisionInitializer().addSmallAbility(this);
		
		this.currentshiftrange = 0;
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
		return "DragonsScales";
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
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

		if (System.currentTimeMillis() - starttime > duration) {
			this.remove();
		}
		
		this.origin = player.getEyeLocation();
		this.dir = origin.getDirection();
		this.loc = origin.clone().add(dir.clone());

		if (!hasshifted) {
			this.conelocs = new ArrayList<Location>();
			for (double d = 0.2; d < clickradius ; d += 0.5){
				Vector orthagonal = GeneralMethods.getOrthogonalVector(dir, 90, d);
				for (double theta = 0; theta < 360; theta += 30) {
					Location temploc = loc.clone().add(orthagonal.clone().rotateAroundAxis(dir, Math.toRadians(theta)).add(dir.clone().multiply(d)));
					conelocs.add(temploc);
					if (!hasbluefire) {
						ParticleEffect.FLAME.display(temploc, 1, 0.1, 0.1, 0.1);
					}
					else {
						ParticleEffect.SOUL_FIRE_FLAME.display(temploc, 1, 0.1, 0.1, 0.1);
					}
				}
			}
		}
		else {
			currentshiftrange += shiftgrowspeed;
			if (currentshiftrange > shiftrange) {
				this.remove();
			}

			
			this.walllocs = new ArrayList<Location>();
			
			for (double d = -shiftlength/2; d < shiftlength/2 ; d += 0.4) {
				for (double h = 0; h < shiftheight; h+= 0.2) {
					Location templocleft = player.getLocation().add(orthagonalleft.clone()).add(new Vector (0,h,0)).add(dir.clone().multiply(d));
					Location templocright = player.getLocation().add(orthagonalright.clone()).add(new Vector (0,h,0)).add(dir.clone().multiply(d));
					walllocs.add(templocleft);
					walllocs.add(templocright);
					
					if (!hasbluefire) {
						ParticleEffect.FLAME.display(templocleft, 1, 0.1, 0.1, 0.1);
						ParticleEffect.FLAME.display(templocright, 1, 0.1, 0.1, 0.1);
					}
					else {
						ParticleEffect.SOUL_FIRE_FLAME.display(templocleft, 1, 0.1, 0.1, 0.1);
						ParticleEffect.SOUL_FIRE_FLAME.display(templocright, 1, 0.1, 0.1, 0.1);
					}
				}
			}
			
		}



	}

	@Override
	public double getCollisionRadius() {
		return collisionradius;
	}

	@Override
	public boolean isCollidable() {
		return true;
	}
	
	@Override
	public List<Location> getLocations() {
		if (!hasshifted) {
			return conelocs;
		}
		else {
			return walllocs;
		}
	}
	
	
	
	
	public void onClick() {
		this.remove();
	}

	public void onShift() {
		if (!hasshifted) {
			ProjectKorra.getCollisionInitializer().addLargeAbility(this);
			hasshifted = true;
			orthagonalleft = GeneralMethods.getOrthogonalVector(dir, 90, currentshiftrange).setY(0).normalize();
			orthagonalright = GeneralMethods.getOrthogonalVector(dir, 270, currentshiftrange).setY(0).normalize();
		}
	}

	@Override
	public void remove() {
		bPlayer.addCooldown(this);
		super.remove();
		return;
	}


	@Override
	public String getDescription() {
		return "By: __Yujiro\n"
				+ "Prince Zuko used this to easily counter Princess Azula's deadly strike in an Agni Kai."; 
	}

	@Override
	public String getInstructions() {
		return "Left click to enable, click again to disable."; 
	}

}

































