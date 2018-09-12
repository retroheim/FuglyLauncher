/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.dialog;

import com.skcraft.launcher.swing.ActionListeners;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import net.miginfocom.swing.MigLayout;
import net.teamfruit.skcraft.launcher.discordrpc.LauncherStatus;

public class AboutDialog
extends JDialog {
    public AboutDialog(Window parent) {
        super(parent, "About", Dialog.ModalityType.DOCUMENT_MODAL);
        this.setDefaultCloseOperation(2);
        this.initComponents();
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new MigLayout("insets dialog"));
        container.add((Component)new JLabel("<html>Licensed under GNU General Public License, version 3."), "wrap, gapbottom unrel");
        container.add((Component)new JLabel("<html>You are using SKCraft Launcher, an open-source customizable<br>launcher platform that anyone can use."), "wrap, gapbottom unrel");
        container.add((Component)new JLabel("<html>SKCraft does not necessarily endorse the version of<br>the launcher that you are using."), "wrap, gapbottom unrel");
        container.add((Component)new JLabel("<html>FuglyNetwork Launcher"), "wrap, gapbottom unrel");
        JButton okButton = new JButton("OK");
        JButton sourceCodeButton = new JButton("Website");
        container.add((Component)sourceCodeButton, "span, split 3, sizegroup bttn");
        container.add((Component)okButton, "tag ok, sizegroup bttn");
        this.add((Component)container, "Center");
        this.getRootPane().setDefaultButton(okButton);
        this.getRootPane().registerKeyboardAction(ActionListeners.dispose(this), KeyStroke.getKeyStroke(27, 0), 2);
        okButton.addActionListener(ActionListeners.dispose(this));
        sourceCodeButton.addActionListener(ActionListeners.openURL(this, "https://github.com/Fugly-Network/FuglyLauncher"));
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowActivated(WindowEvent e) {
                LauncherStatus.instance.update();
            }
        });
    }

    public static void showAboutDialog(Window parent) {
        AboutDialog dialog = new AboutDialog(parent);
        dialog.setVisible(true);
    }

}

