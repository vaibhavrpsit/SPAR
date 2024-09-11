/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/SpecialOrderCargo.java /main/14 2014/04/07 13:59:26 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/31/14 - Remove getAccessFunctionID.
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/27 22:32:05  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:52:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:07:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:01:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:58   msg
 * Initial revision.
 * 
 *    Rev 1.4   Jan 16 2002 11:17:32   dfh
 * changed rolefunction id SPECIAL_ORDER_CANCEL to CANCEL_ORDER
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.3   Dec 10 2001 18:44:02   cir
 * Added order as OrderIfc
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.2   Dec 06 2001 17:26:24   dfh
 * updates to prepare for security override, cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.1   Dec 04 2001 16:11:56   dfh
 * No change.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 14:48:22   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Data and methods common to the sites in Special Order Services.
 * 
 * @version $Revision: /main/14 $
 */
public class SpecialOrderCargo extends AbstractFinancialCargo implements SpecialOrderCargoIfc,
        RetailTransactionCargoIfc
{
    private static final long serialVersionUID = -7449698746936027735L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/14 $";
    
    /**
     * current special order customer
     */
    protected CustomerIfc customer = null;

    /**
     * Sales associate for the special order transaction
     */
    protected EmployeeIfc salesAssociate = null;

    /**
     * order transaction
     */
    protected OrderTransactionIfc orderTransaction = null;

    /**
     * order
     */
    protected OrderIfc order = null;
    
    public SpecialOrderCargo()
    {
        super();
        functionID = RoleFunctionIfc.CANCEL_ORDER;
    }

    /**
     * Gets the special order customer.
     * 
     * @return Customer
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * Sets the special order customer.
     * 
     * @param Customer
     */
    public void setCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Gets the current order transaction.
     * 
     * @return order transaction
     */
    public OrderTransactionIfc getOrderTransaction()
    {
        return orderTransaction;
    }

    /**
     * Sets the order transaction.
     * 
     * @param order transaction
     */
    public void setOrderTransaction(OrderTransactionIfc value)
    {
        orderTransaction = value;
    }

    /**
     * Gets the current order.
     * 
     * @return OrderIfc
     */
    public OrderIfc getOrder()
    {
        return order;
    }

    /**
     * Sets the order.
     * 
     * @param value as OrderIfc
     */
    public void setOrder(OrderIfc value)
    {
        order = value;
    }

    /**
     * Sets the sales associate.
     * 
     * @param EmployeeIfc the sales associate
     */
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

    /**
     * Gets the sales associate.
     * 
     * @return EmployeeIfc sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Gets the current tenderable tansaction.
     * 
     * @return tenderable transaction
     */
    public TenderableTransactionIfc getTenderableTransaction()
    {
        return orderTransaction;
    }

    /**
     * Retrieve the till ID.
     * 
     * @return String till ID
     */
    public String getTillID()
    {
        return (orderTransaction.getTillID());
    }

    /**
     * Retrieves the saved transaction
     * 
     * @return the RetailTransactionIfc that is being printed
     */
    public RetailTransactionIfc getRetailTransaction()
    {
        return orderTransaction;
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     * 
     * @return OrderTransaction[]
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        // Returns null because a special order can never have return items.
        return (null);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = Util.classToStringHeader("SpecialOrderCargo", getRevisionNumber(), hashCode());
        if (getCustomer() == null)
        {
            strResult.append(Util.formatToStringEntry("customer", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("customer", getCustomer()));
        }
        if (getSalesAssociate() == null)
        {
            strResult.append(Util.formatToStringEntry("salesAssociate", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("salesAssociate", getSalesAssociate()));
        }
        if (getOrderTransaction() == null)
        {
            strResult.append(Util.formatToStringEntry("orderTransaction", "null"));
        }
        else
        {
            strResult.append(Util.formatToStringEntry("orderTransaction", getOrderTransaction()));
        }

        // pass back result
        return (strResult.toString());

    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
