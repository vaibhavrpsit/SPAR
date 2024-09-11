/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/IsThereAnotherItemSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:35 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/23 14:58:53  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   26 Jan 2004 00:14:00   baa
 * continue return development
 * 
 *    Rev 1.0   Aug 29 2003 16:06:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:04:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:38   msg
 * Initial revision.
 * 
 *    Rev 1.2   22 Feb 2002 18:02:46   cir
 * Remove check for non returnable gift card
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 * 
 *    Rev 1.1   20 Feb 2002 16:32:02   cir
 * Check for non returnable gift card
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 *
 *    Rev 1.0   Sep 21 2001 11:25:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
     This signal returns true if is another item from the transaction to
     processe.
     <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsThereAnotherItemSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7593598022590462289L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed

       @param bus the bus trying to proceed
       @return true if not cashdrawer; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {

        // Get the current item index
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        int nextItem = cargo.getCurrentItem();

        // If we are undoing, decrement the index
        String letterName = bus.getCurrentLetter().getName();
        if (letterName.equals(CommonLetterIfc.UNDO))
        {
            nextItem--;
        }
        else
        {
            nextItem++;
        }

        // Test to see if the next index will be in range
        int itemListSize = 0;
        if (cargo.areTransactionDetailsDisplayed())
        {
            itemListSize = cargo.getOriginalSaleLineItems().length;
        }
        else
        {
            itemListSize = cargo.getLineItemsToDisplay().length;
        }
        boolean anotherItem = true;
        if (nextItem < 0 || nextItem > (itemListSize - 1))
        {
            anotherItem = false;
        }


        return anotherItem;
    }

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

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = new String("Class:  " + getClass().getName() + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }                                   // end toString()

}
