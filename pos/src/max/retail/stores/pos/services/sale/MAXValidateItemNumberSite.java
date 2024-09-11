/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

// java imports
import java.math.BigDecimal;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 * This site displays the SELL_ITEM screen.
 * 
 * @version $Revision: 1.4 $
 */
// --------------------------------------------------------------------------
public class MAXValidateItemNumberSite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4665069056474025282L;

	// ----------------------------------------------------------------------
	/**
	 * Displays the LINEITEM_VOID screen.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		Letter letter = null;
		// Get the user input
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		PromptAndResponseModel parModel = ((POSBaseBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.LINEITEM_VOID))
				.getPromptAndResponseModel();
		String itemIDToBeDeleted = parModel.getResponseText();
		String barId = itemIDToBeDeleted;
		/** End HC Additions **/
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		LineItemsModel lineItemsModel = (LineItemsModel) ui.getModel();

		SaleReturnTransactionIfc transaction = cargo.getTransaction();
		AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();
		int rowTobeDeleted = 0;
		boolean isDiscounted = false;
		boolean itemfound = false;
		// PLUItemIfc isUPCItem = null;
		String itemNo = "";
		String itemWeight = "";
		int start_position = 0;
		int end_position = 0;
		int weight_start_position = 0;
		int weight_end_position = 0;
		BigDecimal itemWeightDecimal = null;
		BigDecimal decimalweight = null;
		// look for item to be deleted.
		boolean isWeighted = false;
		int barCodeLength = 0;

		for (int i = 0; i < lineItems.length; i++) {

			itemWeightDecimal = null;
			decimalweight = null;
			/**
			 * Begin HC Modification By:amol.patwardhan Spec: TND004 Desc: Check
			 * whether the entered item is a GV number. If we find out that GV
			 * is entered, we search for it in the listItems Delete it from line
			 * items and also update DB
			 */

			PLUItemIfc plu = ((SaleReturnLineItemIfc) lineItems[i])
					.getPLUItem();

			boolean isGVItem = false; // Flag for indicating if this is a GV

			StringBuffer strBuff = new StringBuffer();
			/**
			 * added by aarti if the item id length is less than 18 append with
			 * zero
			 **/
			if (itemIDToBeDeleted.length() < 18) {
				int len = itemIDToBeDeleted.length();
				while (len < 18) {
					strBuff.append("0");
					len++;
				}
			}
			itemIDToBeDeleted = strBuff.append(itemIDToBeDeleted).toString();
			strBuff = null;

			// vaibhav for bug 129
			StringBuffer strBuff1 = new StringBuffer();
			StringBuffer strBuff2 = new StringBuffer();
			String posItemId = plu.getItemID();
			String posID = plu.getPosItemID();
			if (plu.getPosItemID().length() < 18) {
				int len = posID.length();
				while (len < 18) {
					strBuff1.append("0");
					len++;
				}
			}
			if (plu.getItemID().length() < 18) {
				int len = posItemId.length();
				while (len < 18) {
					strBuff2.append("0");
					len++;
				}
			}
			posID = strBuff1.append(posID).toString();
			posItemId = strBuff2.append(posItemId).toString();
			strBuff1 = null;
			// end
			/** End HC Modification **/
			if (!isGVItem) {

				if (itemIDToBeDeleted.equalsIgnoreCase(posID)) {
					itemNo = posID;

				} else if(itemIDToBeDeleted.equalsIgnoreCase(posItemId)) {
					itemNo = posItemId;
				
				}else if (itemIDToBeDeleted.length() == barCodeLength
						&& barCodeLength != 0) {

					// Checking the barId length, it must be greater than
					// end_postion in case of weighted Barcode
					/**
					 * Start HC Additions aarti:27/10/09 Added to match the
					 * wieght in the barcode and delete the item
					 **/
					strBuff = new StringBuffer();
					if (barId.length() >= end_position) {
						itemNo = barId.substring(start_position, end_position);
						if (barId.length() == barCodeLength) {
							itemWeight = barId.substring(weight_start_position,
									weight_end_position);
						}
						if (itemNo != null && !"".equals(itemNo)
								&& itemNo.length() < 18) {
							int len = itemNo.length();
							while (len < 18) {
								strBuff.append("0");
								len++;
							}

						}
						itemNo = strBuff.append(itemNo).toString();
						strBuff = null;

					}

					// String itemId = plu.getPosItemID();
					// vaibhav for bug bug 129
					String itemId = posID;

					/**
					 * Start HC Additions aarti:27/10/09 Added to match the
					 * wieght in the barcode and delete the item
					 **/
					decimalweight = plu.getItemWeight();

					if (!"".equals(itemWeight)) {
						itemWeightDecimal = new BigDecimal(itemWeight)
								.movePointLeft(3);
					}
					if (itemNo.equals(itemId)
							&& itemWeightDecimal.equals(decimalweight)) {
						isDiscounted = true;
						itemfound = true;
						rowTobeDeleted = i;
						break;
					}

				}

				/** End HC Modification **/
				// String itemId = plu.getPosItemID();
				// vaibhav for bug 129
				//String itemId = posID;
				if (itemNo.equals(posID) || itemNo.equals(posItemId)) {
					if (itemWeightDecimal == null || decimalweight == null) {
						itemfound = true;
						isWeighted = false;
					} else if (itemWeightDecimal.equals(decimalweight)) {
						itemfound = true;
						isWeighted = true;

					} else {
						itemfound = false;
						isWeighted = false;
					}

					if (itemfound && !isWeighted) {
						SaleReturnLineItem lineItem = (SaleReturnLineItem) lineItems[i];

						if (lineItem.getItemQuantityDecimal().intValue() > 1) {
							transaction.replaceLineItem(lineItem,
									lineItem.getLineNumber());
							transaction.updateTransactionTotals();
							letter = new Letter("ReduceQty");
							break;
						}
						/** End HC Modification **/
						else {
							isDiscounted = true;
							if (itemIDToBeDeleted.equalsIgnoreCase(plu
									.getPosItemID())) {

								rowTobeDeleted = i;

							}
							break;
						}
					}
				}

				/**
				 * Begin HC Modification By:amol.patwardhan Spec: TND004 Desc:
				 * ended 'if'
				 **/
			}
			/** End HC Modification **/

		}
		if (isDiscounted) {
			lineItemsModel.setRowsToDelete(new int[] { rowTobeDeleted });
			letter = new Letter("Clear");
		}
		if (itemfound) {

			bus.mail(letter, BusIfc.CURRENT);
		} else {
			// show error screen

			if (isValidWeightedBarcode(barId, bus)&& letter == null) {
				weightedItemLookup(ui, cargo, barId.substring(1,7),bus);
			} else
				displayErrorDialog(bus);
		}

	}

	private boolean isValidWeightedBarcode(String itemIDToBeDeleted, BusIfc bus) {
		// TODO Auto-generated method stub
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		try {
			if (itemIDToBeDeleted.length() == pm.getIntegerValue(
					MAXSaleConstantsIfc.WEIGHT_BARCODE_LENGTH).intValue())
				return true;
			else
				return false;
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}

	// ----------------------------------------------------------------------
	/**
	 * Displays the Error dialog.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------

	public void displayErrorDialog(BusIfc bus) {

		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("VoidLineItemDialog");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Enter");

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		/**
		 * Begin HC Additons atul:05/14/09 Spec:SAL004 Scanner Tone play Scanner
		 * Tone
		 **/
		/** End HC Additions **/
	}

	public void weightedItemLookup(POSUIManagerIfc ui, SaleCargoIfc cargo,
			String itemIDToBeDeleted, BusIfc bus) {
		Letter letter = null;
		// Get the user input

		String barId = itemIDToBeDeleted;
		/** End HC Additions **/

		LineItemsModel lineItemsModel = (LineItemsModel) ui.getModel();

		SaleReturnTransactionIfc transaction = cargo.getTransaction();
		AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();
		int rowTobeDeleted = 0;
		boolean isDiscounted = false;
		boolean itemfound = false;
		// PLUItemIfc isUPCItem = null;
		String itemNo = "";
		String itemWeight = "";
		int start_position = 0;
		int end_position = 0;
		int weight_start_position = 0;
		int weight_end_position = 0;
		BigDecimal itemWeightDecimal = null;
		BigDecimal decimalweight = null;
		// look for item to be deleted.
		boolean isWeighted = false;
		int barCodeLength = 0;

		for (int i = 0; i < lineItems.length; i++) {

			itemWeightDecimal = null;
			decimalweight = null;
			/**
			 * Begin HC Modification By:amol.patwardhan Spec: TND004 Desc: Check
			 * whether the entered item is a GV number. If we find out that GV
			 * is entered, we search for it in the listItems Delete it from line
			 * items and also update DB
			 */

			PLUItemIfc plu = ((SaleReturnLineItemIfc) lineItems[i])
					.getPLUItem();

			boolean isGVItem = false; // Flag for indicating if this is a GV

			StringBuffer strBuff = new StringBuffer();
			/**
			 * added by aarti if the item id length is less than 18 append with
			 * zero
			 **/
			if (itemIDToBeDeleted.length() < 18) {
				int len = itemIDToBeDeleted.length();
				while (len < 18) {
					strBuff.append("0");
					len++;
				}
			}
			itemIDToBeDeleted = strBuff.append(itemIDToBeDeleted).toString();
			strBuff = null;

			// vaibhav for bug 129
			StringBuffer strBuff1 = new StringBuffer();
			String posID = plu.getPosItemID();
			if (plu.getPosItemID().length() < 18) {
				int len = posID.length();
				while (len < 18) {
					strBuff1.append("0");
					len++;
				}
			}
			posID = strBuff1.append(posID).toString();
			strBuff1 = null;
			// end
			/** End HC Modification **/
			if (!isGVItem) {

				if (itemIDToBeDeleted.equalsIgnoreCase(posID)) {
					itemNo = posID;

				} else if (itemIDToBeDeleted.length() == barCodeLength
						&& barCodeLength != 0) {

					// Checking the barId length, it must be greater than
					// end_postion in case of weighted Barcode
					/**
					 * Start HC Additions aarti:27/10/09 Added to match the
					 * wieght in the barcode and delete the item
					 **/
					strBuff = new StringBuffer();
					if (barId.length() >= end_position) {
						itemNo = barId.substring(start_position, end_position);
						if (barId.length() == barCodeLength) {
							itemWeight = barId.substring(weight_start_position,
									weight_end_position);
						}
						if (itemNo != null && !"".equals(itemNo)
								&& itemNo.length() < 18) {
							int len = itemNo.length();
							while (len < 18) {
								strBuff.append("0");
								len++;
							}

						}
						itemNo = strBuff.append(itemNo).toString();
						strBuff = null;

					}

					// String itemId = plu.getPosItemID();
					// vaibhav for bug bug 129
					String itemId = posID;

					/**
					 * Start HC Additions aarti:27/10/09 Added to match the
					 * wieght in the barcode and delete the item
					 **/
					decimalweight = plu.getItemWeight();

					if (!"".equals(itemWeight)) {
						itemWeightDecimal = new BigDecimal(itemWeight)
								.movePointLeft(3);
					}
					if (itemNo.equals(itemId)
							&& itemWeightDecimal.equals(decimalweight)) {
						isDiscounted = true;
						itemfound = true;
						rowTobeDeleted = i;
						break;
					}

				}

				/** End HC Modification **/
				// String itemId = plu.getPosItemID();
				// vaibhav for bug 129
				String itemId = posID;
				if (itemNo.equals(itemId)) {
					if (itemWeightDecimal == null || decimalweight == null) {
						itemfound = true;
						isWeighted = false;
					} else if (itemWeightDecimal.equals(decimalweight)) {
						itemfound = true;
						isWeighted = true;

					} else {
						itemfound = false;
						isWeighted = false;
					}

					if (itemfound && !isWeighted) {
						SaleReturnLineItem lineItem = (SaleReturnLineItem) lineItems[i];

						if (lineItem.getItemQuantityDecimal().intValue() > 1) {
							transaction.replaceLineItem(lineItem,
									lineItem.getLineNumber());
							transaction.updateTransactionTotals();
							letter = new Letter("WeightedReduceQty");
							break;
						}
						/** End HC Modification **/
						else {
							isDiscounted = true;
							if (itemIDToBeDeleted.equalsIgnoreCase(plu
									.getPosItemID())) {

								rowTobeDeleted = i;

							}
							break;
						}
					}
				}

				/**
				 * Begin HC Modification By:amol.patwardhan Spec: TND004 Desc:
				 * ended 'if'
				 **/
			}
			/** End HC Modification **/

		}
		if (isDiscounted) {
			lineItemsModel.setRowsToDelete(new int[] { rowTobeDeleted });
			letter = new Letter("WeightedClear");
		}
		if (itemfound) {

			bus.mail(letter, BusIfc.CURRENT);
		} else {
			displayErrorDialog(bus);
		}
	}

}