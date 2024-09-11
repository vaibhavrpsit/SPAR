/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadDepartmentList.java /main/16 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:14 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse   
     $:
      5    .v700     1.2.1.1     11/17/2005 16:10:45    Jason L. DeLeau 4345:
           Replace any uses of Gateway.log() with the log4j.
      4    .v700     1.2.1.0     11/16/2005 16:28:15    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
     $
     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:21  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:31:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:06   msg
 * Initial revision.
 *
 *    Rev 1.2   10 Apr 2002 17:19:04   baa
 * deprecate class and methods for reading dept list
 * Resolution for POS SCR-1562: Get Department list from Reason Codes, not separate Dept. list.
 *
 *    Rev 1.1   Mar 18 2002 22:45:34   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:55:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:32   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;


import java.sql.ResultSet;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
/**
    Class that contains the database calls for reading departments.
    @version $Revision: /main/16 $
    @deprecated as of release 5.1.0
**/
public class JdbcReadDepartmentList extends JdbcDataOperation
                                implements ARTSDatabaseIfc
{
    /**
        Executes the SQL statements against the database.
        <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("Entering JdbcReadDepartmentList.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        DepartmentIfc[] dept = selectDepartmentList(connection);

        /*
         * Send back the result
         */
        dataTransaction.setResult(dept);

        if (logger.isDebugEnabled()) logger.debug( "Exiting JdbcReadDepartmentList.execute()");
    }

    /**
        Returns a list of departments.
        @param  dataConnection      connection to the db
        @param  departmentName      The name of the department to return
        @return DepartmentIfc[]     the list of departments
        @exception DataException upon error
        @deprecated As of release 13.1
     */
    public DepartmentIfc[] selectDepartmentList(JdbcDataConnection dataConnection)
                                                throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        DepartmentIfc[] depts = null;

        /*
         * Add the desired tables
         */
        sql.addTable(TABLE_POS_DEPARTMENT, ALIAS_POS_DEPARTMENT);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_POS_DEPARTMENT_NAME);
        sql.addColumn(FIELD_POS_DEPARTMENT_ID);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            Vector deptList = new Vector(0);

            while (rs.next())
            {
                int index = 0;
                DepartmentIfc deptInfo = DomainGateway.getFactory().getDepartmentInstance();
                deptInfo.setDescription(getSafeString(rs,++index));
                deptInfo.setDepartmentID(getSafeString(rs,++index));
                deptList.addElement(deptInfo);
            }
            rs.close();
            depts = new DepartmentIfc[deptList.size()];
            deptList.copyInto(depts);

        }
        catch (DataException de)
        {
        	logger.warn(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectDepartmentList", e);
        }

        return(depts);
    }

}
