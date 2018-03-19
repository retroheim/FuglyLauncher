package net.teamfruit.skcraft.launcher.discordrpc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.skcraft.launcher.util.Environment;
import com.skcraft.launcher.util.Platform;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordEventHandlers.OnReady;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import lombok.extern.java.Log;
import net.teamfruit.skcraft.launcher.dirs.LauncherDirectories;

@Log
public class LauncherDiscord {
	@Nullable
	private static LauncherDiscord instance;

	public static void init(LauncherDirectories dirs) {
		try {
			loadLibs(dirs.getNativeDir());
			instance = new LauncherDiscord();
		} catch (Exception e) {
			log.log(Level.WARNING, "[DiscordRPC] exception: ", e);
		} catch (Throwable t) {
			log.log(Level.WARNING, "[DiscordRPC] error: ", t);
		}
	}

	private final DiscordRPC lib;

	private LauncherDiscord() throws Exception {
		lib = DiscordRPC.INSTANCE;
		String applicationId = "425297966069317632";
		String steamId = null;
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		handlers.ready = new OnReady() {
			@Override
			public void accept() {
				log.info("[DiscordRPC] online.");
			}
		};
		log.info("[DiscordRPC] initializing.");
		lib.Discord_Initialize(applicationId, handlers, true, steamId);

		log.info("[DiscordRPC] starting.");
		updateStatusImpl(DiscordStatus.DEFAULT.createRPC(ImmutableMap.<String, String>of()));

		// in a worker thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					lib.Discord_RunCallbacks();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException ignored) {
					}
				}
			}
		}, "RPC-Callback-Handler").start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("[DiscordRPC] shutting down.");
				lib.Discord_Shutdown();
			}
		}));
	}

	public void updateStatusImpl(DiscordRichPresence presence) throws UnsatisfiedLinkError {
		lib.Discord_UpdatePresence(presence);
	}

	public void clearStatusImpl() throws UnsatisfiedLinkError {
		lib.Discord_ClearPresence();
	}

	public static void updateStatus(DiscordRichPresence presence) {
		final LauncherDiscord inst = instance;
		if (inst!=null)
			try {
				if (presence!=null)
					inst.updateStatusImpl(presence);
				else
					inst.clearStatusImpl();
			} catch (Throwable t) {
				log.log(Level.WARNING, "[DiscordRPC] update status error: ", t);
			}
	}

	private static void loadLibs(File nativedir) throws Exception {
		try {
			Platform platform = Environment.getInstance().getPlatform();
			switch (platform) {
				case WINDOWS:
					if (StringUtils.equals(System.getProperty("sun.arch.data.model"), "32"))
						loadFile(nativedir, "win32-x86/discord-rpc.dll");
					else
						loadFile(nativedir, "win32-x86-64/discord-rpc.dll");
					break;
				case LINUX:
					loadFile(nativedir, "linux-x86-64/libdiscord-rpc.so");
					break;
				case MAC_OS_X:
					loadFile(nativedir, "darwin/libdiscord-rpc.dylib");
					break;
				default:
					log.info("[DiscordRPC] THIS OPERATING SYSTEM IS NOT SUPPORTED!!");
					break;
			}
		} catch (IOException e) {
			throw new Exception("Could not load DiscordRPC libraries: ", e);
		} catch (UnsatisfiedLinkError e) {
			throw new Exception("Could not link DiscordRPC libraries: ", e);
		}
	}

	private static void loadFile(File nativedir, final String name) throws IOException, UnsatisfiedLinkError {
		nativedir.mkdirs();
		String filename = new File(LauncherDiscord.class.getResource("/"+name).getPath()).getName();
		File lib = new File(nativedir, filename);
		if (!lib.exists()) {
			lib.createNewFile();
			Files.copy(new InputSupplier<InputStream>() {
				@Override
				public InputStream getInput() throws IOException {
					return LauncherDiscord.class.getResourceAsStream("/"+name);
				}
			}, lib);
		}
		System.load(lib.getAbsolutePath());
	}
}
