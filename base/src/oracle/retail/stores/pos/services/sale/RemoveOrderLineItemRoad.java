
package oracle.retail.stores.pos.services.sale;


import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * @version $Revision: /main/1 $
 */
@SuppressWarnings("serial")
public class RemoveOrderLineItemRoad extends PosLaneActionAdapter
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/1 $";

    /**
     * Removes the selected item from the transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        int size = cargo.getOrderLineItems().size();
        for ( int i=0; i<size; i++ )
        {
            cargo.getOrderLineItems().remove(0);
        }
        
        AbstractTransactionLineItemIfc[] lineItems = cargo.getTransaction().
                getItemContainerProxy().getLineItems();
        for(int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItems[i];
            srli.setSelectedForItemModification(false);
        }
    }
}

