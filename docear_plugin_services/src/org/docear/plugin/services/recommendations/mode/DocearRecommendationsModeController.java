package org.docear.plugin.services.recommendations.mode;

import java.net.URL;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.docear.plugin.core.DocearController;
import org.docear.plugin.core.event.DocearEvent;
import org.docear.plugin.services.recommendations.actions.RecommendationsRefreshAction;
import org.freeplane.core.resources.ResourceBundles;
import org.freeplane.core.ui.MenuBuilder;
import org.freeplane.core.ui.components.FreeplaneToolBar;
import org.freeplane.core.ui.components.JResizer.Direction;
import org.freeplane.core.ui.components.OneTouchCollapseResizer;
import org.freeplane.core.ui.components.OneTouchCollapseResizer.CollapseDirection;
import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.clipboard.ClipboardController;
import org.freeplane.features.cloud.CloudController;
import org.freeplane.features.edge.EdgeController;
import org.freeplane.features.filter.FilterController;
import org.freeplane.features.icon.IconController;
import org.freeplane.features.link.LinkController;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.map.filemode.CenterAction;
import org.freeplane.features.map.filemode.OpenPathAction;
import org.freeplane.features.mapio.MapIO;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.mode.IPropertyHandler;
import org.freeplane.features.mode.ModeController;
import org.freeplane.features.nodelocation.LocationController;
import org.freeplane.features.nodestyle.NodeStyleController;
import org.freeplane.features.styles.LogicalStyleController;
import org.freeplane.features.styles.MapStyle;
import org.freeplane.features.text.TextController;
import org.freeplane.features.ui.ViewController;
import org.freeplane.features.url.UrlManager;
import org.freeplane.view.swing.features.nodehistory.NodeHistory;
import org.freeplane.view.swing.ui.UserInputListenerFactory;

public class DocearRecommendationsModeController extends ModeController {
	static public final String MODENAME = "DcrRecommendations";

	private DocearRecommendationsModeController(final Controller controller) {
		super(controller);
		setLanguage();
	}

	@Override
	public String getModeName() {
		return DocearRecommendationsModeController.MODENAME;
	}

	@Override
	public void startup() {
		DocearController.getController().dispatchDocearEvent(new DocearEvent(this, "DOCEAR_MODE_STARTUP"));
		final Controller controller = getController();
		this.getUserInputListenerFactory().getMapPopup().setName(TextUtils.getText("recommendations.popup_title"));
		controller.getMapViewManager().changeToMode(MODENAME);
		if (controller.getMap() == null) {
			((DocearRecommendationsMapController) getMapController()).newMap();
		}
		super.startup();
	}

	@Override
	public void shutdown() {
		super.shutdown();
		DocearController.getController().dispatchDocearEvent(new DocearEvent(this, "DOCEAR_MODE_SHUTDOWN"));
	}

	public static DocearRecommendationsModeController createController() {
		final Controller controller = Controller.getCurrentController();
		return createController(controller);
	}

	public DocearRecommendationsMapController getMapController() {
		return (DocearRecommendationsMapController) super.getMapController();
	}

	public static DocearRecommendationsModeController createController(Controller controller) {
		DocearRecommendationsModeController modeController = new DocearRecommendationsModeController(controller);

		final UserInputListenerFactory userInputListenerFactory = new UserInputListenerFactory(modeController);
		modeController.setUserInputListenerFactory(userInputListenerFactory);

		controller.addModeController(modeController);
		controller.selectModeForBuild(modeController);
		

		controller.addAction(new RecommendationsRefreshAction());

		new DocearRecommendationsMapController(modeController);

		UrlManager.install(new UrlManager());
		MapIO.install(modeController);
		IconController.install(new IconController(modeController));
		NodeStyleController.install(new NodeStyleController(modeController));
		EdgeController.install(new EdgeController(modeController));
		TextController.install(new TextController(modeController));
		LinkController.install(new LinkController());
		CloudController.install(new CloudController(modeController));
		ClipboardController.install(new ClipboardController());
		LocationController.install(new LocationController());
		LogicalStyleController.install(new LogicalStyleController(modeController));
		MapStyle.install(true);
		NodeStyleController.getController().addShapeGetter(new Integer(0), new IPropertyHandler<String, NodeModel>() {
			public String getProperty(final NodeModel node, final String currentValue) {
				return "fork";
			}
		});
		modeController.addAction(new CenterAction());
		modeController.addAction(new OpenPathAction());

		userInputListenerFactory.setNodePopupMenu(new JPopupMenu());
		final FreeplaneToolBar toolBar = new FreeplaneToolBar("main_toolbar", SwingConstants.HORIZONTAL);
		toolBar.putClientProperty(ViewController.VISIBLE_PROPERTY_KEY, "toolbarVisible");
		userInputListenerFactory.addToolBar("/main_toolbar", ViewController.TOP, toolBar);
		userInputListenerFactory.addToolBar("/filter_toolbar", ViewController.TOP, FilterController.getCurrentFilterController().getFilterToolbar());
		userInputListenerFactory.addToolBar("/status", ViewController.BOTTOM, controller.getViewController().getStatusBar());
		NodeHistory.install(modeController);

		Box resisableTabs = Box.createHorizontalBox();
		resisableTabs.add(new OneTouchCollapseResizer(Direction.RIGHT, CollapseDirection.COLLAPSE_RIGHT));
		resisableTabs.add(new JTabbedPane());
		userInputListenerFactory.addToolBar("/format", ViewController.RIGHT, resisableTabs);

		modeController.updateMenus(userInputListenerFactory, modeController.getClass().getResource("/xml/recommendationsMode.xml"));

		return modeController;
	}

	private void updateMenus(UserInputListenerFactory userInputListenerFactory, URL resource) {
		updateMenus(resource.toExternalForm(), Collections.<String> emptySet());
		final MenuBuilder menuBuilder = userInputListenerFactory.getMenuBuilder();
		final boolean isUserDefined = resource.getProtocol().equalsIgnoreCase("file");
		try {
			menuBuilder.processMenuCategory(resource, Collections.<String> emptySet());
		}
		catch (RuntimeException e) {
			if (isUserDefined) {
				LogUtils.warn(e);
			}
		}
		final ViewController viewController = Controller.getCurrentController().getViewController();
		viewController.updateMenus(menuBuilder);
		
	}
	
	public void setLanguage() {
		final String DEFAULT_LANGUAGE = "en";
		ResourceBundles resBundle = ((ResourceBundles)Controller.getCurrentController().getResourceController().getResources());
		String lang = resBundle.getLanguageCode();
		if (lang == null || lang.equals(ResourceBundles.LANGUAGE_AUTOMATIC)) {
			lang = DEFAULT_LANGUAGE;
		}
		
		URL res = this.getClass().getResource("/translations/Resources_"+lang+".properties");
		if (res == null) {
			lang = DEFAULT_LANGUAGE;
			res = this.getClass().getResource("/translations/Resources_"+lang+".properties");
		}
		
		if (res == null) {
			return;
		}
				
		resBundle.addResources(resBundle.getLanguageCode(), res);
	}
	
}
