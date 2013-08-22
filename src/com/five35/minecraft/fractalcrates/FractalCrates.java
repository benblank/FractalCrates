package com.five35.minecraft.fractalcrates;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod.Instance;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;

@Mod(modid = "FractalCrates")
public class FractalCrates {
	@Instance
	public static FractalCrates instance;

	static Configuration config;

	@EventHandler
	public static void preInit(final FMLPreInitializationEvent event) {
		FractalCrates.config = new Configuration(event.getSuggestedConfigurationFile());
		FractalCrates.config.load();

		// config options go here

		FractalCrates.config.save(); // if config file was missing, this will write the defaults
	}

	@EventHandler
	public void init(@SuppressWarnings("unused") final FMLInitializationEvent event) {
	}
}
