/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel		Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.arts.MAXCentralUpdationEmployeeTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.customer.Customer;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.JournalableADOIfc;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.transaction.LayawayTransactionADO;
import oracle.retail.stores.pos.ado.transaction.PaymentTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.PaidUpTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXPaidUpActionSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = -6926257982122921178L;

	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();

		if (cargo.getTenderAttributes() != null) {
			if (cargo.getCurrentTransactionADO() != null || cargo.getTenderableTransaction() != null) {
				if (cargo.getTenderAttributes() != null) {
					String amount, updateamount = null;
					SaleReturnTransaction ls = null;
					LayawayIfc lw = null;
					int laywaystatus = 0;
					int laywaytransaction = 0;
					if (cargo.getTenderableTransaction() != null
							&& !(cargo.getTenderableTransaction() instanceof MAXLayawayTransaction)) {
						if (cargo.getTransaction() instanceof SaleReturnTransaction) {
							ls = (SaleReturnTransaction) cargo.getTransaction();
						}
					} else if (cargo.getCurrentTransactionADO() != null || cargo.getTenderableTransaction() == null) {
						if (cargo.getCurrentTransactionADO() instanceof PaymentTransactionADO) {
							PaymentTransactionADO lsado = (PaymentTransactionADO) cargo.getCurrentTransactionADO();
							LayawayPaymentTransaction layway = (LayawayPaymentTransaction) lsado.toLegacy();
							lw = layway.getLayaway();
							laywaystatus = lw.getStatus();
						} else if (cargo.getCurrentTransactionADO() instanceof LayawayTransactionADO) {
							LayawayTransactionADO lsado2 = (LayawayTransactionADO) cargo.getCurrentTransactionADO();
							LayawayTransaction layway = (LayawayTransaction) lsado2.toLegacy();
							lw = layway.getLayaway();
							laywaystatus = lw.getStatus();
							laywaytransaction = layway.getTransactionType();
							ls = (SaleReturnTransaction) layway;
						}
					}

					CurrencyIfc extendedDiscountSellingAmount = DomainGateway.getBaseCurrencyInstance();
					String empId = null;
					if (ls != null) {
						Vector v = ls.getItemContainerProxy().getLineItemsVector();
						for (Iterator itemsIter = v.iterator(); itemsIter.hasNext();) {
							String customer = "Default";
							String rule = "";
							MAXSaleReturnLineItemIfc lineItem = (MAXSaleReturnLineItemIfc) itemsIter.next();
							PLUItemIfc pluitem = null;
							PriceChangeIfc[] priceChange = lineItem.getPLUItem().getPermanentPriceChanges();
							if (priceChange != null) {
								for (int a = 0; a < priceChange.length; a++) {
									pluitem = priceChange[a].getItem();
								}
							}
							if (pluitem != null) {
								AdvancedPricingRuleIfc[] lapr = pluitem.getAdvancedPricingRules();
								for (int s = 0; s < lapr.length; s++) {
									if (lapr[s].getRuleID() != null
											&& lapr[s].getDiscountAmount().getDoubleValue() > DomainGateway
													.getBaseCurrencyInstance().getDoubleValue())
										rule = lapr[s].getRuleID();

								}
							}

							boolean isPriceOverride = false;
							if (lineItem.getAdvancedPricingDiscount() == null) {
								ItemDiscountStrategyIfc[] k = lineItem.getItemPrice().getItemDiscounts();
								for (int disc = 0; disc < k.length; disc++) {
									if (k[disc].getDiscountEmployee() != null
											&& k[disc].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
										empId = k[disc].getDiscountEmployeeID();

									}

									isPriceOverride = lineItem.getItemPrice().isPriceOverride();

									if ((!isPriceOverride && (!customer.equals("N") && customer != "N")
											&& (rule.equals("") && rule == "")
											|| (customer.equals("N") && lineItem.getBestDealWinnerName() == null) || (customer
											.equals("T") && lineItem.getBestDealWinnerName() == null)) && empId != null) {
										CurrencyIfc price = lineItem.getExtendedDiscountedSellingPrice();
										extendedDiscountSellingAmount = price.add(extendedDiscountSellingAmount);
										ls.setEmployeeDiscountID(empId);
										empId = null;
									}
								}

							} else {
								ItemDiscountStrategyIfc[] l = lineItem.getItemPrice().getItemDiscounts();
								for (int dis = 0; dis < l.length; dis++) {
									if (l[dis].getDiscountEmployee() != null) {
										empId = l[dis].getDiscountEmployeeID();
									}

									if (ls.getItemContainerProxy() != null
											&& ls.getItemContainerProxy().getTransactionDiscounts() != null) {
										TransactionDiscountStrategyIfc[] ip = ls.getItemContainerProxy()
												.getTransactionDiscounts();
										for (int y = 0; y < ip.length; y++) {
											if (ip[y] instanceof TransactionDiscountByPercentageIfc) {
												if (ip[y].getDiscountEmployee() != null) {
													empId = ip[y].getDiscountEmployee().getEmployeeID();
												}
											}
										}
									}

									isPriceOverride = lineItem.getItemPrice().isPriceOverride();

									if ((/* MAXEmployee.specialDiscountFlag && */!isPriceOverride
											&& (!customer.equals("N") && customer != "N")
											&& (!customer.equals("T") && customer != "T")
											&& (!customer.equals("Default") && customer != "Default") || (customer
											.equals("E") && lineItem.getBestDealWinnerName() == null)) && empId != null) {
										CurrencyIfc price = lineItem.getExtendedDiscountedSellingPrice();
										extendedDiscountSellingAmount = price.add(extendedDiscountSellingAmount);
										ls.setEmployeeDiscountID(empId);
										empId = null;
									}
								}
							}

						}
						if (ls.getEmployeeDiscountID() != null) {
							empId = ls.getEmployeeDiscountID();
						}
					}

					// MAXEmployee.specialDiscountFlag=false;
					amount = extendedDiscountSellingAmount.getStringValue();
					// amount="85.00";
					if (amount.indexOf(".") != -1)
						updateamount = amount.substring(0, amount.length() - 3);
					else
						updateamount = amount;
   // below changes made by atul shukla
					String companyName=null;
				//	SaleReturnTransaction trans=(SaleReturnTransaction) cargo.getTransaction();
					
					//if(trans instanceof MAXSaleReturnTransaction)
					if(cargo.getTransaction() instanceof MAXSaleReturnTransaction)
					
					{
						try
						{
						//MAXSaleReturnTransaction maxSls=(MAXSaleReturnTransaction)trans;
							MAXSaleReturnTransaction maxSls=(MAXSaleReturnTransaction)cargo.getTransaction();
						if(maxSls.getEmployeeCompanyName() != null)
						{
						companyName=maxSls.getEmployeeCompanyName().trim().toString();
						}
						}catch(NullPointerException ne)
						{
							ne.printStackTrace();
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
					if (empId != null && empId != "" && companyName != null) {
						MAXCentralUpdationEmployeeTransaction centralUpdationEmployeeTransaction = null;
						centralUpdationEmployeeTransaction = (MAXCentralUpdationEmployeeTransaction) DataTransactionFactory
								.create(MAXDataTransactionKeys.EMPLOYEE_CENTRAL_UPDATION_TRANSACTION);
						if (TransactionIfc.TYPE_LAYAWAY_DELETE == laywaytransaction
								&& (laywaystatus == 1 || laywaystatus == 2)) {
							updateamount = "-" + updateamount;
							try {
								centralUpdationEmployeeTransaction.updateEmloyeeAmount(empId, updateamount,companyName);
							} catch (DataException e1) {
								logger.error("" + e1.getErrorCode() + "");
							}
						} else if (laywaystatus == 0) {
							try {
								centralUpdationEmployeeTransaction.updateEmloyeeAmount(empId, updateamount,companyName);
								MAXEmployee.isUpdatedAmount = false;
							} catch (DataException e1) {
								logger.error("" + e1.getErrorCode() + "");
							}
						}
						if (MAXEmployee.maxEmployee != null)
							MAXEmployee.maxEmployee = null;
					}
				}
			}
		}

		// Changes for Rev 1.0 : Starts
		// set up the TDO attributes
		HashMap<String, Object> attributes = new HashMap<String, Object>(2);
		attributes.put(PaidUpTDO.BUS, bus);
		attributes.put(PaidUpTDO.TRANSACTION, cargo.getCurrentTransactionADO());

		// Changes for Rev 1.0 : Ends
		
		// below is for getting traceID for Ewallet
		try
		{
		CustomerIfc customer=cargo.getTransaction().getCustomer();
		
		if(customer instanceof MAXCustomer)
		{
			MAXCustomer mCustomer=(MAXCustomer)customer;
			if(mCustomer.getLMREWalletTraceId()!=null) {
			String walletTraceId=mCustomer.getLMREWalletTraceId().toString();
			System.out.println("traceId   "+ walletTraceId);
			}
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		// changes end here
		// build bean model helper
		TDOUIIfc tdo = null;
		try {
			tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.PaidUp");
		} catch (TDOException tdoe) {
			tdoe.printStackTrace();
		}

		// Changes for Rev 1.0 : Starts
		RetailTransactionADOIfc trans = cargo.getCurrentTransactionADO();

		// Calculate the cash change/refund adjustment amount (if any).
		trans.adjustCashAmountReturnedToCustomer();
		// Changes for Rev 1.0 : Ends
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		//if (trans.openDrawer()) {
			if (false) {
			boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
			// we know we have negative cash and we will never get here in
			// return
			if (trans.getTotalCashChangeAmount().compareTo(DomainGateway.getBaseCurrencyInstance()) == CurrencyIfc.GREATER_THAN) {
				ui.showScreen(POSUIManagerIfc.ISSUE_CHANGE, tdo.buildBeanModel(attributes));
			} else if (isReentryMode == false) {
				ui.showScreen(POSUIManagerIfc.CLOSE_DRAWER, tdo.buildBeanModel(attributes));
			}
		}

		displayLineDisplayText(bus, tdo);

		// Journal the tender totals (takes the transaction)
		JournalFactoryIfc jrnlFact = null;
		try {
			jrnlFact = JournalFactory.getInstance();
		} catch (ADOException e) {
			logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
			throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
		}
		RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
		registerJournal.journal((JournalableADOIfc) cargo.getCurrentTransactionADO(), JournalFamilyEnum.TRANSACTION,
				JournalActionEnum.TENDER_TOTAL);

		TenderableTransactionIfc transaction = cargo.getTransaction();
		if (transaction instanceof SaleReturnTransaction) {
			MAXSaleReturnTransactionIfc saleReturnTransaction = (MAXSaleReturnTransactionIfc) transaction;

			Vector capillaryCouponVector = saleReturnTransaction.getCapillaryCouponsApplied();
			if (!capillaryCouponVector.isEmpty()) {
				writeJournalEntryForCapillaryCoupon(capillaryCouponVector);
				ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
				journalTotals(cargo, transaction, pm);
			}

		}

		if (cargo.getCurrentTransactionADO().getIRSCustomer() != null) {
			registerJournal.journal((JournalableADOIfc) cargo.getCurrentTransactionADO(),
					JournalFamilyEnum.TRANSACTION, JournalActionEnum.IRS_CUSTOMER);
		}

		/*if (cargo.getCurrentTransactionADO().openDrawer()) {
			bus.mail(new Letter("Open"), BusIfc.CURRENT);
		} else {*/
			ui.statusChanged(2, true, false);
			bus.mail(new Letter("ExitTender"), BusIfc.CURRENT);
		//}
	}

	/**
	 * Display information on the Line display
	 * 
	 * @param bus
	 */
	protected void displayLineDisplayText(BusIfc bus, TDOUIIfc tdo) {
		try {
			POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

			pda.clearText();

			pda.displayTextAt(1, 0,
					tdo.formatPoleDisplayLine2(((AbstractFinancialCargo) bus.getCargo()).getCurrentTransactionADO()));
		} catch (DeviceException e) {
			logger.warn("Unable to use Line Display: " + e.getMessage());
		}

	}

	private void journalTotals(TenderCargo cargo, TenderableTransactionIfc transaction, ParameterManagerIfc pm) {
		JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
		JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher().getManager(
				JournalFormatterManagerIfc.TYPE);
		if (journal != null && formatter != null) {
			journal.journal(transaction.getCashier().getLoginID(), transaction.getTransactionID(),
					formatter.journalTotals((SaleReturnTransactionIfc) transaction, pm));
		}

	}

	/* This method is used for EJ printing in capillaryCouponRedeemTransaction */
	private void writeJournalEntryForCapillaryCoupon(Vector capillaryCouponVector) {
		String couponNumber = "";
		String CouponAmount = "";
		JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
		JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher().getManager(
				JournalFormatterManagerIfc.TYPE);
		StringBuffer stringBuffer = new StringBuffer();
		Object coupons[] = capillaryCouponVector.toArray();
		for (int i = 0; i < coupons.length; i++) {
			if (((MAXDiscountCouponIfc) coupons[i]).getDiscountType().equalsIgnoreCase(("ABS")))
				CouponAmount = ("Coupon Redeem RS:" + ((MAXDiscountCouponIfc) coupons[i])
						.getCouponDiscountAmountPercent());
			else
				CouponAmount = ("Coupon Redeem%:" + ((MAXDiscountCouponIfc) coupons[i])
						.getCouponDiscountAmountPercent());

			couponNumber = ("Coupon Number:" + ((MAXDiscountCouponIfc) coupons[i]).getCouponNumber());

			if (journal != null) {

				if (couponNumber != null) {
					stringBuffer.append(Util.EOL).append(CouponAmount);
					stringBuffer.append(Util.EOL).append(couponNumber);
				}

			} else {
				logger.error("No JournalManager found");
			}
		}
		journal.journal(stringBuffer.toString());
	}

}
