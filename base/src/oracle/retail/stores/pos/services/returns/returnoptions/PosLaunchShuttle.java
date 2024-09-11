/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/PosLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Returns flow refactor: deprected obsolete class.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:08 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/03/17 16:00:16  epd
 *   @scr 3561 Bug fixing and refactoring
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:42:36   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:06:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
    This shuttle transfers Return Infomation to the POS Service.
    <P>
    The Return Services can used in two different ways:
    <P>
        1. It can be called from the POS service.  In this case it
           will RETURN the information it collects to the POS
           through a shuttle in the POS Service.
    <P>
        2. It can be called from the CrossReach.  In this case it
           launches the POS Service in order to complete the return.
    <P>
    This suttle is used in the second case.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated in 13.3 no longer used.
**/
//--------------------------------------------------------------------------
public class PosLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7866228484137757566L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnoptions.PosLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
     * returns options cargo
     */
    protected ReturnOptionsCargo roCargo = null;

    //----------------------------------------------------------------------
    /**
       Store information from the Parrent Cargo.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        super.load(bus);
        roCargo = (ReturnOptionsCargo) bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Transfer Parent infromation to the Child cargo.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        super.unload(bus);

        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.setTransaction(roCargo.getTransaction());
        if (roCargo.getOriginalTransaction() != null)
        {
            cargo.addOriginalReturnTransaction(roCargo.getOriginalTransaction());
        }

        // The sales associate in the return cargo is for the return, not
        // the current transactions action; The for crossreach the operator
        // is the sales associate.
        cargo.setEmployee(cargo.getOperator());

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class: "    + getClass().getName() +
                       "(Revision " + getRevisionNumber()  +
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

}
