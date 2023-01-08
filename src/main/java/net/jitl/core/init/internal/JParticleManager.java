package net.jitl.core.init.internal;

import net.jitl.client.render.particle.*;
import net.jitl.core.init.JITL;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = JITL.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class JParticleManager {

    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, JITL.MODID);

    public static final RegistryObject<SimpleParticleType> RED_FLAME = REGISTRY.register("red_flame", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MUD = REGISTRY.register("mud", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CONJURING = REGISTRY.register("conjuring", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ESSENCIA_LIGHTNING = REGISTRY.register("essencia_lightning", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> MINERS_PEARL = REGISTRY.register("miners_pearl", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SNOWFLAKE = REGISTRY.register("snowflake", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SULPHUR = REGISTRY.register("sulphur", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CRYSTAL_FRUIT = REGISTRY.register("crystal_fruit", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GOLDITE_FLOWER = REGISTRY.register("goldite_flower", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FLAME_POLLEN = REGISTRY.register("flame_pollen", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CAVE_VINE = REGISTRY.register("cave_vine", () -> new SimpleParticleType(false));

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.register(RED_FLAME.get(), RedFlameParticle.Factory::new);
        event.register(MUD.get(), MudParticle.Factory::new);
        event.register(CONJURING.get(), ConjuringParticle.Factory::new);
        event.register(ESSENCIA_LIGHTNING.get(), EssenciaLightningParticle.Factory::new);
        event.register(MINERS_PEARL.get(), MinersPearlParticle.Factory::new);
        event.register(SNOWFLAKE.get(), SnowflakeParticle.Provider::new);
        event.register(SULPHUR.get(), SulphurParticle.Factory::new);
        event.register(CRYSTAL_FRUIT.get(), CrystalFruitParticle.Factory::new);
        event.register(GOLDITE_FLOWER.get(), GolditeFlowerParticle.Factory::new);
        event.register(FLAME_POLLEN.get(), FlamePollenParticle.Factory::new);
    }
}
