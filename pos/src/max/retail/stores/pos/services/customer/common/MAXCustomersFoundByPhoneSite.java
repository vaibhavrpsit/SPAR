package max.retail.stores.pos.services.customer.common;

import java.util.List;
import java.util.Vector;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCustomersFoundByPhoneSite extends PosSiteActionAdapter
{
   
	private static final long serialVersionUID = 1L;
	/**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 1.1 $";


    //----------------------------------------------------------------------
    /**
        Checks the number of customers found and does the appropriate action.
        <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        MAXCustomerCargo cargo = (MAXCustomerCargo) bus.getCargo();
        boolean isLocalCustomerEnabled = false;
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
           isLocalCustomerEnabled = pm.getBooleanValue("IsLocalSearchCustomerEnabled").booleanValue();
        }
        catch (ParameterException e)
        {
          logger.warn("IsLocalCustomerEnabled parameter does not exist");
          e.printStackTrace();
        }
        List<CustomerIfc> cusList = cargo.getCustomerList();
        // grab the customers returned
        // changes start for code merging(updated to match 14.1 version as per MAX)
        //CustomerIfc[] customerList = cargo.getCustomerList();
        
        // Changes ends for code merging
        if(cusList!=null)
        {
        if(isLocalCustomerEnabled)
        {
        	List<CustomerIfc> cList = cargo.getCustomerList();
            CustomerIfc[] customerList = cList.toArray(new CustomerIfc[cList.size()]);
            System.out.println("60 :"+customerList);
        if (customerList.length == 1)
        {
            cargo.setCustomer(customerList[0]);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {

            // setup model to display data using new UI format
            DualListBeanModel model = new DualListBeanModel();
            Vector topList = new Vector();

            if(cargo.getOriginalCustomer() != null)
            {
                topList.addElement(cargo.getOriginalCustomer());
            }
            else
            {
                topList.addElement(customerList[0]);
            }

            model.setTopListModel(topList);
            // Changes start for code merging(commenting below line as per MAX )
           // model.setListModel(cargo.getCustomerListVector());
            model.setListModel(cargo.getCustomerList());
            
            // Changes ends for code merging
            NavigationButtonBeanModel navigationBean=new NavigationButtonBeanModel();
            
            navigationBean.setButtonEnabled("Clear", false);
            navigationBean.setButtonEnabled("Undo", false);
            navigationBean.setButtonEnabled("Cancel", false);
            
            model.setGlobalButtonBeanModel(navigationBean);
            
            PromptAndResponseModel promptModel=new PromptAndResponseModel();
            
        	String promptMessg = Gateway.getProperty("customerText", "TICCustomer.List.Prompt", "Select the Customer");
            promptModel.setPromptText(promptMessg);
            model.setPromptAndResponseModel(promptModel);
            
            
            // Display the screen
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(cargo.getScreen(), model);
        }
    }}
    else{
    	bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    }
}
