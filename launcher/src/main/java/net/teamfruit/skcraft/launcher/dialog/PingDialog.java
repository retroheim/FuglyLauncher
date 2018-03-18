package net.teamfruit.skcraft.launcher.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextArea;

import com.google.common.base.Splitter;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.swing.ActionListeners;
import com.skcraft.launcher.swing.FormPanel;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Data;
import lombok.NonNull;
import net.teamfruit.skcraft.launcher.mcpinger.PingOptions;
import net.teamfruit.skcraft.launcher.mcpinger.PingResult;
import net.teamfruit.skcraft.launcher.mcpinger.Pinger;

public class PingDialog extends JDialog {
	private final Launcher launcher;

	private static final Splitter field_147230_a = Splitter.on('\u0000').limit(6);

	private final FormPanel formpanel = new FormPanel();
	private final JTextArea pingResult = new JTextArea();

	private final LinedBoxPanel buttonsPanel = new LinedBoxPanel(true);
	private final JButton pingButton = new JButton(SharedLocale.tr("pinger.pingButton"));
	private final JButton okButton = new JButton(SharedLocale.tr("button.ok"));

	public PingDialog(Window owner, @NonNull Launcher launcher) {
		super(owner, ModalityType.DOCUMENT_MODAL);

		this.launcher = launcher;

		setTitle(SharedLocale.tr("pinger.title"));
		initComponents();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(owner);
		setSize(new Dimension(400, 400));
		setResizable(false);
	}

	private void initComponents() {
		formpanel.addRow(pingResult);
		SwingHelper.removeOpaqueness(formpanel);
		add(SwingHelper.alignTabbedPane(formpanel), BorderLayout.CENTER);

		buttonsPanel.addElement(pingButton);
		buttonsPanel.addGlue();
		buttonsPanel.addElement(okButton);

		add(buttonsPanel, BorderLayout.SOUTH);

		okButton.addActionListener(ActionListeners.dispose(this));

		pingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					PingResult data = new Pinger().ping(new PingOptions().hostname("teamfruit.net").port(25565));
					pingResult.setText(data.getDescription().getText()+"  --  "+data.getPlayers().getOnline()+"/"+data.getPlayers().getMax());
				} catch (Exception e) {
					System.out.println("Exception: "+e);
				}
			}
		});
	}

	public static int parseIntWithDefault(String p_82715_0_, int p_82715_1_) {
		int j = p_82715_1_;

		try {
			j = Integer.parseInt(p_82715_0_);
		} catch (Throwable throwable) {
			;
		}

		return j;
	}

	@Data
	public static class ServerData {
		public String serverName;
		public String serverIP;
		/**
		 * the string indicating number of players on and capacity of the server that is shown on the server browser (i.e.
		 * "5/20" meaning 5 slots used out of 20 slots total)
		 */
		public String populationInfo;
		/**
		 * (better variable name would be 'hostname') server name as displayed in the server browser's second line (grey
		 * text)
		 */
		public String serverMOTD;
		/** last server ping that showed up in the server browser */
		public long pingToServer;
		public int field_82821_f;
		/** Game version for this server. */
		public String gameVersion;
		public boolean field_78841_f;
		public String field_147412_i;
	}
}
