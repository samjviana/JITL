package net.jitl.client.essence;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketEssenceBar {

    private int essence, maxEssence;

    public PacketEssenceBar(ByteBuf buf) {
        essence = buf.readInt();
        maxEssence = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(essence);
        buf.writeInt(maxEssence);
    }

    public PacketEssenceBar(PlayerEssence essence) {
        if(essence == null)
            return;
        this.essence = essence.getEssence();
        this.maxEssence = essence.getEssence();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEssence essence = ctx.get().getSender().getCapability(PlayerEssenceProvider.PLAYER_ESSENCE).orElseThrow(null);
            essence.setEssence(ctx.get().getSender(), this.essence);
        });
        ctx.get().setPacketHandled(true);
    }
}