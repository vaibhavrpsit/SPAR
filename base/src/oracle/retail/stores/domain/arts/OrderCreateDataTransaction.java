/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/OrderCreateDataTransaction.java /main/15 2012/05/14 15:40:02 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/11/12 - check order customer null pointer
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
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:52 PM  Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
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
 *    Rev 1.0   Aug 29 2003 15:33:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:42:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:50:38   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:10:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:55:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:28   msg
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
 * This class handles the DataTransaction behavior for creating Orders.
 *
 * @version $Revision: /main/15 $
 * @deprecated as of 14.0.  This class is no longer needed
 */
public class OrderCreateDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = -1396152411717041939L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(OrderCreateDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "OrderCreateDataTransaction";

    /**
     * Class constructor.
     */
    public OrderCreateDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     *
     * @param name data command name
     */
    public OrderCreateDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Creates an order.
     *
     * @param order OrderIfc reference
     * @exception DataException when an error occurs.
     */
    public void createOrder(OrderIfc order) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("OrderCreateDataTransaction.updateOrder");

        // set data actions and execute
        ((DataAction)getDataActions()[0]).setDataObject(new ARTSCustomer(order.getCustomer()));
        ((DataAction)getDataActions()[1]).setDataObject(order);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("" + "OrderCreateDataTransaction.updateOrder" + "");
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

    /**
     * Returns the string representation of this object.
     *
     * @return String representation of object
     */
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("Class: OrderCreateDataTransaction ");
        strResult.append("(Revision ").append(getRevisionNumber());
        strResult.append(") @").append(hashCode());
        return (strResult.toString());
    }
}