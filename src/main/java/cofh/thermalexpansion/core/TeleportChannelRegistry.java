package cofh.thermalexpansion.core;

import cofh.core.RegistryEnderAttuned;
import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import cofh.lib.network.ByteBufHelper;
import cofh.lib.transport.ClientEnderChannelRegistry;
import cofh.lib.transport.EnderRegistry;
import cofh.lib.transport.IEnderChannelRegistry;
import cofh.lib.transport.ServerEnderChannelRegistry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public class TeleportChannelRegistry {

	static ServerEnderChannelRegistry serverChannels;
	static ClientEnderChannelRegistry clientChannels;
	final static String dummy = new String();

	public static void initialize() {

		PacketHandler.instance.registerPacket(Packet.class);
	}

	public static void createClientRegistry() {

		clientChannels = new ClientEnderChannelRegistry() {

			@Override
			public String setFrequency(String _, int freq, String name) {

				if (_ != dummy) {
					PacketHandler.sendToServer(new Packet(hostedChannel, freq, name));
				}
				return super.setFrequency(hostedChannel, freq, name);
			}

			@Override
			public String removeFrequency(String _, int freq) {

				if (_ != dummy) {
					PacketHandler.sendToServer(new Packet(hostedChannel, freq));
				}
				return super.removeFrequency(hostedChannel, freq);
			}
		};
	}

	public static void createServerRegistry() {

		serverChannels = new ServerEnderChannelRegistry(new Configuration(new File(DimensionManager.getCurrentSaveRootDirectory(),
				"/cofh/TeleportFrequencies.cfg")));
	}

	static void save() {

		serverChannels.save();
	}

	public static EnderRegistry getRegistry() {

		return RegistryEnderAttuned.getRegistry();
	}

	public static IEnderChannelRegistry getChannels(boolean server) {

		return server ? serverChannels : clientChannels;
	}

	public static void requestChannelList(String channel) {

		PacketHandler.sendToServer(new Packet(channel));
	}

	public static void updateChannelFrequency(EntityPlayer player, String channel, int freq, String name) {

		PacketHandler.sendTo(new Packet(channel, freq, name), player);
	}

	public static void removeChannelFrequency(EntityPlayer player, String channel, int freq) {

		PacketHandler.sendTo(new Packet(channel, freq), player);
	}

	public static class Packet extends PacketBase {

		public ByteBuf data;

		@SuppressWarnings("unused")
		public Packet() {

		}

		private Packet(String channel) {

			data = Unpooled.directBuffer();
			data.writeByte(0);
			ByteBufHelper.writeString(channel, data);
		}

		private Packet(String channel, int freq) {

			data = Unpooled.directBuffer();
			data.writeByte(2);
			ByteBufHelper.writeString(channel, data);
			ByteBufHelper.writeVarInt(freq, data);
		}

		private Packet(String channel, int freq, String name) {

			data = Unpooled.directBuffer();
			data.writeByte(1);
			ByteBufHelper.writeString(channel, data);
			ByteBufHelper.writeVarInt(freq, data);
			ByteBufHelper.writeString(name, data);
		}

		private Packet(String channel, Void v) {

			data = Unpooled.directBuffer();
			data.writeByte(0);
			data.writeBytes(serverChannels.getFrequencyData(channel));
		}

		@Override
		public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {

			buffer.writeBytes(data);
		}

		@Override
		public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {

			data = buffer;
		}

		@Override
		public void handleClientSide(EntityPlayer player) {

			switch (data.readByte()) {
			case 0:
				clientChannels.readFrequencyData(data);
				break;
			case 1:
				if (clientChannels.getChannelName().equals(ByteBufHelper.readString(data))) {
					clientChannels.setFrequency(dummy, ByteBufHelper.readVarInt(data), ByteBufHelper.readString(data));
				}
				break;
			case 2:
				if (clientChannels.getChannelName().equals(ByteBufHelper.readString(data))) {
					clientChannels.removeFrequency(dummy, ByteBufHelper.readVarInt(data));
				}
				break;
			}
		}

		@Override
		public void handleServerSide(EntityPlayer player) {

			switch (data.readByte()) {
			case 0:
				PacketHandler.sendTo(new Packet(ByteBufHelper.readString(data), null), player);
				break;
			case 1:
				serverChannels.setFrequency(ByteBufHelper.readString(data), ByteBufHelper.readVarInt(data), ByteBufHelper.readString(data));
				break;
			case 2:
				serverChannels.removeFrequency(ByteBufHelper.readString(data), ByteBufHelper.readVarInt(data));
				break;
			}
		}

	}

}
