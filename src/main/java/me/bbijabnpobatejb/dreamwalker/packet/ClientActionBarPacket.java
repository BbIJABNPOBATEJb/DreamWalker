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
import net.minecraft.client.Minecraft;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientActionBarPacket implements IMessage {

    String message;


    @Override
    public void fromBytes(ByteBuf buf) {
        message = ByteBufUtils.readUTF8String(buf);
    }


    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, message);
    }

    public static class Handler implements IMessageHandler<ClientActionBarPacket, IMessage> {

        @Override
        public IMessage onMessage(ClientActionBarPacket packet, MessageContext ctx) {
            String message = packet.message;
            Minecraft.getMinecraft().ingameGUI.setRecordPlaying(message, false);
            return null;
        }
    }
}
