/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.1		Feb 17, 2017		Nadia Arora		Changes for Advanced Search - item not getting added and for exception
 *	Rev 1.0		Dec 27, 2016		Mansi Goel		Changes for Advanced Search
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale.advsearch;

//foundation imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.common.MAXRoundingConstantsIfc;
import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManager;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;

import org.apache.log4j.Logger;

public class MAXAdvSearchReturnShuttle implements ShuttleIfc {
	static final long serialVersionUID = -5555241803980173156L;
	protected MAXItemInquiryCargo itemInquiryCargo = null;

	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.sale.advsearch.MAXAdvSearchReturnShuttle.class);

	/**
	 * The modified line item.
	 */
	protected SaleReturnLineItemIfc lineItem = null;

	/**
	 * The modified line items.
	 */
	protected SaleReturnLineItemIfc[] lineItemList = null;

	/**
	 * The flag that indicates whether an item is being added
	 */
	protected boolean addPLUItem = false;

	/**
	 * The item to add.
	 */
	protected PLUItemIfc pluItem = null;

	/**
	 * Item Quantity
	 */
	protected BigDecimal itemQuantity = null;

	/**
	 * Flag indicating whether item added is service and added thru
	 * inquiry/services
	 */
	protected boolean serviceItemFlag = false;

	/**
	 * transaction type - sale or return
	 */
	protected SaleReturnTransactionIfc transaction;

	// end
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) {
		itemInquiryCargo = (MAXItemInquiryCargo) bus.getCargo();

	}

	public void unload(BusIfc bus) {
		pluItem = itemInquiryCargo.getPLUItem();
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
		if (pluItem != null) {
			cargo.setPLUItem(itemInquiryCargo.getPLUItem());
			cargo.setItemQuantity(itemInquiryCargo.getItemQuantity());
			// cargo.setModifiedFlag(itemInquiryCargo.getModifiedFlag());
			cargo.setItemSerial(itemInquiryCargo.getItemSerial());
			// cargo.setTransaction(itemInquiryCargo.getTransaction());
			cargo.setAddSearchPLUItem(itemInquiryCargo.isAddSearchPLUItem());
		} else {
			cargo.setAddSearchPLUItem(false);
			if (cargo.getTransaction() != null
					&& ((SaleReturnTransactionIfc) cargo.getTransaction()).getAgeRestrictedDOB() != null) {
				cargo.setTransaction(cargo.getTransaction());
			}
		}
		/* Changes for Rev 1.1 starts*/
		if(cargo.getTransaction() != null && cargo.getPLUItem() != null && pluItem != null)
		{
			SaleReturnLineItemIfc srli = cargo.getTransaction().addPLUItem(cargo.getPLUItem(), cargo.getItemQuantity());
			cargo.setLineItem(srli);
		}
		/*Changes for Rev 1.1 ends */
		// /Animesh : Added from ModifyItemReturnShuttle

		// retrieve cargo from the parent
		// SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();//commented by
		// Animesh
		cargo.setRefreshNeeded(true);

		// India Localization changes start
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		try {
			cargo.setRounding(pm.getStringValue(MAXRoundingConstantsIfc.ROUNDING));
			String[] roundingDenominationsArray = pm.getStringValues(MAXRoundingConstantsIfc.ROUNDING_DENOMINATIONS);
			if (roundingDenominationsArray == null || roundingDenominationsArray.length == 0) {
				throw new ParameterException("List of parameters undefined");
			}
			List<BigDecimal> roundingDenominations = new ArrayList<BigDecimal>();
			roundingDenominations.add(0, new BigDecimal(0.0));
			for (int i = 0; i < roundingDenominationsArray.length; i++) {
				roundingDenominations.add(new BigDecimal(roundingDenominationsArray[i]));
			}
			roundingDenominations.add(roundingDenominationsArray.length, new BigDecimal(1.00));

			// List must be sorted before setting on the cargo.
			Collections.sort(roundingDenominations, new Comparator() {
				public int compare(Object o1, Object o2) {
					BigDecimal denomination1 = (BigDecimal) o1;
					BigDecimal denomination2 = (BigDecimal) o2;
					return denomination1.compareTo(denomination2);
				}
			});

			cargo.setRoundingDenominations(roundingDenominations);
		} catch (ParameterException pe) {
			// if there is an error with the parameters, the price rounding
			// logic should be disabled
			// cargo.setRoundingEnabledLogic(false);
			logger.error("" + Util.throwableToString(pe) + "");
		}
		// India Localization changes end
		addPLUItem = true;
		// if (addPLUItem)
		if (cargo.isAddSearchPLUItem()) {
			if (cargo.getTransaction() == null) {
				if (transaction == null) {
					cargo.initializeTransaction(bus);
					// Changes for Rev 1.0 : Starts
					if (cargo.getTransaction() != null) {
						((MAXSaleReturnTransaction) cargo.getTransaction()).setRounding(cargo.getRounding());
						((MAXSaleReturnTransaction) cargo.getTransaction()).setRoundingDenominations(cargo
								.getRoundingDenominations());
					}
					/*
					 * if(cargo.getTransaction().getTransactionTotals() != null)
					 * { ((MAXTransactionTotals)
					 * cargo.getTransaction().getTransactionTotals
					 * ()).setRounding(cargo.getRounding());
					 * ((MAXTransactionTotals)
					 * cargo.getTransaction().getTransactionTotals
					 * ()).setRoundingDenominations
					 * (cargo.getRoundingDenominations()); }
					 */
					// Changes for Rev 1.0 : Ends
				} else {
					cargo.setTransaction(transaction);
				}
			}
			cargo.setPLUItem(pluItem);
			SaleReturnLineItemIfc item = cargo.getLineItem();
			if(item == null)
				item = cargo.getTransaction()
					.addPLUItem(pluItem, cargo.getItemQuantity()/* itemQuantity */);
			
			
			String productGroup = pluItem.getProductGroupID();
			if (productGroup != null && productGroup.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_ALTERATION)) {
				// Set the Alteration Item Flag
				item.setAlterationItemFlag(true);
			}

			// set the line item for the serialized item service
			cargo.setLineItem(item);

			if (serviceItemFlag) // journal the service item added to the
									// transaction
			{
				JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(
						JournalManagerIfc.TYPE);
				JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher().getManager(
						JournalFormatterManager.TYPE);
				if (journal != null) {
					StringBuffer sb = new StringBuffer();
					sb.append(formatter.toJournalString(item, null, null));

					if (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
					// add status
					{
						sb.append(Util.EOL).append("  Status: New");
					}

					journal.journal(cargo.getOperator().getLoginID(), cargo.getTransaction().getTransactionID(),
							sb.toString());
				} else {
					logger.error("No JournalManager found");
				}
			}

			// Show item on Line Display device
			POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
			try {
				pda.lineDisplayItem(item);
			} catch (DeviceException e) {
				logger.warn("Unable to use Line Display: " + e.getMessage() + "");
			}

		} // end if (addPLUItem)
		else if (cargo.getIndex() >= 0 || lineItemList != null) {
			if (lineItemList != null) {
				for (int i = 0; i < lineItemList.length; i++) {
					cargo.getTransaction().replaceLineItem(lineItemList[i], lineItemList[i].getLineNumber());
					cargo.setItemModifiedIndex(lineItemList[i].getLineNumber());
				}
			}
		}

		if (transaction != null && cargo.getTransaction() == null) {
			cargo.setTransaction(transaction);
		}
	}

	public static void main(String args[]) { // begin main()
		// instantiate class
		MAXAdvSearchReturnShuttle obj = new MAXAdvSearchReturnShuttle();

		// output toString()
		System.out.println(obj.toString());
	} // end main()
}
