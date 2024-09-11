/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/ProcessCustomerActionSite.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *      5    360Commerce 1.4         6/12/2008 4:40:36 AM   Naveen Ganesh   If
 *           customer information is not linked to the transaction, link it
 *      4    360Commerce 1.3         6/11/2008 5:45:52 AM   Manikandan
 *           Chellapan CR#32012 Moved customerlink method call to
 *           SendCustomerReturnShuttle
 *      3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *      2    360Commerce 1.1         3/10/2005 10:24:25 AM  Robert Pearse   
 *      1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse   
 *     $
 *     Revision 1.7  2004/06/21 13:13:55  lzhao
 *     @scr 4670: cleanup
 *
 *     Revision 1.6  2004/05/26 16:37:47  lzhao
 *     @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 *     Revision 1.5  2004/02/16 14:48:03  blj
 *     @scr -3838 cleanup code
 *
 *     Revision 1.3  2004/02/13 21:10:51  epd
 *     @scr 0
 *     Refactoring to the Send Application Manager
 *
 *     Revision 1.2  2004/02/12 21:54:19  epd
 *     @scr 0
 *     Refactors for Send tour
 *
 *     Revision 1.1  2004/02/12 21:36:28  epd
 *     @scr 0
 *     These files comprise all new/modified files that make up the refactored send service
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * @author epd
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ProcessCustomerActionSite extends PosSiteActionAdapter
{

	private static final long serialVersionUID = -5303194731167627575L;	
    /*
     * letter for asking shipping address is same as billing address
     */
    public final static String CHECK_SAME_AS_BILLING_LETTER = "CheckSameAddress";
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

        bus.mail( new Letter(CHECK_SAME_AS_BILLING_LETTER), BusIfc.CURRENT);
    }
}
