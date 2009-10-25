/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Dimitry
 *
 *  This file author is Dimitry
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
package org.freeplane.main.mindmapmode.stylemode;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;

import org.freeplane.core.frame.IMapViewManager;
import org.freeplane.core.frame.ViewController;
import org.freeplane.core.modecontroller.MapChangeEvent;
import org.freeplane.core.modecontroller.MapController;
import org.freeplane.core.model.MapModel;
import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.undo.IUndoHandler;
import org.freeplane.features.common.addins.mapstyle.MapStyle;
import org.freeplane.features.common.addins.mapstyle.MapStyleModel;
import org.freeplane.features.mindmapmode.MModeController;

/**
 * @author Dimitry Polivaev
 * 13.09.2009
 */
public class EditStylesAction extends AFreeplaneAction {
	public EditStylesAction(MModeController mainModeController) {
	    super("EditStylesAction", mainModeController.getController());
    }

	private void init() {
		if(dialog != null){
			return;
		}
	    dialog = new JDialog(getController().getViewController().getJFrame());
		dialog.setSize(800, 300);
		modeController = SModeControllerFactory.getInstance().createModeController(dialog);
		final ViewController viewController = modeController.getController().getViewController();
		viewController.init();
		viewController.setLeftToolbarVisible(false);
		dialog.addComponentListener(new ComponentAdapter() {

			@Override
            public void componentHidden(ComponentEvent e) {
	            final IMapViewManager mapViewManager = modeController.getController().getMapViewManager();
	            MapModel map = mapViewManager.getModel();
	            final IUndoHandler undoHandler = (IUndoHandler)map.getExtension(IUndoHandler.class);
				if(undoHandler.canUndo()){
					final MapModel currentMap = getController().getMap();
	            	getModeController().getMapController().setSaved(currentMap, false);
	            	final MapController mapController = modeController.getMapController();
					mapController.setSaved(map, true);
					getModeController().getMapController().fireMapChanged(new MapChangeEvent(map, currentMap, MapStyle.MAP_STYLES, null, null));
	            }
				mapViewManager.close(true);
	            super.componentHidden(e);
            }
			
		});
    }

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private JDialog dialog;
	private SModeController modeController;

	public void actionPerformed(ActionEvent e) {
		init();
		final MapModel map = getController().getMap();
		final MapStyleModel mapStyleModel = MapStyleModel.getExtension(map);
		final MapModel styleMap = mapStyleModel.getStyleMap();
		mapStyleModel.getBackgroundColor();
		modeController.getMapController().newMapView(styleMap);
		dialog.setLocationRelativeTo(getController().getViewController().getJFrame());
		dialog.setVisible(true);
	}
}