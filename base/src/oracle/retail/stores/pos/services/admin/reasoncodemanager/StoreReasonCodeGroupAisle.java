/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/StoreReasonCodeGroupAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    mdecama   11/03/08 - Updates to the persistence of CodeLists.
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:30:13 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:36 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:30 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:48:53  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:36:39  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:53:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:38:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:06:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 19 2002 10:28:10   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:12:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;

// foundation imports
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCode;
import oracle.retail.stores.pos.ui.beans.ReasonCodeGroupBeanModel;
import oracle.retail.stores.pos.ui.beans.ReasonCodesCommon;

//------------------------------------------------------------------------------
/**
    Store the reasonCode group as entered at the UI.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class StoreReasonCodeGroupAisle extends LaneActionAdapter
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -3138212138821136683L;

    //--------------------------------------------------------------------------
    /**
        Stores the reasonCode group as entered at the UI. <p>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ReasonCodeCargo cargo = (ReasonCodeCargo)bus.getCargo();

        ListBeanModel model =
            (ListBeanModel)ui.getModel(POSUIManagerIfc.REASON_CODE_SELECT_GROUP);

        ReasonCodeGroupBeanModel selectedModel =
            (ReasonCodeGroupBeanModel)model.getSelectedValue();

        // List Selected, retrieve all the entries
        String listID = selectedModel.getParameterGroup();
        CodeListIfc codeList = utility.getReasonCodes(cargo.getOperator().getStoreID(), listID);
        cargo.setCodeList(codeList);

        ReasonCodeGroupBeanModel modelGroup = inject (codeList, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        cargo.setReasonCodeGroup(modelGroup);

        bus.mail(new Letter(ReasonCodesCommon.ACCEPT_DATA), BusIfc.CURRENT);
    }

    /**
     * Converts a CodeListIfc (as used by the business logic) into ReasonCode
     * bean (as used by the UI).
     *
     * @param list the reason code list to convert into a bean
     * @return the ReasonCode bean model
     * <Note: this method was previously in the cargo>
     */
    public ReasonCodeGroupBeanModel inject(CodeListIfc list, Locale lcl)
    {
        // Set the generic fields
        ReasonCodeGroupBeanModel group = new ReasonCodeGroupBeanModel();
        group.setParameterGroup(list.getGroupName());
        group.setGroupName(list.getListDescription());
        group.setModifiable(true);
        group.setIdIsNumeric(list.getNumericCodes());
        group.setSource(list.getSource());
        group.setDefaultReasonCode(list.getDefaultOrEmptyString(lcl));
        group.setStoreID(list.getStoreID());

        // Create a reason code from group
        Vector<ReasonCode> reasonCodes = new Vector<ReasonCode>();
        Vector<ReasonCode> deletedCodes = new Vector<ReasonCode>();

        CodeEntryIfc[] entries = list.getEntries();
        for (int i = 0; i < entries.length; i++)
        {
            ReasonCode reasonCode = new ReasonCode();
            reasonCode.setReasonCodeName(entries[i].getText(lcl));
            if (entries[i].getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
            {
                reasonCode.setDatabaseId("0");
            }
            else
            {
                reasonCode.setDatabaseId(entries[i].getCode());
            }

            reasonCode.setEnabled(entries[i].getEnabled());
            reasonCode.setReasonCodeGroup(list.getListDescription());
            reasonCode.setReferenceKey(entries[i].getReferenceKey());
            reasonCode.setCodeName(entries[i].getCodeName());
            if (reasonCode.getEnabled())
            {
                reasonCodes.addElement(reasonCode);
            }
            else
            {
                deletedCodes.addElement(reasonCode);
            }
        }

        // Add the disabled codes to the end of the vector.
        Enumeration enumer = deletedCodes.elements();
        while (enumer.hasMoreElements())
        {
            reasonCodes.addElement((ReasonCode)enumer.nextElement());
        }

        // Add the vector of reason code models to the list model
        group.setReasonCodes(reasonCodes);

        return group;
    }
}
