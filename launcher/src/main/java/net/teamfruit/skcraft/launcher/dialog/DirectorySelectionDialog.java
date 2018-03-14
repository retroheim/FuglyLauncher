package net.teamfruit.skcraft.launcher.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.swing.ActionListeners;
import com.skcraft.launcher.swing.FormPanel;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Getter;
import lombok.NonNull;

public class DirectorySelectionDialog extends JDialog {
	private final Launcher launcher;

	private final JTextField targetText;
	private File baseDir;

	private final FormPanel pathDirPanel = new FormPanel();
	private final JPanel pathTextPanel = new JPanel(new BorderLayout());
	private final JTextField pathText = new JTextField();
	private final JLabel pathTextLabel = new JLabel();

	private final LinedBoxPanel buttonsPanel = new LinedBoxPanel(true);
	private final JButton chooseDirButton = new JButton(SharedLocale.tr("options.chooseDirButton"));
	private final JButton openDirButton = new JButton(SharedLocale.tr("options.openDirButton"));
	private final JButton okButton = new JButton(SharedLocale.tr("button.ok"));
	private final JButton cancelButton = new JButton(SharedLocale.tr("button.cancel"));

	public DirectorySelectionDialog(Window owner, @NonNull Launcher launcher, JTextField pathDirText, String title, File baseDir) {
		super(owner, ModalityType.DOCUMENT_MODAL);

		this.launcher = launcher;
		this.targetText = pathDirText;
		try {
			baseDir = baseDir.getCanonicalFile();
		} catch (IOException e) {
		}
		this.baseDir = baseDir;

		setTitle(title);
		initComponents();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(new Dimension(650, 120));
		setResizable(false);
		setLocationRelativeTo(owner);
	}

	private void initComponents() {
		pathText.setText(targetText.getText());
		pathTextPanel.add(pathTextLabel, BorderLayout.WEST);
		pathTextPanel.add(pathText, BorderLayout.CENTER);
		updatePathLabel();
		pathDirPanel.addRow(pathTextPanel);
		SwingHelper.removeOpaqueness(pathDirPanel);
		add(SwingHelper.alignTabbedPane(pathDirPanel), BorderLayout.CENTER);

		buttonsPanel.addElement(chooseDirButton);
		buttonsPanel.addElement(openDirButton);
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
				File dir = launcher.getDirFromOption(baseDir, pathText.getText());
				dir.mkdirs();
				SwingHelper.browseDir(dir, DirectorySelectionDialog.this);
			}
		});

		chooseDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser filechooser = new JFileChooser();
				filechooser.setCurrentDirectory(launcher.getDirFromOption(baseDir, pathText.getText()));
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
				filechooser.showSaveDialog(DirectorySelectionDialog.this);
				File file = filechooser.getSelectedFile();
				if (file!=null) {
					try {
						file = file.getCanonicalFile();
					} catch (IOException e) {
					}
					if (isInSubDirectory(baseDir, file))
						pathText.setText(getRelativePath(baseDir, file));
					else
						pathText.setText(file.getAbsolutePath());
				}
			}
		});

		pathText.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				changed();
			}

			public void removeUpdate(DocumentEvent e) {
				changed();
			}

			public void insertUpdate(DocumentEvent e) {
				changed();
			}

			public void changed() {
				updatePathLabel();
			}
		});
	}

	private void updatePathLabel() {
		pathTextLabel.setText(baseDir.getAbsolutePath()+File.separator);
		pathTextLabel.setVisible(!new File(pathText.getText()).isAbsolute());
	}

	/**
	 * Save the configuration and close the dialog.
	 */
	public void save() {
		targetText.setText(pathText.getText());
		dispose();
	}

	public static boolean isInSubDirectory(File dir, File file) {
		if (file==null)
			return false;

		if (file.equals(dir))
			return true;

		return isInSubDirectory(dir, file.getParentFile());
	}

	public static String getRelativePath(File dir, File file) {
		return dir.toURI().relativize(file.toURI()).getPath();
	}
}
