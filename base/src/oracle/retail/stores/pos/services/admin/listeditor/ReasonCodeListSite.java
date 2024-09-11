/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/listeditor/ReasonCodeListSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/3/2008 5:16:03 PM    Jogesh Panda    File
 *         changed for getting reason code from the Parameter Manager.
 *    4    360Commerce 1.3         5/4/2007 4:34:49 PM    Owen D. Horne
 *         CR#26038 enable/disabled NoDefault button per Reason Code Group's
 *         defaultRequired flag
 *    3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:34 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/14 18:47:09  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.4  2004/04/19 18:48:56  awilliam
 *   @scr 4374 Reason Code featrure work
 *
 *   Revision 1.3  2004/02/12 16:48:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:36   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 09 2002 14:16:48   mpm
 * Text externalization.
 *
 *    Rev 1.0   Sep 21 2001 11:11:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.listeditor;

// foundation imports
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;

// ------------------------------------------------------------------------------
/**
 * Edit a reason code group.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
// ------------------------------------------------------------------------------
public class ReasonCodeListSite extends PosSiteActionAdapter
{
    /** revision number * */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // --------------------------------------------------------------------------
    /**
	 * Sets up the UI to edit a reason code group.
	 * <p>
	 * 
	 * @param bus
	 *            the bus arriving at this site
	 */
    // --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ListEditorCargo cargo = (ListEditorCargo) bus.getCargo();
        cargo.setOperationRequested(null);
        ReasonCodeGroupBeanModel beanModel = cargo.getReasonCodeGroup();
        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        ParameterManagerIfc pm = (ParameterManagerIfc)
        bus.getManager(ParameterManagerIfc.TYPE);
        String reasonCodeEdit ="N";
        try{
	        
	        Object values[] =pm.getParameterValues("EditReasonCodes");
	        reasonCodeEdit = (String)values[0];
	    } 
	    catch (ParameterException e) { 
	        logger.warn("EditReasonCodes parameter could not be found.");
	    } 
	    catch (Exception e)
        {
	    	logger.warn("Exception Occured while retrieveing EditReasonCodes parameter"+ e.toString());
        }


        
        if (reasonCodeEdit.equalsIgnoreCase("N"))
        {
            cargo.setReasonCodeScreenToDisplay(POSUIManagerIfc.REASON_CODE_LIST_VIEW_ONLY);
        }
        else
        {
            cargo.setReasonCodeScreenToDisplay(POSUIManagerIfc.REASON_CODE_LIST);
        }
        
        // enable/disable "No Default" button per ReasonCodeGroupBeanModel's
		// defaultRequired flag
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        navModel.setButtonEnabled("NoDefault", !beanModel.isDefaultRequired());
        beanModel.setLocalButtonBeanModel(navModel);
        
        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(cargo.getReasonCodeScreenToDisplay(), beanModel);
    }
}
