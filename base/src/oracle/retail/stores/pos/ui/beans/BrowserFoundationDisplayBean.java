/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BrowserFoundationDisplayBean.java /main/17 2013/11/01 17:22:10 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 11/01/13 - convert back to jframe to use window decorations
 *    rabhaw 10/31/13 - removed jframe and used popup
 *    rabhaw 10/23/13 - capturing the window.close() event for popup.
 *    rabhaw 08/20/13 - popup should be on top.
 *    rabhaw 08/30/12 - using javafx for embedded browser
 *    abonda 01/03/10 - update header date
 *    nkgaut 11/14/08 - Changes for removing JDIC Componnents from POS
 *    nkgaut 09/30/08 - Bean class for browser foundation
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.management.ObjectName;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.org.glassfish.external.amx.MBeanListener;
import com.sun.org.glassfish.external.amx.MBeanListener.Callback;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.pos.services.browserfoundation.BrowserFoundationDisplayBeanModel;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.behaviour.BrowserBackActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserForwardActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserHomeActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserRefreshActionListener;
import oracle.retail.stores.pos.ui.behaviour.BrowserStopActionListener;

/**
 * A Swing bean that uses a {@link JFXPanel} to embed a JavaFX scene from which
 * a {@link WebView} can used to render HTML content.
 */
public class BrowserFoundationDisplayBean extends BaseBeanAdapter implements BrowserBackActionListener,
        BrowserForwardActionListener, BrowserHomeActionListener, BrowserRefreshActionListener,
        BrowserStopActionListener
{
    private static final long serialVersionUID = 6523315171106495668L;

    /**
     * @deprecated as of 14.0. Use {@link #getComponents()} if necessary.
     */
    protected ArrayList<Component> alcomp = new ArrayList<Component>();

    /**
     * WebEngine for the WebView of this bean.
     */
    protected WebEngine engine = null;

    /**
     * The primary Swing panel that can display JavaFX widgets.
     */
    protected JFXPanel fxPanel;

    /**
     * Configure the class.
     */
    @Override
    public void configure()
    {
        UI_PREFIX = "BrowserFoundationDisplayBean";
        setName("LaunchBrowserDisplayBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());
    }

    /**
     * Initialize the display components.
     */
    protected void initComponents()
    {
        fxPanel = new JFXPanel();

        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                createScene(getBrowserDisplayModel().getHomeURL(), fxPanel);
            }
        });
    }

    /**
     * Initialize the panel.
     */
    public void initLayout()
    {
        this.add(fxPanel, BorderLayout.CENTER);
    }

    /**
     * Update the bean if It's been changed. Resets and sets the layout again.
     */
    @Override
    protected void updateBean()
    {
        // lay out data panel
        removeAll();
        initComponents();
        initLayout();
        invalidate();
        validate();
    }

    /**
     * Sets the information to be shown by this bean.
     * 
     * @param model the model to be shown. The runtime type should be
     *            BrowserFoundationDisplayBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set BrowserFoundationDisplayBean's model to null.");
        }
        if (model instanceof BrowserFoundationDisplayBeanModel)
        {
            beanModel = (BrowserFoundationDisplayBeanModel)model;
            updateBean();
        }
        else
        {
            throw new ClassCastException("Attempt to set BrowserFoundationDisplayBean's model to " + model);
        }
    }

    /**
     * Gets the browser display model associated with this bean.
     * 
     * @return the browser display model associated with this bean
     */
    public BrowserFoundationDisplayBeanModel getBrowserDisplayModel()
    {
        return (BrowserFoundationDisplayBeanModel)beanModel;
    }

    /**
     * This method is called through a connection from the
     * GlobalBrowserNavigationButtonBean. Indicates which event process should
     * be performed based on the ActionCommand
     * 
     * @param event the ActionEvent contructed by the caller.
     */
    public void actionPerformed(ActionEvent event)
    {

        Platform.runLater(new WebAction(event.getActionCommand(), getBrowserDisplayModel().getHomeURL()));
    }

    /**
     * Create a scene and set to JFXPanel
     * 
     * @param homeURL
     * @param fxPanel
     */
    protected void createScene(String homeURL, JFXPanel fxPanel)
    {
        WebView myBrowser = new WebView();
        engine = myBrowser.getEngine();

        engine.setCreatePopupHandler(new PopupCallback());
        engine.load(homeURL);
        fxPanel.setScene(new Scene(myBrowser));
    }

    /**
     * Returns a square 500 x 500. This is the same size coded into the web app 
     * HTML for showing the About and Help dialogs.
     *
     * @return
     */
    protected Dimension getPopupWindowSize()
    {
        // TODO find good way to determine from the Javascript showing the
        // dialogs what the size of the window is supposed to be.
        return new Dimension(500, 500);
    }

    // -------------------------------------------------------------------------
    /**
     * Handling of pop-up windows in browser.
     */
    protected class PopupCallback implements Callback, javafx.util.Callback<PopupFeatures, WebEngine>
    {

        /* (non-Javadoc)
         * @see javafx.util.Callback#call(java.lang.Object)
         */
        public WebEngine call(PopupFeatures popupFeatures)
        {
            /*
             * Using JFrame because the popup has to have window decorations for
             * the Help screen and also the popup has to maintain its Z-Order
             * above the main window. The JavaFX Popup works nicely but doesn't
             * have window decorations. The JavaFX Stage class throws a
             * ClassCastException when using the JFXPanel's scene window as the
             * parent window.
             */
            final JFrame popupWindow = new JFrame();
            popupWindow.setSize(getPopupWindowSize());
            popupWindow.setAlwaysOnTop(true);
            // build a webview and put it in a scene
            final WebView popupWebView = new WebView();
            Scene popupScene = new Scene(popupWebView);
            // embed webview into a swing panel
            JFXPanel embeddedJavaFX = new JFXPanel();
            embeddedJavaFX.setScene(popupScene);
            popupWindow.add(embeddedJavaFX);

            final WebEngine popupEngine = popupWebView.getEngine();
            popupEngine.setCreatePopupHandler((javafx.util.Callback<PopupFeatures, WebEngine>) new PopupCallback());
            popupEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>()
            {
                @Override
                public void changed(ObservableValue<? extends State> ov, State oldState, State newState)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Popup window in embedded browser state: " + newState);
                    }
                    if (Worker.State.SUCCEEDED.equals(oldState))
                    {
                        if (popupEngine.getLocation().endsWith("legacy_return=1"))
                        {
                            popupWindow.dispose();
                        }
                    }
                }
            });

            // handle window.close() javascript in webpage
            popupEngine.setOnVisibilityChanged(new EventHandler<WebEvent<Boolean>>()
            {
                @Override
                public void handle(WebEvent<Boolean> param)
                {
                    if (!param.getData())
                    {
                        popupWindow.dispose();
                    }
                }
            });

            // sets the pop-up title.
            popupEngine.titleProperty().addListener(new ChangeListener<String>()
            {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue)
                {
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            popupWindow.setTitle(newValue);
                        }
                    });
                }
            });

            // show the popup and return the webengine for driving to the target url
            popupWindow.setVisible(true);
            return popupEngine;
        }

		@Override
		public void mbeanRegistered(ObjectName arg0, MBeanListener arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mbeanUnregistered(ObjectName arg0, MBeanListener arg1) {
			// TODO Auto-generated method stub
			
		}
    }

    // -------------------------------------------------------------------------
    /**
     * Class is responsible for handling the navigation buttons.
     */
    protected class WebAction implements Runnable
    {
        private String action = null;
        private String homeURL = null;

        public WebAction(String a, String hu)
        {
            action = a;
            homeURL = hu;
        }

        @Override
        public void run()
        {
            switch (WebActionType.valueOf(action))
            {
            case Back:
                engine.executeScript("history.back()");
                break;
            case Forward:
                engine.executeScript("history.forward()");
                break;
            case Refresh:
                engine.reload();
                break;
            case Home:
                engine.load(homeURL);
                break;
            case Stop:
                engine.getLoadWorker().cancel();
                break;
            case Close:
                closeDialog();
                break;
            }
        }

        protected void closeDialog()
        {
            POSJFCUISubsystem ui = (POSJFCUISubsystem)UISubsystem.getInstance();

            try
            {
                ui.closeDialog();
            }
            catch (UIException e)
            {
                logger.error("Exception: ", e);
            }
        }
    }
}

enum WebActionType
{
    Close, Back, Forward, Refresh, Home, Stop;
}