/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.specialorder.add;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
//------------------------------------------------------------------------------
/**
    Displays the special order customer screen.
    <P>
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXDisplaySpecialOrderCustomerSite extends PosSiteActionAdapter

{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5450343224560220115L;
	/**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //--------------------------------------------------------------------------
    /**
        Displays the special order customer screen using customer data. If
        the customer does not have an address, then use the state and country
        data obtained from parameter values.
        <P>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =   (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =   (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();

        CustomerIfc customer = specialOrderCargo.getCustomer();
        MailBankCheckInfoBeanModel model = CustomerUtilities.copyCustomerToModel(customer,utility,pm);
           
        //model.setContactInfoOnly(true);
  
        // tell MailBankCheckBean that this is a special order or layaway transaction
        model.setLayawayFlag(true);

        // set the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        
        statusModel.setCustomerName(customer.getFirstLastName());
           
        if(customer instanceof MAXCustomerIfc)
        {
        	if(((MAXCustomerIfc)customer).getCustomerType().equals("T"))
        		model.setEditableFields(false);
        			
        }
      
        model.setStatusBeanModel(statusModel);

        // display the special order customer screen
        ui.showScreen(POSUIManagerIfc.CUSTOMER_SPECIAL_ORDER, model);
    }
    

    
}
