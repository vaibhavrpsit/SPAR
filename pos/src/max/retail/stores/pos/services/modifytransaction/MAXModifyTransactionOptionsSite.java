/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev 1.1		Apr 29, 2017		Mansi Goel		Changes to disable Gift Registry button
 *	Rev	1.0 	Oct 13, 2016		Ashish Yadav	Initial Revision : Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionOptionsSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the transaction options menu.
    <p>
    @version $Revision: 5$
**/
//--------------------------------------------------------------------------
public class MAXModifyTransactionOptionsSite extends ModifyTransactionOptionsSite
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:62; $EKW";
    /**
        action identifier for void action
    **/
    private static final String ACTION_VOID     = "Void";
    /**
        action identifier for suspend action
    **/
    private static final String ACTION_SUSPEND  = "Suspend";
    /**
        action identifier for retrieve action
    **/
    private static final String ACTION_RETRIEVE = "Resume";

    /**
        action identifier for layaway action
    **/
    private static final String ACTION_LAYAWAY = "Layaway";
    
    /**
        action identifier for layaway action
    **/
    private static final String ACTION_GIFT_REGISTRY = "GiftRegistry";
    
    /**
     * actionidentifier for gift receipt
     */
    private static final String ACTION_GIFT_RECEIPT = "GiftReceipt";
    /**
       action identifier for send action
    **/
    private static final String ACTION_SEND = "Send";
    
    /**
    action identifier for send action
    **/
    private static final String ACTION_TAX = "Tax";
    /**
     * Tax inclusive flag
     */
    protected boolean taxInclusiveFlag =
            Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);


    //----------------------------------------------------------------------
    /**
        Shows the screen for all the options for ModifyTransaction
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MAXModifyTransactionCargo cargo = (MAXModifyTransactionCargo)bus.getCargo();

        // This is the void rule:
        //
        //      1. If there is a transaction of any sort, transaction void cannot be perfomed.
        //
        // These are the suspend/retrieve rules:
        //
        //      1. If the system is not running POS (i.e. CrossReach),
        //         the suspend/retrieve buttons will be disabled.
        //
        //      2. If there is a transaction in the cargo, then the suspend
        //         will be enabled and the retrieve disabled.
        //
        //      3. If there is NOT a transaction in the cargo, then the suspend
        //         will be disabled and the retrieve enabled.
        //
        // This code sets the booleans to the correct vales before setting up the
        // models.

        // Initialize the booleans
        
      //*****************************izhar
    	
   	        UtilityIfc utility;
       // String letter = "";
        boolean offline = false;
		boolean showDialog = false;
   	//******************************end
		
		
        boolean voidEnabled     = false;
        boolean suspendEnabled  = false;
        boolean retrieveEnabled = false;
        boolean layawayEnabled  = true;
        //Changes for Rev 1.1 : Starts
        boolean giftRegistryEnabled  = false;
      //Changes for Rev 1.1 : Ends
        boolean giftReceiptEnabled  = true;
        boolean sendEnabled = true;

        RetailTransactionIfc transaction = cargo.getTransaction();

        
    	//*****************************izhar
    	try {
			utility = Utility.createInstance();
		
		// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
			offline = isSystemOffline(utility);
			FinancialCountIfc fci = cargo.getRegister().getCurrentTill().getTotals()
					.getCombinedCount().getExpected();
			FinancialCountTenderItemIfc[] fctis = fci.getTenderItems();
			String  tillFloat="0.00";
			for (int i = 0; i < fctis.length; i++) {
				if(fctis[i].getDescription().equalsIgnoreCase("CASH")){
					tillFloat=fctis[i].getAmountTotal().toString();
				}
					
			}

		String limitallowed = utility.getParameterValue(
				"CashThresholdAmount", "50000.00");
		double tf = Double.parseDouble(tillFloat);
		double cta = Double.parseDouble(limitallowed);
		if (tf >= cta)
			showDialog = true;
		// addition ends
		// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
		/*if (showDialog && !offline) {

				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("cashthresholdamounterror");
				model.setType(DialogScreensIfc.ERROR);

				 model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "blocktransaction");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		else {*/
			   if (transaction == null)
		        {
		            voidEnabled = true;
		        }

		        // If the system is running CrossReach, getSystemPos() will be
		        // false.
		        if (cargo.getSystemPos())
		        {
		            // If there is not a transaction set retrieve to true
		            if (transaction == null)
		            {
		                retrieveEnabled = true;
		                giftReceiptEnabled = false;
		            }
		            // If there is a transaction in progress with atleast one line item present,
		            // then set suspend to true
		            else if(transaction.getLineItemsVector()!=null && transaction.getLineItemsVector().size()>0)
		            {
		                suspendEnabled  = true;
		            }
		        }

		        // If return items, send items, CrossReach items, or layaway in progress
		        // disable Layaway button
		        if ((transaction != null) && (transaction.containsOrderLineItems() ||
		            transaction.getTransactionType() != TransactionIfc.TYPE_SALE ||
		            transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE ||
		            ((SaleReturnTransactionIfc)transaction).hasSendItems() ||
		            ((SaleReturnTransactionIfc)transaction).isTransactionLevelSendAssigned() ||
		            ((SaleReturnTransactionIfc)transaction).containsReturnLineItems()) )
		        {
		            layawayEnabled = false;
		        }
		        
		        // do not allow gift registration on returned items
		        if ((transaction != null) && ((SaleReturnTransactionIfc)transaction).containsReturnLineItems())
		        {
		            giftRegistryEnabled = false;
		            giftReceiptEnabled = false;
		        }
		        
		        if (transaction != null)
		        {
		            if (((SaleReturnTransactionIfc)transaction).isTransactionLevelSendAssigned() ||
		                (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE) ||
		                (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT) ||
		                (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_COMPLETE) ||
		                (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_DELETE) ||
		                (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE))
		           {
		               sendEnabled = false;
		           }
		        }           

		        // Setup the models.
		        POSBaseBeanModel pModel = new POSBaseBeanModel();
		        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
		        nModel.setButtonEnabled(ACTION_VOID, voidEnabled);
		        nModel.setButtonEnabled(ACTION_SUSPEND, suspendEnabled);
		        nModel.setButtonEnabled(ACTION_RETRIEVE, retrieveEnabled);
		        nModel.setButtonEnabled(ACTION_LAYAWAY, layawayEnabled);
		        nModel.setButtonEnabled(ACTION_GIFT_REGISTRY, giftRegistryEnabled);
		        nModel.setButtonEnabled(ACTION_GIFT_RECEIPT, giftReceiptEnabled);
		        nModel.setButtonEnabled(ACTION_SEND, sendEnabled);
		        // disable the button in the VAT enabled environment
		       
		        if(taxInclusiveFlag)
		        {
		            nModel.setButtonEnabled(ACTION_TAX, false);
		        }
		        if (showDialog && !offline) {
	        		//nModel.setButtonEnabled(ACTION_VOID, false);
			        nModel.setButtonEnabled(ACTION_SUSPEND, false);
			        nModel.setButtonEnabled(ACTION_RETRIEVE, false);
			        nModel.setButtonEnabled(ACTION_LAYAWAY, false);
			        nModel.setButtonEnabled(ACTION_GIFT_REGISTRY, false);
			        nModel.setButtonEnabled(ACTION_GIFT_RECEIPT, false);
			        nModel.setButtonEnabled(ACTION_SEND, false);
			        nModel.setButtonEnabled(ACTION_TAX, false);
			        nModel.setButtonEnabled("SalesAssociate", false);
	        }
		        pModel.setLocalButtonBeanModel(nModel);

		        uiManager.showScreen(POSUIManagerIfc.TRANS_OPTIONS, pModel);
			
		//}
		}catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		} 
		//******************************end
     
    }
    // added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
	protected boolean isSystemOffline(UtilityIfc utility) {
		DispatcherIfc d = Gateway.getDispatcher();
		DataManagerIfc dm = (DataManagerIfc) d.getManager(DataManagerIfc.TYPE);
		boolean offline = true;
		try {
			if (dm.getTransactionOnline(UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME)
					|| dm.getTransactionOnline(UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME)) {
				offline = false;
			}
		} catch (DataException e) {
			e.printStackTrace();
		}
		return offline;

	}
	//end
}
