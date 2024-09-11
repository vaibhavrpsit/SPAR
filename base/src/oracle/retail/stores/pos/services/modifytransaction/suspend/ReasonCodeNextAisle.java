/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/ReasonCodeNextAisle.java /main/13 2013/02/04 09:49:43 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     02/01/13 - setting suspended reason code with operator selected
 *                         value
 *    tzgarba   07/02/12 - Added check for pre-populated reason code before
 *                         getting it from the model.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/24/08 - I18N updates for Suspend Transaction Reason Codes.
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:34 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:51:16  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:47  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:02:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 21 2003 12:29:46   HDyer
 * Remove use of SelectReasonBeanModel to standardize on use of ReasonBeanModel.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 15:15:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:54   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.suspend;
// foundation imports
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

//------------------------------------------------------------------------------
/**
        Retrieves selected reason code from user interface. <P>
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class ReasonCodeNextAisle extends PosLaneActionAdapter
{                                                                               // begin class ReasonCodeNextAisle
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -796493535491393049L;
    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
       lane name constant
    **/
    public static final String LANENAME = "ReasonCodeNextAisle";

    //--------------------------------------------------------------------------
    /**
       Retrieves selected reason code from user interface. <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                                                   // begin traverse()

        // retrieve bean model from ui
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReasonBeanModel model = (ReasonBeanModel)
            ui.getModel(POSUIManagerIfc.SELECT_SUSPEND_REASON_CODE);
        // compare selection to list and find match
        ModifyTransactionSuspendCargo cargo =
            (ModifyTransactionSuspendCargo) bus.getCargo();

        // If not set by caller OR default Localized Code is set by DomainGateway
        if (cargo.getSelectedLocalizedReason() == null || cargo.getSelectedLocalizedReason().getCode().equals("-1"))
        {
            CodeListIfc   list = cargo.getReasonCodes();
            if (list != null)
            {
                String reason = model.getSelectedReasonKey();
                CodeEntryIfc entry = list.findListEntryByCode (reason);
                LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
                localizedCode.setCode(reason);
                localizedCode.setText(entry.getLocalizedText());
                cargo.setSelectedLocalizedReason(localizedCode);
            }
        }

        bus.mail(new Letter("DoNext"), BusIfc.CURRENT);

    }                                                                   // end traverse()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}                                                                               // end class ReasonCodeNextAisle
