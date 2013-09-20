package com.five35.minecraft.fractalcrates;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CrateItem extends ItemBlock {
	public CrateItem(int itemId) {
		super(itemId);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean verbose) {
		super.addInformation(stack, player, infoList, verbose);

		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Contents")) {
			ItemStack contents = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("Contents"));
			Item item = contents.getItem();
			String info;

			if (item instanceof CrateItem) {
				info = I18n.getString(contents.stackSize > 1 ? "tooltip.cratesOf" : "tooltip.crateOf");
				info = String.format(I18n.getString("tooltip.contents"), Integer.valueOf(contents.stackSize), info);
				info = String.format(I18n.getString("tooltip.contains"), info);

				infoList.add(info);

				while (item instanceof CrateItem && contents.stackTagCompound != null && contents.stackTagCompound.hasKey("Contents")) {
					info = I18n.getString(contents.stackSize > 1 ? "tooltip.cratesOf" : "tooltip.crateOf");
					info = String.format(I18n.getString("tooltip.contents"), Integer.valueOf(contents.stackSize), info);

					infoList.add(info);

					contents = ItemStack.loadItemStackFromNBT(contents.stackTagCompound.getCompoundTag("Contents"));
					item = contents.getItem();
				}

				infoList.add(String.format(I18n.getString("tooltip.contents"), Integer.valueOf(contents.stackSize), contents.getDisplayName()));
			} else {
				info = String.format(I18n.getString("tooltip.contents"), Integer.valueOf(contents.stackSize), contents.getDisplayName());
				info = String.format(I18n.getString("tooltip.contains"), info);

				infoList.add(info);
			}
		}
	}

	@Override
	public String getItemDisplayName(ItemStack stack) {
		if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("Contents")) {
			final ItemStack contents = ItemStack.loadItemStackFromNBT(stack.stackTagCompound.getCompoundTag("Contents"));

			return String.format(I18n.getString("item.crateOf"), contents.getDisplayName());
		}

		return super.getItemDisplayName(stack);
	}
}
