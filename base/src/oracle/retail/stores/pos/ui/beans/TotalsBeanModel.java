/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TotalsBeanModel.java /main/15 2014/06/02 18:02:53 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/02/14 - Add Amount Paid for order pick or order cancel in
 *                         show sale screen.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    blarsen   10/21/11 - The quantity displayed on the sale item screen
 *                         should not include returned items.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/23/10 - change compare to zero to using signum
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   6    360Commerce 1.5         6/12/2007 8:48:27 PM   Anda D. Cadar   SCR
 *        27207: Receipt changes -  proper alignment for amounts
 *   5    360Commerce 1.4         4/25/2007 8:51:25 AM   Anda D. Cadar   I18N
 *        merge
 *   4    360Commerce 1.3         1/22/2006 11:45:29 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:30:33 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:26:18 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:15:10 PM  Robert Pearse
 *
 *  Revision 1.7  2004/07/27 22:17:26  bwf
 *  @scr 6534 Set to "0.00" instead of "" to display in status on ShowSaleScreen.
 *
 *  Revision 1.6  2004/07/15 23:00:08  jriggins
 *  @scr 6309 Capture the absolute value of discount values for presentation reasons.
 *
 *  Revision 1.5  2004/04/05 18:59:16  rsachdeva
 *  @scr  3906  Sale
 *
 *  Revision 1.4  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/02 19:59:00  rsachdeva
 *  @scr 3906 Unit of Measure
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Sep 06 2002 17:25:38   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:19:04   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:16   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:36   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This is the bean model used by the Totals Beans.
 *
 * @version $Revision: /main/15 $
 * @see oracle.retail.stores.pos.ui.beans.SaleBean
 */
public class TotalsBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -1940415816932619939L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    /** The subtotal **/
    protected String subtotal = "";

    /** The discount total **/
    protected String discountTotal = "";

    /** The tax total **/
    protected String taxTotal = "";

    /** the tendered total */
    protected String tendered = "";

    /** the balance due */
    protected String balanceDue = "";

    /** The grand total **/
    protected String grandTotal = "";

    /** The total number of items **/
    protected BigDecimal quantityTotal = BigDecimal.ZERO;

    /** The payment of pickup or cancel order item */
    protected CurrencyIfc amountPaid = DomainGateway.getBaseCurrencyInstance();
    
    /**
     * all items unit of measure as units
     */
    protected boolean allItemUOMQtyDisplay = true;

    /**
     * TotalsBeanModel constructor.
     */
    public TotalsBeanModel()
    {
        super();
    }

    /**
     * Gets the subtotal for the current transaction.
     *
     * @return The subtotal property value.
     */
    public String getSubtotal()
    {
        return subtotal;
    }

    /**
     * Sets the subtotal for the current transaction.
     *
     * @param total The subtotal.
     */
    public void setSubtotal(String total)
    {
        subtotal = total;
    }

    /**
     * Gets the discount total for the current transaction.
     *
     * @return The dicount total property value.
     */
    public String getDiscountTotal()
    {
        return discountTotal;
    }

    /**
     * Sets the discount total for the current transaction.
     *
     * @param total The discount total.
     */
    public void setDiscountTotal(String total)
    {
        discountTotal = total;
    }

    /**
     * Gets the tax total for the current transaction.
     *
     * @return The tax total property value.
     */
    public String getTaxTotal()
    {
        return taxTotal;
    }

    /**
     * Sets the tax total for the current transaction.
     *
     * @param total The tax total.
     */
    public void setTaxTotal(String total)
    {
        taxTotal = total;
    }

    /**
     * Gets the tendered amount for the current transaction.
     *
     * @return The tendered property value.
     */
    public String getTendered()
    {
        return tendered;
    }

    /**
     * Sets the tendered amount for the current transaction.
     *
     * @param total The tendered total.
     */
    public void setTendered(String total)
    {
        tendered = total;
    }

    /**
     * Gets the balance due for the current transaction.
     *
     * @return The balance due property value.
     */
    public String getBalanceDue()
    {
        return balanceDue;
    }

    /**
     * Sets the balance due amount for the current transaction.
     *
     * @param total The balance due.
     */
    public void setBalanceDue(String total)
    {
        balanceDue = total;
    }

    /**
     * Gets the grand total for the current transaction.
     *
     * @return The grandt total property value.
     */
    public String getGrandTotal()
    {
        return grandTotal;
    }

    /**
     * Sets the grand total for the current transaction.
     *
     * @param total The grand total.
     */
    public void setGrandTotal(String total)
    {
        grandTotal = total;
    }

    /**
     * Gets the quantity total for the current transaction.
     *
     * @return The quantity total property value.
     */
    public BigDecimal getQuantityTotal()
    {
        return quantityTotal;
    }

    /**
     * Sets the grand total for the current transaction.
     *
     * @param total The grand total.
     */
    public void setQuantityTotal(BigDecimal total)
    {
        quantityTotal = total;
    }

    /**
     * All items having unit of measure as units
     *
     * @return boolean true if all items have unit of measure as units
     */
    public boolean isAllItemUOMQtyDisplay()
    {
        return this.allItemUOMQtyDisplay;
    }

    /**
     * If any item does not have unit of measure as units, this is set to false,
     * otherwise true.
     *
     * @param allItemUOMAsUnits unit of measure for all items
     */
    public void setAllItemUOMQtyDisplay(boolean allItemUOMQtyDisplay)
    {
        this.allItemUOMQtyDisplay = allItemUOMQtyDisplay;
    }

    /**
     * get amount paid
     * @return amount paid
     */
    public CurrencyIfc getAmountPaid()
    {
        return amountPaid;
    }

    /**
     * set amount paid
     * @param amountPaid
     */
    public void setAmountPaid(CurrencyIfc amountPaid) 
    {
        this.amountPaid = amountPaid;
    }
    
    /**
     * Sets amount totals and recalculate grand total.
     *
     * @param totals TransactionTotalsIfc, CurrencyIfc
     */
    public void setTotals(TransactionTotalsIfc totals, CurrencyIfc paid)
    {
        setTotals(totals);
        
        amountPaid = paid;
                
        grandTotal = totals.getGrandTotal().subtract(amountPaid).toFormattedString();
    }
    
    /**
     * Sets all the total data members from a TransactionTotalsIfc object.
     *
     * @param totals TransactionTotalsIfc
     */
    public void setTotals(TransactionTotalsIfc totals)
    {
        // on each total, display no value if zero, value if non-zero
        if (totals.getSubtotal().signum() == CurrencyIfc.ZERO)
        {
            subtotal = totals.getSubtotal().toFormattedString();
        }
        else
        {
            subtotal = totals.getUISubtotalAsConfigured().toFormattedString();
        }

        if (totals.getDiscountTotal().signum() == CurrencyIfc.ZERO)
        {
            discountTotal = totals.getDiscountTotal().toFormattedString();
        }
        else
        {
            discountTotal = totals.getDiscountTotal().abs().toFormattedString();
        }

        taxTotal = totals.getTaxTotal().toFormattedString();
        tendered = totals.getAmountTender().toFormattedString();
        balanceDue = totals.getBalanceDue().toFormattedString();
        grandTotal = totals.getGrandTotal().toFormattedString();

        quantityTotal = totals.getQuantitySale(); // do not include return items

        // check if all items have unit of measure as units
        allItemUOMQtyDisplay = totals.isAllItemUOMUnits();
    }

} // end TotalsBeanModel
