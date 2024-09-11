/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/OrderWriteDataTransaction.java /main/12 2013/06/24 12:27:17 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/19/13 - Modified to perform the status update of an Order in
 *                         the context of a transaction.
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/27/2006 7:26:59 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    3    360Commerce 1.2         3/31/2005 4:29:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:54 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/10/06 02:44:17  mweis
 *   @scr 7012 Special and Web Orders now have Inventory.
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:33:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:42:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:50:44   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 03 2002 14:01:30   mpm
 * Changes to support inventory movement in order transactions.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.0   Sep 20 2001 15:55:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * This class handles the DataTransaction behavior for reading Orders.
 * 
 * @version $Revision: /main/12 $
 * @deprecated in version 14.0; all Order updates occur in transactions to support
 * queuing and transfer to Central Office.
 */
public class OrderWriteDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = -544694777243283961L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(OrderWriteDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "OrderWriteDataTransaction";

    /**
     * Class constructor.
     */
    public OrderWriteDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name data command name
     */
    public OrderWriteDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Updates status for an order.
     * 
     * @param order OrderIfc reference
     * @exception DataException when an error occurs.
     */
    public void updateOrderStatus(OrderIfc order) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("OrderWriteDataTransaction.updateOrderStatus");

        // set data actions and execute
        ((DataAction)getDataActions()[0]).setDataOperationName("UpdateOrderStatus");
        applyDataObject(order);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("OrderWriteDataTransaction.updateOrderStatus");
    }

    /**
     * Updates an order.
     * 
     * @param order OrderIfc reference
     * @exception DataException when an error occurs.
     */
    public void updateOrder(OrderIfc order) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("OrderWriteDataTransaction.updateOrder");

        // set data actions and execute
        applyDataObject(order);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("OrderWriteDataTransaction.updateOrder");
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
        StringBuilder strResult = new StringBuilder("Class: OrderWriteDataTransaction ");
        strResult.append("(Revision ").append(getRevisionNumber());
        strResult.append(") @").append(hashCode());
        return (strResult.toString());
    }
}