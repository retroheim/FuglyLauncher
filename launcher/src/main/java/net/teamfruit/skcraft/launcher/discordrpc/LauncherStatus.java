package net.teamfruit.skcraft.launcher.discordrpc;

import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import lombok.Data;
import lombok.NonNull;

public class LauncherStatus {
	public static final LauncherStatus instance = new LauncherStatus();

	private final Map<Component, StatusNode> store = new WeakHashMap<Component, StatusNode>();
	private StatusNode last;

	public void open(@NonNull Component window, @NonNull DiscordStatus status, @NonNull Map<String, String> map) {
		StatusNode node = new StatusNode(new WeakReference<Component>(window), getAvailableNode(last), status, map);
		last = node;
		store.put(window, node);
		status.update(map);
	}

	public void close(Component window) {
		StatusNode node = store.get(window);
		if (node!=null) {
			store.remove(window);
			node.setClosed(true);
			StatusNode parent = getAvailableNode(node);
			if (parent!=null)
				parent.getStatus().update(parent.getMap());
		}
	}

	private StatusNode getAvailableNode(StatusNode node) {
		if (node!=null)
			do {
				if (!node.isClosed()) {
					Component window = node.getWindow().get();
					if (window!=null)
						if (window.isVisible())
							break;
						else
							store.remove(window);
				}
			} while ((node = node.getParent())!=null);
		return node;
	}

	@Data
	public static class StatusNode {
		private final WeakReference<Component> window;
		private final StatusNode parent;
		private final DiscordStatus status;
		private final Map<String, String> map;
		private boolean closed;
	}
}
