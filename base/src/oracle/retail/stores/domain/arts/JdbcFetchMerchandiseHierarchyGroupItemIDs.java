/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcFetchMerchandiseHierarchyGroupItemIDs.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 * $Log:
 *    6    360Commerce 1.5         5/27/2008 6:51:15 AM   Naveen Ganesh
 *         Converted the groupId variable type from int to String
 *    5    360Commerce 1.4         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    4    360Commerce 1.3         6/5/2007 12:07:12 PM   Christian Greene
 *         Groupd id is now a String. Fix merchandise hierarchy jdbc classes
 *         and test class.
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:54 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:30:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 26 2003 17:30:40   mwright
 * 1. Added standard header.
 * 2. Use standard toString().
 * 3. Updated javaDoc.
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
 *   Fetches the IDs for the set of Items that're assigned to the merchandise classification
 *   hierarchy group (and it's children groups) from the MerchandiseHierarchy tables in the
 *   ARTS data model (Release 4.0).<p>
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcFetchMerchandiseHierarchyGroupItemIDs extends JdbcMerchandiseHierarchyDataOperation
{

  /**
     revision number of this class
  **/
  public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

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
     String methodName = "JdbcFetchMerchandiseHierarchyGrgoupItemIDs.execute()";
     if (logger.isDebugEnabled()) logger.debug( methodName);

      // Make sure we've got the right transaction....
      MerchandiseHierarchyDataTransaction transaction;
      try {
        transaction = (MerchandiseHierarchyDataTransaction) dataTransaction;
      } catch (ClassCastException ex) {
        throw new DataException(DataException.DATA_FORMAT,
                                methodName + ": Invalid dataTransaction (" +
                                dataTransaction.getClass().getName() + ")");
      }

      fetchMemberItemIDs(transaction, (JdbcDataConnection) dataConnection);

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
    }


    /**
     * Retrieve Item IDs for the given item and all its child groups.
     * @param  transaction     The data transaction
     * @param  connection      The connection to the data source
     * @exception DataException upon error
     */
    protected void fetchMemberItemIDs(MerchandiseHierarchyDataTransaction transaction,
                                      JdbcDataConnection connection) throws DataException
    {
      // Start off with myself in the list of childGroups
      ArrayList/*<String>*/ childGroupIDs = new ArrayList(10);
      childGroupIDs.add(transaction.paramGroupID);

      // Go down the list appending the children of each member to the list
      for(int i = 0; i < childGroupIDs.size(); i++) {
    	  
    	  
        childGroupIDs.addAll(doFetchChildrenGroupIDs(connection,
                                                     transaction.paramHierarchyID,
                                                     (String)childGroupIDs.get(i)));
      }

      // Now that we know what Groups we're including, go fetch the set of
      // items in those groups
      ArrayList result = doFetchItemIDs(connection, childGroupIDs);

      // We're done
      transaction.setResult(result);
    }

  /*****************************************************************************
   * String identification of this class.<p>
   * @return A string identifying the class and revision number.
   ****************************************************************************/
  public String toString()
  {
      StringBuilder strResult = Util.classToStringHeader(
       "JdbcMerchandiseHierarchyGroupItemIDs",
       getRevisionNumber(),
       hashCode());
    return strResult.toString();
  }


    /**
     * Retrieves the source-code-control system revision number. <P>
     * @return String representation of revision number
     **/
    public String getRevisionNumber() {
      return Util.parseRevisionNumber(revisionNumber);
    }
}
