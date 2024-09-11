/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SaleTotalsBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
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
 *    acadar    04/12/10 - use default locale for display of currency
 *    acadar    04/06/10 - use default locale for currency display
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
 *   3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:25:01 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:02 PM  Robert Pearse
 *
 *  Revision 1.5  2004/04/21 20:35:44  rsachdeva
 *  @scr 3906 Comment Added
 *
 *  Revision 1.4  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/02 20:31:32  rsachdeva
 *  @scr  3906 Unit of Measure
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Sep 06 2002 17:25:36   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:18:32   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:49:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:26   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 25 2002 10:51:14   mpm
 * Internationalization
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import javax.swing.JTextField;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import java.math.BigDecimal;
//------------------------------------------------------------------------------
/**
 *  This bean is used to display a series of summary fields below
 *  the Sell Item List.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class SaleTotalsBean extends AbstractTotalsBean
{
    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int SUBTOTAL = 0;
    public static final int DISCOUNT = 1;
    public static final int TAX = 2;
    public static final int QUANTITY = 3;
    public static final int TOTAL = 4;
    public static final int FIELD_COUNT = 5;

    // header labels for the fields (should eventually come from properties)
    public static final String SALE_LABELS = "Subtotal,Discount,Tax,Quantity,Total";
    public static final String SALE_LABEL_TAGS = "SubtotalLabel,DiscountLabel,TaxLabel,QuantityLabel,TotalLabel";
    public static final String SALE_WEIGHTS = "20,20,20,20,20";



    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public SaleTotalsBean()
    {
        super();
        setLabelText(SALE_LABELS);
        setLabelTags(SALE_LABEL_TAGS);
        setLabelWeights(SALE_WEIGHTS);
    }

    //--------------------------------------------------------------------------
    /**
     * Initialize the components.
     */
    protected void initFields()
    {
        fields = new JTextField[FIELD_COUNT];
        fields[SUBTOTAL]    = uiFactory.createTextField("Subtotal");
        fields[DISCOUNT]    = uiFactory.createTextField("Discount");
        fields[TAX]         = uiFactory.createTextField("Tax");
        fields[QUANTITY]    = uiFactory.createTextField("Quantity");
        fields[TOTAL]       = uiFactory.createTextField("Total");

        for(int i=0; i<FIELD_COUNT; i++)
        {
            configureField(fields[i]);
        }
        updatePropertyFields();
    }

    //--------------------------------------------------------------------------
    /**
     *  Updates the totals on the totals bean.
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

        if(beanModel.getQuantityTotal().signum() > 0)
        {
            if (beanModel.isAllItemUOMQtyDisplay())
            {
                //since all items having unit of measure as units,
                //so displayed as a number
                fields[QUANTITY].setText(LocaleUtilities.formatNumber(beanModel.getQuantityTotal(),
                                                                      getLocale()));
            }
            else
            {
                //since at least one item not having unit of measure as units,
                //so displayed as decimal number
                fields[QUANTITY].setText(LocaleUtilities.formatDecimal(beanModel.getQuantityTotal(), getLocale()));
            }
        }
        else
        {
            fields[QUANTITY].setText("");
        }

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
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        SaleTotalsBean bean = new SaleTotalsBean();

        TotalsBeanModel totals = new TotalsBeanModel();
        totals.setSubtotal("49.99");
        totals.setDiscountTotal("1.00");
        totals.setTaxTotal("4.12");
        totals.setGrandTotal("54.11");
        totals.setQuantityTotal(new BigDecimal("25.00"));

        bean.setModel(totals);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
