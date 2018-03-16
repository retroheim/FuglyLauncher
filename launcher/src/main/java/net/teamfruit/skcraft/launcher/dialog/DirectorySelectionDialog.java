package net.teamfruit.skcraft.launcher.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;

import com.skcraft.launcher.swing.ActionListeners;
import com.skcraft.launcher.swing.FormPanel;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.teamfruit.skcraft.launcher.dirs.DirectoryUtils;
import net.teamfruit.skcraft.launcher.dirs.LauncherDirectories;

public class DirectorySelectionDialog extends JDialog {
	private final LauncherDirectories launcher;

	private final JTextField targetText;
	private final File rawBaseDir;
	private File baseDir;
	private final String name;

	private final FormPanel formpanel = new FormPanel();
	private final JPanel pathTextPanel = new JPanel(new BorderLayout());
	private final JTextField pathText = new JTextField();
	private final JLabel pathTextPrefix = new JLabel();
	private final JLabel pathTextSuffix = new JLabel();
	private final JCheckBox pathTextPrefixCheck = new JCheckBox(SharedLocale.tr("options.fixedName"));
	private final LinkedCheckBox linkedCheck;

	private final LinedBoxPanel buttonsPanel = new LinedBoxPanel(true);
	private final JButton chooseDirButton = new JButton(SharedLocale.tr("options.chooseDirButton"));
	private final JButton openDirButton = new JButton(SharedLocale.tr("options.openDirButton"));
	private final JButton okButton = new JButton(SharedLocale.tr("button.ok"));
	private final JButton cancelButton = new JButton(SharedLocale.tr("button.cancel"));

	public DirectorySelectionDialog(Window owner, @NonNull LauncherDirectories launcher, JTextField pathDirText, String title, File baseDir, String name) {
		super(owner, ModalityType.DOCUMENT_MODAL);

		this.launcher = launcher;
		this.targetText = pathDirText;
		this.baseDir = this.rawBaseDir = DirectoryUtils.tryCanonical(baseDir);
		this.name = name;

		this.linkedCheck = new LinkedCheckBox(pathTextPrefixCheck) {
			@Override
			protected void onSetSelected(boolean b) {
				pathTextSuffix.setVisible(b);
			}
		};

		setTitle(title);
		initComponents();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		final int height = 130;
		setMinimumSize(new Dimension(650, height));
		setMaximumSize(new Dimension(Short.MAX_VALUE, height));
		setSize(new Dimension(650, height));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setSize(new Dimension(getWidth(), height));
				super.componentResized(e);
			}
		});
		setLocationRelativeTo(owner);
	}

	private void initComponents() {
		PathTextListener listener = new PathTextListener(pathText, pathTextPrefix) {
			@Override
			public void stateChanged(ChangeEvent e) {
				super.stateChanged(e);
				if (name!=null) {
					final String text = target.getText();
					pathTextSuffix.setText((StringUtils.endsWithAny(text, new String[] { "/", "\\" }) ? "" : File.separator)+name);
					if (!linkedCheck.isSelected()) {
						File file = DirectoryUtils.getDirFromOption(baseDir, text);
						if (StringUtils.equalsIgnoreCase(file.getName(), name)) {
							linkedCheck.setSelected(true);
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									target.setText(new File(text).getParent());
								}
							});
						}
					}
				}
			}
		};
		addChangeListener(pathText, listener);
		String text = targetText.getText();
		pathText.setText(text);
		if (StringUtils.equalsIgnoreCase(baseDir.getName(), name)) {
			File parent = baseDir.getParentFile();
			if (parent!=null) {
				baseDir = parent;
				if (StringUtils.isEmpty(text))
					linkedCheck.setSelected(true);
			}
		}
		listener.stateChanged(new ChangeEvent(pathText));

		if (name==null)
			pathTextPrefixCheck.setEnabled(false);
		pathTextPrefix.setText(SharedLocale.tr("options.baseDir", File.separator));
		pathTextPrefix.setToolTipText(baseDir.getAbsolutePath());
		pathTextPanel.add(pathTextPrefix, BorderLayout.WEST);
		pathTextPanel.add(pathText, BorderLayout.CENTER);
		pathTextPanel.add(pathTextSuffix, BorderLayout.EAST);
		formpanel.addRow(pathTextPanel);
		SwingHelper.removeOpaqueness(formpanel);
		add(SwingHelper.alignTabbedPane(formpanel), BorderLayout.CENTER);

		buttonsPanel.addElement(chooseDirButton);
		buttonsPanel.addElement(openDirButton);
		buttonsPanel.addElement(pathTextPrefixCheck);
		buttonsPanel.addGlue();
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

		openDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File dir = DirectoryUtils.getDirFromOption(baseDir, pathText.getText());
				dir.mkdirs();
				SwingHelper.browseDir(dir, DirectorySelectionDialog.this);
			}
		});

		chooseDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser filechooser = new JFileChooser();

				File targetDir = DirectoryUtils.getDirFromOption(baseDir, pathText.getText());
				File exists = DirectoryUtils.findExistsDirFromAncestors(targetDir);
				filechooser.setCurrentDirectory(exists);
				filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				filechooser.setFileFilter(new FileFilter() {
					@Getter
					private final String description = SharedLocale.tr("options.folderOnly");

					@Override
					public boolean accept(File f) {
						return f.isDirectory();
					}
				});
				filechooser.setAcceptAllFileFilterUsed(false);

				ComponentUI componentUI = filechooser.getUI();
				if (componentUI instanceof BasicFileChooserUI)
					((BasicFileChooserUI) componentUI).setFileName(targetDir.getAbsolutePath());

				if (filechooser.showSaveDialog(DirectorySelectionDialog.this)==JFileChooser.APPROVE_OPTION) {
					File file = filechooser.getSelectedFile();
					if (file!=null) {
						file = DirectoryUtils.tryCanonical(file);
						if (DirectoryUtils.isInSubDirectory(baseDir, file))
							pathText.setText(DirectoryUtils.getRelativePath(baseDir, file));
						else
							pathText.setText(file.getAbsolutePath());
					}
				}
			}
		});
	}

	/**
	 * Save the configuration and close the dialog.
	 */
	public void save() {
		String text = pathText.getText();
		if (linkedCheck.isSelected())
			text = new File(StringUtils.isEmpty(text) ? null : text, name).getPath();
		if (!DirectoryUtils.isAbsolute(text)) {
			File file = DirectoryUtils.getDirFromOption(baseDir, text);
			if (DirectoryUtils.isInSubDirectory(rawBaseDir, file))
				text = DirectoryUtils.getRelativePath(rawBaseDir, file);
			else
				text = file.getAbsolutePath();
		}
		targetText.setText(text);
		dispose();
	}

	@RequiredArgsConstructor
	public static class PathTextListener implements ChangeListener {
		protected final JTextField target;
		protected final JLabel pathLabel;

		@Override
		public void stateChanged(ChangeEvent e) {
			if (pathLabel!=null)
				pathLabel.setVisible(!DirectoryUtils.isAbsolute(target.getText()));
		}
	}

	/**
	 * Installs a listener to receive notification when the text of any
	 * {@code JTextComponent} is changed. Internally, it installs a
	 * {@link DocumentListener} on the text component's {@link Document},
	 * and a {@link PropertyChangeListener} on the text component to detect
	 * if the {@code Document} itself is replaced.
	 *
	 * @param text any text component, such as a {@link JTextField}
	 *        or {@link JTextArea}
	 * @param changeListener a listener to receieve {@link ChangeEvent}s
	 *        when the text is changed; the source object for the events
	 *        will be the text component
	 * @throws NullPointerException if either parameter is null
	 */
	public static void addChangeListener(@NonNull final JTextComponent text, @NonNull final ChangeListener changeListener) {
		final DocumentListener dl = new DocumentListener() {
			private int lastChange = 0, lastNotifiedChange = 0;

			@Override
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				lastChange++;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (lastNotifiedChange!=lastChange) {
							lastNotifiedChange = lastChange;
							changeListener.stateChanged(new ChangeEvent(text));
						}
					}
				});
			}
		};
		text.addPropertyChangeListener("document", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				Document d1 = (Document) e.getOldValue();
				Document d2 = (Document) e.getNewValue();
				if (d1!=null)
					d1.removeDocumentListener(dl);
				if (d2!=null)
					d2.addDocumentListener(dl);
				dl.changedUpdate(null);
			}
		});
		Document d = text.getDocument();
		if (d!=null)
			d.addDocumentListener(dl);
	}

	public static abstract class LinkedCheckBox {
		private final JCheckBox check;

		public LinkedCheckBox(JCheckBox check) {
			this.check = check;
			this.check.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onSetSelected(LinkedCheckBox.this.check.isSelected());
				}
			});
			setSelected(false);
		}

		public void setSelected(boolean b) {
			this.check.setSelected(b);
			onSetSelected(b);
		}

		public boolean isSelected() {
			return this.check.isSelected();
		}

		protected abstract void onSetSelected(boolean b);
	}
}
