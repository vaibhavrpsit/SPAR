/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BaseBeanAdapter.java /main/25 2014/06/03 17:06:10 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/14/11 - tweak search by credit debit and gift card number
 *    cgreene   11/08/10 - Add call to request focus on window when bean is
 *                         deactivated.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    cgreene   06/22/09 - remove window listener from applicationFrame during
 *                         deactivate
 *    cgreene   06/18/09 - added javadoc about not using anonymous adapters
 *    acadar    04/23/09 - removed style
 *    acadar    04/23/09 - more cleanup
 *    acadar    04/22/09 - refactoring
 *    acadar    04/22/09 - additional methods added to the DateTimeService
 *    acadar    04/22/09 - refactoring
 *    acadar    04/22/09 - refactoring changes
 *    acadar    04/22/09 - translate date/time labels
 *    acadar    04/21/09 - additional changes for label translation
 *    acadar    04/21/09 - addtional changes to return default
 *    mdecama   02/06/09 - Added LookAndFeel support by Locale
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         7/11/2007 11:07:30 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    5    360Commerce 1.4         5/24/2007 2:43:53 PM   Michael P. Barnett In
 *          setCurrentFocus(), test component for null before assigning it
 *         focus.
 *    4    360Commerce 1.3         1/25/2006 4:10:50 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:33 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/8/2005 11:24:03     Jason L. DeLeau 6614:
 *         Add SS, NaPhone to UIFactory, add methods to take String's as
 *         parameters for constraintedTextAreas , fix possible NPE
 *    3    360Commerce1.2         3/31/2005 15:27:16     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:19:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:33     Robert Pearse
 *
 *   Revision 1.7  2004/08/18 20:59:06  jdeleau
 *   @scr 6829 Add the comments requested by code review
 *
 *   Revision 1.6  2004/08/16 19:55:49  jdeleau
 *   @scr 6829 Focus events not being listened to when the bean first
 *   appeared (currentWindow would be null in the activate method
 *   because setVisible wasnt called yet).  Also the request of focus was occuring
 *   before the parent window had focus in some instances.
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Nov 24 2003 11:55:56   baa
 * guarantee focus in window
 * Resolution for 3436: Focus lost on Global Nav keys when returning to Report Options screen after running Till Summary Report
 *
 *    Rev 1.2   Sep 10 2003 15:10:12   dcobb
 * Add WindowFocusListener in activate() instead of setVisible().
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.1   Sep 08 2003 17:30:38   DCobb
 * Migration to jvm 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:09:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jul 24 2003 14:44:00   baa
 * add system property BUNDLE_TESTING to allow brakets on property names when not found in bundles
 * Resolution for 2169: Modify base retrieve text methods to include <default text>
 *
 *    Rev 1.5   Apr 16 2003 12:23:10   baa
 * defect fixes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.4   Sep 24 2002 14:10:18   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Aug 07 2002 19:34:08   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   05 Jun 2002 22:02:40   baa
 * support for  opendrawerfortrainingmode parameter
 * Resolution for POS SCR-1645: Training Mode Enhancements
 *
 *    Rev 1.1   13 May 2002 15:50:30   pdd
 * Freeing the model on deactivate().
 * Resolution for POS SCR-1635: Performance improvements
 *
 *    Rev 1.0   Apr 29 2002 14:47:42   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:52:16   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 23 2002 15:04:10   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Feb 12 2002 18:57:22   mpm
 * Added support for text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Jan 19 2002 12:10:10   mpm
 * Fixed merge problems.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0.1.0   Jan 19 2002 10:29:14   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   11 Dec 2001 14:02:06   dwt
 * internationalization support
 *
 *    Rev 1.0   Sep 21 2001 11:34:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:18:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIBeanIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

import org.apache.log4j.Logger;

/**
 * Bean adapter for all screen beans.
 * 
 * @version $Revision: /main/25 $
 */
abstract public class BaseBeanAdapter extends JPanel
    implements EYSPOSBeanIfc, FocusListener, WindowFocusListener
{
    private static final long serialVersionUID = -1185880510181575152L;

    /** Store instance of logger here **/
    protected static final Logger logger = Logger.getLogger(BaseBeanAdapter.class);

    /** revision number supplied by Team Connection **/
    public static String revisionNumber = "$Revision: /main/25 $";

    /** the common tag for property values */
    public static final String COMMON = "COMMON.";

    /** Add -DBUNDLE_TESTING to system properties to enable this testing flag. */
    public static final boolean BUNDLE_TESTING = (System.getProperty("BUNDLE_TESTING") != null);

    /** the bean prefix for look and feel attributes */
    public String UI_PREFIX = "BaseBean";

    /** the bean prefix for lookup and feel attributes for a TabbedUIBean */
    public String TABBED_UI_PREFIX = "TabbedUIBean";

    /** the ui prefix for label attributes */
    public String UI_LABEL = UI_PREFIX + ".label";

    /** the name of the bean spec that defines this bean */
    protected String beanSpecName = null;

    /** a properties object that contains localized text */
    protected Properties props = null;

    /** the name of the resource bundle that populates the properties */
    protected String resourceBundleFilename = null;

    /** the data model for this bean */
    protected POSBaseBeanModel beanModel;

    /** The current window. May be an instance of {@link ApplicationFrame}. */
    protected Window currentWindow = null;
    /** Legacy reference to window this bean is displayed in. */
    private static ApplicationFrame applicationFrame = null;

    /**  The current component    */
    protected JComponent currentComponent = null;

    /** the ui factory for ui components */
    protected UIFactory uiFactory = UIFactory.getInstance();

    /**
     * CurrencyService
     */
    protected static CurrencyServiceIfc currencyService = null;

    /**
     * Translated date pattern
     */
    protected  String translatedDatePattern = null;

    /**
     * Gets the CurrencyService
     */
    protected static CurrencyServiceIfc getCurrencyService()
    {
        if (currencyService == null)
        {
            currencyService = CurrencyServiceLocator.getCurrencyService();
        }
        return currencyService;
    }

    /**
     * Gets the translated date pattern
     *
     * @return String
     */
    protected String getTranslatedDatePattern()
    {
        //call the DateTimeService to replace the chars in the date/time pattern with the translated values
        String localPatternChars = retrieveTextFallbackToDefault("DateTimePatternLabel.LocalPatternChars", "GyMdkHmsSEDFwWahKzZ");
        translatedDatePattern = LocaleUtilities.getTranslatedDatePattern(getDefaultLocale(), DateFormat.SHORT, localPatternChars);

        return translatedDatePattern;
    }

    /**
     * Gets the translated date pattern
     *
     * @return String
     */
    protected String getTranslatedDatePattern(String patternChars)
    {
        //call the DateTimeService to replace the chars in the date/time pattern with the translated values
        String localPatternChars = retrieveTextFallbackToDefault("DateTimePatternLabel.LocalPatternChars", "GyMdkHmsSEDFwWahKzZ");
        translatedDatePattern = LocaleUtilities.getTranslatedDatePattern(getDefaultLocale(), patternChars, localPatternChars);

        return translatedDatePattern;
    }

    /**
     * Constructor, invokes super().
     */
    public BaseBeanAdapter()
    {
        super();
    }

    /**
     * Constructor that defines the layout manager.
     *
     * @param layout the layout manager
     */
    public BaseBeanAdapter(LayoutManager mgr)
    {
        super(mgr);
    }

    /**
     * Empty method for configure. This method is called when the bean is first
     * instantiated.
     */
    public void configure()
    {
    }

    /**
     * This is called just before displaying the bean. It adds this instance as
     * a {@link WindowFocusListener} to the current window.
     * <p>
     * Subclasses should use this method for preparing the bean before each
     * display. If overridden, be sure to call this method to retain its
     * behavior.
     * <P>
     * IMPORTANT: Listeners added to widgets in during this method should be
     * removed during {@link #deactivate()}. Avoid using anonymous listeners
     * that can't be removed later (since you won't have a reference).
     */
    public void activate()
    {
        if (currentWindow != null)
        {
            currentWindow.addWindowFocusListener(this);
        }
        else if(getApplicationFrame() != null)
        {
            getApplicationFrame().addWindowFocusListener(this);
        }
    }

    /**
     * This method is called just before removing the bean from the screen. It
     * removes this instance as a {@link WindowFocusListener} to the current
     * window. The focus is requested onto the window to remove it from any
     * widget in the deactivated bean.
     * <p>
     * Subclasses can override this to clean up the bean after each display,
     * e.g. remove any listeners added during the {@link #activate()} phase. If
     * overridden, be sure to call this method to retain its behavior.
     */
    public void deactivate()
    {
        beanModel = null;
        if (currentWindow != null)
        {
            currentWindow.removeWindowFocusListener(this);
            currentWindow.requestFocusInWindow();
        }
        else
        {
            ApplicationFrame frame = getApplicationFrame();
            if (frame != null)
            {
                frame.removeWindowFocusListener(this);
                frame.requestFocusInWindow();
            }
        }
    }

    /**
     * Only calls {@link #setModel(UIModelIfc)}. This method has no other popup
     * functionality.
     *
     * @see oracle.retail.stores.foundation.manager.gui.UIBeanIfc#popupMenu(String, UIBeanIfc, UIModelIfc)
     */
    @Override
    public void popupMenu(String menuName, UIBeanIfc childBean, UIModelIfc model)
    {
        setModel(model);
    }

    /**
     * Gets the name of the bean spec that defines this bean.
     *
     * @return the bean spec name
     */
    public String getBeanSpecName()
    {
        return beanSpecName;
    }

    /**
     * Gets the application frame
     *
     * @return the application frame
     */
    public ApplicationFrame getApplicationFrame()
    {
        if (applicationFrame == null)
        {
            java.awt.Component frame = SwingUtilities.getRoot(this);

            if (frame != null && frame instanceof ApplicationFrame)
            {
                applicationFrame = (ApplicationFrame)frame;
            }
        }

        return applicationFrame;
    }

    /**
     * Sets the application background
     *
     * @return the application frame
     */
    public void setApplicationBackground(boolean trainingMode)
    {
        // change background color if running swing base
        if (getApplicationFrame() != null)
        {
            if (applicationFrame instanceof ImageAppFrame)
            {
                ((ImageAppFrame) applicationFrame).setTrainingMode(trainingMode);
            }
            else
            {
                applicationFrame.setTrainingMode(trainingMode);
            }
        }
    }

    /**
     * Returns this bean as a JComponent.
     *
     * @return a reference to this bean.
     */
    public JComponent getJComponent()
    {
        return this;
    }

    /**
     * Gets this bean's properties object that contains localized text.
     *
     * @return the properties object
     */
    public Properties getProps()
    {
        return props;
    }

    /**
     * Gets the name of the resource bundle that contains any localized text
     * that this bean might need.
     *
     * @return the resource bundle filename
     */
    public String getResourceBundleFilename()
    {
        return resourceBundleFilename;
    }

    /**
     * Sets the name of the bean spec that defines this bean.
     *
     * @param aValue the bean spec name
     */
    public void setBeanSpecName(String aValue)
    {
        beanSpecName = aValue;
    }

    /**
     * Sets the component that should be currently in focus.
     *
     * @param current the component that currently should be in focus.
     * @see #setCurrentFocus(JComponent)
     */
    public void setCurrentComponent(JComponent current)
    {
        currentComponent = current;
    }

    /**
     * Sets the component that currently has the focus.
     * 
     * @param current the component that currently has focus.
     */
    public void setCurrentFocus(JComponent current)
    {
        currentComponent = current;

        if (current != null)
        {
            current.requestFocusInWindow();
        }
    }

    /**
     * Sets the data that this bean will display.
     *
     * @param model the model to be shown.
     */
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set " +
                                           beanSpecName +
                                           "model to null.");
        }

        if (model instanceof POSBaseBeanModel)
        {
            beanModel = (POSBaseBeanModel)model;
            updateBean();
        }
    }

    /**
     * Sets the properties object.
     *
     * @param props the properties object.
     */
    public void setProps(Properties props)
    {
        this.props = props;
        updatePropertyFields();
    }

    /**
     * Gets the locale for the user interface subsystem properties object
     *
     * @returns locale for the user interface subsystem
     */
    public Locale getLocale()
    {
        Locale lcl = null;
        if (super.getLocale() == null)
        {
        	lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            setLocale(lcl);
        }
        else
        {
            lcl = super.getLocale();
        }
        return lcl;
    }

    /**
     * Gets the locale for the user interface subsystem properties object.
     *
     * @returns locale for the user interface subsystem
     */
    static public Locale getDefaultLocale()
    {
        return (LocaleMap.getLocale(LocaleMap.DEFAULT));
    }

    /**
     * Sets the name of the resource bundle that populated the properties
     * object.
     *
     * @param aValue the bundle filename
     */
    public void setResourceBundleFilename(String aValue)
    {
        resourceBundleFilename = aValue;
    }

    /**
     * Empty method for updateModel. Updates the model from the bean in
     * preperation for sending the model back to the business logic.
     */
    public void updateModel()
    {
    }

    /**
     * Retrieves a localized text string from the resource bundle, using the
     * specified label's text as the default.
     * <p>
     * To activate the BUNDLE_TESTING flag, the application has to be run with 
     * -DBUNDLE_TESTING. This method will add angle brackets to the property
     * names to help determine if text is coming from bundles or the default
     * values. 
     *
     * @param tag a property bundle tag for localized text
     * @return a localized text string
     */
    protected String retrieveText(String tag)
    {
        String result = "";
        if (tag != null)
        {
            if (props != null)
            {
               if (BUNDLE_TESTING)
               {
                   result = props.getProperty(tag, "<" + tag + ">");
               }
               else
               {
                   result = props.getProperty(tag, tag);
               }
            }
        }
        return result;
    }

    /**
     * Retrieves a localized text string from the resource bundle.
     *
     * @param tag a property bundle tag for localized text
     * @param defaultText a plain text value to use as a default
     * @return a localized text string
     */
    protected String retrieveText(String tag, String defaultText)
    {
        // make sure default text is not null
        defaultText = (Util.isEmpty(defaultText))? "" : defaultText;
        // get text from bundles
        String text = retrieveText(tag);
        // return default if bundle text was not found
        return (Util.isEmpty(text))? defaultText : text;
    }

    /**
     * Retrieves a localized text string from the resource bundle.
     *
     * @param tag a property bundle tag for localized text
     * @param defaultText a plain text value to use as a default
     * @return a localized text string
     */
    protected String retrieveTextFallbackToDefault(String tag, String defaultText)
    {
        String result = (retrieveText(tag));
        if(result.equals(tag))
        {
            result = defaultText;
        }
        return result;
    }

    /**
     * Retrieves a localized text string from the resource bundle, using the
     * specified label's text as the default.
     *
     * @param tag a property bundle tag for localized text
     * @param label object
     * @return a localized text string
     */
    protected String retrieveText(String tag, JLabel label)
    {
        return (retrieveText(tag));
    }

    /**
     * Empty method for updateBean. Subclasses should override this method to
     * transfer model data to the display.
     */
    protected void updateBean()
    {
    }

    /**
     * Empty method for updatePropertyFields. Subclasses should use this method
     * to extract component text from the properties object.
     */
    protected void updatePropertyFields()
    {
    }

    /**
     * Set the focus for the screen.
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible)
        {
            currentWindow = SwingUtilities.getWindowAncestor(this);
        }
    }

    /**
     * Invoked when the component is no longer the focused component, which
     * means that keyboard events will no longer be delivered to the component
     *
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e)
    {
    }

    /**
     * Invoked when the Component is set to be the focused Component, which
     * means that the Component will receive keyboard events.
     *
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e)
    {
         if (currentComponent != null && currentComponent.equals(e.getComponent()))
         {
             // Once focus is obtained for the window
             // request focus for the component
             currentComponent.requestFocusInWindow();
         }
    }

    /**
     * Invoked when the Window is set to be the focused Window, which means that
     * the Window, or one of its subcomponents, will receive keyboard events.
     *
     * @see java.awt.event.WindowFocusListener#windowGainedFocus(java.awt.event.WindowEvent)
     */
    public void windowGainedFocus(final WindowEvent e)
    {
         if (currentComponent != null)
         {
             // The request is put on the awt event queue and invoked after the current events
             // are finished, because subsequent events may cause the component
             // to lose focus.
            SwingUtilities.invokeLater(new Runnable()
             {
                 public void run()
                 {
                     // Once focus is obtained for the window
                     // request focus for the component.
                     requestFocusInWindow();
                     if(currentComponent != null)
                     {
                    	 currentComponent.requestFocusInWindow();
                     }
                 }
             });
         }
    }

    /**
     * Invoked when the Window is no longer the focused Window, which means that
     * keyboard events will no longer be delivered to the Window or any of its
     * subcomponents.
     *
     * @see java.awt.event.WindowFocusListener#windowLostFocus(java.awt.event.WindowEvent)
     */
    public void windowLostFocus(WindowEvent e)
    {
    }
}
