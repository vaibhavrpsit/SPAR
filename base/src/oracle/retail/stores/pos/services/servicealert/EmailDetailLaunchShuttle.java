/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/EmailDetailLaunchShuttle.java /main/14 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:47 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 07 2002 11:36:26   jriggins
 * Using ServiceSelectSpec.NotYetAvailable bundle tag.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:02:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:05:32   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

// Java imports
import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.email.EmailCargo;

//------------------------------------------------------------------------------
/**
    The Email Detail Launch Shuttle carries the data required by the E-Mail
    service from the Service Alert service to the Email Detail Service.

    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class EmailDetailLaunchShuttle extends FinancialCargoShuttle
{
    protected static final String NOT_YET_AVAILABLE_TAG = "NotYetAvailable";
    protected static final String NOT_YET_AVAILABLE_TEXT = "NOT YET AVAILABLE";

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.servicealert.EmailDetailLaunchShuttle.class);

    public static final String SHUTTLENAME = "EmailDetailLaunchShuttle";

    protected ServiceAlertCargo cargo = null;

    //--------------------------------------------------------------------------
    /**
       Load the data from the calling service's cargo.

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        super.load(bus);
        cargo = (ServiceAlertCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       Load the data into the child service's cargo.

       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        super.unload(bus);
        EmailCargo emailCargo = (EmailCargo) bus.getCargo();
        emailCargo.setSelectedMessage(cargo.getSelectedMessage());
        emailCargo.setStoreID(cargo.getStoreStatus().getStore().getStoreID());

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        UtilityManagerIfc utility = 
          (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);        
        String cashierID = 
          utility.retrieveText("ServiceSelectSpec",
                               BundleConstantsIfc.SERVICE_ALERT_BUNDLE_NAME,
                               NOT_YET_AVAILABLE_TAG, 
                               NOT_YET_AVAILABLE_TEXT);
          
        if (cargo.getOperator() != null && cargo.getEmployeeID() != null)
        {
            cashierID = cargo.getOperator().getEmployeeID();
        }
        emailCargo.setCashierID(cashierID);

    }
}
