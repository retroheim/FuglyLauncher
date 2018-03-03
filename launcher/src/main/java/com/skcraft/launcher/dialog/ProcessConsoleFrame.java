/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.dialog;

import static com.skcraft.launcher.util.SharedLocale.*;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.google.common.collect.Maps;
import com.skcraft.launcher.swing.LinedBoxPanel;
import com.skcraft.launcher.swing.MessageLog;
import com.skcraft.launcher.swing.SwingHelper;
import com.skcraft.launcher.util.SharedLocale;

import lombok.Getter;
import net.teamfruit.skcraft.launcher.appicon.AppIcon;
import net.teamfruit.skcraft.launcher.appicon.AppIcon.IconSet;

/**
 * A version of the console window that can manage a process.
 */
public class ProcessConsoleFrame extends ConsoleFrame {

    private JButton killButton;
    private JButton minimizeButton;
    private TrayIcon trayIcon;

    @Getter private Process process;
    //@Getter @Setter private boolean killOnClose;

    private PrintWriter processOut;

    /**
     * Create a new instance of the frame.
     *
     * @param numLines the number of log lines
     * @param colorEnabled whether color is enabled in the log
     */
    public ProcessConsoleFrame(int numLines, boolean colorEnabled) {
        super(SharedLocale.tr("console.title"), numLines, colorEnabled);
        processOut = new PrintWriter(
                getMessageLog().getOutputStream(new Color(0, 0, 255)), true);
        initComponents();
        updateComponents();
    }

    /**
     * Track the given process.
     *
     * @param process the process
     */
    public synchronized void setProcess(Process process) {
        try {
            Process lastProcess = this.process;
            if (lastProcess != null) {
                processOut.println(tr("console.processEndCode", lastProcess.exitValue()));
            }
        } catch (IllegalThreadStateException e) {
        }

        if (process != null) {
            processOut.println(SharedLocale.tr("console.attachedToProcess"));
        }

        this.process = process;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateComponents();
            }
        });
    }

    private synchronized boolean hasProcess() {
        return process != null;
    }

    @Override
    protected void performClose() {
        if (hasProcess()) {
            if (!performKill()) {
                return;
            }
        }

        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }

        super.performClose();
    }

    private boolean performKill() {
        if (!confirmKill()) {
            return false;
        }

        synchronized (this) {
            if (hasProcess()) {
                process.destroy();
                setProcess(null);
            }
        }

        updateComponents();

        return true;
    }

    protected void initComponents() {
    	MessageLog message = getMessageLog();

    	message.setTitleAt(0, SharedLocale.tr("console.gameTab"));

    	message.addTab(SharedLocale.tr("console.chatTab"), message.new MessagePanel() {
	     	private final Pattern chatPattern = Pattern.compile("\\[(.+?)\\] \\[.+?\\]: \\[CHAT\\] (.*)");
	     	private final Pattern colorPattern = Pattern.compile("\u00A7(.)");

	     	private final Map<Character, Color> colors = Maps.newHashMap();

	     	private SimpleAttributeSet blankAttr = new SimpleAttributeSet();

	     	{
	     		textComponent.setBackground(new Color(0f, 0f, 0f, 0.5f));

	     		colors.put('0', new Color(0x000000));
	     		colors.put('1', new Color(0x0000AA));
	     		colors.put('2', new Color(0x00AA00));
	     		colors.put('3', new Color(0x00AAAA));
	     		colors.put('4', new Color(0xAA0000));
	     		colors.put('5', new Color(0xAA00AA));
	     		colors.put('6', new Color(0xFFAA00));
	     		colors.put('7', new Color(0xAAAAAA));
	     		colors.put('8', new Color(0x555555));
	     		colors.put('9', new Color(0x5555FF));
	     		colors.put('a', new Color(0x55FF55));
	     		colors.put('b', new Color(0x55FFFF));
	     		colors.put('c', new Color(0xFF5555));
	     		colors.put('d', new Color(0xFF55FF));
	     		colors.put('e', new Color(0xFFFF55));
	     		colors.put('f', new Color(0xFFFFFF));

	     		StyleConstants.setForeground(blankAttr, new Color(0xAAAAAA));
	     	}

	   		@Override
    		public void log(String line, AttributeSet attributes) {
    			if (line.contains("[CHAT] ")) {
    				Matcher m = chatPattern.matcher(line);
    			    while (m.find()) {
    			    	String timeText = m.group(1);

    			    	super.log("["+timeText+"] ", blankAttr);

    			        String chatText = m.group(2);

    			        Matcher cm = colorPattern.matcher(chatText);
    			        StringBuffer csb = new StringBuffer();

    			     	SimpleAttributeSet lastAttr = new SimpleAttributeSet();

    			        while (cm.find()) {
    			        	char key = cm.group(1).charAt(0);
    			        	Color color = colors.get(key);
    			        	if (color!=null) {
    			        		lastAttr = new SimpleAttributeSet();
    			        		StyleConstants.setForeground(lastAttr, color);
    			        	} else {
    			        		if (key=='r')
    			        			lastAttr = new SimpleAttributeSet();
    			        		else if (key=='k')
    			        			StyleConstants.setBackground(lastAttr, StyleConstants.getForeground(lastAttr));
    			        		else if (key=='l')
    			        			StyleConstants.setBold(lastAttr, true);
    			        		else if (key=='m')
    			        			StyleConstants.setStrikeThrough(lastAttr, true);
    			        		else if (key=='n')
    			        			StyleConstants.setUnderline(lastAttr, true);
    			        		else if (key=='o')
    			        			StyleConstants.setItalic(lastAttr, true);
    			        	}

    			        	csb.setLength(0);
    			        	cm.appendReplacement(csb, "");
    			        	super.log(csb.toString(), lastAttr);
    			        }

			        	csb.setLength(0);
			        	cm.appendTail(csb);
			        	csb.append("\n");
			        	super.log(csb.toString(), lastAttr);
			        }
    			}
    		}
    	});

        killButton = new JButton(SharedLocale.tr("console.forceClose"));
        minimizeButton = new JButton(); // Text set later

        LinedBoxPanel buttonsPanel = getButtonsPanel();
        buttonsPanel.addGlue();
        buttonsPanel.addElement(killButton);
        buttonsPanel.addElement(minimizeButton);

        killButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performKill();
            }
        });

        minimizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contextualClose();
            }
        });

        if (!setupTrayIcon()) {
            minimizeButton.setEnabled(true);
        }
    }

    private boolean setupTrayIcon() {
        if (!SystemTray.isSupported()) {
            return false;
        }

        Image icon = getTrayRunningIcon().getIcon();
        if (icon==null)
        	return false;

        trayIcon = new TrayIcon(icon);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(SharedLocale.tr("console.trayTooltip"));

        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reshow();
            }
        });

        PopupMenu popup = new PopupMenu();
        MenuItem item;

        popup.add(item = new MenuItem(SharedLocale.tr("console.trayTitle")));
        item.setEnabled(false);

        popup.add(item = new MenuItem(SharedLocale.tr("console.tray.showWindow")));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reshow();
            }
        });

        popup.add(item = new MenuItem(SharedLocale.tr("console.tray.forceClose")));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performKill();
            }
        });

        trayIcon.setPopupMenu(popup);

        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
            return true;
        } catch (AWTException e) {
        }

        return false;
    }

    private synchronized void updateComponents() {
        IconSet iconSet = hasProcess() ? getTrayRunningIcon() : getTrayClosedIcon();

        killButton.setEnabled(hasProcess());

        if (!hasProcess() || trayIcon == null) {
            minimizeButton.setText(SharedLocale.tr("console.closeWindow"));
        } else {
            minimizeButton.setText(SharedLocale.tr("console.hideWindow"));
        }

        if (trayIcon != null) {
	        Image icon = iconSet.getIcon();
        	if (icon!=null)
        		trayIcon.setImage(icon);
        }

        AppIcon.setFrameIconSet(this, iconSet);
    }

    private synchronized void contextualClose() {
        if (!hasProcess() || trayIcon == null) {
            performClose();
        } else {
            minimize();
        }

        updateComponents();
    }

    private boolean confirmKill() {
        if (System.getProperty("skcraftLauncher.killWithoutConfirm", "false").equalsIgnoreCase("true")) {
            return true;
        } else {
            return SwingHelper.confirmDialog(this,  SharedLocale.tr("console.confirmKill"), SharedLocale.tr("console.confirmKillTitle"));
        }
    }

    private void minimize() {
        setVisible(false);
    }

    private void reshow() {
        setVisible(true);
        requestFocus();
    }

}
