/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/AlertDataTransaction.java /main/12 2013/01/10 14:04:11 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/09/13 - retrieve alerts using order manager api
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:12 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:35 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:26 PM  Robert Pearse
 *
 *   Revision 1.7  2004/04/09 16:55:45  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.6  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.5  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.4  2004/02/12 19:58:06  baa
 *   @scr 0 fix javadoc
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
 *    Rev 1.0   Aug 29 2003 15:29:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 07 2003 10:26:48   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:33:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:44:40   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:04:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:55:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:35:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.alert.AlertListIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * This class handles the DataTransaction behavior for Alerts data requests.
 *
 * @version $Revision: /main/12 $
 * @deprecated as of 14.0.  This class is no longer needed.
 * Use order manager to retrieve order alerts.
 */
public class AlertDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = -5284255670854792897L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(AlertDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "AlertDataTransaction";

    /**
     * Class constructor.
     */
    public AlertDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     *
     * @param name data command name
     */
    public AlertDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Retrieves list of alert entries for a given store in descending date
     * order.
     *
     * @param storeID store identifier
     * @exception DataException when an error occurs.
     */
    public AlertListIfc retrieveAlertList(String storeID) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("AlertDataTransaction.retrieveAlertList");

        AlertListIfc alertList = null;

        // set data actions and execute
        applyDataObject(storeID);

        // execute data request
        alertList = (AlertListIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("AlertDataTransaction.retrieveAlertList");

        return (alertList);
    }

    /**
     * Retrieves list of alert entries for a given store in descending date
     * order.
     *
     * @param inquiry inquiry data
     * @exception DataException when an error occurs.
     */
    public AlertListIfc retrieveAlertList(SearchCriteriaIfc inquiry) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("AlertDataTransaction.retrieveAlertList");

        AlertListIfc alertList = null;

        // set data actions and execute
        applyDataObject(inquiry);

        // execute data request
        alertList = (AlertListIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("AlertDataTransaction.retrieveAlertList");

        return (alertList);
    }

    /**
     * Returns the revision number of this class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("Class: AlertDataTransaction ");
        strResult.append("(Revision ").append(getRevisionNumber());
        strResult.append(") @").append(hashCode());
        return (strResult.toString());
    }
}