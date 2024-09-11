/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/ExitLayawayConversionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:58 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:50:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway;

// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

// pos imports
//import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//------------------------------------------------------------------------------
/**
    Mails a Success letter.  This class is used to traverse from another
    letter (such as Yes or No or Ok or another UI-based letter) to a
    Success letter. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ExitLayawayConversionAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
        Mails a Success letter. <P>
        @param  bus as BusIfc
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter("ExitLayaway"), BusIfc.CURRENT);
    }
}
