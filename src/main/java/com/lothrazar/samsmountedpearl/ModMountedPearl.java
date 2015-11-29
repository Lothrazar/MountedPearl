package com.lothrazar.samsmountedpearl;
 
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ModMountedPearl.MODID, useMetadata=true, updateJSON = "https://raw.githubusercontent.com/LothrazarMinecraftMods/MountedPearl/master/update.json")
public class ModMountedPearl
{
    public static final String MODID = "samsmountedpearl";
	@Instance(value = MODID)
	public static ModMountedPearl instance;
    @EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{ 
  	    MinecraftForge.EVENT_BUS.register(instance); 
	}
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @SubscribeEvent
	public void onEnderTeleportEvent(EnderTeleportEvent event)
	{  
		if(event.entityLiving.worldObj.isRemote == false)//do not spawn a second 'ghost' one on client side
		{
			if(event.entityLiving.ridingEntity != null && event.entityLiving instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)event.entityLiving;
				 
				player.getEntityData().setInteger(NBT_RIDING_ENTITY, event.entityLiving.ridingEntity.getEntityId());
				
				event.entityLiving.ridingEntity.setPositionAndUpdate(event.targetX, event.targetY, event.targetZ);
	 
			}
		}
	}
	public static final String NBT_RIDING_ENTITY = "ride";
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) 
	{  
		if(event.entityLiving == null){return;}
		
		if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			
			int setride = player.getEntityData().getInteger(NBT_RIDING_ENTITY);
			
			if(setride > 0 && event.entityLiving.ridingEntity == null)
			{ 
				Entity horse = event.entityLiving.worldObj.getEntityByID(setride);
				 
				if(horse != null)
				{
					event.entityLiving.mountEntity(horse);
					player.getEntityData().setInteger(NBT_RIDING_ENTITY, -1);
				}
			}
		}
	}
}
