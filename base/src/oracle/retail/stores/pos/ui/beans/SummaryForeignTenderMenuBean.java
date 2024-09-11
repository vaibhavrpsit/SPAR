/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryForeignTenderMenuBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
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
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
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
 *     4    360Commerce 1.3         4/25/2007 8:51:27 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse
 *    $
 *    Revision 1.1  2004/06/07 18:29:37  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency counts.
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
public class SummaryForeignTenderMenuBean extends BaseBeanAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -8859153657936825999L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        The bean model for this bean
    **/
    protected SummaryForeignTenderMenuBeanModel beanModel = new SummaryForeignTenderMenuBeanModel();
    /**
        The label for the total field
    **/
    protected static final String TOTAL_TEXT = "Total:";
    /**
        The tag for the total field label
    **/
    protected static final String TOTAL_LABEL = "TotalLabel";
    /**
        Screen labels
    **/
    protected JLabel[] fieldLabels = null;
    /**
        Screen fields
    **/
    protected JComponent[] totalFields = null;
    /**
        Indicates if this class was started from the Main method in this class.
    **/
    public boolean testOnly = false;

    //----------------------------------------------------------------------------
    /**
        Constructs the bean.
    **/
    //----------------------------------------------------------------------------
    public SummaryForeignTenderMenuBean()
    {
        super();
        setName("SummaryForeignTenderMenuBean");
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
        String[] tendersToCount    = beanModel.getTendersToCount();
        String[] tendersAccepted   = beanModel.getTendersAccepted();
        CurrencyIfc total          = null;
        try
        {
            total = DomainGateway.getAlternateCurrencyInstance(beanModel.getTenderCurrencyCountryCode());
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, database/server may be offline");

        }

        fieldLabels                = new JLabel[sc.length + 2];
        totalFields                = new JComponent[sc.length + 2];
        String labelText = null;

        // labels and fields for foreign currency
        for(int i = 0; i < sc.length; i++)
        {
            String tender = sc[i].getDescription();
            if (isTenderListed(tender, tendersAccepted))
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

                if (sc[i].isFieldDisabled())
                {
                    currencyField.setEnabled(false);
                }
                else
                {
                    currencyField.setEnabled(true);
                }

                totalFields[i] = currencyField;

                // layout label field pair
                int col = 0;
                int row = i;

                UIUtilities.layoutComponent(this,fieldLabels[i],totalFields[i],col,row,false);
            }
            else
            {
                // invisible row
                fieldLabels[i] = uiFactory.createLabel("", null, UI_LABEL);
                JLabel noSpacer = uiFactory.createLabel("", null, UI_LABEL);
                noSpacer.setEnabled(false);
                noSpacer.setVisible(true);
                totalFields[i] = noSpacer;
            }
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

        ConstrainedTextField currencyField = uiFactory.createConstrainedField(TOTAL_TEXT + "Amount Field", "1", "10", true);
        if(total != null)
        {
            currencyField.setText(total.toFormattedString());
        }
        currencyField.setEditable(false);
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
            throw new NullPointerException("Attempt to set SummaryForeignTenderMenuBean"
                                           + " model to null");
        }
        if (model instanceof SummaryForeignTenderMenuBeanModel)
        {
            beanModel = (SummaryForeignTenderMenuBeanModel)model;
            updateBean();
        }
    }

    //----------------------------------------------------------------------
    /**
        Determines if the specified tender is listed in the tenders to count.
        <P>
        @param tender  The tender to look for
        @param list    The list of tenders to count
        @return true   if the specified currency is listed, false otherwise.
    **/
    //----------------------------------------------------------------------
    protected boolean isTenderListed(String tender, String[] list)
    {
        boolean enable = false;
        if ( list != null)
        {
            for (int i=0; i < list.length; i++)
            {
                if (tender.equals(list[i]))
                {
                    enable = true;
                    break;
                }
            }
        }
        return enable;
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
        String strResult = new String("Class: SummaryForeignTenderMenuBean (Revision " +
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
        scbm.setDescription("Gift Certificate");
        try
        {
            scbm.setAmount(DomainGateway.getBaseCurrencyInstance("423.47"));
        }
        catch (Exception e)
        {
            logger.error("Currency Information is not available, server or database connectivity problems");
        }
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

        SummaryForeignTenderMenuBeanModel smbm = new SummaryForeignTenderMenuBeanModel();
        smbm.setSummaryCountBeanModel(sArray);
        javax.swing.JFrame jframe = new JFrame();

        final SummaryForeignTenderMenuBean bean = new SummaryForeignTenderMenuBean();
        bean.testOnly = true;
        bean.setModel(smbm);
        bean.activate();

        jframe.setSize(bean.getSize());
        jframe.getContentPane().add(bean);
        jframe.setVisible(true);
    }
}
