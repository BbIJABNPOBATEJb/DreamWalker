package me.bbijabnpobatejb.dreamwalker.mixin;

import net.minecraft.network.play.client.C01PacketChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static me.bbijabnpobatejb.dreamwalker.DreamWalker.MAX_CHAT_CHAR;

@Mixin(C01PacketChatMessage.class)
public abstract class C01PacketChatMessageMixin {

    @ModifyConstant(
            method = "<init>(Ljava/lang/String;)V",
            constant = @Constant(intValue = 100)
    )
    private int modifyMaxLengthInConstructor(int original) {
        return MAX_CHAT_CHAR;
    }

    @ModifyConstant(
            method = "readPacketData",
            constant = @Constant(intValue = 100)
    )
    private int modifyMaxLengthInReader(int original) {
        return MAX_CHAT_CHAR;
    }
}
