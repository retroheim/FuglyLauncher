/*
 * Decompiled with CFR 0_132.
 */
package com.skcraft.launcher.swing;

import com.skcraft.launcher.util.SharedLocale;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

public class TextFieldPopupMenu
extends JPopupMenu
implements ActionListener {
    public static final TextFieldPopupMenu INSTANCE = new TextFieldPopupMenu();
    private final JMenuItem cutItem = this.addMenuItem(new JMenuItem(SharedLocale.tr("context.cut"), 84));
    private final JMenuItem copyItem = this.addMenuItem(new JMenuItem(SharedLocale.tr("context.copy"), 67));
    private final JMenuItem pasteItem = this.addMenuItem(new JMenuItem(SharedLocale.tr("context.paste"), 80));
    private final JMenuItem deleteItem = this.addMenuItem(new JMenuItem(SharedLocale.tr("context.delete"), 68));
    private final JMenuItem selectAllItem;

    private TextFieldPopupMenu() {
        this.addSeparator();
        this.selectAllItem = this.addMenuItem(new JMenuItem(SharedLocale.tr("context.selectAll"), 65));
    }

    private JMenuItem addMenuItem(JMenuItem item) {
        item.addActionListener(this);
        return this.add(item);
    }

    @Override
    public void show(Component invoker, int x, int y) {
        JTextComponent textComponent = (JTextComponent)invoker;
        boolean editable = textComponent.isEditable() && textComponent.isEnabled();
        this.cutItem.setVisible(editable);
        this.pasteItem.setVisible(editable);
        this.deleteItem.setVisible(editable);
        super.show(invoker, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean haveSelection;
        JTextComponent textComponent = (JTextComponent)this.getInvoker();
        textComponent.requestFocus();
        boolean bl = haveSelection = textComponent.getSelectionStart() != textComponent.getSelectionEnd();
        if (e.getSource() == this.cutItem) {
            if (!haveSelection) {
                textComponent.selectAll();
            }
            textComponent.cut();
        } else if (e.getSource() == this.copyItem) {
            if (!haveSelection) {
                textComponent.selectAll();
            }
            textComponent.copy();
        } else if (e.getSource() == this.pasteItem) {
            textComponent.paste();
        } else if (e.getSource() == this.deleteItem) {
            if (!haveSelection) {
                textComponent.selectAll();
            }
            textComponent.replaceSelection("");
        } else if (e.getSource() == this.selectAllItem) {
            textComponent.selectAll();
        }
    }
}

