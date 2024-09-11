/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreateCalendarLevel.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
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
 */
package oracle.retail.stores.domain.arts;

// java imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelKeyIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Performs all the actual database accesses required by the other classes
 * in the package.
 */
public class JdbcCreateCalendarLevel extends JdbcCalendarDataOperation
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
     String methodName = "JdbcCreateCalendarLevel.execute()";
     if (logger.isDebugEnabled()) logger.debug( methodName);

      // Make sure we've got the right transaction....
      CalendarDataTransaction transaction;
      try {
        transaction = (CalendarDataTransaction) dataTransaction;
      } catch (ClassCastException ex) {
        throw new DataException(DataException.UNKNOWN,
                                methodName + ": Invalid dataTransaction (" +
                                dataTransaction.getClass().getName() + ")");
      }

      createLevel(transaction, (JdbcDataConnection) dataConnection);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }


    /**
     *   createLevel()
     *   Makes a new CalendarLevel in the database, re-organizes parent pointers
     *   and constructs matching instance
     */
    protected void createLevel(CalendarDataTransaction transaction,
                               JdbcDataConnection connection) throws DataException
    {
      // Extract the parentLevel Key
      CalendarLevelKeyIfc parentKey = transaction.paramParentLevel.getLevelKey();

      // Make sure the name hasn't been used already in that calendar
      if (doFetchCalendarLevelID(connection, parentKey.getCalendarID(), transaction.paramName) != 0)
        throw new DataException(DataException.KEY_VIOLATION_ERROR, "Proposed Calendar Level Name already exists");

      // Find out what the new childLevel's ID will be
      int levelID = doGenerateLevelID(connection, parentKey.getCalendarID());

      // Make up the LevelKey for the new level
      CalendarLevelKeyIfc newLevelKey = DomainGateway.getFactory().
          getCalendarLevelKeyInstance().initialize(transaction.paramParentLevel.getLevelKey().getCalendarID(),
                                                   levelID);
      // Go make the new childLevel
      doCreateLevel(connection,
                    newLevelKey,
                    transaction.paramName,
                    transaction.paramLevelBoundaryCode);

      // The parent now has a new child
      doAssociateLevels(connection,
                        newLevelKey,
                        transaction.paramParentLevel.getLevelKey());

      // Go fetch the new child
      CalendarLevelIfc result =
          doFetchLevel(connection, newLevelKey);
      transaction.setResult(result);
    }

    /**
     * Debug/Logging representation of this class
     * @return String containing class name and version number
     */
    public String toString() {
      return "JdbcCreateCalendarLevel - version: " + getRevisionNumber();
    }

    /**
     * Revision number of the code
     * @return String containing revision number as set by source code control system.
     */
    public String getRevisionNumber() {
      return Util.parseRevisionNumber(revisionNumber);
    }
}
