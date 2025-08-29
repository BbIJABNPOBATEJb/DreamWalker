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
import me.bbijabnpobatejb.dreamwalker.cube.RollHandler;
import me.bbijabnpobatejb.dreamwalker.side.ClientProxy;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientConfigPacket implements IMessage {
    boolean clientIsAdmin;
    String aliasPrefix;
    String argsHolder;
    String rollPrefix;
    String formatMessageRoll;
    int rollCommentMaxChars;
    String[] channelPrefixes;

    @Override
    public void fromBytes(ByteBuf buf) {
        clientIsAdmin = buf.readBoolean();
        aliasPrefix = ByteBufUtils.readUTF8String(buf);
        argsHolder = ByteBufUtils.readUTF8String(buf);
        rollPrefix = ByteBufUtils.readUTF8String(buf);
        formatMessageRoll = ByteBufUtils.readUTF8String(buf);
        rollCommentMaxChars = buf.readInt();

        int length = buf.readInt();
        channelPrefixes = new String[length];
        for (int i = 0; i < length; i++) {
            channelPrefixes[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(clientIsAdmin);
        ByteBufUtils.writeUTF8String(buf, aliasPrefix);
        ByteBufUtils.writeUTF8String(buf, argsHolder);
        ByteBufUtils.writeUTF8String(buf, rollPrefix);
        ByteBufUtils.writeUTF8String(buf, formatMessageRoll);
        buf.writeInt(rollCommentMaxChars);

        buf.writeInt(channelPrefixes.length);
        for (String prefix : channelPrefixes) {
            ByteBufUtils.writeUTF8String(buf, prefix);
        }
    }

    public static class Handler implements IMessageHandler<ClientConfigPacket, IMessage> {

        @Override
        public IMessage onMessage(ClientConfigPacket packet, MessageContext ctx) {
            ClientProxy.clientIsAdmin = packet.clientIsAdmin;
            ClientProxy.config.setAliasPrefix(packet.aliasPrefix);
            ClientProxy.config.setArgsHolder(packet.argsHolder);
            ClientProxy.config.setRollPrefix(packet.rollPrefix);
            ClientProxy.config.setFormatMessageRoll(packet.formatMessageRoll);
            ClientProxy.config.setRollCommentMaxChars(packet.rollCommentMaxChars);
            ClientProxy.config.setChannelPrefixes(packet.channelPrefixes);

            DreamWalker.getLogger().info("Config from server loaded");
            return null;
        }
    }
}