/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/ShowOnScreenKeyboardAisle.java /main/6 2011/02/16 09:13:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/14/10 - call showOnScreenKeyboard(boolean)
 *    abondala  01/03/10 - update header date
 *    cgreene   12/16/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * A common aisle used to call the on screen keyboard.
 * 
 * @see UISubsystem#showOnScreenKeyboard(boolean)
 * $Revision:
 */
public class ShowOnScreenKeyboardAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = 2228419100137350366L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void traverse(BusIfc bus)
    {
        ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            if (parameterManager != null && parameterManager.getBooleanValue(ParameterConstantsIfc.BASE_OnScreenKeyboardEnabled))
            {
                UISubsystem ui = UISubsystem.getInstance();
                try
                {
                    ui.showOnScreenKeyboard(true);
                }
                catch (UIException e)
                {
                    logger.error("Unable to display popup keyboard dialog.", e);
                }
            }
        }
        catch (ParameterException e)
        {
            logger.error("Unable to check parameters for " + ParameterConstantsIfc.BASE_OnScreenKeyboardEnabled, e);
        }
    }
}
