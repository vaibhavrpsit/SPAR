/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/SetOpenTillOfflineAccessAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   02/24/10 - add call to setAccessFunctionTitle
 *    cgreene   02/16/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * This aisle sets the cargo's function id to {@link RoleFunctionIfc#START_OF_DAY}
 * so that tills being opened while offline will require a higher access role.
 * <p>
 * Will mail an {@link CommonLetterIfc#OK}.
 *
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SetOpenTillOfflineAccessAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = -3544878137267900827L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Key to resourced text. Equals "DialogSpec.ConfirmOfflineRegisterStatus.title".
     */
    public static final String KEY_OPEN_TILL_OFFLINE_TITLE = "DialogSpec.ConfirmOfflineRegisterStatus.title";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.START_OF_DAY);

        UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String title = util.retrieveDialogText(KEY_OPEN_TILL_OFFLINE_TITLE, "Open Till While Offline");
        if (Util.isEmpty(title))
        {
            Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            title = Role.getFunctionTitle(userLocale, RoleFunctionIfc.OPEN_TILL);
        }
        cargo.setAccessFunctionTitle(title);

        bus.mail(CommonLetterIfc.OK);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
