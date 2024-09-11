/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRemoveCalendarLevel.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
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
import java.util.ArrayList;

import oracle.retail.stores.domain.utility.calendar.CalendarLevelKey;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 */
public class JdbcRemoveCalendarLevel extends JdbcCalendarDataOperation
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
     String methodName = "JdbcRemoveCalendarLevel.execute()";
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

      removeLevel(transaction, (JdbcDataConnection) dataConnection);
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }

    /**
     * removeLevel()
     * Remove a CalendarLevel from the database
     */
    protected void removeLevel(CalendarDataTransaction transaction,
                               JdbcDataConnection connection) throws DataException
    {
      // Figure out where we are
      String methodName = "JdbcRemoveCalendarLevel.removeLevel()";

      // Are there any non-root periods in the level?
      if(doLevelHasPeriods(connection, transaction.paramLevel.getLevelKey()))
         throw new DataException(DataException.UNKNOWN,
                                 methodName + " -  " + transaction.paramLevel.getLevelKey().toString() + " has periods");


      // Go get the list of children Level IDs
      ArrayList children =
          doFetchChildrenLevelKeys(connection,
                                   transaction.paramLevel.getLevelKey());

      // If there are children levels, we can't remove this one
      if(children != null && children.size() > 0)
        throw new DataException(DataException.UNKNOWN,
                                methodName + " -  " + transaction.paramLevel.getLevelKey().toString() + " has child levels");

      // Go get the list of parent Level IDs
      ArrayList parents =
          doFetchParentLevelKeys(connection,
                                 transaction.paramLevel.getLevelKey());

      // Are we our own parent?  - We can't remove the rootLevel
      for(int i=0; i<parents.size(); i++) {
        if( ((CalendarLevelKey) parents.get(i)).equals(transaction.paramLevel.getLevelKey())) {
          throw new DataException(DataException.UNKNOWN,
                                  methodName + " - can't remove root level");
        }
      }

      // Okay, we are actually allowed to remove this level...
      doRemoveLevelAssociations(connection,
                                transaction.paramLevel.getLevelKey());
      // finally do the removal
      doRemoveLevel(connection,
                    transaction.paramLevel.getLevelKey());
    }

  /**
   * Debug/Logging representation of this class
   * @return String containing class name and version number
   */
  public String toString()
  {
    return "JdbcRemoveCalendarLevel - version: " + getRevisionNumber();
  }

  /**
   * Revision number of the code
   * @return String containing revision number as set by source code control system.
   */
  public String getRevisionNumber()  {
    return Util.parseRevisionNumber(revisionNumber);
  }
}
