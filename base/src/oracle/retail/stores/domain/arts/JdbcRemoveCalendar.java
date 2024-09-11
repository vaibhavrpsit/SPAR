/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRemoveCalendar.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// java imports
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;


/**
 */
public class JdbcRemoveCalendar extends JdbcCalendarDataOperation
{

      /** revision number supplied by source-code-control system */
      public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  /**
   * Executes the requested SQL statments against the database.
   *
   * @param  dataTransaction     The data transaction
   * @param  dataConnection      The connection to the data source
   * @param  action              The information passed by the valet
   * @exception DataException upon error
  **/
   public void execute(DataTransactionIfc dataTransaction,
                       DataConnectionIfc dataConnection,
                       DataActionIfc action)   throws DataException {

     // Figure out where we are
     String methodName = "JdbcRemoveCalendar.execute()";
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

      removeCalendar(transaction, (JdbcDataConnection) dataConnection);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }

    /**
     *  removeCalendar()
     *  removes a Calendar from the database.
     */
    protected void removeCalendar(CalendarDataTransaction transaction,
                                  JdbcDataConnection connection) throws DataException
    {
      // Figure out the method name for Exception reporting
      String methodName = this.getClass().getName() + ".removeCalendar()";

      // Delete the remaining Period & Level associations in the calendar
      doRemoveAllCalendarAssociations(connection, transaction.paramCalendarID);

      // Delete the periods
      doRemoveAllCalendarPeriods(connection, transaction.paramCalendarID);

      // ... Levels
      doRemoveLevels(connection, transaction.paramCalendarID);

      // ... Calendar
      doRemoveCalendar(connection, transaction.paramCalendarID);
    }



  /**
   * Debug/Logging representation of this class
   * @return String containing class name and version number
   */
  public String toString()
  {
    return "JdbcRemoveCalendar - version: " + getRevisionNumber();
  }

  /**
   * Revision number of the code
   * @return String containing revision number as set by source code control system.
   */
  public String getRevisionNumber()  {
    return Util.parseRevisionNumber(revisionNumber);
  }
}
