/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckForTrainingModeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - deprecating this class.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/07/23 22:17:26  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.2  2004/04/13 15:23:47  tfritz
 *   @scr 3884 - More training mode changes
 *
 *   Revision 1.1  2004/04/07 17:50:55  tfritz
 *   @scr 3884 - Training Mode rework
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

//import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
//import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    Check to see if the register is in training mode.  If so, switch
    the normal register to the training mode register.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated As of 14.1, this site is not used.
**/
//--------------------------------------------------------------------------
@Deprecated
public class CheckForTrainingModeSite extends PosSiteActionAdapter
{
    /**
     revision number
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
     * Check to see if training mode is on.  If so, set the register
     * to the training register.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.CONTINUE); 
        
        bus.mail(letter, BusIfc.CURRENT);                
    }
}
