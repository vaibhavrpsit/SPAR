/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/IXRetailTransactionWriteDataTrans.java /main/13 2011/01/27 19:03:04 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:43 PM  Robert Pearse   
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
 *    Rev 1.0   Jun 03 2002 16:35:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   23 May 2002 13:48:06   pdd
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

/**
 * This DataTransaction sends IX Retail Transactions to the Data Technician for
 * storage in the database.
 * 
 * @version $Revision: /main/13 $
 */
public class IXRetailTransactionWriteDataTrans extends DataTransaction
{
    private static final long serialVersionUID = 1048270627197583682L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(IXRetailTransactionWriteDataTrans.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * The default name that links this transaction to a command within
     * DataScript.
     */
    public static String dataCommandName = "IXRetailTransactionWriteDataTrans";

    /**
     * Class constructor.
     */
    public IXRetailTransactionWriteDataTrans()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name transaction name
     */
    public IXRetailTransactionWriteDataTrans(String name)
    {
        super(name);
    }

    /**
     * Saves an IX Retail Transaction to the data store.
     * 
     * @param String contains a transaction as defined by the IX Retail
     *            Standard.
     * @exception DataException when an error occurs.
     */
    public void saveTransaction(String transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("IXRetailTransactionWriteDataTrans.saveTransaction");

        applyDataObject(transaction);
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("IXRetailTransactionWriteDataTrans.saveTransaction");
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ");
        strResult.append(getClass().getName() + " (Revision ").append(revisionNumber).append(") @").append(hashCode())
                .append(Util.EOL).append("dataCommandName = " + dataCommandName).append(Util.EOL);

        return (strResult.toString());
    }

    /**
     * Returns the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
