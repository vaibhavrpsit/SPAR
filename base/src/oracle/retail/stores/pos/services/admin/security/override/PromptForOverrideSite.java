/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/override/PromptForOverrideSite.java /main/15 2013/11/19 09:42:41 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/18/13 - create unlockScreen method for cargo that needs to
 *                         control the ui not to unlock until it is done.
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   02/24/10 - add check for getAccessFunctionTitle
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:24:26 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:13:28 PM  Robert Pearse   
 *
 * Revision 1.7  2004/06/25 22:54:54  cdb
 * @scr 1642 Updated so that Undo selected from Operator ID screen returns
 * to the calling service rather than prompting for another security override.
 * 
 * Revision 1.6  2004/06/03 14:47:43  epd
 * @scr 5368 Update to use of DataTransactionFactory
 * 
 * Revision 1.5  2004/04/20 13:11:00  tmorris
 * @scr 4332 -Sorted imports
 * 
 * Revision 1.4  2004/04/14 15:17:09  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 * 
 * Revision 1.3  2004/02/12 16:49:03  mcs
 * Forcing head revision
 * 
 * Revision 1.2  2004/02/11 21:37:44  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 * updating to pvcs 360store-current
 * 
 *    Rev 1.0   Aug 29 2003 15:53:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 06 2003 13:21:00   sfl
 * Use static method in Role.java
 * Resolution for POS SCR-2444: <ARG> missing on Security Error screen
 *
 *    Rev 1.1   05 Jul 2003 23:10:02   baa
 * display role fn title
 *
 *    Rev 1.0   Apr 29 2002 15:37:16   msg
 * Initial revision.
 *
 *    Rev 1.1   04 Apr 2002 15:22:26   baa
 * Remove references to Rolefunction descriptor array and maximun number of role functions
 * Resolution for POS SCR-1565: Remove references to RoleFunctionIfc.Descriptor Security Service
 *
 *    Rev 1.0   Mar 18 2002 11:21:48   msg
 * Initial revision.
 *
 *    Rev 1.4   17 Jan 2002 17:35:24   baa
 * update roles/security model
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.3   29 Nov 2001 17:11:52   pdd
 * Set <ARG> in override dialog.
 * Resolution for POS SCR-50: Security Access Error message should be ARGd to the function
 *
 *    Rev 1.2   09 Nov 2001 17:20:26   pdd
 * Code review cleanup.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.1   22 Oct 2001 17:00:34   pdd
 * Added SCR association.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.0   22 Oct 2001 15:04:30   pdd
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.override;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.RoleTransaction;
import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Prompts the user to determine whether they want to override access to a
 * function.
 * 
 * @version $Revision: /main/15 $
 */
public class PromptForOverrideSite extends SiteActionAdapter
{
    private static final long serialVersionUID = 346851650900640257L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SecurityOverrideCargo cargo = (SecurityOverrideCargo) bus.getCargo();
        cargo.setUndoSelected(false);
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        
        String dialogID = cargo.getResourceID();

        String title = cargo.getAccessFunctionTitle();

        if (Util.isEmpty(title))
        {
            title = Role.getFunctionTitle(userLocale, cargo.getAccessFunctionID());
        }

        if (Util.isEmpty(title))
        {
            RoleFunctionIfc[] roleFunctions = getFunctions();
            if (roleFunctions != null)
            {
                title = Role.getFunctionTitle(userLocale, cargo.getAccessFunctionID());
            }
            else
            {
                dialogID = "SecurityErrorNotice";
            }
        }

        String args[] = new String[] {title};
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setUnlockContainer(cargo.isUnlockScreenAfterDialog());
        model.setResourceID(dialogID);
        model.setType(DialogScreensIfc.NO_RESPONSE);
        model.setArgs(args);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, CommonLetterIfc.FAILURE);
        ui.showDialogAndWait(POSUIManagerIfc.OVERRIDE_DIALOG, model, true);
    }

    /**
     * Get an array of role functions to initialize the role object.
     * 
     * @return RoleFunctionIfc[] array of RoleFunctionIfc objects
     */
    public RoleFunctionIfc[] getFunctions()
    {
        RoleTransaction trans = null;

        trans = (RoleTransaction) DataTransactionFactory.create(DataTransactionKeys.ROLE_TRANSACTION);

        RoleFunctionIfc[] funcs;
        try
        {
            funcs = trans.getRoleFunctions();
        }
        catch (DataException e)
        {
            funcs = null;
        }
        return funcs;
    }
}
