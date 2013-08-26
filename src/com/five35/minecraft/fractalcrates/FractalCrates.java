package com.five35.minecraft.fractalcrates;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "FractalCrates")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class FractalCrates {
	static final int CRATE_GUI_ID = 0;

	@Instance
	public static FractalCrates instance;

	@SidedProxy(clientSide = "com.five35.minecraft.fractalcrates.ClientProxy", serverSide = "com.five35.minecraft.fractalcrates.CommonProxy")
	public static CommonProxy proxy;

	static Configuration config;
	static int crateId;

	public static Crate crate;
	public static CrateRenderer crateRenderer;

	@EventHandler
	public static void init(@SuppressWarnings("unused") final FMLInitializationEvent event) {
		FractalCrates.crate = new Crate(FractalCrates.crateId);
		FractalCrates.crateRenderer = new CrateRenderer(RenderingRegistry.getNextAvailableRenderId());

		GameRegistry.registerBlock(FractalCrates.crate, FractalCrates.crate.getUnlocalizedName());
		GameRegistry.registerTileEntity(CrateTileEntity.class, "fractalCrate");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(FractalCrates.crate), new String[] { "x x", "x x", "xxx" }, Character.valueOf('x'), "plankWood"));

		NetworkRegistry.instance().registerGuiHandler(FractalCrates.instance, FractalCrates.proxy);

		RenderingRegistry.registerBlockHandler(FractalCrates.crateRenderer);
	}

	@EventHandler
	public static void preInit(final FMLPreInitializationEvent event) {
		FractalCrates.config = new Configuration(event.getSuggestedConfigurationFile());
		FractalCrates.config.load();

		FractalCrates.crateId = FractalCrates.config.getBlock("crate", 2083).getInt();

		FractalCrates.config.save(); // if config file was missing, this will write the defaults
	}
}
