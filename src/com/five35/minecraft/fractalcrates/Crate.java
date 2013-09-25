package com.five35.minecraft.fractalcrates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Crate extends BlockContainer {
	private final Map<Integer, CrateTileEntity> tileEntityCache = new HashMap<Integer, CrateTileEntity>();

	private static Integer getCacheKey(final int x, final int y, final int z) {
		return Integer.valueOf(31 * (31 * (31 + x) + y) + z);
	}

	public Crate(final int id) {
		super(id, Material.wood);

		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setHardness(2);
		this.setTextureName("fractalcrates:crate");
		this.setResistance(10);
		this.setStepSound(Block.soundWoodFootstep);
		this.setUnlocalizedName("fractalCrate");
	}

	@Override
	public void breakBlock(final World world, final int x, final int y, final int z, final int blockId, final int metadata) {
		// the tile entity gets discarded before getBlockDropped
		// runs, so we need to save a reference to it

		final TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te != null && te instanceof CrateTileEntity) {
			this.tileEntityCache.put(Crate.getCacheKey(x, y, z), (CrateTileEntity) te);
		}

		super.breakBlock(world, x, y, z, blockId, metadata);
	}

	@Override
	public TileEntity createNewTileEntity(final World world) {
		return new CrateTileEntity();
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(final World world, final int x, final int y, final int z, final int metadata, final int fortune) {
		final Integer key = Crate.getCacheKey(x, y, z);

		if (this.tileEntityCache.containsKey(key)) {
			final ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

			drops.add(this.getStack(this.tileEntityCache.get(key)));
			this.tileEntityCache.remove(key);

			return drops;
		}

		return super.getBlockDropped(world, x, y, z, metadata, fortune);
	}

	@Override
	public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
		final TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te != null && te instanceof CrateTileEntity) {
			return this.getStack((CrateTileEntity) te);
		}

		return super.getPickBlock(target, world, x, y, z);
	}

	@Override
	public int getRenderType() {
		return FractalCrates.proxy.crateRendererId;
	}

	private ItemStack getStack(final CrateTileEntity te) {
		final ItemStack stack = new ItemStack(this);

		if (te.contents != null) {
			stack.stackTagCompound = new NBTTagCompound("tag"); // provide tag name so that items stack properly regardless of origin
			te.writeStack(stack.stackTagCompound);
		}

		return stack;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player, final int side, final float hitX, final float hitY, final float hitZ) {
		final TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te != null && te instanceof CrateTileEntity) {
			player.openGui(FractalCrates.instance, FractalCrates.proxy.CRATE_GUI_ID, world, x, y, z);
		}

		return true;
	}

	@Override
	public void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entity, final ItemStack stack) {
		if (stack.stackTagCompound != null) {
			final TileEntity te = world.getBlockTileEntity(x, y, z);

			if (te != null && te instanceof CrateTileEntity) {
				((CrateTileEntity) te).readStack(stack.stackTagCompound);
			}
		}

		super.onBlockPlacedBy(world, x, y, z, entity, stack);
	}

	@Override
	public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final int blockId) {
		if (world.getBlockPowerInput(x, y, z) > 0) {
			this.dropBlockAsItem(world, x, y, z, 0, 0);
			world.setBlock(x, y, z, 0);
		}
	}
}
