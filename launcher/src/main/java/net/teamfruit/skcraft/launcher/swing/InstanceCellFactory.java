package net.teamfruit.skcraft.launcher.swing;

import java.awt.Component;
import java.awt.Image;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.skcraft.launcher.Instance;

public class InstanceCellFactory implements TableCellRenderer, ListCellRenderer<Instance> {

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		return getCellComponent(table, value, isSelected);
	}

	@Override
	public Component getListCellRendererComponent(final JList<? extends Instance> list, final Instance value, final int index, final boolean isSelected, final boolean cellHasFocus) {
		return getCellComponent(list, value, isSelected);
	}

	private final Table<Instance, JComponent, InstanceTableCellPanel> table = Tables.newCustomTable(new WeakHashMap<Instance, Map<JComponent, InstanceTableCellPanel>>(), new Supplier<Map<JComponent, InstanceTableCellPanel>>() {
		@Override
		public Map<JComponent, InstanceTableCellPanel> get() {
			return new WeakHashMap<JComponent, InstanceTableCellPanel>();
		}
	});
	private final Map<Instance, Image> iconCaches = new WeakHashMap<Instance, Image>();
	private final InstanceTableCellPanel defaultPanel = new InstanceTableCellPanel(null);

	public InstanceTableCellPanel getCellComponent(final JComponent component, @Nullable final Object value, final boolean isSelected) {
		if (value instanceof Instance) {
			final Instance instance = (Instance) value;

			InstanceTableCellPanel tablecell = table.get(instance, component);
			if (tablecell==null) {
				tablecell = new InstanceTableCellPanel(component);

				tablecell.setTitle(instance.getTitle());
				tablecell.setInstance(instance, iconCaches);

				table.put(instance, component, tablecell);
			}

			tablecell.setShowPlayIcon(isSelected);
			tablecell.setShowSelected(isSelected);

			return tablecell;
		}

		return defaultPanel;
	}
}
