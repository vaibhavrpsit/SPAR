/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/PopupDialogAction.java /main/1 2013/02/21 12:05:47 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  02/21/13 - Abstract action class for popup dialogs
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services;

import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;
import oracle.retail.stores.pos.ui.timer.ScreenTimeoutIfc;

public abstract class PopupDialogAction implements ActionListener
{
    private static final Logger logger = Logger.getLogger(PopupDialogAction.class);

    /**
     * Display the specified screen as a popup dialog. This method uses the
     * UISubsystem instead of the UIManager to avoid a deadlock issue where two
     * buses try to use the UIManager at the same time.
     * 
     * @param bus
     * @param screenName
     * @param beanModel
     */
    protected void showDialog(BusIfc bus, String screenName, UIModelIfc beanModel)
    {
        if (beanModel instanceof ScreenTimeoutIfc)
        {
            // Add timer model for popup timeout
            boolean hasTransaction = false;
            if (bus.getCargo() instanceof AbstractFinancialCargoIfc)
            {
                hasTransaction = isTransactionOpen(bus.getCargo());
            }

            ((ScreenTimeoutIfc)beanModel).setTimerModel(new DefaultTimerModel(bus, hasTransaction, null));
        }

        UISubsystem ui = UISubsystem.getInstance();
        try
        {
            ui.showDialog(screenName, beanModel);
        }
        catch (UIException ex)
        {
            logger.error("Unable to display popup dialog for " + screenName, ex);
        }
    }

    /**
     * Returns true if a transaction object is present in the cargo.
     * 
     * @param AbstractFinancialCargoIfc
     * @return boolean
     */
    private boolean isTransactionOpen(CargoIfc cargo)
    {
        Class<?> cargoClass = cargo.getClass();
        boolean isTransactionOpen = false;
        try
        {
            Method getTransaction = cargoClass.getMethod("getTransaction", null);
            isTransactionOpen = (getTransaction.invoke(cargo, null) != null);
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e)
        {
            // Do nothing
        }

        return isTransactionOpen;
    }

}
