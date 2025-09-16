package me.bbijabnpobatejb.dreamwalker.packet;


import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.DreamWalker;
import me.bbijabnpobatejb.dreamwalker.alias.AliasHandler;
import me.bbijabnpobatejb.dreamwalker.alias.RunAliasHandler;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerRunAliasPacket implements IMessage {

    String rawMessage;


    @Override
    public void fromBytes(ByteBuf buf) {
        rawMessage = ByteBufUtils.readUTF8String(buf);
    }


    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, rawMessage);
    }

    public static class Handler implements IMessageHandler<ServerRunAliasPacket, IMessage> {

        @Override
        public IMessage onMessage(ServerRunAliasPacket packet, MessageContext ctx) {
            val sender = ctx.getServerHandler().playerEntity;
            val s = packet.rawMessage;
            val log = s != null ? s : "null";
            DreamWalker.getLogger().info("Player {} use alias '{}'", sender.getCommandSenderName(), log);
            RunAliasHandler.handle(sender, s);
            return null;
        }
    }
}
