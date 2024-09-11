/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/patriot/CaptureIRSCustomerUISite.java /main/13 2013/12/20 10:27:42 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  12/11/13 - fix null dereferences
 *    abondala  09/04/13 - initialize collections
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    2    360Commerce 1.1         12/7/2006 2:24:19 PM   Brett J. Larsen CR
 *         21928 - no poslogs for money orders above PAT limit
 *
 *         the cash-received date was not being saved anywhere
 *
 *         this caused the code to throw null-pointer exception when saving
 *         the irs customer to the database
 *
 *         (using current date (not transaction date) was approved by FA
 *    1    360Commerce 1.0         12/13/2005 4:47:05 PM  Barry A. Pape
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.patriot;

import java.util.HashMap;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.CaptureIRSCustomerTDO;
import oracle.retail.stores.pos.services.tender.tdo.TenderOptionsTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureIRSCustomerBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site displays the PAT Customer Information screen and retrieves the
 * user's input to that screen.
 *
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class CaptureIRSCustomerUISite extends PosSiteActionAdapter
{

    /**
     * Display the PAT Customer Information screen
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        boolean transReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();

        // build bean model helper
        POSBaseBeanModel beanModel = null;
        try
        {
            TDOUIIfc tdo = null;
            tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.CaptureIRSCustomer");

            // Create map for TDO
            HashMap<String,Object> attributeMap = new HashMap<String,Object>(4);
            attributeMap.put(TenderOptionsTDO.BUS, bus);
            attributeMap.put(TenderOptionsTDO.TRANSACTION,
                    ((AbstractFinancialCargo)bus.getCargo()).getCurrentTransactionADO());

            attributeMap.put(TenderOptionsTDO.TRANSACTION_REENTRY_MODE, transReentryMode);
            attributeMap.put(TenderOptionsTDO.SWIPE_ANYTIME, (cargo.getPreTenderMSRModel() != null));
            beanModel = tdo.buildBeanModel(attributeMap);

            if (cargo.getCurrentTransactionADO().getIRSCustomer() != null)
            {
                ((CaptureIRSCustomerTDO)tdo).customerToModel((CaptureIRSCustomerBeanModel)beanModel, bus, cargo
                        .getCurrentTransactionADO().getIRSCustomer());
            }
            else if (cargo.getCurrentTransactionADO().getCustomer() != null)
            {
                ((CaptureIRSCustomerTDO)tdo).customerToModel((CaptureIRSCustomerBeanModel)beanModel, bus, cargo
                        .getCurrentTransactionADO().getCustomer());
            }
        }
        catch (TDOException tdoe)
        {
            logger.error("Problem creating Capture IRS Customer screen: " + tdoe.getMessage());
        }

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.CAPTURE_IRS_CUSTOMER, beanModel);
    }

    /**
     * Capture user input from PAT Customer Information screen
     *
     * @param bus the bus departing from this site
     */
    @Override
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();

            TDOUIIfc tdo = null;
            // Create the tdo object.
            try
            {
                tdo = (TDOUIIfc)TDOFactory.create("tdo.tender.CaptureIRSCustomer");
            }
            catch (TDOException tdoe)
            {
                tdoe.printStackTrace();
            }

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // Copy the model back onto the customer.
            CaptureIRSCustomerBeanModel model = (CaptureIRSCustomerBeanModel)ui
                    .getModel(POSUIManagerIfc.CAPTURE_IRS_CUSTOMER);

            IRSCustomerIfc irsCustomer = DomainGateway.getFactory().getIRSCustomerInstance();

            if (tdo != null)
            {
                ((CaptureIRSCustomerTDO)tdo).modelToCustomer(model, bus, irsCustomer);
            }
            
            irsCustomer.setDateCashReceived(new EYSDate());

            cargo.getCurrentTransactionADO().setIRSCustomer(irsCustomer);
        }
    }
}
