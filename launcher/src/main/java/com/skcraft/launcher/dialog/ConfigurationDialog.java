/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.dialog;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.skcraft.concurrency.ObservableFuture;
import com.skcraft.launcher.Configuration;
import com.skcraft.launcher.Instance;
import com.skcraft.launcher.InstanceList;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.dialog.AboutDialog;
import com.skcraft.launcher.dialog.ConsoleFrame;
import com.skcraft.launcher.dialog.LauncherFrame;
import com.skcraft.launcher.persistence.Persistence;
import com.skcraft.launcher.swing.ActionListeners;
import com.skcraft.launcher.swing.FormPanel;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.ObjectSwingMapper;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;
import com.skcraft.launcher.util.SwingExecutor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import lombok.NonNull;
import net.teamfruit.skcraft.launcher.dialog.DirectorySelectionDialog;
import net.teamfruit.skcraft.launcher.dialog.SkinSelectionDialog;
import net.teamfruit.skcraft.launcher.dirs.DirectoryTasks;
import net.teamfruit.skcraft.launcher.dirs.DirectoryUtils;
import net.teamfruit.skcraft.launcher.dirs.LauncherDirectories;
import net.teamfruit.skcraft.launcher.dirs.OptionLauncherDirectories;
import net.teamfruit.skcraft.launcher.discordrpc.DiscordStatus;
import net.teamfruit.skcraft.launcher.discordrpc.LauncherStatus;
import net.teamfruit.skcraft.launcher.skins.LocalSkin;
import net.teamfruit.skcraft.launcher.skins.Skin;
import org.apache.commons.lang.StringUtils;

public class ConfigurationDialog
extends JDialog {
    private static final Logger log = Logger.getLogger(ConfigurationDialog.class.getName());
    private final Launcher launcher;
    private final Configuration config;
    private final ObjectSwingMapper mapper;
    private final JPanel tabContainer = new JPanel(new BorderLayout());
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final FormPanel javaSettingsPanel = new FormPanel();
    private final JTextField jvmPathText = new JTextField();
    private final JTextField jvmArgsText = new JTextField();
    private final JSpinner minMemorySpinner = new JSpinner();
    private final JSpinner maxMemorySpinner = new JSpinner();
    private final JSpinner permGenSpinner = new JSpinner();
    private final FormPanel gameSettingsPanel = new FormPanel();
    private final JCheckBox showConsoleCheck = new JCheckBox(SharedLocale.tr("options.showConsole"));
    private final JSpinner widthSpinner = new JSpinner();
    private final JSpinner heightSpinner = new JSpinner();
    private final JCheckBox serverEnabledCheck = new JCheckBox(SharedLocale.tr("options.serverEnabled"));
    private final JTextField serverHostText = new JTextField();
    private final JSpinner serverPortSpinner = new JSpinner();
    private final FormPanel proxySettingsPanel = new FormPanel();
    private final JCheckBox useProxyCheck = new JCheckBox(SharedLocale.tr("options.useProxyCheck"));
    private final JTextField proxyHostText = new JTextField();
    private final JSpinner proxyPortText = new JSpinner();
    private final JTextField proxyUsernameText = new JTextField();
    private final JPasswordField proxyPasswordText = new JPasswordField();
    private final FormPanel advancedPanel = new FormPanel();
    private final JTextField skinText = new JTextField();
    private final JButton skinButton = new JButton(SharedLocale.tr("options.skinButton"));
    private final JTextField gameKeyText = new JTextField();
    private final JTextField secretKeyText = new JTextField();
    private final JButton secretUnlockButton = new JButton(SharedLocale.tr("options.secretUnlockButton"));
    private final JCheckBox offlineModeEnabledCheck = new JCheckBox(SharedLocale.tr("options.offlineModeEnabled"));
    private final JTextField offlineModePlayerNameText = new JTextField();
    private final LinedBoxPanel buttonsPanel = new LinedBoxPanel(true);
    private final FormPanel pathDirPanel = new FormPanel();
    private final JTextField pathCommonDataDirText = new JTextField();
    private final JTextField pathInstancesDirText = new JTextField();
    private final JButton okButton = new JButton(SharedLocale.tr("button.ok"));
    private final JButton cancelButton = new JButton(SharedLocale.tr("button.cancel"));
    private final JButton aboutButton = new JButton(SharedLocale.tr("options.about"));
    private final JButton logButton = new JButton(SharedLocale.tr("options.launcherConsole"));
    private final JCheckBox moveFilesCheck = new JCheckBox(SharedLocale.tr("options.moveFiles"), true);
    private OptionLauncherDirectories launcherDirs = new OptionLauncherDirectories(){

        @Override
        public File getConfigDir() {
            return ConfigurationDialog.this.launcher.getConfigDir();
        }

        @Override
        public File getBaseDir() {
            return ConfigurationDialog.this.launcher.getBaseDir();
        }

        @Override
        public String getPathCommonDataDir() {
            return ConfigurationDialog.this.pathCommonDataDirText.getText();
        }

        @Override
        public String getPathInstancesDir() {
            return ConfigurationDialog.this.pathInstancesDirText.getText();
        }
    };

    public ConfigurationDialog(Window owner, @NonNull Launcher launcher) {
        super(owner, Dialog.ModalityType.DOCUMENT_MODAL);
        if (launcher == null) {
            throw new NullPointerException("launcher");
        }
        this.launcher = launcher;
        this.config = launcher.getConfig();
        this.mapper = new ObjectSwingMapper(this.config);
        this.setTitle(SharedLocale.tr("options.title"));
        this.initComponents();
        this.setDefaultCloseOperation(2);
        this.setSize(new Dimension(400, 500));
        this.setResizable(false);
        this.setLocationRelativeTo(owner);
        this.mapper.map(this.jvmPathText, "jvmPath");
        this.mapper.map(this.jvmArgsText, "jvmArgs");
        this.mapper.map(this.minMemorySpinner, "minMemory");
        this.mapper.map(this.maxMemorySpinner, "maxMemory");
        this.mapper.map(this.permGenSpinner, "permGen");
        this.mapper.map(this.showConsoleCheck, "showConsole");
        this.mapper.map(this.widthSpinner, "windowWidth");
        this.mapper.map(this.heightSpinner, "widowHeight");
        this.mapper.map(this.serverEnabledCheck, "serverEnabled");
        this.mapper.map(this.serverHostText, "serverHost");
        this.mapper.map(this.serverPortSpinner, "serverPort");
        this.mapper.map(this.useProxyCheck, "proxyEnabled");
        this.mapper.map(this.proxyHostText, "proxyHost");
        this.mapper.map(this.proxyPortText, "proxyPort");
        this.mapper.map(this.proxyUsernameText, "proxyUsername");
        this.mapper.map(this.proxyPasswordText, "proxyPassword");
        this.mapper.map(this.skinText, "skin");
        this.mapper.map(this.gameKeyText, "gameKey");
        this.mapper.map(this.offlineModeEnabledCheck, "offlineModeEnabled");
        this.mapper.map(this.offlineModePlayerNameText, "offlineModePlayerName");
        this.mapper.map(this.pathCommonDataDirText, "pathCommonDataDir");
        this.mapper.map(this.pathInstancesDirText, "pathInstancesDir");
        this.mapper.copyFromObject();
    }

    private void initComponents() {
        this.javaSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.jvmPath")), this.jvmPathText);
        this.javaSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.jvmArguments")), this.jvmArgsText);
        this.javaSettingsPanel.addRow(Box.createVerticalStrut(15));
        this.javaSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.64BitJavaWarning")));
        this.javaSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.minMemory")), this.minMemorySpinner);
        this.javaSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.maxMemory")), this.maxMemorySpinner);
        this.javaSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.permGen")), this.permGenSpinner);
        SwingHelper.removeOpaqueness(this.javaSettingsPanel);
        this.tabbedPane.addTab(SharedLocale.tr("options.javaTab"), SwingHelper.alignTabbedPane(this.javaSettingsPanel));
        this.gameSettingsPanel.addRow(this.showConsoleCheck);
        this.gameSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.windowWidth")), this.widthSpinner);
        this.gameSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.windowHeight")), this.heightSpinner);
        this.gameSettingsPanel.addRow(this.serverEnabledCheck);
        this.gameSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.serverHost")), this.serverHostText);
        this.gameSettingsPanel.addRow(new JLabel(SharedLocale.tr("options.serverPort")), this.serverPortSpinner);
        SwingHelper.removeOpaqueness(this.gameSettingsPanel);
        this.tabbedPane.addTab(SharedLocale.tr("options.minecraftTab"), SwingHelper.alignTabbedPane(this.gameSettingsPanel));
        this.proxySettingsPanel.addRow(this.useProxyCheck);
        this.proxySettingsPanel.addRow(new JLabel(SharedLocale.tr("options.proxyHost")), this.proxyHostText);
        this.proxySettingsPanel.addRow(new JLabel(SharedLocale.tr("options.proxyPort")), this.proxyPortText);
        this.proxySettingsPanel.addRow(new JLabel(SharedLocale.tr("options.proxyUsername")), this.proxyUsernameText);
        this.proxySettingsPanel.addRow(new JLabel(SharedLocale.tr("options.proxyPassword")), this.proxyPasswordText);
        SwingHelper.removeOpaqueness(this.proxySettingsPanel);
        this.tabbedPane.addTab(SharedLocale.tr("options.proxyTab"), SwingHelper.alignTabbedPane(this.proxySettingsPanel));
        this.advancedPanel.addRow(new JLabel(SharedLocale.tr("options.skin")), this.skinButton);
        this.skinText.setEditable(false);
        this.advancedPanel.addRow(this.skinText);
        this.advancedPanel.addRow(new JLabel(SharedLocale.tr("options.secretUnlock")), this.secretUnlockButton);
        this.advancedPanel.addRow(this.secretKeyText);
        this.advancedPanel.addRow(this.offlineModeEnabledCheck);
        this.advancedPanel.addRow(new JLabel(SharedLocale.tr("options.offlineModePlayerName")), this.offlineModePlayerNameText);
        SwingHelper.removeOpaqueness(this.advancedPanel);
        this.tabbedPane.addTab(SharedLocale.tr("options.advancedTab"), SwingHelper.alignTabbedPane(this.advancedPanel));
        File pathCurrentDir = DirectoryUtils.tryCanonical(new File("."));
        JTextField pathCurrentDirText = new JTextField(pathCurrentDir.getAbsolutePath());
        JButton openCurrentDirButton = new JButton(SharedLocale.tr("features.openFolder"));
        this.pathDirPanel.addRow(new JLabel(SharedLocale.tr("options.pathCurrentDir")), openCurrentDirButton);
        this.pathDirPanel.addRow(pathCurrentDirText);
        pathCurrentDirText.setEditable(false);
        File pathBaseDir = DirectoryUtils.tryCanonical(this.launcher.getBaseDir());
        JTextField pathBaseDirText = new JTextField(pathBaseDir.getAbsolutePath());
        JButton openBaseDirButton = new JButton(SharedLocale.tr("features.openFolder"));
        this.pathDirPanel.addRow(new JLabel(SharedLocale.tr("options.pathBaseDir")), openBaseDirButton);
        this.pathDirPanel.addRow(pathBaseDirText);
        pathBaseDirText.setEditable(false);
        JButton pathCommonDataDirButton = new JButton(SharedLocale.tr("options.pathDirButton"));
        this.pathDirPanel.addRow(new JLabel(SharedLocale.tr("options.pathCommonDataDir")), pathCommonDataDirButton);
        this.pathDirPanel.addRow(this.pathCommonDataDirText);
        this.pathCommonDataDirText.setEditable(false);
        JButton pathInstancesDirButton = new JButton(SharedLocale.tr("options.pathDirButton"));
        this.pathDirPanel.addRow(new JLabel(SharedLocale.tr("options.pathInstancesDir")), pathInstancesDirButton);
        this.pathDirPanel.addRow(this.pathInstancesDirText);
        this.pathInstancesDirText.setEditable(false);
        this.pathDirPanel.addRow(this.moveFilesCheck);
        SwingHelper.removeOpaqueness(this.pathDirPanel);
        this.tabbedPane.addTab(SharedLocale.tr("options.pathDirTab"), SwingHelper.alignTabbedPane(this.pathDirPanel));
        this.buttonsPanel.addElement(this.logButton);
        this.buttonsPanel.addElement(this.aboutButton);
        this.buttonsPanel.addGlue();
        this.buttonsPanel.addElement(this.okButton);
        this.buttonsPanel.addElement(this.cancelButton);
        this.tabContainer.add((Component)this.tabbedPane, "Center");
        this.tabContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add((Component)this.tabContainer, "Center");
        this.add((Component)this.buttonsPanel, "South");
        SwingHelper.equalWidth(this.okButton, this.cancelButton);
        openCurrentDirButton.addActionListener(ActionListeners.browseDir(this, pathCurrentDir, true));
        openBaseDirButton.addActionListener(ActionListeners.browseDir(this, pathBaseDir, true));
        this.cancelButton.addActionListener(ActionListeners.dispose(this));
        this.aboutButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog.showAboutDialog(ConfigurationDialog.this);
            }
        });
        this.okButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigurationDialog.this.save();
            }
        });
        this.logButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ConsoleFrame.showMessages();
            }
        });
        this.skinButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                new SkinSelectionDialog(ConfigurationDialog.this, ConfigurationDialog.this.launcher, ConfigurationDialog.this.skinText).setVisible(true);
            }
        });
        pathCommonDataDirButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                new DirectorySelectionDialog(ConfigurationDialog.this, ConfigurationDialog.this.launcher.getDirectories(), ConfigurationDialog.this.pathCommonDataDirText, SharedLocale.tr("options.pathCommonDataDirTitle"), ConfigurationDialog.this.launcherDirs.getDefaultCommonDataDir(), null).setVisible(true);
            }
        });
        pathInstancesDirButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                new DirectorySelectionDialog(ConfigurationDialog.this, ConfigurationDialog.this.launcher.getDirectories(), ConfigurationDialog.this.pathInstancesDirText, SharedLocale.tr("options.pathInstancesDirTitle"), ConfigurationDialog.this.launcherDirs.getDefaultInstancesDir(), "instances").setVisible(true);
            }
        });
        Window owner = this.getOwner();
        if (owner instanceof LauncherFrame) {
            final LauncherFrame frame = (LauncherFrame)owner;
            this.secretUnlockButton.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    String runid = ConfigurationDialog.this.secretKeyText.getText();
                    Instance runinstance = null;
                    for (Instance instance : ConfigurationDialog.this.launcher.getInstances().getInstancesSecret()) {
                        if (!StringUtils.equalsIgnoreCase(runid, instance.getKey())) continue;
                        runinstance = instance;
                        break;
                    }
                    if (runinstance != null) {
                        ConfigurationDialog.this.dispose();
                        log.info("Launching " + runinstance.getName());
                        frame.loadInstances();
                        frame.launch(runinstance);
                    } else {
                        log.warning("Unable to find " + runid);
                        SwingHelper.showErrorDialog(frame, SharedLocale.tr("errors.missingInstance", runid), SharedLocale.tr("errors.missingInstanceTitle", runid));
                    }
                }
            });
        } else {
            this.secretUnlockButton.setEnabled(false);
        }
        LauncherStatus.instance.open(DiscordStatus.CONFIG, new LauncherStatus.WindowDisablable(this), ImmutableMap.<String, String>of());
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowActivated(WindowEvent e) {
                LauncherStatus.instance.update();
            }
        });
    }

    public void save() {
        Runnable saveAndClose = new Runnable(){

            @Override
            public void run() {
                String oldskin = ConfigurationDialog.this.config.getSkin();
                ConfigurationDialog.this.mapper.copyFromSwing();
                String newskin = ConfigurationDialog.this.config.getSkin();
                if (!StringUtils.equals(oldskin, ConfigurationDialog.this.config.getSkin())) {
                    LocalSkin localSkin = new LocalSkin(ConfigurationDialog.this.launcher, newskin);
                    Skin skin = localSkin.getSkin();
                    Window owner = ConfigurationDialog.this.getOwner();
                    if (owner instanceof LauncherFrame) {
                        LauncherFrame frame = (LauncherFrame)owner;
                        frame.updateSkin(skin);
                    } else {
                        ConfigurationDialog.this.launcher.setSkin(skin);
                    }
                }
                Persistence.commitAndForget(ConfigurationDialog.this.config);
                ConfigurationDialog.this.dispose();
            }
        };
        if (this.moveFilesCheck.isSelected()) {
            this.moveFiles(saveAndClose);
        } else {
            saveAndClose.run();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        LauncherStatus.instance.close(DiscordStatus.CONFIG);
    }

    public void moveFiles(final Runnable callback) {
        File commonDataDirSrc = DirectoryUtils.tryCanonical(this.launcher.getCommonDataDir());
        File commonDataDirDest = DirectoryUtils.tryCanonical(this.launcherDirs.getCommonDataDir());
        File commonAssetsDirSrc = DirectoryUtils.tryCanonical(this.launcher.getAssetsDir());
        File commonAssetsDirDest = DirectoryUtils.tryCanonical(this.launcherDirs.getAssetsDir());
        File commonLibrariesDirSrc = DirectoryUtils.tryCanonical(this.launcher.getLibrariesDir());
        File commonLibrariesDirDest = DirectoryUtils.tryCanonical(this.launcherDirs.getLibrariesDir());
        File commonVersionsDirSrc = DirectoryUtils.tryCanonical(this.launcher.getVersionsDir());
        File commonVersionsDirDest = DirectoryUtils.tryCanonical(this.launcherDirs.getVersionsDir());
        File instancesDirSrc = DirectoryUtils.tryCanonical(this.launcher.getInstancesDir());
        File instancesDirDest = DirectoryUtils.tryCanonical(this.launcherDirs.getInstancesDir());
        if (!(commonDataDirSrc.equals(commonDataDirDest) || DirectoryUtils.checkMovable(this, commonAssetsDirSrc, commonAssetsDirDest) && DirectoryUtils.checkMovable(this, commonLibrariesDirSrc, commonLibrariesDirDest) && DirectoryUtils.checkMovable(this, commonVersionsDirSrc, commonVersionsDirDest))) {
            return;
        }
        if (!instancesDirSrc.equals(instancesDirDest) && !DirectoryUtils.checkMovable(this, instancesDirSrc, instancesDirDest)) {
            return;
        }
        DirectoryTasks tasks = new DirectoryTasks(this.launcher);
        ArrayList<ObservableFuture<File>> futures = Lists.newArrayList();
        if (!commonDataDirSrc.equals(commonDataDirDest)) {
            if (!commonAssetsDirSrc.equals(commonAssetsDirDest) && commonAssetsDirSrc.isDirectory()) {
                futures.add(tasks.move(this, commonAssetsDirSrc, commonAssetsDirDest));
            }
            if (!commonLibrariesDirSrc.equals(commonLibrariesDirDest) && commonLibrariesDirSrc.isDirectory()) {
                futures.add(tasks.move(this, commonLibrariesDirSrc, commonLibrariesDirDest));
            }
            if (!commonVersionsDirSrc.equals(commonVersionsDirDest) && commonVersionsDirSrc.isDirectory()) {
                futures.add(tasks.move(this, commonVersionsDirSrc, commonVersionsDirDest));
            }
        }
        if (!instancesDirSrc.equals(instancesDirDest) && instancesDirSrc.isDirectory()) {
            futures.add(tasks.move(this, instancesDirSrc, instancesDirDest));
        }
        Futures.addCallback(Futures.allAsList(futures), new FutureCallback<List<File>>(){

            @Override
            public void onSuccess(List<File> result) {
                callback.run();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        }, SwingExecutor.INSTANCE);
    }

}

