package net.teamfruit.skcraft.launcher.mcpinger;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Storage class for {@link Pinger} options.
 */
@Accessors(chain = true)
@Data
public class PingOptions {

	private String hostname;
	private int port = 25565;
	private int timeout = 2000;
	private String charset = "UTF-8";

}
