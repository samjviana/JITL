package net.jitl.common.entity.base;

import net.jitl.common.entity.IJourneyBoss;
import net.jitl.common.entity.boss.BossCrystal;
import net.jitl.core.init.internal.JEntities;
import net.jitl.core.init.internal.JSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public abstract class JBossEntity extends AnimatableMonster implements IJourneyBoss, IDontAttackWhenPeaceful{

    private static final EntityDataAccessor<Boolean> HAS_SPAWNED = SynchedEntityData.defineId(JBossEntity.class, EntityDataSerializers.BOOLEAN);

    protected JBossEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SPAWNED, false);
    }

    public void setHasSpawned() {
        this.entityData.set(HAS_SPAWNED, true);
    }

    public boolean hasSpawned() {
        return this.entityData.get(HAS_SPAWNED);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity living) {
        return level.getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return JSounds.BOSS_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 10.0F;
    }

    protected abstract BossCrystal.Type getDeathCrystalType();
    public abstract ResourceLocation lootTable();
    public abstract ServerBossEvent getEvent();

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        JBossInfo.removeInfo(player, getEvent(), this);
    }

    @Override
    public void tick() {
        super.tick();
        int playerArea = 10;
        AABB axisalignedbb = AABB.unitCubeFromLowerCorner(this.position()).inflate(playerArea);
        for(Player player : this.level.getEntitiesOfClass(Player.class, axisalignedbb)) {
            if(player instanceof ServerPlayer p) {
                if(!level.isClientSide())
                    JBossInfo.addInfo(p, getEvent(), this);
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource s) {
        super.die(s);
        if(!level.isClientSide()) {
            if(!hasSpawned()) {
                if(s.getEntity() instanceof Player p) {
                    JBossInfo.removeInfo((ServerPlayer)p, getEvent(), this);
                }
                BossCrystal crystal = new BossCrystal(JEntities.BOSS_CRYSTAL_TYPE.get(), level, getDeathCrystalType(), lootTable());
                crystal.setPos(position().add(0, 1, 0));
                level.addFreshEntity(crystal);
                setHasSpawned();
            }
        }
    }
}
