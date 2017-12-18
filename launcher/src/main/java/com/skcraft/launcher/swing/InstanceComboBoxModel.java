/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.skcraft.launcher.swing;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import com.skcraft.launcher.Instance;
import com.skcraft.launcher.InstanceList;

public class InstanceComboBoxModel extends AbstractListModel<Instance> implements ComboBoxModel<Instance> {

    private final InstanceList instances;

    public InstanceComboBoxModel(final InstanceList instances) {
        this.instances = instances;
    }

    public void update() {
        this.instances.sort();
        // fireTableDataChanged();
    }

	@Override
	public int getSize() {
		return this.instances.size();
	}

	@Override
	public Instance getElementAt(final int index) {
		return this.instances.get(index);
	}

	@Override
	public void setSelectedItem(final Object anItem) {
		;
	}

	@Override
	public Object getSelectedItem() {
		final List<Instance> list = this.instances.getInstances();
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

}
