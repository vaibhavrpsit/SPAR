/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/RegistryDataTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:05 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:39 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:34:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:42:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:51:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:59:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;

import org.apache.log4j.Logger;

/**
 * The RegistryDataTransaction implements the Registry ID lookup operation.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class RegistryDataTransaction extends DataTransaction
{
    private static final long serialVersionUID = -3533234312009766350L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(RegistryDataTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The name that links this transaction to a command within the DataScript.
     */
    protected static String dataCommandName = "RegistryDataTransaction";

    /**
     * Class constructor.
     */
    public RegistryDataTransaction()
    {
        super(dataCommandName);
    }

    /**
     * Retrieve the gift registry id rows gift registry ID from the data store.
     * 
     * @param registryID A gift registry ID that contains the key values
     *            required to restore the transaction from a persistent store.
     * @return An array of gift registry id rows that match the key criteria,
     *         null if no registryID matches.
     * @exception DataException when an error occurs
     */
    public RegistryIDIfc readRegistryID(String registryID) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("RegistryDataTransaction.readRegistryID");

        // set data actions and execute
        applyDataObject(registryID);
        RegistryIDIfc retrievedRegistryID = (RegistryIDIfc) getDataManager().execute(this);

        if (retrievedRegistryID == null)
        {
            throw new DataException(DataException.NO_DATA);
        }

        if (logger.isDebugEnabled())
            logger.debug("RegistryDataTransaction.readRegistryID");

        return (retrievedRegistryID);
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
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class: RegistryDataTransaction");
        strResult.append(" (Revision ").append(getRevisionNumber()).append(") @").append(hashCode());
        return (strResult.toString());
    }
}
