/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryChargeMenuBean.java /main/17 2012/10/16 17:37:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    sgu       02/02/09 - allow double byte space for french locale
 *
 * ===========================================================================
 * $Log:
 *     7    I18N_P2    1.5.1.0     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *          max length of constraied text field.
 *     6    360Commerce 1.5         7/9/2007 3:07:53 PM    Anda D. Cadar   I18N
 *           changes for CR 27494: POS 1st initialization when Server is
 *          offline
 *     5    360Commerce 1.4         6/11/2007 11:48:53 AM  Anda D. Cadar   SCR
 *          27206: switch to ConstrainedTExtField to display proper formatting
 *           for the currecies amounts
 *     4    360Commerce 1.3         4/25/2007 8:51:27 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:15 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:25:39 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse
 *    $
 *    Revision 1.6  2004/06/25 00:24:34  dcobb
 *    @scr 4205 Feature Enhancement: Till Options
 *    Lookup TotalLabel tag in bundles.
 *
 *    Revision 1.5  2004/05/20 22:54:59  cdb
 *    @scr 4204 Removed tabs from code base again.
 *
 *    Revision 1.4  2004/05/20 20:40:53  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Removed alternate tender from Select Tender screen and
 *    corrected Select Charge screen.
 *
 *    Revision 1.3  2004/03/16 17:15:18  build
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 20:56:27  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Apr 18 2003 17:14:02   baa
 * fix buttons on screen
 * Resolution for POS SCR-2170: Missing property names in bundles
 *
 *    Rev 1.5   Apr 18 2003 09:42:38   baa
 * fixes to bundles
 * Resolution for POS SCR-2170: Missing property names in bundles
 *
 *    Rev 1.4   Nov 26 2002 17:41:36   kmorneau
 * fix display of expected amounts for blind close - now using fields
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.3   Nov 18 2002 13:42:42   kmorneau
 * added ui functionality for display of expected amounts for non-blind close
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.2   Sep 03 2002 16:07:54   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:18:54   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:51:54   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:20   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:57:48   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 07 2002 14:53:00   mpm
 * Text externalization for till UI screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   Jan 19 2002 10:32:14   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   02 Jan 2002 15:37:30   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Allows the usr to select a tender to work with.
 * 
 * @version $Revision: /main/17 $
 */
public class SummaryChargeMenuBean extends BaseBeanAdapter
{
    private static final long serialVersionUID = -705787693360390591L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/17 $";

    /** The bean model for this bean */
    protected SummaryChargeMenuBeanModel beanModel;

    /** The label for the total field */
    protected static String TOTAL_TEXT = "Total:";

    /** The label for the total field */
    protected static String TOTAL_LABEL = "TotalLabel";

    /** Screen labels */
    protected JLabel[] fieldLabels = null;

    /** Screen fields */
    protected JComponent[] totalFields = null;

    /** Indicates if this class was started from the Main method in this class. */
    public boolean testOnly = false;

    /**
     *  Default constructor.
     */
    public SummaryChargeMenuBean()
    {
        super();
    }

    /**
     * Configures the class.
     */
    @Override
    public void configure()
    {
        setName("SummaryChargeMenuBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
    }

    /**
     * Called when the panel is removed.
     */
    @Override
    public void deactivate()
    {
        removeAll();
    }

    /**
     * Updates the display with data from the model. This will create labels and
     * fields based on number of summary counts in the model.
     */
    @Override
    protected void updateBean()
    {
        this.removeAll();
        initComponents();
        initLayout();
    }

    /**
     * Creates the labels and fields for the summary counts.
     */
    protected void initComponents()
    {
        setLayout(new GridBagLayout());
        CurrencyIfc total = null;
        try
        {
            total = DomainGateway.getBaseCurrencyInstance();
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        SummaryCountBeanModel sc[] = beanModel.getSummaryCountBeanModel();
        int spacerPos = sc.length;
        int totalPos = sc.length + 1;
        fieldLabels  = new JLabel[sc.length + 2];
        totalFields  = new JComponent[sc.length + 2];

        for(int cnt = 0; cnt < sc.length; cnt++)
        {
            String fieldName = sc[cnt].getDescription() + "AmountField";
            CurrencyIfc sum  = sc[cnt].getAmount();

            String labelText = retrieveText(sc[cnt].getLabelTag(),
                                            sc[cnt].getLabel());
            fieldLabels[cnt] = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

            ConstrainedTextField  currencyField = uiFactory.createConstrainedField(fieldName, "1", "10", true);

            currencyField.setEnabled(false);
            //use default locale when displaying currency
            currencyField.setText(sum.toFormattedString());
            totalFields[cnt] = currencyField;
            if (total != null)
            {
                total = total.add(sum);
            }
        }

        // blank line - There must be at least one character in order for the line to show
        fieldLabels[spacerPos] = uiFactory.createLabel(" ", null, UI_LABEL);
        JLabel spacer = uiFactory.createLabel("", null, UI_LABEL);
        spacer.setEnabled(false);
        spacer.setVisible(true);
        totalFields[spacerPos] = spacer;

        // total field

        String totalLabelText = retrieveText(TOTAL_LABEL, TOTAL_TEXT);
        fieldLabels[totalPos] = uiFactory.createLabel(totalLabelText, totalLabelText, null, UI_LABEL);

        ConstrainedTextField  currencyField = uiFactory.createConstrainedField(TOTAL_TEXT + "AmountField", "1", "10", true);
        currencyField.setEditable(false);
        currencyField.setEnabled(false);
        if (total != null)
        {
            currencyField.setText(total.toFormattedString());
        }
        totalFields[totalPos] = currencyField;
    }

    /**
     * Updates property-based fields.
     */
    public void updatePropertyFields()
    {
        if (beanModel != null)
        {
            SummaryCountBeanModel[] sc = beanModel.getSummaryCountBeanModel();
            // column 1 labels & fields
            if (fieldLabels != null)
            {
                for(int i = 0; i < sc.length; i++)
                {
                    fieldLabels[i].setText(retrieveText(sc[i].getLabelTag(),
                                                        sc[i].getLabel()));
                }

                fieldLabels[sc.length].setText(retrieveText(TOTAL_LABEL,
                                                            TOTAL_TEXT) );
            }
        }
    }

    /**
     * Lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, fieldLabels, totalFields);
    }

    /**
     * Set the bean model into the bean.
     * 
     * @param model The bean model.
     */
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set SummaryChargeMenuBeanModel"
                                           + " to null");
        }
        if (model instanceof SummaryChargeMenuBeanModel)
        {
            beanModel = (SummaryChargeMenuBeanModel)model;
            updateBean();
        }
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: SummaryChargeMenuBeanModel (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**
     * Displays the bean for test purposes.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>None.
     * </UL>
     * 
     * @param args[] The command line parameters
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        SummaryCountBeanModel sArray[] = new SummaryCountBeanModel[8];

        SummaryCountBeanModel scbm   = new SummaryCountBeanModel();
        scbm.setDescription("Cash");
        scbm.setLabel("Cash");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("123.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[0] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Charge");
        scbm.setLabel("Charge");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("223.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[1] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Check");
        scbm.setLabel("Check");

        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("323.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[2] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Gift Certificate");
        scbm.setLabel("Gift Certificate");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("423.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[3] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Store Credit");
        scbm.setLabel("Store Credit");

        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("523.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[4] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Traveler's Check");
        scbm.setLabel("Traveler's Check");

        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("623.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[5] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Manufacturer's Coupon");
        scbm.setLabel("Manufacturer's Coupon");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("723.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[6] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Retailer's Coupon");
        scbm.setLabel("Retailer's Coupon");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("823.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database may be online");
        }
        sArray[7] = scbm;

        SummaryChargeMenuBeanModel smbm = new SummaryChargeMenuBeanModel();
        smbm.setSummaryCountBeanModel(sArray);

        final SummaryChargeMenuBean bean = new SummaryChargeMenuBean();
        bean.configure();
        bean.testOnly = true;
        bean.setModel(smbm);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
