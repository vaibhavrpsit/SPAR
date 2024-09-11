/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaRetrieveOrder.java /main/1 2014/06/17 15:26:37 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/16/14 - CAE Order summary enhancement phase I
* abhinavs    06/16/14 - Initial Version
* abhinavs    06/16/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.domain.arts.jpa;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.transform.entity.order.OrderSummaryTransformerIfc;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.order.OrderSummary;
import oracle.retail.stores.transform.TransformerIfc;

/**
 * This class is a base class for all the retrieve order {@link #JpaRetrieveOrderSummary, 
 * #JpaRetrieveOrderSnapshot} related classes.
 * 
 * @since 14.1
 * @author abhinavs
 */
@SuppressWarnings("serial")
public abstract class JpaRetrieveOrder extends JpaDataOperation
{
    /**
     * This method uses the Spring loaded {@link OrderSummaryTransformerIfc} order summary transformer to convert 
     * the {@link OrderSummary) entities into oracle.retail.stores.domain.order.OrderSummaryEntryIfc domain objects.  The 
     * implementation for the OrderSummaryTransformerIfc is defined in the TransformerContext.xml file; bean ID 
     * is 'transformer_OrderSummaryDomainTransformer'.
     * @param summaries a list of OrderSummary entities
     * @return a list of OrderSummaryEntryIfc domain objects.
     */
    protected OrderSummaryEntryIfc[] transformOrderSummary(List<OrderSummary> summaries)
    {
        ArrayList<OrderSummaryEntryIfc> summaryEntryList = new ArrayList<OrderSummaryEntryIfc>();
        OrderSummaryTransformerIfc transformer = (OrderSummaryTransformerIfc)BeanLocator
                .getTransformerBean(TransformerIfc.TRANSF_ORDER_SUMMARY_DOMAIN_TRANSFORMER);

        for (OrderSummary summary : summaries)
        {
            OrderSummaryEntryIfc summaryEntry = transformer.toDomain(summary);
            summaryEntryList.add(summaryEntry);
        }

        OrderSummaryEntryIfc[] summaryEntries = summaryEntryList.toArray(new OrderSummaryEntryIfc[0]);
        return summaryEntries;
    }

}