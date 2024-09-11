/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/GetEnteredLoanCashDetailAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:34 AM   Anda D. Cadar   I18N
 *         merge
 *         
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
 *    Rev 1.0   Feb 06 2004 17:09:32   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;

//--------------------------------------------------------------------------
/**
    Save the entered loan cash detail count to the cargo.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetEnteredLoanCashDetailAisle extends PosLaneActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";    

    //----------------------------------------------------------------------
    /**
       Saves the entered loan cash detail count to the cargo and mails a  
       Continue letter. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Get the tender amount from the UI
        POSUIManagerIfc ui         = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PosCountCargo   cargo      = (PosCountCargo)bus.getCargo();

        // Save the entered amount in the cargo in case we need it later.
        CurrencyDetailBeanModel beanModel = (CurrencyDetailBeanModel)ui.getModel(POSUIManagerIfc.CURRENCY_DETAIL_LOAN);
        CurrencyIfc            enteredAmt = beanModel.getTotal();
        cargo.addCurrencyDetailBeanModel(enteredAmt.getCountryCode(), beanModel);
        cargo.setCurrentAmount(enteredAmt);

        // Save the to/from registers        
        cargo.setPickupAndLoanFromRegister(beanModel.getRegister());
        cargo.setPickupAndLoanToRegister(cargo.getRegister().getWorkstation().getWorkstationID());

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
