package biomesoplenty.itemblocks;

import net.minecraft.item.ItemBlock;

public class ItemBlockGrave extends ItemBlock
{
	public ItemBlockGrave(int par1)
	{
		super(par1);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int meta)
	{
		return meta & 15;
	}
}
