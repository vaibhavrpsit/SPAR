/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/SearchByCreditCardSite.java /main/3 2013/04/19 13:35:38 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* jswan       04/18/13 - Modified to make lookup by credit card work the same
*                        way for orders and transactions.
* yiqzhao     07/27/12 - modify order search flow and populate order cargo for
*                        searching
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.search;

//foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//------------------------------------------------------------------------------
/**
    Displays the ORDER_SEARCH_OPTIONS screen.  This site is used by
    the Pickup and Cancel Order services. Removes the customer name from 
    the screen.
    <P>
    @version $Revision: /main/3 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class SearchByCreditCardSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "SearchByCreditCardSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";

    //--------------------------------------------------------------------------
    /**
       Displays the Order Search Options screen and removes the customer name
       from the status area.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
    	Letter letter = new Letter("Enter");
    	
        String swipeCard = Gateway.getProperty("application", "ReturnByAccountNumberToken", "true");
        
        if ( Boolean.valueOf(swipeCard).booleanValue() )
        {
        	letter = new Letter("Swipe");
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}