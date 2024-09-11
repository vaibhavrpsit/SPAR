/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CheckTrainingReentryMode.java /rgbustores_13.4x_generic_branch/2 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         6/7/2008 6:11:04 AM    Manikandan Chellapan
 *       CR#31924 Enabled audit logging for reentry mode credit tenders
 *  2    360Commerce 1.1         1/7/2008 8:14:36 AM    Chengegowda Venkatesh
 *       PABP FR40 : Changes for AuditLog incorporation
 *  1    360Commerce 1.0         1/7/2008 8:03:03 AM    Chengegowda Venkatesh 
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

/**
 * Copyright: Copyright (c) 2008<p>
 * Company:   Oracle, Inc.<p>
 *
 * <p>CheckTrainingReentryMode</p>
 *  Common logging functionalities
 *
 */

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;

public class CheckTrainingReentryMode
{

    //---------------------------------------------------------------------------
    /**
     * Returns true if register is in training or re-entry mode
     * @return boolean isTrainingRetryOn True when training or reentry mode is on
     * @param RegisterIfc register the status of the register being logged
     */
    //---------------------------------------------------------------------------
    public static boolean isTrainingRetryOn(RegisterIfc register)
    {
        WorkstationIfc workstation = null;
        boolean isTrainingRetryOn= false;

        if(register != null)
        {
            workstation = register.getWorkstation();

            if(workstation != null)
            {
                if(workstation.isTrainingMode() || workstation.isTransReentryMode())
                    isTrainingRetryOn = true;
            }
            else
            {
                isTrainingRetryOn = false;
            }
        }
        else
        {
            isTrainingRetryOn = false;
        }
        return isTrainingRetryOn;
    }

    //---------------------------------------------------------------------------
    /**
     * Returns true if register is in training mode
     * @return boolean isTrainingRetryOn True when training or reentry mode is on
     * @param RegisterIfc register the status of the register being logged
     */
    //---------------------------------------------------------------------------
    public static boolean isTrainingOn(RegisterIfc register)
    {
        WorkstationIfc workstation = null;
        boolean isTrainingOn= false;

        if(register != null)
        {
            workstation = register.getWorkstation();

            if(workstation != null)
            {
                if(workstation.isTrainingMode())
                    isTrainingOn = true;
            }
            else
            {
                isTrainingOn = false;
            }
        }
        else
        {
            isTrainingOn = false;
        }
        return isTrainingOn;
    }
}
