package net.teamfruit.skcraft.launcher.discordrpc;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import club.minnced.discord.rpc.DiscordRichPresence;

public enum DiscordStatus {
	STARTING {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "fruit_general";
			presence.state = "Starting Launcher...";
			return presence;
		}
	},
	DEFAULT {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			return STARTING.createRPC(lastPresence, args);
		}
	},
	MENU {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.largeImageKey = "fruit_general";
			presence.state = "In Menu";
			return presence;
		}
	},
	CONFIG {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.smallImageKey = "icon_config";
			presence.largeImageKey = "fruit_general";
			presence.state = "In Config";
			return presence;
		}
	},
	LOGIN {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.smallImageKey = "icon_login";
			presence.largeImageKey = "fruit_general";
			presence.smallImageText = args.get("server");
			presence.details = args.get("instance");
			presence.state = "Logging in";
			return presence;
		}
	},
	WAITING {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.smallImageKey = "icon_waiting";
			presence.largeImageKey = "fruit_general";
			presence.smallImageText = args.get("server");
			presence.details = args.get("instance");
			presence.state = "Waiting";
			return presence;
		}
	},
	FEATURE_SELECT {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.smallImageKey = "icon_feature_select";
			presence.largeImageKey = "fruit_general";
			presence.smallImageText = args.get("server");
			presence.details = args.get("instance");
			presence.state = "Selecting Mods";
			return presence;
		}
	},
	DOWNLOADING {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.smallImageKey = "icon_download";
			presence.largeImageKey = "fruit_general";
			presence.smallImageText = args.get("server");
			presence.details = args.get("instance");
			presence.state = "Downloading Minecraft";
			return presence;
		}
	},
	LAUNCHING {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.smallImageKey = "icon_launching";
			presence.largeImageKey = "fruit_general";
			presence.smallImageText = args.get("server");
			presence.details = args.get("instance");
			presence.largeImageText = "Player: "+args.get("player");
			presence.state = "Loading Minecraft";
			presence.startTimestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
			return presence;
		}
	},
	PLAYING {
		@Override
		public DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args) {
			DiscordRichPresence presence = new DiscordRichPresence();
			presence.smallImageKey = "icon_playing";
			presence.largeImageKey = "fruit_general";
			presence.smallImageText = args.get("server");
			presence.details = args.get("instance");
			presence.largeImageText = "Player: "+args.get("player");
			presence.state = "Playing Minecraft";
			presence.startTimestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
			return presence;
		}
	},

	;

	public abstract DiscordRichPresence createRPC(final DiscordRichPresence lastPresence, final Map<String, String> args);

	public void update(final DiscordRichPresence lastPresence, final Map<String, String> args) {
		LauncherDiscord.updateStatus(createRPC(lastPresence, args));
	}
}
