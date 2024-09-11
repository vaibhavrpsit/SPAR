/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/capturecustomerinfo/CaptureCustomerInfoSite.java /main/28 2014/01/09 16:23:22 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/09/14 - fix null dereferences
 *    mjwallac  12/11/13 - fix null dereferences
 *    abondala  09/04/13 - initialize collections
 *    abondala  01/14/13 - fix the issue of return transaction linked with a
 *                         business custome
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    jswan     10/25/12 - Modified to support returns by order.
 *    cgreene   04/03/12 - removed deprecated methods
 *    asinton   03/26/12 - Customer UI changes to accomodate multiple
 *                         addresses.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  05/27/09 - Use mail bank check reason codes for store credits
 *    sbeesnal  04/27/09 - Modified the code to retrieve reason codes with
 *                         respective codeListType for mail bank check tender
 *                         type.
 *    mahising  04/02/09 - Fixed business customer issue if tender done by mail
 *                         bank check for refund option
 *    sbeesnal  04/02/09 - Incase of SaleReturnTransaction, modified the code
 *                         to pre-populate the customer info (both business &
 *                         non-business customers)
 *    mahising  02/21/09 - Fixed phonetype issue for mailcheck tender
 *    mahising  02/19/09 - Resolved Bug Id:2211
 *    npoola    02/11/09 - fix issue for send customer info
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes

     $Log:
      4    360Commerce 1.3         8/1/2006 5:33:37 PM    Brett J. Larsen CR
           6131 - added phone type list initialization
      3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse
     $

      4    .v7x      1.2.1.0     4/28/2006 7:28:33 AM   Dinesh Gautam   CR
           6131: Phone Type Values text updated

     Revision 1.32  2004/08/23 16:15:58  cdb
     @scr 4204 Removed tab characters

     Revision 1.31  2004/07/21 02:10:22  aschenk
     @scr 6041 - ID type is now prepopulated on the customer Info screen.

     Revision 1.30  2004/07/07 22:13:53  khassen
     @scr 5464 - fixed check for tender type.

     Revision 1.29  2004/06/23 00:46:36  blj
     @scr 5113 - added nullpointer checking

     Revision 1.27  2004/06/21 14:22:41  khassen
     @scr 5684 - Feature enhancements for capture customer use case: customer/capturecustomer accomodation.

     Revision 1.26  2004/06/18 12:12:26  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.25  2004/05/28 20:11:31  lzhao
     @scr 4670: ignore NegativeAmountOnly parameter for send.

     Revision 1.24  2004/05/27 20:00:45  khassen
     @scr 0 - Removed commented-out code, fixed parameter check for NegativeAmtDue.

     Revision 1.23  2004/05/26 16:37:47  lzhao
     @scr 4670: add capture customer and bill addr. same as shipping for send

     Revision 1.22  2004/05/06 16:26:57  aschenk
     @scr 4647 - The Country and State/Region data fields both contained a value of "Other" which was removed when collecting Customer information for Mail Check tenders.

     Revision 1.21  2004/03/16 18:30:42  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.20  2004/03/05 19:28:03  khassen
     @scr 0 Capture Customer Info use-case - req modifications

     Revision 1.19  2004/03/02 04:27:06  khassen
     @scr 0 Capture Customer Info use-case - Modifications to tour script and sites.  Added verification for postal code.

     Revision 1.18  2004/02/27 21:34:07  khassen
     @scr 0 Capture Customer Info use-case - code clean-up and post-review modifications

     Revision 1.17  2004/02/27 21:08:23  khassen
     @scr 0 Capture Customer Info use-case - code clean-up and post-review modifications

     Revision 1.16  2004/02/27 20:17:21  bwf
     @scr 0 Organize imports.

     Revision 1.15  2004/02/27 19:23:02  khassen
     @scr 0 Capture Customer Info use-case


 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */
package oracle.retail.stores.pos.services.tender.capturecustomerinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.tender.tdo.CaptureCustomerInfoTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;

/**
 * Used for the capture customer info use-case. Displays a screen that will ask
 * for customer information. Capture Customer Info use case. This use case
 * capture one-time customer information for a transaction. This use case should
 * only be executed when the "360Customer" parameter is set to "N".
 * <p>
 * **NOTE** : The shuttle coming to this service should load the
 * CaptureCustomerInfoCargo object with 2 things:
 * <ul>
 * <li>1. the transaction object.
 * <li>2. the tender type.
 * </ul>
 * 
 */
public class CaptureCustomerInfoSite extends PosSiteActionAdapter
{
    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 9145736589209726721L;

    public static final String SITENAME = "CaptureCustomerInfoSite";

    public void arrive(BusIfc bus)
    {
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        CustomerInfoBeanModel custModel = null;
        TransactionIfc trans = cargo.getTransaction();
        CaptureCustomerIfc customer = null;

        if (cargo.getCustomer() != null)
        {
            customer = cargo.getCustomer();

            if (trans != null)
            {
                if (trans.getCustomerInfo() != null)
                {
                    customer.setPersonalIDType(trans.getCustomerInfo().getLocalizedPersonalIDType());
                }
            }
        }
        else
        {
            if (trans != null)
            {
                if (trans.getCaptureCustomer() != null)
                {
                    cargo.setCustomer(trans.getCaptureCustomer());
                }
            }
        }

        CustomerInfoBeanModel customerInfoBeanModel = new CustomerInfoBeanModel();
        custModel = (CustomerInfoBeanModel)CustomerUtilities.populateCustomerInfoBeanModel(customer, utility, pm, customerInfoBeanModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        TDOUIIfc tdo = null;

        // Create the tdo object.
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.CaptureCustomerInfo");
        }
        catch (TDOException tdoe)
        {
            tdoe.printStackTrace();
        }

        // Load the CodeList
        String storeID = Gateway.getProperty("application", "StoreID", "");

        CodeListIfc codeList = null;
        if (cargo.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK
                || cargo.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT) // Store credits use mail bank reason codes
        {
        	codeList = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_MAIL_BANK_CHECK_ID_TYPES);
        }
        else
        {
        	codeList = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES);
        }
        cargo.setPersonalIDTypes(codeList);

        HashMap<String, Object> map = new HashMap<String, Object>(1);
        map.put("Bus", bus);

        // Check to see if there is already a model in the cargo. If so, that
        // model
        // takes precedence. If no model exists on the cargo, then we create a
        // new model
        // and prefill it with any available customer data.
        // The reason we check the cargo for a model is to take care of the case
        // when we
        // return from the ValidateCustomerInfoSite with a "Retry" letter.

        CaptureCustomerInfoBeanModel model = null;

        if (cargo.getModel() != null)
        {
            model = cargo.getModel();
        }
        else
        {
            if (tdo != null)
            {
                model = (CaptureCustomerInfoBeanModel)((CaptureCustomerInfoTDO)tdo).buildBeanModel(map);
                model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));

                // Transfer any customer information into the model.
                ((CaptureCustomerInfoTDO)tdo).customerToModel(model, bus, cargo.getCustomer());
            }
        }
        if (model != null && custModel != null && custModel.getPhoneList() != null)
        {
            model.setPhoneList(custModel.getPhoneList());
        }
        // Determine the screen type based on the transaction/tender
        // type that was passed to it.
        if (cargo.getTenderType() == TransactionConstantsIfc.TYPE_SEND)
        {
            if (cargo.getTransaction() instanceof SaleReturnTransaction)
            {
                SaleReturnTransaction saleReturnTransaction = (SaleReturnTransaction)cargo.getTransaction();
                if (saleReturnTransaction.getCustomer() != null)
                {
                    if (model != null && saleReturnTransaction.getCustomer().isBusinessCustomer())
                    {
                        String companyName = saleReturnTransaction.getCustomer().getCompanyName();
                        model.setOrgName(companyName);
                        model.setBusinessCustomer(true);
                    }
                }

            }
            cargo.setScreenType(POSUIManagerIfc.CAPTURE_CUSTOMER_INFO_SEND);
        }
        else if (cargo.getTenderType() == TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK)
        {
            if (cargo.getTransaction() instanceof SaleReturnTransaction)
            {
                SaleReturnTransaction saleReturnTransaction = (SaleReturnTransaction)cargo.getTransaction();
                if (saleReturnTransaction.getCustomer() != null)
                {
                    CustomerIfc saleReturnCustomer = saleReturnTransaction.getCustomer();
                    model = populateCustomerDetails(model, saleReturnCustomer, utility, pm);
                }
            }
            if (model != null)
            {
                model.setMailCheck(true);
            }
            cargo.setScreenType(POSUIManagerIfc.CAPTURE_CUSTOMER_INFO_BANK_CHECK);
        }
        else
        {
            if (cargo.getTransaction() instanceof SaleReturnTransaction)
            {
                SaleReturnTransaction saleReturnTransaction = (SaleReturnTransaction)cargo.getTransaction();
                if (saleReturnTransaction.getCustomer() != null)
                {
                    CustomerIfc saleReturnCustomer = saleReturnTransaction.getCustomer();
                    model = populateCustomerDetails(model, saleReturnCustomer, utility, pm);
                }
            }

            cargo.setScreenType(POSUIManagerIfc.CAPTURE_CUSTOMER_INFO_DEFAULT);
        }

        // Display the screen.
        ui.showScreen(cargo.getScreenType(), model);
    }

    private CaptureCustomerInfoBeanModel populateCustomerDetails(CaptureCustomerInfoBeanModel model,
            CustomerIfc customer, UtilityManagerIfc utility, ParameterManagerIfc pm)
    {
        int index = 0;
        int countryIndex = 0;
        PhoneIfc phone = null;
        AddressIfc address = null;

        if (customer.isBusinessCustomer())
        {
            String companyName = customer.getLastName();
            model.setOrgName(companyName);
            model.setLastName(companyName);
            model.setBusinessCustomer(true);
        }
        else
        {
            model.setFirstName(customer.getFirstName());
            model.setLastName(customer.getLastName());
            model.setBusinessCustomer(false);
        }

        // set the address in the model
        List<AddressIfc> addressVector = customer.getAddressList();

        if (!addressVector.isEmpty())
        {
            // look for the first available address
            while (address == null && index < AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR.length)
            {
                address = customer.getAddressByType(index);
                index++;
            }

            if (address != null)
            {
                Vector<String> lines = address.getLines();
                if (lines.size() >= 1)
                {
                    model.setAddressLine1(lines.get(0));
                }

                if (lines.size() >= 2 && lines.get(1) != null)
                {
                    model.setAddressLine2(lines.get(1));
                }

                // get list of all available states and selected country and
                // state
                countryIndex = CustomerUtilities.getCountryIndex(address.getCountry(), utility, pm);
                model.setCountryIndex(countryIndex);

                if (Util.isEmpty(address.getState()))
                {
                    model.setStateIndex(-1);
                }
                else
                {
                    model.setStateIndex(utility.getStateIndex(countryIndex, address.getState(), pm));
                }

                model.setCity(address.getCity());
                model.setPostalCode(address.getPostalCode());
            }
        }
        else
        {
            // if the address vector is empty, set the state and the country
            // to the store's state and country from parameters
            String storeState = CustomerUtilities.getStoreState(pm);
            String storeCountry = CustomerUtilities.getStoreCountry(pm);

            countryIndex = utility.getCountryIndex(storeCountry, pm);
            model.setCountryIndex(countryIndex);
            model.setStateIndex(utility.getStateIndex(countryIndex, storeState.substring(3, storeState.length()), pm));
        }

        // get customer phone list
        for (int i = PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length - 1; i >= 0; i--)
        {
            phone = customer.getPhoneByType(i);
            if (phone != null)
            {
                model.setPhoneNumber(phone.getPhoneNumber(), phone.getPhoneType());
                model.setPhoneType(phone.getPhoneType());
            }
        }

        return model;
    }
}
