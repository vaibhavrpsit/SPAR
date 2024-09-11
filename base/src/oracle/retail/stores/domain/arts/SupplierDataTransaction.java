/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/SupplierDataTransaction.java /main/10 2011/01/27 19:03:04 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
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
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:34:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Feb 15 2003 17:38:36   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   19 Aug 2002 16:01:24   adc
 * Initial revision.
 * Resolution for Backoffice SCR-805: Receiving
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.purchasing.SupplierIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * The DataTransaction to perform supplier operations.
 * 
 * @version $Revision: /main/10 $
 */
public class SupplierDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = 7829740123430928085L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(SupplierDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "SupplierDataTransaction";

    /**
     * Class constructor.
     */
    public SupplierDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Class constructor.
     */
    public SupplierDataTransaction(String name)
    {
        super(name);
    }

    /**
     * Searches for all suppliers in the database.
     * 
     * @return array of SuppliersIfc objects
     * @exception DataException when an error occurs
     */
    public SupplierIfc[] retrieveAllSuppliers() throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("SupplierDataTransaction.retrieveAllStores");

        SupplierIfc[] retrievedSuppliers = (SupplierIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("SupplierDataTransaction.retrieveAllStores");

        return (retrievedSuppliers);

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder("Class:  SupplierDataTransaction (Revision ");
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