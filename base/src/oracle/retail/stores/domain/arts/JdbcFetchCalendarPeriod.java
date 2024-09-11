/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFetchCalendarPeriod.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Fetch a particular CalendarPeriod from the database.
 */
public class JdbcFetchCalendarPeriod extends JdbcCalendarDataOperation
{

      /** revision number supplied by source-code-control system */
      public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  public void execute(DataTransactionIfc dataTransaction,
                      DataConnectionIfc dataConnection,
                      DataActionIfc action)   throws DataException {

    // Figure out where we are
    String methodName = "JdbcFetchCalendarPeriod.execute()";
    if (logger.isDebugEnabled()) logger.debug( methodName);

    // Make sure we've got the right transaction....
    CalendarDataTransaction transaction;
    try {
      transaction = (CalendarDataTransaction) dataTransaction;
    } catch (ClassCastException ex) {
      throw new DataException(DataException.DATA_FORMAT,
                              methodName + ": Invalid dataTransaction (" +
                              dataTransaction.getClass().getName() + ")");
    }

    // Go fetch
    CalendarPeriodIfc result =
        doFetchCalendarPeriod((JdbcDataConnection) dataConnection,
                              transaction.paramPeriodKey);
    // We're done
    transaction.setResult(result);
    if (logger.isDebugEnabled()) logger.debug( methodName);
   }

   /**
    * Debug/Logging representation of this class
    * @return String containing class name and version number
    */
   public String toString()
   {
     return "JdbcFetchCalendarPeriod - version: " + getRevisionNumber();
   }

   /**
    * Revision number of the code
    * @return String containing revision number as set by source code control system.
    */
   public String getRevisionNumber()  {
     return Util.parseRevisionNumber(revisionNumber);
   }
}
