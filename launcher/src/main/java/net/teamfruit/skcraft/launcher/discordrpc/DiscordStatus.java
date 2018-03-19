package net.teamfruit.skcraft.launcher.discordrpc;

import java.util.Map;

import club.minnced.discord.rpc.DiscordRichPresence;

public enum DiscordStatus {
	STARTING {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "Starting Launcher...";
			return presence;
		}
	},
	DEFAULT {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			return STARTING.createRPC(args);
		}
	},
	MENU {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "In Menu";
			return presence;
		}
	},
	CONFIG {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "In Config";
			return presence;
		}
	},
	LOGIN {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "Preparing";
			presence.details = "to join ";
			return presence;
		}
	},
	FEATURE_SELECT {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "Selecting Mods";
			presence.details = "to join ";
			return presence;
		}
	},
	ABOUT {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "Viewing About Launcher";
			return presence;
		}
	},
	DOWNLOADING {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "Downloading Minecraft";
			return presence;
		}
	},
	PLAYING {
		@Override
		public DiscordRichPresence createRPC(final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "idle";
			presence.largeImageText = "FruitLauncher";
			presence.state = "Playing Minecraft";
			return presence;
		}
	},

	;

	public abstract DiscordRichPresence createRPC(final Map<String, String> args);

	public void update(final Map<String, String> args) {
		LauncherDiscord.updateStatus(createRPC(args));
	}
}
