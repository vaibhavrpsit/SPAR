/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/LayawayDataTransaction.java /main/13 2013/03/29 09:06:16 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  03/28/13 - Removed customer lookup from readLayaway.
 *    vtemker   02/12/13 - Fixed classcast exception when searching layaway by
 *                         layaway number (BugDb id 16245276)
 *    vtemker   07/28/11 - Set the isCustomerLinkedWithLayawayAttributeEnabled
 *                         flag of Customer to true, as this is a layaway
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         4/30/2008 1:59:23 PM   Maisa De Camargo CR
 *         31328 - Added method readLayawayTenders. Code Reviewed by Jack
 *         Swan.
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    5    360Commerce 1.4         11/9/2006 7:28:30 PM   Jack G. Swan
 *         Modifided for XML Data Replication and CTR.
 *    4    360Commerce 1.3         12/13/2005 4:43:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:33:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:41:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:50:34   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:10:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:59:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

//-------------------------------------------------------------------------
/**
    This data transaction was constructed to test the layaway data operations.
    @version $Revision: /main/13 $
**/
//-------------------------------------------------------------------------
public class LayawayDataTransaction 
extends DataTransaction
implements DataTransactionIfc
{                                       // begin class LayawayDataTransaction
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7316419398844270522L;

    /** 
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.LayawayDataTransaction.class);
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/13 $";
    /**
        The default name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName  = "LayawayDataTransaction";
    
    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public LayawayDataTransaction()
    {
        super(dataCommandName);
    }

    //---------------------------------------------------------------------
    /**
        Class constructor. <P>
        @param name transaction name
    **/
    //---------------------------------------------------------------------
    public LayawayDataTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves layaway data for a given layaway ID and training mode flag,
       which are packaged in a layaway object. <P>
       @param inputLayaway layaway containing search criteria
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public LayawayIfc readLayaway(LayawayIfc inputLayaway)
        throws DataException
    {                                   // begin readLayaway()
        if (logger.isDebugEnabled()) logger.debug(
                     "LayawayDataTransaction.readLayaway");

        LayawayIfc layaway = null;

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[2];
        dataActions[0] = createDataAction(inputLayaway,
                                          "ReadLayaway");
        dataActions[1] = createDataAction(null, 
                                          "ReadLayawayDescription");
        setDataActions(dataActions);

        // execute data request
        layaway = (LayawayIfc) getDataManager().execute(this);

        if (layaway == null)
        {
            throw new DataException(DataException.NO_DATA,
                                    "No layaway was returned to LayawayDataTransaction.");
        }
       
        if (layaway != null)
        {
            dataActions = new DataActionIfc[1];
            DataAction dataAction = new DataAction();
            dataAction.setDataOperationName("ReadLayawayPaymentHistoryInfo");
            dataAction.setDataObject(layaway);
            dataActions[0] = dataAction;
            setDataActions(dataActions);
            layaway = (LayawayIfc) getDataManager().execute(this);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "LayawayDataTransaction.readLayaway");

        return(layaway);
    }                                   // end readLayaway()

    //---------------------------------------------------------------------
    /**
       Updates an layaway.
       @param layaway LayawayIfc object to be updated
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void updateLayaway(LayawayIfc layaway)
        throws DataException
    {                                   // begin updateLayaway()
        if (logger.isDebugEnabled()) logger.debug(
                     "LayawayDataTransaction.updateLayaway");

        // set data action and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(layaway,
                                          "UpdateLayaway");
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "LayawayDataTransaction.updateLayaway");

    }                                   // end updateLayaway()
    
    //---------------------------------------------------------------------
    /**
       Inserts an layaway.
       @param layaway LayawayIfc object to be inserted
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void insertLayaway(LayawayIfc layaway)
        throws DataException
    {                                   // begin insertLayaway()
        if (logger.isDebugEnabled()) logger.debug(
                     "LayawayDataTransaction.insertLayaway");

        // set data action and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = createDataAction(layaway,
                                          "InsertLayaway");
        setDataActions(dataActions);

        // execute data request
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "LayawayDataTransaction.insertLayaway");

    }                                   // end insertLayaway()

    //---------------------------------------------------------------------
    /**
       Reads POS Layaway summaries from the data store based on a customer id.
       @param  customerID   The customer ID to use in the search for layaways.
       @return An array of Layaway Transaction summaries
       @exception  DataException upon error
    **/
    //---------------------------------------------------------------------
    public LayawaySummaryEntryIfc[] readLayawaysByCustomerID(LayawayIfc layaway)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( 
                     "LayawayTransaction.readLayawaysByCustomerID");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[2];
        
        dataActions[0] = createDataAction(layaway, 
                                          "ReadLayawaysByCustomerID");
        dataActions[1] = createDataAction(null, 
                                          "ReadLayawayDescriptions");
        setDataActions(dataActions);

        LayawaySummaryEntryIfc[] layawaySummaries 
            = (LayawaySummaryEntryIfc[])getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug( 
                    "LayawayTransaction.readLayawaysByCustomerID");

        return(layawaySummaries);
    }

    //---------------------------------------------------------------------
    /**
       Reads a POS Transaction from the data store; the TransactionIfc contains
       a LayawayIfc object which provides the LayawayID to the lookup 
       process.
       
       This code has been added to this class so that all the Layaway lookups can
       can be pointed at the same set of data sources.
       
       @param  transaction A Transaction that contains the key values
       required to restore the transaction from a
       persistent store.  These key values include
       the store id, workstation id, business day
       date, transaction sequence number, and
       transaction ID.
       @return  The Transaction that matches the key criteria, null if
       no Transaction matches.
       @exception  DataException upon error
    **/
    //---------------------------------------------------------------------
    public TransactionIfc readTransaction(TransactionIfc transaction)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "LayawayDataTransaction.readTransaction");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadTransaction");
        da.setDataObject(transaction);
        dataActions[0] = da;
        setDataActions(dataActions);

        TransactionIfc readTransaction =
          (TransactionIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "LayawayDataTransaction.readTransaction");

        return(readTransaction);
    }

    //---------------------------------------------------------------------
    /**
        Creates a data transaction.
        @param object the serialized object to be used in the data operation.
        @param name the name of the data action and operation.
        @return the new data action.
    **/
    //---------------------------------------------------------------------
    protected DataAction createDataAction(Serializable object, String name)
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(object);
        dataAction.setDataOperationName(name);
        return dataAction;
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number of this class.
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    
    /**
     * Returns all the tenders associated to a layaway
     * 
     * @param transaction
     * @return
     * @throws DataException
     */
    public TenderLineItemIfc[] readLayawayTenders(TransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("LayawayDataTransaction.readLayawayTenders");

        DataAction dataAction = createDataAction(transaction, "ReadLayawayTenders");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        TenderLineItemIfc[] layawayTenders = (TenderLineItemIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled())
            logger.debug("LayawayDataTransaction.readLayawayTenders");

        return (layawayTenders);
    }
    
    // ---------------------------------------------------------------------
    /**
       Method to default display string function.
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        StringBuilder strResult = 
          Util.classToStringHeader("LayawayDataTransaction",
                                   getRevisionNumber(),
                                   hashCode());
        return(strResult.toString());
    }
}
