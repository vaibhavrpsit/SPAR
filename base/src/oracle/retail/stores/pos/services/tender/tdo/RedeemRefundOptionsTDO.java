/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/RedeemRefundOptionsTDO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:49 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

/**
 * This TDO builds the beanModel for redeem refund options.
 */
public class RedeemRefundOptionsTDO extends RefundOptionsTDO
{    
    /* Build bean model.
     * @param attributeMap
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        BusIfc bus = (BusIfc)attributeMap.get(BUS);
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        // Get RDO version of transaction for use in some processing
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();
        
        // get new tender bean model
        TenderBeanModel model = new TenderBeanModel();
        // populate tender bean model w/ tender and totals info
        model.setTransactionTotals(txnRDO.getTenderTransactionTotals());
        model.setTenderLineItems(txnRDO.getTenderLineItemsVector());
        
        // Assumption: If this TDO is called, we have a RedeemTransactionIfc
        // set customer information
        StatusBeanModel sModel = getStatusBean(bus, txnRDO.getCustomer());
        
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        
        if (sModel != null)
        {
            model.setStatusBeanModel(sModel);        
        }
          
        model.setPromptAndResponseModel(parModel);
        
        // set the local navigation button bean model
        model.setLocalButtonBeanModel(getNavigationBeanModel());
        
        // This is a return
        model.setReturn(false);
        
        return model;
    }
    
    /**
     * builds status bean based on customer information
     * @param bus
     * @param customer
     * @return
     
    protected StatusBeanModel getStatusBean(BusIfc bus, CustomerIfc customer)
    {
        StatusBeanModel sModel = null;
        if (customer != null)
        {
            sModel = new StatusBeanModel();
            String[] vars = { customer.getFirstName(), customer.getLastName() };
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String pattern = utility.retrieveText("CustomerAddressSpec",
                                                  BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                                  TagConstantsIfc.CUSTOMER_NAME_TAG,
                                                  TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG,
                                                  LocaleConstantsIfc.USER_INTERFACE);
            String customerName = LocaleUtilities.formatComplexMessage(pattern,vars);
            sModel.setCustomerName(customerName);
        }
        return sModel;        
    }
    */
    /**
     * enables/disables tender buttons as they exist in enabledTypes array.
     * @param enabledTypes
     * @return
     */
    protected NavigationButtonBeanModel getNavigationBeanModel()
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        try
        {
            UtilityIfc util = Utility.createInstance();        
            String[] options = util.getParameterValueList("RedeemTransactionTenders");
            for (int i=0; i<options.length; i++)
            {    
                navModel.setButtonEnabled(options[i], true);
            }
        }
        catch(ADOException adoe)
        {
            
        }
        return navModel;
        
    }
}
