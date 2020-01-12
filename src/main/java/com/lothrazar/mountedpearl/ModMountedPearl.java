package com.lothrazar.mountedpearl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid=ModMountedPearl.MODID, acceptableRemoteVersions="*")
public class ModMountedPearl {

  public static final String MODID = "mountedpearl";
  public static final String NBT_RIDING_ENTITY = "ride";
  public static final String NBT_RIDING_TIMER = "ridetimer";

  public ModMountedPearl() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onEnderTeleportEvent(EnderTeleportEvent event) {
    Entity ent = event.getEntity();
    if (ent instanceof EntityLivingBase == false) {
      return;
    }
    EntityLivingBase living = (EntityLivingBase) event.getEntity();
    if (living == null) {
      return;
    }
    if (living.world.isRemote == false) {// do not spawn a second 'ghost' one on client side
      if (living.getRidingEntity() != null && living instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) living;
        player.getEntityData().setInteger(NBT_RIDING_ENTITY, player.getRidingEntity().getEntityId());
//        player.stopRiding(); 
        //
        //
        //
        player.dismountRidingEntity();
        player.getEntityData().setInteger(NBT_RIDING_TIMER, 3);
        //                player.getRidingEntity().setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        player.getEntityData().setDouble("mpx", event.getTargetX());
        player.getEntityData().setDouble("mpy", event.getTargetY());
        player.getEntityData().setDouble("mpz", event.getTargetZ());
        event.setAttackDamage(0);
      }
    }
  }

  @SubscribeEvent
  public void onEntityUpdate(LivingUpdateEvent event) {
    Entity ent = event.getEntity();
    if (ent instanceof EntityLivingBase == false) {
      return;
    }
    EntityLivingBase living = (EntityLivingBase) event.getEntity();
    if (living == null) {
      return;
    }
    if (living instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) living;
      if (player.getEntityData() == null) {
        return;
      }
      int setride = player.getEntityData().getInteger(NBT_RIDING_ENTITY);
      int timer = player.getEntityData().getInteger(NBT_RIDING_TIMER);
      if (setride > 0) {// && player.getRidingEntity() == null
        Entity horse = player.world.getEntityByID(setride);
        if (horse != null) {
          if (timer > 0) {
            player.getEntityData().setInteger(NBT_RIDING_TIMER, timer - 1);
            double x = player.getEntityData().getDouble("mpx");
            double y = player.getEntityData().getDouble("mpy");
            double z = player.getEntityData().getDouble("mpz");
            horse.setPositionAndUpdate(x, y, z);
            return;
          }
          player.startRiding(horse, true);
          player.getEntityData().setInteger(NBT_RIDING_ENTITY, -1);
        }
      }
    }
  }
}
