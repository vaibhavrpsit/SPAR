/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/DisplayReturnOptionsSite.java /rgbustores_13.4x_generic_branch/2 2011/10/07 12:02:35 rsnayak Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rsnayak   10/05/11 - ReEntry Mode Search button fix
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/11/10 - Pre code reveiw clean up.
 *    jswan     05/11/10 - Returns flow refactor: Remove and consolidated
 *                         obsolete code; modified to support new response
 *                         field.
 *    mkutiana  03/09/10 - Clearing out the objects in the event of user
 *                         escaping from tour after retrieving recepted return
 *    mkutiana  02/17/10 - change to previous checkin
 *    mkutiana  02/11/10 - cargo is not bieng cleaned up correctly
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:10:58 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/14/2005 17:29:00    Deepanshu       CR
 *         4000: Reset the search criteria to null when the ReturnItemCargo is
 *         reset.
 *    3    360Commerce1.2         3/31/2005 15:27:49     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:05     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:40     Robert Pearse
 *
 *   Revision 1.5  2004/06/24 21:34:46  mweis
 *   @scr 5792 Return of item w/out receipt, no wait as a gift receipt, no wait w/out receipt crashes app
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:06:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:04:50   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:14   msg
 * Initial revision.
 *
 *    Rev 1.1   17 Jan 2002 17:37:18   baa
 * update roles/security model
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.0   Sep 21 2001 11:25:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
//------------------------------------------------------------------------------
/**
    This site displays the options available from Return Options screen.
**/
//--------------------------------------------------------------------------
public class DisplayReturnOptionsSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 5671113859954192990L;
    
    /**  site name constant  **/
    public static final String SITENAME = "DisplayReturnOptionsSite";

    //--------------------------------------------------------------------------
    /**
       Displays return options menu.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Reinitialize the cargo.
    	ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
    	boolean isReEntryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
    	boolean enableSearchButton  = true;
        
    	if(isReEntryMode)
    	    enableSearchButton = false;
    	
    	cargo.setGiftReceiptSelected(false);
        cargo.setHaveReceipt(false); 
        cargo.setOriginalTransaction(null);
        cargo.setOriginalTransactionId(null);
        cargo.setOriginalTenders(null); 
        cargo.setSearchCriteria(null);
                
        // get ui reference and display screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel prompt = new PromptAndResponseModel();
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        nModel.setButtonEnabled(CommonActionsIfc.SEARCH, enableSearchButton);
        nModel.setButtonEnabled(CommonActionsIfc.GIFT_RECEIPT, true);
        nModel.setButtonEnabled(CommonActionsIfc.NO_RECEIPT, true);
        beanModel.setPromptAndResponseModel(prompt);
        beanModel.setLocalButtonBeanModel(nModel);
        ui.showScreen(POSUIManagerIfc.RETURN_OPTIONS, beanModel);
    }
}
