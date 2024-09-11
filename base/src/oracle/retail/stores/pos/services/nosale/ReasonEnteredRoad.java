/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/nosale/ReasonEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/21/08 - I18N - Localizing No Sale ReasonCode

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:33 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:34 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:51:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:03:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 28 2003 15:41:24   bwf
 * Set correct reason code.
 * Resolution for 3274: No Sale Receipt is printing wrong reason code
 *
 *    Rev 1.4   Jul 19 2003 07:52:32   jgs
 * The root cause of the help screen problem was discover to reside in the reason code bean.  Since it has been resolved, this work around can be removed.
 * Resolution for 3031: Selecting Enter on No Sale Reason after returning from Help hangs POS
 *
 *    Rev 1.3   Jul 18 2003 14:09:22   jgs
 * This fix is a tempory measure to prevent the application from hanging when the user selects F1 to display help and returns the no sale screen.  In this case the list does not have focus, and model does not contain a valid reason code description.
 * Resolution for 3031: Selecting Enter on No Sale Reason after returning from Help hangs POS
 *
 *    Rev 1.2   Feb 14 2003 14:43:54   HDyer
 * Use ReasonBeanModel instead of deprecated NoSaleReasonBeanModel.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Jan 14 2003 08:50:32   RSachdeva
 * Replaced AbstractFinancialCargo.getCodeListMap()   by UtilityManagerIfc.getCodeListMap()
 * Resolution for POS SCR-1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.0   Apr 29 2002 15:13:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:32:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:08   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.nosale;

// foundation imports
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

//--------------------------------------------------------------------------
/**
    This road is traveled after the user has entered the
    No Sale Reason code.  It stores the reason code in the cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReasonEnteredRoad extends LaneActionAdapter
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -3278744719699695782L;
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Stores the reason code in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        /*
         * Get reason code from the UI Manager
         */
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ReasonBeanModel model =
            (ReasonBeanModel)ui.getModel(POSUIManagerIfc.SELECT_NO_SALE_REASON_CODE);

        /*
         * Store the reason code in the cargo
         */
        NoSaleCargo  cargo = (NoSaleCargo)bus.getCargo();
        String      reason = model.getSelectedReasonKey();

        CodeListIfc   list = cargo.getLocalizedReasonCodes();

        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode (reason);
            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());
            cargo.setSelectedReasonCode(localizedCode);
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  ReasonEnteredRoad (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
