/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ProcessOrderSaleItemsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/09/10 - take out the line that clears the external order
 *                         sale item
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/17/10 - temporarily rename the package
 *    acadar    05/17/10 - additional logic added for processing orders
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import java.util.List;


import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItemIfc;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;




/**
 * This site validates the order items by calling the
 * item validation service for each order sale item
 *
 * @author acadar
 *
 */
public class ProcessOrderSaleItemsSite extends PosSiteActionAdapter
{


    /**
     *  Serial Version UID
     */
    private static final long serialVersionUID = -3743109251138807069L;

    /**
     * For each order item marked for sale calls the item validation service
     * @param BusIfc
     */
    public void arrive(BusIfc bus)
    {
        ProcessOrderCargo cargo = (ProcessOrderCargo)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.NEXT);


        //iterate over the line items
        if(cargo.isBeginIterationOverItems())
        {
            List<ExternalOrderSaleItemIfc> externalOrderSaleItems = cargo.getExternalOrderSaleItems();
            cargo.setExternalOrderSaleItems(externalOrderSaleItems);

            int currentRecord = 0;
            try
            {
                cargo.setCurrentExternalOrderItem(externalOrderSaleItems.get(currentRecord));
                cargo.setNextRecord(currentRecord + 1);
                cargo.setBeginIterationOverItems(false);
            }
            catch (IndexOutOfBoundsException ie)
            {
//              we finished processing all the items, move one to do additional validation
                letter = new Letter(CommonLetterIfc.CONTINUE);
                //we may need to reset the data in the cargo
                resetCargoData(cargo);
            }

        }
        else
        {
            int nextRecord = cargo.getNextRecord();
            try
            {
                    cargo.setCurrentExternalOrderItem(cargo.getExternalOrderSaleItems().get(nextRecord));
                    nextRecord = nextRecord + 1;
                    cargo.setNextRecord(nextRecord);

             }
             catch (IndexOutOfBoundsException ie)
             {
                    //we finished processing all the items, move one to do additional validation
                    letter = new Letter(CommonLetterIfc.CONTINUE);
                    resetCargoData(cargo);
             }

        }

        // go to item validation station
        bus.mail(letter, BusIfc.CURRENT);

    }

    /**
     * Resets the data in the cargo
     */
    private void resetCargoData(ProcessOrderCargo cargo)
    {
        cargo.setCurrentExternalOrderItem(null);
        cargo.setNextRecord(0);
        cargo.setBeginIterationOverItems(true);
    }




}
