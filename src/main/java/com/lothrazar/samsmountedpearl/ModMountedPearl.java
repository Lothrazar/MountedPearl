package com.lothrazar.samsmountedpearl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ModMountedPearl.MODID, useMetadata = true, updateJSON = "https://raw.githubusercontent.com/LothrazarMinecraftMods/MountedPearl/master/update.json")
public class ModMountedPearl{

	public static final String MODID = "samsmountedpearl";
	@Instance(value = MODID)
	public static ModMountedPearl instance;
	public static final String NBT_RIDING_ENTITY = "ride";

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event){

		MinecraftForge.EVENT_BUS.register(instance);
	}

	@SubscribeEvent
	public void onEnderTeleportEvent(EnderTeleportEvent event){

		Entity ent = event.getEntity();
		if(ent instanceof EntityLiving == false){
			return;
		}
		EntityLivingBase living = (EntityLivingBase) event.getEntity();
		if(living == null){
			return;
		}

		if(living.worldObj.isRemote == false)// do not spawn a second 'ghost' one on client side
		{
			if(living.getRidingEntity() != null && living instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer) living;

				player.getEntityData().setInteger(NBT_RIDING_ENTITY, player.getRidingEntity().getEntityId());

				player.getRidingEntity().setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
			}
		}
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event){

		Entity ent = event.getEntity();
		if(ent instanceof EntityLiving == false){
			return;
		}
		EntityLivingBase living = (EntityLivingBase) event.getEntity();
		if(living == null){
			return;
		}

		if(living instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) living;
			if(player.getEntityData() == null){
				return;
			}
			int setride = player.getEntityData().getInteger(NBT_RIDING_ENTITY);

			if(setride > 0 && player.getRidingEntity() == null){
				Entity horse = player.worldObj.getEntityByID(setride);

				if(horse != null){
					player.startRiding(horse, true);
					player.getEntityData().setInteger(NBT_RIDING_ENTITY, -1);
				}
			}
		}
	}
}
