package me.bbijabnpobatejb.dreamwalker.packet;


import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.bbijabnpobatejb.dreamwalker.alias.AliasHandler;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientMessagePacket implements IMessage {

    String message;


    @Override
    public void fromBytes(ByteBuf buf) {
        message = ByteBufUtils.readUTF8String(buf);
    }


    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, message);
    }

    public static class Handler implements IMessageHandler<ClientMessagePacket, IMessage> {

        @Override
        public IMessage onMessage(ClientMessagePacket packet, MessageContext ctx) {
            String message = packet.message;
            AliasHandler.handleClientMessagePacket(message);
            return null;
        }
    }
}
