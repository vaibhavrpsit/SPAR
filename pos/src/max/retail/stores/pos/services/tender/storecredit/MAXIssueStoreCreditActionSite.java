/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 *   Rev 1.0      Aug 23, 2021		Atul Shukla        	EWallet FES Implementation
 * ===========================================================================
 */
package max.retail.stores.pos.services.tender.storecredit;

// java imports
import java.util.HashMap;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//----------------------------------------------------------------------------
/**
 *   This site validates different conditions and throws exceptions based on
 *   various conditions
 *   $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
//----------------------------------------------------------------------------
public class MAXIssueStoreCreditActionSite extends PosSiteActionAdapter
{
    /**  */
    private static final long serialVersionUID = -7352626056576449111L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // Static Strings
    public static final String SUCCESS_LETTER = "Success";
    public static final String FAILURE_LETTER = "RefundOptions";
    public static final String STORE_CREDIT_MINIMUM = "StoreCreditMinimum";
    public static final String OVERTENDER_IN_A_RETURN = "OvertenderInAReturn";
    public static final String YES_LETTER = "Yes";
    public static final String NO_LETTER = "No";

    //------------------------------------------------------------------------
    /*
     * @param bus  The bus arriving at this site.
     */
    //------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // If we already have the store credit tender in cargo, we use that
        // other create new store credit tender
        TenderStoreCreditADO storeCreditTender = null;
        if (cargo.getTenderADO() == null)
        {
            // Get tender attributes
            HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();
            // add tender type
            tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.STORE_CREDIT);
           tenderAttributes.put(TenderConstants.STATE, TenderConstants.ISSUE);

            try
            {
                // create a new store credit tender
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                storeCreditTender = (TenderStoreCreditADO)factory.createTender(tenderAttributes);

                // update cargo with new tender.
                cargo.setTenderADO(storeCreditTender);
            }
            catch (ADOException adoe)
            {
                adoe.printStackTrace();
            }
            catch (TenderException e)
            {
                logger.error("Error creating Issue Store Credit Tender", e);
            }
        }
        else
        {
            storeCreditTender =  (TenderStoreCreditADO)cargo.getTenderADO();
        }

        // use transaction to validate refund limits
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            cargo.setTenderADO(storeCreditTender);
            if(txnADO instanceof ReturnableTransactionADOIfc )
            {
              boolean isReturnWithReceipt = ((ReturnableTransactionADOIfc)txnADO).isReturnWithReceipt();
              boolean isReturnWithOriginalRetrieved = ((ReturnableTransactionADOIfc)txnADO).isReturnWithOriginalRetrieved();
              txnADO.validateRefundLimits(storeCreditTender.getTenderAttributes(), isReturnWithReceipt, isReturnWithOriginalRetrieved);
            }
            storeCreditTender.checkPrePrintedStoreCreditParameter();
        }
        catch (TenderException e)
        {
            // There was a problem parsing the tender attributes data.
            TenderErrorCodeEnum error = e.getErrorCode();
            if (error == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                // must save tender in cargo for possible override
                cargo.setTenderADO(storeCreditTender);

                displayErrorDialog(bus, STORE_CREDIT_MINIMUM, DialogScreensIfc.ERROR);
            }
            else if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                displayErrorDialog(bus, OVERTENDER_IN_A_RETURN, DialogScreensIfc.ERROR);
            }

            else if (error == TenderErrorCodeEnum.CREATE_STORE_CREDIT)
            {
                // create new store credit number
                bus.mail(new Letter(NO_LETTER), BusIfc.CURRENT);
            }

            else if (error == TenderErrorCodeEnum.ENTER_STORE_CREDIT)
            {
                // enter store credit number
                bus.mail(new Letter(YES_LETTER), BusIfc.CURRENT);
            }
        }
    }

    //--------------------------------------------------------------------------------------
    /**
     * Show an error dialog
     * @param bus The bus arriving at the site.
     * @param name Name of the dialog message to be displayed
     * @param dialogType Type of error dialog
     */
    //--------------------------------------------------------------------------------------
    protected void displayErrorDialog(BusIfc bus, String name, int dialogType)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(dialogType);

        if (dialogType == DialogScreensIfc.ERROR)
        {
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, FAILURE_LETTER);
        }
        dialogModel.setArgs(
            new String[] {DomainGateway.getFactory()
                                       .getTenderTypeMapInstance()
                                       .getDescriptor(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT)});
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
      /**
          Depart method retrieves input.
          @param  bus     Service Bus
      **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        // MAX Rev 1.0 Change : Start
         boolean eWalletFlag=false;
      	if(cargo.getCurrentTransactionADO().toLegacy() instanceof MAXSaleReturnTransaction )
  		{
      		 eWalletFlag= ((MAXSaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy()).isEWalletTenderFlag();
      logger.info(" Inside MAXIssueStoreCreditActionSite Ewallet flag value   "+ eWalletFlag );
  		}
          // changes end here
          
          // Get the amount and put in the cargo

    	if(!eWalletFlag)
      	{
        // Grab the entered or displayed amount
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        String amount = null;
        Number value = LocaleUtilities.parseCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT));
        if (value!=null)
        {
        	amount = value.toString();
        }
        CurrencyIfc balance = DomainGateway.getBaseCurrencyInstance(amount);
        cargo.getTenderAttributes().put(TenderConstants.AMOUNT, balance.getDecimalValue().toString());
     
    }
    else
    {
    	MAXTenderCargo mCargo=null;
    	if(cargo instanceof MAXTenderCargo)
    	{
    		mCargo=(MAXTenderCargo)cargo;
    	}
 
    HashMap<String, Object> tenderAttribute=mCargo.getTenderAttributes();
    String amount=tenderAttribute.get(TenderConstants.AMOUNT).toString();
       CurrencyIfc balance = DomainGateway.getBaseCurrencyInstance(amount).abs();
        cargo.getTenderAttributes().put(TenderConstants.AMOUNT, balance.getDecimalValue().toString());	
    }
    }
    // MAX Rev 1.0 Change : End
}
