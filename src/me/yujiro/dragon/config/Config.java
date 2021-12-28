package me.yujiro.dragon.config;



import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.configuration.ConfigManager;

import me.yujiro.dragon.Dragon;




public class Config {

    private static ConfigFile main;
    static Dragon plugin;

    public Config(Dragon plugin) {
        Config.plugin = plugin;
        main = new ConfigFile("config");
        loadConfig();
    }

    public static FileConfiguration getConfig() {
        return main.getConfig();
    }

    public void loadConfig() {
        FileConfiguration config = Dragon.plugin.getConfig();
        FileConfiguration rankConfig = ConfigManager.languageConfig.get();
        FileConfiguration langConfig = config;
        
        //Ability configuration
        
		config.addDefault("Dragon.DragonsBreath.Cooldown", Long.valueOf(5000));
		config.addDefault("Dragon.DragonsBreath.ChargeTime", Long.valueOf(1000));
		config.addDefault("Dragon.DragonsBreath.Duration", Long.valueOf(10000));
		config.addDefault("Dragon.DragonsBreath.Damage", Double.valueOf(0.5));
		config.addDefault("Dragon.DragonsBreath.Range", Double.valueOf(8.0));
		config.addDefault("Dragon.DragonsBreath.FireHelixes", Integer.valueOf(2));
	
		
		config.addDefault("Dragon.DragonsJet.Cooldown", Long.valueOf(10000));
		
		config.addDefault("Dragon.DragonsJet.ManaTotal", Double.valueOf(80.0));
		config.addDefault("Dragon.DragonsJet.ManaRegenPerSecond", Double.valueOf(2.0));
		config.addDefault("Dragon.DragonsJet.ManaUseHoverPerSecond", Double.valueOf(2.0));
		config.addDefault("Dragon.DragonsJet.ManaUseLeap", Double.valueOf(5.0));
		config.addDefault("Dragon.DragonsJet.ManaUseSlowFlyPerSecond", Double.valueOf(4.0));
		config.addDefault("Dragon.DragonsJet.ManaUseFastFlyPerSecond", Double.valueOf(7.0));
		
		config.addDefault("Dragon.DragonsJet.LeapSpeed", Double.valueOf(4.0));
		config.addDefault("Dragon.DragonsJet.SlowFlySpeed", Double.valueOf(1.0));
		config.addDefault("Dragon.DragonsJet.FastFlySpeed", Double.valueOf(2.0));
		
		
		
		config.addDefault("Dragon.DragonsComet.Cooldown", Long.valueOf(5000));
		config.addDefault("Dragon.DragonsComet.ChargeTime", Long.valueOf(2000));
		
		config.addDefault("Dragon.DragonsComet.CometRadius", Double.valueOf(3.0));
		config.addDefault("Dragon.DragonsComet.ChargeRadius", Double.valueOf(5.0));
		config.addDefault("Dragon.DragonsComet.HitboxRadius", Double.valueOf(3.5));
		
		config.addDefault("Dragon.DragonsComet.Speed", Double.valueOf(1.5));
		config.addDefault("Dragon.DragonsComet.Damage", Double.valueOf(2.5));
		config.addDefault("Dragon.DragonsComet.Knockback", Double.valueOf(2.0));
		config.addDefault("Dragon.DragonsComet.Range", Double.valueOf(40.0));
		
		

		config.addDefault("Dragon.DragonsScales.Cooldown", Long.valueOf(2000));
		config.addDefault("Dragon.DragonsScales.Duration", Long.valueOf(3000));
		
		config.addDefault("Dragon.DragonsScales.ClickRadius", Double.valueOf(2.0));
		config.addDefault("Dragon.DragonsScales.ShiftRange", Double.valueOf(10.0));
		config.addDefault("Dragon.DragonsScales.ShiftLength", Double.valueOf(6.0));
		config.addDefault("Dragon.DragonsScales.ShiftGrowSpeed", Double.valueOf(1.0));
		config.addDefault("Dragon.DragonsScales.ShiftHeight", Double.valueOf(4.0));
		config.addDefault("Dragon.DragonsScales.CollisionRadius", Double.valueOf(0.2));
		
		
		
		config.addDefault("Dragon.DragonsSlam.Cooldown", Long.valueOf(10000));
		
		config.addDefault("Dragon.DragonsSlam.MaxRadius", Double.valueOf(15.0));
		
		config.addDefault("Dragon.DragonsSlam.Speed", Double.valueOf(1));
		config.addDefault("Dragon.DragonsSlam.AngleIncrement", Double.valueOf(50));
		config.addDefault("Dragon.DragonsSlam.Damage", Double.valueOf(2.5));
		config.addDefault("Dragon.DragonsSlam.JumpHeight", Double.valueOf(1.0));
		config.addDefault("Dragon.DragonsSlam.Hitbox", Double.valueOf(1.0));


		config.addDefault("Dragon.DragonsBolt.Cooldown", Long.valueOf(5000));
		config.addDefault("Dragon.DragonsBolt.Speed", Double.valueOf(1));
		config.addDefault("Dragon.DragonsBolt.Damage", Double.valueOf(1.5));
		config.addDefault("Dragon.DragonsBolt.Range", Double.valueOf(30.0));
		config.addDefault("Dragon.DragonsBolt.Hitbox", Double.valueOf(1.5));
		
		
        ConfigManager.languageConfig.save();
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}




