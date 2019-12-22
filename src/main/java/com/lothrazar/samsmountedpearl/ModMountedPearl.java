package com.lothrazar.samsmountedpearl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ModMountedPearl.MODID)
public class ModMountedPearl {

  public static final String MODID = "mountedpearl";
  public static final String NBT_RIDING_ENTITY = "ride";

  public ModMountedPearl() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onEnderTeleportEvent(EnderTeleportEvent event) {
    Entity ent = event.getEntity();
    if (ent instanceof LivingEntity == false) {
      return;
    }
    LivingEntity living = (LivingEntity) event.getEntity();
    if (living == null) {
      return;
    }
    if (living.world.isRemote == false) {// do not spawn a second 'ghost' one on client side
      if (living.getRidingEntity() != null && living instanceof PlayerEntity) {
        PlayerEntity player = (PlayerEntity) living;
        player.getPersistentData().putInt(NBT_RIDING_ENTITY, player.getRidingEntity().getEntityId());
        System.out.println("setpos after teleport " + player.getRidingEntity());
        player.getRidingEntity().setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        event.setAttackDamage(0);
      }
    }
  }

  @SubscribeEvent
  public void onEntityUpdate(LivingUpdateEvent event) {
    Entity ent = event.getEntity();
    if (ent instanceof LivingEntity == false) {
      return;
    }
    LivingEntity living = (LivingEntity) event.getEntity();
    if (living == null) {
      return;
    }
    if (living instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) living;
      if (player.getPersistentData() == null) {
        return;
      }
      int setride = player.getPersistentData().getInt(NBT_RIDING_ENTITY);
      if (setride > 0 && player.getRidingEntity() == null) {
        Entity horse = player.world.getEntityByID(setride);
        if (horse != null) {
          player.startRiding(horse, true);
          player.getPersistentData().putInt(NBT_RIDING_ENTITY, -1);
        }
      }
    }
  }
}
