/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/parametervaluelisteditor/StoreNewValuesAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:29 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 03 2003 10:28:48   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 14 2002 10:55:32   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 05 2002 15:18:56   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:38:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:10   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Feb 2002 14:40:04   KAC
 * Now handles situation with no existing values.
 * Resolution for POS SCR-1226: Update list parameter value editor per new requirements
 * 
 *    Rev 1.0   22 Jan 2002 13:52:56   KAC
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:11:24   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.parametervaluelisteditor;

//foundation imports
import java.util.Vector;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Store the values the user has entered.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreNewValuesAisle extends PosLaneActionAdapter
{
    public static final String LANENAME = "StoreNewValuesAisle";
    /**
        invalid reason code value name tag
    **/
    public static final String INVALID_REASON_CODE_VALUE_NAME_TAG = 
      "InvalidReasonCodeValue.name";
    /**
        invalid reason code value name default text
    **/
    public static final String INVALID_REASON_CODE_VALUE_NAME_TEXT = 
      "The name entered  {0} is already in use";
    //--------------------------------------------------------------------------
    /**
       Check the edit for a reason code name conflict.
       @param cargo
    **/
    //--------------------------------------------------------------------------
    protected boolean badReasonCodeName(ListEditorCargo cargo)
    {
        ReasonCode reason = cargo.getReasonCode();
        String newName = reason.getNewReasonCodeName();
        ReasonCodeGroupBeanModel group = cargo.getReasonCodeGroup();
        Vector reasonCodes = group.getReasonCodes();
        String opRequested = cargo.getOperationRequested();
        ReasonCode currentRc = null;
        String currentName = null;
        boolean isBad = false;
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
 
        for (int i = 0; i < reasonCodes.size() && !isBad; i++)
        {
            currentRc = (ReasonCode)reasonCodes.elementAt(i);
            currentName = currentRc.getReasonCodeName();

            // When editing a reason code, the user may keep the
            // same name, but any other duplication is bad.
            isBad = (newName.equals(currentName) && currentRc.getEnabled()
                     && !(ReasonCodesCommon.SHOW_EDIT_SCREEN.equals(opRequested)
                          && currentRc.equals(cargo.getReasonCode())));

            if (isBad)
            {
                String[] vars = new String[1];
                vars[0] = newName;
                String pattern = utility.retrieveDialogText(INVALID_REASON_CODE_VALUE_NAME_TAG,
                                                            INVALID_REASON_CODE_VALUE_NAME_TEXT); 
                String formattedMessage = LocaleUtilities.formatComplexMessage(pattern,vars);
                cargo.setErrorMessage(formattedMessage);
            }
        }

        return isBad;
    }

    //--------------------------------------------------------------------------
    /**
       Store the values the user has entered.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListEditorCargo cargo = (ListEditorCargo)bus.getCargo();

        // This aisle is used for both adds and edits.

        ReasonCode model = (ReasonCode)
            ui.getModel("PARAMETER_VALUE_EDIT");
        //ui.getModel(POSUIManagerIfc.PARAMETER_LIST_EDIT);

        ReasonCode reason = cargo.getReasonCode();
        ReasonCodeGroupBeanModel group = cargo.getReasonCodeGroup();
        String oldName = reason.getReasonCodeName();


        // NEED TO DO SOME VALIDATION HERE
        String newName = model.getNewReasonCodeName();
        reason.setNewReasonCodeName(newName);
        Letter letter = new Letter(ReasonCodesCommon.ACCEPT_DATA);
        reason.setReasonCodeName(newName);
        String opRequested = cargo.getOperationRequested();

        // If the user is adding a reason code, do it here
        if (ReasonCodesCommon.SHOW_ADD_SCREEN.equals(opRequested))
        {
            Vector reasons = group.getReasonCodes();

            // If we have elements, add this one after the selected one
            if (reasons.size() > 0)
            {
                int position = group.getReasonCodeSelectionIndex();
                reasons.insertElementAt(reason, position + 1);
            }
            else
            {
                reasons.addElement(reason);
            }
        }
        // If there was an edit of the default, we need to note it
        else if (oldName.equals(group.getDefaultReasonCode()))
        {
            group.setDefaultReasonCode(newName);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }


    //--------------------------------------------------------------------------
    /**
       Log the backing up.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void backup(BusIfc bus)
    {
    }

}
