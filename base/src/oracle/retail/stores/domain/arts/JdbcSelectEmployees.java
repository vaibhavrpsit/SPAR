/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSelectEmployees.java /main/18 2012/10/03 14:27:30 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  09/12/12 - Modifications to support Biometrics Quickwin -
 *                         support for multiple FP per employee
 *    hyin      02/22/11 - format change
 *    hyin      02/21/11 - minor clean up
 *    hyin      02/17/11 - added new jdbc call for biometrics
 *    blarsen   05/26/10 - Removed check for employee != null. This was removed
 *                         so the new login w/ fingerprint site could retrieve
 *                         all employees.
 *    blarsen   06/09/10 - XbranchMerge blarsen_biometrics-poc from
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *  4    360Commerce 1.3         1/25/2006 4:11:25 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 * $:
 *  4    .v700     1.2.1.0     11/16/2005 16:27:31    Jason L. DeLeau 4215: Get
 *       rid of redundant ArtsDatabaseifc class
 *  3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:22:51     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
 * $
 * Revision 1.6  2004/02/19 23:36:46  jriggins
 * @scr 3782 this commit mainly deals with the database modifications needed for Enter New Password feature in Operator ID
 * Revision 1.5 2004/02/17 17:57:36 bwf @scr
 * 0 Organize imports.
 *
 * Revision 1.4 2004/02/17 16:18:45 rhafernik @scr 0 log4j conversion
 *
 * Revision 1.3 2004/02/12 17:13:19 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 23:25:22 bwf @scr 0 Organize imports.
 *
 * Revision 1.1.1.1 2004/02/11 01:04:28 cschellenger updating to pvcs
 * 360store-current
 *
 *
 *
 * Rev 1.1 Jan 26 2004 09:56:20 jriggins Added support for select employee by
 * role Resolution for 3597: Employee 7.0 Updates
 *
 * Rev 1.0 Aug 29 2003 15:33:12 CSchellenger Initial revision.
 *
 * Rev 1.1 Feb 26 2003 11:41:32 bwf Database Internationalization Resolution
 * for 1866: I18n Database support
 *
 * Rev 1.0 Jun 03 2002 16:40:40 msg Initial revision.
 *
 * Rev 1.1 Mar 18 2002 22:49:16 msg - updated copyright
 *
 * Rev 1.0 Mar 18 2002 12:08:56 msg Initial revision.
 *
 * Rev 1.0 Sep 20 2001 15:56:48 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads the current status of a workstation.
 * 
 * @version $Revision: /main/18 $
 */
public class JdbcSelectEmployees extends JdbcReadEmployee implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -792042995570259204L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSelectEmployees.class);

    /**
     * Class constructor.
     */
    public JdbcSelectEmployees()
    {
        setName("JdbcSelectEmployees");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(
        DataTransactionIfc dataTransaction,
        DataConnectionIfc dataConnection,
        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSelectEmployees.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        LocaleRequestor localeRequestor = null;
        EmployeeIfc employee = null;
        RoleIfc role = null;
        boolean isFingerprintMode = false;

        // check data object and get info
        Serializable searchFilterData = action.getDataObject();
        if (searchFilterData instanceof SearchCriteriaIfc)
        {
            SearchCriteriaIfc employeeInfo =
                (SearchCriteriaIfc) searchFilterData;
            localeRequestor = employeeInfo.getLocaleRequestor();
            employee = employeeInfo.getEmployee();
            isFingerprintMode = employeeInfo.isFingerprintFullEmployeeListMode();
        }
        // This new condition searches by role
        else if (searchFilterData instanceof RoleIfc)
        {
            role = (RoleIfc) searchFilterData;
        }
        // This original code assumes that we are searching by employee name
        else if (searchFilterData instanceof EmployeeIfc)
        {
            employee = (EmployeeIfc) searchFilterData;
        }

        Vector selectedEmployees = new Vector();
        
        if (isFingerprintMode)
        {
            /*
             * In fingerprintFullEmployeeListMode, we only get a few 
             * columns back to achieve much better performance when 
             * dealing with large employee tables.
             */
            selectedEmployees = selectEmployeesForBiometrics(connection, localeRequestor);
        }else {

			// This original code assumes that we are searching by employee name
			if (localeRequestor != null) {
				selectedEmployees = selectEmployees(connection, employee,
						localeRequestor);
			}
			// This new condition searches by role
			else if (role != null) {
				selectedEmployees = selectEmployeesByRole(connection, role);
			}
			else {
				selectedEmployees = selectEmployees(connection, employee);
			}
        }
        dataTransaction.setResult(selectedEmployees);
        if (logger.isDebugEnabled())
            logger.debug("JdbcSelectEmployees.execute()");
    }

}
