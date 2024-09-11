/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ExecuteSQLDataTransaction.java /main/13 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:32 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:57 PM  Robert Pearse   
 *
 *Revision 1.6  2004/04/09 16:55:47  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.5  2004/02/17 17:57:38  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.4  2004/02/17 16:18:48  rhafernik
 *@scr 0 log4j conversion
 *
 *Revision 1.3  2004/02/12 17:13:13  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 23:25:26  bwf
 *@scr 0 Organize imports.
 *
 *Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:30:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jan 22 2003 09:41:42   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to a execute free form SQL Statement which has been
 * formulated by the application.
 */
public class ExecuteSQLDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = 2176315665431644505L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(ExecuteSQLDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "ExecuteSQLDataTransaction";

    /**
     * Class constructor.
     */
    public ExecuteSQLDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     */
    public ExecuteSQLDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Executes the SQL Statement as supplied in the parameter.
     * 
     * @param sqlStatement the SQL Statement
     * @exception DataException when an error occurs
     */
    public void executeSQL(String sqlStatement) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("ExecuteSQLDataTransaction.searchStoreDirectory");

        // set data actions and execute
        applyDataObject(sqlStatement);
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("ExecuteSQLDataTransaction.searchStoreDirectory");
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder("Class:  ExecuteSQLDataTransaction (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode()).append("\n");
        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}