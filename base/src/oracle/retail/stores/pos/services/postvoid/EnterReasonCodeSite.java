/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/EnterReasonCodeSite.java /main/14 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abonda 09/04/13 - initialize collections
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *    acadar 10/24/08 - localization of post void reason codes
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:03 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:26 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse
     $
     Revision 1.5  2004/03/03 23:15:08  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.4  2004/02/12 16:48:15  mcs
     Forcing head revision

     Revision 1.3  2004/02/11 23:22:58  bwf
     @scr 0 Organize imports.

     Revision 1.2  2004/02/11 21:28:20  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Jan 21 2004 14:54:52   epd
 * fixed bug in depar() by wrapping in method to check for letter name
 *
 *    Rev 1.1   Nov 19 2003 14:10:40   epd
 * TDO refactoring to use factory
 *
 *    Rev 1.0   Nov 04 2003 11:16:02   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:28:32   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:03:20   epd
 * Initial revision.
 *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.postvoid;

// Java imports
import java.util.HashMap;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.postvoid.tdo.VoidReasonCodeTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.VoidConfirmBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the VOID_CONFIRM screen.
    <p>
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class EnterReasonCodeSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    //----------------------------------------------------------------------
    /**
       Displays the VOID_CONFIRM Screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        VoidCargo cargo = (VoidCargo) bus.getCargo();

        // build the attributes map for the TDO to use to build the UI model
        HashMap uiAttributes = new HashMap(3);
        uiAttributes.put(VoidReasonCodeTDO.TRANSACTION, cargo.getOriginalTransactionADO());
        uiAttributes.put(VoidReasonCodeTDO.BUS, bus);


        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        CodeListIfc codeList = utility.getReasonCodes(cargo.getOperator().getStoreID(),CodeConstantsIfc.CODE_LIST_POST_VOID_REASON_CODES);
        uiAttributes.put(CodeConstantsIfc.CODE_LIST_POST_VOID_REASON_CODES, codeList);
        cargo.setLocalizedReasonCodes(codeList);

        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc)TDOFactory.create("tdo.postvoid.VoidReasonCode");
        }
        catch (TDOException e)
        {
           logger.error("TDO not found: tdo.postvoid.VoidReasonCode");
        }

        POSBaseBeanModel model = tdo.buildBeanModel(uiAttributes);

        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.VOID_CONFIRM, model);
    }

    /**
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT))
        {
            POSUIManagerIfc ui;
            ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            VoidConfirmBeanModel model =
                (VoidConfirmBeanModel) ui.getModel(POSUIManagerIfc.VOID_CONFIRM);

            VoidCargo cargo = (VoidCargo)bus.getCargo();
            String      reasonCodeKey = model.getSelectedReasonKey();

            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            CodeListIfc   list = cargo.getLocalizedReasonCodes();

            if (list != null)
            {
                CodeEntryIfc entry = list.findListEntryByCode (reasonCodeKey);

                localizedCode.setCode(reasonCodeKey);
                localizedCode.setText(entry.getLocalizedText());
            }
            else
            {
                localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
            }
            cargo.setLocalizedReasonCode(localizedCode);

        }
    }



}
