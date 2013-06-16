package biomesoplenty.handlers;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import biomesoplenty.api.Blocks;
import biomesoplenty.api.Items;
import biomesoplenty.configuration.BOPConfiguration;
import biomesoplenty.entities.ai.EntityAITemptArmour;
import biomesoplenty.helpers.TeleporterPromised;

public class EntityEventHandler
{
	@ForgeSubscribe
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		Entity entity = event.entity;

		if (!(entity instanceof EntityLiving))
			return;

		if (entity instanceof EntityChicken) {
			((EntityLiving)entity).tasks.addTask(3, new EntityAITemptArmour((EntityCreature) entity, 0.25F, Items.flowerBand.get().itemID, 0, false));
		}

		if (entity instanceof EntitySheep) {
			((EntityLiving)entity).tasks.addTask(3, new EntityAITemptArmour((EntityCreature) entity, 0.25F, Items.flowerBand.get().itemID, 1, false));
		}

		if (entity instanceof EntityPig) {
			((EntityLiving)entity).tasks.addTask(4, new EntityAITemptArmour((EntityCreature) entity, 0.25F, Items.flowerBand.get().itemID, 2, false));
		}

		if (entity instanceof EntityCow) {
			((EntityLiving)entity).tasks.addTask(3, new EntityAITemptArmour((EntityCreature) entity, 0.25F, Items.flowerBand.get().itemID, 3, false));
		}
	}

	@ForgeSubscribe
	public void canEntitySpawn(CheckSpawn event)
	{
		if (event.entityLiving instanceof EntityAnimal)
		{
			int i = MathHelper.floor_double(event.entityLiving.posX);
			int j = MathHelper.floor_double(event.entityLiving.boundingBox.minY);
			int k = MathHelper.floor_double(event.entityLiving.posZ);

			if (event.entityLiving.getMaxSpawnedInChunk() <= event.world.getEntitiesWithinAABB(event.entityLiving.getClass(), AxisAlignedBB.getAABBPool().getAABB(i - 16, j - 4, k - 16, i + 17, j + 5, k + 17)).size())
				return;

			if (event.entityLiving.worldObj.getBlockId(i, j - 1, k) == Blocks.holyGrass.get().blockID && event.entityLiving.worldObj.getFullBlockLightValue(i, j, k) > 8
					&& event.entityLiving.worldObj.checkNoEntityCollision(event.entity.boundingBox) && !event.entityLiving.worldObj.isAnyLiquid(event.entityLiving.boundingBox)
					&& event.entityLiving.worldObj.getCollidingBoundingBoxes(event.entityLiving, event.entityLiving.boundingBox).isEmpty()) {
				event.setResult(Result.ALLOW);
			}
		}
	}

	@ForgeSubscribe
	public void fallingFromPromisedLand(LivingHurtEvent event)
	{
		if (event.source == DamageSource.outOfWorld && event.entityLiving.dimension == BOPConfiguration.promisedLandDimID)
		{
			event.setCanceled(true);

			if (!event.entityLiving.worldObj.isRemote && !event.entityLiving.isDead)
			{
				if (event.entityLiving instanceof EntityPlayerMP)
				{
					EntityPlayerMP thePlayer = (EntityPlayerMP) event.entityLiving;
					thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 0, new TeleporterPromised(thePlayer.mcServer.worldServerForDimension(0), true));
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void lightningStrike(LivingHurtEvent event)
	{
		AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB((double)event.entity.posX, (double)event.entity.posY, (double)event.entity.posZ, (double)(event.entity.posX + 1), (double)(event.entity.posY + 1), (double)(event.entity.posZ + 1)).expand(5, 5, 5);

		if (!event.entity.worldObj.getEntitiesWithinAABB(EntityLightningBolt.class, axisalignedbb).isEmpty());
		{
			if (!event.entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb).isEmpty());
			{
				if (isBlockInBB(event.entity.worldObj, axisalignedbb, Blocks.glass.get().blockID, 2) || isBlockInBB(event.entity.worldObj, axisalignedbb, Blocks.glass.get().blockID, 3))
				{           	
					event.setCanceled(true);
				}
			}
		}
	}
	
    public boolean isBlockInBB(World world, AxisAlignedBB par1AxisAlignedBB, int blockID, int blockMeta)
    {
        int i = MathHelper.floor_double(par1AxisAlignedBB.minX);
        int j = MathHelper.floor_double(par1AxisAlignedBB.maxX + 1.0D);
        int k = MathHelper.floor_double(par1AxisAlignedBB.minY);
        int l = MathHelper.floor_double(par1AxisAlignedBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(par1AxisAlignedBB.minZ);
        int j1 = MathHelper.floor_double(par1AxisAlignedBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1)
        {
            for (int l1 = k; l1 < l; ++l1)
            {
                for (int i2 = i1; i2 < j1; ++i2)
                {
                    Block block = Block.blocksList[world.getBlockId(k1, l1, i2)];

                    if (block != null && block.blockID == blockID && world.getBlockMetadata(k1, l1, i2) == blockMeta)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}