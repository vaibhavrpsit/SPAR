/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MailBankCheckInfoBean.java /main/23 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   04/03/12 - removed deprecated methods
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    mkutiana  02/28/12 - XbranchMerge
 *                         mkutiana_bug13780593-field_alignment_fixed from
 *                         rgbustores_13.4x_generic_branch
 *    mkutiana  02/27/12 - Using this since layout
 *                         intialization needs to be overidden at the
 *                         initLayout stage
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vchengeg  12/12/08 - EJ defect fixes
 *    mkochumm  11/20/08 - cleanup based on i18n changes
 *    mkochumm  11/05/08 - i18n changes for phone and postalcode fields
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Feb 05 2004 14:28:50   bjosserand
 * Mail Bank Check.
 *
 *    Rev 1.1   Sep 16 2003 17:52:44   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.8   Apr 11 2003 11:45:32   baa
 * extend customerInfobean
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.7   Mar 21 2003 10:58:46   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.6   Feb 19 2003 14:42:48   crain
 * Replaced abbreviations
 * Resolution for 1760: Layaway feature updates
 *
 *    Rev 1.5   Jan 23 2003 18:27:42   crain
 * Removed setting the screen name
 * Resolution for 1760: Layaway feature updates
 *
 *    Rev 1.4   Jan 21 2003 15:19:30   crain
 * Changed to accomodate business customer
 * Resolution for 1760: Layaway feature updates
 *
 *    Rev 1.3   Sep 20 2002 18:03:06   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 18 2002 17:15:30   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:18:00   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:48:58   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:28   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:56:08   msg
 * Initial revision.
 *
 *    Rev 1.9   Mar 08 2002 14:36:12   mpm
 * Externalized text for send UI screens.
 *
 *    Rev 1.8   Mar 01 2002 10:02:56   mpm
 * Internationalization of tender-related screens
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.7   Feb 07 2002 19:45:58   dfh
 * added email field below telephone number, removed use in
 * special order customer info - set it to visible there, invisible in mail bank check info -
 * Resolution for POS SCR-907: Text field boxes on Special Order Customer too small to data
 *
 *
 *    Rev 1.6   Jan 22 2002 06:32:32   mpm
 *
 * UI fixes.
 *
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *
 *    Rev 1.5   Jan 19 2002 10:30:56   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   20 Dec 2001 18:44:16   baa
 * remove focus from prompt area
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.2   20 Dec 2001 11:23:38   baa
 * update for extended postal code
 * Resolution for POS SCR-98: Invalid Data Notice cites Postal Code vs. Ext Postal Code
 *
 *    Rev 1.1   Dec 03 2001 16:26:26   dfh
 * updates local journal string for changes to customer data, should these changes need to be journaled
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.0   Sep 21 2001 11:35:14   msg
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:17:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.localization.AddressField;
import oracle.retail.stores.pos.ui.localization.OrderableField;

/**
 * Contains the visual presentation for Mail Bank Check Information
   @version $Revision: /main/23 $
 */
public class MailBankCheckInfoBean extends CustomerInfoBean
{

    private static final long serialVersionUID = -4787311011694458386L;

    /**
     * Default class Constructor and initializes its components.
     */
    public MailBankCheckInfoBean()
    {
        super();
    }

    /**
     * Using/Overriding this only this method since
     * the taxcertificate/reasoncode layout intialization needs to be ovveridden at the initLayout stage.
     *
     * Initialize the layout.
     *
     */
    protected void initLayout()
    {
        JPanel postalPanel = uiFactory.createPostalPanel(postalCodeField);
        JPanel panel1 = createPanel(discountField, pricingGroupLabel, pricingGroupField);

        // initial list of fields in the order they occur in the UI currently
        List<OrderableField> orderableFields = new ArrayList<OrderableField>(16);
        orderableFields.add(new OrderableField(firstNameLabel, firstNameField, AddressField.FIRST_NAME));
        orderableFields.add(new OrderableField(lastNameLabel, lastNameField, AddressField.LAST_NAME));
        orderableFields.add(new OrderableField(customerNameLabel, customerNameField, AddressField.BUSINESS_NAME));
        orderableFields.add(new OrderableField(addressLine1Label, addressLine1Field, AddressField.ADDRESS_LINE_1));
        orderableFields.add(new OrderableField(addressLine2Label, addressLine2Field, AddressField.ADDRESS_LINE_2));
        orderableFields.add(new OrderableField(cityLabel, cityField, AddressField.CITY));
        orderableFields.add(new OrderableField(countryLabel, countryField, AddressField.COUNTRY));
        orderableFields.add(new OrderableField(stateLabel, stateField, AddressField.STATE));
        orderableFields.add(new OrderableField(postalCodeLabel, postalPanel, AddressField.POSTAL_CODE));
        orderableFields.add(new OrderableField(phoneTypeLabel, phoneTypeField, AddressField.TELEPHONE_TYPE));
        orderableFields.add(new OrderableField(telephoneLabel, telephoneField, AddressField.TELEPHONE));
        orderableFields.add(new OrderableField(emailLabel, emailField));
        orderableFields.add(new OrderableField(discountLabel, panel1));
        orderableFields.add(new OrderableField(reasonCodeLabel, reasonCodeField));
        orderableFields.add(new OrderableField(custTaxIDLabel, custTaxIDField));
        orderableFields.add(new OrderableField(instrLabel, instrScrollPane));

        List<OrderableField> orderedFields = CustomerUtilities.arrangeInAddressFieldOrder(orderableFields);

        setLayout(new GridBagLayout());

        int xValue = 0;
		if (!isCustomerLookup()) // if customer info lookup do not layout the customer id
		{
			UIUtilities.layoutComponent(this, customerIDLabel, customerIDField,
			        0, 0, false);
			UIUtilities.layoutComponent(this, employeeIDLabel, employeeIDField,
			        0, 1, false);
			customerIDField.setLabel(customerIDLabel);
			employeeIDField.setLabel(employeeIDLabel);
			xValue = 2;
		}

        for (OrderableField orderedField : orderedFields)
        {
            UIUtilities.layoutComponent(this, orderedField.getLabel(), orderedField.getField(), 0, xValue, false);
            xValue++;
        }
        // init labels for fields
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        customerNameField.setLabel(customerNameLabel);
    }


    /**
     * Returns default display string.
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: MailBankCheckInfoBean @" + hashCode());
        return(strResult);
    }

 }
