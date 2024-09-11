/* ===========================================================================
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/add/SaveSpecialOrderCustomerAisle.java /main/20 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    mjwallac  05/02/12 - Fortify: fix redundant null checks, part 4
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    asinton   03/07/12 - Use new CustomerManager instead of DataTransaction
 *                         method to access customer data.
 *    npoola    10/25/10 - load the model from special order screen
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   09/29/09 - Modified to use new method in CustomerUtilities to
 *                         journal customer information.
 *    asinton   09/29/09 - XbranchMerge asinton_bug-8889855 from
 *                         rgbustores_13.1x_branch
 *    asinton   09/10/09 - Added customer data to EJournal per requirements.
 *    mahising  03/04/09 - Fixed special order issue for business customer
 *    vchengeg  01/07/09 - ej defect fixes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/15 16:59:44  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:13  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:52:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:07:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   02 Jun 2003 00:38:26   baa
 * fix null pointer
 *
 *    Rev 1.5   May 02 2003 14:26:58   baa
 * add business customer hooks to special order
 * Resolution for POS SCR-2263:  Sp. Order, Link business customer, POS is crashed at Customer Options screen
 *
 *    Rev 1.4   Mar 21 2003 10:58:42   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Sep 25 2002 10:45:18   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 20 2002 17:55:18   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 18 2002 17:15:24   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:02:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:48:12   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 10 2002 18:01:20   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Feb 16 2002 17:52:22   dfh
 * journals updates to customer, only the info that changed
 * Resolution for POS SCR-1321: Updating customer info on Special Order Customer does not journal
 *
 *    Rev 1.3   01 Feb 2002 13:17:16   vxs
 * Ejournal updates
 * Resolution for POS SCR-985: Special Order - EJournal completion
 *
 *    Rev 1.2   Jan 14 2002 09:27:22   dfh
 * changed method name to copyModelDataToCustomer
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   26 Oct 2001 12:39:28   jbp
 * added email address to special order customer screen
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 04 2001 14:55:14   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.add;

// java imports
import java.util.zip.DataFormatException;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;

/**
 * Determines whether to save the special order customer information from the customer
 * special order screen. Mails the Continue letter when the customer data was changed or
 * when the customer data was successfully saved to the database.
 */
@SuppressWarnings("serial")
public class SaveSpecialOrderCustomerAisle extends PosLaneActionAdapter
{
    /**
        class name constant
    **/
    public static final String LANENAME = "SaveSpecialOrderCustomerAisle";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/20 $";
    /**
        blanks for special order header journal string
    **/
    public static final String headerBlanks = "                 ";
    /**
        special order header journal string
    **/
    public static final String specialOrderHeader = "SPECIAL ORDER";
    /**
        special order number journal string
    **/
    public static final String specialOrderNumber = "Order Number: ";
    /**
        special order customer journal string
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

   //--------------------------------------------------------------------------
    /**
        Get the data from the special order customer screen.
        Updates the customer record in the DB. Displays the invalid postal code
        error screen when the postal code is not formatted correcly for the
        selected country.
        <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Get the SpecialOrder Cargo
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo) bus.getCargo();
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();
        CustomerIfc customer = orderTransaction.getCustomer();
        UtilityManagerIfc utility =   (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

        // Get the customer data from the UI
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel) ui.getModel(POSUIManagerIfc.CUSTOMER_SPECIAL_ORDER);

        boolean mailLetter = false;

        // resets the model to false for MailBankCheck Transactions not involving layaway/special order
        model.setLayawayFlag(false);

        if (model.getChangeState()) // data was changed at Special Order Customer screen
        {
            // attempt to validate postal code and do the database update
            try
            {
                AddressIfc address = DomainGateway.getFactory().getAddressInstance();
                String postalString = address.validatePostalCode(model.getPostalCode(), model.getCountry());
                model.setPostalCode(postalString);
                // move data from customer special order screen to the customer to save
                customer = copyModelDataToCustomer(customer, model);

                // If there is a linked customer
                if (customer != null)
                {
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                    customerManager.saveCustomer(customer);
                }
                else
                {
                    logger.warn(
                        "No customer to save!");
                }

                // move on even if there was nothing to save... which shouldn't
                // happen
                mailLetter = true;

                if (customer != null)
                {
                    ui.customerNameChanged(customer.getCustomerName());
                }
            }
            catch (DataException exception)
            {
                logger.warn(
                    "Unable to save customer: [" + exception + "]");

                // Show the error dialog screen
                logger.error(
                        "[" + exception + "]");

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
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.SUCCESS);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            catch( DataFormatException e)
            {
                // Using "generic dialog bean".
                DialogBeanModel dialogModel = new DialogBeanModel();

                // Set model to same name as dialog in config\posUI.properties
                dialogModel.setResourceID("InvalidPostalCode");
                dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.RETRY);
                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
        }
        else     // customer info did not change - continue
        {
            mailLetter = true;
        }
        // Send the Success letter if save succeeded.
        if (mailLetter)
        {
            orderTransaction.setCustomer(customer);
            journalCustomer(bus.getServiceName(),orderTransaction,customer,jmi,model);
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
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
    protected CustomerIfc copyModelDataToCustomer(CustomerIfc customer, MailBankCheckInfoBeanModel model)
    {
       return CustomerUtilities.updateCustomer(customer, model);

    }
   //--------------------------------------------------------------------------
    /**
        Journals the customers information: name, address, city, state/province,
        and postal code.
        <P>
        @param  BusIfc bus reference
        @param  SaleReturnTransaction saleTransaction reference
        @param  CustomerIfc customer reference
        @param  JournalManagerIfc jmi reference

    **/
    //--------------------------------------------------------------------------
    protected void journalCustomer(String serviceName,OrderTransactionIfc orderTransaction,
                                   CustomerIfc customer,JournalManagerIfc jmi,
                                   MailBankCheckInfoBeanModel model )
    {
        // journal special order customer info
        if (jmi != null)
        {
        	CustomerUtilities.journalCustomerInformation(customer, jmi, orderTransaction);
        }
        else
        {
           logger.error( "No journal manager found!");
        }
        // journal customer data updates
         if (jmi != null)
        {
            jmi.journal(orderTransaction.getCashier().getEmployeeID(),
                        orderTransaction.getTransactionID(),
                        model.getJournalString());
        }
        else
        {
           logger.error( "No journal manager found!");
        }
    }

    /**
     * Adds the journals entry to the given StringBuilder if text is not empty.
     * @param sb
     * @param labelKey
     * @param text
     */
    protected void createJournalEntry(StringBuilder sb, String labelKey, String text)
    {
        if(!Util.isEmpty(text))
        {
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, labelKey, new Object[]{text}));
        }
    }

}  // end SaveSpecialOrderCustomerAisle
