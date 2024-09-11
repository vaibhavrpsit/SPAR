/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.2 	May 14, 2024		Kamlesh Pant		Store Credit OTP:
 *	Rev 1.1     June 15,2017        Nayya Gupta        	Gst defect fixes
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.capturecustomerinfo;

import java.util.ArrayList;
import java.util.zip.DataFormatException;



import max.retail.stores.domain.MAXUtils.MAXIGSTTax;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXReadTaxOnPLUItem;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetail;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.pos.services.tender.tdo.CaptureCustomerInfoTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site validates any information entered by the user at the
 * CaptureCustomerInfo site. Currently, only the postal code is checked.
 * 
 * @author kph
 */
public class MAXValidateCustomerInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3262810430994077343L;

    public static final String SITENAME = "ValidateCustomerInfoSite";
private String toState= null;

    /**
     * Part of the capture customer info use case.
     * 
     * @param bus the bus.
     */
    @Override
    public void arrive(BusIfc bus)
    {
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel) ui.getModel(cargo.getScreenType());

        // Check to see if the postal code is required.  The boolean value in the
        // model is set by the bean during the updateModel() call.
        if (!model.isPostalCodeRequired())
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            return;
        }
        // We store the model (conveniently) in the cargo so that when we return to
        // the CaptureCustomerInfoSite we can reuse the information on the model.
        cargo.setModel(model);

        // Set up an address with the info from the model, and verify the postal code.
        AddressIfc address = DomainGateway.getFactory().getAddressInstance();
        address.setCountry(model.getCountry());
        address.setPostalCode(model.getPostalCode());
      //Change for Rev 1.0 : Starts
        if(model.getStateName().equalsIgnoreCase("Select State")){
        	 DialogBeanModel dialogModel = new DialogBeanModel();
             dialogModel.setResourceID("InvalidState");
             dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
             dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
             // Display the dialog.
             ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        
      //Change for Rev 1.2 : Starts
        else if(model.getPhoneNumber().toString().startsWith("1")||model.getPhoneNumber().toString().startsWith("0")
        		||model.getPhoneNumber().toString().startsWith("2"))
        {
       	 DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidMobile");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
      //Change for Rev 1.2 : Ends
        
        else{
        	//Change for Rev 1.0 : Ends
	        try
	        {
	            String postalString = address.validatePostalCode(address.getPostalCode(), address.getCountry());
	            // If an exception is not thrown, then the following will execute.
	            address.setPostalCode(postalString);
	            toState=model.getStateName();
	            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
	        }
	        catch (DataFormatException e)
	        {
	            // The postal code is invalid, so display a dialog.
	            DialogBeanModel dialogModel = new DialogBeanModel();
	            dialogModel.setResourceID("InvalidPostalCode");
	            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
	            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
	            // Display the dialog.
	            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	        }
        }
    }

    /**
     * Part of the capture customer info use case.
     */
    @Override
    public void depart(BusIfc bus)
    {
    	
        // Check for success before setting up the cargo.
        if (CommonLetterIfc.SUCCESS.equals(bus.getCurrentLetter().getName()))
        {
            TDOUIIfc tdo = null;
            // Create the tdo object.
            try
            {
                tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CaptureCustomerInfo");
            }
            catch (TDOException tdoe)
            {
                tdoe.printStackTrace();
            }
            CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
            CaptureCustomerIfc customer = cargo.getCustomer();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // Copy the model back onto the customer.
            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)ui.getModel(cargo.getScreenType());

            if (customer == null)
            {
                customer = DomainGateway.getFactory().getCaptureCustomerInstance();
            }
            ((CaptureCustomerInfoTDO) tdo).modelToCustomer(model, bus, customer);

            if(model.isBusinessCustomer())
            {
              customer.setBusinessCustomer(true);
              customer.setCustomerName(model.getOrgName());
            }

            // Update the customer object with information from the current
            // transaction.  Necessary for db updates.
            TransactionIfc transaction = cargo.getTransaction();
            if (transaction != null)
            {
                customer.setTransactionID(transaction.getFormattedTransactionSequenceNumber());
                customer.setStoreID(transaction.getWorkstation().getStoreID());
                customer.setWsID(transaction.getWorkstation().getWorkstationID());
                customer.setBusinessDay(transaction.getBusinessDay());
                // Make certain the customer is set properly in the cargo.
                transaction.setCaptureCustomer(customer);
                cargo.setCustomer(customer);

                // Journal captured information
                JournalManagerIfc jmi = (JournalManagerIfc)
                bus.getManager(JournalManagerIfc.TYPE);

                jmi.journal(transaction.getCashier().getEmployeeID(),
                    transaction.getTransactionID(),
                    model.getJournalString());
                
              //Change for Rev 1.0 : Starts
	            if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc){
	            	((MAXSaleReturnTransactionIfc) cargo.getTransaction()).setCaptureCustomer(true);
	            	
	            	if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc){
	        			//MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc)cargo.getTransaction();
	        			if(((MAXSaleReturnTransactionIfc)transaction).isGstEnable() &&  ((MAXSaleReturnTransactionIfc)transaction).getHomeStateCode()!= null){
	        				if( ((MAXSaleReturnTransactionIfc)transaction).getHomeStateCode().equalsIgnoreCase(transaction.getCaptureCustomer().getState()))
	        					((MAXSaleReturnTransactionIfc)transaction).setIgstApplicable(false);
	        				else
	        					((MAXSaleReturnTransactionIfc)transaction).setIgstApplicable(true);
	        				((MAXSaleReturnTransactionIfc)transaction).setHomeState(((MAXSaleReturnTransactionIfc)transaction).getHomeState());
	        				((MAXSaleReturnTransactionIfc)transaction).setToState(toState);
	        			}
	        			else
	        				 ((MAXSaleReturnTransactionIfc) transaction).setIgstApplicable(false);
	            	}

	            }
	          //Change for Rev 1.0 : Ends
            }
          //Change for Rev 1.0 : Starts
            if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc 
            		//&& ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isTransactionLevelSendAssigned()
            		&&((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isIgstApplicable()
            		&&((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getLineItems().length==1){
            	//Recalculate the tax
            	recalculateTax((MAXSaleReturnTransactionIfc)transaction);		
            	((MAXSaleReturnTransactionIfc)transaction).setTransactionTotals(DomainGateway.getFactory().getTransactionTotalsInstance());
            	((MAXSaleReturnTransactionIfc)transaction).getTransactionTotals().updateTransactionTotals(
            			((MAXSaleReturnTransactionIfc)transaction).getItemContainerProxy().getLineItems(),
            			((MAXSaleReturnTransactionIfc)transaction).getItemContainerProxy().getTransactionDiscounts(),
            			((MAXSaleReturnTransactionIfc)transaction).getItemContainerProxy().getTransactionTax()
						);	
            
            }
          //Change for Rev 1.0 : Ends
        }
        

    }
//Change for Rev 1.3 : Starts
		protected void recalculateTax(MAXSaleReturnTransactionIfc transaction){
			if(((MAXPLUItemIfc)((MAXSaleReturnLineItemIfc)transaction.getItemContainerProxy().getLineItems()[0]).getPLUItem()).getTaxAssignments()!= null){
			int taxCategory=((MAXPLUItemIfc)((MAXSaleReturnLineItemIfc)((MAXSaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItems()[0]).getPLUItem()).getTaxAssignments()[0].getTaxCategory();
			
			//check for -1
			MAXIGSTTax igstTax = new MAXIGSTTax();
			igstTax.setTaxCategory(String.valueOf(taxCategory));
			igstTax.setStoreId(((MAXSaleReturnTransactionIfc) transaction).getTransactionIdentifier().getStoreID());
			igstTax.setFromRegion(((MAXSaleReturnTransactionIfc) transaction).getHomeState());
			igstTax.setToRegion(((MAXSaleReturnTransactionIfc) transaction).getToState());
			MAXReadTaxOnPLUItem tax = new MAXReadTaxOnPLUItem();
			tax = (MAXReadTaxOnPLUItem) DataTransactionFactory
					.create(MAXDataTransactionKeys.ReadIGSTTaxTransactions);
			ArrayList<MAXTaxAssignment> taxAssignment = null;
				 try {
					  taxAssignment=tax.readTax(igstTax);
				} catch (DataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//Rev 1.1 changes starts
			if(taxAssignment == null || taxAssignment.isEmpty())
			{
				logger.info("tax cannot be calculated on item as no tax details found in tax category table for tostate"+igstTax.getToRegion());
				MAXSaleReturnLineItemIfc srli = ((MAXSaleReturnLineItemIfc)((MAXSaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItems()[0]);
				((MAXPLUItemIfc)srli.getPLUItem()).setTaxAssignments(null);	
				srli.setLineItemTaxBreakUpDetails(null);
				return;
			}
			//rev 1.1 changes ends
			// Get the line items from the retrieve transaction
			// Process each line item
			MAXSaleReturnLineItemIfc srli = ((MAXSaleReturnLineItemIfc)((MAXSaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItems()[0]);
			
			CurrencyIfc taxinclusiveSellingPrice =srli.getItemPrice().getExtendedDiscountedSellingPrice();
			
			
			 MAXLineItemTaxBreakUpDetailIfc lineItemTaxBreakUpDetail = new MAXLineItemTaxBreakUpDetail();
			  lineItemTaxBreakUpDetail.setTaxableAmount(taxinclusiveSellingPrice.multiply(((MAXTaxAssignmentIfc)taxAssignment.get(0)).getTaxableAmountFactor()));
			  lineItemTaxBreakUpDetail.setTaxAmount(taxinclusiveSellingPrice.multiply(((MAXTaxAssignmentIfc)taxAssignment.get(0)).getTaxAmountFactor()));
			  lineItemTaxBreakUpDetail.setTaxRate(((MAXTaxAssignmentIfc) taxAssignment.get(0)).getTaxRate());
			  lineItemTaxBreakUpDetail.setTaxAssignment(((MAXTaxAssignmentIfc)taxAssignment.get(0)));
			
				ArrayList lineItemTaxBreakUpList = new ArrayList();
				lineItemTaxBreakUpList.add(lineItemTaxBreakUpDetail);

				MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetailList = new MAXLineItemTaxBreakUpDetail[lineItemTaxBreakUpList.size()];
				for (int k = 0; k <= lineItemTaxBreakUpDetailList.length - 1; k++) {
					lineItemTaxBreakUpDetailList[k] = (MAXLineItemTaxBreakUpDetailIfc) lineItemTaxBreakUpList
							.get(k);
				}     
				((MAXItemTaxIfc) (srli.getItemPrice().getItemTax())).setLineItemTaxBreakUpDetail(lineItemTaxBreakUpDetailList);
				
				MAXTaxAssignmentIfc[] taxAssignmentIfc = new MAXTaxAssignmentIfc[taxAssignment.size()];
				 for (int k = 0; k <= taxAssignmentIfc.length - 1; k++) {
					taxAssignmentIfc[k] = (MAXTaxAssignmentIfc) taxAssignment.get(k);
				}
				((MAXPLUItemIfc)srli.getPLUItem()).setTaxAssignments(taxAssignmentIfc);	
				srli.setLineItemTaxBreakUpDetails(lineItemTaxBreakUpDetailList);
			}else{
				logger.info("Item is not associate with tax");
			}
			
		}
		
		public CurrencyIfc getTaxInclusiveSellingRetail(TaxLineItemInformationIfc item)
		{
			CurrencyIfc retValue = DomainGateway.getBaseCurrencyInstance();

			if (item.getExtendedDiscountedSellingPrice() != null) {
				retValue = item.getExtendedDiscountedSellingPrice();
			}
			return retValue;
		}
		//Change for Rev 1.3 : Ends

}
