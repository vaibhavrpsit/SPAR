/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/PINPadNeededAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:04 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/05/04 20:05:15  bwf
 *   @scr 3377 Debit Reversal Work
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderDebitADO;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
     This aisle determines whether or not we still need to get a pin
     pad entry.
     $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class PINPadNeededAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Send the Open letter
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        VoidCargo cargo = (VoidCargo)bus.getCargo();
        VoidTransactionADO voidTxn = (VoidTransactionADO) cargo.getCurrentTransactionADO();
        
        String letterName = "Continue";        

        TenderADOIfc[] voidTenders = voidTxn.getTenderLineItems(TenderLineItemCategoryEnum.VOID_AUTH_PENDING);
        for(int i = 0; i < voidTenders.length; i++)
        {
            if(voidTenders[i] instanceof TenderDebitADO &&
               !((TenderDebitADO)voidTenders[i]).hasPIN())
            {
                letterName = "PinPad";
                break;
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }

}
