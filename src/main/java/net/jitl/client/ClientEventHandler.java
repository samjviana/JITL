package net.jitl.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.jitl.client.render.world.*;
import net.jitl.client.util.ClientGetter;
import net.jitl.common.world.dimension.Dimensions;
import net.jitl.core.init.JITL;
import net.jitl.core.init.internal.JDataAttachments;
import net.jitl.core.init.internal.JItems;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mod.EventBusSubscriber(modid = JITL.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    public static void regToBus(IEventBus forgeBus) {
        forgeBus.addListener(ClientEventHandler::onFogDensityEvent);
    }

    public static void onFogDensityEvent(ViewportEvent.RenderFog event) {
        float farPlaneDistance = event.getFarPlaneDistance();
        Player player = ClientGetter.player();
        if (ClientGetter.level().dimension() == Dimensions.FROZEN_LANDS) {
                float density;
                ICuriosItemHandler curios = CuriosApi.getCuriosInventory(player).get();
                if(player.getData(JDataAttachments.PLAYER_STATS).hasBlizzard() || curios.findFirstCurio(JItems.EYE_OF_THE_BLIZZARD.get()).isPresent()) {
                    density = 0.55F;
                } else {
                    density = 0.1F;
                }
                RenderSystem.setShaderFogStart(density);
                RenderSystem.setShaderFogEnd(density * farPlaneDistance);
        }

        if(ClientGetter.level().dimension() == Dimensions.CLOUDIA) {
            float density = 0.35F;
            RenderSystem.setShaderFogStart(density);
            RenderSystem.setShaderFogEnd(density * farPlaneDistance);
        }

        if(ClientGetter.level().dimension() == Dimensions.DEPTHS) {
            float density = 0.5F;
            RenderSystem.setShaderFogStart(density);
            RenderSystem.setShaderFogEnd(density * farPlaneDistance);
        }
    }

    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(Dimensions.EUCA_EFFECTS, new EucaRenderInfo());
        event.register(Dimensions.BOIL_EFFECTS, new BoilRenderInfo());
        event.register(Dimensions.FROZEN_EFFECTS, new FrozenRenderInfo());
        event.register(Dimensions.TERRANIA_EFFECTS, new TerraniaRenderInfo());
        event.register(Dimensions.CLOUDIA_EFFECTS, new CloudiaRenderInfo());
    }
}