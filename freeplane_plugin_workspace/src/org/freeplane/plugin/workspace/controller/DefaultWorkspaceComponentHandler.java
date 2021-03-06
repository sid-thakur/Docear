/**
 * author: Marcel Genzmehr
 * 10.08.2011
 */
package org.freeplane.plugin.workspace.controller;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.freeplane.core.resources.ResourceController;
import org.freeplane.features.mode.Controller;
import org.freeplane.plugin.workspace.WorkspacePreferences;

/**
 * 
 */
public class DefaultWorkspaceComponentHandler implements ComponentListener {

	private final Component component;
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	public DefaultWorkspaceComponentHandler(final Component comp) {
		this.component = comp;
	}
	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
	public void componentResized(ComponentEvent e) {
		ResourceController resCtrl = Controller.getCurrentController().getResourceController();
		if (resCtrl.getBooleanProperty(WorkspacePreferences.SHOW_WORKSPACE_PROPERTY_KEY)
				&& e.getComponent() == this.component) {
			resCtrl.setProperty(WorkspacePreferences.WORKSPACE_WIDTH_PROPERTY_KEY, String.valueOf(e.getComponent().getWidth()));
		}
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}
}
