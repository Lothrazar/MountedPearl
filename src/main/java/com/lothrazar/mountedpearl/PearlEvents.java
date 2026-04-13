package com.lothrazar.mountedpearl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * TODO we could do AttachmentTypes something like
 * public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
 *     DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
 *
 * public static final Supplier<AttachmentType<RideData>> RIDE_DATA =
 *     ATTACHMENT_TYPES.register("ride_data", () ->
 *         AttachmentType.builder(() -> new RideData())
 *             .serialize(RideData.CODEC)  // optional: only if you want it to save to disk
 *             .build()
 *     );
 */
public class PearlEvents {

  // now namespaced in 1.21+
  public static final String NBT_VEHICLE_ENTITY = ModMountedPearl.MODID + ":ride";
  // i think zero could be a valid id
  public static final int EMPTY = -1;

  // Sync the player's current mount every tick so we can read it in the pearl event
  @SubscribeEvent
  public void onEntityUpdate(EntityTickEvent.Post event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    if (player.level().isClientSide) {
      return;
    }
    Entity vehicle = player.getVehicle();
    if (vehicle != null) {
      player.getPersistentData().putInt(NBT_VEHICLE_ENTITY, vehicle.getId());
    } else {
      player.getPersistentData().putInt(NBT_VEHICLE_ENTITY, EMPTY);
    }
  }

  // By the time this fires, the player has already teleported
  //its now a post event. in 1.20 and previous this fired pre, so vehicle was nonnull
  // look up the saved vehicle,
  // teleport it to the player's destination, and re-startRiding
  @SubscribeEvent
  public void onEnderTeleportEvent(EntityTeleportEvent.EnderPearl event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    if (player.level().isClientSide) {
      // do not spawn a second 'ghost' one on client side
      return;
    }
    int savedId = player.getPersistentData().getInt(NBT_VEHICLE_ENTITY);
    if (savedId > 0) {
      Entity horse = player.level().getEntity(savedId);
      if (horse != null) {
        horse.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        // TODO: we need a delay between these two things
        player.startRiding(horse, true);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
    event.getEntity().getPersistentData().putInt(NBT_VEHICLE_ENTITY, EMPTY);
  }
}
