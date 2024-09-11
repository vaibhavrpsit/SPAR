/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadRoleFunctions.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/12/2006 5:26:28 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    5    .v700     1.2.1.1     11/17/2005 16:10:45    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    4    .v700     1.2.1.0     11/16/2005 16:26:10    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.4  2004/08/18 01:53:33  kll
 *   @scr 6826: extract roles specific to POS
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 22 2003 12:32:28   adc
 * Changes to accomodate new Back Office requirements
 * Resolution for 1935: Roles/Security updates
 *
 *    Rev 1.0   Jun 03 2002 16:38:00   msg
 * Initial revision.
 *
 *    Rev 1.1   04 Apr 2002 15:14:16   baa
 * Modify employee flat file to allow for role functions to be extendible
 * Resolution for POS SCR-1565: Remove references to RoleFunctionIfc.Descriptor Security Service
 *
 *    Rev 1.0   Mar 18 2002 12:05:52   msg
 * Initial revision.
 *
 *    Rev 1.0   05 Mar 2002 16:37:14   baa
 * Initial revision.
 * Resolution for POS SCR-626: Make the list of Role functions extendible.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.TreeMap;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionGroupIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
    This operation takes a POS domain Customer and creates a new entry
    in the database.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
public class JdbcReadRoleFunctions    extends JdbcDataOperation
                                      implements ARTSDatabaseIfc
{
    /**
       Class constructor. <P>
     */
    public JdbcReadRoleFunctions()
    {
        super();
        setName("JdbcReadRoleFunctions");
    }

    /**
       Execute the SQL statements against the database. <P>
       @param  dataTransaction
       @param  dataConnection
       @param  action
       @exception  DataException
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        try
        {
           JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

           RoleFunctionIfc[] roleFunctions = selectRoleFunctions(connection);

           dataTransaction.setResult(roleFunctions);
        }
        catch(SQLException e)
        {
            ((JdbcDataConnection)dataConnection).logSQLException(e, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "An SQL Error occurred proccessing the result set from selecting a role function in JdbcReadRoleFunction.", e);
        }
    }

    /**
       Read from the contact table. <P>
       @param  dataConnection The data connection on which to execute.
       @exception  DataException thrown when an error occurs executing the
       against the DataConnection
       @exception  SQLException thrown when an error occurs with the
       ResultSet
     */
    public RoleFunctionIfc[] selectRoleFunctions(JdbcDataConnection dataConnection)
        throws DataException, SQLException
    {
        TreeMap roleFunctions = new TreeMap();
         SQLSelectStatement sql = new SQLSelectStatement();

        //add tables
        sql.addTable(TABLE_ROLE_ACCESS, ALIAS_ROLE_ACCESS);
        sql.addTable(TABLE_RESOURCE, ALIAS_RESOURCE);

        //add columns
        sql.addColumn("distinct "+ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_DESCRIPTION);
        sql.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_ID);
        sql.addColumn(ALIAS_RESOURCE + "." + FIELD_GROUP_PARENT_NAME);
        sql.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_VISIBILITY_CODE);
        sql.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_PARENT_RESOURCE_ID);
        sql.addColumn(ALIAS_ROLE_ACCESS + "." + FIELD_ID_APPLICATION);

        //add qualifiers
        sql.addOrdering(ALIAS_ROLE_ACCESS + "." + FIELD_GROUP_RESOURCE_DESCRIPTION);
        sql.addQualifier(ALIAS_RESOURCE + "." + FIELD_PARENT_RESOURCE_ID + " = " +
                         ALIAS_ROLE_ACCESS + "." + FIELD_PARENT_RESOURCE_ID);
        // short-term solution, hardcoded to extract roles specific to POS
        sql.addQualifier(ALIAS_ROLE_ACCESS + "." + FIELD_ID_APPLICATION + " = 2");

        try
        {
              dataConnection.execute(sql.getSQLString());
              ResultSet rs = (ResultSet)dataConnection.getResult();

              while (rs.next())
              {
                  RoleFunctionIfc rf =  DomainGateway.getFactory().getRoleFunctionInstance();

                  rf.setTitle(getSafeString(rs, 1));
                  rf.setFunctionID(rs.getInt(2));

                  String groupName = getSafeString(rs, 3);
                  int visibLevel = rs.getInt(4);
                  int groupID =rs.getInt(5);
                  rf.setVisibilityLevel(visibLevel);
                  RoleFunctionGroupIfc group = DomainGateway.getFactory().getRoleFunctionGroupInstance();
                  group.setGroupID(groupID);
                  group.setGroupName(groupName);
                  rf.setFunctionGroup(group);

                  roleFunctions.put(rf.getTitle(), rf);
              }
              rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "SelectRoleFunctions");
            throw new DataException(DataException.SQL_ERROR, "SelectRoleFunctions", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "SelectRoleFunctions", e);
        }

        return (sortFunctionList(roleFunctions));
    }

    /**
       Sort role functions in alphabetical order
     */
    
     public RoleFunctionIfc[] sortFunctionList(TreeMap list)
     {
        Iterator ki = list.keySet().iterator();
        int j = 0;

        // transfer sorted data to array
        RoleFunctionIfc[] funcs = new RoleFunctionIfc[list.size()];
        while (ki.hasNext())
        {
            String functionName = (String) ki.next();
            funcs[j++] = (RoleFunctionIfc)list.get(functionName);
        }
        return (funcs);
     }

    /**
       Set all data members should be set to their initial state. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>
       All processing must be complete
       <LI>
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>
       All data member have been returned to the initial state.
       </UL>
     */
    public void initialize() throws DataException
    {
        // no action taken here
    }
}
