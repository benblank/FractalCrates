package com.five35.minecraft.fractalcrates;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayerFactory;

public class CrateDispenserBehavior extends BehaviorDefaultDispenseItem {
	@Override
	protected ItemStack dispenseStack(final IBlockSource source, final ItemStack stack) {
		final int side = BlockDispenser.getFacing(source.getBlockMetadata()).ordinal();
		final World world = source.getWorld();
		final int x = source.getXInt();
		final int y = source.getYInt();
		final int z = source.getZInt();

		((CrateItem) stack.getItem()).onItemUse(stack, FakePlayerFactory.getMinecraft(world), world, x, y, z, side, 0, 0, 0);

		return stack;
	}
}
