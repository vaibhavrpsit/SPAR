/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/OpenTillReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:48 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/05/13 14:10:00  awilliam
 *   @scr 4314 selcting Service Alert crashes Pos
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:10:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   19 Nov 2001 15:37:04   jbp
 * removed operator being reset in cargo.
 * Resolution for POS SCR-206: Layaway Delete refund by check missing on Tender Summary
 *
 *    Rev 1.0   Sep 21 2001 11:13:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This service copies any needed information from the cargo used
    in one service to another service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OpenTillReturnShuttle extends FinancialCargoShuttle
{                                       // begin class OpenTillReturnShuttle
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.common.OpenTillReturnShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Copies information to the cargo used in the service. <P>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // execute FinancialCargoShuttle class unload
        super.unload(bus);

        // get cargo reference and set attributes
        ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object. <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  OpenTillReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}                                       // end class OpenTillReturnShuttle
