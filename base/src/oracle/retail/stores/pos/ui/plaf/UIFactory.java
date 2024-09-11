/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/UIFactory.java /main/40 2014/06/03 17:06:10 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/02/14 - Added TabbedUIBean for tabbed UI support
 *    yiqzhao   03/18/14 - Make allowable characters configurable.
 *    yiqzhao   03/17/14 - Allow hypen in item id and serial number.
 *    cgreene   01/10/14 - added a JButton method
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    cgreene   09/18/13 - only check for country and variant properties if the
 *                         values exist in the locale
 *    cgreene   04/30/13 - OSK needs to use EYSButtons so that borders and
 *                         background can be painted properly
 *    asinton   12/20/12 - fix null pointer exception when lafFile is null
 *    cgreene   12/14/12 - Button image and plaf loading updates
 *    mjwallac  11/21/12 - ui button changes
 *    cgreene   10/17/12 - tweak implementation of search field with icon
 *    cgreene   10/15/12 - implement buttons that can use images to paint
 *                         background
 *    yiqzhao   07/09/12 - add createCurrencyField with column size specfied.
 *    vtemker   04/05/12 - Added createNumericByteTextField methods
 *    cgreene   03/05/12 - check for null when getting properties
 *    asinton   06/27/11 - Added Call Referral UI and flow to the tender
 *                         authorization tour
 *    cgreene   06/17/11 - added method createMaskedCardNumberField
 *    rrkohli   06/16/11 - Highlighting CR
 *    cgreene   10/25/10 - do not set preferred size if size is null
 *    cgreene   10/22/10 - re-org constants into interface
 *    cgreene   07/02/10 - added createURLLabel method
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    dwfung    02/03/10 - add support for Validating List
 *    abondala  01/03/10 - update header date
 *    cgreene   12/16/09 - add createToggleButton method
 *    cgreene   11/09/09 - refactor createConstrainedField methods to reduce
 *                         cutnpaste
 *    mdecama   02/12/09 - Added LookAndFeel support by Locale
 *    mdecama   02/06/09 - Added LookAndFeel support by Locale
 *    mkochumm  12/09/08 - fix screen layout
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/07/08 - i18n changes for phone and postalcode fields
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
    $Log:
     18   I18N_P2    1.15.1.1    1/4/2008 5:00:24 PM    Maisa De Camargo CR
          29826 - Setting the size of the combo boxes. This change was
          necessary because the width of the combo boxes used to grow
          according to the length of the longest content. By setting the size,
           we allow the width of the combo box to be set independently from
          the width of the dropdown menu.
     17   I18N_P2    1.15.1.0    12/26/2007 9:54:39 AM  Maisa De Camargo CR
          29822 - I18N - Fixed Collapsing of Input Fields when labels are
          expanded.
     16   360Commerce 1.15        11/29/2007 5:15:58 PM  Alan N. Sinton  CR
          29677: Protect user entry fields of PAN data.
     15   360Commerce 1.14        10/11/2007 12:31:18 PM Peter J. Fierro
          Changes to define popup widths independently of max columns
     14   360Commerce 1.13        10/10/2007 1:08:11 PM  Anda D. Cadar
          Updates
     13   360Commerce 1.12        10/8/2007 2:01:30 PM   Maisa De Camargo Added
           a new signature for the method createValidatingComboBox to include
          the parameter columns - width of the display field.
          Refactored the other createValidatingComboBox methods.
     12   360Commerce 1.11        10/8/2007 11:36:46 AM  Anda D. Cadar   UI
          changes to not allow double bytes chars in some cases
     11   360Commerce 1.10        10/4/2007 4:31:59 PM   Maisa De Camargo
          Created new methods to create UI Fields. The signature of these
          methods include the column parameter. The column represents the
          width of the display field.
     10   360Commerce 1.9         8/8/2007 5:53:01 PM    Michael P. Barnett
          Specify max length of NumericDecimal field.
     9    360Commerce 1.8         7/9/2007 3:07:55 PM    Anda D. Cadar   I18N
          changes for CR 27494: POS 1st initialization when Server is offline
     8    360Commerce 1.7         7/3/2007 9:50:35 AM    Anda D. Cadar   get
          the number of decimal digits from base currency type
     7    360Commerce 1.6         6/18/2007 2:48:10 PM   Anda D. Cadar
          cleanup - use UI locale not default
     6    360Commerce 1.5         5/8/2007 11:32:33 AM   Anda D. Cadar
          currency changes for I18N
     5    360Commerce 1.4         1/25/2006 4:11:53 PM   Brett J. Larsen merge
          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
     4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
          Base-lining of 7.1_LA
     3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse
     2    360Commerce 1.1         3/10/2005 10:26:29 AM  Robert Pearse
     1    360Commerce 1.0         2/11/2005 12:15:21 PM  Robert Pearse
    $:
     5    .v700     1.2.1.1     11/8/2005 11:24:05     Jason L. DeLeau 6614:
          Add SS, NaPhone to UIFactory, add methods to take String's as
          parameters for constraintedTextAreas , fix possible NPE
     4    .v700     1.2.1.0     5/12/2005 15:16:52     Charles Suehs   Added
          labels to the fields in the status bean to make automated testing
          easier.
     3    360Commerce1.2         3/31/2005 15:30:38     Robert Pearse
     2    360Commerce1.1         3/10/2005 10:26:29     Robert Pearse
     1    360Commerce1.0         2/11/2005 12:15:21     Robert Pearse
    $
    $Log:
     18   I18N_P2    1.15.1.1    1/4/2008 5:00:24 PM    Maisa De Camargo CR
          29826 - Setting the size of the combo boxes. This change was
          necessary because the width of the combo boxes used to grow
          according to the length of the longest content. By setting the size,
           we allow the width of the combo box to be set independently from
          the width of the dropdown menu.
     17   I18N_P2    1.15.1.0    12/26/2007 9:54:39 AM  Maisa De Camargo CR
          29822 - I18N - Fixed Collapsing of Input Fields when labels are
          expanded.
     16   360Commerce 1.15        11/29/2007 5:15:58 PM  Alan N. Sinton  CR
          29677: Protect user entry fields of PAN data.
     15   360Commerce 1.14        10/11/2007 12:31:18 PM Peter J. Fierro
          Changes to define popup widths independently of max columns
     14   360Commerce 1.13        10/10/2007 1:08:11 PM  Anda D. Cadar
          Updates
     13   360Commerce 1.12        10/8/2007 2:01:30 PM   Maisa De Camargo Added
           a new signature for the method createValidatingComboBox to include
          the parameter columns - width of the display field.
          Refactored the other createValidatingComboBox methods.
     12   360Commerce 1.11        10/8/2007 11:36:46 AM  Anda D. Cadar   UI
          changes to not allow double bytes chars in some cases
     11   360Commerce 1.10        10/4/2007 4:31:59 PM   Maisa De Camargo
          Created new methods to create UI Fields. The signature of these
          methods include the column parameter. The column represents the
          width of the display field.
     10   360Commerce 1.9         8/8/2007 5:53:01 PM    Michael P. Barnett
          Specify max length of NumericDecimal field.
     9    360Commerce 1.8         7/9/2007 3:07:55 PM    Anda D. Cadar   I18N
          changes for CR 27494: POS 1st initialization when Server is offline
     8    360Commerce 1.7         7/3/2007 9:50:35 AM    Anda D. Cadar   get
          the number of decimal digits from base currency type
     7    360Commerce 1.6         6/18/2007 2:48:10 PM   Anda D. Cadar
          cleanup - use UI locale not default
     6    360Commerce 1.5         5/8/2007 11:32:33 AM   Anda D. Cadar
          currency changes for I18N
     5    360Commerce 1.4         1/25/2006 4:11:53 PM   Brett J. Larsen merge
          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
     4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
          Base-lining of 7.1_LA
     3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse
     2    360Commerce 1.1         3/10/2005 10:26:29 AM  Robert Pearse
     1    360Commerce 1.0         2/11/2005 12:15:21 PM  Robert Pearse
    $: UIFactory.java,v $
    $Log:
     18   I18N_P2    1.15.1.1    1/4/2008 5:00:24 PM    Maisa De Camargo CR
          29826 - Setting the size of the combo boxes. This change was
          necessary because the width of the combo boxes used to grow
          according to the length of the longest content. By setting the size,
           we allow the width of the combo box to be set independently from
          the width of the dropdown menu.
     17   I18N_P2    1.15.1.0    12/26/2007 9:54:39 AM  Maisa De Camargo CR
          29822 - I18N - Fixed Collapsing of Input Fields when labels are
          expanded.
     16   360Commerce 1.15        11/29/2007 5:15:58 PM  Alan N. Sinton  CR
          29677: Protect user entry fields of PAN data.
     15   360Commerce 1.14        10/11/2007 12:31:18 PM Peter J. Fierro
          Changes to define popup widths independently of max columns
     14   360Commerce 1.13        10/10/2007 1:08:11 PM  Anda D. Cadar
          Updates
     13   360Commerce 1.12        10/8/2007 2:01:30 PM   Maisa De Camargo Added
           a new signature for the method createValidatingComboBox to include
          the parameter columns - width of the display field.
          Refactored the other createValidatingComboBox methods.
     12   360Commerce 1.11        10/8/2007 11:36:46 AM  Anda D. Cadar   UI
          changes to not allow double bytes chars in some cases
     11   360Commerce 1.10        10/4/2007 4:31:59 PM   Maisa De Camargo
          Created new methods to create UI Fields. The signature of these
          methods include the column parameter. The column represents the
          width of the display field.
     10   360Commerce 1.9         8/8/2007 5:53:01 PM    Michael P. Barnett
          Specify max length of NumericDecimal field.
     9    360Commerce 1.8         7/9/2007 3:07:55 PM    Anda D. Cadar   I18N
          changes for CR 27494: POS 1st initialization when Server is offline
     8    360Commerce 1.7         7/3/2007 9:50:35 AM    Anda D. Cadar   get
          the number of decimal digits from base currency type
     7    360Commerce 1.6         6/18/2007 2:48:10 PM   Anda D. Cadar
          cleanup - use UI locale not default
     6    360Commerce 1.5         5/8/2007 11:32:33 AM   Anda D. Cadar
          currency changes for I18N
     5    360Commerce 1.4         1/25/2006 4:11:53 PM   Brett J. Larsen merge
          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
     4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
          Base-lining of 7.1_LA
     3    360Commerce 1.2         3/31/2005 4:30:38 PM   Robert Pearse
     2    360Commerce 1.1         3/10/2005 10:26:29 AM  Robert Pearse
     1    360Commerce 1.0         2/11/2005 12:15:21 PM  Robert Pearse
    $:
     5    .v710     1.2.1.0.1.0 10/26/2005 16:46:46    Charles Suehs   Modify
          createLabel.  If no name given, make one up from the call stack.
     4    .v700     1.2.1.0     5/12/2005 15:16:52     Charles Suehs   Added
          labels to the fields in the status bean to make automated testing
          easier.
     3    360Commerce1.2         3/31/2005 15:30:38     Robert Pearse
     2    360Commerce1.1         3/10/2005 10:26:29     Robert Pearse
     1    360Commerce1.0         2/11/2005 12:15:21     Robert Pearse
    $
    Revision 1.11  2004/07/22 19:18:59  dcobb
    @scr 5443 MAX lengths on Check Search screen is incorrect for MICR Number and Item Number
    Added method to UIFactory to be able to set the width of the field independently of the masLength.
    Also designated MICRNumber as a MICRField so that the check can be MICR'd.

    Revision 1.10  2004/07/05 16:38:20  aachinfiev
    @scr 5215 - Password accepting non-alphanumeric characters

    Revision 1.9  2004/04/16 18:56:34  tfritz
    @scr 4251 - Integer parameters now can except negative and positive integers.

    Revision 1.8  2004/04/13 21:27:26  rsachdeva
    @scr 3906 Sale ValidatingField

    Revision 1.7  2004/04/01 00:11:33  cdb
    @scr 4206 Corrected some header foul ups caused by Eclipse auto formatting.


    Revision 1.1.1.1 2004/02/11 01:04:23 cschellenger
    updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.4 Dec 29 2003 15:44:38 baa return enhancements
 *
 * Rev 1.3 Dec 17 2003 11:23:06 baa return enhancements Resolution for 3561:
 * Feature Enhacement: Return Search by Tender
 *
 * Rev 1.2 Oct 29 2003 14:14:34 baa fix currency digits calculation
 *
 * Rev 1.1.1.0 Oct 28 2003 13:56:56 baa fix currency digits
 *
 * Rev 1.1 Sep 08 2003 17:30:56 DCobb Migration to jvm 1.4.1 Resolution for
 * 3361: New Feature: JVM 1.4.1_03 (Windows) Migration
 *
 * Rev 1.0 Aug 29 2003 16:13:22 CSchellenger Initial revision.
 *
 * Rev 1.20 Aug 27 2003 17:20:10 baa fix currency mask Resolution for 3348: If
 * entire price field is populated on Info Not Found, system crashes when
 * Tendered
 *
 * Rev 1.19 23 Jul 2003 00:47:52 baa add EYSbutton to factory
 *
 * Rev 1.18 Jul 22 2003 10:40:10 baa create a nonzero numeric field
 *
 * Rev 1.17 09 Jul 2003 23:23:46 baa modify screen to get customer name
 *
 * Rev 1.16 Apr 24 2003 14:44:30 bwf Added new create function to create
 * drivers license text field. Resolution for 2208: Space and Asterisk chars
 * are not allowed in a driver's license ID number
 *
 * Rev 1.15 Mar 13 2003 17:53:22 DCobb Generalized fitToField parameter of
 * createConstrainedTextAreaFieldPane and moved to UIFactory. Resolution for
 * POS SCR-1753: POS 6.0 Alterations Package
 *
 * Rev 1.14 Mar 13 2003 17:04:30 baa cleanup Resolution for POS SCR-1553:
 * cleanup dead code
 *
 * Rev 1.13 Mar 10 2003 09:06:00 baa code review changes for I18n Resolution
 * for POS SCR-1740: Code base Conversions
 *
 * Rev 1.12 Sep 20 2002 18:03:18 baa country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.11 Sep 18 2002 17:15:34 baa country/state changes Resolution for POS
 * SCR-1740: Code base Conversions
 *
 * Rev 1.10 Sep 10 2002 17:49:26 baa add password field Resolution for POS
 * SCR-1810: Adding pasword validating fields
 *
 * Rev 1.9 Sep 03 2002 16:08:06 baa externalize domain constants and parameter
 * values Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.8 Aug 15 2002 17:55:58 baa apply foundation updates to UISubsystem
 *
 * Resolution for POS SCR-1769: 5.2 UI defects resulting from change to java
 * 1.4
 *
 * Rev 1.7 Aug 14 2002 18:19:12 baa format currency Resolution for POS
 * SCR-1740: Code base Conversions
 *
 * Rev 1.6 Aug 08 2002 08:50:36 baa fix getCurrencyDigits method Resolution for
 * POS SCR-1740: Code base Conversions
 *
 * Rev 1.4 Jul 15 2002 10:52:18 baa remove 1.4 method Resolution for POS
 * SCR-1740: Code base Conversions
 *
 * Rev 1.3 Jul 05 2002 17:58:54 baa code conversion and reduce number of color
 * settings Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.2 Jun 21 2002 18:27:04 baa externalize parameter names, start
 * formatting currency base on locale Resolution for POS SCR-1624: Localization
 * Support
 *
 * Rev 1.1 24 May 2002 18:54:44 vxs Removed unncessary concatenations from log
 * statements. Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 * Rev 1.0 Apr 29 2002 14:45:30 msg Initial revision.
 *
 * Rev 1.2 16 Apr 2002 16:42:18 baa paint disable background for square border
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * Rev 1.1 10 Apr 2002 13:59:38 baa make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * Rev 1.0 Mar 18 2002 11:58:40 msg Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.plaf.UIFactoryIfc;
import oracle.retail.stores.gui.URLLabel;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AlphaNumericPasswordField;
import oracle.retail.stores.pos.ui.beans.AlphaNumericPlusTextField;
import oracle.retail.stores.pos.ui.beans.AlphaNumericTextField;
import oracle.retail.stores.pos.ui.beans.BackgroundHighlightFocusListener;
import oracle.retail.stores.pos.ui.beans.BaseBeanAdapter;
import oracle.retail.stores.pos.ui.beans.ConstrainedPasswordField;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextAreaField;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.CurrencyTextField;
import oracle.retail.stores.pos.ui.beans.DecimalTextField;
import oracle.retail.stores.pos.ui.beans.DriversLicenseTextField;
import oracle.retail.stores.pos.ui.beans.EYSButton;
import oracle.retail.stores.pos.ui.beans.EYSDateField;
import oracle.retail.stores.pos.ui.beans.EYSTimeField;
import oracle.retail.stores.pos.ui.beans.GovernmentIdField;
import oracle.retail.stores.pos.ui.beans.IntegerTextField;
import oracle.retail.stores.pos.ui.beans.MaskableNumericByteTextField;
import oracle.retail.stores.pos.ui.beans.NaPhoneNumField;
import oracle.retail.stores.pos.ui.beans.NonZeroDecimalTextField;
import oracle.retail.stores.pos.ui.beans.NumericByteTextField;
import oracle.retail.stores.pos.ui.beans.NumericDecimalTextField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.PercentageTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingFormattedTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingList;
import oracle.retail.stores.pos.ui.beans.YesNoComboBox;
import oracle.retail.stores.pos.ui.plaf.eys.EYSBorderFactory;

import org.apache.log4j.Logger;

/**
 * Factory class for creation and configuration of ui components. This default
 * version creates standard JFC components. Subclasses can extend this object
 * to create custom components.
 *
 * @see oracle.retail.stores.pos.ui.POSUIFactory
 */
public class UIFactory implements UIFactoryIfc
{
    /** revision number supplied by PVCS * */
    public static final String revisionNumber = "$Revision: /main/40 $";

    protected static final Logger logger = Logger.getLogger(UIFactory.class);

    /** static instance of the factory */
    private static UIFactory instance = null;

    /** UI Properties for all Languages */
    protected Hashtable <Locale, Properties> uiPropertiesForAllLocales = new Hashtable<Locale, Properties> ();

    /** currency properties */
    protected int currencyDigits = 0;

    /** Minimum Size for Validating Fields */
    protected static final int DEFAULT_MINIMUM_SIZE = 10;
    protected int minimumFieldSize = 0;

    /** Look and Feel File **/
    protected String lafFile = null;

    /**
     * Constructor that sets singleton object.
     */
    public UIFactory()
    {
        instance = this;

    }

    /**
     * Returns an instance of the specific application ui factory.
     *
     * @return the ui factory configured by the subsystem
     */
    public static UIFactory getInstance()
    {
        if (instance == null)
        {
            instance = new UIFactory();
        }
        return instance;
    }

    /**
     * Configures a swing component based on values from the UI defaults.
     *
     * @param component the component to configure
     * @param prefix the identifier to use in the default lookup
     */
    public void configureUIComponent(JComponent component, String prefix)
    {
        // if we have a prefix, get the values from the UIManager
        if (prefix != null)
        {
            component.putClientProperty("prefix", prefix);
            Border border = UIManager.getBorder(prefix + ".border");
            Color background = UIManager.getColor(prefix + ".background");
            Color foreground = UIManager.getColor(prefix + ".foreground");
            Font font = UIManager.getFont(prefix + ".font");
            String opaque = UIManager.getString(prefix + ".opaque");
            String onFocus = UIManager.getString(prefix + ".onFocus");

            // if each value is not null, set it on the component
            if (border != null)
            {
                component.setBorder(border);
                if (component instanceof JButton
                    && border instanceof EYSBorderFactory.RoundedBorder)
                {
                    JButton btn = (JButton) component;
                    btn.setContentAreaFilled(false);
                    component = btn;
                }
                if(component instanceof JTabbedPane)
                {
                    Component[] subComponents = component.getComponents();
                    for(Component subComponent : subComponents)
                    {
                        if(subComponent instanceof BaseBeanAdapter)
                        {
                            configureUIComponent((JComponent)subComponent, "BaseBean");
                        }
                    }
                }
            }
            if (background != null)
                component.setBackground(background);

            if (foreground != null)
                component.setForeground(foreground);

            if (font != null)
                component.setFont(font);

            if (opaque != null)
                component.setOpaque(UIUtilities.getBooleanValue(opaque));
            
            if (onFocus != null && UIUtilities.getBooleanValue(onFocus))
              component.addFocusListener(BackgroundHighlightFocusListener.getFocusListner());
        }
    }

    /**
     * Creates and configures an alphanumeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field, maximum number of input characters
     * @param columns the width of the display field
     */
    public AlphaNumericTextField createAlphaNumericField(
        String name,
        String minLength,
        String maxLength,
        String columns)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        AlphaNumericTextField field = new AlphaNumericTextField("", min, max);

        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);
        return field;
    }

    /**
     * Creates and configures an alphanumeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field, maximum number of input characters
     * @param columns the width of the display field
     */
    public AlphaNumericTextField createAlphaNumericField(
        String name,
        String minLength,
        String maxLength,
        String columns,
        boolean doubleByteCharsAllowed)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        AlphaNumericTextField field = new AlphaNumericTextField("", min, max, doubleByteCharsAllowed);

        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);
        return field;
    }

    /**
     * Creates and configures an alphanumeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public AlphaNumericTextField createAlphaNumericField(
        String name,
        String minLength,
        String maxLength)
    {
        return createAlphaNumericField (name, minLength, maxLength, maxLength);
    }
    
    /**
     * Creates and configures an alphanumeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param doubleByteCharsAllowed flag to indicate if double byte chars are allowed as input
     */
    public AlphaNumericTextField createAlphaNumericField(
        String name,
        String minLength,
        String maxLength,
        boolean doubleByteCharsAllowed)
    {
        return createAlphaNumericField (name, minLength, maxLength, maxLength, doubleByteCharsAllowed);
    }    
   
    
    /**
     * Creates and configures an alphanumeric hyphen text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param doubleByteCharsAllowed flag to indicate if double byte chars are allowed as input
     * @param allowableCharacters all the allowable characters
     */
    public AlphaNumericPlusTextField createAlphaNumericPlusField(
        String name,
        String minLength,
        String maxLength,
        boolean doubleByteCharsAllowed,
        char... allowableCharacters)
    {
        return createAlphaNumericPlusField(name, minLength, maxLength, maxLength, doubleByteCharsAllowed, allowableCharacters);
    } 
    
    /**
     * Creates and configures an alphanumeric hyphen text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param doubleByteCharsAllowed flag to indicate if double byte chars are allowed as input
     * @param allowableCharacters all the allowable characters
     */
    public AlphaNumericPlusTextField createAlphaNumericPlusField(
        String name,
        String minLength,
        String maxLength,
        boolean doubleByteCharsAllowed,
        String allowableCharacters)
    {
        char[] chars = allowableCharacters.toCharArray();
        return createAlphaNumericPlusField(name, minLength, maxLength, maxLength, doubleByteCharsAllowed, chars);
    }
    
    /**
     * Creates and configures an alphanumeric hyphen text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field, maximum number of input characters
     * @param columns the width of the display field
     * @param allowableCharacters all the allowable characters
     */
    public AlphaNumericPlusTextField createAlphaNumericPlusField(
        String name,
        String minLength,
        String maxLength,
        String columns,
        boolean doubleByteCharsAllowed,
        char... allowableCharacters)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        AlphaNumericPlusTextField field = new AlphaNumericPlusTextField("", min, max, doubleByteCharsAllowed, allowableCharacters);

        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);
        return field;
    }      

    /**
     * Creates and configures a drivers license text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     */
    public DriversLicenseTextField createDriversLicenseField(
        String name,
        String minLength,
        String maxLength,
        String columns)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        DriversLicenseTextField field =
            new DriversLicenseTextField("", min, max);

        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);
        return field;
    }

    /**
     * Creates and configures a drivers license text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     * @param doubleByteCharsAllowed
     *             flag to indicate if double byte chars are allowed
     */
    public DriversLicenseTextField createDriversLicenseField(
        String name,
        String minLength,
        String maxLength,
        String columns,
        boolean doubleByteCharsAllowed)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        DriversLicenseTextField field =
            new DriversLicenseTextField("", min, max, doubleByteCharsAllowed);

        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, VALIDATING_FIELD);
        return field;
    }

    /**
     * Creates and configures a drivers license text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public DriversLicenseTextField createDriversLicenseField(
        String name,
        String minLength,
        String maxLength)
    {
        return createDriversLicenseField (name, minLength, maxLength, maxLength);
    }

    /**
     * Creates and configures a drivers license text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param doubleByteCharsAllowed
     */
    public DriversLicenseTextField createDriversLicenseField(
        String name,
        String minLength,
        String maxLength,
        boolean doubleByteCharsAllowed)
    {
        return createDriversLicenseField (name, minLength, maxLength, maxLength, doubleByteCharsAllowed);
    }

    /**
     * Creates and configures an {@link EYSButton}.
     *
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @param traversable indicates if button is focustraversable
     * @return a configures button
     */
    public EYSButton createEYSButton(
        String label,
        Icon icon,
        String prefix,
        boolean traversable)
    {
        EYSButton button = new EYSButton(label, icon, traversable);
        configureButton(button, label, prefix);
        return button;
    }

    /**
     * Creates and configures a {@link JButton}.
     *
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public JButton createJButton(String label, Icon icon, String prefix)
    {
        JButton button = new JButton(label, icon);
        configureButton(button, label, prefix);
        return button;
    }

    /**
     * Creates and configures a {@link JButton}.
     *
     * @param name the name of the widget.
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public JButton createJButton(String name, String label, Icon icon, String prefix)
    {
        JButton button = new JButton(label, icon);
        configureButton(button, name, prefix);
        return button;
    }

    /**
     * Creates and configures a {@link EYSButton}.
     *
     * @param name the name of the widget.
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public EYSButton createButton(String name, String label, Icon icon, String prefix)
    {
        EYSButton button = new EYSButton(label, icon);
        configureButton(button, name, prefix);
        return button;
    }

    /**
     * Creates and configures a {@link JToggleButton}.
     *
     * @param name the name of the widget.
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public JToggleButton createToggleButton(String name, String label, Icon icon, String prefix)
    {
        JToggleButton button = new JToggleButton(label, icon);
        configureButton(button, name, prefix);
        return button;
    }

    /**
     * Creates and configures an {@link AbstractButton}.
     *
     * @param name the name of the widget.
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    protected void configureButton(AbstractButton button, String name, String prefix)
    {
        configureUIComponent(button, prefix);

        button.setName(name);
        button.setFocusTraversalKeysEnabled(false);
        button.setFocusable(false);
        if (button instanceof EYSButton)
        {
            EYSButton eysButton = (EYSButton)button;
            eysButton.setImageUp(getImage(prefix + ".imageUp", eysButton));
            eysButton.setImageDown(getImage(prefix + ".imageDown", eysButton));
            eysButton.setImageDisabled(getImage(prefix + ".imageDisabled", eysButton));
        }
    }

    /**
     * Creates and configures a constrained text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public ConstrainedTextField createConstrainedField(
        String name,
        String minLength,
        String maxLength)
    {
        return createConstrainedField(name, minLength, maxLength, maxLength);
    }

    /**
     * Creates and configures a constrained text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param doubleByteCharsAllowed flag to indicate if double byte chars are allowed
     */
    public ConstrainedTextField createConstrainedField(
        String name,
        String minLength,
        String maxLength,
        boolean doubleByteCharsAllowed)
    {
        return createConstrainedField(name, minLength, maxLength, maxLength, doubleByteCharsAllowed);
    }

    /**
     * Creates and configures a constrained text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     */
    public ConstrainedTextField createConstrainedField(
        String name,
        String minLength,
        String maxLength,
        String columns)
    {
        return createConstrainedField(name, minLength, maxLength, columns, true);
    }

    /**
     * Creates and configures a constrained text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     * @param doubleByteCharsAllowed flag to indicate if double byte chars are allowed
     */
    public ConstrainedTextField createConstrainedField(
        String name,
        String minLength,
        String maxLength,
        String columns,
        boolean doubleByteCharsAllowed)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        ConstrainedTextField field = new ConstrainedTextField("", min, max, doubleByteCharsAllowed);

        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a search panel with a search field and icon button
     * 
     * @param name the panel name
     * @param field the search field
     * @param button the icon button
     * @return the search panel
     */
    public ConstrainedTextField createSearchField(
            String name,
            String minLength,
            String maxLength,
            String columns,
            boolean doubleByteCharsAllowed,
            ActionListener l)
    {
        ConstrainedTextField field = createConstrainedField(name, minLength, maxLength, columns);
        field.addSearchActionListener(l);
        return field;
    }  

    /**
     * Creates and configures a validating formatted text field.
     *
     * @param name the name associated with the field
     */
    public ValidatingFormattedTextField createValidatingFormattedTextField(String name, String pattern, String maxlength, String columns)
    {
        return (createValidatingFormattedTextField (name, pattern, false, maxlength, columns));
    }

    /**
     * Creates and configures a validating formatted text field.
     *
     * @param name the name associated with the field
     */
    public ValidatingFormattedTextField createValidatingFormattedTextField(String name, String pattern, boolean containsLiterals, String maxlength, String columns)
    {
        int cols = Integer.parseInt(columns);
        ValidatingFormattedTextField field = new ValidatingFormattedTextField(pattern, containsLiterals, maxlength);
        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a constrained text area field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns number of columns
     * @param wrapStyle word wrap style
     * @param lineWrap line wrap flag
     */
    public ConstrainedTextAreaField createConstrainedTextAreaField(
        String name,
        String minLength,
        String maxLength,
        String columns,
        String wrapStyle,
        String lineWrap)
    {
        ConstrainedTextAreaField field = new ConstrainedTextAreaField();

        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        field.setWrapStyleWord(UIUtilities.getBooleanValue(wrapStyle));
        field.setLineWrap(UIUtilities.getBooleanValue(lineWrap));
        field.setName(name);
        field.setMinLength(min);
        field.setMaxLength(max);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, TEXT_AREA_FIELD);
        return field;
    }

    /**
     * Creates and configures a constrained text area field in scroll pane.
     * SI SCR-5992, release 7.0.0
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns number of columns
     * @param wrapStyle word wrap style
     * @param lineWrap line wrap flag
     * @param verticalScrollPolicy scroll bar policy
     * @param horizontalScrollPolicy scroll bar policy
     * @return A new JScrollPane
     */
    public JScrollPane createConstrainedTextAreaFieldPane(
        String name,
        String minLength,
        String maxLength,
        String columns,
        String wrapStyle,
        String lineWrap,
        String verticalScrollPolicy,
        String horizontalScrollPolicy)
    {
        return createConstrainedTextAreaFieldPane(
            name,
            minLength,
            maxLength,
            columns,
            wrapStyle,
            lineWrap,
            verticalScrollPolicy,
            verticalScrollPolicy,
            Boolean.toString(false));
    }

    /**
     * Creates and configures a constrained text area field in scroll pane.
     * SI SCR-5992, release 7.0.0
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns number of columns
     * @param wrapStyle word wrap style
     * @param lineWrap line wrap flag
     * @param verticalScrollPolicy scroll bar policy
     * @param horizontalScrollPolicy scroll bar policy
     * @param fitToField The Fit To Field setting
     * @return A new JScrollPane
     */
    public JScrollPane createConstrainedTextAreaFieldPane(
        String name,
        String minLength,
        String maxLength,
        String columns,
        String wrapStyle,
        String lineWrap,
        String verticalScrollPolicy,
        String horizontalScrollPolicy,
        String fitToField)
    {
        int verticalScrollPolicyInt = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
        int horizontalScrollPolicyInt = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;

        if (UIUtilities.VERTICAL_SCROLLBAR_NEVER.equals(verticalScrollPolicy))
        {
            verticalScrollPolicyInt = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
        }
        else if (UIUtilities.VERTICAL_SCROLLBAR_ALWAYS.equals(verticalScrollPolicy))
        {
            verticalScrollPolicyInt = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
        }

        if (UIUtilities.HORIZONTAL_SCROLLBAR_NEVER.equals(horizontalScrollPolicy))
        {
            horizontalScrollPolicyInt = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        }
        else if (UIUtilities.HORIZONTAL_SCROLLBAR_ALWAYS.equals(horizontalScrollPolicy))
        {
            horizontalScrollPolicyInt = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS;
        }

        return createConstrainedTextAreaFieldPane(
            name,
            minLength,
            maxLength,
            columns,
            wrapStyle,
            lineWrap,
            verticalScrollPolicyInt,
            horizontalScrollPolicyInt,
            Boolean.valueOf(fitToField).booleanValue());
    }

    /**
     * Creates and configures a constrained text area field in scroll pane.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns number of columns
     * @param wrapStyle word wrap style
     * @param lineWrap line wrap flag
     * @param vertical scroll bar policy
     * @param horizontal scroll bar policy
     */
    public JScrollPane createConstrainedTextAreaFieldPane(
        String name,
        String minLength,
        String maxLength,
        String columns,
        String wrapStyle,
        String lineWrap,
        int verticalScrollPolicy,
        int horizontalScrollPolicy)
    {
        return createConstrainedTextAreaFieldPane(
            name,
            minLength,
            maxLength,
            columns,
            wrapStyle,
            lineWrap,
            verticalScrollPolicy,
            horizontalScrollPolicy,
            false);
    }

    /**
     * Creates and configures a constrained text area field in scroll pane. If
     * the fit to field parameter is specified, the size of the pane is set to
     * fit around the field size, otherwise it is set to the size of the
     * constrained Text Area Pane as specified in the properties.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns number of columns
     * @param wrapStyle word wrap style
     * @param lineWrap line wrap flag
     * @param vertical scroll bar policy
     * @param horizontal scroll bar policy
     * @param fit to field
     */
    public JScrollPane createConstrainedTextAreaFieldPane(
        String name,
        String minLength,
        String maxLength,
        String columns,
        String wrapStyle,
        String lineWrap,
        int verticalScrollPolicy,
        int horizontalScrollPolicy,
        boolean fitToField)
    {
        ConstrainedTextAreaField field =
            createConstrainedTextAreaField(
                name,
                minLength,
                maxLength,
                columns,
                wrapStyle,
                lineWrap);

        JScrollPane pane = new JScrollPane();

        if (fitToField)
        { // determine pane size to fit field
            Dimension fieldSize = field.getPreferredScrollableViewportSize();
            int fieldWidth = new Double(fieldSize.getWidth()).intValue();
            int fieldHeight = new Double(fieldSize.getHeight()).intValue();
            Insets borderInsets = field.getBorder().getBorderInsets(field);
            int paneWidth = fieldWidth;
            int paneHeight = fieldHeight;

            if (!(verticalScrollPolicy
                == JScrollPane.VERTICAL_SCROLLBAR_NEVER))
            {
                // add width for a vertical scrollbar
                int verticalScrollBarWidth =
                    pane.getVerticalScrollBar().getWidth();
                if (verticalScrollBarWidth == 0)
                {
                    verticalScrollBarWidth =
                        new Double(
                            pane
                                .getVerticalScrollBar()
                                .getPreferredSize()
                                .getWidth())
                            .intValue();
                }
                paneWidth += verticalScrollBarWidth;
                // adjust height for border
                int borderHeight = borderInsets.top + borderInsets.bottom;
                paneHeight += borderHeight;
            }

            if (!(horizontalScrollPolicy
                == JScrollPane.HORIZONTAL_SCROLLBAR_NEVER))
            {
                // add height for a horizontal scrollbar
                int horizontalScrollBarHeight =
                    pane.getHorizontalScrollBar().getHeight();
                if (horizontalScrollBarHeight == 0)
                {
                    horizontalScrollBarHeight =
                        new Double(
                            pane
                                .getHorizontalScrollBar()
                                .getPreferredSize()
                                .getHeight())
                            .intValue();
                }
                paneHeight += horizontalScrollBarHeight;
                //adjust width for border
                int borderWidth = borderInsets.left + borderInsets.right;
                paneWidth += borderWidth;
            }
            Dimension paneDim = new Dimension(paneWidth, paneHeight);
            pane.setPreferredSize(paneDim);
        }
        else
        { // get pane size from properties
            Dimension dim = getDimension("constrainedTextAreaPane");
            pane.setPreferredSize(dim);
        }

        pane.setVerticalScrollBarPolicy(verticalScrollPolicy);
        pane.setHorizontalScrollBarPolicy(horizontalScrollPolicy);
        pane.setViewportView(field);
        pane.setBorder(EYSBorderFactory.getValidatingBorder());

        // Add tab key to list of traversal keys
        Set<AWTKeyStroke> fowardKeys =
            pane.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> backwardKeys =
            pane.getFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);

        field.setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
            fowardKeys);
        field.setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
            backwardKeys);
        return pane;
    }

    /**
     * Creates and configures constrained password field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     */
    public ConstrainedPasswordField createPasswordField(
        String name,
        String minLength,
        String maxLength,
        String columns)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        ConstrainedPasswordField field =
            new ConstrainedPasswordField(name, min, max);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures constrained password field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public ConstrainedPasswordField createPasswordField(
        String name,
        String minLength,
        String maxLength)
    {
        return createPasswordField(name, minLength, maxLength, maxLength);
    }


    /**
     * Creates and configures alphanumeric password field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     */
    public AlphaNumericPasswordField createAlphaNumericPasswordField(
        String name,
        String minLength,
        String maxLength,
        String columns)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int cols = Integer.parseInt(columns);

        AlphaNumericPasswordField field =
            new AlphaNumericPasswordField(name, min, max);
        field.setColumns(getAdjustedColumns(cols));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures alphanumeric password field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public AlphaNumericPasswordField createAlphaNumericPasswordField(
        String name,
        String minLength,
        String maxLength)
    {
        return createAlphaNumericPasswordField (name, minLength, maxLength, maxLength);
    }

    /**
     * Creates and configures a currency text field.
     *
     * @param name the name associated with the field
     * @param zeroAllowed true if zero can be entered as a value
     * @param negativeAllowed true if negative currencies can be entered
     * @param emptyAllowed true if the field can be left blank
     */
    public CurrencyTextField createCurrencyField(
        String name, int columns,
        String zeroAllowed,
        String negativeAllowed,
        String emptyAllowed)
    {
        CurrencyTextField field = createCurrencyField(name);
        field.setZeroAllowed(UIUtilities.getBooleanValue(zeroAllowed));
        field.setNegativeAllowed(UIUtilities.getBooleanValue(negativeAllowed));
        field.setEmptyAllowed(UIUtilities.getBooleanValue(emptyAllowed));
        field.setColumns(columns);
        return field;
    }
    
    /**
     * Creates and configures a currency text field.
     *
     * @param name the name associated with the field
     * @param zeroAllowed true if zero can be entered as a value
     * @param negativeAllowed true if negative currencies can be entered
     * @param emptyAllowed true if the field can be left blank
     */
    public CurrencyTextField createCurrencyField(
        String name,
        String zeroAllowed,
        String negativeAllowed,
        String emptyAllowed)
    {
        CurrencyTextField field = createCurrencyField(name);
        field.setZeroAllowed(UIUtilities.getBooleanValue(zeroAllowed));
        field.setNegativeAllowed(UIUtilities.getBooleanValue(negativeAllowed));
        field.setEmptyAllowed(UIUtilities.getBooleanValue(emptyAllowed));

        return field;
    }
    /**
     * Creates and configures a currency text field.
     *
     * @param name the name associated with the field
     * @param zeroAllowed true if zero can be entered as a value
     * @param negativeAllowed true if negative currencies can be entered
     * @param emptyAllowed true if the field can be left blank
     */
    public CurrencyTextField createCurrencyField(String name)
    {
        // Retrieve fractionDigits for this currency
        int fractionDigits = 2;
        try
        {
           fractionDigits = DomainGateway.getBaseCurrencyType().getScale();
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database or server may be offline, using default number of fraction digits", e);
        }

        int totalDigits = getCurrencyDigits();

        CurrencyTextField field =
            new CurrencyTextField("0.00", totalDigits, fractionDigits);

        field.setName(name);
        field.setColumns(totalDigits);
        field.setMinimumSize(getMinimumFieldSizeProperty());

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a decimal text field.
     *
     * @param name the name associated with the field
     * @param maxLen the maximum length of the text field
     * @param negativeAllowed true if negative currencies can be entered
     * @param emptyAllowed true if the field can be left blank
     */
    public DecimalTextField createDecimalField(
        String name,
        String maxLen,
        String negativeAllowed,
        String emptyAllowed)
    {
        int max = Integer.parseInt(maxLen);

        DecimalTextField field = new DecimalTextField("0.00", max);
        field.setName(name);
        field.setNegativeAllowed(UIUtilities.getBooleanValue(negativeAllowed));
        field.setEmptyAllowed(UIUtilities.getBooleanValue(emptyAllowed));
        field.setColumns(getAdjustedColumns(max));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a decimal text field.
     *
     * @param name the name associated with the field
     * @param emptyAllowed true if the field can be left blank
     * @param minValue the minimum percentage value (defaults to 0%)
     * @param maxValue the maximum percentage value (defaults to 100%)
     */
    public PercentageTextField createPercentField(
        String name,
        String emptyAllowed,
        String minValue,
        String maxValue)
    {
        int min = Integer.parseInt(minValue);
        int max = Integer.parseInt(maxValue);

        PercentageTextField field = new PercentageTextField("", min, max);
        field.setName(name);
        field.setNegativeAllowed(false);
        field.setEmptyAllowed(UIUtilities.getBooleanValue(emptyAllowed));
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a display text field.
     *
     * @param name the name associated with the field
     */
    public JLabel createDisplayField(String name)
    {
        JLabel field = new JLabel();

        // make sure the label never gets focus
        field.setFocusable(false);

        field.setName(name);

        configureUIComponent(field, DISPLAY_FIELD);

        return field;
    }

    /**
     * Creates and configures a display text field.
     *
     * @param name the name associated with the field
     */
    public JLabel createTextLabel(String name)
    {
        JLabel field = new JLabel();

        // make sure the label never gets focus
        field.setFocusable(false);

        field.setName(name);

        configureUIComponent(field, TEXT_LABEL);

        return field;
    }    
    /**
     * Creates and configures a divider.
     */
    public EYSDivider createDivider()
    {
        EYSDivider divider = new EYSDivider();
        configureUIComponent(divider, "Divider");

        return divider;
    }

    /**
     * Creates and configures an EYS date field.
     * 
     * @param name the name associated with the field
     */
    public EYSDateField createEYSDateField(String name)
    {
        EYSDateField field = new EYSDateField();
        field.setName(name);
        field.setColumns(getAdjustedColumns(10));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures an EYS date field.
     * 
     * @param name the name associated with the field
     */
    public EYSTimeField createEYSTimeField(String name)
    {
        EYSTimeField field = new EYSTimeField();

        field.setName(name);
        field.setColumns(getAdjustedColumns(5));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a JLabel. Here for backward compatibility only:
     * This deprecated method passes the text or the icon identifier as the name
     * to the new implementation of createLabel.
     * 
     * @param text the label text
     * @param icon the label icon
     * @param prefix the lookup identifier to use in configuration
     * @return a configured label
     * @deprecated see createLabel(String name, String text, Icon icon, String
     *             prefix)
     */
    public JLabel createLabel(String text, Icon icon, String prefix)
    {
        String name = text;
        if (name == null && icon != null)
        {
            name = icon.toString();
        }

        return createLabel(name, text, icon, prefix, false);
    }

    /**
     * Creates and configures a JLabel.
     * 
     * @param name the widget's name
     * @param text the label text
     * @param icon the label icon
     * @param prefix the lookup identifier to use in configuration
     * @return a configured label
     */
    public JLabel createLabel(String name, String text, Icon icon, String prefix)
    {
        return createLabel(name, text, icon, prefix, false);
    }

    /**
     * Creates and configures a URLLabel and adds the specified ActionListener
     * to it to respond to clicks.
     * 
     * @param name the widget's name
     * @param text the label text
     * @param icon the label icon
     * @param prefix the lookup identifier to use in configuration
     * @return a configured label
     */
    public JLabel createURLLabel(String name, String text, Icon icon, String prefix, ActionListener l)
    {
        URLLabel urlLabel = (URLLabel)createLabel(name, text, icon, prefix, true);
        if (urlLabel != null && l != null)
        {
            urlLabel.addActionListener(l);
        }
        return urlLabel;
    }

    /**
     * Creates and configures a JLabel.
     * 
     * @param name the widget's name
     * @param text the label text
     * @param icon the label icon
     * @param prefix the lookup identifier to use in configuration
     * @param hyperlink if true, this method creates a {@link URLLabel}.
     * @return a configured label
     */
    public JLabel createLabel(String name, String text, Icon icon, String prefix, boolean hyperlink)
    {
        // create label with given text and icon,
        // default alignment is left
        JLabel label = (hyperlink)? new URLLabel(text, icon, JLabel.LEFT) : new JLabel(text, icon, JLabel.LEFT);

        // Check that the requested label was requested with a given name.
        // Labels should be given a name so that automated testing
        // can easily examine the GUI.
        if (name == null || name.trim().length() == 0) {
            // Create a throwable so we can get a stack trace to log, but we won't throw it.
            Throwable t = new Throwable("label requested with no name");
            // see if we can come up with a name from the call stack
            try
            {
                StackTraceElement[] stacktrace = t.getStackTrace();
                int i = 1;
                // might have been our deprecated method, dig further.
                if (stacktrace[1].getMethodName().equals("createLabel")) {
                    i = 2;
                }
                name = stacktrace[i].toString() + "DidNotNameThisLabel";
            }
            catch (Exception ex)
            {
                // Well, that didn't work for some reason.
                // just give up, eat the exception, and don't tell anybody anything.
            }
            logger.warn("UI Objects with no names impedes automated testing.  Please name this label.  prefix " + prefix + ", text " + text + ", icon " + icon,t);
        } // end if name == null or empty
        label.setName(name);

        // make sure the label never gets focus
        label.setFocusable(false);

        // if there is no property prefix, just use
        // the label default
        if (prefix == null)
        {
            prefix = "Label";
        }
        // configure the label
        configureUIComponent(label, prefix);

        int align = UIManager.getInt(prefix + ".alignment");
        label.setHorizontalAlignment(align);

        return label;
    }

    /**
     * Creates and configures a non-zero decimal text field.
     *
     * @param name the name associated with the field
     * @param maxLen the maximum length of the text field
     */
    public NonZeroDecimalTextField createNonZeroDecimalField(
        String name,
        String maxLen)
    {
        int max = Integer.parseInt(maxLen);

        NonZeroDecimalTextField field = new NonZeroDecimalTextField("", max, 2);
        field.setName(name);
        field.setColumns(getAdjustedColumns(max));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a text field that could use numeric or decimal
     * document
     * @param name the name associated with the field
     * @param maximum length of the field
     * @param zero allowed for value
     * @return NumericDecimalTextField numeric decimal text field instance
     */
    public NumericDecimalTextField createNumericDecimalField(String name,
                                                             int maxLength,
                                                             boolean zeroAllowed)
    {
        NumericDecimalTextField field =
          new NumericDecimalTextField("",
                                       maxLength,
                                       zeroAllowed);
        field.setName(name);
        configureUIComponent(field, VALIDATING_FIELD);
        return field;
    }
    

    /**
     * Creates and configures a Numeric byte text field. Uses the max
     * as the width of the display field.
     * @param name
     * @param minLength
     * @param maxLength
     * @return
     */
    public NumericByteTextField createNumericByteTextField(String name, String minLength, String maxLength)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        NumericByteTextField field = new NumericByteTextField("", min, max);
        field.setName(name);
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, "ValidatingField");
        return field;
    }

    /**
     * Creates and configures a maskable numeric byte text field. 
     * @param name
     * @param minLength
     * @param maxLength
     * @param columns
     * @return
     */
    public NumericByteTextField createNumericByteTextField(String name, String minLength, String maxLength, String columns)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int col = Integer.parseInt(columns);
        NumericByteTextField field = new NumericByteTextField("", min, max);
        field.setName(name);
        field.setColumns(getAdjustedColumns(col));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, "ValidatingField");
        return field;
    }

    /**
     * Creates and configures a maskable numeric byte text field. Uses the max
     * as the width of the display field.
     * 
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public MaskableNumericByteTextField createMaskableNumericByteField(String name, String minLength, String maxLength)
    {
        return createMaskableNumericByteField(name, minLength, maxLength, maxLength);
    }

    /**
     * Creates and configures a numeric byte text field.
     * 
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     */
    public MaskableNumericByteTextField createMaskableNumericByteField(String name, String minLength, String maxLength,
            String columns)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int col = Integer.parseInt(columns);

        MaskableNumericByteTextField field = new MaskableNumericByteTextField("", min, max);
        field.setName(name);
        field.setColumns(getAdjustedColumns(col));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a numeric byte text field.
     * 
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     */
    public MaskableNumericByteTextField createMaskedCardNumberField(String name,
            int minLength, int maxLength, int columns)
    {
        MaskableNumericByteTextField field = new MaskableNumericByteTextField("", minLength, maxLength);
        field.setName(name);
        field.setColumns(getAdjustedColumns(columns));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        field.setCardNumber(true);
        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a numeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public NumericTextField createNumericField(
        String name,
        String minLength,
        String maxLength)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);

        NumericTextField field = new NumericTextField("", min, max);
        field.setName(name);
        field.setColumns(getAdjustedColumns(max));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a numeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     * @param columns the width of the display field
     */
    public NumericTextField createNumericField(
        String name,
        String minLength,
        String maxLength,
        String columns)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);
        int col = Integer.parseInt(columns);

        NumericTextField field = new NumericTextField("", min, max);
        field.setName(name);
        field.setColumns(getAdjustedColumns(col));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a numeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public IntegerTextField createIntegerField(
            String name,
            String minLength,
            String maxLength)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);

        IntegerTextField field = new IntegerTextField("", min, max);
        field.setName(name);
        field.setColumns(getAdjustedColumns(max));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a non zero numeric text field.
     *
     * @param name the name associated with the field
     * @param minLength the mimimum length of the field
     * @param maxLength the maximum length of the field
     */
    public NumericTextField createNonZeroNumericField(
        String name,
        String minLength,
        String maxLength)
    {
        int min = Integer.parseInt(minLength);
        int max = Integer.parseInt(maxLength);

        NumericTextField field = new NumericTextField("", min, max, false);
        field.setName(name);
        field.setColumns(getAdjustedColumns(max));
        field.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Creates and configures a panel with a postal code field, a dash, and an
     * extended postal code field.
     *
     * @param post the postal code field
     * @param ext the extended code field
     * @return the postal panel
     */
    public JPanel createPostalPanel(
        JTextField post,
        JTextField ext,
        JLabel delim)
    {
        JPanel postalPanel = new JPanel(new GridBagLayout());
        postalPanel.setBackground(UIManager.getColor("beanBackground"));
        postalPanel.setOpaque(false);

        delim.setHorizontalAlignment(JLabel.CENTER);

        GridBagConstraints constraints = getConstraints("");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        postalPanel.add(post, constraints);
        postalPanel.add(delim, constraints);

        constraints.insets = new Insets(0, 10, 0, 0);
        postalPanel.add(ext, constraints);

        return postalPanel;
    }

    /**
     * Creates and configures a panel with a postal code field
     *
     * @param post the postal code field
     * @return the postal panel
     */
    public JPanel createPostalPanel(JTextField post)
    {
        JPanel postalPanel = new JPanel(new GridBagLayout());
        postalPanel.setBackground(UIManager.getColor("beanBackground"));
        postalPanel.setOpaque(false);

        GridBagConstraints constraints = getConstraints("");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        postalPanel.add(post, constraints);

        return postalPanel;
    }

    /**
     * Creates and configures a selection list and wraps it in a JScrollPane
     * for displaying.
     *
     * @param name the name associated with the component
     * @param size the size of the component. Can be null,
     *      {@link UIFactoryIfc#DIMENSION_SMALL} or {@link UIFactoryIfc#DIMENSION_LARGE}
     * @param emptyAllowed An empty selection allowed.  A true or false value.
     */
    public JScrollPane createSelectionList(String name, int paneWidth, int paneHeight, String emptyAllowed)
    {
        ValidatingList list = new ValidatingList();
        list.setName(name);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setEnabled(true);
        list.setModel(new POSListModel());
        list.setEmptyAllowed(Boolean.valueOf(emptyAllowed).booleanValue());
        configureUIComponent(list, "List");

        // create scroll pane and set dimension
        JScrollPane pane = new JScrollPane();
        Dimension dim = new Dimension(paneWidth, paneHeight);;
        pane.setPreferredSize(dim);
       
        pane.setViewportView(list);
        pane.setBorder(EYSBorderFactory.getValidatingBorder());
        return pane;
    }

    /**
     * Creates and configures a selection list and wraps it in a JScrollPane
     * for displaying.
     *
     * @param name the name associated with the component
     * @param size the size of the component. Can be null,
     *      {@link UIFactoryIfc#DIMENSION_SMALL} or {@link UIFactoryIfc#DIMENSION_LARGE}
     * @param emptyAllowed An empty selection allowed.  A true or false value.
     */
    public JScrollPane createSelectionList(String name, String size, String emptyAllowed)
    {
        ValidatingList list = new ValidatingList();
        list.setName(name);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setEnabled(true);
        list.setModel(new POSListModel());
        list.setEmptyAllowed(Boolean.valueOf(emptyAllowed).booleanValue());
        configureUIComponent(list, "List");

        // create scroll pane and set dimension
        JScrollPane pane = new JScrollPane();
        Dimension dim = null;
        if (size == null)
        {
            // do not set a preferred size
        }
        else if (size.equals(DIMENSION_SMALL))
        {
            dim = getDimension("smallSelectListDimension");
            pane.setPreferredSize(dim);
        }
        else if (size.equals(DIMENSION_LARGE))
        {
            dim = getDimension("selectionListDimension");
            pane.setPreferredSize(dim);
        }
        pane.setViewportView(list);
        pane.setBorder(EYSBorderFactory.getValidatingBorder());
        return pane;
    }

    
    /**
     * Creates and configures a selection list and wraps it in a JScrollPane
     * for displaying.
     *
     * @param name the name associated with the component
     * @param size the size of the component. Can be null,
     *      {@link UIFactoryIfc#DIMENSION_SMALL} or {@link UIFactoryIfc#DIMENSION_LARGE}
     */
    public JScrollPane createSelectionList(String name, String size)
    {
        return createSelectionList(name, size, "false");
    }

    /**
     * Creates and configures a standard JTextField.
     *
     * @param name the name associated with the component
     */
    public JTextField createTextField(String name)
    {
        return createTextField(name, 15);
    }

    /**
     * Creates and configures a standard JTextField.
     *
     * @param name the name associated with the component
     */
    public JTextField createTextField(String name, int cols)
    {
        JTextField field = new JTextField();

        field.setName(name);
        field.setColumns(getAdjustedColumns(cols));

        Dimension minimumFieldSizeDimension = new Dimension();
        minimumFieldSizeDimension.width = getAdjustedColumns(getMinimumFieldSizeProperty());
        minimumFieldSizeDimension.height = field.getPreferredSize().height;
        field.setMinimumSize(minimumFieldSizeDimension);

        configureUIComponent(field, VALIDATING_FIELD);

        return field;
    }

    /**
     * Reduce columns by the value defined in the uiProperties.columnWidthAdjustment
     * so that fields are not bigger than necessary.
     * The size of a column is determined by the with of the letter m
     *
     * @param name the name associated with the component
     */
    public int getAdjustedColumns(int numberOfColumns)
    {
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        return getAdjustedColumns(numberOfColumns, lcl);
    }

    /**
     * Reduce columns by the value defined in the uiProperties.columnWidthAdjustment
     * so that fields are not bigger than necessary.
     * The size of a column is determined by the with of the letter m
     *
     * @param name the name associated with the component
     * @param locale
     */
    public int getAdjustedColumns(int numberOfColumns, Locale lcl)
    {
        String propertyValue = getUIProperty("columnWidthAdjustment", lcl);
        if (Util.isEmpty (propertyValue))
        {
            propertyValue = "1.0";
        }

        Double columnWidthAdjustment = new Double(propertyValue);

        int minimumFieldSize = getMinimumFieldSizeProperty();

        if (numberOfColumns < minimumFieldSize)
        {
            numberOfColumns = minimumFieldSize;
        }

        Double offset = new Double(numberOfColumns * columnWidthAdjustment.doubleValue());
        int adjustedColumns = numberOfColumns - offset.intValue();

        return adjustedColumns;
    }

    /**
     * Creates and configures a validating combo box.
     *
     * @param name the name associated with the component
     */
    public ValidatingComboBox createValidatingComboBox(String name)
    {
        return createValidatingComboBox(name, "false", "-1");
    }

    /**
     * Creates and configures a validating combo box with the given empty
     * allowed parameter.
     *
     * @param name the name associated with the component
     * @param emptyAllowed true if empty selections are allowed, false if not
     * @return a validating combo box
     */
    public ValidatingComboBox createValidatingComboBox(
        String name,
        String emptyAllowed)
    {
        return createValidatingComboBox(name, emptyAllowed, "-1");
    }

    /**
     * Creates and configures a validating combo box with the given empty
     * allowed parameter.
     *
     * @param name the name associated with the component
     * @param emptyAllowed true if empty selections are allowed, false if not
     * @param columns the width of the display field
     * @return a validating combo box
     */
    public ValidatingComboBox createValidatingComboBox(
        String name,
        String emptyAllowed,
        String columns)
    {
        ValidatingComboBox box = new ValidatingComboBox();
        int cols = Integer.parseInt(columns);
        box.setName(name);
        box.setEmptyAllowed(UIUtilities.getBooleanValue(emptyAllowed));

        if (cols > 0)
        {
            box.setColumns(getAdjustedColumns(cols));
        }
        box.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));

        configureUIComponent(box, VALIDATING_FIELD);

        //this prevents screen garbage when painting the field's background
        box.setOpaque(false);

        return box;
    }

    /**
     * Creates and configures a YesNovalidating combo box.
     *
     * @param name the name associated with the component
     */
    public YesNoComboBox createYesNoComboBox(String name)
    {
        return createYesNoComboBox(name, -1);
    }

    /**
     * Creates and configures a YesNovalidating combo box.
     *
     * @param name the name associated with the component
     */
    public YesNoComboBox createYesNoComboBox(String name, int columns)
    {
        YesNoComboBox box = new YesNoComboBox();

        box.setName(name);
        box.setMinimumSize(getMinimumFieldSizeProperty());
        if (columns > 0)
        {
            box.setColumns(getAdjustedColumns(columns));
        }

        configureUIComponent(box, VALIDATING_FIELD);

        //this prevents screen garbage when painting the field's background
        box.setOpaque(false);


        return box;
    }

    /**
     * Creates a constraints object.
     *
     * @param prefix an identifying name for the constraint
     * @return a constraints object
     */
    public GridBagConstraints getConstraints(String prefix)
    {
        return new GridBagConstraints();
    }

    /**
     * Creates a dimension object from a property name. If the dimension object
     * cannot be located, an empty dimension (0,0) is returned.
     *
     * @param prefix an identifying name to use in lookups
     * @return an insets object
     */
    public Dimension getDimension(String prefix)
    {
        Dimension dimension = UIManager.getDimension(prefix);

        // if the dimension exists, return a clone
        if (dimension != null)
        {
            dimension = (Dimension) dimension.clone();
        }
        else
        {
            logger.warn(
                "Dimension - " + prefix + " - not found.");

            dimension = new Dimension(0, 0);
        }
        return dimension;
    }

    /**
     * Creates an insets object. If the inset object cannot be located, an
     * empty inset (0,0,0,0) is returned.
     *
     * @param prefix an identifying name to use in lookups
     * @return an insets object
     */
    public Insets getInsets(String prefix)
    {
        Insets insets = UIManager.getInsets(prefix);

        // if the insets exist, we return a clone
        if (insets != null)
        {
            insets = (Insets) insets.clone();
        }
        // if they don't exist, return a zero inset
        else
        {
            logger.warn(
                "Insets - " + prefix + " - not found.");

            insets = new Insets(0, 0, 0, 0);
        }
        return insets;
    }

    /**
     * Retrieve an image object from the {@link UIUtilities} that is keyed in
     * the properties by the specified resourceKey.
     *
     * @param resourceKey
     * @param c
     * @return
     */
    public Image getImage(String resourceKey, Component c)
    {
        String imageName = UIManager.getString(resourceKey);
        if (imageName != null && !imageName.contains("."))
        {
            imageName = UIManager.getString(imageName);
        }
        if (imageName != null)
        {
            if (imageName.contains(","))
            {
                StringTokenizer tokens = new StringTokenizer(imageName, ",");
                if (tokens.countTokens() == 4)
                {
                    imageName = tokens.nextToken();
                    float h = Float.parseFloat(tokens.nextToken());
                    float s = Float.parseFloat(tokens.nextToken());
                    float b = Float.parseFloat(tokens.nextToken());
                    return UIUtilities.getImage(imageName, c, new float[] { h, s, b }, true);
                }
            }
            return UIUtilities.getImage(imageName, c);
        }
        return null;
    }

    /**
     * Returns the # of currency digits allowed
     *
     * @return currency digits
     */
    public int getCurrencyDigits()
    {

        // Check to se if the currency value has been read already
        if (currencyDigits == 0)
        {
            Locale lcl = LocaleMap.getLocale(LocaleMap.DEFAULT);
            String digits = getUIProperty("currencyDigits", lcl);

            if (!Util.isEmpty(digits))
            {
                currencyDigits = (new Integer(digits)).intValue();
            }
            else
            {
                logger.warn(
                    "currencyDigits property not found. Set to default");
                // default value if not specified
                currencyDigits = 13; // Default format is 10,2
            }
        }
        return currencyDigits;
    }

    /**
     * Creates and configures a GovernmentIdField.
     *
     * @param name the name associated with the component
     * @param minLength Minimum Length
     * @param columns Number of Columns
     * @return A GovernmentIdField
     * @since 14.0
     */
    public GovernmentIdField createGovernmentIdField(
            String name,
            String minLength,
            String columns)
    {
        GovernmentIdField governmentIdField = new GovernmentIdField();

        governmentIdField.setName(name);
        governmentIdField.setMinLength(Integer.parseInt(minLength));
        governmentIdField.setColumns(Integer.parseInt(columns));
        governmentIdField.setMinimumSize(getMinimumFieldSizeProperty());
        configureUIComponent(governmentIdField, VALIDATING_FIELD);

        return governmentIdField;
    }
    /**
     * Creates and configures a NaPhoneNumField.
     * SI SCR-5992, release 7.0.0
     *
     * @param name the name associated with the component
     * @param columns Number of Columns
     * @param validateAreaCodeFirstDigit Validate Area Code First Digit setting
     * @param aphaAccepted Alpha Numeric Values Accepted setting
     * @return A NaPhoneNumField
     */
    public NaPhoneNumField createNaPhoneNumField(String name,
                                                 String columns,
                                                 String validateAreaCodeFirstDigit,
                                                 String aphaAccepted)
    {
        NaPhoneNumField naPhoneNumField = new NaPhoneNumField();

        naPhoneNumField.setName(name);
        naPhoneNumField.setColumns(getAdjustedColumns(Integer.parseInt(columns)));
        naPhoneNumField.setMinimumSize(getAdjustedColumns(getMinimumFieldSizeProperty()));
        naPhoneNumField.setValidateAreaCodeFirstDigit(Boolean.valueOf(validateAreaCodeFirstDigit).booleanValue());
        naPhoneNumField.setAlphaAccepted(Boolean.valueOf(aphaAccepted).booleanValue());

        return naPhoneNumField;
    }

    /**
     * Gets a property from the current look and feel properites file
     *
     * @param prop the name of the ui property
     * @param the locale
     */
    public String getUIProperty(String prop, Locale lcl)
    {
        Properties props = getUIProperties(lcl);
        return (props != null)? props.getProperty(prop) : null;
    }

    /**
     * When the Locale is set, this method loads all UI PLAF properties files
     * starting using the {@link #lafFile} variable. The base properties, the
     * language properties, the country properties and the variant properties
     * are all loaded and combind into one {@link Properties}.
     *
     * @param locale
     */
    public void setUIProperties(Locale lcl)
    {
        // load the base properties
        if(lafFile == null)
        {
            lafFile = "";
            logger.warn("Look and Feel file, lafFile, is null");
        }
        StringBuilder fullFileName = new StringBuilder(lafFile).append(".properties");
        if (logger.isDebugEnabled())
        {
            logger.debug("Loading UI Properties for " + fullFileName);
        }
        Properties uiProperties = UIUtilities.loadProperties(fullFileName.toString());
        // load the language properties
        if (lcl != null)
        {
            uiProperties = loadUIProperties(fullFileName, uiProperties, "_" + lcl.getLanguage());
            if (!Util.isEmpty(lcl.getCountry()))
            {
                uiProperties = loadUIProperties(fullFileName, uiProperties, "_" + lcl.getCountry());
            }
            if (!Util.isEmpty(lcl.getVariant()))
            {
                uiProperties = loadUIProperties(fullFileName, uiProperties, "_" + lcl.getVariant());
            }
            if (uiProperties != null)
            {
                Locale bestMatch = LocaleMap.getBestMatch(lcl);
                uiPropertiesForAllLocales.put(bestMatch, uiProperties);
            }
        }
    }

    /**
     * Loads properties specified by the <code>fullFileName</code> but altered by
     * the <code>localeString</code> and then loaded in the
     * <code>uiProperties</code> and returned.
     * 
     * @param fullFileName the file name that will be altered and search for.
     * @param uiProperties
     * @param localeString
     * @return
     */
    private Properties loadUIProperties(StringBuilder fullFileName, Properties uiProperties, String localeString)
    {
        if (localeString != null)
        {
            if (!localeString.startsWith("_"))
            {
                localeString = "_" + localeString;
            }
            fullFileName.insert(fullFileName.length() - 11, localeString);
            if (logger.isDebugEnabled())
            {
                logger.debug("Loading UI Properties for " + fullFileName);
            }
            Properties tempProperties = UIUtilities.loadProperties(fullFileName.toString());
            if (tempProperties != null)
            {
                if (uiProperties != null)
                    uiProperties.putAll(tempProperties);
                else
                    uiProperties = tempProperties;
            }
        }
        return uiProperties;
    }

    /**
     * Divider Class Place holder to create an empty divider
     */
    public class EYSDivider extends JLabel
    {
        private static final long serialVersionUID = 12345L;
    }

    /**
     * Retrieves the PVCS revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Returns the minimumFieldSize for Text Fields
     *
     * @return the minimum field size (width)
     */
    public int getMinimumFieldSizeProperty ()
    {

        // Check to se if the currency value has been read already
        if (minimumFieldSize == 0)
        {
            Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            String size = getUIProperty("minimumFieldSize", lcl);

            if (!Util.isEmpty(size))
            {
                minimumFieldSize = (new Integer(size)).intValue();
            }
            else
            {
                // set the default value if not specified
                logger.warn("minimumFieldSize property not found. Set to default: " + DEFAULT_MINIMUM_SIZE);
                minimumFieldSize = DEFAULT_MINIMUM_SIZE;
            }
        }
        return minimumFieldSize;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.gui.plaf.UIFactoryIfc#getUIProperties(java.util.Locale)
     */
    public Properties getUIProperties(Locale lcl)
    {
        Locale bestMatch = LocaleMap.getBestMatch(lcl);
        Properties properties = uiPropertiesForAllLocales.get(bestMatch);
        if (properties == null)
        {
            setUIProperties(lcl);
            properties = uiPropertiesForAllLocales.get (bestMatch);
        }
        return properties;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.gui.plaf.UIFactoryIfc#setLookAndFeelFileName(java.lang.String)
     */
    public void setLookAndFeelFileName(String lafFile)
    {
        this.lafFile = lafFile;
    }
}
