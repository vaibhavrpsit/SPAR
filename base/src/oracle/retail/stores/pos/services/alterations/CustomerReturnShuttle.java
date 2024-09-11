/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/CustomerReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:15 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 21 2002 11:21:22   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// foundation imports

// Foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//--------------------------------------------------------------------------
/**
    Transfer necessary data from the Customer service back to the POS service.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7894305100203802791L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.alterations.CustomerReturnShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       indicates whether to link the customer to the current transaction
    **/
    protected boolean link;
    /**
       the customer to link to the current transaction
    **/
    protected CustomerIfc customer;
    /**
       customer main cargo
    **/
    protected CustomerMainCargo customerCargo = null;
    //----------------------------------------------------------------------
    /**
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // save the cargo from the CustomerIfc service in an instance variable
        customerCargo = (CustomerMainCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        AlterationsCargo alterationsCargo = (AlterationsCargo)bus.getCargo();
        customer = customerCargo.getCustomer();
        link = customerCargo.isLink();

        if (link)
        {
            alterationsCargo.setCustomer(customer);
            alterationsCargo.setCustomerLinkRefreshUI(true);
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  CustomerReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
