/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/listeditor/StoreNewValuesAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    Rev 1.0   Aug 29 2003 15:52:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Mar 03 2003 10:16:06   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.3   Aug 14 2002 10:49:54   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Jul 05 2002 15:14:42   RSachdeva
 * code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 02 2002 09:51:58   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:40:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:19:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   02 Mar 2002 16:42:38   jbp
 * Changed selected index.
 * Resolution for POS SCR-1341: Edit Reason Code missing * for data fields
 *
 *    Rev 1.0   Sep 21 2001 11:11:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.listeditor;

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
    /**
        invalid reason code value database id tag
    **/
    public static final String INVALID_REASON_CODE_VALUE_DATABASE_ID_TAG = 
      "InvalidReasonCodeValue.databaseId";
    /**
        invalid reason code value database id default text
    **/
    public static final String INVALID_REASON_CODE_VALUE_DATABASE_ID_TEXT = 
      "The database ID entered  {0}  is already being used by {1}";
    /**
        invalid reason code value both tag
    **/
    public static final String INVALID_REASON_CODE_VALUE_BOTH_TAG = 
      "InvalidReasonCodeValue.both";
    /**
        invalid reason code value both tag
    **/
    public static final String INVALID_REASON_CODE_VALUE_BOTH_TEXT = 
      "Already in USE";

      

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
                UtilityManagerIfc utility = 
                  (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
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
       Check the edit for a db id conflict.
       @param cargo
    **/
    //--------------------------------------------------------------------------
    protected boolean badDatabaseId(ListEditorCargo cargo)
    {
        boolean isBad = false;
        ReasonCode reason = cargo.getReasonCode();
        String newId = reason.getNewDatabaseId();

        ReasonCodeGroupBeanModel group = cargo.getReasonCodeGroup();
        Vector reasonCodes = group.getReasonCodes();
        ReasonCode reasonCode = cargo.getReasonCode();
        String opRequested = cargo.getOperationRequested();
        ReasonCode currentRc = null;
        String currentId = null;

        for (int i = 0; i < reasonCodes.size() && !isBad; i++)
        {
            currentRc = (ReasonCode)reasonCodes.elementAt(i);
            currentId = currentRc.getDatabaseId();

            // If the currentId and the new id are the same, but
            // current reason code has been marked for deletion....
            if (newId.equals(currentId) && !currentRc.getEnabled())
            {
                // It is ok to "re-add" it to the list, So remove the
                // current reason code and let the edit go on.
                reasonCodes.removeElementAt(i);
            }
            else
            {
                // When editing a reason code, the user may keep the
                // same id, but any other duplication is bad.
                isBad = ((newId.equals(currentId))
                         && !(ReasonCodesCommon
                              .SHOW_EDIT_SCREEN.equals(opRequested)
                              && currentRc.equals(reasonCode)));
            }

            if (isBad)
            {
                UtilityManagerIfc utility = 
                  (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
                String[] vars = new String[2];
                vars[0] = newId;
                vars[1] = currentRc.getReasonCodeName();
                String pattern = utility.retrieveDialogText(INVALID_REASON_CODE_VALUE_DATABASE_ID_TAG,
                                                            INVALID_REASON_CODE_VALUE_DATABASE_ID_TEXT); 
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

        // This aisle is used for both adds and edits.  We use
        // essentially the same screen (with different prompts) for
        // both.
        String addOrEdit = null;
        if (cargo.getReasonCodeGroup().getModifyingParameter())
        {
            addOrEdit = POSUIManagerIfc.PARAMETER_LIST_EDIT;
        }
        else
        {
            if (cargo.getReasonCodeGroup().getIdIsNumeric())
            {
                addOrEdit = POSUIManagerIfc.REASON_CODE_EDIT;
            }
            else
            {
                addOrEdit = POSUIManagerIfc.REASON_CODE_EDIT_ALPHA;
            }
        }

        ReasonCode model = (ReasonCode)ui.getModel(addOrEdit);

        ReasonCode reason = cargo.getReasonCode();

        ReasonCodeGroupBeanModel group = cargo.getReasonCodeGroup();
        String oldName = reason.getReasonCodeName();


        // NEED TO DO SOME VALIDATION HERE
        String newName = model.getNewReasonCodeName();
        reason.setNewReasonCodeName(newName);
        String newId = model.getNewDatabaseId();

        reason.setNewDatabaseId(newId);
        Letter letter = new Letter(ReasonCodesCommon.ACCEPT_DATA);
        boolean doValidations = !(reason.getModifyingParameter());
        boolean dataIsValid   = true;

        if (doValidations)
        {
            if ((newName == null) || newName.equals("") ||
                (newId == null) || (newId == "0") || (newId == ""))
            {
                UtilityManagerIfc utility = 
                  (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
                String pattern = utility.retrieveDialogText(INVALID_REASON_CODE_VALUE_BOTH_TAG,
                                                            INVALID_REASON_CODE_VALUE_BOTH_TEXT); 
                cargo.setErrorMessage(pattern);
                dataIsValid = false;
            }
            else
                if (badReasonCodeName(cargo) || badDatabaseId(cargo))
                {
                    dataIsValid = false;
                }
        }

        if (dataIsValid)
        {
            reason.setReasonCodeName(newName);

            reason.setDatabaseId(newId);

            String opRequested = cargo.getOperationRequested();

            // If the user is adding a reason code, do it here
            if (ReasonCodesCommon.SHOW_ADD_SCREEN.equals(opRequested))
            {
                Vector reasons = group.getReasonCodes();
                int position = group.getReasonCodeSelectionIndex();
                reasons.insertElementAt(reason, position + 1);
            }
            // If there was an edit of the default, we need to note it
            else
                if (oldName.equals(group.getDefaultReasonCode()))
                {
                    group.setDefaultReasonCode(newName);
                }
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
