/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/ChooseSpecialOrderOptionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:00  mcs
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
 *    Rev 1.0   Aug 29 2003 16:07:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:01:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:56   msg
 * Initial revision.
 * 
 *    Rev 1.3   Dec 04 2001 16:11:56   dfh
 * test
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.2   04 Dec 2001 15:56:58   msg
 * test
 * Resolution for 7: test scr
 * 
 *    Rev 1.1   04 Dec 2001 15:55:54   msg
 * test
 * Resolution for 260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 14:48:20   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the special order options screen.
    <P>       
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ChooseSpecialOrderOptionSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "ChooseSpecialOrderOptionSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Displays the specal order options screen.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel pModel = new POSBaseBeanModel();
        
        ui.showScreen(POSUIManagerIfc.SPECIAL_ORDER_OPTIONS, pModel);
    }
}
