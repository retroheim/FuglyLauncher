/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.swing;

import com.skcraft.launcher.Instance;
import com.skcraft.launcher.InstanceList;
import com.skcraft.launcher.Launcher;
import com.skcraft.launcher.util.SharedLocale;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class InstanceTableModel extends AbstractTableModel {

    private final InstanceList instances;
    private final Icon instanceIcon;
    private final Icon customInstanceIcon;
    private final Icon downloadIcon;

    public InstanceTableModel(InstanceList instances) {
        this.instances = instances;
        instanceIcon = SwingHelper.createIcon(Launcher.class, "instance_icon.png", 16*4, 16*4);
        customInstanceIcon = SwingHelper.createIcon(Launcher.class, "custom_instance_icon.png", 16*4, 16*4);
        downloadIcon = SwingHelper.createIcon(Launcher.class, "download_icon.png", 14*4, 14*4);
    }

    public void update() {
        instances.sort();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return SharedLocale.tr("launcher.modpackColumn");
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return InstanceTableCellModel.class;
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            default:
                break;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            default:
                return false;
        }
    }

    @Override
    public int getRowCount() {
        return instances.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Instance instance;
        switch (columnIndex) {
            case 0:
            	Icon icon;
                instance = instances.get(rowIndex);
                if (!instance.isLocal()) {
                    icon = downloadIcon;
                } else if (instance.getThumb()!=null) {
                	icon = SwingHelper.createIcon(instance.getThumb(), 16*4, 16*4);
                } else if (instance.getManifestURL() != null) {
                	icon = instanceIcon;
                } else {
                	icon = customInstanceIcon;
                }
                if (icon!=null) {
                    return new InstanceTableCellModel(instance.getTitle(), icon);
                }
            default:
                return null;
        }
    }

}
