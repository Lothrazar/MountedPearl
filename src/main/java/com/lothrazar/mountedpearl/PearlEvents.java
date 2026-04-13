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
  // horse id queued for remounting after pearl teleport
  public static final String NBT_PENDING_MOUNT = ModMountedPearl.MODID + ":pending_mount";
  // countdown ticks before startRiding is called, horse teleport is async
  public static final String NBT_PENDING_TIMER = ModMountedPearl.MODID + ":pending_timer";
  // up from when it used to be 3 in 1.20 and below
  public static final int MOUNT_DELAY_TICKS = 5;
  // i think zero could be a valid id
  public static final int EMPTY = -1;

  // Sync the player's current mount every tick so we can read it in the pearl event.
  // Also consume any pending mount queued by the pearl event.
  @SubscribeEvent
  public void onEntityUpdate(EntityTickEvent.Post event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    if (player.level().isClientSide) {
      return;
    }
    // horse.teleportTo() from the pearl event done
    asyncVehicleRiding(player);
    // sync current vehicle for use in the pearl event
    syncCurrentVehicle(player);
  }

  // sauce!
  private static void asyncVehicleRiding(Player player) {
    int pendingId = player.getPersistentData().getInt(NBT_PENDING_MOUNT);
    if (pendingId <= EMPTY) {
      return;
    }
    int timer = player.getPersistentData().getInt(NBT_PENDING_TIMER);
    if (timer > 0) {
      player.getPersistentData().putInt(NBT_PENDING_TIMER, timer - 1);
      return;
    }
    Entity horse = player.level().getEntity(pendingId);
    if (horse != null) {
      player.startRiding(horse, true);
    }
    player.getPersistentData().putInt(NBT_PENDING_MOUNT, EMPTY);
  }

  private static void syncCurrentVehicle(Player player) {
    Entity vehicle = player.getVehicle();
    if (vehicle != null) {
      player.getPersistentData().putInt(NBT_VEHICLE_ENTITY, vehicle.getId());
    } else {
      player.getPersistentData().putInt(NBT_VEHICLE_ENTITY, EMPTY);
    }
  }

  // By the time this fires, the player has already teleported.
  // In 1.20 and previous this fired pre, so vehicle was non-null.
  // Now we read the vehicle from the last tick's saved data, teleport it,
  // and queue startRiding for the next tick so teleportTo has time to resolve.
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
    if (savedId > EMPTY) {
      Entity horse = player.level().getEntity(savedId);
      if (horse != null) {
        horse.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        player.getPersistentData().putInt(NBT_PENDING_MOUNT, savedId);
        player.getPersistentData().putInt(NBT_PENDING_TIMER, MOUNT_DELAY_TICKS);
      }
    }
  }

  @SubscribeEvent
  public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
    Player player = event.getEntity();
    player.getPersistentData().putInt(NBT_VEHICLE_ENTITY, EMPTY);
    player.getPersistentData().putInt(NBT_PENDING_MOUNT, EMPTY);
    player.getPersistentData().putInt(NBT_PENDING_TIMER, 0);
  }
}
