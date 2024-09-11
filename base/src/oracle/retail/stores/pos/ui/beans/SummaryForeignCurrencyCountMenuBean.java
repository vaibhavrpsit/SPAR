/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryForeignCurrencyCountMenuBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
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
 *     6    I18N_P2    1.4.1.0     1/8/2008 2:56:48 PM    Sandy Gu        Set
 *          max length of constraied text field.
 *     5    360Commerce 1.4         5/21/2007 1:02:07 PM   Anda D. Cadar   use
 *          ConstrainedTextField for currency display
 *     4    360Commerce 1.3         4/25/2007 8:51:27 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:15 PM   Robert Pearse
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

// java imports
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    Allows the user to select a foreign tender to work with.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SummaryForeignCurrencyCountMenuBean extends BaseBeanAdapter
{
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The bean model for this bean */
    protected SummaryForeignCurrencyCountMenuBeanModel beanModel =
        new SummaryForeignCurrencyCountMenuBeanModel();

    /** Screen labels */
    protected JLabel[] fieldLabels = null;

    /** Screen fields */
    protected JComponent[] totalFields = null;

    /** Indicates if this class was started from the Main method in this class. */
    public boolean testOnly = false;

    //---------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    //---------------------------------------------------------------------------
    public SummaryForeignCurrencyCountMenuBean()
    {
        super();
    }

    //---------------------------------------------------------------------------
    /**
     * Configures the class.
     */
    //---------------------------------------------------------------------------
    public void configure()
    {
        setName("SummaryForeignCurrencyCountMenuBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
    }

    //----------------------------------------------------------------------------
    /**
     *  Called when the panel is removed.
     */
    //---------------------------------------------------------------------------
    public void deactivate()
    {
        removeAll();
    }

    //----------------------------------------------------------------------------
    /**
     *  Updates the display with data from the model. This will create
     *  labels and fields based on number of summary counts in the model.
     */
    //----------------------------------------------------------------------------
    protected void updateBean()
    {
        this.removeAll();
        initComponents();
        initLayout();
    }

    //---------------------------------------------------------------------------
    /**
     *  Creates the labels and fields for the summary counts.
     */
    //---------------------------------------------------------------------------
    protected void initComponents()
    {
        setLayout(new GridBagLayout());
        CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
        SummaryCountBeanModel sc[] = beanModel.getSummaryCountBeanModel();
        int spacerPos = sc.length;
        int totalPos = sc.length + 1;
        fieldLabels  = new JLabel[sc.length];
        totalFields  = new JComponent[sc.length];
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        for(int cnt = 0; cnt < sc.length; cnt++)
        {
            String fieldName = sc[cnt].getDescription() + "AmountField";
            String labelText = retrieveText(sc[cnt].getLabelTag(),
                                            sc[cnt].getLabel());
            fieldLabels[cnt] = uiFactory.createLabel(labelText, null, UI_LABEL);

           /* CurrencyTextField currencyField =
                uiFactory.createCurrencyField(fieldName, "true","true", "true");*/
            ConstrainedTextField  currencyField = uiFactory.createConstrainedField(fieldName, "1", "10", true);

            currencyField.setEditable(false);
            currencyField.setEnabled(false);
            currencyField.setText(sc[cnt].getAmount().toFormattedString());
            totalFields[cnt] = currencyField;
        }
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
            // column 1 labels & fields
            if (fieldLabels != null)
            {
                for(int i = 0; i < sc.length; i++)
                {
                    fieldLabels[i].setText(retrieveText(sc[i].getLabelTag(),
                                                        sc[i].getLabel()));
                }
            }
        }

    }                                   // end updatePropertyFields()

    //--------------------------------------------------------------------------
    /**
        Lays out the components. <P>
    **/
    //--------------------------------------------------------------------------
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, fieldLabels, totalFields);
    }

    //----------------------------------------------------------------------
    /**
        Set the bean model into the bean.
        @param model The bean model.
    **/
    //----------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set SummaryForeignCurrencyCountMenuBeanModel"
                                           + " to null");
        }
        if (model instanceof SummaryForeignCurrencyCountMenuBeanModel)
        {
            beanModel = (SummaryForeignCurrencyCountMenuBeanModel)model;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the revision number. <P>
       @return String representation of revision number
    **/
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
        String strResult = new String("Class: SummaryForeignCurrencyCountMenuBean(Revision " +
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
        @param args[]  The command line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        SummaryCountBeanModel sArray[] = new SummaryCountBeanModel[5];

        SummaryCountBeanModel scbm   = new SummaryCountBeanModel();
        scbm.setDescription("Canadian Dollars");
        scbm.setLabel("Canadian Dollars:");
        CurrencyIfc c = DomainGateway.getAlternateCurrencyInstance("CAD");
        c.setDecimalValue(new BigDecimal("123.47"));
        scbm.setAmount(c);
        sArray[0] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Mexican Pesos");
        scbm.setLabel("Mexican Pesos:");
        c = DomainGateway.getAlternateCurrencyInstance("MXN");
        c.setDecimalValue(new BigDecimal("223.47"));
        scbm.setAmount(c);
        sArray[1] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("U.K. Pounds");
        scbm.setLabel("U.K. Pounds:");
        c = DomainGateway.getAlternateCurrencyInstance("GBP");
        c.setDecimalValue(new BigDecimal("323.47"));
        scbm.setAmount(c);
        sArray[2] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("E.U. Euros");
        scbm.setLabel("E.U. Euros:");
        c = DomainGateway.getAlternateCurrencyInstance("EUR");
        c.setDecimalValue(new BigDecimal("423.47"));
        scbm.setAmount(c);
        sArray[3] = scbm;

        scbm = new SummaryCountBeanModel();
        scbm.setDescription("Japanese Yen");
        scbm.setLabel("Japanese Yen:");
        c = DomainGateway.getAlternateCurrencyInstance("JPY");
        c.setDecimalValue(new BigDecimal("523.47"));

        scbm.setAmount(c);
        sArray[4] = scbm;

        SummaryForeignCurrencyCountMenuBeanModel smbm = new SummaryForeignCurrencyCountMenuBeanModel();
        smbm.setSummaryCountBeanModel(sArray);

        final SummaryForeignCurrencyCountMenuBean bean = new SummaryForeignCurrencyCountMenuBean();
        bean.configure();
        bean.testOnly = true;
        bean.setModel(smbm);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
