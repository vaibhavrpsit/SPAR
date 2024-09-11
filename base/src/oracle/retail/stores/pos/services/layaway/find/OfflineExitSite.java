/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/OfflineExitSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:48 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find; 
 
//foundation imports 
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
 
//------------------------------------------------------------------------------ 
/** 
    Displays the menu screen for selecting layaway options.
    
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/ 
//------------------------------------------------------------------------------ 
 
public class OfflineExitSite extends PosSiteActionAdapter 
{ 
    /** 
        class name constant 
    **/ 
    public static final String SITENAME = "OfflineExitSite"; 
 
    /** 
        revision number for this class 
    **/ 
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $"; 
 
    //--------------------------------------------------------------------------
    /** 
            Mails a letter indicating whether a transaction id has been burned.
            <P> 
            @param bus the bus arriving at this site 
    **/ 
    //--------------------------------------------------------------------------
 
    public void arrive(BusIfc bus) 
    { 
        Letter result = new Letter("ExitLayaway"); // default value
                    
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();
        if (cargo.getSeedLayawayTransaction() != null)
        {
            result = new Letter (CommonLetterIfc.CANCEL);
        }
        bus.mail(result, BusIfc.CURRENT);   
    }
  
} 
