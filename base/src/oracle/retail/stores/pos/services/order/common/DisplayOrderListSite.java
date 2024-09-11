/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/DisplayOrderListSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:33 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/22 00:06:35  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:12:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Feb 2002 18:14:32   baa
 * more ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 24 2001 13:00:14   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**

    Displays a list of order summaries for user to select one.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayOrderListSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "DisplayOrderListSite";

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

        OrderCargo              cargo   = (OrderCargo) bus.getCargo();
        POSUIManagerIfc         ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel           model   = new ListBeanModel();
        StatusBeanModel         sbModel = new StatusBeanModel();
        PromptAndResponseModel  prModel = new PromptAndResponseModel();

        // get order list data
        model.setListModel(cargo.getOrderSummaries());

        //StatusBeanModel Settings Configured
        sbModel.setCustomerName(" ") ;


        //PromptAndResponseModel Settings Configured
        //model.setPromptArgument(cargo.getServiceName());
        prModel.setArguments(cargo.getServiceName());

        //OrderListBeanModel Configured
        model.setStatusBeanModel(sbModel);
        model.setPromptAndResponseModel(prModel);

        //Displays Screen
        ui.showScreen(POSUIManagerIfc.ORDER_LIST, model);

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
        String strResult = new String("Class:  DisplayOrderListSite (Revision " +
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

}




