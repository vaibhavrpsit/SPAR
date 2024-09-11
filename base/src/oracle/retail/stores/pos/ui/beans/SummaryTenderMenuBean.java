/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryTenderMenuBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/01/10 - use default locale for currency display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       02/02/09 - allow double byte space for french locale
 *
 * ===========================================================================
 * $Log:
 *     8    I18N_P2    1.6.1.0     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *          max length of constraied text field.
 *     7    360Commerce 1.6         7/9/2007 3:07:54 PM    Anda D. Cadar   I18N
 *           changes for CR 27494: POS 1st initialization when Server is
 *          offline
 *     6    360Commerce 1.5         5/21/2007 1:02:07 PM   Anda D. Cadar   use
 *          ConstrainedTextField for currency display
 *     5    360Commerce 1.4         5/18/2007 9:18:15 AM   Anda D. Cadar   EJ
 *          and currency UI changes
 *     4    360Commerce 1.3         4/25/2007 8:51:26 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse
 *    $
 *    Revision 1.6  2004/06/29 17:05:38  cdb
 *    @scr 4205 Removed merging of money orders into checks.
 *    Added ability to count money orders at till reconcile.
 *
 *    Revision 1.5  2004/06/07 18:29:37  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency counts.
 *
 *    Revision 1.4  2004/05/20 20:40:53  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Removed alternate tender from Select Tender screen and
 *    corrected Select Charge screen.
 *
 *    Revision 1.3  2004/03/16 17:15:18  build
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 20:56:26  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.11   Jun 18 2003 12:56:14   bwf
 * Add tenderCurrencyCountryCode.
 * Resolution for 2613: Internationalization: try to print till summary report, POS client hangs up.
 *
 *    Rev 1.10   May 20 2003 16:29:16   adc
 * Changes to support alternate currencies
 * Resolution for 2229: Register Close Till Reports not reporting Canadian Tender
 *
 *    Rev 1.9   Apr 18 2003 09:42:38   baa
 * fixes to bundles
 * Resolution for POS SCR-2170: Missing property names in bundles
 *
 *    Rev 1.8   Jan 16 2003 14:15:38   crain
 * Added a call to removeAll() in initialize
 * Resolution for 1911: Returning from Help to Currency Detail changes the UI
 *
 *    Rev 1.7   Dec 09 2002 15:10:46   DCobb
 * Fixed expected amounts for alternate currencies.
 * Resolution for POS SCR-1852: Multiple defects on Till Close Select Tenders screen funtionality.
 *
 *    Rev 1.6   Nov 27 2002 15:55:58   DCobb
 * Add Canadian Check tender.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 *
 *    Rev 1.5   Nov 26 2002 17:41:36   kmorneau
 * fix display of expected amounts for blind close - now using fields
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.4   Nov 18 2002 13:42:42   kmorneau
 * added ui functionality for display of expected amounts for non-blind close
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.3   Sep 19 2002 11:47:14   DCobb
 * Add Purchase Order tender type.
 * Resolution for POS SCR-1799: POS 5.5 Purchase Order Tender Package
 *
 *    Rev 1.2   Sep 03 2002 16:08:04   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:18:58   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:52:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:56   msg
 * Initial revision.
 *
 *    Rev 1.7   13 Mar 2002 16:49:02   epd
 * fixed label problem.
 * This will need to be changed, however, to support internationalisation
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.6   Mar 07 2002 14:53:02   mpm
 * Text externalization for till UI screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.5   18 Feb 2002 11:30:04   baa
 * ui changes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.4   16 Feb 2002 18:15:58   baa
 * more ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   15 Feb 2002 16:33:32   baa
 * ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.2   Jan 23 2002 07:40:42   mpm
 * Corrected problems with labels, updateBean().  This bean is still not PLAF-ed.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   Jan 19 2002 10:32:20   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   02 Jan 2002 15:37:32   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    Allows the usr to select a tender to work with.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SummaryTenderMenuBean extends BaseBeanAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 2578328344430004023L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        The bean model for this bean
    **/
    protected SummaryTenderMenuBeanModel beanModel = new SummaryTenderMenuBeanModel();
    /**
        The label for the total field
    **/
    protected static final String TOTAL_TEXT         = "Total:";
    /**
        The tag for the total field label
    **/
    protected static final String TOTAL_LABEL         = "TotalLabel";
    /**
        Screen labels
    **/
    protected JLabel[]            fieldLabels = null;
    /**
        Screen fields
    **/
    protected JComponent[] totalFields = null;
    /**
        Indicates if this class was started from the Main method in this class.
    **/
    public boolean              testOnly    = false;

    //----------------------------------------------------------------------------
    /**
        Constructs the bean.
    **/
    //----------------------------------------------------------------------------
    public SummaryTenderMenuBean()
    {
        super();
        setName("SummaryTenderMenuBean");
    }

    //----------------------------------------------------------------------------
    /**
        Calls methods to set up the labels, fields and button bar. <P>
    **/
    //----------------------------------------------------------------------------
    protected void initialize()
    {
        removeAll();

        uiFactory.configureUIComponent(this, UI_PREFIX);

        setLayout(new GridBagLayout());

        SummaryCountBeanModel sc[] = beanModel.getSummaryCountBeanModel();
        CurrencyIfc total          = null;

        try
        {
            total =  DomainGateway.getBaseCurrencyInstance();
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database or server may be offline, using default number of fraction digits", e);
        }


        fieldLabels                = new JLabel[sc.length + 2];
        totalFields                = new JComponent[sc.length + 2];
        String labelText = null;

        // labels and fields for base currency
        for(int i = 0; i < sc.length; i++)
        {
            if (total != null)
            {
                total = total.add(sc[i].getAmount());
            }

            // create label and field
            labelText = retrieveText(sc[i].getLabelTag(),
                                     sc[i].getLabel());
            fieldLabels[i] = uiFactory.createLabel(labelText, null, UI_LABEL);


            ConstrainedTextField  currencyField = uiFactory.createConstrainedField(sc[i].getDescription() +  "AmountField", "1", "10", true);


            currencyField.setText(sc[i].getAmount().toFormattedString());
            currencyField.setEditable(false);

            currencyField.setEnabled(false);
            totalFields[i] = currencyField;

            if (sc[i].isFieldDisabled())
            {
                totalFields[i].setEnabled(false);
            }

            // layout label field pair
            int col = 0;
            int row = i;

            UIUtilities.layoutComponent(this,fieldLabels[i],totalFields[i],col,row,false);
        }

        // blank line - There must be at least one character in order for the line to show
        int pos = sc.length;

        fieldLabels[pos] = uiFactory.createLabel(" ", null, UI_LABEL);
        JLabel spacer = uiFactory.createLabel("", null, UI_LABEL);
        spacer.setEnabled(false);
        spacer.setVisible(true);
        totalFields[pos] = spacer;

        // total field
        int totalPos = sc.length + 1;

        labelText = retrieveText(TOTAL_LABEL, TOTAL_TEXT);
        fieldLabels[totalPos] = uiFactory.createLabel(labelText, null, UI_LABEL);
        ConstrainedTextField  currencyField = uiFactory.createConstrainedField(TOTAL_TEXT +  "AmountField", "1", "10", true);

        currencyField.setEditable(false);
        if (total != null)
        {
            currencyField.setText(total.toFormattedString());
        }

        currencyField.setEnabled(false);
        totalFields[totalPos] = currencyField;

        UIUtilities.layoutDataPanel(this,fieldLabels,totalFields);
    }

    //----------------------------------------------------------------------------
    /**
        Adds the counted amout fields to the panel; the number of fields and
        their values depends on the data in the model. <P>
    **/
    //----------------------------------------------------------------------------
    protected void updateBean()
    {
      initialize();
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    public void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        if (beanModel != null)
        {
            SummaryCountBeanModel[] sc = beanModel.getSummaryCountBeanModel();

            if (fieldLabels != null)
            {
                for(int i = 0; i < sc.length; i++)
                {
                    fieldLabels[i].setText(retrieveText(sc[i].getLabelTag(),
                                                        sc[i].getLabel()));
                }

                int totalPos = sc.length + 1;
                fieldLabels[totalPos].setText(retrieveText(TOTAL_LABEL,TOTAL_TEXT));
            }
        }

    }                                   // end updatePropertyFields()

    //----------------------------------------------------------------------------
    /**
        Set the bean model into the bean. <P>
        @param model  The bean model
    **/
    //----------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set SummaryTenderMenuBean"
                                           + " to null");
        }
        if (model instanceof SummaryTenderMenuBeanModel)
        {
            beanModel = (SummaryTenderMenuBeanModel)model;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: SummaryTenderMenuBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------------
    /**
        Displays the bean for test purposes. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI> none.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI> None.
        </UL>
        @param args  command-line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String[] args)
    {
        SummaryCountBeanModel sArray[] = new SummaryCountBeanModel[8];

        SummaryCountBeanModel scbm   = new SummaryCountBeanModel();
        scbm.setDescription("Cash");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("123.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }
        sArray[0] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Charge");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("223.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }
        sArray[1] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Check");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("323.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }
        sArray[2] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Gift Certificate");
        scbm.setAmount(DomainGateway.getBaseCurrencyInstance("423.47"));
        sArray[3] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Store Credit");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("523.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }
        sArray[4] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Traveler's Check");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("623.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }

        sArray[5] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Manufacturer's Coupon");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("723.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }
        sArray[6] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Retailer's Coupon");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("823.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }
        sArray[7] = scbm;

        SummaryMenuBeanModel smbm = new SummaryMenuBeanModel();
        smbm.setSummaryCountBeanModel(sArray);
        javax.swing.JFrame jframe = new JFrame();

        final SummaryTenderMenuBean bean = new SummaryTenderMenuBean();
        bean.testOnly = true;
        bean.setModel(smbm);
        bean.activate();

        jframe.setSize(bean.getSize());
        jframe.getContentPane().add(bean);
        jframe.setVisible(true);
    }
}
