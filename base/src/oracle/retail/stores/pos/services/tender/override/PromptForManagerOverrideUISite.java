/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/override/PromptForManagerOverrideUISite.java /rgbustores_13.4x_generic_branch/1 2011/07/28 21:09:47 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:36 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:28 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/07/27 19:27:28  bwf
 *   @scr 5947 Set correct error messages for security.
 *
 *   Revision 1.2  2004/07/16 16:56:23  bwf
 *   @scr 6130 Check manager override parameter and display error.
 *
 *   Revision 1.1  2004/04/02 20:56:24  epd
 *   @scr 4263 Updates to accommodate new tender limit override station
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:17:48   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:29:50   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:46   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.override;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.utility.tdo.ManagerOverrideTDOIfc;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ReasonCodeValue;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 *  This site prompts for Manager override
 */
public class PromptForManagerOverrideUISite extends PosSiteActionAdapter
{

    /**
        manager override tdo
    **/
    protected static final String TDO_MANAGER_OVERRIDE = "tdo.utility.manageroverride";
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        UserAccessCargo cargo = (UserAccessCargo)bus.getCargo();
        int fID = cargo.getAccessFunctionID();
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        boolean overridable = true;
        try
        {
            ManagerOverrideTDOIfc tdo  = (ManagerOverrideTDOIfc)TDOFactory.create(TDO_MANAGER_OVERRIDE);
            overridable = tdo.isOverridable(bus, fID);
        }
        catch (TDOException e)
        {          
            logger.error(" manager override tdo exception", e);
        }
               
        // If the list of functions contains the functionID of the function being accessed,
        //  then that function allows manager override.
        
        if(!overridable)
        {
            // get correct arg text
            String title = Role.getFunctionTitle(userLocale, fID);
            String args[] = new String[] {title};
            
            // display screen
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("SecurityAccess");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
            dialogModel.setArgs(args);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            String args[] = new String[] {Role.getFunctionTitle(userLocale, fID)};
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(cargo.getResourceID());
            model.setType(DialogScreensIfc.CONFIRMATION);
            model.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Failure");
            model.setArgs(args);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
    }
    
    
    
    //----------------------------------------------------------------------
    /**
        Check to see if the functionID being accessed is in the list of 
        functions contained by the ManagerOverrideForSecurityAccess parameter.
        @param rcValues The list of override-able functions.
        @param functionID The ID of the function being accessed.
        @deprecated Deprecated as of 7.0.1 replaced by ManagerOverrideTDOIfc 
                    isOverridable method
    **/
    //----------------------------------------------------------------------
    public boolean containsID(ReasonCodeValue[] rcValues, int functionID)
    {
        boolean retCode = false;
        
        for(int i=0; i<rcValues.length && retCode==false; i++)
        {
            if( rcValues[i].getDatabaseId() == functionID )
            {
                retCode = true;
                break;
            }
        }
        
        return retCode;
    }
    
}
