package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

public class StoreOrderReadDataTransaction extends DataTransaction {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.StoreOrderReadDataTransaction.class);

    /**
     * The name that links this transaction to a command within DataScript.
     */
    public static String dataCommandName="StoreOrderReadDataTransaction";
    
    /**
     * array list of data actions
     */
    protected List<DataAction> actionsList = new ArrayList<DataAction>();

    /**
     * Class constructor.
     */
    public StoreOrderReadDataTransaction()
    {                                   // begin StoreOrderReadDataTransaction()
        super(dataCommandName);
    }                                   // end StoreOrderReadDataTransaction()

    /**
     * Class constructor.
     * @param name data command name
     */
    public StoreOrderReadDataTransaction(String name)
    {                                   // begin StoreOrderReadDataTransaction()
        super(name);
    } 
    /**
     * Retrieves summaries of orders last-modified within a specified
     * order id, customer info, date range or card info, and having one of a specified list of statuses
     * for a given store.
     * @param searchCriteria order search criteria
     * @return array of summaries of orders
     * @exception  DataException when an error occurs.
     */
    public OrderSummaryEntryIfc[]
    retrieveOrderSummary(OrderSearchCriteriaIfc searchCriteria)
        throws DataException
    {                                   // begin retrieveOrderSummary()
        if (logger.isDebugEnabled()) logger.debug(
                "StoreOrderReadDataTransaction.RetrieveOrderSummary");

        // set data actions and execute
        addAction("RetrieveOrderSummary", searchCriteria);
        setActions();

        // execute data request
        OrderSummaryEntryIfc[] orderSummaryList = (OrderSummaryEntryIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                   "" + "StoreOrderReadDataTransaction.RetrieveOrderSummary" + "");

        return(orderSummaryList);
    }
    
    /**
     * Adds a data action to the list.
     * @param operationName data operation name
     * @param dataObject data object
     */
    protected void addAction(String operationName,
                             Serializable dataObject)
    {                                   // begin addAction()
        DataAction da = new DataAction();
        da.setDataObject(dataObject);
        da.setDataOperationName(operationName);
        actionsList.add(da);
    }                                   // end addAction()

    /**
     * Adds a data action to the list.
     * @param operationName data operation name
     */
    protected void addAction(String operationName)
    {                                   // begin addAction()
        addAction(operationName, (Serializable) null);
    }                                   // end addAction()

    /**
     * Sets data actions. The ArrayList of data actions is copied into
     * an array of data actions and these are set in the transaction.
     * The array list is then re-initialized.
     */
    protected void setActions()
    {                                   // begin setActions()
        DataActionIfc[] actions = new DataActionIfc[actionsList.size()];
        actionsList.toArray(actions);
        setDataActions(actions);
        actionsList = new ArrayList();
    }                                   // end setActions()

    /**
     * Returns the revision number of this class.
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}
