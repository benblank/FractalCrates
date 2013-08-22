package com.five35.minecraft.fractalcrates;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "FractalCrates")
public class FractalCrates {
	@Instance
	public static FractalCrates instance;

	static Configuration config;
	static int crateID;
	public static Crate crate;

	@EventHandler
	public static void init(@SuppressWarnings("unused") final FMLInitializationEvent event) {
		FractalCrates.crate = new Crate(FractalCrates.crateID);
		GameRegistry.registerBlock(FractalCrates.crate, FractalCrates.crate.getUnlocalizedName());

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(FractalCrates.crate), new String[] { "x x", "x x", "xxx" }, Character.valueOf('x'), "plankWood"));
	}

	@EventHandler
	public static void preInit(final FMLPreInitializationEvent event) {
		FractalCrates.config = new Configuration(event.getSuggestedConfigurationFile());
		FractalCrates.config.load();

		FractalCrates.crateID = FractalCrates.config.getBlock("crate", 2083).getInt();

		FractalCrates.config.save(); // if config file was missing, this will write the defaults
	}
}
