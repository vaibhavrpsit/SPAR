/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ItemCheckReturnLetterSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:36 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 15 2003 14:09:26   bwf
 * Set discount type based on letter sent.
 * Resolution for 2103: Remove uses of deprecated items in POS.
 * 
 *    Rev 1.0   Apr 29 2002 15:16:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:29:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
//--------------------------------------------------------------------------
/**
    This site Check if an override return letter was set and sends the 
    approprate letter
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ItemCheckReturnLetterSite extends PosSiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //--------------------------------------------------------------------------
    /**
     *   Constant for the Discount Amount button action. <P>
     */
    //--------------------------------------------------------------------------
    public static final String ACTION_DISCOUNT_AMOUNT  = "DiscountAmount";

    //----------------------------------------------------------------------
    /**
     *   Displays the ITEM_OPTIONS screen.
     *   <P>
     *   @param  bus     Service bus.
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        Letter   letter           = null;
        
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        letter = new Letter("default");

        // Return to the calling service
        if(cargo.getSecurityOverrideReturnLetter() != null)
        {
            letter = new Letter(cargo.getSecurityOverrideReturnLetter()); 
            if(letter.getName().equals(ACTION_DISCOUNT_AMOUNT))
            {
                cargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
            }
        }        
        bus.mail(letter, BusIfc.CURRENT);
    }
}
