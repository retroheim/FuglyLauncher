/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.dialog;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.skcraft.concurrency.ObservableFuture;
import com.skcraft.launcher.Configuration;
import com.skcraft.launcher.FancyBackgroundPanel;
import com.skcraft.launcher.Instance;
import com.skcraft.launcher.InstanceList;
import com.skcraft.launcher.InstanceTasks;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.LauncherArguments;
import com.skcraft.launcher.dialog.ConfigurationDialog;
import com.skcraft.launcher.dialog.ProgressDialog;
import com.skcraft.launcher.launch.LaunchListener;
import com.skcraft.launcher.launch.LaunchOptions;
import com.skcraft.launcher.launch.LaunchSupervisor;
import com.skcraft.launcher.swing.ActionListeners;
import com.skcraft.launcher.swing.DoubleClickToButtonAdapter;
import com.skcraft.launcher.swing.InstanceTable;
import com.skcraft.launcher.swing.InstanceTableModel;
import com.skcraft.launcher.swing.PopupMouseAdapter;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.swing.WebpagePanel;
import com.skcraft.launcher.update.UpdateManager;
import com.skcraft.launcher.util.SharedLocale;
import com.skcraft.launcher.util.SwingExecutor;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.table.TableModel;
import lombok.NonNull;
import net.teamfruit.skcraft.launcher.Tip;
import net.teamfruit.skcraft.launcher.TipList;
import net.teamfruit.skcraft.launcher.appicon.AppIcon;
import net.teamfruit.skcraft.launcher.discordrpc.DiscordStatus;
import net.teamfruit.skcraft.launcher.discordrpc.LauncherStatus;
import net.teamfruit.skcraft.launcher.integration.AppleHandler;
import net.teamfruit.skcraft.launcher.skins.LocalSkin;
import net.teamfruit.skcraft.launcher.skins.RemoteSkin;
import net.teamfruit.skcraft.launcher.skins.RemoteSkinList;
import net.teamfruit.skcraft.launcher.skins.Skin;
import net.teamfruit.skcraft.launcher.skins.SkinUtils;
import net.teamfruit.skcraft.launcher.swing.BoardPanel;
import net.teamfruit.skcraft.launcher.swing.InstanceCellFactory;
import net.teamfruit.skcraft.launcher.swing.InstanceCellPanel;
import net.teamfruit.skcraft.launcher.swing.ServerInfoStyle;
import net.teamfruit.skcraft.launcher.swing.TipsPanel;
import net.teamfruit.skcraft.launcher.swing.WebpageScrollBarUI;
import net.teamfruit.skcraft.launcher.util.SharedLocaleUpdater;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

public class LauncherFrame
extends JFrame {
    private static final Logger log = Logger.getLogger(LauncherFrame.class.getName());
    private final Launcher launcher;
    private final SharedLocaleUpdater localeUpdater = SharedLocaleUpdater.create();
    private JPanel container;
    private final InstanceTable instancesTable = new InstanceTable();
    private final InstanceTableModel instancesModel;
    private final JScrollPane instanceScroll = new JScrollPane(this.instancesTable);
    private WebpagePanel webView;
    private BoardPanel<InstanceCellPanel> selectedPane;
    private JPanel splitPane;
    private final JButton launchButton = this.localeUpdater.tr(new JButton(), "launcher.launch", new Object[0]);
    private final JButton refreshButton = this.localeUpdater.tr(new JButton(), "launcher.checkForUpdates", new Object[0]);
    private final JButton optionsButton = this.localeUpdater.tr(new JButton(), "launcher.options", new Object[0]);
    private final JCheckBox updateCheck = this.localeUpdater.tr(new JCheckBox(), "launcher.downloadUpdates", new Object[0]);
    private boolean firstLoaded = true;
    private JPanel rightPane;

    public LauncherFrame(@NonNull Launcher launcher) {
        if (launcher == null) {
            throw new NullPointerException("launcher");
        }
        this.localeUpdater.tr(this, "launcher.title", launcher.getVersion());
        this.launcher = launcher;
        this.instancesModel = new InstanceTableModel(launcher.getInstances());
        AppleHandler.register(launcher, this);
        this.setDefaultCloseOperation(3);
        this.setMinimumSize(new Dimension(750, 500));
        this.initComponents();
        this.pack();
        this.setLocationRelativeTo(null);
        AppIcon.setFrameIconSet(this, AppIcon.getSwingIconSet(AppIcon.getAppIconSet()));
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowActivated(WindowEvent e) {
                LauncherStatus.instance.update();
            }
        });
    }

    private void setExpand(boolean visible) {
        this.rightPane.setVisible(visible);
        int rightWidth = this.rightPane.getWidth();
        this.setMinimumSize(new Dimension(visible ? 750 : 500, 500));
        this.setSize(new Dimension(visible ? 750 : 750 - rightWidth, this.getHeight()));
    }

    private void loadTips() {
        ObservableFuture<TipList> future = this.launcher.getInstanceTasks().reloadTips(this);
        Futures.addCallback(future, new FutureCallback<TipList>(){

            @Override
            public void onSuccess(TipList result) {
                if (result != null) {
                    TipsPanel.instance.updateTipList(result.getTipList());
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        }, SwingExecutor.INSTANCE);
    }

    private void loadSkinList() {
        SkinUtils.loadSkinList(this, this.launcher, false, new Predicate<RemoteSkinList>(){

            @Override
            public boolean apply(RemoteSkinList remoteSkinList) {
                if (remoteSkinList != null) {
                    String skinname = LauncherFrame.this.launcher.getConfig().getSkin();
                    RemoteSkin remoteSkin = remoteSkinList.getRemoteSkin(skinname);
                    SkinUtils.loadSkin(LauncherFrame.this, LauncherFrame.this.launcher, false, remoteSkin, new Predicate<RemoteSkin>(){

                        @Override
                        public boolean apply(RemoteSkin remoteSkin) {
                            LocalSkin localSkin;
                            if (remoteSkin != null && (localSkin = remoteSkin.getLocalSkin()) != null) {
                                Skin skin = localSkin.getSkin();
                                LauncherFrame.this.updateSkin(skin);
                            }
                            return true;
                        }
                    });
                }
                return true;
            }

        });
    }

    public void updateSkin(Skin skin) {
        if (skin != null && !skin.equals(this.launcher.getSkin())) {
            this.launcher.setSkin(skin);
            this.localeUpdater.update();
            this.webView.browse(this.launcher.getNewsURL(), false);
            this.initSkin(skin);
            this.repaint();
        }
    }

    public void initSkin(Skin skin) {
        this.setExpand(skin.isShowList());
        this.loadTips();
        this.loadInstances();
    }

    private void initComponents() {
        this.setResizable(false);
        this.container = this.createContainerPanel();
        this.container.setBackground(Color.WHITE);
        this.container.setLayout(new BorderLayout());
        this.webView = this.createNewsPanel();
        this.webView.setBrowserBorder(BorderFactory.createEmptyBorder());
        this.webView.setPreferredSize(new Dimension(250, 250));
        JScrollPane webViewScroll = this.webView.getDocumentScroll();
        webViewScroll.getVerticalScrollBar().setUI(new WebpageScrollBarUI(webViewScroll));
        webViewScroll.getHorizontalScrollBar().setUI(new WebpageScrollBarUI(webViewScroll));
        this.splitPane = new JPanel(new BorderLayout());
        this.selectedPane = new BoardPanel();
        this.selectedPane.setOpaque(false);
        this.selectedPane.setPreferredSize(new Dimension(250, 60));
        this.localeUpdater.trTooltip(this.selectedPane, "launcher.launchButton", new Object[0]);
        this.launcher.getUpdateManager().addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("readyUpdate") && !BooleanUtils.toBoolean(System.getProperty("com.skcraft.launcher.noupdate"))) {
                    LauncherFrame.this.launcher.getUpdateManager().performUpdate(LauncherFrame.this);
                }
            }
        });
        this.updateCheck.setSelected(true);
        this.instancesTable.setModel(this.instancesModel);
        this.instanceScroll.setPreferredSize(new Dimension(250, this.instanceScroll.getPreferredSize().height));
        this.instanceScroll.getVerticalScrollBar().setUI(new WebpageScrollBarUI(this.instanceScroll));
        this.instanceScroll.getHorizontalScrollBar().setUI(new WebpageScrollBarUI(this.instanceScroll));
        this.instanceScroll.setBorder(BorderFactory.createEmptyBorder());
        this.launchButton.setFont(this.launchButton.getFont().deriveFont(1));
        JButton expandButton = new JButton(">");
        JPanel buttons = new JPanel(new GridLayout(3, 1));
        this.refreshButton.setIcon(SwingHelper.createIcon(Launcher.class, "refresh_icon.png", 20, 20));
        this.localeUpdater.trTooltip(this.refreshButton, "launcher.refreshButton", new Object[0]);
        this.refreshButton.setText(null);
        buttons.add(this.refreshButton);
        this.optionsButton.setIcon(SwingHelper.createIcon(Launcher.class, "settings_icon.png", 20, 20));
        this.localeUpdater.trTooltip(this.optionsButton, "launcher.optionButton", new Object[0]);
        this.optionsButton.setText(null);
        buttons.add(this.optionsButton);
        expandButton.setIcon(SwingHelper.createIcon(Launcher.class, "expand_icon.png", 20, 20));
        this.localeUpdater.trTooltip(expandButton, "launcher.expandButton", new Object[0]);
        expandButton.setText(null);
        buttons.add(expandButton);
        SwingHelper.removeOpaqueness(buttons);
        JPanel leftBottomTopPanel = new JPanel(new GridBagLayout());
        JLabel leftBottomTopText = this.localeUpdater.tr(new JLabel(), "launcher.launchTitle", new Object[0]);
        leftBottomTopText.setFont(new Font(leftBottomTopText.getFont().getName(), 0, 16));
        leftBottomTopText.setForeground(Color.WHITE);
        leftBottomTopPanel.add(leftBottomTopText);
        leftBottomTopPanel.setUI(new BasicPanelUI());
        leftBottomTopPanel.setBackground(new Color(32, 30, 98));
        JPanel leftBottomPane = new JPanel(new BorderLayout());
        leftBottomPane.add((Component)leftBottomTopPanel, "North");
        leftBottomPane.add(this.selectedPane, "Center");
        leftBottomPane.add((Component)buttons, "East");
        leftBottomPane.setOpaque(false);
        JPanel leftPane = new JPanel(new BorderLayout());
        leftPane.add((Component)this.webView, "Center");
        leftPane.add((Component)leftBottomPane, "South");
        leftPane.setOpaque(false);
        JLabel instanceLabel = this.localeUpdater.tr(new JLabel(SwingHelper.createIcon(Launcher.class, "package_icon.png", 20, 20), 2), "launcher.instance", new Object[0]);
        instanceLabel.setFont(new Font(instanceLabel.getFont().getName(), 0, 16));
        instanceLabel.setBorder(new EmptyBorder(3, 3, 3, 3));
        instanceLabel.setForeground(Color.WHITE);
        this.rightPane = new JPanel(new BorderLayout()){

            @Override
            protected void paintComponent(Graphics g) {
                if (!this.isOpaque()) {
                    Graphics2D g2d = (Graphics2D)g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setComposite(AlphaComposite.getInstance(3, 0.3f));
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };
        this.rightPane.add((Component)instanceLabel, "North");
        this.rightPane.add((Component)this.instanceScroll, "Center");
        SwingHelper.removeOpaqueness(this.instanceScroll);
        this.rightPane.setOpaque(false);
        this.splitPane.add((Component)leftPane, "Center");
        this.splitPane.add((Component)this.rightPane, "East");
        this.splitPane.setOpaque(false);
        expandButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean visible = !LauncherFrame.this.rightPane.isVisible();
                LauncherFrame.this.setExpand(visible);
            }
        });
        this.container.add((Component)this.splitPane, "Center");
        this.add((Component)this.container, "Center");
        this.instancesModel.addTableModelListener(new TableModelListener(){

            @Override
            public void tableChanged(TableModelEvent e) {
                if (LauncherFrame.this.instancesTable.getRowCount() > 0) {
                    LauncherFrame.this.instancesTable.setRowSelectionInterval(0, 0);
                }
            }
        });
        final Cursor cursorhand = Cursor.getPredefinedCursor(12);
        final Cursor cursornormal = Cursor.getPredefinedCursor(0);
        this.selectedPane.setCursor(cursorhand);
        this.selectedPane.addMouseListener((MouseListener)new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                LauncherFrame.this.launchButton.doClick();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                InstanceCellPanel panel = (InstanceCellPanel)LauncherFrame.this.selectedPane.get();
                if (panel != null) {
                    panel.setShowSelected(true);
                    LauncherFrame.this.selectedPane.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                InstanceCellPanel panel = (InstanceCellPanel)LauncherFrame.this.selectedPane.get();
                if (panel != null) {
                    panel.setShowSelected(false);
                    LauncherFrame.this.selectedPane.repaint();
                }
            }
        });
        this.instancesTable.addMouseListener(new DoubleClickToButtonAdapter(this.launchButton));
        this.instancesTable.addMouseMotionListener(new MouseAdapter(){

            @Override
            public void mouseMoved(MouseEvent e) {
                int i = LauncherFrame.this.instancesTable.rowAtPoint(e.getPoint());
                if (i >= 0) {
                    LauncherFrame.this.instancesTable.setCursor(cursorhand);
                } else {
                    LauncherFrame.this.instancesTable.setCursor(cursornormal);
                }
            }
        });
        this.instancesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int index = LauncherFrame.this.instancesTable.getSelectionModel().getLeadSelectionIndex();
                    if (index < 0) {
                        index = 0;
                    }
                    Instance instance = LauncherFrame.this.instancesModel.getValueAt(index, 0);
                    InstanceCellPanel tablecell = InstanceCellFactory.instance.getCellComponent(LauncherFrame.this.selectedPane, instance, false, ServerInfoStyle.NORMAL);
                    tablecell.setShowPlayIcon(true);
                    LauncherFrame.this.selectedPane.set(tablecell);
                }
            }
        });
        this.refreshButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LauncherFrame.this.loadInstances();
                LauncherFrame.this.launcher.getUpdateManager().checkForUpdate();
                LauncherFrame.this.webView.browse(LauncherFrame.this.launcher.getNewsURL(), false);
            }
        });
        this.optionsButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LauncherFrame.this.showOptions();
            }
        });
        this.launchButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LauncherFrame.this.launch();
            }
        });
        this.instancesTable.addMouseListener(new PopupMouseAdapter(){

            @Override
            protected void showPopup(MouseEvent e) {
                int index = LauncherFrame.this.instancesTable.rowAtPoint(e.getPoint());
                Instance selected = null;
                if (index >= 0) {
                    LauncherFrame.this.instancesTable.setRowSelectionInterval(index, index);
                    selected = LauncherFrame.this.launcher.getInstances().get(index);
                }
                LauncherFrame.this.popupInstanceMenu(e.getComponent(), e.getX(), e.getY(), selected);
            }
        });
        this.loadSkinList();
        this.initSkin(this.launcher.getSkin());
    }

    protected JPanel createContainerPanel() {
        return new FancyBackgroundPanel(this.launcher);
    }

    protected WebpagePanel createNewsPanel() {
        return WebpagePanel.forURL(this.launcher.getNewsURL(), false);
    }

    private void popupInstanceMenu(Component component, int x, int y, final Instance selected) {
        JMenuItem menuItem;
        JPopupMenu popup = new JPopupMenu();
        if (selected != null) {
            menuItem = new JMenuItem(!selected.isLocal() ? SharedLocale.tr("instance.install") : SharedLocale.tr("instance.launch"));
            menuItem.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    LauncherFrame.this.launch();
                }
            });
            popup.add(menuItem);
            if (selected.isLocal()) {
                popup.addSeparator();
                menuItem = new JMenuItem(SharedLocale.tr("instance.openFolder"));
                menuItem.addActionListener(ActionListeners.browseDir(this, selected.getContentDir(), true));
                popup.add(menuItem);
                menuItem = new JMenuItem(SharedLocale.tr("instance.openMods"));
                menuItem.addActionListener(ActionListeners.browseDir(this, new File(selected.getContentDir(), "mods"), true));
                popup.add(menuItem);
                menuItem = new JMenuItem(SharedLocale.tr("instance.openConfig"));
                menuItem.addActionListener(ActionListeners.browseDir(this, new File(selected.getContentDir(), "config"), true));
                popup.add(menuItem);
                menuItem = new JMenuItem(SharedLocale.tr("instance.openSaves"));
                menuItem.addActionListener(ActionListeners.browseDir(this, new File(selected.getContentDir(), "saves"), true));
                popup.add(menuItem);
                menuItem = new JMenuItem(SharedLocale.tr("instance.openResourcePacks"));
                menuItem.addActionListener(ActionListeners.browseDir(this, new File(selected.getContentDir(), "resourcepacks"), true));
                popup.add(menuItem);
                menuItem = new JMenuItem(SharedLocale.tr("instance.openScreenshots"));
                menuItem.addActionListener(ActionListeners.browseDir(this, new File(selected.getContentDir(), "screenshots"), true));
                popup.add(menuItem);
                menuItem = new JMenuItem(SharedLocale.tr("instance.copyAsPath"));
                menuItem.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        File dir = selected.getContentDir();
                        dir.mkdirs();
                        SwingHelper.setClipboard(dir.getAbsolutePath());
                    }
                });
                popup.add(menuItem);
                popup.addSeparator();
                if (!selected.isUpdatePending()) {
                    menuItem = new JMenuItem(SharedLocale.tr("instance.forceUpdate"));
                    menuItem.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            selected.setUpdatePending(true);
                            LauncherFrame.this.launch();
                            LauncherFrame.this.instancesModel.update();
                        }
                    });
                    popup.add(menuItem);
                }
                menuItem = new JMenuItem(SharedLocale.tr("instance.hardForceUpdate"));
                menuItem.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        LauncherFrame.this.confirmHardUpdate(selected);
                    }
                });
                popup.add(menuItem);
                menuItem = new JMenuItem(SharedLocale.tr("instance.deleteFiles"));
                menuItem.addActionListener(new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        LauncherFrame.this.confirmDelete(selected);
                    }
                });
                popup.add(menuItem);
            }
            popup.addSeparator();
        }
        menuItem = new JMenuItem(SharedLocale.tr("launcher.refreshList"));
        menuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LauncherFrame.this.loadInstances();
            }
        });
        popup.add(menuItem);
        popup.show(component, x, y);
    }

    private void onInstanceReady() {
        if (this.firstLoaded) {
            this.firstLoaded = false;
            LauncherStatus.instance.open(DiscordStatus.MENU, new LauncherStatus.WindowDisablable(this), ImmutableMap.<String, String>of());
            this.processRun();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        LauncherStatus.instance.close(DiscordStatus.MENU);
    }

    public boolean processRun() {
        this.launcher.getOptions().processURI();
        String runid = this.launcher.getOptions().getRun();
        String runkey = this.launcher.getOptions().getKey();
        if (this.launchFromID(runid, runkey, true)) {
            return true;
        }
        String skinrunid = this.launcher.getSkin().getLoginModPack();
        if (this.launchFromID(skinrunid, null, false)) {
            return true;
        }
        return false;
    }

    private boolean launchFromID(String runid, String runkey, boolean keyRequired) {
        if (!StringUtils.isEmpty(runid)) {
            log.info("Trying to launch " + runid);
            Instance runinstance = null;
            for (Instance instance : this.launcher.getInstances().getInstances()) {
                if (!StringUtils.equalsIgnoreCase(runid, instance.getName()) && !StringUtils.equalsIgnoreCase(runid, instance.getKey())) continue;
                runinstance = instance;
                break;
            }
            if (runinstance == null) {
                for (Instance instance : this.launcher.getInstances().getInstancesSecret()) {
                    if (!StringUtils.equalsIgnoreCase(runid, instance.getKey()) && (!StringUtils.equalsIgnoreCase(runid, instance.getName()) || keyRequired && !StringUtils.equalsIgnoreCase(runkey, instance.getKey()))) continue;
                    runinstance = instance;
                    break;
                }
            }
            if (runinstance != null) {
                log.info("Launching " + runinstance.getName());
                this.launch(runinstance);
                return true;
            }
            log.warning("Unable to find " + runid);
            SwingHelper.showErrorDialog(this, SharedLocale.tr("errors.missingInstance", runid), SharedLocale.tr("errors.missingInstanceTitle", runid));
        }
        return false;
    }

    private void selectInstance(String instanceName) {
        InstanceList list = this.instancesModel.getInstances();
        if (list.size() > 0) {
            int findindex = 0;
            if (!StringUtils.isEmpty(instanceName)) {
                for (int index = 0; index < list.size(); ++index) {
                    Instance instance = list.get(index);
                    if (!StringUtils.equals(instance.getName(), instanceName)) continue;
                    findindex = index;
                    break;
                }
            }
            if (findindex >= 0) {
                this.instancesTable.setRowSelectionInterval(findindex, findindex);
            }
        }
    }

    private void confirmDelete(Instance instance) {
        if (!SwingHelper.confirmDialog(this, SharedLocale.tr("instance.confirmDelete", instance.getTitle()), SharedLocale.tr("confirmTitle"))) {
            return;
        }
        ObservableFuture<Instance> future = this.launcher.getInstanceTasks().delete(this, instance);
        future.addListener(new Runnable(){

            @Override
            public void run() {
                LauncherFrame.this.loadInstances();
            }
        }, SwingExecutor.INSTANCE);
    }

    private void confirmHardUpdate(Instance instance) {
        if (!SwingHelper.confirmDialog(this, SharedLocale.tr("instance.confirmHardUpdate"), SharedLocale.tr("confirmTitle"))) {
            return;
        }
        ObservableFuture<Instance> future = this.launcher.getInstanceTasks().hardUpdate(this, instance);
        future.addListener(new Runnable(){

            @Override
            public void run() {
                LauncherFrame.this.launch();
                LauncherFrame.this.instancesModel.update();
            }
        }, SwingExecutor.INSTANCE);
    }

    public void loadInstances() {
        ObservableFuture<InstanceList> future = this.launcher.getInstanceTasks().reloadInstances(this);
        future.addListener(new Runnable(){

            @Override
            public void run() {
                String defaultModPack = LauncherFrame.this.launcher.getSkin().getSelectModPack();
                LauncherFrame.this.instancesModel.getInstances().unlock(defaultModPack);
                LauncherFrame.this.instancesModel.update();
                LauncherFrame.this.selectInstance(defaultModPack);
                LauncherFrame.this.onInstanceReady();
                LauncherFrame.this.requestFocus();
            }
        }, SwingExecutor.INSTANCE);
        ProgressDialog.showProgress(this, future, SharedLocale.tr("launcher.checkingTitle"), SharedLocale.tr("launcher.checkingStatus"));
        SwingHelper.addErrorDialogCallback(this, future);
    }

    private void showOptions() {
        ConfigurationDialog configDialog = new ConfigurationDialog(this, this.launcher);
        configDialog.setVisible(true);
    }

    public void launch() {
        this.launch(this.launcher.getInstances().get(this.instancesTable.getSelectedRow()));
    }

    public void launch(Instance instance) {
        boolean permitUpdate;
        LaunchOptions options = new LaunchOptions.Builder().setInstance(instance).setListener(new LaunchListenerImpl(this)).setUpdatePolicy((permitUpdate = this.updateCheck.isSelected()) ? LaunchOptions.UpdatePolicy.UPDATE_IF_SESSION_ONLINE : LaunchOptions.UpdatePolicy.NO_UPDATE).setWindow(this).build();
        this.launcher.getLaunchSupervisor().launch(options);
    }

    public InstanceTable getInstancesTable() {
        return this.instancesTable;
    }

    public JScrollPane getInstanceScroll() {
        return this.instanceScroll;
    }

    public boolean isFirstLoaded() {
        return this.firstLoaded;
    }

    private static class LaunchListenerImpl
    implements LaunchListener {
        private final WeakReference<LauncherFrame> frameRef;
        private final Launcher launcher;

        private LaunchListenerImpl(LauncherFrame frame) {
            this.frameRef = new WeakReference<LauncherFrame>(frame);
            this.launcher = frame.launcher;
        }

        @Override
        public void instancesUpdated() {
            LauncherFrame frame = this.frameRef.get();
            if (frame != null) {
                frame.loadInstances();
            }
        }

        @Override
        public void gameStarted() {
            LauncherFrame frame = this.frameRef.get();
            if (frame != null) {
                frame.dispose();
            }
        }

        @Override
        public void gameClosed() {
            this.launcher.showLauncherWindow();
        }
    }

}

