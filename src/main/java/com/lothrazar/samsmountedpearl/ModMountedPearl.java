package com.lothrazar.samsmountedpearl;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

@Mod(modid = ModMountedPearl.MODID, useMetadata = true)
public class ModMountedPearl {

  public static final String MODID = "samsmountedpearl";
  @Instance(value = MODID)
  public static ModMountedPearl instance;
  public static final String NBT_RIDING_ENTITY = "ride";

  @EventHandler
  public void onPreInit(FMLPreInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(instance);
  }

  @SubscribeEvent
  public void onEnderTeleportEvent(EnderTeleportEvent event) {
    if (event.entityLiving.worldObj.isRemote == false)//do not spawn a second 'ghost' one on client side
    {
      if (event.entityLiving.ridingEntity != null && event.entityLiving instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        player.getEntityData().setInteger(NBT_RIDING_ENTITY, event.entityLiving.ridingEntity.getEntityId());
        event.entityLiving.ridingEntity.setPosition(event.targetX, event.targetY, event.targetZ);
      }
    }
  }

  @SubscribeEvent
  public void onEntityUpdate(LivingUpdateEvent event) {
    if (event.entityLiving == null) {
      return;
    }
    if (event.entityLiving instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) event.entityLiving;
      int setride = player.getEntityData().getInteger(NBT_RIDING_ENTITY);
      if (setride > 0 && event.entityLiving.ridingEntity == null) {
        Entity horse = event.entityLiving.worldObj.getEntityByID(setride);
        if (horse != null) {
          event.entityLiving.mountEntity(horse);
          player.getEntityData().setInteger(NBT_RIDING_ENTITY, -1);
        }
      }
    }
  }
}
