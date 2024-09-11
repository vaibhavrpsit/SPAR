/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/StoreReasonCodeLevelAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:30 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:11:00  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/12 18:49:35  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:39  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 19 2003 15:25:08   mrm
 * Handle only store level reason codes
 * Resolution for POS SCR-3172: Remove the Reason Code Level screen - not in req, remove Corp level Reason Codes
 *
 *    Rev 1.1   Jun 27 2003 13:48:14   bwf
 * Internationalize codeListMapForUpdate when retrieving.
 * Resolution for 2269: Tags instead of names displaying in the Reason Code List Screen
 *
 *    Rev 1.0   Apr 29 2002 15:38:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:06:34   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:38   msg
 * Initial revision.
 *
 *    Rev 1.3   25 Jan 2002 21:02:08   baa
 * partial fix ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.2   Jan 23 2002 09:09:42   mpm
 * Corrected security fault.
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:12:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;

// Java imports
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.CodeListManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Store the location level indicated by the UI in the cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreReasonCodeLevelAisle extends PosLaneActionAdapter
{
    /**
     * Generated SerialVersion UID
     */
    private static final long serialVersionUID = 5734453559028541773L;

    //--------------------------------------------------------------------------
    /**
       Stores the location level retrieved from the UI.
       <p>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        LetterIfc letter = new Letter(ReasonCodesCommon.ACCEPT_DATA);
        ReasonCodeCargo cargo = (ReasonCodeCargo)bus.getCargo();
        cargo.setLocationToModify(cargo.getStore());
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        CodeListSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeListSearchCriteriaInstance();
        criteria.setSearchType(CodeListSearchCriteriaIfc.SEARCH_CODE_LIST_ID);
        criteria.setStoreID(cargo.getOperator().getStoreID());
        criteria.setLocaleRequestor(utility.getRequestLocales());

        CodeListManagerIfc codeListManager = (CodeListManagerIfc)Dispatcher.getDispatcher().getManager(CodeListManagerIfc.TYPE);
        List<String> listIDs = codeListManager.getCodeListIDs(criteria);

        cargo.setListIDs(listIDs);

        bus.mail(letter, BusIfc.CURRENT);
    }
}
