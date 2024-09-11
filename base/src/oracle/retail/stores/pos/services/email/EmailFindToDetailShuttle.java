/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/EmailFindToDetailShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:10  mcs
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
 *    Rev 1.0   Aug 29 2003 15:58:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 11:16:28   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//------------------------------------------------------------------------------
/**
    The Email Find To Detail Shuttle carries the data required by the E-Mail
    service from the Email Find service to the Email Detail Service.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EmailFindToDetailShuttle
    extends FinancialCargoShuttle
{
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.email.EmailFindToDetailShuttle.class);

    /*
      class name constant
    */
    public static final String SHUTTLENAME = "EmailFindToDetailLaunchShuttle";

    /*
      email find cargo
    */
    EmailCargo findCargo = null;

    /*
      email detail cargo
    */
    EmailCargo detailCargo = null;

    //--------------------------------------------------------------------------
    /**
       Load the shuttle with data from Email Find cargo.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        super.load(bus);
        findCargo = (EmailCargo)bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       Unload the shuttle data into Email Detail cargo.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        super.unload(bus);
        detailCargo = (EmailCargo)bus.getCargo();
        detailCargo.setCashierID(findCargo.getCashierID());
        detailCargo.setSelectedMessage(findCargo.getSelectedMessage());

    }
}
