package net.teamfruit.skcraft.launcher.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

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
	private File baseDir;
	private final String name;

	private final FormPanel pathDirPanel = new FormPanel();
	private final JPanel pathTextPanel = new JPanel(new BorderLayout());
	private final JTextField pathText = new JTextField();
	private final JLabel pathTextPrefix = new JLabel();
	private final JLabel pathTextSuffix = new JLabel();
	private final JCheckBox fixedNameCheck = new JCheckBox(SharedLocale.tr("options.fixedName"));
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
		this.baseDir = DirectoryUtils.tryCanonical(baseDir);
		this.name = name;

		this.linkedCheck = new LinkedCheckBox(fixedNameCheck) {
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
			public void changed() {
				super.changed();
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
		listener.register(pathText);
		pathText.setText(targetText.getText());
		if (StringUtils.equalsIgnoreCase(DirectoryUtils.getDirFromOption(baseDir, pathText.getText()).getName(), name)) {
			File parent = baseDir.getParentFile();
			if (parent!=null) {
				baseDir = parent;
				linkedCheck.setSelected(true);
			}
		}
		listener.changed();

		pathTextPrefix.setText(SharedLocale.tr("options.baseDir", File.separator));
		pathTextPrefix.setToolTipText(baseDir.getAbsolutePath());
		pathTextPanel.add(pathTextPrefix, BorderLayout.WEST);
		pathTextPanel.add(pathText, BorderLayout.CENTER);
		pathTextPanel.add(pathTextSuffix, BorderLayout.EAST);
		pathDirPanel.addRow(pathTextPanel);
		SwingHelper.removeOpaqueness(pathDirPanel);
		add(SwingHelper.alignTabbedPane(pathDirPanel), BorderLayout.CENTER);

		buttonsPanel.addElement(chooseDirButton);
		buttonsPanel.addElement(openDirButton);
		buttonsPanel.addElement(fixedNameCheck);
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
				if (exists!=null&&exists.equals(targetDir))
					exists = exists.getParentFile();
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

				filechooser.showSaveDialog(DirectorySelectionDialog.this);
				File file = filechooser.getSelectedFile();
				if (file!=null) {
					file = DirectoryUtils.tryCanonical(file);
					if (DirectoryUtils.isInSubDirectory(baseDir, file))
						pathText.setText(DirectoryUtils.getRelativePath(baseDir, file));
					else
						pathText.setText(file.getAbsolutePath());
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
			text = new File(StringUtils.isEmpty(text)?null:text, name).getPath();
		targetText.setText(text);
		dispose();
	}

	@RequiredArgsConstructor
	public static class PathTextListener extends DocumentChangeListener {
		protected final JTextField target;
		protected final JLabel pathLabel;

		public void changed() {
			if (pathLabel!=null)
				pathLabel.setVisible(!new File(target.getText()).isAbsolute());
		}
	}

	public static abstract class DocumentChangeListener implements DocumentListener {
		public void changedUpdate(DocumentEvent e) {
			changed();
		}

		public void removeUpdate(DocumentEvent e) {
			changed();
		}

		public void insertUpdate(DocumentEvent e) {
			changed();
		}

		public abstract void changed();

		public void register(JTextField target) {
			target.getDocument().addDocumentListener(this);
		}
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
