/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/DisplayItemInfoAction.java /main/4 2014/05/22 09:40:21 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     05/21/14 - Changes to prevent NPE due to line item list being
 *                         null.
 *    mchellap  02/19/13 - Added timer model for screen timeout
 *    cgreene   10/26/12 - Change item description to a urllabel
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.awt.event.ActionEvent;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.TierTechnicianIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PopupDialogAction;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

import org.apache.log4j.Logger;

/**
 * An action that will display the {@link ShowItemSite} in a popup
 * dialog. This action expects that the {@link TierTechnicianIfc} is running
 * as one of the {@link Dispatcher}'s local technicians.
 *
 * @author cgreene
 * @since 14.0
 */
public class DisplayItemInfoAction extends PopupDialogAction
{
    private static final Logger logger = Logger.getLogger(DisplayItemInfoAction.class);

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            TierTechnicianIfc tierTechnician = (TierTechnicianIfc)Dispatcher.getDispatcher().getLocalTechnician("APPLICATION");
            if (tierTechnician != null)
            {
                BusIfc[] buses = tierTechnician.getBuses();
                if (buses.length > 0)
                {
                    for (int i = 0; i < buses.length; i++)
                    {
                        if (buses[i].getCargo() instanceof SaleCargoIfc)
                        {
                            try
                            {
                                UIModelIfc model = UISubsystem.getInstance().getModel();
                                if (model instanceof LineItemsModel)
                                {
                                    LineItemsModel lineItemModel = (LineItemsModel)model;
                                    int idx = lineItemModel.getItemModifiedIndex();
                                    if (idx >= 0 && lineItemModel.getLineItems().length > idx)
                                    {
                                        AbstractTransactionLineItemIfc lineItem = lineItemModel.getLineItems()[idx];
                                        if (lineItem instanceof SaleReturnLineItemIfc)
                                        {
                                            // Setup bean model information for the UI to display
                                            ItemInfoBeanModel beanModel = ShowItemSite.buildItemInfoModel(buses[i], (SaleReturnLineItemIfc)lineItem);                                            
                                            showDialog(buses[i], POSUIManagerIfc.ITEM_INFO_DIALOG, beanModel);                                            
                                        }
                                        else
                                        {
                                            logger.warn("Line item selected was not a SaleReturnLineItemIfc.");
                                        }
                                    }
                                    else
                                    {
                                        logger.warn("No line item selected.");
                                    }
                                }
                                else
                                {
                                    logger.warn("SaleCargo did not have a LineItemModel.");
                                }
                            }
                            catch (Exception ex)
                            {
                                logger.error("Could not display item info dialog.", ex);
                            }
                        }
                    }
                }
                else
                {
                    logger.warn("Could not find an active bus to display item info with.");
                }
            }
            else
            {
                logger.error("TierTechnician is not configured for \"APPLICATION\" for the Dispatcher.");
            }
        }
        catch (TechnicianNotFoundException ex)
        {
            logger.error("TierTechnician is not configured for \"APPLICATION\" for the Dispatcher.", ex);
        }
    }

}
