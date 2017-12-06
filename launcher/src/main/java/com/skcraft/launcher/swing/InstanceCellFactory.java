package com.skcraft.launcher.swing;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.skcraft.launcher.Instance;

public class InstanceCellFactory implements TableCellRenderer, ListCellRenderer<Instance> {

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		final InstanceTableCellPanel tablecell = new InstanceTableCellPanel(table);

		if (value instanceof Instance) {
			final Instance instance = (Instance) value;
			tablecell.setTitle(instance.getTitle());
			tablecell.setSelected(isSelected);
			tablecell.setInstance(instance);
		}

		return tablecell;
	}

	@Override
	public Component getListCellRendererComponent(final JList<? extends Instance> list, final Instance value, final int index, final boolean isSelected, final boolean cellHasFocus) {
		final InstanceTableCellPanel tablecell = new InstanceTableCellPanel(list);

		if (value!=null) {
			final Instance instance = value;
			tablecell.setTitle(instance.getTitle());
			tablecell.setSelected(isSelected);
			tablecell.setInstance(instance);
		}

		return tablecell;
	}
}
