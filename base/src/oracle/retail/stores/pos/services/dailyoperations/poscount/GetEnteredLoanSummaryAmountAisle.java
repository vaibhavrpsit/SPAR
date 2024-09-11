/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/GetEnteredLoanSummaryAmountAisle.java /main/13 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/12/10 - use default locale for display of currency
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Feb 04 2004 18:35:12   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// java imports
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

//--------------------------------------------------------------------------
/**
    Stores the amount and the 'from' register entered.
    <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class GetEnteredLoanSummaryAmountAisle extends PosLaneActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
       Gets the Loan Summary Amount and the From Register and saves it
       in the cargo.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);

        DataInputBeanModel model = (DataInputBeanModel)ui.getModel();

        // Get the tender amount from the UI
        // This use of toString is safe here. We need to convert string into decimal format
        String currencyText = (LocaleUtilities.parseCurrency(
                               (String)model.getValueAsString("amountField"), defaultLocale)).toString();
        cargo.setCurrentAmountStr(currencyText);

        String fromRegister = (String)model.getValueAsString("fromRegisterField");
        cargo.setPickupAndLoanFromRegister(fromRegister);
        cargo.setPickupAndLoanToRegister(cargo.getRegister().getWorkstation().getWorkstationID());

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

}
