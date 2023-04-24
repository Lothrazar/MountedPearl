package com.lothrazar.mountedpearl;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(ModMountedPearl.MODID)
public class ModMountedPearl {

  public static final String MODID = "mountedpearl";

  public ModMountedPearl() {
    MinecraftForge.EVENT_BUS.register(new PearlEvents());
  }
}
