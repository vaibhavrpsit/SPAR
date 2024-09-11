/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/CheckForSerialisedItemAisle.java /main/6 2014/05/07 17:05:41 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole		09/03/14 - added serial number collection when balance paid in full.
 *    icole     05/06/14 - Forward port fix for not item serial number not
 *                         being captured.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/24/09 - Added check to prompt for serial number for pick up
 *                         layaway transaction only
 *    nkgautam  12/16/09 - code review updates
 *    nkgautam  12/15/09 - Aisle class creation for layaway serialisation
 *                         changes
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import java.util.Iterator;
import java.util.Vector;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.layaway.find.FindLayawayCargoIfc;

/**
 * This aisle will iterate through the items in the layaway transaction
 * and check for serialized control items.
 * @author nkgautam
 *
 */
public class CheckForSerialisedItemAisle extends LaneActionAdapter
{
    /**
     *
     */
    public void traverse(BusIfc bus)
    {
        String letter="";
        LayawayCargo cargo = (LayawayCargo)bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        boolean serializationEnabled = utility.getSerialisationProperty();
        cargo.setSerializationEnabled(serializationEnabled);
        LayawayTransactionIfc layawayTransaction = cargo.getInitialLayawayTransaction();
        int layawayOperation = cargo.getLayawayOperation();
        Vector<SaleReturnLineItemIfc> serialisedItemsVector = new Vector<SaleReturnLineItemIfc>();
        //if complete payment
        if(layawayOperation != FindLayawayCargoIfc.LAYAWAY_PICKUP &&  cargo.isFinalPayment())
        {
            layawayTransaction = (LayawayTransactionIfc)cargo.getTenderableTransaction();
        }

        Iterator lineItemIterator = layawayTransaction.getItemContainerProxy().getLineItemsIterator();
        while(lineItemIterator.hasNext())
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItemIterator.next();
            if(lineItem.isSerializedItem())
            {
                serialisedItemsVector.add(lineItem);
            }
        }

        int vectorSize = serialisedItemsVector.size();
        //Obtain serial number for Layawy pickup  or complete payment but not pickup
        //Full payment during layaway creation will allow picking up later, omitting that condition
        if (vectorSize != 0
                && layawayOperation != FindLayawayCargoIfc.LAYAWAY_UNDEFINED
                && ((layawayOperation == FindLayawayCargoIfc.LAYAWAY_PICKUP && !cargo.isFinalPayment()) 
                || ((layawayOperation != FindLayawayCargoIfc.LAYAWAY_PICKUP && cargo.isFinalPayment())))) 
        {
            AbstractTransactionLineItemIfc[] serializedItems = new AbstractTransactionLineItemIfc[vectorSize];
            serialisedItemsVector.copyInto(serializedItems);
            cargo.setSerializedItems(serializedItems);
            cargo.setSerializedItemsCounter(0);
            letter = CommonLetterIfc.SERIAL_NUMBER;
        }
        else
        {
            letter = CommonLetterIfc.NO_SERIALISED_ITEM;
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

}
