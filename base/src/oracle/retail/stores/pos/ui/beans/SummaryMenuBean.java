/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryMenuBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/9/2007 3:07:54 PM    Anda D. Cadar   I18N
 *         changes for CR 27494: POS 1st initialization when Server is offline
 *    4    360Commerce 1.3         4/25/2007 8:51:26 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:56   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing imports
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    Displays summary totals for tenders.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $;
**/
//--------------------------------------------------------------------------
public class SummaryMenuBean extends BaseBeanAdapter
{
    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The bean model for this bean */
    protected SummaryMenuBeanModel beanModel = new SummaryMenuBeanModel();

    /** The label for the total field */
    protected static String TOTAL_TEXT = "Total:";

    /** Screen labels */
    protected JLabel[] fieldLabels = null;

    /** Screen fields */
    protected CurrencyTextField[] totalFields = null;

    //--------------------------------------------------------------------------
    /**
     *    Default constructor.
     */
    public SummaryMenuBean()
    {
        super();
        setName("SummaryMenuBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
    }

    //--------------------------------------------------------------------------
    /**
     *    Called when the panel is removed. 
     */
    public void deactivate()
    {
        for (int i=0; i<fieldLabels.length; i++)
        {
            remove(fieldLabels[i]);
            remove(totalFields[i]);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *    Calls methods to set up the labels, fields and button bar. 
     */
    protected void updateBean()
    {
        initComponents();
        UIUtilities.layoutDataPanel(this, fieldLabels, totalFields);
    }

    //--------------------------------------------------------------------------
    /**
     *    Creates the components at display time. The number of fields 
     *    and labels depends on the data in the model. 
     */
    protected void initComponents()
    {
        CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
        
        SummaryCountBeanModel counts[] = 
            beanModel.getSummaryCountBeanModel();
            
        int length = counts.length;
        
        fieldLabels = new JLabel[length + 1];
        totalFields = new CurrencyTextField[length + 1];
        
        for(int i=0; i<length; i++)
        {
            fieldLabels[i] = 
                uiFactory.createLabel(counts[i].getDescription(), null, UI_LABEL);
            
            total = total.add(counts[i].getAmount());
            totalFields[i] = createField();
            totalFields[i].setValue(counts[i].getAmount());
        }

        fieldLabels[length] = uiFactory.createLabel(TOTAL_TEXT, null, UI_LABEL);
        fieldLabels[length].setName("TotalLabel");
        fieldLabels[length].setHorizontalAlignment(JLabel.RIGHT);
        
        totalFields[length] = createField();
        totalFields[length].setValue(total);
    }

    //----------------------------------------------------------------------------
    /**
        Creates a currency display field.
    **/
    //----------------------------------------------------------------------------
    protected CurrencyTextField createField()
    {
        
        CurrencyTextField field = uiFactory.createCurrencyField("","true","true","false");

        field.setHorizontalAlignment(SwingConstants.RIGHT);
        field.setEditable(false);
        field.setEnabled(false);
        
        return field;
    }

    //----------------------------------------------------------------------------
    /**
        Gets the data from screen, moves it to the the bean model, returns
        the bean model. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI> setModel must be called first.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI> None.
        </UL>
    **/
    //----------------------------------------------------------------------------
    public void UpdateModel()
    {}

    //----------------------------------------------------------------------------
    /**
        Set the bean model into the bean. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI> none.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI> None.
        </UL>
        @param The bean model
    **/
    //----------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set SummaryMenuBean"
                                           + " to null");
        }
        if (model instanceof SummaryMenuBeanModel)
        {
            beanModel = (SummaryMenuBeanModel)model;
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
        String strResult = new String("Class: SummaryMenuBean (Revision " +
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
    **/
    //----------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        
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
        
        SummaryMenuBean bean = new SummaryMenuBean();
        bean.setModel(smbm);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
