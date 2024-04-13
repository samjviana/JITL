package net.jitl.client.music;

import net.jitl.core.helper.EnumStructureMusic;
import net.jitl.core.init.JITL;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = JITL.MODID, value = Dist.CLIENT)
public class StructureMusicHandler {
    public static EnumStructureMusic currentMusic;

    @SubscribeEvent
    public static void structureMusicTick(TickEvent.ClientTickEvent event) {
        if (currentMusic != null) {
            JMusicTicker.addTrack(currentMusic.getMusicForStructure());
        }
    }
}
