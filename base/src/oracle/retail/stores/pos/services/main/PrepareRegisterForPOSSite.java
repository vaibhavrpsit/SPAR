/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/PrepareRegisterForPOSSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:21 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.1  2004/04/07 17:50:56  tfritz
 *   @scr 3884 - Training Mode rework
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site checks to see if training mode is on and then makes sure
    the register's training mode is turned off before calling the
    Setup Register service.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class PrepareRegisterForPOSSite extends PosSiteActionAdapter
{
    /**
     revision number
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
     * Check to see if training mode is on.  If so, temporarily turn off
     * the workstation's training mode flag so the setup register service
     * does not take the Training Mode path and setup this register
     * as the training mode register.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        MainCargo cargo = (MainCargo) bus.getCargo();
        
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);                
    }
}
