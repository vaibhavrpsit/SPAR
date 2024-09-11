/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRemoveCalendarPeriod.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
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
import java.util.ArrayList;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 */
public class JdbcRemoveCalendarPeriod extends JdbcCalendarDataOperation
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
     String methodName = "JdbcRemoveCalendarPeriod.execute()";
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

      removeCalendarPeriod(transaction, (JdbcDataConnection) dataConnection);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }

    /**
     * removeCalendarPeriod()
     * Remove a CalendarPeriod from the database - Cannot remove rootPeriod
     * or periods with children.
     */
    protected void removeCalendarPeriod(CalendarDataTransaction transaction,
                                        JdbcDataConnection connection) throws DataException
    {
      // Figure out where we are
      String methodName = this.getClass().getName() + ".removePeriod()";

      // Go get the set of parentKeys
      ArrayList parents =
          doFetchParentCalendarPeriodKeys(connection,
                                          transaction.paramPeriod.getPeriodKey());

      // Are we our own parent? - can't remove root period
      if(parents.contains(transaction.paramPeriod)) {
        throw new DataException(DataException.UNKNOWN, methodName + " can't remove rootPeriod");
      }

      // Check the period has no children
      ArrayList children =
          doFetchChildrenCalendarPeriodKeys(connection,
                                            transaction.paramPeriod.getPeriodKey());

      if(children != null && children.size() > 0)
        throw new DataException(DataException.UNKNOWN, methodName + " - can't remove period with children");

      // ...This period can be removed ...it is no longer a child of it's parents
      doRemovePeriodAssociations(connection,
                                 transaction.paramPeriod.getPeriodKey());

      // ...finally kill off the period
      doRemoveCalendarPeriod(connection,
                             transaction.paramPeriod.getPeriodKey());
    }

  /**
   * Debug/Logging representation of this class
   * @return String containing class name and version number
   */
  public String toString()
  {
    return "JdbcRemoveCalendarPeriod - version: " + getRevisionNumber();
  }

  /**
   * Revision number of the code
   * @return String containing revision number as set by source code control system.
   */
  public String getRevisionNumber()  {
    return Util.parseRevisionNumber(revisionNumber);
  }
}
