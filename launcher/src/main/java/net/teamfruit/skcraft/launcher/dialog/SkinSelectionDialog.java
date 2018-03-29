package net.teamfruit.skcraft.launcher.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Predicate;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.swing.ActionListeners;
import com.skcraft.launcher.swing.FormPanel;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.teamfruit.skcraft.launcher.skins.RemoteSkin;
import net.teamfruit.skcraft.launcher.skins.SkinUtils;

public class SkinSelectionDialog extends JDialog {
	private final Launcher launcher;

	private final JTextField targetText;

	private final FormPanel formpanel = new FormPanel();
	private final JList<SkinItem> skins = new JList<SkinItem>();

	private final LinedBoxPanel buttonsPanel = new LinedBoxPanel(true);
	private final JButton okButton = new JButton(SharedLocale.tr("button.ok"));
	private final JButton cancelButton = new JButton(SharedLocale.tr("button.cancel"));

	public SkinSelectionDialog(Window owner, @NonNull Launcher launcher, JTextField pathDirText) {
		super(owner, ModalityType.DOCUMENT_MODAL);

		this.launcher = launcher;
		this.targetText = pathDirText;

		setTitle(SharedLocale.tr("options.selectSkinTitle"));
		initComponents();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(new Dimension(350, 350));
		setLocationRelativeTo(owner);
	}

	private void initComponents() {
		Properties prop = launcher.getRemoteSkins().getSkinProperties();
		if (prop!=null) {
			final List<SkinItem> skinNames = Lists.newArrayList(DefaultSkinItem.instance);
			for (Object oname : prop.keySet())
				if (oname instanceof String)
					skinNames.add(new DataSkinItem((String) oname));

			skins.setModel(new AbstractListModel<SkinItem>() {
				public int getSize() {
					return skinNames.size();
				}

				public SkinItem getElementAt(int i) {
					return skinNames.get(i);
				}
			});
			String skinName = targetText.getText();
			skins.setSelectedValue(StringUtils.isEmpty(skinName)?DefaultSkinItem.instance:new DataSkinItem(skinName), true);
		}

		formpanel.addRow(skins);
		SwingHelper.removeOpaqueness(formpanel);
		add(SwingHelper.alignTabbedPane(formpanel), BorderLayout.CENTER);

		buttonsPanel.addElement(okButton);
		buttonsPanel.addElement(cancelButton);

		add(buttonsPanel, BorderLayout.SOUTH);

		cancelButton.addActionListener(ActionListeners.dispose(this));

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
	}

	/**
	 * Save the configuration and close the dialog.
	 */
	public void save() {
		final SkinItem skinName = skins.getSelectedValue();
		if (skinName==null||skinName==DefaultSkinItem.instance) {
			targetText.setText("");
			dispose();
			return;
		}
		RemoteSkin remoteSkin = launcher.getRemoteSkins().getRemoteSkin(skinName.toString());
		SkinUtils.loadSkin(this, launcher, remoteSkin, new Predicate<RemoteSkin>() {
			@Override
			public boolean apply(RemoteSkin input) {
				targetText.setText(skinName.toString());
				dispose();
				return true;
			}
		});
	}

	private static interface SkinItem {
		String toString();
	}

	private static class DefaultSkinItem implements SkinItem {
		public static final SkinItem instance = new DefaultSkinItem();

		private DefaultSkinItem() {
		}

		@Override
		public String toString() {
			return SharedLocale.tr("options.selectSkinDefault");
		}
	}

	@RequiredArgsConstructor
	@Data
	private static class DataSkinItem implements SkinItem {
		private final String name;

		@Override
		public String toString() {
			return name;
		}
	}
}
