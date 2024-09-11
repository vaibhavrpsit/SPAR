/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TransactionReadItemCost.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 10:01:05 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:16 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:43:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:58   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:11:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 20 2002 15:03:56   cdb
 * Corrected defect in ReadItemCost transaction - The result was being set in a "remote" object, so in an n-tiered environment, the original object was never updated.
 * Resolution for Backoffice SCR-586: Item's cost is not saved in db.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Handles the data transactions for read item cost.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TransactionReadItemCost extends DataTransaction
{
    private static final long serialVersionUID = 5630729596850028781L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // transaction name
    public static final String READ_ITEM_COST = "ReadItemCost";
    
    /**
     * Class constructor.
     */
    public TransactionReadItemCost()
    {
        super(READ_ITEM_COST);
    }

    /**
     * Reads the cost for an item.
     * 
     * @param itemObj the item object for which we get the cost.
     * @exception DataException when an error occurs.
     */
    public void readItemCost(ItemIfc itemObj) throws DataException
    {
        // set data actions and execute
        applyDataObject(itemObj);

        // execute data request
        CurrencyIfc itemCost = (CurrencyIfc) getDataManager().execute(this);
        itemObj.setItemCost(itemCost);
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return (Util.classToStringHeader("TransactionReadItemCost", getRevisionNumber(), hashCode()).toString());
    }
}
