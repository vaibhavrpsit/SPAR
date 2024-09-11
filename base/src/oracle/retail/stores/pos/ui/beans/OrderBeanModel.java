/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:51 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:11:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:54:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:30   msg
 * Initial revision.
 * 
 *    Rev 1.3   Jan 19 2002 10:31:10   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   13 Nov 2001 07:16:26   mpm
 * Implemented ItemContainerProxy.
 * Resolution for POS SCR-8: Item Kits
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 11:19:22   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

//--------------------------------------------------------------------------
/**
 * This is the bean model used by the OrderBean. <P>
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.pos.ui.beans.OrderBean
 */
//--------------------------------------------------------------------------
public class OrderBeanModel extends POSBaseBeanModel
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    private     OrderIfc                fieldOrder              = null;
    private     String                  promptArgument          = null;
    private     boolean                 multiSelectEnabled      = false;
    private     boolean                 pickupEnabled           = false;

    //----------------------------------------------------------------------
    /**
     * Model requires OrderIfc.
     */
    //----------------------------------------------------------------------
    public OrderBeanModel(OrderIfc order)
    {
        fieldOrder = order;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the OrderLineItems.<P>
     * At the conclusion of the deprecation period, this method will be changed
     * to return an array of {@link AbstractTransactionLineItemIfc} objects.
     * @return The OrderLineItems property value.
     * @deprecated As of release 5.0.0, replaced by {@link #getOrderLineItems()}
     */
    //----------------------------------------------------------------------
    public AbstractTransactionLineItemIfc[] getLineItems()
    {
        return(fieldOrder.getLineItems());
    }

    //----------------------------------------------------------------------
    /**
     * Gets the OrderLineItems.
     * @return The OrderLineItems property value.
     */
    //----------------------------------------------------------------------
    public SaleReturnLineItemIfc[] getOrderLineItems()
    {
        AbstractTransactionLineItemIfc [] abstractLineItems = getLineItems();
        int arrayLength = abstractLineItems.length;
        SaleReturnLineItemIfc [] lineItems = new SaleReturnLineItemIfc[arrayLength];
        for (int i = 0; i < arrayLength; i++)
        {
            lineItems[i] = (SaleReturnLineItemIfc) abstractLineItems[i];
        }

        return(lineItems);
    }

    //----------------------------------------------------------------------
    /**
     * Gets the TransactionTotals from the order.
     * @return Totals of the order held by this model.
     */
    //----------------------------------------------------------------------
    public TransactionTotalsIfc getTotals()
    {
        return fieldOrder.getTotals();
    }

    //----------------------------------------------------------------------
    /**
     * Gets the transactionDiscounts property value.
     * @return The transactionDiscounts property value.
     * @see #setTransactionDiscounts
     */
    //----------------------------------------------------------------------
    public TransactionDiscountStrategyIfc[] getTransactionDiscounts()
    {
        return fieldOrder.getTransactionDiscounts();
    }

    //---------------------------------------------------------------------
    /**
        Returns transaction tax property value. <P>
        @return the transaction tax property value.
        @see #setTransactionTaxIfc
    **/
    //---------------------------------------------------------------------
    public TransactionTaxIfc getTransactionTax()
    {
        return fieldOrder.getTransactionTax();
    }
    //---------------------------------------------------------------------
    /**
        Returns the pickedUpEnabled boolean. <P>
        @return boolean.
    **/
    //---------------------------------------------------------------------
    public boolean getPickupEnabled()
    {
        return(pickupEnabled);
    }
    //---------------------------------------------------------------------
    /**
        Enables/Disables the next button on the Navigation bar. <P>
        @param boolean.
    **/
    //---------------------------------------------------------------------
    public void setPickupEnabled(boolean value)
    {
        pickupEnabled = value;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the prompt argument string.
     */
    //----------------------------------------------------------------------
    public void setPromptArgument(String arg)
    {
        promptArgument = arg;
    }

    //----------------------------------------------------------------------
    /**
     * Returns the prompt argument string.
     * @return String argument.
     */
    //----------------------------------------------------------------------
    public String getPromptArgument()
    {
        return promptArgument;
    }
}
