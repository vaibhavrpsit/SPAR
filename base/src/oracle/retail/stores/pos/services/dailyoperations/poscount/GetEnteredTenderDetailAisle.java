/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/GetEnteredTenderDetailAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 8:52:33 AM   Anda D. Cadar   I18N
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
 *    Rev 1.1   Feb 04 2004 18:39:12   DCobb
 * Added SUMMARY_COUNT_PICKUP and SUMMARY_COUNT_LOAN screens.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:56:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   May 01 2003 10:25:36   RSachdeva
 * Removing toLowerCase(locale) 
 * Resolution for POS SCR-2215: Internationlaztion- Till Functions -Pickup- Summar Count Screens
 * 
 *    Rev 1.2   Mar 04 2003 13:26:38   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 23 2002 14:07:56   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:30:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:32   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:24   msg
 * header update
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
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;

//--------------------------------------------------------------------------
/**
    Save the entered tender detail in the cargo.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetEnteredTenderDetailAisle extends PosLaneActionAdapter
{

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
       Save the entered tender detail in the cargo. 
       Mail a Continue letter.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Get the tender amount from the UI
        POSUIManagerIfc ui    = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PosCountCargo   cargo = (PosCountCargo)bus.getCargo();

        // Save the entered amount in the cargo in case we need it later.
        cargo.setOtherTenderDetailBeanModel((OtherTenderDetailBeanModel)
                                            ui.getModel(POSUIManagerIfc.OTHER_TENDER_DETAIL));
        OtherTenderDetailBeanModel beanModel =
            cargo.getOtherTenderDetailBeanModel();
        CurrencyIfc enteredAmt = beanModel.getTotal();
        cargo.setCurrentAmount(enteredAmt);

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

}
