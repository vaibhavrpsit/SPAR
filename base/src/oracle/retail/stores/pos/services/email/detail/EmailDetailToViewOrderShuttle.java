/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/detail/EmailDetailToViewOrderShuttle.java /main/13 2012/07/13 12:43:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/13/12 - remove order search type that is no longer used
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
 *   Revision 1.4  2004/04/09 16:56:03  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:38  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 28 2002 10:08:18   jriggins
 * Introduced the OrderCargo.serviceType property complete with accessor and mutator methods.  Replaced places where service names were being compared (via String.equals()) to String constants in OrderCargoIfc with comparisons to the newly-created serviceType constants which are ints.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:25:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:31:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 11:17:26   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.detail;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;

//------------------------------------------------------------------------------
/**
    The Email Detail to View Order shuttle carries the data required by the E-Mail
    service from the Email Detail service to the View Order service.

    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class EmailDetailToViewOrderShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.email.detail.EmailDetailToViewOrderShuttle.class);

    /*
      class name constant
    */
    public static final String SHUTTLENAME = "EmailDetailToViewOrderShuttle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /*
      email detail cargo
    */
    EmailCargo detailCargo = null;

    /*
      order cargo
    */
    OrderCargo orderCargo = null;

    //--------------------------------------------------------------------------
    /**
       Load the shuttle with data from Email Detail cargo.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        super.load(bus);
        detailCargo = (EmailCargo)bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       Unload the shuttle data into Order cargo.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        super.unload(bus);

        orderCargo = (OrderCargo)bus.getCargo();
        orderCargo.setOrderID(detailCargo.getOrderID());
        orderCargo.setEMessage(detailCargo.getSelectedMessage());
        orderCargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_ORDER_ID);

        //set the service type to view so the Order status is not updated if
        //order is printed
        orderCargo.setServiceType(OrderCargoIfc.SERVICE_VIEW_TYPE);

    }
}
