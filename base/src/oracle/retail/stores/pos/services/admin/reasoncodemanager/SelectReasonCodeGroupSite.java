/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/SelectReasonCodeGroupSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ranojha   11/20/08 - Incorporated Code review comments
 *    ranojha   11/20/08 - Fixed SelectReasonCodeGroupSite for protective code
 *                         against NullPointException
 *    mdecama   11/03/08 - Sorting the ReasonCode List
 *    mdecama   11/03/08 - Updates to the persistence of CodeLists.
 * ===========================================================================

 $Log:
 4    360Commerce 1.3         3/29/2007 6:06:13 PM   Michael Boyd    CR
 26172 - v8x merge to trunk

 4    .v8x      1.2.1.0     3/8/2007 4:06:31 PM    Maisa De Camargo
 Hiding
 the TimeTypeCodes and TimekeepingManagerEditReasonCodes Reason
 Code Groups.
 They are not needed by POS (only Backoffice).
 3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse
 $
 Revision 1.4  2004/04/19 18:48:56  awilliam
 @scr 4374 Reason Code featrure work

 Revision 1.3  2004/02/12 16:48:53  mcs
 Forcing head revision

 Revision 1.2  2004/02/11 21:36:39  rhafernik
 @scr 0 Log4J conversion and code cleanup

 Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:53:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 03 2003 10:57:38   bwf
 * Made sure to check Tag instead of internationalized text.
 * Resolution for 2054: The string "Shipping Method" from SelectReasonCodeGroupSite should be i18n.
 *
 *    Rev 1.0   Apr 29 2002 15:38:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:06:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:36   msg
 * Initial revision.
 *
 *    Rev 1.2   Jan 19 2002 10:28:10   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   07 Jan 2002 18:02:06   sfl
 * Filter out "Shipping Method" from the standard reason code group list because the whole purpose of have "Shipping Method" in the reason code flat file is to support displaying
 * a shipping method selection list during send when database
 * is offline, it is not used like regular reason code used in other
 * applications.
 * Resolution for POS SCR-323: Shipping Method use case in Send Package
 *
 *    Rev 1.0   Sep 21 2001 11:12:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;

// java imports
import java.util.Arrays;
import java.util.List;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;

// ------------------------------------------------------------------------------
/**
    Choose which reason code group will be edited.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SelectReasonCodeGroupSite extends PosSiteActionAdapter
{
    /**
     * Generate SerialVersionUID
     */
    private static final long serialVersionUID = -2012185238955730049L;

    /**
     * Key for Shipping Method in bundles.
     * @deprecated as 13.1 use {@link CodeConstantsIfc.CODE_LIST_SHIPPING_METHOD}
     */
    public String SHIPPING_METHOD_TAG = "ShippingMethod";

    /**
     * ID for the TimekeepingManagerEditReasonCodes Group
     * @deprecated as of 13.1 use {@link CodeConstantsIfc.CODE_LIST_TIMEKEEPING_MANAGER_EDIT_REASON_CODES}
     */
    public String TIME_KEEPING_MANAGER_EDIT_REASON_CODES_GROUP = "TimekeepingManagerEditReasonCodes";

    /**
     * ID for the TimeTypeCodes Group
     * @deprecated as of 13.1. use {@link CodeConstantsIfc.TIME_TYPE_CODES_GROUP}
     */
    public String TIME_TYPE_CODES_GROUP = "TimeTypeCodes";

    /**
     * Sets up the UI to choose which reason code group will be edited.
     * <p>
     *
     * @param bus the bus arriving at this site
     */
    // --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ReasonCodeCargo cargo = (ReasonCodeCargo)bus.getCargo();

        List<String> listIDs = cargo.getListIDs();
        ListBeanModel beanModel = new ListBeanModel();
        if (listIDs != null)
        {
	        listIDs.remove(CodeConstantsIfc.CODE_LIST_SHIPPING_METHOD);
	        listIDs.remove(CodeConstantsIfc.CODE_LIST_TIMEKEEPING_MANAGER_EDIT_REASON_CODES);
	        listIDs.remove(CodeConstantsIfc.CODE_LIST_TIME_TYPE_CODES);
	
	        ReasonCodeGroupBeanModel groups[] = new ReasonCodeGroupBeanModel [listIDs.size()];
	
	        // Sort
	        Object[] listIDArray = listIDs.toArray();
	        Arrays.sort(listIDArray);
	
	        for (int i = 0; i < listIDs.size(); i++)
	        {
	            ReasonCodeGroupBeanModel group = new ReasonCodeGroupBeanModel();
	
	            group.setParameterGroup((String)listIDArray[i]);
	            group.setGroupName(UIUtilities.retrieveCommonText ((String) listIDArray[i]));
	            groups[i] = group;
	        }
	
	        
	        beanModel.setListModel(groups);
	        beanModel.setSelectedValue(groups[0]);
	        beanModel.setSelectedRow(0);
        }
        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.REASON_CODE_SELECT_GROUP, beanModel);
    }
}
