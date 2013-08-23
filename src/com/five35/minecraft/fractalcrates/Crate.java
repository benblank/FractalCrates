package com.five35.minecraft.fractalcrates;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Crate extends BlockContainer {
	public Crate(final int id) {
		super(id, Material.wood);

		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setHardness(2);
		this.setResistance(10);
		this.setStepSound(Block.soundWoodFootstep);
		this.setUnlocalizedName("fractalCrate");
	}

	@Override
	public TileEntity createNewTileEntity(final World world) {
		return new CrateTileEntity();
	}
}
