/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TenderTotalsBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   6    360Commerce 1.5         6/4/2007 2:38:54 PM    Alan N. Sinton  CR
 *        26483 - Changes per review comments.
 *   5    360Commerce 1.4         4/30/2007 3:45:30 PM   Alan N. Sinton  Merge
 *        from v12.0_temp.
 *   4    360Commerce 1.3         1/22/2006 11:45:29 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:26:05 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:57 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 08 2003 17:30:46   DCobb
 * Migration to jvm 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Sep 06 2002 17:25:36   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 14 2002 18:19:02   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:55:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:10   msg
 * Initial revision.
 * 
 *    Rev 1.5   Mar 01 2002 10:02:58   mpm
 * Internationalization of tender-related screens
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Feb 05 2002 16:44:02   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.3   30 Jan 2002 16:42:56   baa
 * ui fixes
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import javax.swing.JTextField;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import java.math.BigDecimal;

//------------------------------------------------------------------------------
/**
 *  Totals display object for the tender screen. The model for this class
 *  is oracle.retail.stores.domain.transaction.TransactionTotals.
 *  @see oracle.retail.stores.domain.transaction.TransactionTotals.
 *  @version $Log:
 *  @version  6    360Commerce 1.5         6/4/2007 2:38:54 PM    Alan N.
 *  @version       Sinton  CR 26483 - Changes per review comments.
 *  @version  5    360Commerce 1.4         4/30/2007 3:45:30 PM   Alan N.
 *  @version       Sinton  Merge from v12.0_temp.
 *  @version  4    360Commerce 1.3         1/22/2006 11:45:29 AM  Ron W. Haight
 *  @version          removed references to com.ibm.math.BigDecimal
 *  @version  3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse
 *  @version          
 *  @version  2    360Commerce 1.1         3/10/2005 10:26:05 AM  Robert Pearse
 *  @version          
 *  @version  1    360Commerce 1.0         2/11/2005 12:14:57 PM  Robert Pearse
 *  @version          
 *  @version $
 *  @version Revision 1.3  2004/03/16 17:15:18  build
 *  @version Forcing head revision
 *  @version
 *  @version Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @version @scr 0 Log4J conversion and code cleanup
 *  @version
 *  @version Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  @version updating to pvcs 360store-current
 *  @version
 *  @version
 */
//------------------------------------------------------------------------------
public class TenderTotalsBean extends AbstractTotalsBean
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int SUBTOTAL    = 0;
    public static final int DISCOUNT    = 1;
    public static final int TAX         = 2;
    public static final int TOTAL       = 3;
    public static final int TENDERED    = 4;
    public static final int BALANCEDUE  = 5;
    public static final int FIELD_COUNT = 6;

    // header labels for the fields (should eventually come from properties)
    public static final String TOTAL_LABELS = "Subtotal,Discount,Tax,Total,Tendered,Balance Due";
    public static final String TOTAL_LABEL_TAGS = "SubtotalLabel,DiscountLabel,TaxLabel,TotalLabel,TenderedLabel,BalanceDueLabel";
    public static final String TOTAL_WEIGHTS = "16,16,16,16,16,16";

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public TenderTotalsBean()
    {
        super();
        setLabelText(TOTAL_LABELS);
        setLabelTags(TOTAL_LABEL_TAGS);
        setLabelWeights(TOTAL_WEIGHTS);
    }

    //--------------------------------------------------------------------------
    /**
     * Initialize the components.
     */
    protected void initFields()
    {
        fields = new JTextField[FIELD_COUNT];

        for(int i=0; i<FIELD_COUNT; i++)
        {
            fields[i] = uiFactory.createTextField("");
            configureField(fields[i]);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Updates the displayed values from the bean model.
     */
    public void updateBean()
    {
        fields[SUBTOTAL].setText(beanModel.getSubtotal());
        fields[DISCOUNT].setText(beanModel.getDiscountTotal());
        // If in tax inclusive environment, do not display tax amount 
        if(AbstractTotalsBean.taxInclusiveFlag)
        {
            fields[TAX].setText("");
            super.setOneLabel(TAX, "");
        }
        else
        {
            fields[TAX].setText(beanModel.getTaxTotal());
        }
        fields[TOTAL].setText(beanModel.getGrandTotal());
        fields[TENDERED].setText(beanModel.getTendered());
        fields[BALANCEDUE].setText(beanModel.getBalanceDue());
    }

    //--------------------------------------------------------------------------
    /**
     *  Returns default display string.
     *  @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());

    }

    //--------------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number.
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    /**
     *  Main entry point for testing.
     * @param args java.lang.String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        TenderTotalsBean bean = new TenderTotalsBean();

        TotalsBeanModel totals = new TotalsBeanModel();

        totals.setSubtotal("49.99");
        totals.setDiscountTotal("1.00");
        totals.setTaxTotal("4.12");
        totals.setTendered("8.81");
        totals.setBalanceDue("45.22");
        totals.setGrandTotal("54.11");
        totals.setQuantityTotal(new BigDecimal("25.00"));

        bean.setModel(totals);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
