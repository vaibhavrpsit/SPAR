/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev 1.0		Dec 27, 2016		Mansi Goel		Changes for Advanced Search
 *
 ********************************************************************************/

package max.retail.stores.pos.services.inquiry.iteminquiry;

// java imports
import java.math.BigDecimal;
import java.util.Locale;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.pos.ui.beans.MAXItemInfoBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;

//--------------------------------------------------------------------------
/**
 * This site displays the ITEM_INFO screen.
 * 
 * @version $Revision: /rgbustores_12.0.9in_branch/2 $
 **/
// --------------------------------------------------------------------------
public class MAXShowItemSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = -6954925496493840462L;
	
	protected static final String ITEM_DESCRIPTION = "Item Description: ";
	protected static final String ITEM_DEPARTMENT = "Item Department: ";
	protected static final String ITEM_PRICE = "Item Price: ";
	protected static final String ITEM_UNIT = "Item Unit of Measure: ";
	protected static final String ITEM_TAXABLE = "Item Taxable: ";
	protected static final String ITEM_DISCOUNTABLE = "Item Discountable: ";
	protected static final String PRICE_INQUIRY = "Price Inquiry ";
	protected static final String ITEM_NUMBER = "Item Number: ";
	protected static final String ITEM_SIZE = "Item Size: ";
	protected static final String YES = "YES";
	protected static final String NO = "NO";
	protected static final String TRUE = "true";
	protected static final String FALSE = "false";
	// to display manufacturer and planogram
	protected static final String ITEM_MANUFACTURER = "Manufacturer:";
	protected static final String ITEM_PLANOGRAM_ID = "Planogram ID:";

	public static final String SEARCH_ITEM_BY_MANUFACTURER = "SearchForItemByManufacturer";
	public static final String PLANOGRAM_DISPLAY = "PlanogramDisplay";
	public static final String SIZE_INPUT_FIELD = "SizeInputField";

	// ----------------------------------------------------------------------
	/**
	 * Displays the ITEM_INFO screen.
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// retrieve item information from cargo
		ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
		PLUItemIfc item = cargo.getPLUItem();

		// Initialize bean model values
		MAXItemInfoBeanModel model = new MAXItemInfoBeanModel();

		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		model.setItemDescription(item.getDescription(locale));
		model.setItemNumber(item.getItemID());
		if (item.getDepartment() != null) {
			model.setItemDept(item.getDepartment().getDescription(locale));
		}
		model.setPrice(new BigDecimal(item.getSellingPrice().getStringValue()));
		model.setUnitOfMeasure(item.getUnitOfMeasure().getUnitName());
		model.setTaxableFlag(item.getTaxable());
		model.setDiscountableFlag(item.isDiscountEligible());
		/* India Localization- MRP related Changes starts here */
		model.setMaximumRetailPrice(new BigDecimal(((MAXPLUItemIfc) item).getMaximumRetailPrice().getStringValue()));
		model.setRetailLessThanMRPFlag(((MAXPLUItemIfc) item).getRetailLessThanMRPFlag());
		model.setMultipleMaximumRetailPriceFlag(((MAXPLUItemIfc) item).getMultipleMaximumRetailPriceFlag());
		/* India Localization- MRP related Changes ends here */

		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		boolean searchForItemByManufacturer = false;
		boolean usePlanogramID = false;

		try {
			if (pm.getStringValue(SEARCH_ITEM_BY_MANUFACTURER).equalsIgnoreCase("Yes")) {
				searchForItemByManufacturer = true;
			}

			if (pm.getStringValue(PLANOGRAM_DISPLAY).equalsIgnoreCase("Yes")) {
				usePlanogramID = true;
			}
		} catch (ParameterException pe) {
			logger.error("Cannot retrive parameter value");
		}
		model.setSearchItemByManufacturer(searchForItemByManufacturer);
		model.setUsePlanogramID(usePlanogramID);

		if (model.isSearchItemByManufacturer()) {
			model.setItemManufacturer(item.getManufacturer(locale));
		}

		if (model.isUsePlanogramID()) {
			model.setPlanogramID(item.getPlanogramID());
		}

		Boolean sizeInput = new Boolean(false);
		try {
			sizeInput = pm.getBooleanValue(SIZE_INPUT_FIELD);
		} catch (ParameterException e) {
			logger.error("" + Util.throwableToString(e) + "");
		}

		if (sizeInput.booleanValue()) {
			model.setItemSizeRequired(true);
		} else {
			model.setItemSizeRequired(false);
		}
		showPriceInquiryJournal(model, item);

		// Display the screen
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.ITEM_DISPLAY, model);

		// Show item on Line Display device
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		try {
			pda.lineDisplayItem(item);
		} catch (DeviceException e) {
			logger.warn("Unable to use Line Display: " + e.getMessage() + "");
		}
	}

	public void depart(BusIfc bus) {

		LetterIfc letter = (LetterIfc) bus.getCurrentLetter();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
		SearchCriteriaIfc inquiry = cargo.getInquiry();
		inquiry.setItemNumber(ui.getInput());
		String letterName = null;
		if (letter instanceof ButtonPressedLetter) // Is ButtonPressedLetter
		{
			// Get the String representation of the letter name
			// from the LetterIfc object
			letterName = letter.getName();
			if (letterName != null && letterName.equals(CommonLetterIfc.UNDO)) {
				 cargo = (ItemInquiryCargo) bus.getCargo();
				cargo.setInquiry(null);
			}
		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Journal the price inquiry results.
	 * <P>
	 * 
	 * @param ItemInfoBeanModel
	 *            model
	 * @param PLUItemIfc
	 *            item
	 **/
	// --------------------------------------------------------------------------
	protected void showPriceInquiryJournal(ItemInfoBeanModel model, PLUItemIfc item) {
		JournalManagerIfc jmi = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
		if (jmi != null) {
			StringBuffer content = new StringBuffer();
			content.append(PRICE_INQUIRY).append("\n");
			content.append(ITEM_NUMBER).append(model.getItemNumber()).append("\n");
			content.append(ITEM_DESCRIPTION).append(model.getItemDescription()).append("\n");
			content.append(ITEM_MANUFACTURER).append(model.getItemManufacturer()).append("\n");
			if (item.isItemSizeRequired())
				content.append(ITEM_SIZE).append(model.getItemSize()).append("\n");
			content.append(ITEM_DEPARTMENT).append(model.getItemDept()).append("\n");
			Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
			String formattedPrice = CurrencyServiceLocator.getCurrencyService()
					.formatCurrency(model.getPrice(), locale);
			content.append(ITEM_PRICE).append(formattedPrice).append("\n");
			content.append(ITEM_UNIT).append(model.getUnitOfMeasure()).append("\n");

			if (model.isTaxable() == true)
				content.append(ITEM_TAXABLE).append(YES).append("\n");
			else
				content.append(ITEM_TAXABLE).append(NO).append("\n");
			// change true to YES, false to NO
			if (model.isDiscountable() == true)
				content.append(ITEM_DISCOUNTABLE).append(YES).append("\n");
			else
				content.append(ITEM_DISCOUNTABLE).append(NO).append("\n");

			if (model.isUsePlanogramID()) {
				content.append(ITEM_PLANOGRAM_ID);
				if (model.getPlanogramID() != null) {
					int planogram = model.getPlanogramID().length;
					for (int i = 0; i < planogram; i++) {
						String planogramID = model.getPlanogramID()[i];
						content.append(planogramID).append("\n");
						content.append("\t");
					}
				}
			}
			jmi.journal(content.toString());
		}
	}


	public void reset(BusIfc bus) {
		arrive(bus);
	}
}
