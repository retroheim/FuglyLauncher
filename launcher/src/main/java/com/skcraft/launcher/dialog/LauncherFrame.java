/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.dialog;

import static com.skcraft.launcher.util.SharedLocale.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.ref.WeakReference;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.skcraft.concurrency.ObservableFuture;
import com.skcraft.launcher.Instance;
import com.skcraft.launcher.InstanceList;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.launch.LaunchListener;
import com.skcraft.launcher.launch.LaunchOptions;
import com.skcraft.launcher.launch.LaunchOptions.UpdatePolicy;
import com.skcraft.launcher.swing.ActionListeners;
import com.skcraft.launcher.swing.InstanceTable;
import com.skcraft.launcher.swing.InstanceTableModel;
import com.skcraft.launcher.swing.PopupMouseAdapter;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.swing.WebpagePanel;
import com.skcraft.launcher.util.SharedLocale;
import com.skcraft.launcher.util.SwingExecutor;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import net.miginfocom.swing.MigLayout;

/**
 * The main launcher frame.
 */
@Log
public class LauncherFrame extends JFrame {

    private final Launcher launcher;

    @Getter
    private final InstanceTable instancesTable = new InstanceTable();
    private final InstanceTableModel instancesModel;
    @Getter
    private final JScrollPane instanceScroll = new JScrollPane(this.instancesTable);
    private WebpagePanel webView;
    private JPanel splitPane;
    private final JButton launchButton = new JButton(SharedLocale.tr("launcher.launch"));
    private final JButton refreshButton = new JButton(SharedLocale.tr("launcher.checkForUpdates"));
    private final JButton optionsButton = new JButton(SharedLocale.tr("launcher.options"));
    private final JButton selfUpdateButton = new JButton(SharedLocale.tr("launcher.updateLauncher"));
    private final JCheckBox updateCheck = new JCheckBox(SharedLocale.tr("launcher.downloadUpdates"));

    /**
     * Create a new frame.
     *
     * @param launcher the launcher
     */
    public LauncherFrame(@NonNull final Launcher launcher) {
        super(tr("launcher.title", launcher.getVersion()));

        this.launcher = launcher;
        this.instancesModel = new InstanceTableModel(launcher.getInstances());

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        initComponents();
        pack();
        setLocationRelativeTo(null);

        SwingHelper.setFrameIcon(this, Launcher.class, "icon.png");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadInstances();
            }
        });
    }

    private void initComponents() {
        final JPanel container = createContainerPanel();
        container.setLayout(new MigLayout("fill, insets dialog", "[][]push[][]", "[grow][]"));

        this.webView = createNewsPanel();

        this.splitPane = new JPanel(new BorderLayout(4, 4));
        this.selfUpdateButton.setVisible(this.launcher.getUpdateManager().getPendingUpdate());

        this.launcher.getUpdateManager().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("pendingUpdate"))
					LauncherFrame.this.selfUpdateButton.setVisible((Boolean) evt.getNewValue());
            }
        });

        this.updateCheck.setSelected(true);
        this.instancesTable.setModel(this.instancesModel);
        this.instanceScroll.setPreferredSize(new Dimension(250, this.instanceScroll.getPreferredSize().height));
        this.launchButton.setFont(this.launchButton.getFont().deriveFont(Font.BOLD));
        this.splitPane.add(this.webView, BorderLayout.CENTER);
        this.splitPane.add(this.instanceScroll, BorderLayout.EAST);
        this.splitPane.setOpaque(false);
        container.add(this.splitPane, "grow, wrap, span 5, gapbottom unrel, w null:680, h null:350");
        // SwingHelper.flattenJSplitPane(this.splitPane);
        container.add(this.refreshButton);
        container.add(this.updateCheck);
        container.add(this.selfUpdateButton);
        container.add(this.optionsButton);
        container.add(this.launchButton);

        add(container, BorderLayout.CENTER);

        this.instancesModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(final TableModelEvent e) {
                if (LauncherFrame.this.instancesTable.getRowCount() > 0)
					LauncherFrame.this.instancesTable.setRowSelectionInterval(0, 0);
            }
        });

		final Cursor cursorhand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		final Cursor cursornormal = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		this.instancesTable.addMouseListener(new MouseAdapter() {
			private int lastSelected;

			@Override
			public void mouseClicked(final MouseEvent e) {
				final int i = LauncherFrame.this.instancesTable.rowAtPoint(e.getPoint());
				if (e.getButton()==MouseEvent.BUTTON1) {
					final int j = this.lastSelected;
					log.info(i+"*"+j);
					if (i==j)
						LauncherFrame.this.launchButton.doClick();
				}
				this.lastSelected = LauncherFrame.this.instancesTable.getSelectedRow();
			}
		});
		this.instancesTable.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(final MouseEvent e) {
				final int i = LauncherFrame.this.instancesTable.rowAtPoint(e.getPoint());
                if (i>=0)
					LauncherFrame.this.instancesTable.setCursor(cursorhand);
				else
					LauncherFrame.this.instancesTable.setCursor(cursornormal);
			}
		});

        this.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                loadInstances();
                LauncherFrame.this.launcher.getUpdateManager().checkForUpdate();
                LauncherFrame.this.webView.browse(LauncherFrame.this.launcher.getNewsURL(), false);
            }
        });

        this.selfUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                LauncherFrame.this.launcher.getUpdateManager().performUpdate(LauncherFrame.this);
            }
        });

        this.optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showOptions();
            }
        });

        this.launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                launch();
            }
        });

        this.instancesTable.addMouseListener(new PopupMouseAdapter() {
            @Override
            protected void showPopup(final MouseEvent e) {
                final int index = LauncherFrame.this.instancesTable.rowAtPoint(e.getPoint());
                Instance selected = null;
                if (index >= 0) {
                    LauncherFrame.this.instancesTable.setRowSelectionInterval(index, index);
                    selected = LauncherFrame.this.launcher.getInstances().get(index);
                }
                popupInstanceMenu(e.getComponent(), e.getX(), e.getY(), selected);
            }
        });
    }

    protected JPanel createContainerPanel() {
        return new JPanel();
    }

    /**
     * Return the news panel.
     *
     * @return the news panel
     */
    protected WebpagePanel createNewsPanel() {
        return WebpagePanel.forURL(this.launcher.getNewsURL(), false);
    }

    /**
     * Popup the menu for the instances.
     *
     * @param component the component
     * @param x mouse X
     * @param y mouse Y
     * @param selected the selected instance, possibly null
     */
    private void popupInstanceMenu(final Component component, final int x, final int y, final Instance selected) {
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem;

        if (selected != null) {
            menuItem = new JMenuItem(!selected.isLocal() ? tr("instance.install") : tr("instance.launch"));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    launch();
                }
            });
            popup.add(menuItem);

            if (selected.isLocal()) {
                popup.addSeparator();

                menuItem = new JMenuItem(SharedLocale.tr("instance.openFolder"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, selected.getContentDir(), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.openSaves"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, new File(selected.getContentDir(), "saves"), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.openResourcePacks"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, new File(selected.getContentDir(), "resourcepacks"), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.openScreenshots"));
                menuItem.addActionListener(ActionListeners.browseDir(
                        LauncherFrame.this, new File(selected.getContentDir(), "screenshots"), true));
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.copyAsPath"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final File dir = selected.getContentDir();
                        dir.mkdirs();
                        SwingHelper.setClipboard(dir.getAbsolutePath());
                    }
                });
                popup.add(menuItem);

                popup.addSeparator();

                if (!selected.isUpdatePending()) {
                    menuItem = new JMenuItem(SharedLocale.tr("instance.forceUpdate"));
                    menuItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            selected.setUpdatePending(true);
                            launch();
                            LauncherFrame.this.instancesModel.update();
                        }
                    });
                    popup.add(menuItem);
                }

                menuItem = new JMenuItem(SharedLocale.tr("instance.hardForceUpdate"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        confirmHardUpdate(selected);
                    }
                });
                popup.add(menuItem);

                menuItem = new JMenuItem(SharedLocale.tr("instance.deleteFiles"));
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        confirmDelete(selected);
                    }
                });
                popup.add(menuItem);
            }

            popup.addSeparator();
        }

        menuItem = new JMenuItem(SharedLocale.tr("launcher.refreshList"));
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                loadInstances();
            }
        });
        popup.add(menuItem);

        popup.show(component, x, y);

    }

    private void confirmDelete(final Instance instance) {
        if (!SwingHelper.confirmDialog(this,
                tr("instance.confirmDelete", instance.getTitle()), SharedLocale.tr("confirmTitle")))
			return;

        final ObservableFuture<Instance> future = this.launcher.getInstanceTasks().delete(this, instance);

        // Update the list of instances after updating
        future.addListener(new Runnable() {
            @Override
            public void run() {
                loadInstances();
            }
        }, SwingExecutor.INSTANCE);
    }

    private void confirmHardUpdate(final Instance instance) {
        if (!SwingHelper.confirmDialog(this, SharedLocale.tr("instance.confirmHardUpdate"), SharedLocale.tr("confirmTitle")))
			return;

        final ObservableFuture<Instance> future = this.launcher.getInstanceTasks().hardUpdate(this, instance);

        // Update the list of instances after updating
        future.addListener(new Runnable() {
            @Override
            public void run() {
                launch();
                LauncherFrame.this.instancesModel.update();
            }
        }, SwingExecutor.INSTANCE);
    }

    private void loadInstances() {
        final ObservableFuture<InstanceList> future = this.launcher.getInstanceTasks().reloadInstances(this);

        future.addListener(new Runnable() {
            @Override
            public void run() {
                LauncherFrame.this.instancesModel.update();
                if (LauncherFrame.this.instancesTable.getRowCount() > 0)
					LauncherFrame.this.instancesTable.setRowSelectionInterval(0, 0);
                requestFocus();
            }
        }, SwingExecutor.INSTANCE);

        ProgressDialog.showProgress(this, future, SharedLocale.tr("launcher.checkingTitle"), SharedLocale.tr("launcher.checkingStatus"));
        SwingHelper.addErrorDialogCallback(this, future);
    }

    private void showOptions() {
        final ConfigurationDialog configDialog = new ConfigurationDialog(this, this.launcher);
        configDialog.setVisible(true);
    }

    private void launch() {
        final boolean permitUpdate = this.updateCheck.isSelected();
        final Instance instance = this.launcher.getInstances().get(this.instancesTable.getSelectedRow());

        final LaunchOptions options = new LaunchOptions.Builder()
                .setInstance(instance)
                .setListener(new LaunchListenerImpl(this))
                .setUpdatePolicy(permitUpdate ? UpdatePolicy.UPDATE_IF_SESSION_ONLINE : UpdatePolicy.NO_UPDATE)
                .setWindow(this)
                .build();
        this.launcher.getLaunchSupervisor().launch(options);
    }

    private static class LaunchListenerImpl implements LaunchListener {
        private final WeakReference<LauncherFrame> frameRef;
        private final Launcher launcher;

        private LaunchListenerImpl(final LauncherFrame frame) {
            this.frameRef = new WeakReference<LauncherFrame>(frame);
            this.launcher = frame.launcher;
        }

        @Override
        public void instancesUpdated() {
            final LauncherFrame frame = this.frameRef.get();
            if (frame != null)
				frame.instancesModel.update();
        }

        @Override
        public void gameStarted() {
            final LauncherFrame frame = this.frameRef.get();
            if (frame != null)
				frame.dispose();
        }

        @Override
        public void gameClosed() {
            this.launcher.showLauncherWindow();
        }
    }

}
