/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ParameterTransaction.java /main/10 2011/01/27 19:03:04 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:59 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:33:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   May 20 2003 13:06:32   cdb
 * Initial revision.
 * Resolution for 1930: RE-FACTORING AND FEATURE ENHANCEMENTS TO PARAMETER SUBSYSTEM
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.utility.UpdateMessage;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.parameter.Source;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

/**
 * This class handles the DataTransaction behavior for parameter (system
 * setting) updates.
 * 
 * @version $Revision: /main/10 $
 */
public class ParameterTransaction extends DataTransaction
{
    private static final long serialVersionUID = -3641568560156840920L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(ParameterTransaction.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    public static final String STORE_ID = "StoreID";
    public static final String WORKSTATION_CLASSIFICATION = "WorkstationClassification";

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName = "ParameterTransaction";

    /**
     * the name linking this transaction to a command within datascript for a
     * local data transaction
     */
    // public static String LocalDataCommandName="LocalParameterTransaction";

    /**
     * Class constructor.
     */
    public ParameterTransaction()
    {
        this(dataCommandName);
    }

    /**
     * Class constructor.
     * 
     * @param name data command name
     */
    public ParameterTransaction(String name)
    {
        super(name);
    }

    /**
     * Saves a source. Previously saved entries for this register are replaced.
     * 
     * @param source Source object containing parameters to be updated
     * @param storeID Store ID to update
     * @param registerClass Register classification to update
     * @exception DataException when an error occurs.
     */
    public void saveSource(Source source, String storeID, String registerClass) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("ParameterTransaction.saveSource; Name = " + getTransactionName());

        // set data actions and execute
        applyDataObject(convertToUpdateMessage(source, storeID, registerClass));

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("ParameterTransaction.saveSource");
    }

    /**
     * Creates an UpdateMessage object.
     * 
     * @param source Source object containing parameters to be updated
     * @param storeID Store ID to update
     * @param registerClass Register classification to update
     * @return The populated UpdateMessage
     * @exception DataException when an error occurs.
     */
    public UpdateMessage convertToUpdateMessage(Source source, String storeID, String registerClass)
            throws DataException
    {
        UpdateMessage updateMessage = new UpdateMessage();
        updateMessage.setProperty(STORE_ID, storeID);
        updateMessage.setProperty(WORKSTATION_CLASSIFICATION, registerClass);
        updateMessage.setDataObject(source);
        return updateMessage;
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
        return (Util.classToStringHeader("ParameterTransaction", getRevisionNumber(), hashCode()).toString());
    }
}