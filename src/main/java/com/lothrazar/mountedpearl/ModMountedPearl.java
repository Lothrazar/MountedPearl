package com.lothrazar.mountedpearl;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ModMountedPearl.MODID)
public class ModMountedPearl {

  public static final String MODID = "mountedpearl";

  public ModMountedPearl() {
    NeoForge.EVENT_BUS.register(new PearlEvents());
  }
}
