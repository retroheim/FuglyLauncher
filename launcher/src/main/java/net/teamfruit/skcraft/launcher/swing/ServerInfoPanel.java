package net.teamfruit.skcraft.launcher.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JComponent;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.skcraft.launcher.util.SwingExecutor;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Builder;
import lombok.extern.java.Log;
import net.teamfruit.skcraft.launcher.mcpinger.PingResult;
import net.teamfruit.skcraft.launcher.mcpinger.PingResult.Description;
import net.teamfruit.skcraft.launcher.mcpinger.PingResult.Player;
import net.teamfruit.skcraft.launcher.mcpinger.PingResult.Players;
import net.teamfruit.skcraft.launcher.mcpinger.PingResult.Version;
import net.teamfruit.skcraft.launcher.model.modpack.ConnectServerInfo;
import net.teamfruit.skcraft.launcher.swing.ChatMessagePanel.HTMLLog;

@Log
@RequiredArgsConstructor
public class ServerInfoPanel {

	private final ServerInfoStyle style;
	private final ConnectServerInfo server;
	private final Callable<ListenableFuture<PingResult>> futureSupplier;
	private InstanceCellPanel instancePanel;

	public void paint(final Graphics g, Rectangle r, InstanceCellPanel instancePanel) {
		this.instancePanel = instancePanel;
		if (futureSupplier!=null) {
			InfoMessage infoMessage = getMessage();
			String message = infoMessage.getMessage();
			String details = infoMessage.getDetails();
			if (message!=null) {
				Rectangle rect = new Rectangle(r.x+50, r.y+0, r.width-50-25, 20);
				final Graphics2D g2d = (Graphics2D) g.create();
				g2d.translate(rect.x, rect.y);

				FontMetrics fontMetrics = g2d.getFontMetrics();
				int padding = 3;
				int width = fontMetrics.stringWidth(message);
				g2d.setColor(new Color(0, 0, 0, 50));
				//g2d.drawRect(0, 0, rect.width, rect.height);
				g2d.fillRect(rect.width-width-padding*2, 0, width+padding*2, rect.height);
				g2d.setColor(Color.WHITE);
				g2d.drawString(message, rect.width-width-padding, rect.height-fontMetrics.getDescent());
				g2d.dispose();
			}
			if (style==ServerInfoStyle.DETAILS)
				if (instancePanel!=null)
					instancePanel.setToolTipText(details==null ? null : "<html>"+details.replace("\n", "<br>")+"</html>");
		}
	}

	private ListenableFuture<PingResult> resultFuture;

	private InfoMessage getMessage() {
		try {
			{
				ListenableFuture<PingResult> future = futureSupplier.call();
				if (future!=resultFuture) {
					if (!future.isDone())
						future.addListener(new Runnable() {
							@Override
							public void run() {
								update();
							}
						}, SwingExecutor.INSTANCE);
					resultFuture = future;
				}
			}
			if (resultFuture!=null) {
				if (!resultFuture.isDone())
					return InfoMessage.builder().message("Pinging...").build();
				else {
					PingResult result = null;
					String error = null;
					try {
						result = resultFuture.get();
					} catch (Exception e) {
						error = e.getMessage();
					}
					if (result!=null) {
						Players players = result.getPlayers();
						Version version = result.getVersion();
						Description description = result.getDescription();
						InfoMessage.InfoMessageBuilder builder = InfoMessage.builder();
						if (players!=null) {
							builder.message((style!=ServerInfoStyle.SIMPLE ? server+" | " : "")+"✓ "+(players.getOnline()+" / "+players.getMax()));
							List<Player> sampleplayers = players.getSample();
							List<String> samples = Lists.newArrayList();
							if (sampleplayers!=null)
								for (Player player : sampleplayers)
									if (player!=null)
										samples.add(player.getName());
							StringBuilder stb = new StringBuilder();
							stb.append("ip: ").append(server).append("\n");
							stb.append("status: online").append("\n");
							stb.append("version: ").append(version.getName()).append("\n");
							HTMLLog htmllog = new HTMLLog();
							ChatMessagePanel.log(description.getText(), htmllog);
							stb.append("description: ").append(htmllog).append("\n");
							stb.append("players: ").append(players.getOnline()+" / "+players.getMax())
									.append("   ").append(StringUtils.join(samples, "\n   ")).append("\n");
							builder.details(stb.toString());
							return builder.build();
						}
					} else if (style!=ServerInfoStyle.SIMPLE) {
						StringBuilder stb = new StringBuilder();
						stb.append("ip: ").append(server).append("\n");
						stb.append("status: offline").append("\n");
						if (error!=null)
							stb.append("error: "+error).append("\n");
						return InfoMessage.builder().message("✘").details(stb.toString()).build();
					}
				}
			}
		} catch (Exception e1) {
		}
		return InfoMessage.builder().build();
	}

	@Builder(fluent = true)
	@Data
	private static class InfoMessage {
		private String message;
		private String details;
	}

	public void update() {
		JComponent updateComponent = instancePanel.getUpdateComponent();
		if (updateComponent!=null)
			updateComponent.repaint();
	}

}
