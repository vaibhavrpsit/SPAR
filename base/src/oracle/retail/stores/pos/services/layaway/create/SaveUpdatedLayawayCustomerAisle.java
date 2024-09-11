/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/create/SaveUpdatedLayawayCustomerAisle.java /main/25 2012/09/12 11:57:22 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   09/11/12 - Merge project Echo (MPOS) into Trunk.
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    asinton   03/26/12 - Customer UI changes to accomodate multiple
 *                         addresses.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    asinton   03/07/12 - Use new CustomerManager instead of DataTransaction
 *                         method to access customer data.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    rrkohli   12/10/10 - Fixed updated customer info in EJ
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    jswan     03/10/10 - Fixed issue with retrieved customer not printing on
 *                         EJ.
 *    abondala  01/03/10 - update header date
 *    mahising  03/18/09 - Fixed CSP issue if item qty change and customer link
 *                         to the transaction
 *    vchengeg  01/27/09 - ej defect fixes
 *    deghosh   12/23/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/21/2007 9:25:26 PM   Mathews Kochummen use
 *          locale format
 *    5    360Commerce 1.4         12/13/2006 11:15:27 AM Brett J. Larsen CR
 *         21298 - country was being assigned country code - the country name
 *         should be stored in the db
 *    4    360Commerce 1.3         1/25/2006 4:11:45 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     12/23/2005 17:17:52    Rohit Sachdeva  8203:
 *         Null Pointer Fix for Business Customer Info
 *    3    360Commerce1.2         3/31/2005 15:29:50     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:03     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:03     Robert Pearse
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/15 16:58:49  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 29 2003 10:42:44   blj
 * fixed a problem with journal data assuming home phone rather than getting the list of phone types.  (this problem discovered in v601)
 *
 *    Rev 1.0.1.0   Dec 29 2003 10:01:12   blj
 * changed the journal so that it prints all phone numbers for the customer
 * Resolution for 3602: New Layaway creation using a customer with a non-Home phone type crashes POS
 *
 *    Rev 1.0   Aug 29 2003 16:00:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.11   02 Jun 2003 00:24:00   baa
 * save customer data
 *
 *    Rev 1.10   May 06 2003 13:41:08   baa
 * updates for business customer
 * Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 *
 *    Rev 1.9   Mar 21 2003 10:58:32   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.8   Mar 20 2003 18:18:54   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.7   Feb 19 2003 13:51:42   crain
 * Replaced abbreviations
 * Resolution for 1760: Layaway feature updates
 *
 *    Rev 1.6   Jan 21 2003 15:41:28   crain
 * Changed to accomodate business customer
 * Resolution for 1760: Layaway feature updates
 *
 *    Rev 1.5   Sep 25 2002 10:41:20   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.4   Sep 20 2002 17:55:16   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Sep 18 2002 17:15:22   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 29 2002 13:47:34   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   24 Jun 2002 11:45:22   jbp
 * merge from 5.1 SCR 1726
 * Resolution for POS SCR-1726: Void - Void of new special order gets stuck in the queue in DB2
 *
 *    Rev 1.0   Apr 29 2002 15:21:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:46   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 10 2002 18:00:26   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Feb 21 2002 08:53:10   dfh
 * quick fix for journalling changes to layaway customer
 * Resolution for POS SCR-1322: Updating customer info on Layaway Customer does not journal
 *
 *    Rev 1.2   Feb 20 2002 13:13:48   dfh
 * updates to journal changes to customer data for the layaway customer, only changed data is journaled
 * Resolution for POS SCR-1322: Updating customer info on Layaway Customer does not journal
 *
 *    Rev 1.1   04 Feb 2002 17:36:04   jbp
 * journal layaway info after Layaway Customer screen.
 * Resolution for POS SCR-996: Adding new Customer thru Layaway causes system to hang
 *
 *    Rev 1.0   Sep 21 2001 11:21:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.create;

// java imports
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.DataFormatException;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
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
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Determines whether to save the layaway customer information from the customer
 * layaway screen. Mails the continue letter when customer was changed or when
 * customer was successfully saved to the database.
 *
 * @version $Revision: /main/25 $
 */
public class SaveUpdatedLayawayCustomerAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 3155953369192180252L;

    /**
     * class name constant
     */
    public static final String LANENAME = "SaveUpdatedLayawayCustomerAisle";

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/25 $";

    /**
     * blanks for layaway header journal string
     */
    public static final String headerBlanks = "                 ";

    /**
     * layaway header journal string
     */
    public static final String layawayHeader = "Layaway";

    /**
     * layaway number journal string
     */
    public static final String layawayNumber = "Layaway Number: ";

    /**
     * layaway expiration date journal string
     */
    public static final String expirationHeader = "Expiration Date: ";

    /**
     * date format for journal string
     *
     * @deprecated as of release 5.5
     */
    public static final String dateFormat = "MM/dd/yyyy";

    /**
     * layaway customer journal string
     */
    public static final String customerHeader = "Customer: ";

    /**
     * transaction discount journal string
     */
    public static final String transactionDiscount = "TRANS: Discount";

    /**
     * discount percent journal string
     */
    public static final String discountPercent = "Discount % ";

    /**
     * discount reason journal string
     */
    public static final String discountReason = "Disc. Rsn. ";

    /**
     * loop letter constant
     */
    public static final String loopLetter = "Loop";

    /**
     * Customer name bundle tag
     */
    protected static final String CUSTOMER_NAME_TAG = "CustomerName";

    /**
     * Customer name default text
     */
    protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    /**
     * Get the data from the layaway customer screen. Update the customer record
     * in the DB.
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get the journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
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
        CustomerIfc originalCustomer = DomainGateway.getFactory().getCustomerInstance();
        originalCustomer = (CustomerIfc) customer.clone();

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
                model.setFromLayaway(true);
                // update customer info from model
                customer = copyModelDatatoCustomer(customer, model);


                // If there is a linked customer
                if (customer != null)
                {
                    // save updated customer to db
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                    customerManager.saveCustomer(customer);
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
        RegisterIfc register = layawayCargo.getRegister();

        // set up the rest of the layaway -
        initializeNewLayaway(layawayTransaction, jmi, pm, customer, register, ui,
                             setID, bus.getServiceName());

        // set training mode on layaway
        layawayTransaction.getLayaway().setTrainingMode(register.getWorkstation().isTrainingMode());
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
                        model.getJournalString()+ CustomerUtilities.getChangedCustomerData(originalCustomer, customer));

            // journal layaway info
            Locale locale = LocaleMap
					.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
			StringBuffer sb = new StringBuffer();
			Object[] dataArgs = new Object[]{""};
			dataArgs[0] = layawayTransaction.getLayaway().getLayawayID();
			sb.append(Util.EOL+Util.EOL
					+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.LAYAWAY_LABEL, null)
					+ Util.EOL
					+ Util.EOL
					+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.LAYAWAY_NUMBER_LABEL, dataArgs)
					+ Util.EOL);
			dataArgs[0] = layawayTransaction.getLayaway().getExpirationDate()
					.toFormattedString(locale);
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.EXPIRATION_DATE_LABEL, dataArgs)
					+ Util.EOL);

			// The customer object behaves differently depending on whether is read
			// or created.  Make sure the customers first and last name are used
		    // if this is not a business customer.
			if (!Util.isEmpty(customer.getCustomerName()))
			{
			    dataArgs[0] = customer.getCustomerName();
			}
			else
			{
			    if (customer.isBusinessCustomer())
			    {
			        dataArgs[0] = "";
			    }
			    else
			    {
			        dataArgs[0] = customer.getFirstName() + " " + customer.getLastName();
			    }
			}
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
					JournalConstantsIfc.CUSTOMER_TAG_LABEL, dataArgs));


            // Get the phone(s) info for this customer and print the phone
			// type(s) for the journal
            List<PhoneIfc> phones = customer.getPhoneList();
            for (int i=0; i<phones.size(); i++)
            {
                if (phones.get(i) != null)
                {
                	sb.append(Util.EOL).append(customer.getPhoneByType(((PhoneIfc)phones.get(i)).getPhoneType()).toFormattedString());
                }
            }


			jmi.journal(layawayTransaction.getCashier().getEmployeeID(),
					layawayTransaction.getTransactionID(), sb.toString());

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }

    /**
     * Gets the data from the MailBankCheckInfoBeanModel Updates the customer
     * information with the model data
     *
     * @param customer reference
     * @param MailBankCheckInfoBeanModel reference
     */
    protected CustomerIfc copyModelDatatoCustomer(CustomerIfc customer,
                                           MailBankCheckInfoBeanModel model)
                                           throws DataFormatException
    {
        CustomerUtilities.updateCustomer(customer, model);


        AddressIfc address = DomainGateway.getFactory().getAddressInstance();
        Vector<String> lines = new Vector<String>(3);
        lines.addElement(model.getAddressLine1());
        lines.addElement(model.getAddressLine2());
        lines.addElement(model.getAddressLine3());
        address.setLines(lines);
        address.setCity(model.getCity());
        address.setState(model.getState());
        address.setCountry(model.getCountry());

        // validate postal code
        String postalString = address.validatePostalCode(model.getPostalCode(), model.getCountry());
        address.setPostalCode(postalString);
        List<AddressIfc> addresses = new ArrayList<AddressIfc>(1);
        addresses.add(address);
        customer.setAddressList(addresses);

        customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED).setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);

        return customer;
    }

    /**
     * Calculates and sets the layaway expiration date, links the customer to
     * the layaway, journals the layaway creation based upon the journalFlag,
     * and sets the customer's name in the ui status area.
     *
     * @param layawayTransaction reference
     * @param JournalManagerIfc reference
     * @param ParameterManagerIfc reference
     * @param CustomerIfc reference
     * @param RegisterIfc reference
     * @param POSUIManagerIfc reference
     * @param boolean flag whether to journal the layaway
     * @param boolean flag whether to journal the phone number
     * @param boolean flag whether to create the layaway id
     * @param serviceName service name used in log
     */
    protected void initializeNewLayaway(LayawayTransactionIfc layawayTransaction,
                                        JournalManagerIfc jmgr, ParameterManagerIfc pm,
                                        CustomerIfc customer,  RegisterIfc register,
                                        POSUIManagerIfc ui,
                                        boolean setID,
                                        String serviceName)
    {
        // get layaway duration parameter value
        Integer layawayDuration = new Integer(30);
        try
        {
            layawayDuration = pm.getIntegerValue(ParameterConstantsIfc.LAYAWAY_LayawayDuration);
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
    }
}
