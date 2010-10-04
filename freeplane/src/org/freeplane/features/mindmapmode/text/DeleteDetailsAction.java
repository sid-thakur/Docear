/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.mindmapmode.text;

import java.awt.event.ActionEvent;

import org.freeplane.core.controller.Controller;
import org.freeplane.core.ui.AMultipleNodeAction;
import org.freeplane.core.ui.EnabledAction;
import org.freeplane.features.common.map.NodeModel;
import org.freeplane.features.common.text.DetailTextModel;

@EnabledAction(checkOnNodeChange=true)
class DeleteDetailsAction extends AMultipleNodeAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeleteDetailsAction() {
		super("DeleteDetailsAction");
	}

	@Override
    protected void actionPerformed(ActionEvent e, NodeModel node) {
		final DetailTextModel detailText = DetailTextModel.getDetailText(node);
		if(detailText == null){
			return;
		}
		MTextController controller = (MTextController) MTextController.getController();
		controller.setDetailsHidden(node, false);
		controller.setDetails(node, null);
    }

	@Override
	public void setEnabled() {
		final NodeModel node = Controller.getCurrentModeController().getMapController().getSelectedNode();
		setEnabled(node != null && DetailTextModel.getDetailText(node) != null);
	}
}