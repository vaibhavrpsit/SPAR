/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/listeditor/EditReasonCodeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:52:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:40:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:26   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 09 2002 14:16:48   mpm
 * Text externalization.
 *
 *    Rev 1.0   Sep 21 2001 11:11:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.listeditor;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Edits a reason code whose value is a string value.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EditReasonCodeSite extends PosSiteActionAdapter
{
    /** Add Reason Code prompt constant */
    protected static final String RC_ADD_PROMPT_TAG   = "EditReasonCodeSite.ReasonCodeAddPrompt";
    /** Add Reason Code prompt constant */
    protected static final String RC_ADD_PROMPT   = "Add a reason code and database ID and press Next.";
    /** Add Reason Code screen name */
    protected static final String RC_ADD_SC_NAME_TAG  = "EditReasonCodeSite.ReasonCodeAddScreenName";
    /** Add Reason Code screen name */
    protected static final String RC_ADD_SC_NAME  = "Add Reason Code";
    /** Modify Reason Code prompt constant */
    protected static final String RC_EDIT_PROMPT_TAG  = "EditReasonCodeSite.ReasonCodeEditPrompt";
    /** Modify Reason Code prompt constant */
    protected static final String RC_EDIT_PROMPT  = "Modify the reason code value and press Next.";
    /** Modify Reason Code screen name */
    protected static final String RC_EDIT_SC_NAME_TAG = "EditReasonCodeSite.ReasonCodeEditScreenName";
    /** Modify Reason Code screen name */
    protected static final String RC_EDIT_SC_NAME = "Edit Reason Code";
    /** Modify Parameter prompt constant */
    protected static final String PM_ADD_PROMPT_TAG   = "EditReasonCodeSite.ParameterListEditPrompt";
    /** Modify Parameter prompt constant */
    protected static final String PM_ADD_PROMPT   = "Modify the parameter value and press Next.";
    /** Modify parameter screen name */
    protected static final String PM_ADD_SC_NAME_TAG  = "EditReasonCodeSite.ParameterListEditScreenName";
    /** Modify parameter screen name */
    protected static final String PM_ADD_SC_NAME  = "Edit Param List";
    /** Add Parameter prompt constant */
    protected static final String PM_EDIT_PROMPT_TAG  = "EditReasonCodeSite.ParameterAddPrompt";
    /** Add Parameter prompt constant */
    protected static final String PM_EDIT_PROMPT  = "Add a parameter value and press Next.";
    /** Add Parameter screen name */
    protected static final String PM_EDIT_SC_NAME_TAG = "EditReasonCodeSite.ParameterAddScreenName";
    /** Add Parameter screen name */
    protected static final String PM_EDIT_SC_NAME = "Add Param List";

    //--------------------------------------------------------------------------
    /**
        Edits a reason code whose value is a string. <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();
        cargo.setErrorMessage(null);
        ReasonCode beanModel = cargo.getReasonCode();

        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // display the UI screen
        // This single site is used for both adds and edits of reason codes
        // and parameter lists.  We use essentially the same screen
        // (with different prompts) for all.
        String addOrEdit = null;
        if (cargo.getOperationRequested() == ReasonCodesCommon.SHOW_ADD_SCREEN)
        {
            beanModel.setEditBean(false);
        }

        // Instantiate models
        StatusBeanModel        sbm = new StatusBeanModel();
        PromptAndResponseModel prm = new PromptAndResponseModel();
        String screenNameTag = PM_EDIT_SC_NAME_TAG;
        String promptTextTag = PM_EDIT_PROMPT_TAG;
        String screenName = PM_EDIT_SC_NAME;
        String promptText = PM_EDIT_PROMPT;
        if (cargo.getReasonCodeGroup().getModifyingParameter())
        {
            beanModel.setModifyingParameter(true);
            addOrEdit = POSUIManagerIfc.PARAMETER_LIST_EDIT;

            if (beanModel.getEditBean())
            {
                screenNameTag = PM_EDIT_SC_NAME_TAG;
                screenName = PM_EDIT_SC_NAME;
                promptTextTag = PM_EDIT_PROMPT_TAG;
                promptText = PM_EDIT_PROMPT;
            }
            else
            {
                screenNameTag = PM_ADD_SC_NAME_TAG;
                screenName = PM_ADD_SC_NAME;
                promptTextTag = PM_ADD_PROMPT_TAG;
                promptText = PM_ADD_PROMPT;
            }
        }
        else
        {
            if (beanModel.getEditBean())
            {
                screenNameTag = RC_EDIT_SC_NAME_TAG;
                screenName = RC_EDIT_SC_NAME;
                promptTextTag = RC_EDIT_PROMPT_TAG;
                promptText = RC_EDIT_PROMPT;
            }
            else
            {
                screenNameTag = RC_ADD_SC_NAME_TAG;
                screenName = RC_ADD_SC_NAME;
                promptTextTag = RC_ADD_PROMPT_TAG;
                promptText = RC_ADD_PROMPT;
            }

            if (cargo.getReasonCodeGroup().getIdIsNumeric())
            {
                addOrEdit = POSUIManagerIfc.REASON_CODE_EDIT;
            }
            else
            {
                addOrEdit = POSUIManagerIfc.REASON_CODE_EDIT_ALPHA;
            }
        }

        screenName = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                                          BundleConstantsIfc.LIST_EDITOR_BUNDLE_NAME,
                                          screenNameTag,
                                          screenName);
        sbm.setScreenName(screenName);
        promptText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                          BundleConstantsIfc.LIST_EDITOR_BUNDLE_NAME,
                                          promptTextTag,
                                          promptText);
        prm.setPromptText(promptText);


        beanModel.setStatusBeanModel(sbm);
        beanModel.setPromptAndResponseModel(prm);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(addOrEdit, beanModel);
    }

}


