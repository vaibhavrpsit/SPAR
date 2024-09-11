/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/EnrollNowAisle.java /rgbustores_13.4x_generic_branch/2 2011/08/19 11:19:33 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       08/17/11 - display house card referral number from the
 *                         parameter
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 24 2003 19:41:30   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.InstantCreditCallCenterBeanModel;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//--------------------------------------------------------------------------
public class EnrollNowAisle extends LaneActionAdapter
{
    /** The logger to which log messages will be sent */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.instantcredit.enrollment.EnrollNowAisle.class);

    /** revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    //----------------------------------------------------------------------
    /**
        @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        InstantCreditCallCenterBeanModel model = new InstantCreditCallCenterBeanModel();

        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        String serviceNumber = "";
        String refNumber = cargo.getReferenceNumber();
        try
        {
            serviceNumber = pm.getStringValue(ParameterConstantsIfc.TENDERAUTHORIZATION_HouseCardRefPhoneNumber);
        }
        catch(ParameterException pe)
        {
            logger.warn( pe.getStackTraceAsString());
        }

        model.setServiceNumber(serviceNumber);
        model.setReferenceNumber(refNumber);
        ui.showScreen(POSUIManagerIfc.INSTANT_CREDIT_CALL_CENTER, model);
    }
}
