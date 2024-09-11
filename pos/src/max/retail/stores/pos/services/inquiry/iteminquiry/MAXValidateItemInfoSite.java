/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.3		Mar 22, 2017		Nitika Arora	Changes for fixing the database error on clicking the search button
 *	Rev 1.2     Mar 02, 2017        Abhishek Goyal  Changes for multiple rows displaying for item with multiple barcodes 
 *	Rev 1.1		Jan 27, 2016		Ashish Yadav	Changes for item Inquiry
 *	Rev 1.0		Dec 27, 2016		Mansi Goel		Changes for Advanced Search
 *
 ********************************************************************************/

package max.retail.stores.pos.services.inquiry.iteminquiry;

//java imports
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 * This site validates the item number stored in the cargo.
 * 
 * @version $Revision: 7$
 **/
// --------------------------------------------------------------------------
public class MAXValidateItemInfoSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 8574691248887956000L;
	public static final int ITEM_NUMBER_FIELD = 1;
	/**
	 * item description field
	 **/
	public static final int ITEM_DESC_FIELD = 2;

	/**
	 * item manufacturer field
	 **/
	public static final int ITEM_MANUFAC_FIELD = 3;

	/**
	 * constant for parameter name
	 **/
	public static final String ITEM_MAXIMUM_MATCHES = "ItemMaximumMatches";

	/**
	 * UI wildcard tag
	 **/
	public static final String UI_WILDCARD_TAG = "UIWildcard";
	/**
	 * UI wildcard
	 **/
	public static final String UI_WILDCARD = "*";
	/**
	 * DB wildcard tag
	 **/
	public static final String DB_WILDCARD_TAG = "DBWildcard";
	/**
	 * DB wildcard
	 **/
	public static final String DB_WILDCARD = "%";
	/**
	 * default string for PlanogramDisplay
	 **/
	public static final String PLANOGRAM_DISPLAY = "PlanogramDisplay";

	// ----------------------------------------------------------------------
	/**
	 * Validate the item info stored in the cargo( number, desc and dept) . If
	 * the item is found, a Success letter is sent. Otherwise, a Failure letter
	 * is sent.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// letter to be sent
		String letter = null;

		// get item inquiry from cargo
		ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
		SearchCriteriaIfc inquiry = (SearchCriteriaIfc) cargo.getInquiry();
		cargo.resetInvalidFieldCounter();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		System.out.println("110:"+cargo.getLiqcat());

		boolean usePlanogramID = false;
		try {
			if ((pm.getStringValue(PLANOGRAM_DISPLAY)).equalsIgnoreCase("Yes")) {
				usePlanogramID = true;
			}
		} catch (ParameterException pe) {
			logger.error("Cannot retrive parameter value");
		}
        // Changes for Rev 1.3 starts
		 if ((inquiry.getDescription() == null) && (inquiry.getItemNumber() == null || inquiry.getItemNumber().equals("")) && (inquiry.getManufacturer() == null))
		    {
		      cargo.setInvalidField(1);
		      cargo.setInvalidField(2);
		      letter = "Invalid";
		    }
		 // Changes for Rev 1.3 ends
		// validate input fields prior to execute transaction
		//Changes for Rev 1.0 : Starts
		 else if (!isValidField(inquiry.getItemNumber())) {
			// check if the search critera is valid
			cargo.setInvalidField(ITEM_NUMBER_FIELD);
			if (!isValidField(inquiry.getDescription())) {
				// check if the search critera is valid
				cargo.setInvalidField(ITEM_DESC_FIELD);
				if (!isValidField(inquiry.getManufacturer())) {
					// check if the search critera is valid
					cargo.setInvalidField(ITEM_MANUFAC_FIELD);
				}
			}
			letter = CommonLetterIfc.NEXT;
			//Changes for Rev 1.0 : Ends
		} else if (!isValidField(inquiry.getDescription())) {
			// check if the search critera is valid
			cargo.setInvalidField(ITEM_DESC_FIELD);
			letter = CommonLetterIfc.INVALID;
		} else if (!isValidField(inquiry.getManufacturer())) {
			// check if the search critera is valid
			cargo.setInvalidField(ITEM_MANUFAC_FIELD);
			letter = CommonLetterIfc.INVALID;
		} else {
			// go ahead with the database search
			SearchCriteriaIfc searchInquiry = (SearchCriteriaIfc) inquiry.clone();

			searchInquiry.setItemID(replaceStar(inquiry.getItemNumber()));
			searchInquiry.setDescription(replaceStar(inquiry.getDescription()));
			searchInquiry.setManufacturer(replaceStar(inquiry.getManufacturer()));
			searchInquiry.setSearchForItemByManufacturer(inquiry.isSearchForItemByManufacturer());
			searchInquiry.setUsePlanogramID(usePlanogramID);
			letter = getItems(bus, cargo, searchInquiry);
		}

		/*
		 * Proceed to the next site
		 */
		if (letter != null) {
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
		//System.out.println("Validate168 :"+cargo.getItemDesc());

	}

	// ----------------------------------------------------------------------
	/**
	 * Returns a list of items matching the search criteria
	 * 
	 * @param bus
	 * @param cargo
	 * @param inquiry
	 *            the inquiry search criteria
	 * @return String The letter to be sent
	 **/
	// ----------------------------------------------------------------------
	public String getItems(BusIfc bus, ItemInquiryCargo cargo, SearchCriteriaIfc inquiry) {
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		// letter to be sent
		String letter = null;
		try {
			PLUTransaction pluTransaction = null;

			pluTransaction = (PLUTransaction) DataTransactionFactory.create(DataTransactionKeys.PLU_TRANSACTION);

			// get list of items matching search criteria from database
			PLUItemIfc[] pluItems = null;
			inquiry.setMaximumMatches(getMaximumMatches(bus));
			inquiry.setLocaleRequestor(LocaleMap.getSupportedLocaleRequestor());
			// set storeID
			inquiry.setStoreNumber(cargo.getRegister().getWorkstation().getStoreID());
			pluItems = pluTransaction.getMatchingItems(inquiry);

			// if multiple items found display list of items
			/**Changes For Rev 1.2 starts*/
			//if (pluItems.length > 1)
			if (pluItems.length > 1 && (inquiry.getItemNumber()==null || inquiry.getItemNumber().trim().equals(""))){
			/**Changes For Rev 1.2 ends*/
				// store list of items on cargo
				cargo.setItemList(pluItems);

				// display list of matching items
				letter = CommonLetterIfc.NEXT;
			} else {
				// get department info
				DepartmentIfc department = getDepartment(utility, cargo.getRegister().getWorkstation().getStoreID(),
						pluItems[0].getDepartmentID());
				pluItems[0].setDepartment(department);

				// store the selected item on cargo
				cargo.setPLUItem(pluItems[0]);

				// display single item information
				letter = CommonLetterIfc.SUCCESS;
			}

		} catch (DataException de) {
			int errorCode = de.getErrorCode();
			logger.warn("ItemNo: " + inquiry.getItemID() + " \nItem Desc: " + inquiry.getDescription()
					+ " \nItem Dept: " + inquiry.getDepartmentID() + " \nItem Manufacturer: "
					+ inquiry.getManufacturer() + "");

			logger.warn("Error: " + de.getMessage() + " \n " + de + "");

			cargo.setDataExceptionErrorCode(errorCode);

			// Don't think that args is used, but...
			String args[] = new String[1];
			args[0] = utility.getErrorCodeString(de.getErrorCode());
			switch (de.getErrorCode()) {
			case DataException.NO_DATA:
				//Changes starts for Rev 1.1 (Ashish)
				cargo.setInquiry(null);
				//Changes ends for Rev 1.1 (Ashish)
				showErrorDialog(bus, "INFO_NOT_FOUND_ERROR", null, CommonLetterIfc.UNDO);
				break;
			case DataException.RESULT_SET_SIZE:
				showErrorDialog(bus, "TooManyMatches", null, CommonLetterIfc.RETRY);
				break;
			default:
				String msg[] = new String[1];
				msg[0] = utility.getErrorCodeString(de.getErrorCode());
				showErrorDialog(bus, "DatabaseError", msg, CommonLetterIfc.RETRY);

				break;
			}

		}

		return (letter);
	}


	public boolean isValidField(String data) {
		// isValid returns false if only a wild character is sent as data
		// true other wise.
		boolean isValid = true;

		// when using wild card search at least one character must be used
		// with the wildcard.This is to narrow the search
		if (data != null && (data.equals("%") || data.length() == 0)) {
			isValid = false;
		}
		return (isValid);
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns an Integer, the maximum matches allowed from the parameter file.
	 * <P>
	 * 
	 * @param bus
	 * @return int maximum matches allowed as Integer
	 */
	// ----------------------------------------------------------------------
	private int getMaximumMatches(BusIfc bus) {
		// get paramenter manager
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

		// maximum number of matches allowed
		Integer maximum = new Integer("100"); // default
		try {
			String s = pm.getStringValue(ITEM_MAXIMUM_MATCHES);
			s.trim();
			maximum = new Integer(s);
			if (logger.isInfoEnabled())
				logger.info("Parameter read: " + ITEM_MAXIMUM_MATCHES + " = [" + maximum + "]");
		} catch (ParameterException e) {
			logger.error("" + Util.throwableToString(e) + "");
		}

		return (maximum.intValue());
	}


	protected String replaceStar(String oldtext) {
		UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
		String uiWildcard = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
				UI_WILDCARD_TAG, UI_WILDCARD);
		String dbWildcard = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
				DB_WILDCARD_TAG, DB_WILDCARD);

		return replaceStar(oldtext, uiWildcard.charAt(uiWildcard.length() - 1),
				dbWildcard.charAt(uiWildcard.length() - 1));
	}

	protected String replaceStar(String oldtext, char uiWildCard, char dbWildCard) {
		// new string after wild card symbol replac3
		String newtext = null;

		if (oldtext != null && !oldtext.equals("")) {
			newtext = oldtext.replace(uiWildCard, dbWildCard);
		}

		return newtext;
	}

	protected DepartmentIfc getDepartment(UtilityManagerIfc utility, String storeID, String departmentID) {
		// retrieve department code list from reason codes.
		CodeListIfc deptMap = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_DEPARTMENT);

		// retrieve department entries
		CodeEntryIfc[] deptEntries = deptMap.getEntries();
		DepartmentIfc department = null;
		for (int i = 0; i < deptEntries.length; i++) {
			if (deptEntries[i].getCode().equals(departmentID)) {
				department = DomainGateway.getFactory().getDepartmentInstance();
				department.setLocalizedDescriptions(deptEntries[i].getLocalizedText());
				department.setDepartmentID(deptEntries[i].getCode());
				break;
			}
		}
		return department;
	}

	
	private void showErrorDialog(BusIfc bus, String id, String[] args, String letter) {
		POSUIManagerIfc ui;
		ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID(id);
		if (args != null) {
			model.setArgs(args);
		}
		model.setType(DialogScreensIfc.ERROR);
		model.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
}
