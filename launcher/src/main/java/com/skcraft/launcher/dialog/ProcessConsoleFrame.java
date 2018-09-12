/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.dialog;

import com.skcraft.launcher.dialog.ConsoleFrame;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.MessageLog;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import net.teamfruit.skcraft.launcher.appicon.AppIcon;
import net.teamfruit.skcraft.launcher.swing.ChatMessagePanel;

public class ProcessConsoleFrame
extends ConsoleFrame {
    private JButton killButton;
    private JButton minimizeButton;
    private TrayIcon trayIcon;
    private Process process;
    private PrintWriter processOut = new PrintWriter(this.getMessageLog().getOutputStream(new Color(0, 0, 255)), true);

    public ProcessConsoleFrame(int numLines, boolean colorEnabled) {
        super(SharedLocale.tr("console.title"), numLines, colorEnabled);
        this.initComponents();
        this.updateComponents();
    }

    public synchronized void setProcess(Process process) {
        try {
            Process lastProcess = this.process;
            if (lastProcess != null) {
                this.processOut.println(SharedLocale.tr("console.processEndCode", lastProcess.exitValue()));
            }
        }
        catch (IllegalThreadStateException lastProcess) {
            // empty catch block
        }
        if (process != null) {
            this.processOut.println(SharedLocale.tr("console.attachedToProcess"));
        }
        this.process = process;
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ProcessConsoleFrame.this.updateComponents();
            }
        });
    }

    private synchronized boolean hasProcess() {
        return this.process != null;
    }

    @Override
    protected void performClose() {
        if (this.hasProcess() && !this.performKill()) {
            return;
        }
        if (this.trayIcon != null) {
            SystemTray.getSystemTray().remove(this.trayIcon);
        }
        super.performClose();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean performKill() {
        if (!this.confirmKill()) {
            return false;
        }
        ProcessConsoleFrame processConsoleFrame = this;
        synchronized (processConsoleFrame) {
            if (this.hasProcess()) {
                this.process.destroy();
                this.setProcess(null);
            }
        }
        this.updateComponents();
        return true;
    }

    protected void initComponents() {
        MessageLog message = this.getMessageLog();
        message.setTitleAt(0, SharedLocale.tr("console.gameTab"));
        message.addTab(SharedLocale.tr("console.chatTab"), new ChatMessagePanel(message));
        this.killButton = new JButton(SharedLocale.tr("console.forceClose"));
        this.minimizeButton = new JButton();
        LinedBoxPanel buttonsPanel = this.getButtonsPanel();
        buttonsPanel.addGlue();
        buttonsPanel.addElement(this.killButton);
        buttonsPanel.addElement(this.minimizeButton);
        this.killButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessConsoleFrame.this.performKill();
            }
        });
        this.minimizeButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessConsoleFrame.this.contextualClose();
            }
        });
        if (!this.setupTrayIcon()) {
            this.minimizeButton.setEnabled(true);
        }
    }

    private boolean setupTrayIcon() {
        if (!SystemTray.isSupported()) {
            return false;
        }
        List<BufferedImage> icon = this.getTrayRunningIcon();
        if (icon.isEmpty()) {
            return false;
        }
        this.trayIcon = new TrayIcon(icon.get(0));
        this.trayIcon.setImageAutoSize(true);
        this.trayIcon.setToolTip(SharedLocale.tr("console.trayTooltip"));
        this.trayIcon.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessConsoleFrame.this.reshow();
            }
        });
        PopupMenu popup = new PopupMenu();
        MenuItem item = new MenuItem(SharedLocale.tr("console.trayTitle"));
        popup.add(item);
        item.setEnabled(false);
        item = new MenuItem(SharedLocale.tr("console.tray.showWindow"));
        popup.add(item);
        item.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessConsoleFrame.this.reshow();
            }
        });
        item = new MenuItem(SharedLocale.tr("console.tray.forceClose"));
        popup.add(item);
        item.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessConsoleFrame.this.performKill();
            }
        });
        this.trayIcon.setPopupMenu(popup);
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(this.trayIcon);
            return true;
        }
        catch (AWTException tray) {
            return false;
        }
    }

    private synchronized void updateComponents() {
        List<BufferedImage> iconSet = this.hasProcess() ? this.getTrayRunningIcon() : this.getTrayClosedIcon();
        this.killButton.setEnabled(this.hasProcess());
        if (!this.hasProcess() || this.trayIcon == null) {
            this.minimizeButton.setText(SharedLocale.tr("console.closeWindow"));
        } else {
            this.minimizeButton.setText(SharedLocale.tr("console.hideWindow"));
        }
        if (this.trayIcon != null && iconSet.isEmpty()) {
            this.trayIcon.setImage(iconSet.get(0));
        }
        AppIcon.setFrameIconSet(this, iconSet);
    }

    private synchronized void contextualClose() {
        if (!this.hasProcess() || this.trayIcon == null) {
            this.performClose();
        } else {
            this.minimize();
        }
        this.updateComponents();
    }

    private boolean confirmKill() {
        if (System.getProperty("skcraftLauncher.killWithoutConfirm", "false").equalsIgnoreCase("true")) {
            return true;
        }
        return SwingHelper.confirmDialog(this, SharedLocale.tr("console.confirmKill"), SharedLocale.tr("console.confirmKillTitle"));
    }

    private void minimize() {
        this.setVisible(false);
    }

    private void reshow() {
        this.setVisible(true);
        this.requestFocus();
    }

    public Process getProcess() {
        return this.process;
    }

}

