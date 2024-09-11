/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/CustomerSearchOptionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/16 14:41:24  blj
 *   @scr 3838 - cleanup code
 *
 *   Revision 1.3  2004/02/12 16:49:32  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   04 Sep 2002 08:57:38   djefferson
 * added support for Business Customer
 * Resolution for POS SCR-1605: Business Customer
 * 
 *    Rev 1.0   Apr 29 2002 15:32:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:44   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   23 Oct 2001 16:53:40   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:15:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerSelectBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
//--------------------------------------------------------------------------
/**
    This site is the starting site for the CustomerLookup service.
    This site allows the user to select the type of search to perform.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerSearchOptionSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Prompts the user for the type of customer search. <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
                // Set the screen ID and bean type
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        CustomerSelectBeanModel  model = new CustomerSelectBeanModel();

        
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

        if (cargo.isAddCustomerEnabled())
        {
            nModel.setButtonEnabled(CustomerCargo.EMPID, true);
            nModel.setButtonEnabled(CustomerCargo.CUSTINFO, true);
        }
        else
        {
            nModel.setButtonEnabled(CustomerCargo.EMPID, false);
            nModel.setButtonEnabled(CustomerCargo.CUSTINFO, false);
        }
        
        if (cargo.isAddBusinessEnabled())
        {
            nModel.setButtonEnabled(CustomerCargo.BUSINFO, true);
        }
        else
        {
            nModel.setButtonEnabled(CustomerCargo.BUSINFO, false);
        }

        model.setLocalButtonBeanModel(nModel);

        ui.showScreen(POSUIManagerIfc.CUSTOMER_SEARCH_OPTIONS, model);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
