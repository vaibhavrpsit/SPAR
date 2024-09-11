/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  3/4/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.addbusiness;

import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import max.retail.stores.pos.services.customer.common.MAXCustomerUtilities;
import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.addbusiness.AddBusinessInfoSite;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;


//--------------------------------------------------------------------------
/**
    Displays the Business Customer Info screen for input of business
    information during add business customer service. <p>
**/
//--------------------------------------------------------------------------
public class MAXAddBusinessInfoSite extends AddBusinessInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "";

    //----------------------------------------------------------------------
    /**
        Displays the Business Customer Info screen for input of business
        information during add business customer service. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
       CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        MAXUtilityManagerIfc utility = (MAXUtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        cargo.setHistoryMode(false);
      
       //<!-- MAX Rev 1.0 Change : Start -->
        MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel) getCustomerInfoBeanModel(bus); 
        model.setBusinessCustomer(true);       
        model.setEditableFields(true);
       
        if(cargo.getEmployee()!=null)
			model.setEmployeeID(cargo.getEmployee().getLoginID());
		if(cargo.getOperator()!=null)
            model.setEmployeeID(cargo.getOperator().getLoginID());
        model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_MOBILE);
        //model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
        //<!-- MAX Rev 1.0 Change : end -->
        model.setReasonCodes(MAXCustomerUtilities.getTaxExceptions(utility));
        model.setReasonCodeTags(MAXCustomerUtilities.getTaxExceptionsTags(utility));
        CodeListIfc rcl = utility.getReasonCodes("CORP",CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
        model.setDefaultValue(rcl.getDefaultOrEmptyString(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));

        cargo.setHistoryMode(false);

        // allow the customer to edit in add.
        // set the link done switch
        int linkOrDone = cargo.getLinkDoneSwitch();
        model.setLinkDoneSwitch(linkOrDone);

        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

        if (linkOrDone == CustomerMainCargo.LINKANDDONE)
        {
            //enable done
            nModel.setButtonEnabled(CommonLetterIfc.DONE, true);

            // enable link
            nModel.setButtonEnabled(CommonLetterIfc.LINK, true);
        }
        if (linkOrDone == CustomerMainCargo.LINK)
        {
            //disable done
            nModel.setButtonEnabled(CommonLetterIfc.DONE, false);

            // enable link
            nModel.setButtonEnabled(CommonLetterIfc.LINK, true);
        }
        if (linkOrDone == CustomerMainCargo.DONE)
        {
            //disable Link
            nModel.setButtonEnabled(CommonLetterIfc.LINK, false);

            // enable done
            nModel.setButtonEnabled(CommonLetterIfc.DONE, true);
        }

        model.setLocalButtonBeanModel(nModel);

        //Check if History button should be enabled
        nModel.setButtonEnabled(CustomerCargo.HISTORY, cargo.isHistoryModeEnabled());
        
        model.setLocalButtonBeanModel(nModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ADD_BUSINESS, model);
    }
    /**
    Initialzes the CustomerInfoBeanModel from the cargo. <p>
    @param  bus     Service Bus
    @return CustomerInfoBeanModel  the beanmodel
**/
//----------------------------------------------------------------------
public MAXCustomerInfoBeanModel getCustomerInfoBeanModel(BusIfc bus)
{

    CustomerCargo cargo = (CustomerCargo) bus.getCargo();
    CustomerIfc customer = cargo.getCustomer();
    UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
    ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
    
    
    // Enable display of History button
    cargo.setHistoryMode(true);
    
    // model to use for the UI
   //<!-- MAX Rev 1.0 Change : Start -->
    MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel) MAXCustomerUtilities.getCustomerInfo(customer, utility,pm);
//<!-- MAX Rev 1.0 Change : end-->
     // get available customer groups
    if (cargo.getCustomerGroups() == null)
    {
        CustomerGroupIfc[] groups = MAXCustomerUtilities.getCustomerGroups();
        // append retrieved array -- if it exists -- to "none"
        if (groups == null)
        {
            // use no-discount entry as array
            cargo.setCustomerGroups(cargo.getNoCustomerGroup());
            model.setCustomerGroups(cargo.getNoCustomerGroup());
            // set the customer group index to 0
            model.setSelectedCustomerGroupIndex(0);
            cargo.setSelectedCustomerGroup(0);
        }
        else
        {
            
            CustomerGroupIfc[] useArray = new CustomerGroupIfc[groups.length + 1];
            System.arraycopy(cargo.getNoCustomerGroup(), 0, useArray, 0, 1);
            System.arraycopy(groups, 0, useArray, 1, groups.length);
            cargo.setCustomerGroups(useArray);
            model.setCustomerGroups(useArray);

            // set index based on existing discount
            int index = 0;
            groups = customer.getCustomerGroups();
            if (groups != null && groups.length > 0)
            {
                // find match in used array
                for (int i = 1; i < useArray.length; i++)
                {
                    // once customer's discount group is
                    // found in array, set index and end search
                    if (useArray[i].getGroupID().equals(groups[0].getGroupID()))
                    {
                        index = i;
                        i = useArray.length;
                    }
                }
            }
            model.setSelectedCustomerGroupIndex(index);
            cargo.setSelectedCustomerGroup(index);
        }
    }
    else
    {
         model.setCustomerGroups(cargo.getCustomerGroups());
        // set the customer group index to 0
         model.setSelectedCustomerGroupIndex(cargo.getSelectedCustomerGroup());
    }
     return(model);
}
}
