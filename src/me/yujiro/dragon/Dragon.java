package me.yujiro.dragon;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.CoreAbility;

import me.yujiro.dragon.config.Config;
import me.yujiro.dragon.listeners.AbilityListener;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;



public final class Dragon extends JavaPlugin {
	
    public static Dragon plugin;

    @Override
    public void onEnable() {
        plugin = this;

        new Config(this);
        CoreAbility.registerPluginAbilities(plugin, "me.yujiro.dragon.abilities");
        //CoreAbility.registerPluginAbilities(plugin, "me.yujiro.dragon.abilities.combos");
        this.registerListeners();

        plugin.getLogger().info("Successfully enabled Dragon.");
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Successfully disabled Dragon.");
    }

    public static Dragon getInstance() {
        return plugin;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
    }
}