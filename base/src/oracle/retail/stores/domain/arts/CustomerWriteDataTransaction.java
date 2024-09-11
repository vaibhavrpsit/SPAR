/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CustomerWriteDataTransaction.java /main/18 2013/11/14 12:22:36 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  11/14/13 - configure the retry flag through the spring context
 *                         in persistenceContext.xml for the data transactions
 *    abondala  11/13/13 - when the pos server is rebooted, the first
 *                         webservice call never makes to the server becuase of
 *                         RMI lookup excetion. Solution is to retry the RMI
 *                         lookup.
 *    acadar    08/05/12 - XC refactoring
 *    acadar    08/02/12 - backward compatible
 *    acadar    08/01/12 - integration with jpa
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:43:43 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:39 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:43 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:25 PM  Robert Pearse
 *
 *   Revision 1.8  2004/09/23 00:30:49  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.7  2004/08/05 16:16:15  jdeleau
 *   @scr 6782 Use Factory when creating CustomerWriteDataTransaction
 *
 *   Revision 1.6  2004/04/09 16:55:43  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:29:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 07 2003 17:04:54   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Jun 03 2002 16:34:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:02   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:04:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:35:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.datareplication.DataReplicationCustomerEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    The DataTransaction to perform persistent write operations on the POS
    Customer object.
    @version $Revision: /main/18 $
    @see oracle.retail.stores.domain.arts.CustomerReadDataTransaction
**/
//-------------------------------------------------------------------------
public class CustomerWriteDataTransaction extends DataTransaction
                                     implements DataTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5322455283644347759L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.CustomerWriteDataTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/18 $";
    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String  dataUpdateName  = "CustomerWriteDataTransaction";
    /**
       The name that links this transaction to a command within DataScript.
       This is called by the DTM that updates the customer batch id's in the 
       local database for every customer that is successfully sent to the CentralOffice
       application.
    **/
    public static String  dataAddNameNotQueued = "CustomerAddTransactionNotQueued";
    /**
       local customer object
    **/
    protected CustomerIfc customer = null;

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public CustomerWriteDataTransaction()
    {
        super(dataUpdateName);
    }

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
       @param transaction name
    **/
    //---------------------------------------------------------------------
    public CustomerWriteDataTransaction(String name)
    {
        super(name);
    }

   
    
    //---------------------------------------------------------------------
    /**
       Saves a customer to the database.
       @param customer the Customer to save.
       @exception DataException when an error occurs
    **/
    //---------------------------------------------------------------------
    public void saveCustomer(CustomerIfc customer) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "CustomerWriteDataTransaction.saveCustomer");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        ARTSCustomer artsCustomer = new ARTSCustomer(customer);

        // Make sure that any source information from the customer is
        // maintained in the ArtsCustomer
        artsCustomer.setSource(customer.getSource());
        dataActions[0] = new SaveCustomerAction(this, artsCustomer);
        setDataActions(dataActions);
        CustomerIfc result = (CustomerIfc) getDataManager().execute(this);
        if(result != null)
        {
            customer.setCustomerID(result.getCustomerID());
        }
        if (logger.isDebugEnabled()) logger.debug(
                    "CustomerWriteDataTransaction.saveCustomer");
    }

  // ---------------------------------------------------------------------
    /**
     * Updates batch IDs for the listed customers. A count of records updated is
     * returned.
     * <P>
     *
     * @param transactionEntries array of customers entries
     * @return count of records updated
     * @exception DataException thrown if error occurs
     */
    // ---------------------------------------------------------------------
    public Integer updateCustomerBatchIDs(DataReplicationCustomerEntryIfc[] customers) throws DataException
    {
        return updateCustomerBatch(customers, "UpdateCustomerBatchIDs");
    }

    /**
     * Updates batch IDs for the listed customers. A count of records updated is
     * returned.
     * <P>
     *
     * @param transactionEntries array of customers entries
     * @param batchType the name of the data operation/action
     * @return count of records updated
     * @exception DataException thrown if error occurs
     */
    // ---------------------------------------------------------------------
    public Integer updateCustomerBatch(DataReplicationCustomerEntryIfc[] customers, String batchType)
            throws DataException
    { // begin updateCustomerBatch()
        Integer batchCount = new Integer(0);
        // set batch ID in initial transaction in list
        if (customers.length > 0)
        {
            DataActionIfc[] dataActions = new DataActionIfc[1];
            DataAction da = createDataAction(customers, batchType);
            dataActions[0] = da;
            setDataActions(dataActions);
            batchCount = (Integer)getDataManager().execute(this);
        }
        return (batchCount);
    }                                   // end updateCustomerBatch()

    // ---------------------------------------------------------------------
    /**
     * Updates customer sequence number in workstation table.
     * <P>
     *
     * @param dataConnection connection to the db
     * @param register the register information
     * @exception DataException upon error
     */
    // ---------------------------------------------------------------------
    public void updateCustomerSequenceNumber(RegisterIfc register) throws DataException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("CustomerWriteDataTransaction.updateCustomerSequenceNumber");
        }

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction dataAction = new DataAction();
        dataAction.setDataOperationName("UpdateCustomerSequenceNumber");
        dataAction.setDataObject(register);
        dataActions[0] = dataAction;
        setDataActions(dataActions);

        getDataManager().execute(this);

    }

    /**
     * Set the name on the transaction
     *
     * @param transactionName
     */
    public void setTransactionName(String transactionName)
    {
        super.setTransactionName(transactionName);
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

    //---------------------------------------------------------------------
    /**
       Method to default display string function.
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: CustomerWriteDataTransaction (Revision "
                                      + getRevisionNumber() + ") @"
                                      + hashCode());
        return(strResult);
    }
}
