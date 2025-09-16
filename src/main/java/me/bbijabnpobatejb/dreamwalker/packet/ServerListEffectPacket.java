package me.bbijabnpobatejb.dreamwalker.packet;


import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.alias.RunAliasHandler;
import me.bbijabnpobatejb.dreamwalker.effects.EffectHandler;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerListEffectPacket implements IMessage {



    @Override
    public void fromBytes(ByteBuf buf) {
    }


    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<ServerListEffectPacket, IMessage> {

        @Override
        public IMessage onMessage(ServerListEffectPacket packet, MessageContext ctx) {
            val sender = ctx.getServerHandler().playerEntity;
            DreamWalker.getLogger().info("Player {} use list effect", sender.getCommandSenderName());
            EffectHandler.showSelfEffects(sender);
            return null;
        }
    }
}
