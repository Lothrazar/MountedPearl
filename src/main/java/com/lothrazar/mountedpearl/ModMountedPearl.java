package com.lothrazar.mountedpearl;

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

@Mod(modid = ModMountedPearl.MODID, useMetadata = true, updateJSON = "https://raw.githubusercontent.com/Lothrazar/MountedPearl/trunk/1.10/update.json")
public class ModMountedPearl {

	public static final String MODID = "mountedpearl";
	@Instance(value = MODID)
	public static ModMountedPearl instance;
	public static final String NBT_RIDING_ENTITY = MODID + "ride";
	public static final String NBT_RIDING_TIMER = MODID + "ridetimer";

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(instance);
	}

	@SubscribeEvent
	public void onEnderTeleportEvent(EnderTeleportEvent event) {

		Entity ent = event.getEntity();

		if (ent instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) ent;

			if (player.getRidingEntity() == null) {
				return;
			}

			player.getEntityData().setInteger(NBT_RIDING_ENTITY, player.getRidingEntity().getEntityId());
			player.dismountRidingEntity();
			player.getEntityData().setInteger(NBT_RIDING_TIMER, 3);
			player.getEntityData().setDouble("mpx", event.getTargetX());
			player.getEntityData().setDouble("mpy", event.getTargetY());
			player.getEntityData().setDouble("mpz", event.getTargetZ());
			event.setAttackDamage(0);
		}

	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {

		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if (player.getEntityData() == null) {
				return;
			}
			int setride = player.getEntityData().getInteger(NBT_RIDING_ENTITY);
			int timer = player.getEntityData().getInteger(NBT_RIDING_TIMER);

			if (setride > 0) {// && player.getRidingEntity() == null
				Entity horse = player.worldObj.getEntityByID(setride);
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
