package me.yujiro.dragon.abilities.combos;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

import me.yujiro.dragon.Dragon;
import me.yujiro.dragon.Methods;


public class DragonsSlam extends FireAbility implements ComboAbility{

	//Config variables
	private long cooldown;
	private double maxradius, speed, damage, jumpheight, hitbox, angleincrement;

	//Set variables
	private Boolean hasbluefire, hastouchedground;
	private Location origin;
	private double currentradius;
	private double angle;

	public DragonsSlam(Player player) {
		super(player);

		if (CoreAbility.hasAbility(player, this.getClass()) || bPlayer.isOnCooldown(this) || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			return;
		}

		setFields();
		start();

	}

	public void setFields() {

		//Config
		this.cooldown = Dragon.plugin.getConfig().getLong("Dragon.DragonsSlam.Cooldown");

		this.maxradius = Dragon.plugin.getConfig().getDouble("Dragon.DragonsSlam.MaxRadius");
		this.speed = Dragon.plugin.getConfig().getDouble("Dragon.DragonsSlam.Speed");
		this.angleincrement = Dragon.plugin.getConfig().getDouble("Dragon.DragonsSlam.AngleIncrement");
		this.damage = Dragon.plugin.getConfig().getDouble("Dragon.DragonsSlam.Damage");
		this.jumpheight = Dragon.plugin.getConfig().getDouble("Dragon.DragonsSlam.JumpHeight");
		this.hitbox = Dragon.plugin.getConfig().getDouble("Dragon.DragonsSlam.Hitbox");

		//Set variables

		this.hasbluefire = bPlayer.hasElement(Element.BLUE_FIRE);
		this.hastouchedground = false;
		this.currentradius = 0;

		player.setVelocity(new Vector(0,jumpheight,0));
		this.angle = 0;
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return origin;
	}

	@Override
	public String getName() {
		return "DragonsSlam";
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

		if (!hastouchedground) {
			player.setFallDistance(0);
			Location ploc = player.getLocation().clone().subtract(0,0.5,0);
			Methods.playFireBall(GeneralMethods.getRightSide(ploc, 0.2), 0.5, 4, 1, hasbluefire);
			Methods.playFireBall(GeneralMethods.getLeftSide(ploc, 0.2), 0.5, 4, 1, hasbluefire);
		}

		if (!hastouchedground && player.isOnGround()) {
			bPlayer.addCooldown(this);
			hastouchedground = true;
			origin = player.getLocation().add(0,1,0);
			Methods.playExplosion(origin, maxradius/3, 0, false);
		}

		if (hastouchedground) {
			currentradius+=speed;
			angle+=angleincrement;
			
			if (currentradius > maxradius) {
				this.remove();
			}

			Vector zpositive = new Vector(0,0,currentradius);
			for (double theta = 0; theta < 360; theta += 5) {
				Location temploc = origin.clone().add(zpositive.clone().rotateAroundY(Math.toRadians(theta)));
				temploc.add(0, Math.sin(Math.toRadians(angle)), 0);
				
				if (!hasbluefire) {
					ParticleEffect.FLAME.display(temploc, 1, 0.2, 0, 0.2);
				}
				else {
					ParticleEffect.SOUL_FIRE_FLAME.display(temploc, 1, 0.2, 0, 0.2);
				}
				
				if (Methods.getAffected(temploc, hitbox, player) != null) {
					DamageHandler.damageEntity(Methods.getAffected(temploc, hitbox, player), damage, this);
					this.remove();
				}
			}
		}


	}


	@Override
	public Object createNewComboInstance(Player player) {
		return new DragonsSlam(player);
	}

	@Override
	public ArrayList<AbilityInformation> getCombination() {
		final ArrayList<AbilityInformation> combo = new ArrayList<>();
		combo.add(new AbilityInformation("DragonsComet", ClickType.RIGHT_CLICK_BLOCK));
		combo.add(new AbilityInformation("DragonsScales", ClickType.SHIFT_DOWN));
		combo.add(new AbilityInformation("DragonsScales", ClickType.SHIFT_UP));
		return combo;
	}

	@Override
	public void remove() {
		super.remove();
		return;
	}


	@Override
	public String getDescription() {
		return "By: __Yujiro\n"
				+ "Fire Lord Ozai used this in his fight with Avatar Aang."; 
	}

	@Override
	public String getInstructions() {
		return "DragonsComet <Right Click a block> >> DragonsScales <Tap Shift>."; 
	}
}
