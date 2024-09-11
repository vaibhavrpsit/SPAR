/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnItemInfoBean.java /main/31 2014/03/28 11:01:31 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  03/26/14 - made SerialNumberField an AlphaNumericPlusTextField
 *                         to allow Hyphen to be added
 *    mchellap  03/19/13 - Disable validation for price field if its readonly
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    cgreene   08/29/12 - made item description wider because the 16char was
 *                         too short in customer demos
 *    rabhawsa  03/02/12 - RM i18n Changes added item condition code
 *    ohorne    02/22/11 - ItemNumber can be ItemID or PosItemID
 *    abhayg    08/26/10 - Serial Number needs to be displayed on the Return
 *                         Item Info screen For Serialized Item
 *    jswan     08/11/10 - Removed restocking fee field from this screen.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
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
 *    nkgautam  03/26/09 - Fix for screen getting meshed up when item
 *                         description is too long
 *    abondala  03/05/09 - ReasonCodes are retrieved from the database not from
 *                         the bundles.
 *    sgu       02/25/09 - fix max lenght for all item quantity fields
 *    glwang    01/29/09 - checked in after merge with trunk
 *    glwang    01/29/09 - set max length of user id as 10
 *    sgu       01/28/09 - fix return item info to display price in user locale
 *    sgu       01/28/09 - fix gift card balance to display in user locale
 *
 * ===========================================================================
 * $Log:
 *    12   360Commerce 1.11        2/4/2008 12:09:49 PM   Charles D. Baker CR
 *         29652 - Repaired ConstrainedTextField to permit insertString
 *         override of insertString in implementing classes. Code reviewed by
 *         Sandy Gu.
 *    11   360Commerce 1.10        1/30/2008 4:11:22 PM   Charles D. Baker CR
 *         29652 - Mofified to account for changes to NumericTextField
 *         trickling from changes to ConstrainedTextField. Includes removal of
 *          deprecated method calls and commented code. Code reviewed by Siva
 *         Papenini.
 *    12   I18N_P2    1.9.1.1     1/7/2008 3:52:27 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    11   I18N_P2    1.9.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *         alphanumerice fields for I18N purpose
 *    10   360Commerce 1.9         11/15/2007 11:19:26 AM Christian Greene
 *         Belize merge - Increased allowable item size to 10 chars.
 *    9    360Commerce 1.8         8/8/2007 5:49:41 PM    Michael P. Barnett
 *         Specify max length of NumericDecimal field.
 *    8    360Commerce 1.7         7/13/2007 2:04:23 PM   Ashok.Mondal    CR
 *         27666 :Correcting price format on the return item info screen.
 *    7    360Commerce 1.6         7/28/2006 6:07:15 PM   Brett J. Larsen CR
 *         4530 - default reason code fix
 *         v7x->360Commerce merge
 *    6    360Commerce 1.5         5/12/2006 5:25:35 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         5/4/2006 5:11:52 PM    Brendan W. Farrell
 *         Remove inventory.
 *    4    360Commerce 1.3         1/22/2006 11:45:27 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse
 *
 *
 *    7    .v7x      1.3.1.2     7/21/2006 1:22:10 PM   Michael Wisbauer added
 *         setting default and changed reason code to be required.
 *    6    .v7x      1.3.1.1     7/17/2006 10:55:23 AM  Michael Wisbauer
 *         changed code to set default to none and also commented out code
 *         that was causing cotrol to use db values instead of from bundles
 *    5    .v7x      1.3.1.0     6/23/2006 4:55:58 AM   Dinesh Gautam   CR
 *         4530: Fix for reason code
 *
 *   Revision 1.23.2.1  2004/10/15 18:50:31  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.24  2004/10/11 20:31:16  mweis
 *   @scr 7012 Remove misleading (and dead) code.
 *
 *   Revision 1.23  2004/10/05 20:48:36  cdb
 *   @scr 7246 Removed references to location id and item id in inventory state. Modified returns
 *   so that selection is first in list when default location or state is invalid.
 *
 *   Revision 1.22  2004/08/30 20:34:51  mweis
 *   @scr 7021 POS Inventory database work for locations and statuses.
 *
 *   Revision 1.21  2004/08/27 20:27:29  mweis
 *   @scr 7012 First iteration on Inventory w.r.t. Returns.
 *
 *   Revision 1.20  2004/08/27 17:51:03  bvanschyndel
 *   Added check for inventory integration switch
 *
 *   Revision 1.19  2004/07/30 15:48:24  bvanschyndel
 *   @scr 0 Catch case when no states for a location could be retrieved from
 *   the database.
 *
 *   Revision 1.18  2004/07/28 18:19:59  mweis
 *   @scr 5421 Remove unused imports -- for eclipse
 *
 *   Revision 1.17  2004/07/28 17:09:59  bvanschyndel
 *   @scr 6568 Moved inventory state DB query from the Bean to the Site for returns
 *
 *   Revision 1.16  2004/07/28 16:59:15  bvanschyndel
 *   @scr 0 Moved inventory state DB query from the Bean to the Site for returns
 *
 *   Revision 1.15  2004/07/20 18:54:12  mweis
 *   @scr 6366 ReturnItemInfo's intiFields() method internal rework.
 *
 *   Revision 1.14  2004/07/16 17:01:35  mweis
 *   @scr 5564 Return Item Info panel needs to display full transaction ID (when business date part of trans ID).
 *
 *   Revision 1.13  2004/07/14 20:38:01  mweis
 *   @scr 6032 Quantity field is now left justified on the Return Item Info screen.
 *
 *   Revision 1.12  2004/07/14 00:48:15  mweis
 *   @scr 6172 Return Item Info panel needs title case labels.
 *
 *   Revision 1.11  2004/07/14 00:04:37  mweis
 *   @scr 6174 When required, an item's size matters.
 *
 *   Revision 1.10  2004/06/30 22:44:08  cdb
 *   @scr 0 Hid fields introduced by Red Iron's inventory integration for the Gap release.
 *
 *   Revision 1.9  2004/06/29 22:03:30  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.8  2004/06/02 21:55:59  mweis
 *   @scr 3098 Returns of a non-UOM item allows a fractional (decimal) quantity.
 *
 *   Revision 1.7  2004/03/23 18:42:20  baa
 *   @scr 3561 fix gifcard return bugs
 *
 *   Revision 1.6  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.5  2004/03/11 14:32:10  baa
 *   @scr 3561 Add itemScanned get/set methods to PLUItemCargoIfc and add support for changing type of quantity based on the uom
 *
 *   Revision 1.4  2004/03/10 20:50:14  epd
 *   @scr 3561 Item size now just displays as label if item from retrieved transaction
 *
 *   Revision 1.3  2004/03/05 14:39:37  baa
 *   @scr 3561  Returns
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 17 2003 11:22:52   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.1   Sep 16 2003 17:53:02   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.10   Jul 15 2003 14:46:32   baa
 * allow alphanumeric values on sale associate field
 * Resolution for 3121: sales associate field not editable
 *
 *    Rev 1.9   Apr 23 2003 15:23:38   KLL
 * max serial length is 25 alpha-numerics
 * Resolution for POS SCR-2135: Max length of serial number need be 25
 *
 *    Rev 1.8   Mar 05 2003 15:26:56   HDyer
 * Display localized strings for return reasons.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.7   Feb 24 2003 11:43:40   HDyer
 * Use non-deprecated method to set the model reason code index and string.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.6   Feb 07 2003 12:38:48   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.5   Jan 29 2003 15:53:52   baa
 * fix balace field formatting
 * Resolution for POS SCR-1843: Multilanguage support
 *
 *    Rev 1.4   Sep 23 2002 13:26:46   baa
 * fix decimal field
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Aug 14 2002 18:18:28   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   21 May 2002 17:38:24   baa
 * ils
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   13 May 2002 14:12:04   baa
 * Initial revision.
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   15 Apr 2002 09:35:46   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:57:14   msg
 * Initial revision.
 *
 *    Rev 1.8   11 Mar 2002 16:38:22   cir
 * Set the label for serial number field
 * Resolution for POS SCR-1536: Invalid Data Notice missing what is invalid doing Return by Item using serialized item
 *
 *    Rev 1.7   Mar 06 2002 19:30:08   mpm
 * Added text externalization for returns screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.6   Feb 05 2002 16:43:56   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.5   Jan 31 2002 12:36:44   mpm
 * Added pluggable-look-and-feel.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This class is used to display and gather Return Item Information.
 */
public class ReturnItemInfoBean extends ValidatingBean implements DocumentListener
{
    /** serialVersionUID */
    private static final long serialVersionUID = -8023923046462092221L;

    /**
     * Bean Model
     */
    protected ReturnItemInfoBeanModel beanModel = null;

    /**
     * Label object for the item number
     */
    protected JLabel itemNumberLabel = null;

    /**
     * Label object for the item description
     */
    protected JLabel itemDescriptionLabel = null;

    /**
     * Label object for the price
     */
    protected JLabel priceLabel = null;

    /**
     * Label object for the quantity
     */
    protected JLabel quantityLabel = null;

    /**
     * Label object for the unit of measure
     */
    protected JLabel unitOfMeasureLabel = null;

    /**
     * Label object for the store number
     */
    protected JLabel storeNumberLabel = null;

    /**
     * Label object for the sales associate
     */
    protected JLabel salesAssociateLabel = null;

    /**
     * Label object for the reciept number
     */
    protected JLabel receiptNumberLabel = null;

    /**
     * Label object for the reciept number
     */
    protected JLabel tenderTypeLabel = null;

    /**
     * Label object for the reason code
     */
    protected JLabel returnReasonCodeLabel = null;

    /**
     * Label object for the item condition code
     */
    protected JLabel itemConditionCodeLabel = null;

    /** item SizeLabel **/
    protected JLabel itemSizeLabel = null;

    /** item SerialNumberLabel **/
    protected JLabel serialNumberLabel = null;

    /**
     * Field object for the item number
     */
    protected JLabel itemNumberField = null;

    /**
     * Field object for the item description
     */
    protected JLabel itemDescriptionField = null;

    /**
     * Field object for the price
     */
    protected CurrencyTextField priceField = null;

    /** ItemSize Field **/
    protected NumericTextField itemSizeField = null;

    /**
     * Field object for the item size (display only)
     */
    protected JLabel itemSizeFieldDisplayOnly = null;

    /**
     * Field object for the price (display only)
     */
    protected JLabel priceFieldDisplayOnly = null;

    /**
     * Field object for the quantity
     */
    protected NumericDecimalTextField quantityField = null;

    /**
     * Field object for the quantity (display only)
     */
    protected JLabel quantityFieldDisplayOnly = null;

    /**
     * Field object for the unit of measure
     */
    protected JLabel unitOfMeasureField = null;

    /**
     * Field object for the store number
     */
    protected AlphaNumericTextField storeNumberField = null;

    /**
     * Field object for the sales associate
     */
    protected AlphaNumericTextField salesAssociateField = null;

    /**
     * Field object for the receipt number
     */
    protected AlphaNumericTextField receiptNumberField = null;

    /**
     * Field object for the reason code
     */
    protected ValidatingComboBox tenderTypeComboBox = null;

    /**
     * Field object for the store number (display only)
     */
    protected JLabel storeNumberFieldDisplayOnly = null;

    /**
     * Field object for the sales associate (display only)
     */
    protected JLabel salesAssociateFieldDisplayOnly = null;

    /**
     * Field object for the receipt number (display only)
     */
    protected JLabel receiptNumberFieldDisplayOnly = null;

    /**
     * Field object for the reason code
     */
    protected ValidatingComboBox reasonCodeComboBox = null;

    /**
     * Label for the gift card number
     */
    protected JLabel giftCardNumberLabel = null;

    /**
     * Label for the gift card balance
     */
    protected JLabel giftCardBalanceLabel = null;

    /**
     * Field for the gift card number
     */
    protected JLabel giftCardNumberField = null;

    /**
     * Field for the gift card balance
     */
    protected JLabel giftCardBalanceField = null;

    /**
     * Field object for the serial number
     */
    protected AlphaNumericPlusTextField serialNumberField = null;

    /**
     * Field object for the serial number (display only)
     */
    protected JLabel serialNumberFieldDisplayOnly = null;

    /**
     * Field object for the Item Condition code
     */
    protected ValidatingComboBox itemConditionCodeComboBox = null;

    /**
     * Constant for none
     */
    protected static String none = "(none)";

    /**
     * Constant for units
     */
    protected static String units = "Units";

    /**
     * Indicates if the model has changed.
     */
    protected boolean dirtyModel = false;

    /**
     * indicates decimal qty
     */
    final static int DECIMAL_TYPE = 0;

    /**
     * indicates integer qty
     */
    final static int INTEGER_TYPE = 1;

    /**
     * Inset to use when a widget is a field, combo box, etc.
     */
    protected int iFIELD = 0;

    /**
     * Inset to use when widget is a label (ie, read only).
     */
    protected int iLABEL = 2;

    /** Display Length of Item Description in the item inquiry screen. */
    public static final int MAX_ITM_DESC_DISPLAY_LENGTH = 32;

    /**
     * Constructor
     */
    public ReturnItemInfoBean()
    {
        super();
        initialize();
    }

    /**
     * Initialize the class with Gift Card Item.
     */
    protected void initialize()
    {
        setName("ReturnItemInfoBean");
        setLayout(new GridBagLayout());
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
    }

    /**
     * Initializes the components for the bean.
     */
    protected void initComponents()
    {
        initLabels();
        initFields();
    }

    /**
     * Initialize the label components.
     */
    protected void initLabels()
    {
        // Initializing the constraints
        GridBagConstraints constraints = uiFactory.getConstraints("DataEntryBean");
        constraints.gridx = 0;
        constraints.gridy = 0;

        // create labels
        itemNumberLabel = uiFactory.createLabel("Item Number:", "Item Number:", null, UI_LABEL);
        add(itemNumberLabel, constraints);

        constraints.gridy = GridBagConstraints.RELATIVE;

        itemDescriptionLabel = uiFactory.createLabel("Item Description:", "Item Description:", null, UI_LABEL);
        add(itemDescriptionLabel, constraints);

        constraints.gridy = GridBagConstraints.RELATIVE;

        priceLabel = uiFactory.createLabel("Price:", "Price:", null, UI_LABEL);
        add(priceLabel, constraints);

        itemSizeLabel = uiFactory.createLabel("Size:", "Size:", null, UI_LABEL);
        add(itemSizeLabel, constraints);

        unitOfMeasureLabel = uiFactory.createLabel("Unit of Measure:", "Unit of Measure:", null, UI_LABEL);
        add(unitOfMeasureLabel, constraints);

        quantityLabel = uiFactory.createLabel("Quantity:", "Quantity:", null, UI_LABEL);
        add(quantityLabel, constraints);

        storeNumberLabel = uiFactory.createLabel("Store Number:", "Store Number:", null, UI_LABEL);
        add(storeNumberLabel, constraints);

        salesAssociateLabel = uiFactory.createLabel("Sales Associate:", "Sales Associate:", null, UI_LABEL);
        add(salesAssociateLabel, constraints);

        receiptNumberLabel = uiFactory.createLabel("Receipt Number:", "Receipt Number:", null, UI_LABEL);
        add(receiptNumberLabel, constraints);

        returnReasonCodeLabel = uiFactory.createLabel("Return Reason Code:", "Return Reason Code:", null, UI_LABEL);
        add(returnReasonCodeLabel, constraints);

        itemConditionCodeLabel = uiFactory.createLabel("Item Condition Code:", "Item Condition Code:", null, UI_LABEL);
        add(itemConditionCodeLabel, constraints);

        giftCardNumberLabel = uiFactory.createLabel("Gift Card Number:", "Gift Card Number:", null, UI_LABEL);
        add(giftCardNumberLabel, constraints);

        giftCardBalanceLabel = uiFactory.createLabel("Initial Balance:", "Initial Balance:", null, UI_LABEL);
        add(giftCardBalanceLabel, constraints);

        serialNumberLabel = uiFactory.createLabel("Serial Number:", "Serial Number:", null, UI_LABEL);
        add(serialNumberLabel, constraints);

        tenderTypeLabel = uiFactory.createLabel("Tender Type:", "Tender Type:", null, UI_LABEL);
        add(tenderTypeLabel, constraints);
    }

    /**
     * Initialize the field components.
     */
    protected void initFields()
    {
        // initialize the constraints
        GridBagConstraints constraints = uiFactory.getConstraints("DataEntryBean");
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, iLABEL, 5);

        // Item Number field
        itemNumberField = uiFactory.createLabel("not available", "not available", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(itemNumberField, constraints);

        // update constraints
        constraints.gridy = GridBagConstraints.RELATIVE;

        // Item Description field
        itemDescriptionField = uiFactory.createLabel("no description available", "no description available", null,
                UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(itemDescriptionField, constraints);

        // Price field + display
        priceField = uiFactory.createCurrencyField("PriceField", "true", "false", "false");
        priceField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(priceField, constraints);

        priceFieldDisplayOnly = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(priceFieldDisplayOnly, constraints);

        // Item Size field + display
        itemSizeField = uiFactory.createNumericField("ItemSizeField", "1", "10");
        constraints.insets.bottom = iFIELD;
        add(itemSizeField, constraints);

        itemSizeFieldDisplayOnly = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(itemSizeFieldDisplayOnly, constraints);

        // Unit Of Measure field
        unitOfMeasureField = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(unitOfMeasureField, constraints);

        // Quantity field + display
        quantityField = uiFactory.createNumericDecimalField("QuantityField", 3, false);
        quantityField.setHorizontalAlignment(JTextField.LEFT); // force it to
                                                               // take input
                                                               // like the
                                                               // others
        quantityField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(quantityField, constraints);

        quantityFieldDisplayOnly = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(quantityFieldDisplayOnly, constraints);

        // Store Number field + display
        storeNumberField = uiFactory.createAlphaNumericField("StoreNumberField", "1", "5", false);
        storeNumberField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(storeNumberField, constraints);

        storeNumberFieldDisplayOnly = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(storeNumberFieldDisplayOnly, constraints);

        // Sales Associate field + display
        salesAssociateField = uiFactory.createAlphaNumericField("SalesAssociateField", "0", "10", "10", false);
        salesAssociateField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(salesAssociateField, constraints);

        salesAssociateFieldDisplayOnly = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(salesAssociateFieldDisplayOnly, constraints);

        // Receipt Number field + display
        String transIDLength = Integer.toString(TransactionID.getTransactionIDLength());
        receiptNumberField = uiFactory.createAlphaNumericField("ReceiptNumberField", "0", "30", true);
        receiptNumberField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(receiptNumberField, constraints);

        receiptNumberFieldDisplayOnly = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(receiptNumberFieldDisplayOnly, constraints);

        // Reason Code combo box
        reasonCodeComboBox = uiFactory.createValidatingComboBox("ReasonCodeField", "false", "20");
        reasonCodeComboBox.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(reasonCodeComboBox, constraints);

        // Item condition code combo box
        itemConditionCodeComboBox = uiFactory.createValidatingComboBox("ItemConditionCodeField", "false", "20");
        itemConditionCodeComboBox.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(itemConditionCodeComboBox, constraints);

        // Gift Card Number label
        giftCardNumberField = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.top = iLABEL;
        constraints.insets.bottom = iLABEL;
        add(giftCardNumberField, constraints);
        constraints.insets.top = 0; // reset

        // Gift Card Balance label
        giftCardBalanceField = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(giftCardBalanceField, constraints);

        // SerialNumber field + display
        serialNumberField = uiFactory.createAlphaNumericPlusField("SerialNumberField", "1", "15", false,"-");
        serialNumberField.setEnabled(true);
        constraints.insets.bottom = iFIELD;
        add(serialNumberField, constraints);

        serialNumberFieldDisplayOnly = uiFactory.createLabel("N/A", "N/A", null, UI_LABEL);
        constraints.insets.bottom = iLABEL;
        add(serialNumberFieldDisplayOnly, constraints);

        // Tender Type combo box
        tenderTypeComboBox = 
            uiFactory.createValidatingComboBox("TenderType", "true", "15");
        tenderTypeComboBox.setShowDisabled(true);
        constraints.insets.bottom = iFIELD;
        add(tenderTypeComboBox, constraints);

    }

    /**
     * Sets the model property (java.lang.Object) value.
     * 
     * @param model The new value for the property.
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ReturnItemInfoBean " + "model to null");
        }
        if (model instanceof ReturnItemInfoBeanModel)
        {
            beanModel = (ReturnItemInfoBeanModel) model;
            updateBean();
        }
    }

    /**
     * Update the model if It's been changed
     */
    protected void updateBean()
    {
        itemNumberField.setText(beanModel.getItemNumber());
        itemDescriptionField.setText(UIUtilities.makeSafeStringForDisplay(beanModel.getItemDescription(),
                MAX_ITM_DESC_DISPLAY_LENGTH));
        unitOfMeasureField.setText(beanModel.getUnitOfMeasure());

        // Use the proper unitOfMeasure text description.
        if (beanModel.getUnitOfMeasure().equalsIgnoreCase(none) || beanModel.getUnitOfMeasure().equalsIgnoreCase(units))
        {
            beanModel.setUnitOfMeasure(retrieveText("EachLabel", "Each"));
        }

        // Reason Code combo box
        reasonCodeComboBox.setModel(new ValidatingComboBoxModel(beanModel.getReasonCodes()));

        // Item Condition combo box
        itemConditionCodeComboBox.setModel(new ValidatingComboBoxModel(beanModel.getItemConditionModel()
                .getItemConditionCodes()));

        if (beanModel.isSelected())
        {
            reasonCodeComboBox.setSelectedIndex(beanModel.getSelectedIndex());
        }
        else
        {
            reasonCodeComboBox.setSelectedIndex(-1);
            beanModel.setSelectedReasonCode(-1);
        }

        if (beanModel.getItemConditionModel().isSelected())
        {
            itemConditionCodeComboBox.setSelectedIndex(beanModel.getItemConditionModel().getSelectedIndex());

        }
        else
        {
            itemConditionCodeComboBox.setSelectedIndex(-1);
            beanModel.getItemConditionModel().setSelectedItemConditionCode(-1);
        }

        // Set up labels and fields for a gift card
        if (beanModel.getGiftCardSerialNumber() != null)
        {
            giftCardNumberField.setText(beanModel.getGiftCardSerialNumber());
            giftCardBalanceField.setText(LocaleUtilities.formatDecimal(beanModel.getGiftCardBalance(),
                    getDefaultLocale()));

            giftCardNumberField.setVisible(true);
            giftCardBalanceField.setVisible(true);

            giftCardNumberLabel.setVisible(true);
            giftCardBalanceLabel.setVisible(true);

            priceLabel.setText(retrieveText("CurrentBalanceLabel", "Current Balance:"));

            quantityFieldDisplayOnly.setText(quantityField.getText());
            quantityFieldDisplayOnly.setVisible(true);
            quantityField.setVisible(false);
            quantityField.setEnabled(false);

            String formattedPrice = getCurrencyService().formatCurrency(beanModel.getPrice(), getDefaultLocale());
            priceFieldDisplayOnly.setText(formattedPrice);
            priceFieldDisplayOnly.setVisible(true);
            priceField.setVisible(false);
            priceField.setEnabled(false);
        }
        else
        {
            giftCardNumberField.setVisible(false);
            giftCardBalanceField.setVisible(false);

            giftCardNumberLabel.setVisible(false);
            giftCardBalanceLabel.setVisible(false);

            priceLabel.setText(retrieveText("ReturnPriceLabel", "Price :"));
        }

        // Initialize the values of all fields which can be disabled.
        intializeEnableDisableFields();

        // Enable and disable fields depending on bean model settings.
        if (beanModel.isItemSizeEnabled())
        {
            itemSizeFieldDisplayOnly.setVisible(false);
            itemSizeField.setVisible(true);
            setFieldRequired(itemSizeField, beanModel.isItemSizeRequired());
        }
        else
        {
            itemSizeFieldDisplayOnly.setText(beanModel.getItemSize());
            itemSizeFieldDisplayOnly.setVisible(true);
            itemSizeField.setVisible(false);
            itemSizeField.setEnabled(false);
            setFieldRequired(itemSizeField, false);
        }

        // Setup all the fields that be optionally enabled.
        if (beanModel.isPriceEnabled())
        {
            priceFieldDisplayOnly.setVisible(false);
            priceField.setVisible(true);
            priceField.setEnabled(true);      
        }
        else
        {
            String formattedPrice = getCurrencyService().formatCurrency(beanModel.getPrice(),
                    LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            
            setFieldRequired(priceField, false);
            Document doc = priceField.getDocument();
            if (doc instanceof ConstrainedTextDocument)
            {
                ((ConstrainedTextDocument)doc).transientBypassMaxValidation = true;
            }            
            priceFieldDisplayOnly.setText(formattedPrice);
            priceFieldDisplayOnly.setVisible(true);
            priceField.setVisible(false);
            priceField.setEnabled(false);           

        }

        // The store number field is required.
        if (beanModel.isStoreNumberEnabled())
        {
            storeNumberFieldDisplayOnly.setVisible(false);
            storeNumberField.setVisible(true);
            storeNumberField.setEnabled(true);
        }
        else
        {
            storeNumberFieldDisplayOnly.setText(beanModel.getStoreNumber());
            storeNumberFieldDisplayOnly.setVisible(true);
            storeNumberField.setVisible(false);
            storeNumberField.setEnabled(false);
        }

        if (beanModel.isSalesAssociateEnabled())
        {
            salesAssociateFieldDisplayOnly.setVisible(false);
            salesAssociateField.setVisible(true);
            salesAssociateField.setEnabled(true);
        }
        else
        {
            salesAssociateFieldDisplayOnly.setText(beanModel.getSalesAssociate());
            salesAssociateFieldDisplayOnly.setVisible(true);
            salesAssociateField.setVisible(false);
            salesAssociateField.setEnabled(false);
        }

        if (beanModel.isReceiptNumberEnabled())
        {
            receiptNumberFieldDisplayOnly.setVisible(false);
            receiptNumberField.setVisible(true);
            receiptNumberField.setEnabled(true);
            // Tender Type combo box
            if (receiptNumberField.getText().length() == 0)
            {
                tenderTypeComboBox.setModel(new ValidatingComboBoxModel(beanModel.getTenderDescriptors()));
                tenderTypeComboBox.setSelectedIndex(-1);
                tenderTypeComboBox.setEnabled(false);
            }
            else
            {
                tenderTypeComboBox.setEnabled(true);
            }
            tenderTypeComboBox.setVisible(true);
            tenderTypeLabel.setVisible(true);
        }
        else
        {
            receiptNumberFieldDisplayOnly.setText(beanModel.getReceiptNumber());
            receiptNumberFieldDisplayOnly.setVisible(true);
            receiptNumberField.setVisible(false);
            receiptNumberField.setEnabled(false);
            tenderTypeComboBox.setVisible(false);
            tenderTypeComboBox.setEnabled(false);
            tenderTypeComboBox.setSelectedIndex(-1);
            beanModel.setTenderSelectedIndex(-1);
            tenderTypeLabel.setVisible(false);
        }

        if (beanModel.isQuantityEnabled())
        {
            quantityFieldDisplayOnly.setVisible(false);
            quantityField.setVisible(true);
            quantityField.setEnabled(true);
        }
        else
        {
            quantityFieldDisplayOnly.setText(quantityField.getText());
            quantityFieldDisplayOnly.setVisible(true);
            quantityField.setVisible(false);
            quantityField.setEnabled(false);
        }
        // The Serial number field is required.
        if (beanModel.getSerialNumberRequired())
        {
            serialNumberLabel.setVisible(true);
            if (beanModel.isSerialNumberEnabled())
            {
                serialNumberFieldDisplayOnly.setVisible(false);
                serialNumberField.setVisible(true);
                serialNumberField.setEnabled(true);
            }
            else
            {
                serialNumberFieldDisplayOnly.setText(serialNumberField.getText());
                serialNumberFieldDisplayOnly.setVisible(true);
                serialNumberField.setVisible(false);
                serialNumberField.setEnabled(false);
            }
            setFieldRequired(serialNumberField, beanModel.getSerialNumberRequired());
        }
        else
        {
            serialNumberLabel.setVisible(false);
            serialNumberField.setVisible(false);
            serialNumberFieldDisplayOnly.setVisible(false);
            setFieldRequired(serialNumberField, false);
        }

        // Final things
        setCurrentFocus(storeNumberField);
    }

    /**
     * The fields which can be enabled/disabled programatically, must be
     * initialized with the values from the bean model due to the fact that the
     * updateModel() method repopulates the model with this data. This can lead
     * to a number of bad effects.
     */
    private void intializeEnableDisableFields()
    {
        itemSizeField.setText(beanModel.getItemSize());
        priceField.setValue(beanModel.getPrice());
        storeNumberField.setText(beanModel.getStoreNumber());
        salesAssociateField.setText(beanModel.getSalesAssociate());
        receiptNumberField.setText(beanModel.getReceiptNumber());
        serialNumberField.setText(beanModel.getSerialNumber());
        // Control the type of text field input based on if we allow fractions
        if (!beanModel.isUOM())
        {
            // Set quantity type integer
            setQuantityFieldType(INTEGER_TYPE);
        }
        else
        {
            // Set quantity type decimal
            setQuantityFieldType(DECIMAL_TYPE);
        }
        quantityField.setDecimalValue(beanModel.getQuantity());
    }

    /**
     * Gets the model property (java.lang.Object) value.
     */
    public void updateModel()
    {
        // Note: The fieldReasonCodes of the model are alread set.
        beanModel.setItemNumber(itemNumberField.getText());
        beanModel.setItemDescription(itemDescriptionField.getText());
        if (beanModel.isPriceEnabled())
        {
            beanModel.setPrice(priceField.getCurrencyValue());
        }

        beanModel.setItemSize(itemSizeField.getText());
        beanModel.setQuantity(quantityField.getDecimalValue());
        beanModel.setUnitOfMeasure(unitOfMeasureField.getText());
        beanModel.setStoreNumber(storeNumberField.getText());
        beanModel.setSalesAssociate(salesAssociateField.getText());
        beanModel.setReceiptNumber(receiptNumberField.getText());
        beanModel.setSerialNumber(serialNumberField.getText());

        if (beanModel.getGiftCardSerialNumber() != null)
        {
            beanModel.setGiftCardSerialNumber(giftCardNumberField.getText());
            if (giftCardBalanceField.getText().length() > 0)
            {
                Number balance = LocaleUtilities.parseCurrency(giftCardBalanceField.getText(), getDefaultLocale());
                beanModel.setGiftCardBalance(new BigDecimal(balance.toString()));
            }

        }

        // set index for item selected
        int reasonIndex = reasonCodeComboBox.getSelectedIndex();
        if (reasonIndex >= 0)
        {
            beanModel.setSelectedReasonCode(reasonIndex);
            beanModel.setSelected(true);
        }
        else
        {
            beanModel.setSelected(false);
        }

        // set index for item condition
        int itemConditionIndex = itemConditionCodeComboBox.getSelectedIndex();
        if (itemConditionIndex >= 0)
        {
            beanModel.getItemConditionModel().setSelectedItemConditionCode(itemConditionIndex);
            beanModel.getItemConditionModel().setSelected(true);
        }
        else
        {
            beanModel.getItemConditionModel().setSelected(false);
        }

        // set index for item selected
        int tenderIndex = tenderTypeComboBox.getSelectedIndex();
        if (tenderIndex >= 0)
        {
            beanModel.setTenderSelectedIndex(tenderIndex);
        }
    }

    /**
     * Return the POSBaseBeanModel.
     * 
     * @return posBaseBeanModel as POSBaseBeanModel
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Override ValidatingBean setVisible() to request focus.
     * 
     * @param aFlag indicates if the component should be visible or not.
     */
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        // ValidatingBean set the errorFound flag when it finds a
        // validation error. If an error has been found, ValidatingBean
        // sets the focus the first error field.
        if (aFlag && !getErrorFound())
        {
            if (beanModel.isStoreNumberEnabled())
            {
                setCurrentFocus(storeNumberField);
            }
            else if (beanModel.isQuantityEnabled())
            {
                setCurrentFocus(quantityField);
            }
            else
            {
                setCurrentFocus(priceField);
            }
        }

    }

    /**
     * Activates this bean.
     */
    public void activate()
    {
        super.activate();
        storeNumberField.addFocusListener(this);
        quantityField.addFocusListener(this);
        priceField.addFocusListener(this);
        serialNumberField.addFocusListener(this);
        if (beanModel.isReceiptNumberEnabled())
        {
            receiptNumberField.getDocument().addDocumentListener(this);
        }

        // Note: Assume "status" drop down list does *not* change as user makes
        // a selection in "location".
        // modify "status" drop down list content when user makes a selection in
        // "location"
    }

    /**
     * Sets the correct type for the quantity field
     * 
     * @param type whether this field should be integer or decimal
     */
    protected void setQuantityFieldType(int type)
    {
        switch (type)
        {
        case (DECIMAL_TYPE):
            DecimalDocument decDoc = quantityField.getDecimalDocument(7, false, 2);
            decDoc.setZeroAllowed(false);
            quantityField.setDocument(decDoc);
            break;

        case (INTEGER_TYPE):
        default:
            NumericDocument numDoc = quantityField.getNumericDocument(3, false);
            quantityField.setDocument(numDoc);
        }
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        storeNumberField.removeFocusListener(this);
        quantityField.removeFocusListener(this);
        priceField.removeFocusListener(this);
        serialNumberField.removeFocusListener(this);
        if (beanModel.isReceiptNumberEnabled())
        {
            receiptNumberField.getDocument().removeDocumentListener(this);
        }
    }

    /**
     * Updates property-based fields.
     */
    public void updatePropertyFields()
    {
        itemNumberLabel.setText(retrieveText("ItemIDLabel", itemNumberLabel));
        itemDescriptionLabel.setText(retrieveText("ItemDescriptionLabel", itemDescriptionLabel));
        priceLabel.setText(retrieveText("ReturnPriceLabel", priceLabel));
        itemSizeLabel.setText(retrieveText("ItemSizeLabel", itemSizeLabel));
        quantityLabel.setText(retrieveText("ReturnQuantityLabel", quantityLabel));
        unitOfMeasureLabel.setText(retrieveText("UnitOfMeasureLabel", unitOfMeasureLabel));
        storeNumberLabel.setText(retrieveText("StoreNumberLabel", storeNumberLabel));
        salesAssociateLabel.setText(retrieveText("SalesAssociateLabel", salesAssociateLabel));
        receiptNumberLabel.setText(retrieveText("ReceiptNumberLabel", receiptNumberLabel));
        returnReasonCodeLabel.setText(retrieveText("ReturnReasonCodeLabel", returnReasonCodeLabel));
        itemConditionCodeLabel.setText(retrieveText("ItemConditionCodeLabel", itemConditionCodeLabel));
        giftCardNumberLabel.setText(retrieveText("GiftCardNumberLabel", giftCardNumberLabel));
        giftCardBalanceLabel.setText(retrieveText("GiftCardBalanceLabel", giftCardBalanceLabel));
        serialNumberLabel.setText(retrieveText("SerialNumberLabel", serialNumberLabel));
        tenderTypeLabel.setText(retrieveText("TenderTypeLabel", tenderTypeLabel));
        
        // associate fields with labels
        receiptNumberField.setLabel(receiptNumberLabel);
        salesAssociateField.setLabel(salesAssociateLabel);
        storeNumberField.setLabel(storeNumberLabel);
        quantityField.setLabel(quantityLabel);
        priceField.setLabel(priceLabel);
        itemSizeField.setLabel(itemSizeLabel);
        reasonCodeComboBox.setLabel(returnReasonCodeLabel);
        itemConditionCodeComboBox.setLabel(itemConditionCodeLabel);
        serialNumberField.setLabel(serialNumberLabel);
        tenderTypeComboBox.setLabel(tenderTypeLabel);
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  GetReturnItemInformationAisle (Revision " + getRevisionNumber() + ")"
                + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();
        java.awt.Frame frame = new java.awt.Frame();
        ReturnItemInfoBean aReturnItemInfoBean;
        aReturnItemInfoBean = new ReturnItemInfoBean();
        frame.add("Center", aReturnItemInfoBean);
        frame.setSize(aReturnItemInfoBean.getSize());
        frame.setVisible(true);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void changedUpdate(DocumentEvent e)
    {
        enableDisableTenderComboBox(e);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void insertUpdate(DocumentEvent e)
    {
        enableDisableTenderComboBox(e);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void removeUpdate(DocumentEvent e)
    {
        enableDisableTenderComboBox(e);
    }
    
    /**
     * This enables or disables the Tender Combo Box based on
     * the current length of the reciept number field.
     * @param e document event
     */
    protected void enableDisableTenderComboBox(DocumentEvent e)
    {
        if (beanModel.isReceiptNumberEnabled())
        {
            if (receiptNumberField.getText().length() > 0)
            {
                tenderTypeComboBox.setEnabled(true);
            }
            else
            {
                tenderTypeComboBox.setSelectedIndex(-1);
                tenderTypeComboBox.setModel(new ValidatingComboBoxModel(beanModel.getTenderDescriptors()));
                tenderTypeComboBox.setEnabled(false);
            }
        }
    }
}
