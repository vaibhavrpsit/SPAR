/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/UIUtilities.java /main/39 2014/06/09 17:10:00 abondala Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    abonda 06/09/14 - initialize map
*    abonda 06/02/14 - mpos notifications distribution
*    abonda 05/28/14 - notificaitons available indicator message to registers
*    cgreen 05/20/14 - refactor list model sorting
*    arabal 02/04/14 - released the stream handles
*    cgreen 01/10/14 - update to swanplaf
*    arabal 12/16/13 - released the Stream handles
*    mjwall 11/21/12 - ui button changes
*    cgreen 10/15/12 - implement buttons that can use images to paint
*                      background
*    cgreen 12/05/11 - updated from deprecated packages and used more
*                      bigdecimal constants
*    blarse 06/30/11 - Added setFinancialNetworkUIStatus().
*    rrkohl 05/09/11 - adding getStatusBean() method in utility class for POS
*                      UI quickwin
*    cgreen 03/22/11 - XbranchMerge cgreene_124_receipt_quick_wins_part2 from
*                      main
*    cgreen 03/22/11 - get parameter manager from dispatcher
*    npoola 11/11/10 - fixed the layoutDataPanel method to pass addWeight to
*                      false for single component
*    cgreen 10/27/10 - do not add fill if addWeight is false
*    cgreen 10/25/10 - add method addDualListPanel
*    cgreen 10/22/10 - added ability to format since row widgets onto entire
*                      screen
*    blarse 06/09/10 - XbranchMerge blarsen_biometrics-poc from
*                      st_rgbustores_techissueseatel_generic_branch
*    cgreen 05/26/10 - convert to oracle packaging
*                      fingerprint prettier.)*    cgreen 05/26/10 - convert to oracle packaging
*    blarse 05/25/10 - Enhanced layoutComponent() to support the vertical
*                      centering of components. (To make new login w/
*    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
*                      st_rgbustores_techissueseatel_generic_branch
*    cgreen 04/05/10 - get image using best match locale
*    cgreen 01/14/10 - added method to check for parameter boolean status
*    cgreen 01/05/10 - implement parameter enabled bean spec
*    abonda 01/03/10 - update header date
*    sswamy 03/31/09 - Updated to handle Null argument for
*                      makeSafeStringForDisplay()
*    nkgaut 03/25/09 - A new method added makeSafeStringForDisplay() for
*                      truncating long decription of items
*    mchell 03/13/09 - Fixed errors on Detail Currency (foreign currency)
*                      screen
*    abonda 03/05/09 - get reasoncode text entries from the database, not from
*                      the bundles.
*    mdecam 02/23/09 - Added alternativeLayoutDataPanel() and wrapJLabelText()
*                      methods.
*    mdecam 02/12/09 - Added LookAndFeel support by Locale
*    mdecam 01/26/09 - Setting the constraints.fill property in the
*                      layoutDualPanel(). It allows us to visualize the
*                      predefined size of the component instead of letting the
*                      GridBag Layout stretch the components.
*    ranojh 10/09/08 - Changes for User Selection for Employee and Customer
*    nkgaut 09/29/08 - Overloaded the method layoutdatapanel with one argument
*                      as Component
*
* ===========================================================================
    $Log:
     9    360Commerce 1.8         10/8/2007 1:56:14 PM   Peter J. Fierro Add a
          method that lays out dual columns for CurrencyDetailBean
     8    360Commerce 1.7         11/27/2006 5:38:36 PM  Charles D. Baker CR
          21362 - Reintroducing previously deleted funcationlity for special
          order location behavior previously removed by inventory
     7    360Commerce 1.6         5/12/2006 5:25:33 PM   Charles D. Baker
          Merging with v1_0_0_53 of Returns Managament
     6    360Commerce 1.5         5/4/2006 5:11:51 PM    Brendan W. Farrell
          Remove inventory.
     5    360Commerce 1.4         4/27/2006 7:07:08 PM   Brett J. Larsen CR
          17307 - inventory functionality removal - stage 2
     4    360Commerce 1.3         1/25/2006 4:11:53 PM   Brett J. Larsen merge
          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
     3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse
     2    360Commerce 1.1         3/10/2005 10:26:30 AM  Robert Pearse
     1    360Commerce 1.0         2/11/2005 12:15:22 PM  Robert Pearse
    $:
     4    .v700     1.2.1.0     11/8/2005 11:24:05     Jason L. DeLeau 6614:
          Add SS, NaPhone to UIFactory, add methods to take String's as
          parameters for constraintedTextAreas , fix possible NPE
     3    360Commerce1.2         3/31/2005 15:30:38     Robert Pearse
     2    360Commerce1.1         3/10/2005 10:26:30     Robert Pearse
     1    360Commerce1.0         2/11/2005 12:15:22     Robert Pearse
    $
    Revision 1.4.4.2  2004/11/03 22:16:21  mweis
    @scr 7604 Allow inventory to behave correctly when database is offline.

    Revision 1.4.4.1  2004/10/15 18:50:30  kmcbride
    Merging in trunk changes that occurred during branching activity

    Revision 1.7  2004/10/12 18:53:52  mweis
    @scr 7012 Consolodate inventory UI model work under InventoryBeanModelIfc.

    Revision 1.6  2004/10/11 21:35:17  mweis
    @scr 7012 Begin consolidating inventory location loading for Layaways and Orders.

    Revision 1.5  2004/10/11 20:15:45  mweis
    @scr 7012 Consolodate the populating of inventory locations for the ItemLocationBeanModel.

    Revision 1.4  2004/04/01 00:11:34  cdb
    @scr 4206 Corrected some header foul ups caused by Eclipse auto formatting.


    Revision 1.1.1.1 2004/02/11 01:04:21 cschellenger
    updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.2 Feb 04 2004 09:55:34 kll ensure preferred language is compatible
 * with language list entities Resolution for 3748: The default for the
 * preferred language listbox should be English.
 *
 * Rev 1.1 Dec 30 2003 14:38:10 baa add method to load a property file for
 * testing beans Resolution for 3561: Feature Enhacement: Return Search by
 * Tender
 *
 * Rev 1.0 Aug 29 2003 16:09:28 CSchellenger Initial revision.
 *
 * Rev 1.13 Aug 08 2003 11:55:10 bliau Modified getImage so the
 * tracker.addImage call is inside the try block in case image is null.
 *
 * Rev 1.12 Jul 24 2003 14:43:58 baa add system property BUNDLE_TESTING to
 * allow brakets on property names when not found in bundles Resolution for
 * 2169: Modify base retrieve text methods to include <default text>
 *
 * Rev 1.11 Apr 16 2003 12:23:10 baa defect fixes Resolution for POS SCR-2098:
 * Refactoring of Customer Service Screens
 *
 * Rev 1.10 Apr 10 2003 11:41:34 bwf Added getReasonCodeTextEntries so that
 * UtilityManagerIfc references could be removed from all beans. Resolution for
 * 1866: I18n Database support
 *
 * Rev 1.9 Apr 02 2003 17:50:44 baa customer and screen changes Resolution for
 * POS SCR-2098: Refactoring of Customer Service Screens
 *
 * Rev 1.8 Mar 20 2003 18:18:56 baa customer screens refactoring Resolution for
 * POS SCR-2098: Refactoring of Customer Service Screens
 *
 * Rev 1.7 Sep 24 2002 14:09:32 baa retrieve domain descriptor text from
 * bundles Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.6 Sep 18 2002 17:15:26 baa country/state changes Resolution for POS
 * SCR-1740: Code base Conversions
 *
 * Rev 1.5 Sep 10 2002 16:50:28 DCobb Changed layoutComponent in UIUtilities to
 * set the fill for fields to NONE. Resolution for POS SCR-1811: Change
 * layoutComponent in UIUtilities to set the fill for fields to NONE
 *
 * Rev 1.4 Sep 05 2002 16:50:12 baa I18n changes Resolution for POS SCR-1740:
 * Code base Conversions
 *
 * Rev 1.3 Sep 03 2002 16:04:56 baa externalize domain constants and parameter
 * values Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.2 Jun 21 2002 18:26:18 baa externalize parameter names, start
 * formatting currency base on locale Resolution for POS SCR-1624: Localization
 * Support
 *
 * Rev 1.1 28 May 2002 12:21:56 vxs Removed unncessary concatenations from
 * logging statements. Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 * Rev 1.0 Apr 29 2002 14:45:20 msg Initial revision.
 *
 * Rev 1.2 15 Apr 2002 09:33:14 baa make call to setLabel() from the
 * updatePropertyFields() method Resolution for POS SCR-1599: Field name labels
 * on dialog screens use default text instead of text from bundles
 *
 * Rev 1.1 10 Apr 2002 13:59:28 baa make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * Rev 1.0 Mar 18 2002 11:51:54 msg Initial revision.
 *
 * Rev 1.11 15 Feb 2002 16:33:18 baa ui fixes Resolution for POS SCR-798:
 * Implement pluggable-look-and-feel user interface
 *
 * Rev 1.10 13 Feb 2002 17:34:30 baa fix ui boxes Resolution for POS SCR-1240:
 * VisaRefPhoneNumber parameter value field box too small to see data
 *
 * Rev 1.9 11 Feb 2002 23:53:24 baa fixing text area display Resolution for POS
 * SCR-1204: AutomaticEmailCanceledOrder parameter value field box too small to
 * see data
 *
 * Rev 1.7 08 Feb 2002 18:52:24 baa defect fix Resolution for POS SCR-798:
 * Implement pluggable-look-and-feel user interface
 *
 * Rev 1.6 06 Feb 2002 20:47:32 baa defect fix Resolution for POS SCR-798:
 * Implement pluggable-look-and-feel user interface
 *
 * Rev 1.5 30 Jan 2002 16:42:32 baa ui fixes Resolution for POS SCR-965: Add
 * Customer screen UI defects
 *
 * Rev 1.4 Jan 22 2002 06:34:24 mpm UI fixes. Resolution for POS SCR-798:
 * Implement pluggable-look-and-feel user interface
 *
 * Rev 1.3 Jan 20 2002 11:17:04 mpm Cleanup pass on PLAF UI integration.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeResponseIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.InternationalTextSupport;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ResourceManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.BillPayCargo;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.InventoryBeanModelIfc;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

import org.apache.log4j.Logger;

/**
 * Implements various utility methods for ui-related tasks.
 */
public class UIUtilities
{
    /** revision number supplied by PVCS * */
    public static final String revisionNumber = "$Revision: /main/39 $";

    /** logger for debugging. */
    private static final Logger logger = Logger.getLogger(UIUtilities.class);

    /** default UI Factory for testing */
    public static String TEST_FACTORY =
        "oracle.retail.stores.pos.ui.plaf.eys.EYSUIFactory";

    /** default plaf properties for testing */
    public static String TEST_PROPS = "swanplaf";

    /** default Look and Feel for testing */
    public static String TEST_PLAF =
        "oracle.retail.stores.pos.ui.plaf.eys.EYSLookAndFeel";

    /** default resource path if resource manager can't be retrieved */
    public static String defaultPath = "oracle/retail/stores/pos/resources/";

    /** default property path if resource manager can't be retrieved */
    public static String defaultPropPath = "oracle/retail/stores/pos/config/";

    public static final String ALIGN_CENTER = "CENTER";
    public static final String ALIGN_LEFT = "LEFT";
    public static final String ALIGN_RIGHT = "RIGHT";

    /** String representing JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED integer **/
    public static String VERTICAL_SCROLLBAR_AS_NEEDED = Integer.toString(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    /** String representing JScrollPane.VERTICAL_SCROLLBAR_NEVER integer **/
    public static String VERTICAL_SCROLLBAR_NEVER = Integer.toString(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

    /** String representing JScrollPane.VERTICAL_SCROLLBAR_ALWAYS integer **/
    public static String VERTICAL_SCROLLBAR_ALWAYS = Integer.toString(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    /** String representing JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED integer **/
    public static String HORIZONTAL_SCROLLBAR_AS_NEEDED = Integer.toString(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    /** String representing JScrollPane.HORIZONTAL_SCROLLBAR_NEVER integer **/
    public static String HORIZONTAL_SCROLLBAR_NEVER = Integer.toString(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    /** String representing JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS integer **/
    public static String HORIZONTAL_SCROLLBAR_ALWAYS = Integer.toString(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    /** Icon/Image constants for PLAF. */
    public static final String SUFFIX_ICON_SET = ".iconSet";
    public static final String ICON_SET_WHITE = "white";
    public static final String ICON_SET_GRAY = "gray";

    /**
     * Constant used for defaulting the inventory status.
     */
    public static final int SALES_FLOOR = 0;
    
    /** Notifications indicator available message for ORPOS/MPOS applications **/
    public static HashMap<String, Boolean> notificationsAvailableStatus = new HashMap<String, Boolean>();
    
    /**
     * Private constructor for static singleton.
     */
    private UIUtilities()
    {
    }

    /**
     * Returns a swing aligment constant {@link SwingConstants#CENTER},
     * {@link SwingConstants#LEFT}, {@link SwingConstants#RIGHT} that matches the
     * given string value. If the string is not recognized, the default value
     * is LEFT.
     *
     * @param alignString
     *            the string alignment value
     * @return the resulting alignment constant
     */
    public static int getAlignmentValue(String alignString)
    {
        int result = SwingConstants.LEFT;
        alignString = alignString.toUpperCase().intern();

        if (alignString == ALIGN_CENTER)
        {
            result = SwingConstants.CENTER;
        }
        else if (alignString == ALIGN_RIGHT)
        {
            result = SwingConstants.RIGHT;
        }
        return result;
    }

    /**
     * Returns a boolean value that matches the given string value. If the
     * string is not recognized, the default value is FALSE.
     *
     * @param boolString
     *            the string alignment value
     * @return the resulting boolean value
     */
    public static boolean getBooleanValue(String boolString)
    {
        return Boolean.valueOf(boolString);
    }

    /**
     * Same as calling {@link #getImageIcon(String, String, Component)} with
     * {@link #ICON_SET_WHITE} as the iconSet.
     * @since 14.1
     */
    public static ImageIcon getImageIcon(String iconKey, Component c)
    {
        return getImageIcon(ICON_SET_WHITE, iconKey, c);
    }

    /**
     * Return an image icon, useful for button icons, that can be used as the
     * specified components icon.
     * 
     * @param iconSet
     * @param iconKey
     * @param c
     * @return
     * @since 14.1
     */
    public static ImageIcon getImageIcon(String iconSet, String iconKey, Component c)
    {
        String iconName = UIManager.getString(iconKey);
        if (iconName != null && !iconName.isEmpty())
        {
            iconName = iconSet + "/" + iconName;
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting image for iconKey=" + iconKey + ", iconName=" + iconName);
            }
            Image iconImage = getImage(iconName, c, null, false);
            if (iconImage != null)
            {
                return new ImageIcon(iconImage, "actionName");
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Image icon not found for " + iconKey);
        }
        return null;
    }

    /**
     * Retrieves an image from the ResourceManager. Will create image using a
     * java MediaTracker if ResourceManager is unavailable.
     *
     * @param imageName
     *            name of image file to load
     * @param c
     *            the image's assoicated component
     */
    public static Image getImage(String imageName, Component c)
    {
        return getImage(imageName, c, null, true);
    }

    /**
     * Retrieves an image from the ResourceManager. Will create image using a
     * java MediaTracker if ResourceManager is unavailable. The image will
     * be adjusted with the specified HSB values if non-null.
     *
     * @param imageName name of image file to load
     * @param c the image's assoicated component
     * @param hsb the hue, saturation and brightness to adjust the image with.
     * @param localize boolean specifying whether to searc for localized image name should be localized.
     * @since 14.0
     */
    public static Image getImage(String imageName, Component c, float[] hsb, boolean localize)
    {
        Image image = null;
        ResourceManagerIfc rsrcMgr = getResourceManager();

        if (rsrcMgr != null)
        {
            if (localize)
            {
                Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
                image = rsrcMgr.getImage(imageName, bestMatchLocale, hsb);
            }
            else
            {
                image = rsrcMgr.getImage(imageName, null, hsb);
            }

        }
        // if there's no resource manager, use MediaTracker
        if (image == null)
        {
            // build relative image path
            StringBuilder path = new StringBuilder(System.getProperty("user.dir"));
            path.append(System.getProperty("file.separator"));
            path.append(defaultPath);
            path.append(imageName);
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating image with default path: " + path);
            }
            File imageFile = new File(path.toString());
            if (imageFile.exists())
            {
                // create a tracker and get image from toolkit
                MediaTracker tracker = new MediaTracker(c);
                try
                {
                    image = Toolkit.getDefaultToolkit().createImage(imageFile.toURI().toURL());

                    // add image to cache
                    tracker.addImage(image, 0);

                    // wait for image to load
                    tracker.waitForID(0);
                }
                catch (Exception e)
                {
                    logger.error("UIUtilites could not load image: " + imageName, e);
                }
            }
        }
        return image;
    }

    /**
     * Returns an array of integers parsed from a comma-delimited string.
     *
     * @param intString
     *            the delimited list of integers
     * @return an integer array
     */
    public static int[] getIntArrayFromString(String intString)
    {
        int[] intList = null;

        // parse the string list into an array
        String[] strings = UIUtilities.parseDelimitedList(intString, ",");

        // if the array has values, turn each one into an integer
        if (strings != null && strings.length > 0)
        {
            intList = new int[strings.length];

            for (int i = 0; i < strings.length; i++)
            {
                try
                {
                    // create the integer
                    intList[i] = Integer.parseInt(strings[i]);
                }
                catch (NumberFormatException nfe)
                {
                    // if we can't make a number, use 0
                    intList[i] = 0;
                }
            }
        }
        return intList;
    }

    /**
     * Creates a class instance from a class name using the foundation
     * reflection utility. If the creation throws an exception, a null object
     * is returned.
     *
     * @param className
     *            the full name of the class to create
     * @return the instantiated object, or null
     */
    public static Object getNamedClass(String className)
    {
        Object result = null;

        try
        {
            result = ReflectionUtility.createClass(className);
        }
        catch (Exception e)
        {
            logger.warn("UIUtilities could not instantiate " + className, e);
        }
        return result;
    }

    /**
     * Creates a dimension object based on the width of the parent object. This
     * computes the preferred size based on the total width and the component's
     * weight.
     *
     * @param parentWidth
     *            the width of the parent object
     * @param weight
     *            the component's weight as an integer
     * @param height
     *            the component's height
     * @return a Dimension object for the component
     */
    public static Dimension sizeFromWeight(
        int parentWidth,
        int weight,
        int height)
    {
        // convert the weight to a decimal
        double tempWeight = weight * .01;

        // compute the width
        int width = (int) Math.round(parentWidth * tempWeight);
        // create and return the dimension
        Dimension dim = new Dimension(width, height);

        return dim;
    }

    /**
     * Sizes a component based on the size of the provided image.
     * 
     * @param component the component to size
     * @param image the image to derive measurements from
     */
    public static void sizeToImage(JComponent component, Image image)
    {
        if (image != null)
        {
            int height = image.getHeight(null);
            int width = image.getWidth(null);

            Dimension d = new Dimension(width, height);
            component.setPreferredSize(d);
        }
    }

    /**
     * Sizes a component based on the size of the provided icon.
     * 
     * @param component the component to size
     * @param icon the icon to derive measurements from
     */
    public static void sizeToIcon(JComponent component, Icon icon)
    {
        if (icon != null)
        {
            int height = icon.getIconHeight();
            int width = icon.getIconWidth();

            Dimension d = new Dimension(width, height);
            component.setPreferredSize(d);
        }
    }

    /**
     * This method parses a comma delimited string into an array of strings.
     * 
     * @param listString a delimited list
     * @param delimiter the delimiting character
     * @return an array of string values
     */
    public static String[] parseDelimitedList(
        String listString,
        String delimiter)
    {
        String[] listArray = null;

        if (listString != null && !listString.equals(""))
        {
            StringTokenizer st = new StringTokenizer(listString, delimiter);
            List<String> arrayList = new ArrayList<String>(st.countTokens());

            while (st.hasMoreTokens())
            {
                arrayList.add(st.nextToken());
            }

            listArray = new String[arrayList.size()];
            arrayList.toArray(listArray);
        }
        return (listArray);
    }

    /**
     * Strips off the front segment of a property tag.
     * 
     * @param tag the property name
     * @return the ending segment of the property after the last period
     */
    public static String parseProperty(String tag)
    {
        int pos = tag.lastIndexOf(".");

        if (pos == -1)
        {
            return tag;
        }

        return tag.substring(pos + 1, tag.length());
    }

    /**
     * Loads a set of proerties from a file.
     * 
     * @param propFileName the name of the property file
     * @return the property object parsed from the file
     */
    public static Properties loadProperties(String propFilename)
    {
        return (loadProperties(defaultPropPath, propFilename));
    }

    /**
     * Load the specified properties from the resource manager.
     * 
     * @param path
     * @param propFilename
     * @return
     */
    public static Properties loadProperties(String path, String propFilename)
    {
        Properties properties = null;
        ResourceManagerIfc rsrcMgr = getResourceManager();

        if (rsrcMgr != null)
        {
            properties = rsrcMgr.getProperties(propFilename);
        }
        else
        {
            logger.error("Resource manager is null. Attempting to load from file.");

            properties = new Properties();

            try(FileInputStream fis = new FileInputStream(path + propFilename);)
            {
                properties.load(fis);
                logger.info("Properties loaded.");
            }
            catch (FileNotFoundException fnfe)
            {
                logger.error("Property file not found.", fnfe);
                properties = loadTestProperties();
            }
            catch (IOException ioe)
            {
                logger.error("IOException reading property file.", ioe);
            }
        }
        return properties;
    }

    /**
     * Attempts to get an instance of the {@link ResourceManagerIfc} from the
     * {@link Dispatcher}.
     *
     * @return the resource manager, or null
     */
    public static ResourceManagerIfc getResourceManager()
    {
        Dispatcher dispatcher = null;
        ResourceManagerIfc manager = null;

        try
        {
            // get the dispatcher
            dispatcher = Dispatcher.getDispatcher();
        }
        catch (IllegalStateException ise)
        {
            logger.warn("No dispatcher.");
        }
        // if we have a dispatcher instance,
        // get the resource manager
        if (dispatcher != null)
        {
            manager =
                (ResourceManagerIfc) dispatcher.getManager(
                    ResourceManagerIfc.TYPE);

        }
        return manager;
    }

    /**
     * If the given parameter is non-<code>null</code> then the resulting
     * <code>boolean</code> will be retrieved using the
     * {@link ParameterManagerIfc}.
     * <p>
     * The parameter should be <code>true</code> if the spec's functionality is
     * desired and <code>false</code> otherwise.
     *
     * @param parameterSpec
     * @return <code>false</code> by default. Otherwise returns value of parameter.
     * @see oracle.retail.stores.foundation.manager.gui.ParameterSpecIfc
     */
    public static boolean isParameterDisabled(String parameterName)
    {
        if (parameterName != null && parameterName.length() > 0)
        {
            ParameterManagerIfc paramMgr = (ParameterManagerIfc)Dispatcher.getDispatcher().getManager(ParameterManagerIfc.TYPE);
            if (paramMgr != null)
            {
                try
                {
                    return paramMgr.getBooleanValue(parameterName);
                }
                catch (ParameterException e)
                {
                    logger.error("Could not determine value of " + parameterName, e);
                }
            }
            else
            {
                logger.error("Parameter manager is null. Could not determine value of " + parameterName);
            }
        }
        else
        {
            logger.warn("Parameter name specified is null.");
        }
        return false;
    }

    /**
     * This method lays out a pair of lists side by side. The lists should have
     * labels identifying the contents. There can be an optional pairing of
     * header lables and fields above the list that will appear in the normal
     * "DataPanel" layout.
     *
     * @param bean
     * @param headerLabels
     * @param headerFields
     * @param listLabels
     * @param lists
     * @since 13.3
     */
    public static void layoutDualListPanel(JPanel bean,
                                       JLabel[] headerLabels, JComponent[] headerFields,
                                       JLabel[] listLabels, JComponent[] lists)
    {
        assert (bean != null);
        assert (listLabels != null);
        assert (lists != null);
        if (listLabels.length != 2 || lists.length != 2)
        {
            throw new IllegalArgumentException("Length of labels and components are not equal to two.");
        }
        if (headerLabels != null && headerFields != null)
        {
            if (headerLabels.length != headerFields.length)
            {
                throw new IllegalArgumentException("Length of labels and components args not equal.");
            }
        }

        // layout panel as border layout
        bean.setLayout(new BorderLayout());
        JPanel header = new JPanel();
        bean.add(header, BorderLayout.NORTH);

        layoutDataPanel(header, headerLabels, headerFields, false);

        JPanel center = new JPanel();
        bean.add(center, BorderLayout.CENTER);
        center.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        UIFactory factory = UIFactory.getInstance();
        lists[0].setOpaque(false);
        lists[1].setOpaque(false);

        // add debug and construction visual help
        if (Boolean.getBoolean("pos.ui.debug"))
        {
            bean.setBackground(Color.blue);
            bean.setOpaque(true);
            center.setBackground(Color.cyan);
            center.setOpaque(true);
            lists[0].setBackground(Color.pink);
            lists[1].setBackground(Color.pink);
        }

        //add the list labels
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = factory.getInsets("defaultLabelLeft");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        center.add(listLabels[0], constraints);
        constraints.gridx = 1;
        center.add(listLabels[1], constraints);

        //add the lists
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = factory.getInsets("defaultLabelBottom");
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        center.add(lists[0], constraints);
        constraints.gridx = 1;
        center.add(lists[1], constraints);
   }

    /**
     * This method lays out dual columns of labels and input fields if the
     * number of rows displayed on the screen would exceed the vertical area
     * available.
     * 
     * @param bean the panel to add components to
     * @param labels an array of text labels
     * @param components an array of components
     * @param JLabel total label
     * @param JComponent total field
     * @param JLabel register label
     * @param JComponent register field
     */
    public static void layoutDualPanel(JPanel bean,
                                       JLabel[] labels,JComponent[] components,
                                       JLabel tLabel, JComponent tComp,
                                       JLabel rLabel, JComponent rComp)
    {
        if (labels.length != components.length)
        {
            throw new IllegalArgumentException("Length of labels and components args not equal.");
        }

        int entries = labels.length;

        int rows = entries/2;
        boolean thatsOdd = entries % 2 == 1;
        if (thatsOdd)
        {
            rows++;
        }

        JLabel[] labels1 = new JLabel[rows];
        JComponent[] components1  = new JComponent[rows];
        JLabel[] labels2  = new JLabel[rows];
        JComponent[] components2  = new JComponent[rows];

        System.arraycopy(labels,0, labels1, 0, rows);
        System.arraycopy(components,0, components1, 0, rows);

        System.arraycopy(labels,rows, labels2, 0, entries - rows);
        System.arraycopy(components,rows, components2, 0, entries - rows);

        bean.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 0, 5, 0);
        constraints.gridheight = 1;

        //add the first column of labels + components
        for (int i = 0; i < labels1.length; i++)
        {
            if (labels1[i] != null)
            {
                constraints.gridx = 0;
                constraints.gridy = i;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                bean.add(labels1[i], constraints);
            }
            if (components1[i] != null)
            {
                constraints.gridx = 1;
                constraints.gridy = i;
                bean.add(components1[i], constraints);
            }
        }

        int j;
        //add the second column of labels + components
        for (j = 0; j < labels2.length; j++)
        {
            if (labels2[j] != null)
            {
                constraints.gridx = 2;
                constraints.gridy = j;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                bean.add(labels2[j], constraints);
            }
            if (components2[j] != null)
            {
                constraints.gridx = 3;
                constraints.gridy = j;
                constraints.fill = GridBagConstraints.NONE;
                bean.add(components2[j], constraints);
            }
        }

        constraints.insets = new Insets(10, 5, 10, 5);
        if (tLabel != null && rLabel == null)
        {
            constraints.gridx = 2;
            constraints.gridy = j;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            bean.add(tLabel, constraints);
            constraints.gridx = 3;
            constraints.fill = GridBagConstraints.NONE;
            bean.add(tComp, constraints);
        }
        else
        {
            constraints.gridx = 0;
            constraints.gridy = j++;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            bean.add(tLabel, constraints);
            constraints.gridx = 1;
            constraints.fill = GridBagConstraints.NONE;
            bean.add(tComp, constraints);

            constraints.gridx = 2;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            bean.add(rLabel, constraints);
            constraints.gridx = 3;
            constraints.fill = GridBagConstraints.NONE;
            bean.add(rComp, constraints);
        }
    }

    /**
     * Applies the standard constraints to a set of labels and components. The
     * standard layout is the GridBagLayout, and the constraints are derived
     * from the ui properties. The assumption is that the labels and components
     * line up as complementary pairs in two columns.
     * 
     * @param bean the panel to add components to
     * @param labels an array of text labels
     * @param components an array of components
     * @param addWeight flag indicating last components get full weight
     */
    public static void layoutDataPanel(
        JPanel bean,
        JLabel[] labels,
        JComponent[] components)
    {
        if (components[components.length - 1] instanceof JLabel ||
                components[components.length - 1] instanceof JTextField || 
                components[components.length - 1] instanceof JComboBox)
        {
            layoutDataPanel(bean, labels, components, false);
        }
        else
        {
            layoutDataPanel(bean, labels, components, true);
        }
    }

    /**
     * Applies the standard constraints to a set of labels and components. The
     * standard layout is the GridBagLayout, and the constraints are derived
     * from the ui properties. The assumption is that the labels and components
     * line up as complementary pairs in two columns.
     * 
     * @param bean the panel to add components to
     * @param labels an array of text labels
     * @param components an array of components
     * @param addWeight flag indicating last components get full weight
     */
    public static void layoutDataPanel(
        JPanel bean,
        JLabel[] labels,
        JComponent[] components,
        boolean addWeight)
    {
        if (Boolean.getBoolean("pos.ui.debug"))
        {
            bean.setBackground(Color.red);
            bean.setOpaque(true);
        }

        UIFactory factory = UIFactory.getInstance();
        // get size of the widgets being laid out. Assumes labels=comps length
        int size = labels.length;
        bean.setLayout(new GridBagLayout());

        // layout top label and field
        GridBagConstraints constraints = factory.getConstraints("DataEntryBean");
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridy = 0;
        if (size == 1 || addWeight)
        {
            constraints.insets = factory.getInsets("singleLabelTop");
        }
        else
        {
            constraints.insets = factory.getInsets("defaultLabelTop");
        }
        constraints.fill = GridBagConstraints.HORIZONTAL;
        if (labels[0] != null)
        {
            bean.add(labels[0], constraints);
        }

        // reuse constraint object (since it is cloned by layout) and add comp
        if (size == 1 || addWeight)
        {
            constraints.insets = factory.getInsets("singleFieldTop");
            if (size == 1 && addWeight)
            {
                // this will be the only widget taking up the work area, so
                // set the top insets for the bottom as well.
                constraints.insets.bottom = constraints.insets.top;
                constraints.insets.right = constraints.insets.left;
            }
        }
        else
        {
            constraints.insets = factory.getInsets("defaultFieldTop");
        }
        constraints.gridx = 1;
        // adjust for single pair layouts
        if (size == 1)
        {
            constraints.weighty = 1.0;
            if (addWeight)
            {
                constraints.fill = GridBagConstraints.BOTH;
            }
        }
        else
        {
            constraints.fill = GridBagConstraints.NONE;
        }
        bean.add(components[0], constraints);

        // layout the remaining middle labels and fields
        for (int i = 1; i < (size - 1); i++)
        {
            constraints.gridy = i;
            constraints.gridx = 0;
            constraints.insets = factory.getInsets("defaultLabelLeft");
            constraints.fill = GridBagConstraints.HORIZONTAL;

            if (labels[i] != null)
            {
                // check if the label is a divider
                if (labels[i] instanceof UIFactory.EYSDivider)
                {
                    constraints.gridwidth = GridBagConstraints.REMAINDER;
                }
                else
                {
                    constraints.gridwidth = 1;
                }
                bean.add(labels[i], constraints);
            }
            if (components[i] != null)
            {
                constraints.gridx = 1;
                constraints.insets = factory.getInsets("defaultFieldRight");
                constraints.fill = GridBagConstraints.NONE;
                bean.add(components[i], constraints);
            }

        }
        constraints.gridwidth = 1;

        // layout the bottom label and field
        if (size > 1)
        {
            constraints.gridy = labels.length - 1;
            constraints.gridx = 0;
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
            constraints.insets = factory.getInsets("defaultLabelBottom");
            constraints.fill = GridBagConstraints.HORIZONTAL;
            bean.add(labels[size - 1], constraints);

            constraints.gridx = 1;
            if (addWeight)
            {
                constraints.weighty = 1.0;
                //constraints.weightx = 1.0;
            }
            constraints.fill = GridBagConstraints.NONE;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.insets = factory.getInsets("defaultFieldBottom");
            bean.add(components[size - 1], constraints);
        }
    }

    /**
     * This method is overloaded with Jcomponent[] argument replaced by an
     * arraylist of component This was required for the Browser Launch since
     * WebBrowser returns a Component rather than a JComponent
     * 
     * @param bean the panel to add components to
     * @param labels an array of text labels
     * @param components an arraylist of components
     */
    public static void layoutDataPanel(JPanel bean, JLabel[] labels, List<Component> components)
    {
        int size = labels.length;
        UIFactory factory = UIFactory.getInstance();
        bean.setLayout(new GridBagLayout());
        GridBagConstraints constraints = factory.getConstraints("BrowserFoundationDisplayBean");
        constraints.weightx = 0;
        constraints.weighty = 0;
        // layout top label and field
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        if (labels[0] != null)
        {
            bean.add(labels[0], constraints);
        }
        constraints.gridx = 1;
        // adjust for single pair layouts
        if (size == 1)
        {
            constraints.weighty = 1.0;
            constraints.weightx = 1.0;
        }
        bean.add(components.get(0), constraints);
        constraints.gridwidth = 1;
    }

    /**
     * Applies the standard constraints to a set of labels and components. The
     * standard layout is the GridBagLayout, and the constraints are derived
     * from the ui properties. The assumption is that the label and component
     * line up as complementary pairs in two columns.
     * 
     * @param bean the panel to add components to
     * @param label a text labels
     * @param component a components
     * @param xValue the x coodinate
     * @param yValue the y coordinate
     */
    public static void layoutComponent(
        JPanel bean,
        JLabel label,
        JComponent component,
        int xValue,
        int yValue,
        boolean bottom)
    {
        layoutComponent(bean, label, component, xValue, yValue, bottom, false);
    }    
    
    /**
     * Applies the standard constraints to a set of label and component. The
     * standard layout is the GridBagLayout, and the constraints are derived
     * from the ui properties. The assumption is that the label and component
     * line up as complementary pairs in two columns.
     *
     * @param bean
     *            the panel to add components to
     * @param label
     *            a text labels
     * @param component
     *            a components
     * @param xValue
     *            the x coodinate
     * @param yValue
     *            the y coordinate
     */
    public static void layoutComponent(
        JPanel bean,
        JLabel label,
        JComponent component,
        int xValue,
        int yValue,
        boolean bottom,
        boolean center)
    {
        UIFactory factory = UIFactory.getInstance();
        GridBagConstraints gbc = factory.getConstraints("DataEntryBean");

        if (center)
        {
            gbc.anchor = GridBagConstraints.CENTER;
        }
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        String labelInsets = new String("defaultLabelLeft");
        String fieldInsets = new String("defaultFieldRight");

        // layout top label and field
        gbc.gridx = xValue;
        gbc.gridy = yValue;

        if (yValue == 0) // use top insets
        {
            labelInsets = "defaultLabelTop";
            fieldInsets = "defaultFieldTop";
        }
        else if (bottom) // use bottom insets
        {
            labelInsets = "defaultLabelBottom";
            fieldInsets = "defaultFieldBottom";
        }

        gbc.insets = factory.getInsets(labelInsets);
        // check if the label is a divider
        if (label instanceof UIFactory.EYSDivider)
        {
            gbc.gridwidth = GridBagConstraints.REMAINDER;
        }
        bean.add(label, gbc);

        if (component != null)
        {
            gbc.fill = GridBagConstraints.NONE;
            if (center)
            {
                gbc.anchor = GridBagConstraints.CENTER;
            }
            if (bottom)
            {
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.weighty = 1.0;
                //gbc.weightx=1.0;
            }
            gbc.insets = factory.getInsets(fieldInsets);
            gbc.gridx = xValue + 1;
            bean.add(component, gbc);
        }

    }

    /**
     * Retrieves text through international text support facility for specified
     * property.
     *
     * @param propName
     *            property key
     * @return text from support facility
     */
    public static String retrieveCommonText(String propName)
    {
        return retrieveText(
            "Common",
            BundleConstantsIfc.COMMON_BUNDLE_NAME,
            propName,
            propName);
    }

    /**
     * Retrieves text through international text support facility for specified
     * spec name, bundle name and property. Implements default if property not
     * found.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public static String retrieveCommonText(
        String propName,
        String defaultValue)
    {
        return retrieveText(
            "Common",
            BundleConstantsIfc.COMMON_BUNDLE_NAME,
            propName,
            defaultValue);
    }

    /**
     * Retrieves text through international text support facility for specified
     * spec name, bundle name and property. Implements default if property not
     * found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public static String retrieveText(
        String specName,
        String bundleName,
        String propName,
        String defaultValue)
    {
        Properties props = null;
        if (Util.isObjectEqual(bundleName, BundleConstantsIfc.COMMON_BUNDLE_NAME))
        {
            props =
                InternationalTextSupport.getInternationalBeanText(
                    specName,
                    BundleConstantsIfc.COMMON_BUNDLE_NAME);
        }
        else
        {
            // use multiple bundles to include common
            String bundles[] =
                { BundleConstantsIfc.COMMON_BUNDLE_NAME, bundleName };
            props =
                InternationalTextSupport.getInternationalBeanText(
                    specName,
                    bundles);
        }

        String returnValue = null;

        // Adding brakets to the property names help us
        // determine if text is comming from bundles or the
        // default values. To activate the BUNDLE_TESTING flag
        // the application has to be run with
        // -DBUNDLE_TESTING
        String testPropName = "<" + propName + ">";
        boolean testingBundles = (System.getProperty("BUNDLE_TESTING") != null);
        if (props == null)
        {
            if (testingBundles)
            {
                returnValue = testPropName;
            }
            else
            {
                returnValue = defaultValue;
            }

        }
        else
        {
            if (testingBundles)
            {
                returnValue = props.getProperty(propName, testPropName);
            }
            else
            {
                returnValue = props.getProperty(propName, defaultValue);
            }

        }

        return (returnValue);
    }

    /**
     * Retrieves text through international text support facility for specified
     * spec name, bundle name and property. Implements default if property not
     * found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @return text from support facility
     */
    public static String retrieveText(
        String specName,
        String bundleName,
        String propName)
    {

        return retrieveText(specName, bundleName, propName, propName);
    }

    /**
     * Creates a JFrame for testing a bean. The frame contains dummy panels as
     * placeholders and uses a border layout.
     *
     * @return a frame object
     */
    public static void doBeanTest(Container bean)
    {
        // create a test frame and assign a close window listener
        JFrame testFrame = new JFrame("BeanTestFrame");
        testFrame.setSize(800, 600);
        testFrame.getContentPane().setLayout(new BorderLayout());

        testFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        // create dummy panels for the border layout
        JPanel top = new JPanel();
        top.setPreferredSize(new Dimension(800, 60));
        top.setBorder(BorderFactory.createEtchedBorder());
        testFrame.getContentPane().add(top, BorderLayout.NORTH);

        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(150, 640));
        side.setBorder(BorderFactory.createEtchedBorder());
        testFrame.getContentPane().add(side, BorderLayout.EAST);

        JPanel bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(800, 60));
        bottom.setBorder(BorderFactory.createEtchedBorder());
        testFrame.getContentPane().add(bottom, BorderLayout.SOUTH);

        // add the bean to the center
        bean.setSize(675, 550);
        testFrame.getContentPane().add(bean, BorderLayout.CENTER);

        testFrame.setVisible(true);
    }

    /**
     * Sets up the UI Factory, ui properties, and Look and Feel for testing
     * beans.
     */
    public static void setUpTest()
    {
        // set up the test ui
        System.setProperty("pos.ui.debug", Boolean.TRUE.toString());
        UIUtilities.defaultPropPath = "locales/en/config/ui/";

        try
        {
            POSJFCUISubsystem.setUpCustomUI(
                TEST_FACTORY,
                TEST_PROPS,
                TEST_PLAF);
        }
        catch (ConfigurationException ce)
        {
            logger.error("Unable to setup UI configuration.", ce);
        }
        finally
        {
            UIUtilities.defaultPropPath = "oracle/retail/stores/pos/config/";
        }
    }

    private static Properties loadTestProperties(String filename)
    {
        Properties p = new Properties();
        try(FileInputStream fstrm = new FileInputStream(filename))
        {
            p.load(fstrm);
        }
        catch (IOException e)
        {
            p.setProperty("Color.black", "#000000");
            p.setProperty("Color.white", "#FFFFFF");
            p.setProperty("Color.gray", "#808080");
            p.setProperty("Color.beanBackground", "#CCCCCC");
            p.setProperty("Color.buttonBackground", "#999933");
            p.setProperty("Color.buttonForeground", "#FFFFFF");
            p.setProperty("Color.dialogLabelBackground", "#00FFFF");
            p.setProperty("Color.dialogLabelForeground", "#000000");
            p.setProperty("Color.errorLabelBackground", "#FF0000");
            p.setProperty("Color.fieldBackground", "#FFFFFF");
            p.setProperty("Color.fieldForeground", "#000000");
            p.setProperty("Color.headerBackground", "#FFFFFF");
            p.setProperty("Color.headerLabelBackground", "#FFFFB4");
            p.setProperty("Color.headerLabelForeground", "#000000");
            p.setProperty("Color.labelForeground", "#000000");
            p.setProperty("Color.listBackground", "#FFFFFF");
            p.setProperty("Color.listForeground", "#000000");
            p.setProperty("Color.listSelection", "#808080");
            p.setProperty("Color.noLogoBackground", "#CCCCFF");
            p.setProperty("Color.requiredForeground", "#AF0000");
            p.setProperty("Color.responseBackground", "#C6C6FF");
            p.setProperty("Color.responseForeground", "#000080");
            p.setProperty("Color.statusOnlineHighlight", "#009B00");
            p.setProperty("Color.statusOfflineHighlight", "#FF0000");
            p.setProperty("Color.textHighlight", "#C2D2FF");
            p.setProperty("Color.textHighlightText", "#000000");

            p.setProperty("Dimension.selectionListDimension", "180,120");
            p.setProperty("Dimension.horizontalButtonDimension", "70,70");
            p.setProperty("Dimension.verticalButtonDimension", "150,25");

            p.setProperty("Font.dialogFont", "Helvetica,bold,14");
            p.setProperty("Font.displayTextFont", "Courier New,plain,12");
            p.setProperty("Font.requiredFont", "Helvetica,bold,20");
            p.setProperty("Font.responseFont", "Helvetica,bold,16");
            p.setProperty("Font.fieldFont", "Helvetica,plain,12");
            p.setProperty("Font.labelFont", "Helvetica,bold,12");
            p.setProperty("Font.buttonFont", "Helvetica,bold,12");
            p.setProperty("Insets.emptyInsets", "0,0,0,0");
            p.setProperty("Insets.dialogButtonInsets", "0,40,0,40");
            p.setProperty("Insets.dialogMessageMargin", "5,5,5,5");
            p.setProperty("Insets.defaultLabelTop", "15,15,5,15");
            p.setProperty("Insets.defaultLabelLeft", "0,15,5,15");
            p.setProperty("Insets.defaultLabelBottom", "0,15,15,15");
            p.setProperty("Insets.defaultFieldTop", "15,0,5,15");
            p.setProperty("Insets.defaultFieldRight", "0,0,5,15");
            p.setProperty("Insets.defaultFieldBottom", "0,0,15,15");
            p.setProperty("Weights.customerRendererWeights", "30,40,20");
            p.setProperty("Weights.employeeRendererWeights", "50,25,25");
            p.setProperty("Weights.financialTotalsRendererWeights", "60,20,20");
            p.setProperty(
                "Weights.layawayItemRendererWeights",
                "30,28,14,14,14");
            p.setProperty("Weights.parameterRendererWeights", "40,50,10");
            p.setProperty(
                "Weights.orderItemRendererWeights",
                "30,15,15,15,15,10");
            p.setProperty("Weights.reasonCodeRendererWeights", "65,35");
            p.setProperty(
                "Weights.saleItemRendererWeights",
                "40,13,13,13,13,8");
            p.setProperty("Weights.suspendItemRendererWeights", "25,25,25,25");
            p.setProperty(
                "Weights.transactionItemRendererWeights",
                "15,30,40,15");
            p.setProperty("Weights.tenderItemRendererWeights", "60,20,20");

            p.setProperty("ApplicationFrame.background", "beanBackground");
            p.setProperty("ApplicationFrame.opaque", "true");

            p.setProperty("List.border", "listBorder");
            p.setProperty("List.background", "listBackground");

            p.setProperty("List.header.background", "headerBackground");
            p.setProperty("List.header.opaque", "true");
            p.setProperty("List.header.label.border", "etchedBorder");
            p.setProperty(
                "List.header.label.background",
                "headerLabelBackground");
            p.setProperty(
                "List.header.label.foreground",
                "headerLabelForeground");
            p.setProperty("List.header.label.font", "labelFont");
            p.setProperty("List.header.label.opaque", "true");
            p.setProperty("List.header.label.alignment", "center");
            p.setProperty("List.renderer.focusBorder", "focusBorder");
            p.setProperty("List.renderer.noFocusBorder", "emptyBorder.1");
            p.setProperty("List.renderer.label.font", "fieldFont");
            p.setProperty("List.renderer.label.opaque", "false");

            p.setProperty("TextField.background", "fieldBackground");
            p.setProperty("TextField.foreground", "fieldForeground");
            p.setProperty("TextField.inactiveForeground", "fieldForeground");
            p.setProperty("TextField.font", "fieldFont");
            p.setProperty("TextField.opaque", "true");

            p.setProperty("Table.background", "white");
            p.setProperty("Table.opaque", "true");
            p.setProperty("Table.field.border", "fieldBorder");
            p.setProperty("Table.field.background", "fieldBackground");
            p.setProperty("Table.field.foreground", "fieldForeground");
            p.setProperty("Table.field.font", "fieldFont");
            p.setProperty("Table.field.alignment", "center");
            p.setProperty("Table.field.opaque", "true");
            p.setProperty("Table.label.border", "fieldBorder");
            p.setProperty("Table.label.background", "headerLabelBackground");
            p.setProperty("Table.label.foreground", "headerLabelForeground");
            p.setProperty("Table.label.font", "labelFont");
            p.setProperty("Table.label.opaque", "true");
            p.setProperty("Table.label.alignment", "center");

            p.setProperty("ScrollPane.background", "listBackground");
            p.setProperty("ScrollPane.border", "emptyBorder.0");
            p.setProperty("ScrollPane.opaque", "true");

            p.setProperty("Viewport.background", "listBackground");

            p.setProperty("DialogButton.background", "beanBackground");
            p.setProperty("DialogButton.foreground", "labelForeground");
            p.setProperty("DialogButton.border", "etchedBorder");
            p.setProperty("DialogButton.opaque", "true");

            p.setProperty("HorizontalButton.foreground", "buttonForeground");
            p.setProperty("HorizontalButton.background", "buttonBackground");
            p.setProperty("HorizontalButton.font", "buttonFont");
            p.setProperty("HorizontalButton.size", "horizontalButtonDimension");
            p.setProperty("HorizontalButton.opaque", "true");

            p.setProperty("VerticalButton.border", "roundedBorder");
            p.setProperty("VerticalButton.foreground", "buttonForeground");
            p.setProperty("VerticalButton.background", "buttonBackground");
            p.setProperty("VerticalButton.font", "buttonFont");
            p.setProperty("VerticalButton.size", "verticalButtonDimension");
            p.setProperty("VerticalButton.opaque", "true");

            p.setProperty("StatusScrollPane.border", "listBorder");
            p.setProperty("StatusScrollPane.opaque", "true");

            p.setProperty("ValidatingComboBox.border", "validatingBorder");
            p.setProperty("ValidatingComboBox.background", "fieldBackground");
            p.setProperty("ValidatingComboBox.foreground", "fieldForeground");
            p.setProperty("ValidatingComboBox.font", "fieldFont");
            p.setProperty("ValidatingComboBox.opaque", "true");

            p.setProperty("ValidatingField.border", "validatingBorder");
            p.setProperty("ValidatingField.background", "fieldBackground");
            p.setProperty("ValidatingField.foreground", "fieldForeground");
            p.setProperty("ValidatingField.font", "fieldFont");
            p.setProperty("ValidatingField.opaque", "true");

            p.setProperty("ButtonBar.border", "emptyBorder.1");
            p.setProperty("ButtonBar.foreground", "buttonForeground");
            p.setProperty("ButtonBar.background", "buttonBackground");
            p.setProperty("ButtonBar.insets", "emptyInsets");
            p.setProperty("ButtonBar.opaque", "false");

            p.setProperty("BrowserStatusPanel.background", "beanBackground");
            p.setProperty(
                "BrowserStatusPanel.label.foreground",
                "browserForeground");
            p.setProperty("BrowserStatusPanel.label.font", "browserFont");

            p.setProperty("BaseBean.background", "beanBackground");
            p.setProperty("BaseBean.border", "emptyBorder.0");
            p.setProperty("BaseBean.opaque", "false");
            p.setProperty("BaseBean.label.foreground", "labelForeground");
            p.setProperty("BaseBean.label.font", "labelFont");
            p.setProperty("BaseBean.label.opaque", "false");
            p.setProperty("BaseBean.label.alignment", "left");

            p.setProperty("DialogBean.background", "beanBackground");
            p.setProperty("DialogBean.opaque", "true");
            p.setProperty(
                "DialogBean.buttonPanel.background",
                "beanBackground");
            p.setProperty("DialogBean.buttonPanel.opaque", "true");
            p.setProperty("DialogBean.buttonPanel.border", "arcBottomBorder");
            p.setProperty(
                "DialogBean.header.background",
                "dialogLabelBackground");
            p.setProperty("DialogBean.header.border", "loweredBevelBorder");
            p.setProperty("DialogBean.header.opaque", "true");
            p.setProperty("DialogBean.header.label.font", "dialogFont");
            p.setProperty("DialogBean.header.label.alignment", "center");
            p.setProperty(
                "DialogBean.header.label.foreground",
                "dialogLabelForeground");
            p.setProperty("DialogBean.header.label.border", "emptyBorder.0");
            p.setProperty("DialogBean.header.label.opaque", "false");
            p.setProperty(
                "DialogBean.message.foreground",
                "dialogLabelForeground");
            p.setProperty("DialogBean.message.font", "dialogFont");
            p.setProperty("DialogBean.message.opaque", "false");
            p.setProperty("DialogBean.message.margin", "dialogMessageMargin");
            p.setProperty(
                "DialogBean.messageWrap.background",
                "beanBackground");
            p.setProperty("DialogBean.messageWrap.border", "etchedBorder");
            p.setProperty("DialogBean.messageWrap.opaque", "true");
        }

        return p;
    }

    /**
     *
     * @return
     */
    private static Properties loadTestProperties()
    {
        return loadTestProperties("tigerplaf.properties");
    }

    /**
     * Sets the dialog model and display the error dialog.
     *
     * @param ui
     *            POSUIManagerIfc uimanager instance
     * @param typeID
     *            int the dialog type
     * @param resourceID
     *            String the screen id
     *
     */
    public static void setDialogModel(
        POSUIManagerIfc ui,
        int typeID,
        String resourceID)
    {
        setDialogModel(ui, typeID, resourceID, null, null, null);
    }

    /**
     * Sets the dialog model and display the error dialog.
     *
     * @param ui
     *            POSUIManagerIfc uimanager instance
     * @param typeID
     *            int the dialog type
     * @param resourceID
     *            String the screen id
     * @param args
     *            String array for the text to display on the dialog
     */
    public static void setDialogModel(
        POSUIManagerIfc ui,
        int typeID,
        String resourceID,
        String[] args)
    {
        setDialogModel(ui, typeID, resourceID, args, null, null);
    }

    /**
     * Sets the model for single button dialogs and display the error dialog.
     *
     * @param ui
     *            POSUIManagerIfc uimanager instance
     * @param typeID
     *            int the dialog type
     * @param resourceID
     *            String the screen id
     * @param args
     *            String array for the text to display on the dialog
     * @param letter
     *            String with the new letters for the OK button
     */
    public static void setDialogModel(
        POSUIManagerIfc ui,
        int typeID,
        String resourceID,
        String[] args,
        String letter)
    {
        String letters[] = new String[1];
        int button[] = new int[1];

        letters[0] = new String(letter);
        button[0] = DialogScreensIfc.BUTTON_OK;

        setDialogModel(ui, typeID, resourceID, args, button, letters);
    }

    /**
     * Sets the dialog model and display the error dialog.
     *
     * @param ui
     *            POSUIManagerIfc uimanager instance
     * @param typeID
     *            int the dialog type
     * @param resourceID
     *            String the screen id
     * @param args
     *            String array for the text to display on the dialog
     * @param buttonNames
     *            int array with the buttons of the dialog
     * @param letterNames
     *            String array with the new letters for each dialog button
     */
    public static void setDialogModel(
        POSUIManagerIfc ui,
        int typeID,
        String resourceID,
        String[] args,
        int[] buttonNames,
        String[] letterNames)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(resourceID);
        model.setType(typeID);

        if (args != null)
        {
            model.setArgs(args);
        }

        // check if the setting for changing the return letters are valid
        if ((buttonNames != null)
            && (letterNames != null)
            && (buttonNames.length == letterNames.length))
        {
            for (int i = 0; i < letterNames.length; i++)
            {
                model.setButtonLetter(buttonNames[i], letterNames[i]);
            }
        }

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Retrieves the PVCS revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Retrieves a Vector of internationalized display text corresponding to
     * the supplied reason codes.
     *
     * @param Vector
     *            collection of reason code String keys that are used to
     *            retrieve the internationalized text
     * @return Vector of Strings that contains internationalized text retrieved
     *         from the message bundles.
     * @see oracle#retail#stores#domain#utility#CodeListIfc#getTextEntries
     *
     * @deprecated ReasonCodes are coming from the database and the locale entries
     *        are in the database. No more text entries from the bundles.
     */
    public static Vector<String> getReasonCodeTextEntries(Vector<String> reasonCodeKeys)
    {
        // Use reasonCodeKeys as keys in the common text bundle in order to
        // pull out the proper text
        Vector<String> returnText = null;
        if (reasonCodeKeys != null)
        {
            int numTextEntries = reasonCodeKeys.size();
            returnText = new Vector<String>(numTextEntries);
            for (int x = 0; x < numTextEntries; x++)
            {
                returnText.add(retrieveCommonText(reasonCodeKeys.get(x)));
            }
        }
        // Return the result
        return returnText;
    }

    /**
     * Retrieves appropriate Display Name.
     *
     * @return String with appropriate Display Name.
     */
    public static String getPreferredLanguageDisplayName()
    {
        //
        String displayName =
            LocaleMap
                .getLocale(LocaleConstantsIfc.USER_INTERFACE)
                .getDisplayName();
        // Return the result
        return displayName;
    }

    /**
     * Loads the inventory locations for the store in the model.
     * If provided, will pre-select an inventory location.
     *
     * @param model                 The model.
     * @param storeNumber           The store number.
     * @param preSelectedLocationId The location to preselect.
     */
    public static void loadInventoryLocations(InventoryBeanModelIfc model,
                                              String  storeNumber,
                                              Integer preSelectedLocationId)
    {
        // Sanity
        if (model == null)
        {
            return;
        }

        // Set inventory locations
        Vector<Integer> locationIdList = new Vector<Integer>();
        Vector<String> locationNameList = new Vector<String>();


        model.setInvLocationIds(locationIdList);
        model.setInvLocationNames(locationNameList);

        // Pre-select a location
        if (preSelectedLocationId != null)
        {
            Integer defaultLocationId = Integer.valueOf(preSelectedLocationId.intValue());
            if (!locationIdList.isEmpty() && locationIdList.indexOf(defaultLocationId) == -1)
            {
                // If we can't find the desired inventory location, use a well-known default.
                // (If the list is empty, assume we are offline and just use the pre-selected value.)
                defaultLocationId = locationIdList.get(SALES_FLOOR);
            }

            model.setSelectedInvLocationId(defaultLocationId);
        }
    }

    /**
     * Set the UI Locale for Customer.
     * @param customerLocale
     */
    public static void setUILocaleForCustomer(Locale customerLocale)
    {
        customerLocale = LocaleMap.adjustLocale(customerLocale);
        LocaleMap.putLocale(LocaleConstantsIfc.RECEIPT, customerLocale);
        LocaleMap.putLocale(LocaleConstantsIfc.POLE_DISPLAY, customerLocale);
        LocaleMap.putLocale(LocaleConstantsIfc.DEVICES, customerLocale);
    }

    /**
     * Sets the UI Locale for Employee.
     * @param employeeLocale
     * @param ui
     */
    public static void setUILocaleForEmployee(Locale employeeLocale, POSUIManagerIfc ui)
    {
        employeeLocale = LocaleMap.adjustLocale (employeeLocale);
        if (!LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE).equals(employeeLocale))
        {
            LocaleMap.putLocale(LocaleConstantsIfc.USER_INTERFACE,
                    employeeLocale);

            if (ui != null)
            {
                ui.setLookAndFeel();
            }
        }
    }

    /**
     * This is a more predictable layout assuming the labels and components are
     * line up as complementary pairs in two columns.
     *
     * Column 1 is for labels. It stretches and ocuppies a minimum of 1/3 of the
     * panel. The text is expanded to completely fill the column <BR>
     * Column 2 is for components. It stretches and ocuppies the remaining of the panel.
     * The text is expanded to completely fill the column <BR>
     *
     * @param bean the panel to add components to
     * @param labels an array of text labels
     * @param components an array of components
     * @param addWeight flag indicating last components get full weight
     */
    public static void alternateLayoutDataPanel(JPanel bean, JLabel[] labels, JComponent[] components, boolean addWeight)
    {
        int size = labels.length;

        UIFactory factory = UIFactory.getInstance();

        bean.setLayout(new GridBagLayout());
        GridBagConstraints constraints = factory.getConstraints("DataEntryBean");
        constraints.weightx = 1;
        constraints.weighty = 0;

        // layout top label and field
        constraints.gridy = 0;
        if (size == 1 || addWeight)
        {
            constraints.insets = factory.getInsets("singleLabelTop");
        }
        else
        {
            constraints.insets = factory.getInsets("defaultLabelTop");
        }
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.RELATIVE;
        if (labels[0] != null)
        {
            bean.add(labels[0], constraints);
        }

        if (size == 1 || addWeight)
        {
            constraints.insets = factory.getInsets("singleFieldTop");
        }
        else
        {
            constraints.insets = factory.getInsets("defaultFieldTop");
        }
        constraints.gridx = 1;

        // adjust for single pair layouts
        if (size == 1)
        {
            constraints.weighty = 1.0;
        }

        constraints.weightx = 2.0;
        if (components[0] instanceof JLabel)
        {
            constraints.fill = GridBagConstraints.HORIZONTAL;
        }
        else
        {
            constraints.fill = GridBagConstraints.NONE;
        }
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        bean.add(components[0], constraints);

        // layout the middle labels and fields
        for (int i = 1; i < (size - 1); i++)
        {
            constraints.gridy = i;
            constraints.gridx = 0;
            constraints.insets = factory.getInsets("defaultLabelLeft");
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1;

            if (labels[i] != null)
            {
                constraints.gridwidth = GridBagConstraints.RELATIVE;
                bean.add(labels[i], constraints);
            }
            if (components[i] != null)
            {
                constraints.gridx = 1;
                constraints.insets = factory.getInsets("defaultFieldRight");
                if (components[i] instanceof JLabel)
                {
                    constraints.fill = GridBagConstraints.HORIZONTAL;
                }
                else
                {
                    constraints.fill = GridBagConstraints.NONE;
                }
                constraints.weightx = 2;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                bean.add(components[i], constraints);
            }
        }

        // Layout Bottom Label and Field
        if (size > 1)
        {
            int i = labels.length - 1;
            constraints.gridy = i;
            constraints.gridx = 0;
            constraints.insets = factory.getInsets("defaultLabelBottom");
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1;
            constraints.gridwidth = GridBagConstraints.RELATIVE;
            bean.add(labels[i], constraints);

            if (components[i] != null)
            {
                constraints.gridx = 1;
                constraints.insets = factory.getInsets("defaultFieldBottom");
                if (components[i] instanceof JLabel)
                {
                    constraints.fill = GridBagConstraints.HORIZONTAL;
                }
                else
                {
                    constraints.fill = GridBagConstraints.NONE;
                }


                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.weightx = 2;
                constraints.gridwidth = GridBagConstraints.REMAINDER;

                if (addWeight)
                {
                    constraints.weighty = 1.0;
                }
                bean.add(components[i], constraints);
            }
        }
    }

    /**
     * Wraps a JLabel Text by enclosing it within <HTML> tags.
     * @param label
     */
    public static void wrapJLabelText(JLabel label)
    {
        if (label.getText() != null)
        {
            String text = "<HTML>" + label.getText() + "</HTML>";
            label.setText(text);
        }
    }

    /**
     * If the item description text string is too wide to fit within the available
     * space allocated in the work panel, specific number characters and "..."
     * will be displayed instead.
     * @param args Item description text string
     * @param displayLength Specified length of description string to be displayed
     *          in the screen
     * @return {@link String} Truncated description string suffixed with "..."
     */
    public static String makeSafeStringForDisplay(String args, int displayLength)
    {
        if (args == null)
            return "";

        String clipString = "...";
        args = args.trim();
        if (args.length() > displayLength)
        {
            StringBuilder buffer = new StringBuilder(args.substring(0, displayLength));
            return buffer.append(clipString).toString();
        }
        return args;
    }
    
  public static StatusBeanModel getStatusBean(AbstractFinancialCargo cargo)
  {
    StatusBeanModel sModel = null;
    sModel = new StatusBeanModel();
    boolean trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
    sModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);
    sModel.setCashierName(cargo.getOperator().getPersonName().getFirstLastName());
    sModel.setSalesAssociateName(cargo.getOperator().getPersonName().getFirstLastName());
    sModel.setRegister(cargo.getRegister());
    ParameterManagerIfc pm;
    pm = (ParameterManagerIfc) Gateway.getDispatcher().getManager(ParameterManagerIfc.TYPE);
    try
    {
      Serializable[] values;
      values = pm.getParameterValues("DefaultToCashier");

      String parameterValue = (String) values[0];
      if (parameterValue.equalsIgnoreCase("Y"))
      {
        values = pm.getParameterValues("IdentifySalesAssociateEveryTransaction");
        parameterValue = (String) values[0];
        if (parameterValue.equalsIgnoreCase("Y"))
        {
          if (cargo instanceof TenderCargo)
          {
            TenderCargo tenderCargo = (TenderCargo)cargo;
            if (tenderCargo.getEmployee() != null)
            {
              sModel.setSalesAssociateName(tenderCargo.getEmployee().getPersonName().getFirstLastName());
            }
          }
          else if (cargo instanceof BillPayCargo)
          {
            BillPayCargo billPayCargo = (BillPayCargo) cargo;
            if (billPayCargo.getAccessEmployee() != null)
            {
              sModel.setSalesAssociateName(billPayCargo.getAccessEmployee().getPersonName().getFirstLastName());
            }
          }

        }
        else
        {
          sModel.setSalesAssociateName(cargo.getOperator().getPersonName().getFirstLastName());
        }
      }
    }
    catch (ParameterException e)
    {
      logger.error("" + Util.throwableToString(e) + "");
    }
    return sModel;
  }

  /**
   * Sets the ui online/offline status for the financial network based on the status returned in 
   * the payment service response.
   * 
   * @param paymentServiceResponse
   * @param ui
   */
  public static void setFinancialNetworkUIStatus(AuthorizeResponseIfc authResponse, POSUIManagerIfc ui)
  {
      boolean online = POSUIManagerIfc.OFFLINE;
      
      if (authResponse == null)
      {
          logger.warn("paymentServiceResponse is null.  Assuming financial network is offline.");
      }
      else if (authResponse.getFinancialNetworkStatus() == AuthorizationConstantsIfc.ONLINE)  // TODO: APF: normalize POSUIManagerIfc.on/offline and AuthorizationConstantsIfc.on/offline and all the other occurances as well - int, enum, boolean, String - we seem to use them all 
      {
          online = POSUIManagerIfc.ONLINE;
      }
      ui.statusChanged(POSUIManagerIfc.FINANCIAL_NETWORK_STATUS, online);
  }

  /**
   * Gets the notifications available status for the given orpos/mpos register id
   * For ORPOS clients, POS is the key in the map to get the status
   * 
   * @param registerId
   * @return Boolean of the notificatons available status
   */
  public static Boolean getNotificationsAvailableStatus(String registerId)
  {
      return notificationsAvailableStatus.get(registerId);
  }

  /**
   * Sets the notifications available status for the orpos/mpos registerId
   * @param registerId
   */
  public static void addNotificationsAvailableStatus(String registerId)
  {
      UIUtilities.notificationsAvailableStatus.put(registerId, Boolean.TRUE);
  }
  
  /**
   * Update the notifications available status for the orpos/mpos registerId
   * For ORPOS clients, POS is the key in the map to update the status
   * 
   * @param registerId
   * @param boolean status
   */
  public static void updateNotificationsAvailableStatus(String registerId, Boolean status)
  {
      UIUtilities.notificationsAvailableStatus.put(registerId, status);
  }

}
