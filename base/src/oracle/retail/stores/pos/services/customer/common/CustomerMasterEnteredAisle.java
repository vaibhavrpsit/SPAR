/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerMasterEnteredAisle.java /main/14 2012/11/23 12:51:57 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *    acadar    05/31/12 - fixes for Xchannel
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    asinton   03/26/12 - Customer UI changes to accomodate multiple
 *                         addresses.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:55:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 27 2003 08:48:02   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.2   Mar 20 2003 18:18:44   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.1   Aug 07 2002 19:33:56   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:33:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:26   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:12   msg
 * Initial revision.
 * 
 *    Rev 1.3   09 Jan 2002 14:27:36   baa
 * update preferred customer status
 * Resolution for POS SCR-412: Adding a new customer to db w/PCD does not save PCD entry
 *
 *    Rev 1.2   16 Nov 2001 10:32:04   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   23 Oct 2001 16:52:58   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:14:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.useraccess.AbstractUserAccessAisle;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;

/**
 * Aisle that is traversed when the user presses Next at the CustomerMaster
 * site.
 */
public class CustomerMasterEnteredAisle extends AbstractUserAccessAisle
{
    private static final long serialVersionUID = -6115726874227431152L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Saves the customer information in the cargo and mails a Continue letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        String letterName = CommonLetterIfc.CONTINUE;

        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        // get the model for the bean
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.CUSTOMER_DETAILS);

        // get the customer object from the cargo
        // and set the customer attributes
        CustomerIfc customer = cargo.getCustomer();
        
          
        customer.setBirthdate(model.getBirthMonthAndDay());
        customer.setYearOfBirth(model.getBirthYear());
        
        // use to save customer's full name
        customer.setCustomerName(model.getCustomerName());
        customer.setSalutation(model.getSalutation());        
        customer.setGenderCode(model.getGenderIndex());
        
        customer.setEMailPrivacy(model.getEmailPrivacy());        
        customer.setMailPrivacy(model.getMailPrivacy());
        customer.setTelephonePrivacy(model.getTelephonePrivacy());
        customer.setPreferredLocale(LocaleMap.getSupportedLocales()[model.getSelectedLanguage()]);
        customer.setReceiptPreference(model.getSelectedReceiptMode());
        customer.setEmployeeID(model.getEmployeeID());

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

  }
