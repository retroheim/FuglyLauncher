/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.dialog;

import static com.skcraft.launcher.util.SharedLocale.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.MessageLog;
import com.skcraft.launcher.swing.MessageLog.MessagePanel;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.PastebinPoster;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Getter;
import lombok.NonNull;
import net.teamfruit.skcraft.launcher.appicon.AppIcon;
import net.teamfruit.skcraft.launcher.discordrpc.LauncherDiscord;

/**
 * A frame capable of showing messages.
 */
public class ConsoleFrame extends JFrame {

    private static ConsoleFrame globalFrame;

    @Getter private final List<BufferedImage> trayRunningIcon;
    @Getter private final List<BufferedImage> trayClosedIcon;

    @Getter private final MessageLog messageLog;
    @Getter private LinedBoxPanel buttonsPanel;

    private boolean registeredGlobalLog = false;

	@Getter private JButton clearLogButton;
	@Getter private JButton pastebinButton;

    /**
     * Construct the frame.
     *
     * @param numLines number of lines to show at a time
     * @param colorEnabled true to enable a colored console
     */
    public ConsoleFrame(int numLines, boolean colorEnabled) {
        this(SharedLocale.tr("console.title"), numLines, colorEnabled);
    }

    /**
     * Construct the frame.
     *
     * @param title the title of the window
     * @param numLines number of lines to show at a time
     * @param colorEnabled true to enable a colored console
     */
    public ConsoleFrame(@NonNull String title, int numLines, boolean colorEnabled) {
        messageLog = new MessageLog(numLines, colorEnabled);
        List<BufferedImage> swingIconSet = AppIcon.getSwingIconSet(AppIcon.getAppIconSet());
        trayRunningIcon = AppIcon.getSwingTaskIcon(swingIconSet, new Color(67, 181, 129));
        trayClosedIcon = AppIcon.getSwingTaskIcon(swingIconSet, new Color(152, 41, 41));

        setTitle(title);
        AppIcon.setFrameIconSet(this, trayRunningIcon);

        setSize(new Dimension(650, 400));
        initComponents();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                performClose();
            }
        });
    }

    /**
     * Add components to the frame.
     */
    private void initComponents() {
        this.pastebinButton = new JButton(SharedLocale.tr("console.uploadLog"));
        this.clearLogButton = new JButton(SharedLocale.tr("console.clearLog"));
        buttonsPanel = new LinedBoxPanel(true);

        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buttonsPanel.addElement(this.pastebinButton);
        buttonsPanel.addElement(this.clearLogButton);
        buttonsPanel.addElement(messageLog.getSeeLastCheckbox());

        add(buttonsPanel, BorderLayout.NORTH);
        add(messageLog, BorderLayout.CENTER);
        this.clearLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageLog.clear();
            }
        });

        this.pastebinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pastebinLog();
            }
        });

        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowActivated(WindowEvent e) {
        		LauncherDiscord.updateStatusWithNoChange();
        	}
		});
    }

    /**
     * Register the global logger if it hasn't been registered.
     */
    private void registerLoggerHandler() {
        if (!registeredGlobalLog) {
            getMessageLog().getMessage().registerLoggerHandler();
            registeredGlobalLog = true;
        }
    }

    /**
     * Attempt to perform window close.
     */
    protected void performClose() {
        messageLog.detachGlobalHandler();
        messageLog.clear();
        registeredGlobalLog = false;
        dispose();
    }

    /**
     * Send the contents of the message log to a pastebin.
     */
    private void pastebinLog() {
    	Component selected = messageLog.getSelectedComponent();
    	if (selected instanceof MessagePanel) {
    		final MessagePanel message = (MessagePanel) selected;

	        String text = message.getPastableText();
	        // Not really bytes!
	        message.log(tr("console.pasteUploading", text.length()), messageLog.asHighlighted());

	        PastebinPoster.paste(text, new PastebinPoster.PasteCallback() {
	            @Override
	            public void handleSuccess(String url) {
	                message.log(tr("console.pasteUploaded", url), messageLog.asHighlighted());
	                SwingHelper.openURL(url, message);
	            }

	            @Override
	            public void handleError(String err) {
	                message.log(tr("console.pasteFailed", err), messageLog.asError());
	            }
	        });
    	}
    }

    public static ConsoleFrame initMessages() {
        ConsoleFrame frame = globalFrame;
        if (frame == null) {
            frame = new ConsoleFrame(10000, false);
            globalFrame = frame;
            frame.setTitle(SharedLocale.tr("console.launcherConsoleTitle"));
            frame.registerLoggerHandler();
        } else {
            frame.registerLoggerHandler();
        }
        return frame;
    }

    public static void showMessages() {
        ConsoleFrame frame = initMessages();
        frame.setVisible(true);
        frame.requestFocus();
    }

    public static void hideMessages() {
        ConsoleFrame frame = globalFrame;
        if (frame != null)
			frame.setVisible(false);
    }

}
