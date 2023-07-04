package com.lothrazar.mountedpearl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PearlEvents {

  public static final String NBT_RIDING_ENTITY = "ride";
  public static final String NBT_RIDING_TIMER = "ridetimer";

  @SubscribeEvent
  public void onEnderTeleportEvent(EntityTeleportEvent.EnderPearl event) {
    Entity ent = event.getEntity();
    if (ent instanceof LivingEntity == false) {
      return;
    }
    LivingEntity living = (LivingEntity) event.getEntity();
    if (living == null) {
      return;
    }
    if (living.level().isClientSide == false) { // do not spawn a second 'ghost' one on client side
      if (living.getVehicle() != null && living instanceof Player) {
        Player player = (Player) living;
        player.getPersistentData().putInt(NBT_RIDING_ENTITY, player.getVehicle().getId());
        player.stopRiding();
        player.getPersistentData().putInt(NBT_RIDING_TIMER, 3);
        //                player.getRidingEntity().setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        player.getPersistentData().putDouble("mpx", event.getTargetX());
        player.getPersistentData().putDouble("mpy", event.getTargetY());
        player.getPersistentData().putDouble("mpz", event.getTargetZ());
        event.setAttackDamage(0);
      }
    }
  }

  @SubscribeEvent
  public void onEntityUpdate(LivingTickEvent event) {
    Entity ent = event.getEntity();
    if (ent instanceof LivingEntity == false) {
      return;
    }
    LivingEntity living = event.getEntity();
    if (living == null) {
      return;
    }
    if (living instanceof Player) {
      Player player = (Player) living;
      if (player.getPersistentData() == null) {
        return;
      }
      int setride = player.getPersistentData().getInt(NBT_RIDING_ENTITY);
      int timer = player.getPersistentData().getInt(NBT_RIDING_TIMER);
      if (setride > 0) {
        Entity horse = player.level().getEntity(setride);
        if (horse != null) {
          if (timer > 0) {
            player.getPersistentData().putInt(NBT_RIDING_TIMER, timer - 1);
            double x = player.getPersistentData().getDouble("mpx");
            double y = player.getPersistentData().getDouble("mpy");
            double z = player.getPersistentData().getDouble("mpz");
            horse.teleportTo(x, y, z);
            return;
          }
          player.startRiding(horse, true);
          player.getPersistentData().putInt(NBT_RIDING_ENTITY, -1);
        }
      }
    }
  }
}
