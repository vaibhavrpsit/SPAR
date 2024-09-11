/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/CheckOrderReturnItemsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    acadar    05/17/10 - temporarily rename the package
 *    acadar    05/17/10 - additional logic added for processing orders
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import java.util.List;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;




/**
 * This site checks if the order has any line items marked for return
 *
 * @author acadar
 *
 */
public class CheckOrderReturnItemsSite extends PosSiteActionAdapter
{
    /**
     *  Serial version UID
     */
    private static final long serialVersionUID = 3591104347717529128L;


    /**
     * Checks to see if any line items marked for return. If so,
     * copies them into an array and sends them to the return station
     */
    @SuppressWarnings("unchecked")
    public void arrive(BusIfc bus)
    {

        Letter letter = new Letter(CommonLetterIfc.SALE);

        ProcessOrderCargo cargo = (ProcessOrderCargo)bus.getCargo();

        List<ExternalOrderItemIfc> externalOrderReturnItems = cargo.getExternalOrderReturnItems();
        if (externalOrderReturnItems != null &&
            externalOrderReturnItems.size()> 0)
        {
            cargo.setExternalOrderReturnItems(externalOrderReturnItems);

            //call the return service
            letter = new Letter(CommonLetterIfc.RETURN);
        }

        //else call sale
        bus.mail(letter, BusIfc.CURRENT);

    }




}
