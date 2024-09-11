/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/CustomerLaunchShuttle.java /main/12 2013/10/01 15:27:35 ohorne Exp $
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
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.8  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.7  2004/02/16 14:41:24  blj
 *   @scr 3838 - cleanup code
 *
 *   Revision 1.6  2004/02/12 21:34:49  mcs
 *   @scr 0 Rolled back prevision changes
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
 *    Rev 1.2   Mar 05 2003 18:16:46   DCobb
 * Change Customer offline behavior to OFFLINE_ADD.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.1   Aug 21 2002 11:21:22   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;


//--------------------------------------------------------------------------
/**
    Transfer necessary data from the POS service to the Customer service.
    <p>
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7105839298076764779L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.alterations.CustomerLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
       the current transaction ID
    **/
    protected String transactionID = null;

    /**
       alterations cargo
   **/
    AlterationsCargo alterationsCargo = null;

    //----------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        alterationsCargo = (AlterationsCargo)bus.getCargo();

        if (alterationsCargo.getTransaction() != null)
        {
            transactionID = alterationsCargo.getTransaction().getTransactionID();
        }
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
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        cargo.setTransactionID(transactionID);
        cargo.setOperator(alterationsCargo.getOperator());
        cargo.setRegister(alterationsCargo.getRegister());
        cargo.setFindOnlyMode(false);
        cargo.setLinkDoneSwitch(CustomerCargo.LINK);
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  getClass().getName() (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

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
