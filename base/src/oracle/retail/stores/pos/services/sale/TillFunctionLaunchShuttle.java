/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/TillFunctionLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 07 2003 12:38:30   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillOptionsCargo;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the POS service to the Till Options service. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillFunctionLaunchShuttle extends FinancialCargoShuttle
{                                       // begin class TillFunctionLaunchShuttle
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        class name constant
    **/
    public static final String SHUTTLENAME = "TillFunctionLaunchShuttle";

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.TillFunctionLaunchShuttle.class);
    
    /**
     *  create new PosCargo
     */
    protected SaleCargoIfc saleCargo;

    //--------------------------------------------------------------------------
    /**
        Copies information from the cargo used in the POS service. <P>
        @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
          super.load(bus);
          saleCargo = (SaleCargoIfc) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
        Copies information to the cargo used in the Till Options service. <P>
        @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TillOptionsCargo toCargo = (TillOptionsCargo) bus.getCargo();
        toCargo.setSelectionType("Functions");
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}                                       // end class TillFunctionLaunchShuttle
