/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
*   Rev 1.1   26/04/2018  Atul Shukla                   Centralized Employee Discount 
*  Rev 1.0   12/08/2014  Shruti Singh   Initial Draft	Centralized Employee Discount 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.printing;

// Java imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.arts.MAXCentralEmployeeTransaction;
import max.retail.stores.domain.arts.MAXCentralUpdationEmployeeTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.employee.MAXEmployeeIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;

//------------------------------------------------------------------------------
/**
    Mail letter to frank tender documents

    @version $Revision: 7$
**/
//------------------------------------------------------------------------------
public class MAXFrankTendersSite extends PosSiteActionAdapter
{
    /**
         The name of this site.
    **/
    public static final String SITENAME = "FrankTendersSite";
    /**
       Franking tender list parameter
    **/
    protected static final String FRANKING_TENDER_LIST = "FrankingTenderList";
    protected static final String FRANKING_VOIDED_TENDER_LIST = "TendersToFrankOnPostVoid";
    /**
     * Franking tender list values
    **/
    protected static final String CHECK = "DepositedCheck";
    protected static final String TRAVCHECK =
        DomainGateway.getFactory().getTenderTypeMapInstance()
                                  .getDescriptor(TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK);
    protected static final String MALLCERT =
        DomainGateway.getFactory().getTenderTypeMapInstance()
                                  .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE);
    protected static final String STORECREDIT =
        DomainGateway.getFactory().getTenderTypeMapInstance()
                                  .getDescriptor(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT);
    protected static final String GIFTCERT =
        DomainGateway.getFactory().getTenderTypeMapInstance()
                                  .getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
    protected static final String STORECOUPON =
        DomainGateway.getFactory().getTenderTypeMapInstance()
                                  .getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
    protected static final String PURCHASEORDER =
        DomainGateway.getFactory().getTenderTypeMapInstance()
                                  .getDescriptor(TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER);
    protected static final String MAIL_BANK_CHECK =
        DomainGateway.getFactory().getTenderTypeMapInstance()
                                  .getDescriptor(TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK);
                                  
    protected static final String ECHECK = "ECheck";


    //--------------------------------------------------------------------------
    /**
        Print endorsements if needed.
        <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        MAXPrintingCargo cargo = (MAXPrintingCargo) bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        String letter = new String(CommonLetterIfc.DONE);
        // see if printer can frank
        boolean frankingCapable = isPrinterFrankingCapable(bus);

        try
        {
            String[] frankConfig = null;
            // get the appropriate franking list
            if (cargo.getTransType() == TransactionIfc.TYPE_VOID)
            {
                frankConfig = pm.getStringValues(FRANKING_VOIDED_TENDER_LIST);
            }
            else
            {
                frankConfig = pm.getStringValues(FRANKING_TENDER_LIST);
            }

            // If franking is configured, it's not a void and there are documents to endorse...
            if (frankingCapable
                && frankConfig != null
                && frankNone(frankConfig) == false
                && frankConfig.length != 0)
            {
                ArrayList frankingSettings = new ArrayList(Arrays.asList(frankConfig));
                // check if there are any checks or travel checks that need to be franked.
                boolean inclChecks = cargo.includesCheck();
                boolean inclEChecks = cargo.includesECheck();
                boolean inclTravChecks = cargo.includesTravelerChecks();
                boolean inclMallCerts = cargo.includesMallCertificate();
                boolean inclStoreCreditRedeem = cargo.includesStoreCreditRedeem();
                boolean inclGiftCertsRedeem = cargo.includesGiftCertificateRedeem();
                boolean inclGiftCerts = cargo.includesGiftCertificate();
                boolean inclStoreCredit = cargo.includesStoreCredit();
                boolean inclStoreCoupons = false;
                boolean inclMoneyOrders = false;
                boolean inclPreprintedStoreCredit = false;
                boolean inclPurchaseOrders = false;
                boolean frankGiftCertIssued = true;
                boolean inclMailBankChecks = false;
                
                frankGiftCertIssued = pm.getStringValue("FrankGiftCertificateIssued").equalsIgnoreCase("Y");
                
                TenderableTransactionIfc trans = cargo.getTenderableTransaction();
                
                // Coupons are valid during Sales, Exchanges and Layaway Initiate (new)
                if (trans instanceof LayawayTransactionIfc)
                {
                    if (((LayawayTransactionIfc) trans).getLayaway().getStatus() == LayawayConstantsIfc.STATUS_NEW)
                        inclStoreCoupons = cargo.includesStoreCoupon(trans);
                }
                else if (trans instanceof SaleReturnTransactionIfc)
                {
                    inclStoreCoupons = cargo.includesStoreCoupon(trans);
                }
                
                // POS 7.0 Check to see if preprinted store credit parameter is set to yes
                // if so we need to frank the preprinted store credit form.  
                String prePrintedSC = pm.getStringValue("PrePrintedStoreCredit");
                if (prePrintedSC.equals("Y") && inclStoreCredit)
                {
                    inclPreprintedStoreCredit = true;
                }

                //Add tenders to frank
                Vector tenderLineItems = cargo.getTransaction().getTenderLineItemsVector();
                // Make sure we have tender items
                if (tenderLineItems != null)
                {
                    Enumeration e = tenderLineItems.elements();
                    TenderLineItemIfc tli = null;
                    int typeCode;
                    while (e.hasMoreElements())
                    {
                        tli = (TenderLineItemIfc) e.nextElement();
                        typeCode = tli.getTypeCode();
                        if ((tli instanceof TenderTravelersCheckIfc ||
                            tli instanceof TenderCheckIfc ||
                            typeCode == TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE ||
                            (typeCode == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT 
                                && ((TenderStoreCreditIfc)tli).getState().equals(TenderStoreCreditIfc.REDEEM)) ||
                            (typeCode == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT 
                                && inclPreprintedStoreCredit) ||
                            (typeCode == TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE &&
                                    (tli.getAmountTender().signum() != CurrencyIfc.NEGATIVE ||
                                    (tli.getAmountTender().signum() == CurrencyIfc.NEGATIVE &&
                                            frankGiftCertIssued)))))
                        {
                            cargo.addTenderForFranking(tli);                            
                        }
                        // if this is a check and
                        // this is a check and checks are allowed
                        // or this is an echeck and echecks are allowed
                        else if((tli instanceof TenderCheckIfc) &&  
                                 ((((TenderCheckIfc)tli).getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_CHECK &&
                                    frankingSettings.contains(CHECK)) ||                                    
                                  ((TenderCheckIfc)tli).getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_E_CHECK &&
                                    frankingSettings.contains(ECHECK))
                                    || (tli instanceof TenderGiftCertificateIfc))
                        { 
                            cargo.addTenderForFranking(tli);
                        }
                        
                        /**
                         * Money order tenders are franked
                         */
                        if (tli instanceof TenderMoneyOrderIfc)
                        {
                            cargo.addTenderForFranking(tli);
                            String moneyOrder = TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemIfc.TENDER_TYPE_MONEY_ORDER];        
                             
                            if (frankingSettings.contains(moneyOrder))
                            {
                                inclMoneyOrders = true;
                            } 
                        }
                        
                        // Purchase Order Tenders are franked
                        if (tli instanceof TenderPurchaseOrderIfc)
                        {
                            cargo.addTenderForFranking(tli);
                            String purchaseOrder = TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER];
                            
                            if (frankingSettings.contains(purchaseOrder))
                            {
                                inclPurchaseOrders = true;
                            }
                        }
                        
                        // Mail Bank Checks are franked
                        if (tli instanceof TenderMailBankCheckIfc)
                           {
                            cargo.addTenderForFranking(tli);
                            String mailBankCheck = TenderLineItemIfc.TENDER_LINEDISPLAY_DESC[TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK];
                            
                            if (frankingSettings.contains(mailBankCheck))
                            {
                                inclMailBankChecks = true;
                            }
                        }
                    }
                }//done adding tenders to frank
                
                if (trans.getTransactionType() == TransactionIfc.TYPE_REDEEM &&
                        ( inclStoreCreditRedeem || inclGiftCertsRedeem) )
                {
                    cargo.addTenderForFranking(((RedeemTransactionIfc)trans).getRedeemTender());
                }
                
                if (trans.getTransactionType() == TransactionIfc.TYPE_VOID && inclStoreCreditRedeem)
                {
                    if (((VoidTransactionIfc)trans).getOriginalTransaction() instanceof RedeemTransactionIfc)
                    {
                        TenderableTransactionIfc origTrans = ((VoidTransactionIfc)trans).getOriginalTransaction();
                        cargo.addTenderForFranking(((RedeemTransactionIfc)origTrans).getRedeemTender());
                    }                    
                }
                
                // if not a void transaction get declined echecks
                boolean inclDeclEChecks = false;
                if(trans.getTransactionType() != TransactionIfc.TYPE_VOID)
                {
                    inclDeclEChecks = includeDeclinedEChecks(bus);
                }
                
                //Add gift certificates issued to frank if Parameter set to YES
                if (frankGiftCertIssued)
                {
                    if (cargo.getTransaction() instanceof RetailTransactionIfc)
                    {
                        Vector lineItems = ((RetailTransactionIfc)cargo.getTransaction()).getLineItemsVector();
                        // Make sure we have line items
                        if (lineItems != null)
                        {
                            Enumeration e = lineItems.elements();
                            SaleReturnLineItemIfc sli = null;
                            while (e.hasMoreElements())
                            {
                                sli = (SaleReturnLineItemIfc) e.nextElement();
                                if (sli.getPLUItem() instanceof GiftCertificateItemIfc)
                                {
                                    inclGiftCerts = true;
                                    cargo.addGiftCertificateForFranking(sli);
                                }
                            }
                        }
                    }
                }

                if ((inclChecks && frankingSettings.contains(CHECK))
                     || (inclEChecks && frankingSettings.contains(ECHECK))
                     || (inclDeclEChecks && frankingSettings.contains(ECHECK))
                     || (inclTravChecks && frankingSettings.contains(TRAVCHECK))
                     || (inclMallCerts  && frankingSettings.contains(MALLCERT))
                     || (inclStoreCreditRedeem  && frankingSettings.contains(STORECREDIT))
                     || (inclPreprintedStoreCredit  && frankingSettings.contains(STORECREDIT))
                     || (inclGiftCerts  && frankingSettings.contains(GIFTCERT))
                     || (inclGiftCertsRedeem  && frankingSettings.contains(GIFTCERT))
                     || (inclMoneyOrders)
                     || (inclStoreCoupons) && frankingSettings.contains("StoreCoupon")
                     || (inclPurchaseOrders)
                     || (inclMailBankChecks)                        
                     || (inclStoreCredit) &&(frankingSettings.contains(STORECREDIT)))                	 
                {
                    letter = "Print";
                }
                else
                {
                    Vector tenders = cargo.getTendersToFrank();
                    if (tenders != null)
                    {
                        tenders.removeAllElements();
                    }
                }
            }
        }
        catch (ParameterException pe)
        {
            logger.error("" + Util.throwableToString(pe) + "");
        }
        
        /** Changes for Rev 1.0 : Starts **/
        if(MAXEmployee.isUpdatedAmount)
        {
        	if(MAXEmployee.employeeIDreturn !=null && MAXEmployee.employeeIDreturn!="")
        	{
        		/** Changes for Rev 1.0 : Starts **/


        		if(cargo.getTransaction()!=null || cargo.getTenderableTransaction()!=null){	
        			String amountToUpdate,updateamount = null;
        			SaleReturnTransaction ls = null;
        			if(cargo.getTenderableTransaction()!=null && cargo.getTenderableTransaction() instanceof MAXSaleReturnTransaction){
        				if(cargo.getTransaction() instanceof SaleReturnTransaction){
        					ls = (SaleReturnTransaction) cargo.getTransaction();
        				}
        			}
        			/*else if(cargo.getCurrentTransactionADO()!=null || cargo.getTenderableTransaction()==null)
											{
												if(cargo.getCurrentTransactionADO() instanceof PaymentTransactionADO)
												{
													PaymentTransactionADO lsado = (PaymentTransactionADO)cargo.getCurrentTransactionADO();
													LayawayPaymentTransaction layway =(LayawayPaymentTransaction) lsado.toLegacy();
													lw = layway.getLayaway();
													laywaystatus = lw.getStatus();
												}

											}*/

        			CurrencyIfc extendedDiscountSellingAmount=DomainGateway.getBaseCurrencyInstance();
        			String empId = null;
        			String companyNam=null;
        			 /** Changes for Rev 1.1 : Starts **/
        			if(ls instanceof MAXSaleReturnTransaction)
        			{
        				MAXSaleReturnTransaction maxLs=(MAXSaleReturnTransaction)ls;
						if(maxLs.getEmployeeCompanyName() != null)
        				companyNam=maxLs.getEmployeeCompanyName().trim().toString();        				
        			}
        			 /** Changes for Rev 1.1 : End **/
        			if(ls!=null){
        				Vector v = ls.getItemContainerProxy().getLineItemsVector();
        				for (Iterator itemsIter = v.iterator(); itemsIter.hasNext();) 
        				{
        					String customer = "Default";
        					String rule ="";
        					MAXSaleReturnLineItemIfc lineItem = (MAXSaleReturnLineItemIfc) itemsIter.next();
        					PLUItemIfc pluitem =  null;
        					PriceChangeIfc[] priceChange = lineItem.getPLUItem().getPermanentPriceChanges();
        					if(priceChange!=null){
        						for(int a=0;a<priceChange.length;a++){
        							pluitem = priceChange[a].getItem();
        						}
        					}
        					if(pluitem!=null){
        						AdvancedPricingRuleIfc[] lapr = pluitem.getAdvancedPricingRules();
        						for(int s=0; s<lapr.length;s++)
        						{
        							if(lapr[s].getRuleID()!=null
        									&& lapr[s].getDiscountAmount().getDoubleValue() >
        							DomainGateway.getBaseCurrencyInstance().getDoubleValue())
        								rule = lapr[s].getRuleID();
        						}
        					}


        					boolean isPriceOverride = false;
        					if(lineItem.getAdvancedPricingDiscount()==null)
        					{
        						ItemDiscountStrategyIfc[] k=lineItem.getItemPrice().getItemDiscounts();
        						for(int disc=0; disc<k.length; disc++){
        							if(k[disc].getDiscountEmployee()!=null && 
        									k[disc].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE){
        								empId=k[disc].getDiscountEmployeeID();

        							}

        							isPriceOverride = lineItem.getItemPrice().isPriceOverride();

        							if((!isPriceOverride && (!customer.equals("N") && customer!="N")&& (rule.equals("")&& rule=="") 
        									|| (customer.equals("N") && lineItem.getBestDealWinnerName()==null)
        									|| (customer.equals("T") && lineItem.getBestDealWinnerName()==null))&& empId!=null){
        								CurrencyIfc price=lineItem.getExtendedDiscountedSellingPrice();
        								extendedDiscountSellingAmount=price.add(extendedDiscountSellingAmount);
        								ls.setEmployeeDiscountID(empId);
        								empId = null;
        							}
        						}

        					}
        					else{
        						ItemDiscountStrategyIfc[] l=lineItem.getItemPrice().getItemDiscounts();
        						for(int dis=0;dis<l.length;dis++){
        							if(l[dis].getDiscountEmployee()!=null){
        								empId=l[dis].getDiscountEmployeeID();
        							}


        							if(ls.getItemContainerProxy()!=null && ls.getItemContainerProxy().getTransactionDiscounts()!=null){
        								TransactionDiscountStrategyIfc[] ip=ls.getItemContainerProxy().getTransactionDiscounts();
        								for(int y=0;y<ip.length;y++)
        								{
        									if(ip[y] instanceof TransactionDiscountByPercentageIfc)
        									{
        										if(ip[y].getDiscountEmployee()!=null)
        										{
        											empId=ip[y].getDiscountEmployee().getEmployeeID();
        										}
        									}
        								}
        							}

        							isPriceOverride = lineItem.getItemPrice().isPriceOverride();

        							if((/*MAXEmployee.specialDiscountFlag &&*/ !isPriceOverride && (!customer.equals("N") && customer!="N")&&
        									(!customer.equals("T") && customer!="T")&& (!customer.equals("Default") && customer!="Default") 
        									|| (customer.equals("E") && lineItem.getBestDealWinnerName()==null))&& empId!=null){
        								CurrencyIfc price=lineItem.getExtendedDiscountedSellingPrice();
        								extendedDiscountSellingAmount=price.add(extendedDiscountSellingAmount);
        								ls.setEmployeeDiscountID(empId);
        								empId = null;
        							}
        						}
        					}
        				}
        				if(ls.getEmployeeDiscountID()!=null)
        				{
        					empId=ls.getEmployeeDiscountID();
        				}
        			}

        			// changes for restricting elligible amount to be lesser than total of available amount and return amount
        			if(empId!=null && empId!="" && !(empId.equals("")) && (companyNam != null) && (companyNam != "") && (!companyNam.equals(""))){
        				MAXCentralEmployeeTransaction centralEmployeeTransaction = null;
        				centralEmployeeTransaction = (MAXCentralEmployeeTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.EMPLOYEE_CENTRAL_TRANSACTION);
        				try {
        					//MAXEmployeeIfc employee = centralEmployeeTransaction.getEmployeeNumber(empId);
        					 /** Changes for Rev 1.1 : Starts **/
        					MAXEmployeeIfc employee = centralEmployeeTransaction.getEmployeeNumber(empId,companyNam);
        					 /** Changes for Rev 1.1 : End **/
        					MAXEmployee.availAmount=Integer.parseInt(employee.getAvailableAmount());
        					MAXEmployee.elligibleAmount=Integer.parseInt(employee.getEligibleAmount());

        				} catch (DataException e) {
        					logger.error("" + e.getErrorCode() + "");
        				}
        			}

        			extendedDiscountSellingAmount = extendedDiscountSellingAmount.abs();
        			long val = Math.round(extendedDiscountSellingAmount.getDoubleValue());
        			amountToUpdate = "-"+val+"";

        			if((MAXEmployee.elligibleAmount-(MAXEmployee.availAmount-Integer.parseInt(amountToUpdate)))<-1)
        				//						MAXEmployee.isUpdatedAmount = true;
        				amountToUpdate="-"+Integer.toString(MAXEmployee.elligibleAmount-MAXEmployee.availAmount);


        			MAXCentralUpdationEmployeeTransaction centralUpdationEmployeeTransaction = null;
        			centralUpdationEmployeeTransaction = (MAXCentralUpdationEmployeeTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.EMPLOYEE_CENTRAL_UPDATION_TRANSACTION);
        			try {

        				//								if((Integer.parseInt(MAXEmployee.amountToUpdated)+MAXEmployee.availAmount)<=MAXEmployee.elligibleAmount){
        				//centralUpdationEmployeeTransaction.updateEmloyeeAmount(MAXEmployee.employeeIDreturn, amountToUpdate);
        				centralUpdationEmployeeTransaction.updateEmloyeeAmount(MAXEmployee.employeeIDreturn, amountToUpdate,companyNam);
        				MAXEmployee.employeeIDreturn="";
        				MAXEmployee.amountToUpdated="";
        				MAXEmployee.isUpdatedAmount=false;
        				MAXEmployee.maxEmployee=null;
        				//								}
        			} catch (DataException e1) {
        				logger.error("" + e1.getErrorCode() + "");
        			}
        		}

        	}	
        }
		/** Changes for Rev 1.0 : Ends **/

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    //---------------------------------------------------------------------
    /**
        Determines if printer is franking-capable. <P>
        @param bus instance of bus
        @return true if printer is franking-capable, false otherwise.
    **/
    //---------------------------------------------------------------------
    protected boolean isPrinterFrankingCapable(BusIfc bus)
    {                                   // begin isPrinterFrankingCapable()
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        boolean frankingCapable = true;

        try
        {
            frankingCapable = ((Boolean) pda.isFrankingCapable()).booleanValue();
        }
        catch (DeviceException e)
        {
            frankingCapable = false;
        }

        return(frankingCapable);
    }                                   // end isPrinterFrankingCapable()
    
    //---------------------------------------------------------------------
    /**
        Determines if list of tenders to frank includes "none".<P>
        @param frankParams array of tender types (as strings)
        @return true if list includes "none", false otherwise
    **/
    //---------------------------------------------------------------------
    protected boolean frankNone(String[] frankParams)
    {
        boolean retValue = false;
    
        for(int i=0; i<frankParams.length; i++)
        {
            if(frankParams[i].equalsIgnoreCase("none"))
            {
                retValue = true;
                break;
            }
        }
        
        return retValue;        
    }
    
    //----------------------------------------------------------------------
    /**
        Adds declined echecks.
        @param bus
        @return
    **/
    //----------------------------------------------------------------------
    protected boolean includeDeclinedEChecks(BusIfc bus)
    {
        boolean returnBool = false;
        Vector declinedEChecks = null;
        MAXPrintingCargo cargo = (MAXPrintingCargo)bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTenderableTransaction();
        
        if(trans instanceof SaleReturnTransactionIfc)
        {
            declinedEChecks = ((SaleReturnTransactionIfc)trans).getECheckDeclinedItems();
        }
        if(declinedEChecks != null)
        {
            Enumeration e = declinedEChecks.elements();
            TenderLineItemIfc tli = null;

            // See if there are any checks
            while (e.hasMoreElements())
            {
                tli = (TenderLineItemIfc) e.nextElement();
                if (tli instanceof TenderCheckIfc)
                {
                    cargo.addTenderForFranking(tli);
                    returnBool = true;
                }
            } // END: while
        }        
        return returnBool;        
    }
}
