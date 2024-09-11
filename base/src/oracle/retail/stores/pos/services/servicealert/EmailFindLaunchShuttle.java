/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/EmailFindLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
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
 *   Revision 1.3  2004/02/12 16:51:58  mcs
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
 *    Rev 1.0   Apr 29 2002 15:02:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:47:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:05:28   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.email.EmailCargo;

//------------------------------------------------------------------------------
/**
   Launches the email find service.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EmailFindLaunchShuttle extends FinancialCargoShuttle
{
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.servicealert.EmailFindLaunchShuttle.class);

    public static final String SHUTTLENAME = "EmailFindLaunchShuttle";

    protected ServiceAlertCargo cargo = null;

    //--------------------------------------------------------------------------
    /**
       Load the calling service's cargo.

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
        emailCargo.setStoreID(cargo.getStoreStatus().getStore().getStoreID());

    }
}
