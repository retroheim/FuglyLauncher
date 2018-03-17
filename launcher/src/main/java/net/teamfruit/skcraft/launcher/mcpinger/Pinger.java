package net.teamfruit.skcraft.launcher.mcpinger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.lang.Validate;

import com.skcraft.launcher.persistence.Persistence;

public class Pinger {

	public static byte PACKET_HANDSHAKE = 0x00;
	public static byte PACKET_STATUSREQUEST = 0x00;
	public static byte PACKET_PING = 0x01;

	public static int PROTOCOL_VERSION = 4;
	public static int STATUS_HANDSHAKE = 1;

	/**
	 * Fetches a {@link PingResult} for the supplied hostname.
	 * <b>Assumed timeout of 2s and port of 25565.</b>
	 *
	 * @param hostname - a valid String hostname
	 * @return {@link PingResult}
	 * @throws IOException
	 */
	public PingResult ping(final String hostname) throws IOException {
		return this.ping(new PingOptions().setHostname(hostname));
	}

	/**
	 * Fetches a {@link PingResult} for the supplied options.
	 *
	 * @param options - a filled instance of {@link PingOptions}
	 * @return {@link PingResult}
	 * @throws IOException
	 */
	public PingResult ping(final PingOptions options) throws IOException {
		Validate.notNull(options.getHostname(), "Hostname cannot be null.");
		Validate.notNull(options.getPort(), "Port cannot be null.");

		final Socket socket = new Socket();
		socket.connect(new InetSocketAddress(options.getHostname(), options.getPort()), options.getTimeout());

		final DataInputStream in = new DataInputStream(socket.getInputStream());
		final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		//> Handshake

		ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
		DataOutputStream handshake = new DataOutputStream(handshake_bytes);

		handshake.writeByte(PACKET_HANDSHAKE);
		writeVarInt(handshake, PROTOCOL_VERSION);
		writeVarInt(handshake, options.getHostname().length());
		handshake.writeBytes(options.getHostname());
		handshake.writeShort(options.getPort());
		writeVarInt(handshake, STATUS_HANDSHAKE);

		writeVarInt(out, handshake_bytes.size());
		out.write(handshake_bytes.toByteArray());

		//> Status request

		out.writeByte(0x01); // Size of packet
		out.writeByte(PACKET_STATUSREQUEST);

		//< Status response

		readVarInt(in); // Size
		int id = readVarInt(in);

		io(id==-1, "Server prematurely ended stream.");
		io(id!=PACKET_STATUSREQUEST, "Server returned invalid packet.");

		int length = readVarInt(in);
		io(length==-1, "Server prematurely ended stream.");
		io(length==0, "Server returned unexpected value.");

		byte[] data = new byte[length];
		in.readFully(data);
		String json = new String(data, options.getCharset());

		//> Ping

		out.writeByte(0x09); // Size of packet
		out.writeByte(PACKET_PING);
		out.writeLong(System.currentTimeMillis());

		//< Ping

		readVarInt(in); // Size
		id = readVarInt(in);
		io(id==-1, "Server prematurely ended stream.");
		io(id!=PACKET_PING, "Server returned invalid packet.");

		// Close

		handshake.close();
		handshake_bytes.close();
		out.close();
		in.close();
		socket.close();

		return Persistence.getMapper().readValue(json, PingResult.class);
	}

	private static void io(final boolean b, final String m) throws IOException {
		if (b) {
			throw new IOException(m);
		}
	}

	private static int readVarInt(DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			int k = in.readByte();

			i |= (k&0x7F)<<j++*7;

			if (j>5)
				throw new RuntimeException("VarInt too big");

			if ((k&0x80)!=128)
				break;
		}

		return i;
	}

	private static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
		while (true) {
			if ((paramInt&0xFFFFFF80)==0) {
				out.writeByte(paramInt);
				return;
			}

			out.writeByte(paramInt&0x7F|0x80);
			paramInt >>>= 7;
		}
	}

}