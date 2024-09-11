/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSLookAndFeel.java /main/21 2014/05/20 12:14:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/20/14 - refactor list model sorting
 *    abhinavs  05/11/14 - Sorting Item search results enhancement
 *    cgreene   10/17/12 - tweak implementation of search field with icon
 *    cgreene   10/15/12 - implement buttons that can use images to paint
 *                         background
 *    vbongu    10/01/12 - adding ValidatingComboBox UI
 *    rrkohli   06/16/11 - Highligting CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   12/17/09 - add support for toggle buttons
 *    mdecama   02/06/09 - Added LookAndFeel support by Locale
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:21:34 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse
 *  $
 *  Revision 1.4  2004/09/23 00:07:18  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.3  2004/02/12 16:52:14  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:13:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Sep 10 2002 17:49:26   baa
 * add password field
 * Resolution for POS SCR-1810: Adding pasword validating fields
 *
 *    Rev 1.0   Apr 29 2002 14:46:06   msg
 * Initial revision.
 *
 *    Rev 1.1   10 Apr 2002 13:59:50   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.0   Mar 18 2002 11:58:48   msg
 * Initial revision.
 *
 *    Rev 1.6   13 Mar 2002 23:46:26   baa
 * fix painting problems
 * Resolution for POS SCR-1343: Split tender using Cash causes half of Tender Options to flash
 *
 *    Rev 1.5   11 Mar 2002 16:43:26   dwt
 * performance improvements for ui
 *
 *    Rev 1.4   14 Feb 2002 12:14:48   dwt
 * modfied background drawing for rounded borders
 *
 *    Rev 1.3   30 Jan 2002 23:03:48   baa
 * fix arrow keys on combo box
 * Resolution for POS SCR-928: Arrow keys do not work on Check Entry
 *
 *    Rev 1.2   30 Jan 2002 22:31:48   baa
 * fix arrows for textfields
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 *    Rev 1.1   Jan 22 2002 06:33:16   mpm
 * UI fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Jan 19 2002 11:05:00   mpm
 * Initial revision.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.DefaultEditorKit;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * The default look and feel for the POS application. The ui defaults for this
 * LAF are loaded from a property file. This allows ui elements like colors,
 * fonts, borders, etc. to be modified without recompiling the application.
 * 
 * @version $Revision: /main/21 $
 */
public class EYSLookAndFeel extends BasicLookAndFeel
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -6696817641440372849L;

    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /main/21 $";

    public static final Integer FONT_BOLD = Integer.valueOf(Font.BOLD);
    public static final Integer FONT_PLAIN = Integer.valueOf(Font.PLAIN);

    public static final Integer ALIGN_LEFT = Integer.valueOf(SwingConstants.LEFT);
    public static final Integer ALIGN_CENTER = Integer.valueOf(SwingConstants.CENTER);
    public static final Integer ALIGN_RIGHT = Integer.valueOf(SwingConstants.RIGHT);

    /**
     * Gets the name of this look and feel.
     * 
     * @return the name of this look and feel
     */
    @Override
    public String getName()
    {
        return "OracleStore";
    }

    /**
     * Gets the id of this look and feel.
     * 
     * @return this look and feel's id
     */
    @Override
    public String getID()
    {
        return "OracleStore";
    }

    /**
     * Gets this look and feel's description.
     * 
     * @return the description
     */
    @Override
    public String getDescription()
    {
        return "The Oracle Store Solutions(tm) Look and Feel";
    }

    /**
     * Returns true if this is the native look and feel for the current
     * platform.
     * 
     * @return always returns false for the 360Store L&F
     */
    @Override
    public boolean isNativeLookAndFeel()
    {
        return false;
    }

    /**
     * Returns true if this L&F is supported by the current platform.
     * 
     * @return always returns true for the 360Store L&F
     */
    @Override
    public boolean isSupportedLookAndFeel()
    {
        return true;
    }

    /**
     * Initializes the component UI mappings for the 360Store Look and Feel.
     * 
     * @param table the UIDefaults table
     */
    @Override
    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);

        String packageName = "oracle.retail.stores.pos.ui.plaf.eys.";

        Object[] uiDefaults =
        {
                  "BaseTableHeaderUI", packageName + "EYSBasicTableHeaderUI",    
                        "EYSButtonUI", packageName + "EYSButtonUI",
                     "ToggleButtonUI", packageName + "EYSToggleButtonUI",
                          "EYSListUI", packageName + "EYSListUI",
                            "PanelUI", packageName + "EYSPanelUI",
                      "RequiredBoxUI", packageName + "EYSRequiredBoxUI",
                    "RequiredFieldUI", packageName + "EYSRequiredFieldUI",
            "RequiredPasswordFieldUI", packageName + "EYSRequiredPasswordFieldUI",
                         "TextAreaUI", packageName + "EYSTextAreaUI",
               "ValidatingComboBoxUI", packageName + "EYSValidatingComboBoxUI",
                     "EYSScrollBarUI", table.get("ScrollBarUI"),
        };
        table.putDefaults(uiDefaults);
    }

    /**
     * Initializes the system colors. For the 360Store L&F, the colors are
     * loaded from a properties object stored in the UIFactory.
     * @param table the UIDefaults table
     */
    @Override
    protected void initSystemColorDefaults(UIDefaults table)
    {
        super.initSystemColorDefaults(table);

        String[] eysColors = null;

        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        Properties uiProps = UIFactory.getInstance().getUIProperties(lcl);

        // if we have properties, load colors from them
        if (uiProps != null)
        {
            eysColors = parseColorProperties(uiProps);
            loadSystemColors(table, eysColors, isNativeLookAndFeel());
        }
    }

    /**
     * Initializes the default characteristics for ui components. This sets
     * display properties for standard components as well as 360Store beans.
     * @param table the UIDefaults table
     */
    @Override
    protected void initComponentDefaults(UIDefaults table)
    {
        super.initComponentDefaults(table);

        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        Properties uiProps = UIFactory.getInstance().getUIProperties(lcl);

        // if the properties are not null, process the plaf data
        if (uiProps != null)
        {
            // load the primitive plaf objects (colors, fonts, etc.)
            table.putDefaults(parseFontProperties(uiProps));
            table.putDefaults(parseInsetProperties(uiProps));
            table.putDefaults(parseDimensionProperties(uiProps));
            table.putDefaults(parseWeightProperties(uiProps));

            // create the array of default mappings
            ArrayList<Object> defaultList = new ArrayList<Object>();

            // loop through the properties and extract the data
            for (Enumeration<?> e = uiProps.propertyNames(); e.hasMoreElements();)
            {
                String propName = (String)e.nextElement();

                if (!isPrimitiveName(propName))
                {
                    defaultList.add(propName);
                    String propValue = uiProps.getProperty(propName);
                    defaultList.add(getPlafObject(propName, propValue, table));
                }
            }
            // when we're done, convert the defaults to an object array
            // and put them in the UIDefaults table
            table.putDefaults(defaultList.toArray());
        }
        // At this point, all of the ui property values should be stored in
        // the UIDefaults. Now, we create the ui objects that are not
        // defined in the look and feel properties.

        // initialize radio button icon
        EYSIconFactory.getRadioButtonIcon();

        // key bindings for text fields
        Object fieldInputMap = new UIDefaults.LazyInputMap(new Object[]
        {
                 "ENTER", JTextField.notifyAction,
            "typed \010", DefaultEditorKit.deletePrevCharAction,
                "DELETE", DefaultEditorKit.deleteNextCharAction,
                 "RIGHT", DefaultEditorKit.forwardAction,
                  "LEFT", DefaultEditorKit.backwardAction,
              "KP_RIGHT", DefaultEditorKit.forwardAction,
               "KP_LEFT", DefaultEditorKit.backwardAction,
                "ctrl C", DefaultEditorKit.copyAction,
                "ctrl V", DefaultEditorKit.pasteAction,
                "ctrl X", DefaultEditorKit.cutAction,
                  "COPY", DefaultEditorKit.copyAction,
                 "PASTE", DefaultEditorKit.pasteAction,
                   "CUT", DefaultEditorKit.cutAction
        });
        // key bindings for text areas and panes
        Object multilineInputMap = new UIDefaults.LazyInputMap(new Object[]
        {
                "ctrl C", DefaultEditorKit.copyAction,
                "ctrl V", DefaultEditorKit.pasteAction,
                "ctrl X", DefaultEditorKit.cutAction,
                  "COPY", DefaultEditorKit.copyAction,
                 "PASTE", DefaultEditorKit.pasteAction,
                   "CUT", DefaultEditorKit.cutAction,
                 "ENTER", DefaultEditorKit.insertBreakAction,
            "typed \010", DefaultEditorKit.deletePrevCharAction,
                "DELETE", DefaultEditorKit.deleteNextCharAction,
                 "RIGHT", DefaultEditorKit.forwardAction,
                  "LEFT", DefaultEditorKit.backwardAction,
              "KP_RIGHT", DefaultEditorKit.forwardAction,
               "KP_LEFT", DefaultEditorKit.backwardAction,
                    "UP", DefaultEditorKit.upAction,
                 "KP_UP", DefaultEditorKit.upAction,
                  "DOWN", DefaultEditorKit.downAction,
               "KP_DOWN", DefaultEditorKit.downAction,
               "PAGE_UP", DefaultEditorKit.pageUpAction,
             "PAGE_DOWN", DefaultEditorKit.pageDownAction,
                  "HOME", DefaultEditorKit.beginAction,
                   "END", DefaultEditorKit.endAction
        });
        // key bindings for lists
        Object listInputMap = new UIDefaults.LazyInputMap(new Object[] {
                       "UP", "selectPreviousRow",
                    "KP_UP", "selectPreviousRow",
                     "DOWN", "selectNextRow",
                  "KP_DOWN", "selectNextRow",
                     "HOME", "selectFirstRow",
                      "END", "selectLastRow",
                  "PAGE_UP", "scrollUp",
                "PAGE_DOWN", "scrollDown"
         });
         // key bindings for comboBox
        Object comboBoxInputMap = new UIDefaults.LazyInputMap(new Object[] {
                       "UP", "selectPrevious",
                    "KP_UP", "selectPrevious",
                     "DOWN", "selectNext",
                  "KP_DOWN", "selectNext",
                     "HOME", "homePassThrough",
                      "END", "endPassThrough"
            });

        // put the non-property defined objects into a default array
        // and give it to the UIDefaults
        Object[] defaults =
        {
                "TextField.focusInputMap", fieldInputMap,
               "EditorPane.focusInputMap", multilineInputMap,
            "PasswordField.focusInputMap", fieldInputMap,
                 "TextArea.focusInputMap", multilineInputMap,
                 "TextPane.focusInputMap", multilineInputMap,
                     "List.focusInputMap", listInputMap,
                 "ComboBox.focusInputMap", comboBoxInputMap
        };
        table.putDefaults(defaults);
    }

    /**
     * Parses color properties from a properties object and returns a string
     * array for the default table.
     * 
     * @param props the properties object
     * @return an array of name/string value pairs
     */
    protected String[] parseColorProperties(Properties props)
    {
        List<String> list = parsePropertyList(props, "Color.");

        String[] result = new String[list.size()];

        return list.toArray(result);
    }

    /**
     * Parses font properties from a properties object and returns an object
     * array for the default table.
     * 
     * @param props the properties object
     * @return an array of name/FontUIResource pairs
     */
    protected Object[] parseFontProperties(Properties props)
    {
        List<String> list = parsePropertyList(props, "Font.");
        Object[] result = new Object[list.size()];
        result = list.toArray(result);

        for (int i = 0; i < result.length; i += 2)
        {
            String[] fontList =
                UIUtilities.parseDelimitedList((String)result[i+1], ",");

            Integer style = FONT_PLAIN;

            if (fontList[1].equalsIgnoreCase("bold"))
            {
                style = FONT_BOLD;
            }
            Integer size;

            try
            {
                size = new Integer(fontList[2]);
            }
            catch (NumberFormatException nfe)
            {
                size = new Integer(12);
            }
            result[i+1] =
                new UIDefaults.ProxyLazyValue("javax.swing.plaf.FontUIResource",
                                              null,
                                              new Object[] {fontList[0], style, size});
        }
        return result;
    }

    /**
     * Parses inset properties from a properties object and returns an object
     * array for the default table.
     * 
     * @param props the properties object
     * @return an array of name/InsetUIResource pairs
     */
    protected Object[] parseInsetProperties(Properties props)
    {
        List<String> list = parsePropertyList(props, "Insets.");
        Object[] result = new Object[list.size()];
        result = list.toArray(result);

        for (int i = 0; i < result.length; i += 2)
        {
            String[] insetList =
                UIUtilities.parseDelimitedList((String)result[i+1], ",");

            InsetsUIResource insets = null;

            try
            {
                insets =
                    new InsetsUIResource(Integer.parseInt(insetList[0]),
                                         Integer.parseInt(insetList[1]),
                                         Integer.parseInt(insetList[2]),
                                         Integer.parseInt(insetList[3]));
            }
            catch (Exception e)
            {
                insets = new InsetsUIResource(0, 0, 0, 0);
            }
            result[i + 1] = insets;
        }
        return result;
    }

    /**
     * Parses dimension properties from a properties object and returns an
     * object array for the default table.
     * 
     * @param props the properties object
     * @return an array of name/DimensionUIResource pairs
     */
    protected Object[] parseDimensionProperties(Properties props)
    {
        List<String> list = parsePropertyList(props, "Dimension.");
        Object[] result = new Object[list.size()];
        result = list.toArray(result);

        for (int i = 0; i < result.length; i += 2)
        {
            String[] dimensions =
                UIUtilities.parseDelimitedList((String)result[i+1], ",");

            DimensionUIResource dimension = null;

            try
            {
                dimension =
                    new DimensionUIResource(Integer.parseInt(dimensions[0]),
                                            Integer.parseInt(dimensions[1]));
            }
            catch (Exception e)
            {
                dimension = new DimensionUIResource(0, 0);
            }
            result[i + 1] = dimension;
        }
        return result;
    }

    /**
     * Parses weight properties from a properties object and returns a string
     * array for the default table. The weights are stored as a string
     * representing a comma-delimited list of integers.
     * 
     * @param props the properties object
     * @return an array of name/string value pairs
     */
    protected String[] parseWeightProperties(Properties props)
    {
        List<String> list = parsePropertyList(props, "Weights.");

        String[] result = new String[list.size()];

        return list.toArray(result);
    }

     /**
     * Extracts properties from a properties object that match the provided
     * property key. For example, the tag "List" would return all properties
     * that begin with "List."
     * 
     * @param props the properties object
     * @param tag the property tag to look for
     * @return an ArrayList of matching properties
     */
    protected List<String> parsePropertyList(Properties props, String tag)
    {
        List<String> list = new ArrayList<String>(props.size());

        for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();)
        {
            String propName = (String)e.nextElement();
            if (propName.startsWith(tag))
            {
                list.add(propName.substring(tag.length()));
                list.add(props.getProperty(propName));
            }
        }
        return list;
    }

    /**
     * Determines if the given property tag represents a 'primitive' tag.
     * Primitive tags are things like 'Color', 'Font', 'Insets', etc.
     * 
     * @param propValue the property tag
     * @return true if the tag is a primitive, false otherwise
     */
    protected boolean isPrimitiveName(String propValue)
    {
        boolean result = false;

        if (propValue.startsWith("BorderFactory") ||
            propValue.startsWith("Color") ||
            propValue.startsWith("Dimension") ||
            propValue.startsWith("Font") ||
            propValue.startsWith("Insets") ||
            propValue.startsWith("Weights"))
        {
            result = true;
        }
        return result;
    }

    /**
     * Creates the appropriate plaf object to associate with a named property.
     * These are stored in the UIDefaults object, to use in configuring ui
     * components.
     * 
     * @param propName the name of the property
     * @param propValue the string value from the properties file
     * @param table the table of ui defaults
     * @return the appropriate object (color, border, font, etc.)
     */
    protected Object getPlafObject(String propName, String propValue, UIDefaults table)
    {
        Object result = null;
        String testValue = propName.toLowerCase();

        // if the value represents a border, use the border factory
        // to create a border object
        if (testValue.endsWith("border"))
        {
            result = EYSBorderFactory.getBorder(propValue);
        }
        // if the value is an alignment value, return the integer object
        else if (testValue.endsWith("alignment"))
        {
            if (propValue.equals("right"))
            {
                result = ALIGN_RIGHT;
            }
            else if (propValue.equals("center"))
            {
                result = ALIGN_CENTER;
            }
            else
            {
                result = ALIGN_LEFT;
            }
        }
        // if it is an opaque value, just return the string object
        else if (testValue.endsWith("opaque"))
        {
            result = propValue;
        }
        else if (testValue.endsWith("onfocus"))
        {
            result = propValue;
        }
        // otherwise, get the primitive object from the table
        else
        {
            result = table.get(propValue);
        }
        // if it was not a primitive object, just put the string in the defaults.
        if (result == null)
        {
            result = propValue;
        }
        return result;
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
}
