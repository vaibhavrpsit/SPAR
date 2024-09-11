/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/StartSaleSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:23 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/14 21:15:28  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   03 Feb 2004 12:32:22   awilliamson
 * fix for turning off training mode after tendering out
 * 
 *    Rev 1.1   Jan 07 2004 12:21:54   rrn
 * Added cargo.setPLUItem(null) to initialization.
 * Resolution for 3539: Item Not Found screen has fields filled in if after a transaction
 * 
 *    Rev 1.0   Nov 11 2003 18:02:36   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.1   08 Nov 2003 01:24:58   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:03:46   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site begins the Sale Package.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class StartSaleSite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       letter sent if configured as POS
    **/
    protected static final String POS_LETTER = "Pos";

    //----------------------------------------------------------------------
    /**
       Check if running as POS
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
          //get the cargo
        String letterName = POS_LETTER;
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        
        cargo.setPLUItem( null );           // make sure no leftover pluItem
 
        // initialize list of send items
        cargo.setLineItems(null);
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

}
