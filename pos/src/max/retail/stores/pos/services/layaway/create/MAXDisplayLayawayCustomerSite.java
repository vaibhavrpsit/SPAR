/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   Rev 1.0	Izhar		29/05/2013		Customer
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.layaway.create; 

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.layaway.create.DisplayLayawayCustomerSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
 
//------------------------------------------------------------------------------ 
/** 
    Displays the layaway customer screen.
    <P>
    @version $Revision: 3$
**/ 
//------------------------------------------------------------------------------ 
public class MAXDisplayLayawayCustomerSite extends DisplayLayawayCustomerSite 
{ 
   
    //-------------------------------------------------------------------------- 
    public void arrive(BusIfc bus) 
    { 
        POSUIManagerIfc ui = 
                        (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =   (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE); 
        ParameterManagerIfc pm =   (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();                       
        
        CustomerIfc customer = layawayCargo.getCustomer();  
        MailBankCheckInfoBeanModel model = CustomerUtilities.copyCustomerToModel(customer,utility,pm);
        
 
        
        // tell MailBankCheckBean that this is a Layaway transaction
        model.setLayawayFlag(true);
        
        // set the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();

        String custScreenName = utility.retrieveText("StatusPanelSpec"
                                     ,BundleConstantsIfc.LAYAWAY_BUNDLE_NAME
                                     ,"LayawayCustomerScreenName"
                                     ,"Layaway Customer"
                                     ,LocaleConstantsIfc.USER_INTERFACE); 

        String busCustScreenName = utility.retrieveText("StatusPanelSpec"
                                     ,BundleConstantsIfc.LAYAWAY_BUNDLE_NAME
                                     ,"LayawayBusCustomerScreenName"
                                     ,"Layaway Business"
                                     ,LocaleConstantsIfc.USER_INTERFACE); 

        if (model.isBusinessCustomer())
        {
            statusModel.setScreenName(busCustScreenName);
        }
        else
        {
            statusModel.setScreenName(custScreenName);
        }

        statusModel.setCustomerName(model.getCustomerName());

        model.setStatusBeanModel(statusModel);
        /**MAX Rev 1.0 Change : Start**/
      //MAX Chanhges for TIC customer By Manpreet:Start
        //if(((MAXCustomer)customer).getCustomerType().equalsIgnoreCase("T"))
       // model.setEditableFields(false);
        //else
      //MAX Chanhges for TIC customer By Manpreet:End
        	 model.setEditableFields(true);
        /**MAX Rev 1.0 Change : Start**/
        ui.showScreen(POSUIManagerIfc.CUSTOMER_LAYAWAY, model);
    }
} 


