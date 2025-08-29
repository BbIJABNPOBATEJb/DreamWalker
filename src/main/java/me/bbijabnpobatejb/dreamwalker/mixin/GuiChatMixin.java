package me.bbijabnpobatejb.dreamwalker.mixin;


import me.bbijabnpobatejb.dreamwalker.alias.AliasHandler;
import me.bbijabnpobatejb.dreamwalker.cube.RollHandler;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.bbijabnpobatejb.dreamwalker.DreamWalker.MAX_CHAT_CHAR;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin {

    @Inject(method = "submitChatMessage", at = @At("HEAD"), cancellable = true)
    public void onSendChatMessage(String message, CallbackInfo ci) {
        try {
            if (RollHandler.handleSubmitChatMessage(message)) {
                ci.cancel();
            }
            if (AliasHandler.handleSubmitChatMessage(message)) {
                ci.cancel();
            }
        } catch (Exception ignored) {

        }
    }
    @ModifyArg(
            method = "initGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiTextField;setMaxStringLength(I)V"
            ),
            index = 0
    )
    private int modifyMaxStringLength(int original) {
        return MAX_CHAT_CHAR;
    }
}
