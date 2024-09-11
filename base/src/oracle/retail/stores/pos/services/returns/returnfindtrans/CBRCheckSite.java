/* ===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/CBRCheckSite.java /main/5 2014/03/11 17:13:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/11/14 - add support for returning ASA ordered items
 *    rgour     05/24/13 - Added check if return is CBR and CBR is not enabled
 *    rgour     04/01/13 - CBR cleanup
 *    rsnayak   04/11/12 - Normal return fix
 *    rsnayak   04/04/12 - Cross Border Return
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Site to check if the transaction in the cargo contains a cross border return.
 * 
 * @author rsnayak
 * @since 14.0
 */
@SuppressWarnings("serial")
public class CBRCheckSite extends PosSiteActionAdapter
{
    /** Letter mailed if CBR is not enabled or the trans is not a CBR. */
    public static final String LETTER_NOT_CROSS_BORDER_RETURN = "NotCBRReturn";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // check if CBR is turned on
        boolean cbrEnabled = isCrossBorderReturnEnabled(bus);
        
     // get the transaction to work with
        SaleReturnTransactionIfc originalTransaction = null;
        // we are using the same site in two different tours
        if (bus.getCargo() instanceof ReturnFindTransCargo)
        {
            ReturnFindTransCargo findTransCargo = (ReturnFindTransCargo)bus.getCargo();
            originalTransaction = findTransCargo.getOriginalTransaction();
        }
        else if (bus.getCargo() instanceof ReturnCustomerCargo)
        {
            ReturnCustomerCargo rtCustomerCargo = (ReturnCustomerCargo)bus.getCargo();
            originalTransaction = rtCustomerCargo.getOriginalTransaction();
        }

        // determine if cross border return
        boolean isTransCbr = isTransactionCBRElligible(bus, originalTransaction);
        
        if (cbrEnabled)
        {            
            // mail letter or display dialog
            if (!isTransCbr)
            {
                bus.mail(LETTER_NOT_CROSS_BORDER_RETURN);
            }
            else
            {
                displayCrossBorderReturnMessage(bus, originalTransaction);
            }
        }
        else if (isTransCbr)
        {
            displayCrossBorderReturnError(bus);
        }
        else 
        {
            bus.mail(LETTER_NOT_CROSS_BORDER_RETURN);
        }
    }

    /**
     * This method checks whether the Cross Border Return feature is supported
     * or not. By default, it returns false.
     * 
     * @param bus
     */
    protected boolean isCrossBorderReturnEnabled(BusIfc bus)
    {
        boolean isSupported = false;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            return pm.getBooleanValue(ParameterConstantsIfc.RETURN_CrossBorderReturn);
        }
        catch (ParameterException e)
        {
            logger.error("Could not determine setting for parameter \""
                    + ParameterConstantsIfc.RETURN_CrossBorderReturn + "\".", e);
        }

        return isSupported;
    }

    /**
     * This method checks whether the Cross Border Return feature is based on
     * the sale transaction's country or its currency. This method defaults to
     * country.
     * 
     * @param bus
     */
    protected boolean isCrossBorderReturnBasedOnCountry(BusIfc bus)
    {
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            String determineCBRBasedOn = pm
                    .getStringValue(ParameterConstantsIfc.RETURN_DetermineCrossBorderReturnBasedOn);
            return ParameterConstantsIfc.RETURN_DetermineCrossBorderReturnBasedOn_COUNTRY.equals(determineCBRBasedOn);
        }
        catch (Exception e)
        {
            logger.error("Failed to retrieve or convert to parameter \""
                    + ParameterConstantsIfc.RETURN_DetermineCrossBorderReturnBasedOn + "\".", e);
            logger.info("Defaulting to country-based Cross Border Returns.");
        }
        // return country-based by default
        return true;
    }

    /**
     * Check if the transaction is Cross Border Return.
     * 
     * @param bus
     * @param originalTransaction
     * @return
     */
    protected boolean isTransactionCBRElligible(BusIfc bus, SaleReturnTransactionIfc originalTransaction)
    {
        // should we check if CBR by country or currency?
        boolean determineCBRbyCountry = isCrossBorderReturnBasedOnCountry(bus);
        if (determineCBRbyCountry)
        {
            StoreStatusIfc storeStatus = ((AbstractFinancialCargoIfc)bus.getCargo()).getStoreStatus();
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting country code from store status in cargo: " + storeStatus);
            }
            String currentCountry = storeStatus.getStore().getAddress().getCountry();
            if (logger.isDebugEnabled())
            {
                logger.debug("Checking against current country which is \"" + currentCountry + "\".");
            }
            if (Util.isEmpty(originalTransaction.getTransactionCountryCode()))
            {
                logger.warn("The returned transaction does not have a country code specified.");
            }
            return !currentCountry.equals(originalTransaction.getTransactionCountryCode());
        }
        else
        {
            String currentCurrency = DomainGateway.getBaseCurrencyType().getCurrencyCode();
            return !currentCurrency.equals(originalTransaction.getCurrencyType().getCurrencyCode());
        }
    }

    /**
     * Display Cross Border Return Prompt
     * 
     * @param bus
     * @param originalTransaction
     */
    protected void displayCrossBorderReturnMessage(BusIfc bus, SaleReturnTransactionIfc originalTransaction)
    {
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogBean = new DialogBeanModel();
        String args[] = new String[2];
        args[0] = originalTransaction.getWorkstation().getStoreID();
        CurrencyTypeIfc originalCurrency = originalTransaction.getCurrencyType();
        args[1] = (originalCurrency != null) ? originalCurrency.getCurrencyCode() : "???";
        dialogBean.setResourceID("CrossBorderReturns");
        dialogBean.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogBean.setArgs(args);
        dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, "crossborderreturn");
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
    }

    /**
     * Display Cross Border Return is not Enabled Dialog
     * 
     * @param bus
     * @param uiManager
     */
    protected void displayCrossBorderReturnError(BusIfc bus)
    {
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogBean = new DialogBeanModel();
        dialogBean.setResourceID("CrossBorderReturnsError");
        dialogBean.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
    }
}
