/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.send;

import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


public class MAXProcessCustomerActionSite extends PosSiteActionAdapter
{

	private static final long serialVersionUID = -5303194731167627575L;	
    /*
     * letter for asking shipping address is same as billing address
     */
    public final static String CHECK_SAME_AS_BILLING_LETTER = "CheckSameAddress";
    public final static String SEND_METHOD = "sendMethod";
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        
        // If customer information is not linked to the transaction, link it
        if (cargo.getTransaction().getCustomer()==null)
        {
	        SendManagerIfc sendMgr = null;
	        try
	        {
	            sendMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
	        }
	        catch (ManagerException e)
	        {
	            // default to product version
	            sendMgr = new SendManager();
	        }
	        // link the customer
            JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
	        sendMgr.linkCustomer(journal, cargo);
        }
        
        // Update UI
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        if (cargo.getTransaction().getCustomer().getFirstName() != null &&
            cargo.getTransaction().getCustomer().getLastName() != null)
        {
            String[] vars = { cargo.getTransaction().getCustomer().getFirstName(),
                    cargo.getTransaction().getCustomer().getLastName() };
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String pattern = utility.retrieveText("CustomerAddressSpec",
                    BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    TagConstantsIfc.CUSTOMER_NAME_TAG,
                    TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
            String name = LocaleUtilities.formatComplexMessage(pattern,vars);
            ui.customerNameChanged(name);
        }
        else
        {
            ui.customerNameChanged(cargo.getTransaction().getCustomer().getCustomerID());
        }

      //Change for Rev 1.0 :Starts
        if(!((cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc) && ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isGstEnable()))
            bus.mail( new Letter(CHECK_SAME_AS_BILLING_LETTER), BusIfc.CURRENT);
        else
        	bus.mail( new Letter(SEND_METHOD), BusIfc.CURRENT);
      //Change for Rev 1.0 :Ends
    }
}
