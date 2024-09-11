/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/ConvertLetterToExitTenderAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:49 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:12 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/04/13 21:07:36  bwf
 *   @scr 4263 Decomposition of check.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
     This method mails an exittender letter so that it can exit
     the service correctly.
     $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class ConvertLetterToExitTenderAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -642420717550714801L;

    public static final String LANENAME = "ConvertLetterToExitTendersAisle";

    //----------------------------------------------------------------------
    /**
        Mail an exitTender letter instead of the other so that it can exit the service
        correctly.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.LaneActionIfc#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter("ExitTender"), BusIfc.CURRENT);
    }    
}
