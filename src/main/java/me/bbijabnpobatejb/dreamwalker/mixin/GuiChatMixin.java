package me.bbijabnpobatejb.dreamwalker.mixin;


import me.bbijabnpobatejb.dreamwalker.alias.AliasHandler;
import me.bbijabnpobatejb.dreamwalker.cube.RollHandler;
import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
