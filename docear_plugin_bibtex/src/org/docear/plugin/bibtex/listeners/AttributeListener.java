package org.docear.plugin.bibtex.listeners;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;

import org.docear.plugin.bibtex.JabRefAttributes;
import org.docear.plugin.bibtex.ReferencesController;
import org.freeplane.features.attribute.Attribute;
import org.freeplane.features.attribute.NodeAttributeTableModel;

import org.freeplane.core.util.LogUtils;

public class AttributeListener implements TableModelListener {
	
	private static boolean locked = false;

	@Override
	public void tableChanged(TableModelEvent e) {		
		if (locked) {
			return;
		}
		locked = true;
//		System.out.println("debug tableChanged: column: " + e.getColumn() + " firstRow:" + e.getFirstRow() + " lastRow:"
//				+ e.getLastRow() + " type: " + e.getType());

		// if changes only happened one row and one column
		if (e.getFirstRow() == e.getLastRow() && e.getColumn() > 0) {
			JabRefAttributes jabRefAttributes = ReferencesController.getController().getJabRefAttributes();
			NodeAttributeTableModel table = (NodeAttributeTableModel) e.getSource();
			Attribute attribute = table.getAttribute(e.getFirstRow());
			

			String key = null;
			if (jabRefAttributes.getValueAttributes().containsKey(attribute.getName())) {				
				int pos = table.getAttributePosition(jabRefAttributes.getKeyAttribute());				
				key = (String) table.getValue(pos);
				
				if (key != null) {
					LogUtils.info("debug changed for [" + key + "]: " + attribute.getName() + " --> " + attribute.getValue());
					updateBibtexEntry(key, attribute);
				}
			}
		}
		
		locked = false;
	}

	private void updateBibtexEntry(String key, Attribute attribute) {
		BibtexDatabase database = ReferencesController.getController().getJabrefWrapper().getDatabase(); 
		BibtexEntry entry = database.getEntryByKey(key);
		if (entry != null) {
			entry.setField(ReferencesController.getController().getJabRefAttributes().getValueAttributes().get(attribute.getName()), attribute.getValue().toString());			
			ReferencesController.getController().getJabrefWrapper().updateDatabase(database);
		}
	}
	
	

}