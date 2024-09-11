/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/ServiceAlertCargo.java /main/12 2013/08/08 16:51:15 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified to support returns by order.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:10 PM  Robert Pearse   
 *
 *   Revision 1.5.4.1  2004/11/05 21:54:44  bwf
 *   @scr 7529 Save screen used to use in next site to avoid reoccuring crash when site is changed.
 *
 *   Revision 1.5  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/15 21:43:28  baa
 *   @scr 0 continue moving out deprecated files
 *
 *   Revision 1.3  2004/02/12 16:51:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 05 2003 22:46:38   cdb
 * Modified to implement SaleCargoIfc in _360commerce.
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:06:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 05 2002 14:51:42   jriggins
 * Included a static private method which initializes the static final operatorIdTextPrompt property.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:03:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 09 2002 14:13:42   dfh
 * updates to try to use the new security access code,
 * removed a lot of methods and data members, added
 * getAccessFunctionID() to return SERVICE_ALERT
 * Resolution for POS SCR-184: CR/Svc Alert, no security access displayed for F3/Service
 * 
 *
 *    Rev 1.0   Sep 24 2001 13:05:36   MPM
 *
 * Initial revision.
 *
 * 
 *    Rev 1.1   Sep 17 2001 13:13:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.services.order.common.OrderViewCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * The ServiceAlertCargo contains the data required by the service. This cargo
 * also holds any data which might be collected in or passed to this service and
 * required by any services called by this service. The data required by nested
 * service will be placed in that services LaunchShuttle.
 * 
 * @version $Revision: /main/12 $
 */
public class ServiceAlertCargo extends SaleCargo implements SaleCargoIfc, OrderCargoIfc, OrderViewCargoIfc
{
    // serialVersionUID
    private static final long serialVersionUID = -3831587277221971367L;
    /** entry selected from service alert list */
    protected AlertEntryIfc selectedEntry = null;
    /** holds the retrieved OrderIfc object */
    protected OrderIfc order = null;
    /** the retrieved EMessage object */
    protected EMessageIfc message = null;
    /** data manager return code */
    protected int dataExceptionErrorCode = 0;
    /** flag for printOrder service display */
    protected boolean viewOrder = true;
    /** Used to make sure we don't keep trying on DB error */
    protected boolean retrieveListFailed = false;

    // All the prompts should be defined in the operator ID service - not here
    protected static final String operatorIdPromptText =  initOperatorIdPromptText();
    
    /** screen name used for listnewalertentriesSite */
    protected String screenNameUsed = null;

    /**
     * Sets the Selected Entry
     * 
     * @param selectedEntry new value
     */
    public void setSelectedEntry(AlertEntryIfc selectedEntry)
    {
        this.selectedEntry = selectedEntry;
    }

    /**
     * Returns the Selected Entry
     * 
     * @return AlertEntryIfc selected entry
     */
    public AlertEntryIfc getSelectedEntry()
    {
        return selectedEntry;
    }

    /**
     * Returns the Selected Entry
     * 
     * @param value boolean flag
     */
    public void setRetrieveListFailed(boolean value)
    {
        retrieveListFailed = value;
    }

    /**
     * Returns the value of the flag
     * 
     * @return boolean retrieveListFailed
     */
    public boolean retrieveListFailed()
    {
        return retrieveListFailed;
    }

    /**
     * Gets the selectedOrder property value.
     * 
     * @return OrderIfc the order property value.
     * @see #setSelectedOrder
     */
    public OrderIfc getOrder()
    {
        return order;
    }

    /**
     * Sets the selectedOrder property value.
     * 
     * @param newOrder the new value for the property.
     * @see #getSelectedOrder
     */
    public void setOrder(OrderIfc newOrder)
    {
        order = newOrder;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.order.common.OrderCargoIfc#setOrderID(java.lang.String)
     */
    @Override
    public void setOrderID(String id)
    {
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.order.common.OrderCargoIfc#getOrderID()
     */
    @Override
    public String getOrderID()
    {
        return (getOrder() != null)? getOrder().getOrderID() : null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.order.common.OrderCargoIfc#getOrderTransaction()
     */
    @Override
    public OrderTransactionIfc getOrderTransaction()
    {
        return (getTransaction() instanceof OrderTransactionIfc)? (OrderTransactionIfc)getTransaction() : null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.order.common.OrderCargoIfc#setOrderTransaction(oracle.retail.stores.domain.transaction.OrderTransactionIfc)
     */
    @Override
    public void setOrderTransaction(OrderTransactionIfc orderTransaction)
    {
    }

    /**
     * Gets the message property value.
     * 
     * @return EMessageIfc message
     */
    public EMessageIfc getSelectedMessage()
    {
        return message;
    }

    /**
     * Sets the message property value.
     * 
     * @param message EMessageIfc value for the property.
     */
    public void setSelectedMessage(EMessageIfc message)
    {
        this.message = message;
    }

    /**
     * Sets the flag that indicates to the PrintOrder service whether an Order
     * detail screen should be displayed.
     * 
     * @param value boolean flag
     */
    public void setViewOrder(boolean value)
    {
        viewOrder = value;
    }

    /**
     * Returns the flag that indicates to the PrintOrder service whether an
     * Order detail screen should be displayed.
     * 
     * @return boolean flag
     */
    public boolean viewOrder()
    {
        return viewOrder;
    }

    /**
     * Gets the cashier ID prompt Overrides the one defined in PosCargo.
     * 
     * @return String prompt text.
     */
    public String getOperatorIdPromptText()
    {
        return operatorIdPromptText;
    }

    /**
     * Returns the function ID whose access is to be checked.
     * 
     * @return int Role Function ID
     */
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.SERVICE_ALERT;
    }

    /**
     * Returns a string created from the
     * oracle.retail.stores.pos.config.bundles.operatoridText bundle. It is used
     * to initialize the static final operatorIdTextPrompt property
     * 
     * @return String
     */
    private static String initOperatorIdPromptText()
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String operatorIdPromptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.OPERATORID_BUNDLE_NAME, "OperatorIdentificationPrompt", "Enter cashier ID.");

        return operatorIdPromptText;
    }

    /**
     * This method gets the screen name to use.
     * 
     * @return Returns the screenNameUsed.
     */
    public String getScreenNameUsed()
    {
        return screenNameUsed;
    }

    /**
     * This method sets the screen name to use.
     * 
     * @param screenNameUsed The screenNameUsed to set.
     */
    public void setScreenNameUsed(String screenNameUsed)
    {
        this.screenNameUsed = screenNameUsed;
    }

    @Override
    public boolean isReadOrderFromSummary()
    {
        return false;
    }

    @Override
    public void setReadOrderFromSummary(boolean readOrderFromSummary)
    {
    }
}
