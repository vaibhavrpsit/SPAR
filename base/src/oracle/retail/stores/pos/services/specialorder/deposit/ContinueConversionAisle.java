/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/deposit/ContinueConversionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.2  2004/02/12 16:52:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:07:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:01:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Dec 04 2001 16:08:56   dfh
 * test
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 15:11:18   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.deposit;

// foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Changes letter to ExitSpecialOrder 
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ContinueConversionAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    // Class name
    public static final String LANENAME = "ContinueConversionAisle";

    //----------------------------------------------------------------------
    /**
       Changes letter from Continue to ExitSpecialOrder.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
         bus.mail(new Letter("ExitSpecialOrder"), BusIfc.CURRENT);        
    }
}
