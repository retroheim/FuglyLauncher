/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.dialog;

import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.MessageLog;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.PastebinPoster;
import com.skcraft.launcher.util.SharedLocale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import lombok.NonNull;
import net.teamfruit.skcraft.launcher.appicon.AppIcon;
import net.teamfruit.skcraft.launcher.discordrpc.LauncherStatus;

public class ConsoleFrame
extends JFrame {
    private static ConsoleFrame globalFrame;
    private final List<BufferedImage> trayRunningIcon;
    private final List<BufferedImage> trayClosedIcon;
    private final MessageLog messageLog;
    private LinedBoxPanel buttonsPanel;
    private boolean registeredGlobalLog = false;
    private JButton clearLogButton;
    private JButton pastebinButton;

    public ConsoleFrame(int numLines, boolean colorEnabled) {
        this(SharedLocale.tr("console.title"), numLines, colorEnabled);
    }

    public ConsoleFrame(@NonNull String title, int numLines, boolean colorEnabled) {
        if (title == null) {
            throw new NullPointerException("title");
        }
        this.messageLog = new MessageLog(numLines, colorEnabled);
        List<BufferedImage> swingIconSet = AppIcon.getSwingIconSet(AppIcon.getAppIconSet());
        this.trayRunningIcon = AppIcon.getSwingTaskIcon(swingIconSet, new Color(67, 181, 129));
        this.trayClosedIcon = AppIcon.getSwingTaskIcon(swingIconSet, new Color(152, 41, 41));
        this.setTitle(title);
        AppIcon.setFrameIconSet(this, this.trayRunningIcon);
        this.setSize(new Dimension(650, 400));
        this.initComponents();
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent event) {
                ConsoleFrame.this.performClose();
            }
        });
    }

    private void initComponents() {
        this.pastebinButton = new JButton(SharedLocale.tr("console.uploadLog"));
        this.clearLogButton = new JButton(SharedLocale.tr("console.clearLog"));
        this.buttonsPanel = new LinedBoxPanel(true);
        this.buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.buttonsPanel.addElement(this.pastebinButton);
        this.buttonsPanel.addElement(this.clearLogButton);
        this.buttonsPanel.addElement(this.messageLog.getSeeLastCheckbox());
        this.add((Component)this.buttonsPanel, "North");
        this.add((Component)this.messageLog, "Center");
        this.clearLogButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ConsoleFrame.this.messageLog.clear();
            }
        });
        this.pastebinButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                ConsoleFrame.this.pastebinLog();
            }
        });
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowActivated(WindowEvent e) {
                LauncherStatus.instance.update();
            }
        });
    }

    private void registerLoggerHandler() {
        if (!this.registeredGlobalLog) {
            this.getMessageLog().getMessage().registerLoggerHandler();
            this.registeredGlobalLog = true;
        }
    }

    protected void performClose() {
        this.messageLog.detachGlobalHandler();
        this.messageLog.clear();
        this.registeredGlobalLog = false;
        this.dispose();
    }

    private void pastebinLog() {
        Component selected = this.messageLog.getSelectedComponent();
        if (selected instanceof MessageLog.MessagePanel) {
            final MessageLog.MessagePanel message = (MessageLog.MessagePanel)selected;
            String text = message.getPastableText();
            message.log(SharedLocale.tr("console.pasteUploading", text.length()), this.messageLog.asHighlighted());
            PastebinPoster.paste(text, new PastebinPoster.PasteCallback(){

                @Override
                public void handleSuccess(String url) {
                    message.log(SharedLocale.tr("console.pasteUploaded", url), ConsoleFrame.this.messageLog.asHighlighted());
                    SwingHelper.openURL(url, (Component)message);
                }

                @Override
                public void handleError(String err) {
                    message.log(SharedLocale.tr("console.pasteFailed", err), ConsoleFrame.this.messageLog.asError());
                }
            });
        }
    }

    public static ConsoleFrame initMessages() {
        ConsoleFrame frame = globalFrame;
        if (frame == null) {
            globalFrame = frame = new ConsoleFrame(10000, false);
            frame.setTitle(SharedLocale.tr("console.launcherConsoleTitle"));
            frame.registerLoggerHandler();
        } else {
            frame.registerLoggerHandler();
        }
        return frame;
    }

    public static void showMessages() {
        ConsoleFrame frame = ConsoleFrame.initMessages();
        frame.setVisible(true);
        frame.requestFocus();
    }

    public static void hideMessages() {
        ConsoleFrame frame = globalFrame;
        if (frame != null) {
            frame.setVisible(false);
        }
    }

    public List<BufferedImage> getTrayRunningIcon() {
        return this.trayRunningIcon;
    }

    public List<BufferedImage> getTrayClosedIcon() {
        return this.trayClosedIcon;
    }

    public MessageLog getMessageLog() {
        return this.messageLog;
    }

    public LinedBoxPanel getButtonsPanel() {
        return this.buttonsPanel;
    }

    public JButton getClearLogButton() {
        return this.clearLogButton;
    }

    public JButton getPastebinButton() {
        return this.pastebinButton;
    }

}

