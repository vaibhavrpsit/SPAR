/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/TransactionListPriceMaintenanceEvents.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:15 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
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
 *    Rev 1.0   Jun 03 2002 16:43:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:11:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceMaintenanceSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Handles the data transactions for price changes search.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class TransactionListPriceMaintenanceEvents extends DataTransaction
{
    private static final long serialVersionUID = -4607502431449027278L;

    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * transaction name
     */
    public static final String LIST_PRICE_MAINTENANCE_EVENTS = "ListPriceMaintenanceEvents";

    /**
     * Class constructor.
     * 
     * @param name data command name, must be on of the types above
     */
    public TransactionListPriceMaintenanceEvents()
    {
        super(LIST_PRICE_MAINTENANCE_EVENTS);
    }

    /**
     * Finds all the item price maintenance that match a certain search
     * criteria.
     * 
     * @param searchCriteria the search criteria
     * @exception DataException when an error occurs.
     */
    public ItemPriceMaintenanceEventIfc[] listPriceMaintenanceEvents(PriceMaintenanceSearchCriteriaIfc searchCriteria)
            throws DataException
    {
        // set data actions and execute
        applyDataObject(searchCriteria);

        // execute data request
        ItemPriceMaintenanceEventIfc[] events = (ItemPriceMaintenanceEventIfc[]) getDataManager().execute(this);

        return (events);
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
        return (Util.classToStringHeader("TransactionListPriceMaintenanceEvents", getRevisionNumber(), hashCode())
                .toString());
    }
}
