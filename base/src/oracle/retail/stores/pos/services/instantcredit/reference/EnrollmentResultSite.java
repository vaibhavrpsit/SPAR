/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/reference/EnrollmentResultSite.java /rgbustores_13.4x_generic_branch/3 2011/06/21 17:03:02 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/21/11 - handle the case that response status will be null
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    sgu       05/11/11 - define approval status for instant credit as enum
 *                         type
 *    sgu       05/11/11 - fix instant credit cargo to use the new reponse
 *                         object
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:44  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 24 2003 19:54:52   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.reference;

// foundation imports
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
//------------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/3 $
**/
//------------------------------------------------------------------------------

public class EnrollmentResultSite extends SiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        InstantCreditCargo    cargo       = (InstantCreditCargo) bus.getCargo();
        POSUIManagerIfc         ui          = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel        model       = new POSBaseBeanModel();
        PromptAndResponseModel  pModel      = new PromptAndResponseModel();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        if (InstantCreditApprovalStatus.APPROVED.equals(cargo.getInstantCreditResponse().getApprovalStatus()))
        {
            String promptText = utility.retrieveText
                    (POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                    BundleConstantsIfc.COMMON_BUNDLE_NAME,
                    "InstantCreditApprovedPrompt",
                    "The enrollment application was Approved. Press Next to continue.");
            pModel.setPromptText(promptText);
        }
        else
        {
            String promptText = utility.retrieveText
                                    (POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                    BundleConstantsIfc.COMMON_BUNDLE_NAME,
                                    "InstantCreditNotApprovedPrompt",
                                    "The enrollment application was Not Approved. Press Next to continue.");
            pModel.setPromptText(promptText);
        }
        model.setPromptAndResponseModel(pModel);
        ui.showScreen(POSUIManagerIfc.ENROLL_RESPONSE, model);
    }

}
