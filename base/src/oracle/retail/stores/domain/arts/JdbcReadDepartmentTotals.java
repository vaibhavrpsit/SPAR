/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadDepartmentTotals.java /main/16 2011/12/05 12:16:25 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    ohorne    03/13/09 - added support for localized department names
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:15 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:15    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:55:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.financial.DepartmentActivityIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * Class that contains the database calls for reading department totals.
 * 
 * @version $Revision: /main/16 $
 */
public class JdbcReadDepartmentTotals extends JdbcReadDepartment implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -693977179506138480L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadDepartmentTotals.class);

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadDepartmentTotals.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSActivity activity = (ARTSActivity)action.getDataObject();
        DepartmentActivityIfc[] depts;
        depts = readDepartmentTotals(connection, activity.getReportingPeriods(), activity.getLocales());

        /*
         * Send back the result
         */
        dataTransaction.setResult(depts);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadDepartmentTotals.execute()");
    }

    /**
       Returns a list of department totals for the reporting periods.
       <p>
       @param  dataConnection  connection to the db
       @param  periods         The list of reporting periods to include
       @return department information
       @exception DataException upon error
       @deprecated As of release 13.1 use {@link #readDepartmentTotals(JdbcDataConnection, ReportingPeriodIfc[], LocaleRequestor)}
     */
    public DepartmentActivityIfc[] readDepartmentTotals(JdbcDataConnection dataConnection,
                                                        ReportingPeriodIfc[] periods)
        throws DataException
    {
        return readDepartmentTotals(dataConnection, periods, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }
    
    /**
       Returns a list of department totals for the reporting periods.
       <p>
       @param  dataConnection  connection to the db
       @param  periods         The list of reporting periods to include
       @return department information
       @exception DataException upon error
     */
    public DepartmentActivityIfc[] readDepartmentTotals(JdbcDataConnection dataConnection,
                                                        ReportingPeriodIfc[] periods,
                                                        LocaleRequestor locales)
        throws DataException
    {
        Vector<DepartmentActivityIfc> deptVector = selectDepartmentHistory(dataConnection, periods, locales);

        DepartmentActivityIfc[] activities = new DepartmentActivityIfc[deptVector.size()];
        deptVector.copyInto(activities);

        return activities;
    }
    
    
}
