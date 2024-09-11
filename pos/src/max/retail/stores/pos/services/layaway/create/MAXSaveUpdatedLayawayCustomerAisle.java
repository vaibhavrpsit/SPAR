/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 MAX . All Rights Reserved.
 *		
 * Rev 1.1  Mar 21, 2015 Hitesh Dua
 * Layaway initiate should print the "Transaction re-entry" in the receipt. (Reentry Mode)
 *
 * Rev 1.0  Mar 24, 2015 Akhilesh
 * Initial revision.
 * Resolution for FES_LMG_India_Customer_Loyalty_v1.3
 * Get the model for crm customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.layaway.create;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.DataFormatException;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CustomerWriteDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

public class MAXSaveUpdatedLayawayCustomerAisle extends PosLaneActionAdapter {
	/**
    class name constant
	 **/
	public static final String LANENAME = "SaveUpdatedLayawayCustomerAisle";
	/**
    revision number for this class
	 **/
	public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";
	/**
    blanks for layaway header journal string
	 **/
	public static final String headerBlanks = "                 ";
	/**
    layaway header journal string
	 **/
	public static final String layawayHeader = "Layaway";
	/**
    layaway number journal string
	 **/
	public static final String layawayNumber = "Layaway Number: ";
	/**
    layaway expiration date journal string
	 **/
	public static final String expirationHeader = "Expiration Date: ";
	/**
    date format for journal string
    @deprecated as of release 5.5
	 **/
	public static final String dateFormat = "MM/dd/yyyy";
	/**
    layaway customer journal string
	 **/
	public static final String customerHeader = "Customer: ";
	/**
    transaction discount journal string
	 **/
	public static final String transactionDiscount = "TRANS: Discount";
	/**
    discount percent journal string
	 **/
	public static final String discountPercent = "Discount % ";
	/**
    discount reason journal string
	 **/
	public static final String discountReason = "Disc. Rsn. ";
	/**
    loop letter constant
	 **/
	public static final String loopLetter = "Loop";

	/**
   Customer name bundle tag
	 **/
	protected static final String CUSTOMER_NAME_TAG = "CustomerName";
	/**
   Customer name default text
	 **/
	protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

	//--------------------------------------------------------------------------
	/**
    Get the data from the layaway customer screen.
    Update the customer record in the DB.
    <P>
    @param bus the bus traversing this lane
	 **/
	//--------------------------------------------------------------------------
	public void traverse(BusIfc bus)
	{
		// get the journal manager
		JournalManagerIfc jmi =
			(JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
		// get the prameter manager
		ParameterManagerIfc pm =
			(ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		// get the ui manager
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// initialize the layaway id flag as false
		boolean setID = false;
		// initialize mail letter flag
		boolean mailLetter = false;

		// get cargo
		LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();

		// get the layaway tranasaction
		LayawayTransactionIfc layawayTransaction =
			layawayCargo.getInitialLayawayTransaction();

		// create the layaway transaction if one not already created
		if (layawayTransaction == null)
		{
			// get the sale transaction from cargo
			SaleReturnTransaction saleTransaction =
				(SaleReturnTransaction)layawayCargo.getSaleTransaction();
			// create an instance of a layaway transaction
			layawayTransaction = DomainGateway.getFactory().getLayawayTransactionInstance();
			// initialize the layaway transaction with the sale transaction
			layawayTransaction.initialize(saleTransaction);
			layawayTransaction.setLayaway(DomainGateway.getFactory().getLayawayInstance());
			setID = true;
		}

		// get the current customer from the transaction
		CustomerIfc customer = layawayTransaction.getCustomer();

		///changes to get The customer from cargO AKHILESH Start
		MAXCustomerIfc MAXCustomer=null;
		if(layawayCargo.getCustomer()!=null && layawayCargo.getCustomer() instanceof MAXCustomerIfc){
		 MAXCustomer = (MAXCustomerIfc) layawayCargo.getCustomer();
		}
	    ///changes to get The customer from cargO AKHILESH END
		
		// get Mail Bank Check model
		MailBankCheckInfoBeanModel model =
			(MailBankCheckInfoBeanModel) ui.getModel(POSUIManagerIfc.CUSTOMER_LAYAWAY);

		// reset model for MBC transactions
		model.setLayawayFlag(false);

		// data was changed at Layaway Customer screen
		if (model.getChangeState())
		{
			try
			{
				// update customer info from model
				customer = copyModelDatatoCustomer(customer, model);

				// save updated customer to db
	// Changes start for code merging(comment below line)
				//CustomerUpdateDataTransaction customerTransaction = null;
				CustomerWriteDataTransaction customerTransaction = null;
				
				//customerTransaction = (CustomerUpdateDataTransaction) DataTransactionFactory.create(DataTransactionKeys.CUSTOMER_UPDATE_DATA_TRANSACTION);
				customerTransaction = (CustomerWriteDataTransaction) DataTransactionFactory.create(DataTransactionKeys.CUSTOMER_WRITE_DATA_TRANSACTION);
	//Changes ends for code merging

				// If there is a linked customer
				if (customer != null)
				{
					customerTransaction.saveCustomer(customer);
				}
				else
				{
					logger.warn(
							"No customer to save!");
				}

				mailLetter = true;
			}
			catch (DataException exception)
			{
				UtilityManagerIfc utility =
					(UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

				// Show the error dialog screen
				String errorString[] = new String[3];
				errorString[0] = utility.getErrorCodeString(exception.getErrorCode());
				errorString[1] = utility.retrieveDialogText
				("CustDatabaseError.UpdateFailed",
				"Customer database update failed.");
				errorString[2] = utility.retrieveDialogText
				("CustDatabaseError.FailProceed",
				"Press Enter to proceed without updating the customer database.");
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("CustDatabaseError");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setArgs(errorString);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.SUCCESS);

				// display dialog
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			catch(DataFormatException e)
			{
				// Using "generic dialog bean".
				DialogBeanModel dialogModel = new DialogBeanModel();
				// Set model to same name as dialog in config\posUI.properties
				dialogModel.setResourceID("InvalidPostalCode");
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, loopLetter);

				// set and display the model
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
		}
		else     // customer info did not change - continue
		{
			mailLetter = true;
		}

		// get the register
		RegisterIfc register = (RegisterIfc) layawayCargo.getRegister();

		// set up the rest of the layaway -
		initializeNewLayaway(layawayTransaction, jmi, pm, customer, register, ui,
				setID, bus.getServiceName());

		// set training mode on layaway
		layawayTransaction.getLayaway().setTrainingMode(register.getWorkstation().isTrainingMode());
		//changes for rev 1.1 : set re-entry mode for layaway customer
		layawayTransaction.setReentryMode(register.getWorkstation().isTransReentryMode());
		// set customer to transaction
		layawayTransaction.setCustomer(customer);
		// set layaway transction to cargo
		layawayCargo.setInitialLayawayTransaction(layawayTransaction);

		// Send the Success letter if save succeeded and journal.
		if (mailLetter)
		{
			// journal customer data updates
			jmi.journal(layawayTransaction.getCashier().getEmployeeID(),
					layawayTransaction.getTransactionID(),
					model.getJournalString());

			// journal layaway info
			Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
			jmi.journal(layawayTransaction.getCashier().getEmployeeID(),
					layawayTransaction.getTransactionID(),
					"\n" + headerBlanks + layawayHeader + "  " + "\n\n" + layawayNumber +
					layawayTransaction.getLayaway().getLayawayID() + "\n  " + expirationHeader +
					layawayTransaction.getLayaway().getExpirationDate().toFormattedString(locale) +
					"\n  " + customerHeader + customer.getCustomerName());

			// Get the phone(s) info for this customer and print the phone type(s) for the journal
			Vector phones = customer.getPhones();
			for (int i=0; i<phones.size(); i++)
			{
				if (phones.get(i) != null)
				{
					jmi.journal(layawayTransaction.getCashier().getEmployeeID(),
							layawayTransaction.getTransactionID(), "  " +
							customer.getPhoneByType(((PhoneIfc)phones.get(i)).getPhoneType()).toFormattedString());
				}
			}

			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		}
	}

	//--------------------------------------------------------------------------
	/**
    Gets the data from the MailBankCheckInfoBeanModel
    Updates the customer information with the model data
    <P>
    @param  customer reference
    @param  MailBankCheckInfoBeanModel reference
	 **/
	//--------------------------------------------------------------------------
	protected CustomerIfc copyModelDatatoCustomer(CustomerIfc customer,
			MailBankCheckInfoBeanModel model)
	throws DataFormatException
	{
		CustomerUtilities.updateCustomer(customer, model);


		AddressIfc address = DomainGateway.getFactory().getAddressInstance();
		Vector lines = new Vector();
		lines.addElement(model.getAddressLine1());
		lines.addElement(model.getAddressLine2());
		lines.addElement(model.getAddressLine3());
		address.setLines(lines);
		address.setCity(model.getCity());
		address.setState(model.getState());
		address.setCountry(model.getCountryName());

		// validate postal code
		String postalString = address.validatePostalCode(model.getPostalCode(), model.getCountry());
		address.setPostalCode(postalString);
// Changes starts for code merging(coment below line)
		//deprecated in 14.1
				//address.setPostalCodeExtension(model.getExtPostalCode());
// Changes ends for code merging
		Vector addresses = new Vector();
		addresses.addElement(address);
		customer.setAddresses(addresses);

		customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED).setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);

		return customer;
	}

	//--------------------------------------------------------------------------
	/**
    Calculates and sets the layaway expiration date, links the customer to the
    layaway, journals the layaway creation based upon the journalFlag, and sets
    the customer's name in the ui status area.
    <P>
    @param  layawayTransaction  reference
    @param  JournalManagerIfc reference
    @param  ParameterManagerIfc reference
    @param  CustomerIfc reference
    @param  RegisterIfc reference
    @param  POSUIManagerIfc reference
    @param  boolean flag whether to journal the layaway
    @param  boolean flag whether to journal the phone number
    @param  boolean flag whether to create the layaway id
    @param serviceName service name used in log
	 **/
	//--------------------------------------------------------------------------
	protected void initializeNewLayaway(LayawayTransactionIfc layawayTransaction,
			JournalManagerIfc jmgr, ParameterManagerIfc pm,
			CustomerIfc customer,  RegisterIfc register,
			POSUIManagerIfc ui,
			boolean setID,
			String serviceName)
	{
		// get layaway duration parameter value
		Integer layawayDuration = new Integer(30);
		List roundingDenominations=null;
		String rounding=null;
		try
		{
			layawayDuration = pm.getIntegerValue("LayawayDuration");
// Changes starts for code merging(commenting below code)
			//commented rounding code as per 14.1
			/*rounding = pm.getStringValue(RoundingConstantsIfc.ROUNDING);
			String[] roundingDenominationsArray = pm.getStringValues(RoundingConstantsIfc.ROUNDING_DENOMINATIONS);


			if(roundingDenominationsArray == null || roundingDenominationsArray.length == 0)
			{
				throw new ParameterException("List of parameters undefined");
			}
			roundingDenominations = new ArrayList();
			roundingDenominations.add(0,new BigDecimal(0.0));
			for(int i=0;i<roundingDenominationsArray.length;i++)
			{
				roundingDenominations.add(new BigDecimal(roundingDenominationsArray[i]));
			}
			roundingDenominations.add(roundingDenominationsArray.length,new BigDecimal(1.00));

			//    		List must be sorted before setting on the cargo.
			Collections.sort(roundingDenominations,new Comparator()	{
				public int compare(Object o1, Object o2) {
					BigDecimal denomination1 = (BigDecimal)o1;
					BigDecimal denomination2 = (BigDecimal)o2;
					return denomination1.compareTo(denomination2);
				}
			});
		*/
// Changes ends for code merging	
		}
		catch (ParameterException e)
		{
			logger.warn( Util.throwableToString(e));
		}

		// Calculate and set Layaway Expiration Date
		EYSDate expirationDate = DomainGateway.getFactory().getEYSDateInstance();
		Calendar cal = Calendar.getInstance();
		cal.setTime(expirationDate.dateValue());
		cal.add(Calendar.DAY_OF_MONTH, layawayDuration.intValue());
		expirationDate.initialize(cal.getTime());
		layawayTransaction.getLayaway().setExpirationDate(expirationDate);
// Changes starts for code merging(commenting below code)
		//set Rounding parameter
		//layawayTransaction.setRounding(rounding);
		//layawayTransaction.setRoundingDenominations(roundingDenominations);
// Changes ends for code merging

		// link the customer, set type and update cargo
		layawayTransaction.linkCustomer(customer);
		layawayTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_INITIATE);

		// generate new id
		if (setID)
		{
			layawayTransaction.getLayaway().setLayawayID(register.getNextUniqueID());
			layawayTransaction.setUniqueID(register.getCurrentUniqueID());
		}

		// set transaction details
		layawayTransaction.getLayaway().setInitialTransactionBusinessDate
		(register.getBusinessDate());
		layawayTransaction.getLayaway().setInitialTransactionID
		(layawayTransaction.getTransactionIdentifier());
		layawayTransaction.getLayaway().setStoreID
		(layawayTransaction.getTransactionIdentifier().getStoreID());

		// set the customer's name in the status panel
		StatusBeanModel statusModel = new StatusBeanModel();

		// Create the string from the bundle.
		CustomerIfc layawayCustomer = layawayTransaction.getCustomer();

		ui.customerNameChanged(layawayCustomer.getCustomerName());

		POSBaseBeanModel baseModel = (POSBaseBeanModel)ui.getModel();
		if (baseModel == null)
		{
			baseModel = new POSBaseBeanModel();
		}

		baseModel.setStatusBeanModel(statusModel);
	}                                   // end initializeNewLayaway()
}
