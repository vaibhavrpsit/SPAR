/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/DisplayOrderSummarySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/20/10 - updated search flow
 *    abondala  05/19/10 - update
 *    abondala  05/19/10 - search flow update
 *    abondala  05/19/10 - Display list of external orders flow
 *    abondala  05/17/10 - Siebel search flow
 *    acadar    05/03/10 - initial checkin for external order search
 *    acadar    05/03/10 - external order search initial check in
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import java.util.Date;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrder;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//------------------------------------------------------------------------------
/**

    Displays a list of external order summaries for user to select one.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayOrderSummarySite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = 8979095419161348587L;

    /**
       class name constant
    **/
    public static final String SITENAME = "DisplayOrderSummarySite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Displays a list of order summaries for user to select one.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        SearchOrderCargo        cargo   = (SearchOrderCargo) bus.getCargo();
        POSUIManagerIfc         ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel           model   = new ListBeanModel();

        
        NavigationButtonBeanModel globalNavigationModel = new NavigationButtonBeanModel();
        if (cargo.getExternalOrdersList().length < 1)
        {
            globalNavigationModel.setButtonEnabled(CommonActionsIfc.NEXT, false);
        }
        else
        {
            globalNavigationModel.setButtonEnabled(CommonActionsIfc.NEXT, true);
        }
        
        model.setGlobalButtonBeanModel(globalNavigationModel);
        
        // get order list data
        model.setListModel(cargo.getExternalOrdersList());

        //Displays Screen
        ui.showScreen(POSUIManagerIfc.EXTERNAL_ORDER_LIST, model);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------

    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  DisplayOrderSummarySite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------

    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    public ExternalOrderIfc[] getSampleExternalOrders()
    {
        ExternalOrderIfc temp[] = new ExternalOrderIfc[2];
        ExternalOrder order1 = new ExternalOrder();
        order1.setNumber("SIE_ORD1");
        order1.setCreationDate(new Date());
        order1.setAccount("222");
        order1.setLastName("BOND");
        order1.setTotal(DomainGateway.getBaseCurrencyInstance());
        ExternalOrder order2 = new ExternalOrder();
        order2.setNumber("SIE-ORD2");
        order2.setCreationDate(new Date());
        order2.setAccount("333");
        order2.setLastName("BOND2");
        order2.setTotal(DomainGateway.getBaseCurrencyInstance());
        
        temp[0] = order1;
        temp[1] = order2;
        
        return temp;
    }
    
}





