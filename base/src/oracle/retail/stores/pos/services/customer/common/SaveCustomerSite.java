/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/SaveCustomerSite.java /main/43 2013/11/22 14:58:05 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    11/22/13 - added ej for mpos add customer
 *    jswan     11/06/13 - Fixed an issue with the journaling in which no
 *                         journal string was generated.
 *    asinton   09/09/13 - check for null taxExemptReason to prevent exception
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    acadar    08/05/12 - XC refactoring
 *    acadar    05/31/12 - code review comments
 *    acadar    05/30/12 - merge to tip
 *    acadar    05/30/12 - fixes
 *    acadar    05/29/12 - changes for cross channel
 *    vtemker   03/29/12 - Merged conflicts
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    asinton   03/08/12 - Use new CustomerManager instead of DataTransaction
 *                         to access customer data.
 *    mchellap  09/21/11 - Journal masked TaxId
 *    masahu    09/07/11 - Fix to NullPointerException in journaling tax ID
 *    masahu    09/02/11 - Customer Tax ID in EJournal
 *    rrkohli   12/10/10 - Fixed updated customer info in EJ
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mchellap  06/05/09 - Defect#3756 Fixed additional line in customer EJ
 *    acadar    03/23/09 - check for null pricing group id
 *    mahising  02/25/09 - Fixed e-journal issue for displaying business
 *                         customer lable
 *    vchengeg  02/11/09 - modified to journal the new customer add fields
 *                         namely Tax Certificate and Tax Exemption Reason
 *    vchengeg  02/10/09 - Checked the EJournal entry fields for a null value
 *                         and assigned them a blank string when null.
 *    vchengeg  02/09/09 - modified for i18n of customer information
 *    vchengeg  02/05/09 - Made changes to format EJournal for Discount and
 *                         Markdowns
 *    mahising  01/30/09 - revart back EJ issue for Business customer
 *    mahising  01/27/09 - fixed EJ issue of business customer
 *    mahising  01/20/09 - fix ejournal issue for customer
 *    mahising  12/23/08 - fix base issue
 *    vchengeg  12/08/08 - EJ I18n formatting
 *    deghosh   11/27/08 - EJ i18n changes
 *    mahising  11/26/08 - fixed merge issue
 *    mahising  11/25/08 - updated due to merge
 *    sswamygo  11/05/08 - Checkin after merges
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         4/15/2008 10:27:13 AM  Maisa De Camargo CR
 *         31123 - Using Resource Bundle to retrieve the message for the
 *         CustomerError Dialog.
 *         Replaced StringBuffer with StringBuilder and updated Copyright.
 *    7    360Commerce 1.6         2/11/2007 3:26:59 PM   Charles D. Baker CR
 *         24011 - fix merged from .v8x.
 *    6    360Commerce 1.5         8/7/2006 3:05:31 PM    Brett J. Larsen CR
 *         10796 - adding customer details to ejournal string
 *    5    360Commerce 1.4         1/25/2006 4:11:45 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:39 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse
 *:
 *
 *    6    .v7x      1.4.1.0     4/27/2006 5:12:59 AM   Dinesh Gautam   CR
 *         10796: Updated to add new customer details into EJ
 *
 *    5    .v710     1.2.2.0     9/21/2005 13:40:19     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    4    .v700     1.2.3.0     12/23/2005 17:17:51    Rohit Sachdeva  8203:
 *         Null Pointer Fix for Business Customer Info
 *    3    360Commerce1.2         3/31/2005 15:29:49     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:02     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:03     Robert Pearse
 *
 *   Revision 1.7  2004/08/05 16:16:15  jdeleau
 *   @scr 6782 Use Factory when creating CustomerWriteDataTransaction
 *
 *   Revision 1.6  2004/06/29 13:37:47  kll
 *   @scr 4400: usage of JournalManager's entry type to dictate whether Customer addition belongs inside or outside the context of a transaction
 *
 *   Revision 1.5  2004/04/12 18:58:36  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:55:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   May 27 2003 09:20:56   baa
 * cleanup
 * Resolution for 2483: MBC Customer Sceen
 *
 *    Rev 1.5   Apr 04 2003 17:07:04   baa
 * refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.4   Mar 20 2003 18:18:46   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Mar 03 2003 16:16:28   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Oct 09 2002 15:29:40   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:33:56   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:33:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:48   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:24:34   msg
 * Initial revision.
 *
 *    Rev 1.9   31 Jan 2002 14:13:26   baa
 * journal email address changes
 * Resolution for POS SCR-769: Changing email address on Customer Contact during Customer Find does not journal
 *
 *    Rev 1.8   30 Jan 2002 22:29:24   baa
 * customer ui fixex
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 *    Rev 1.7   14 Jan 2002 17:24:28   baa
 * fix for updating customer offline
 * Resolution for POS SCR-657: Updating Customer offline does not update Customer record when back online
 *
 *    Rev 1.6   11 Jan 2002 18:08:18   baa
 * update phone field
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.5   07 Jan 2002 13:20:48   baa
 * fix journal problems and adding offline
 * Resolution for POS SCR-506: Customer Find prints 'Add Custumer: ' in EJ
 * Resolution for POS SCR-507: Updates to Customer Info during Find use case does not journal
 * Resolution for POS SCR-519: Unable to add more than 1 customer offline
 *
 *    Rev 1.4   03 Jan 2002 15:39:42   vxs
 * Added check at very beginning to not execute code if in training mode.
 * Resolution for POS SCR-521: Customer package training mode updates
 *
 *    Rev 1.3   16 Nov 2001 10:32:14   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   05 Nov 2001 17:36:42   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.1   23 Oct 2001 16:53:02   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:14:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;


import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;

import oracle.retail.stores.domain.arts.DataManagerMsgIfc;

import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;

import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Site to save customer information to the database.
 *
 */
@SuppressWarnings("serial")
public class SaveCustomerSite extends PosSiteActionAdapter
{
    

    private static Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

    /**
     * Saves customer data to the database.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.CONTINUE;
        boolean noErrors = true;
        // get the customer to save to the database, don't save in training mode.
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        if (cargo.getRegister().getWorkstation().isTrainingMode() == false)
        {
            CustomerIfc customer = cargo.getCustomer();
            CustomerIfc originalCustomer = cargo.getOriginalCustomer();

            // attempt to do the database update
            try
            {
                StringBuilder jString = new StringBuilder();
                Object[] dataArgs = new Object[2];

                boolean isNewCustomer = cargo.isNewCustomer();
                CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
              
               
                
                if (isNewCustomer)
                {
                    customer.setCustomerID(customerManager.getNewCustomerID(cargo.getRegister()));
                    // Journal Customer information
                    dataArgs[0] = CustomerUtilities.getNewCustomerJournalString(customer);
                    jString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.ADD_CUSTOMER_LABEL, dataArgs));
                    letterName = "NewCustomerAdded";

                }
                else
                // This is an update.
                {
                    dataArgs[0] = customer.getCustomerID().trim();
                    jString.append(Util.EOL);
                    jString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.FIND_CUSTOMER_LABEL, dataArgs));
                   
                    if (originalCustomer != null)
                    {
                        jString.append(Util.EOL);
                        jString.append(CustomerUtilities.getChangedCustomerData(originalCustomer, customer));
                    }
                }

                
                customerManager.saveCustomer(customer);
                cargo.setCustomer(customer);

                // if new customer update customer sequence number
                if (cargo.isNewCustomer())
                {
                    customerManager.saveLastSequenceNumber(cargo.getRegister());
                }


                // get the Journal manager
                JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                if (jmi != null)
                {
                    if (jString.toString().length() > 0)
                    {
                        jmi.setEntryType(JournalableIfc.ENTRY_TYPE_CUST);
                        jmi.journal(cargo.getEmployeeID(), cargo.getTransactionID(), jString.toString());
                    }
                }
                else
                {
                    logger.error("No journal manager found!");
                }

            }
            catch (DataException e)
            {
                logger.error("Unable to save customer \"" + customer.getCustomerID() + "\".", e);

                // check for database connection error
                int errorCode = e.getErrorCode();
                noErrors = false;
                // cannot link if customer was not added to the database
                cargo.setLink(false);
                cargo.setDataExceptionErrorCode(errorCode);
                showErrorDialog(bus, errorCode);
            }

        }// end (isTrainingMode() == false)
        if (noErrors)
        {
          bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }


    /**
     * Show error screen
     *
     * @param bus the bus
     * @param error the error code
     */
    public void showErrorDialog(BusIfc bus, int error)
    {

        String msg[] = new String[2];
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        msg[0] = utility.getErrorCodeString(error);
        msg[1] = utility.retrieveDialogText("DATABASE_ERROR.Contact", DataManagerMsgIfc.CONTACT);

        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // set  model and display error msg
        UIUtilities.setDialogModel(ui,DialogScreensIfc.ERROR,"CustomerError",msg,CommonLetterIfc.CANCEL);
    }
}
