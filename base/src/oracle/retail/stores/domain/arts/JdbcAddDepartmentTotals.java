/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcAddDepartmentTotals.java /main/18 2012/10/10 14:28:38 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   10/10/12 - Additional refactoring to address race condition in
 *                         DataConnectionPool
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/15/10 - add code to catch primary key violations when
 *                         inserting dept hist records and try update.
 *    abondala  01/03/10 - update header date
 *    cgreene   08/06/09 - XbranchMerge cgreene_bug-8690056 from
 *                         rgbustores_13.1x_branch
 *    cgreene   07/22/09 - made method addDepartmentTotals synchronized so that
 *                         race condition for adding records will not occur
 *                         upon heavy loads.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/20/2008 3:53:21 PM   Gloria Wang     CR
 *         31262. Merge fix from v7x. Code review by Dan.
 *    4    360Commerce 1.3         1/25/2006 4:11:06 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:52 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:00    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:35     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:35     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:52     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.0   Aug 29 2003 15:30:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:35:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:18   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.List;

import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation modifies the POS Department History table.
 * <p>
 * Operations will attempt to first update the appropriate history record. If
 * no update was made, a new record will be inserted.
 * 
 * @version $Revision: /main/18 $
 */
public class JdbcAddDepartmentTotals extends JdbcSaveDepartment
                                     implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -782442850996114021L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcAddDepartmentTotals.class);

    /**
     * Class constructor.
     */
    public JdbcAddDepartmentTotals()
    {
        setName("JdbcAddDepartmentTotals");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public synchronized void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcAddDepartmentTotals.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be updated
        // in the database.
        @SuppressWarnings("unchecked")
        List<ARTSDepartmentTotals> deptTotalsList = (List<ARTSDepartmentTotals>)action.getDataObject();

        for(ARTSDepartmentTotals deptTotal : deptTotalsList)
        {
            addDepartmentTotals(connection,
                                deptTotal.getDepartmentID(),
                                deptTotal.getBusinessDate(),
                                deptTotal.getFinancialTotals());
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcAddDepartmentTotals.execute()");
    }

    /**
     * Adds the financial total information to the POS Department History.
     * <P>
     * This method is synchronized to avoid two simultaneous threads from
     * trying to read then insert the same record for a new dept hist, which
     * results in the second receiving a constraint violation.
     * 
     * @param dataConnection connection to the db
     * @param deptID the department ID
     * @param businessDate the identifying business date
     * @param totals the financial totals information to add
     * @exception DataException thrown when an error occurs.
     */
    protected void addDepartmentTotals(JdbcDataConnection dataConnection,
                                    String deptID,
                                    EYSDate businessDate,
                                    FinancialTotalsIfc totals)
        throws DataException
    {
        try
        {
            addPOSDepartmentTotals(dataConnection, deptID, businessDate, totals);
        }
        catch (DataException de)
        {
            // Maybe the record just isn't there yet
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                try
                {
                    insertPOSDepartmentTotals(dataConnection, deptID, businessDate, totals);
                }
                catch (DataException de2)
                {
                    // we could possibly get a primary key violation if two threads
                    // are inserting the department totals at the same time.
                    // In which case, just update the totals like we tried first.
                    if (de.getErrorCode() == DataException.KEY_VIOLATION_ERROR)
                    {
                        addPOSDepartmentTotals(dataConnection, deptID, businessDate, totals);                        
                    }
                    else
                    {
                        throw de2;                        
                    }
                }
            }
            else 
            {
            	throw de;
            }
        }
    }
}
