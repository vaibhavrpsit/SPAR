/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/CustomerLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/07/16 16:22:55  aachinfiev
 *   @scr 1752 - Wrong error was showing up during Layaway Offline find by Customer
 *
 *   Revision 1.4  2004/02/16 14:46:29  blj
 *   @scr - 3838 cleanup code
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:02   msg
 * Initial revision.
 * 
 *    Rev 1.3   16 Nov 2001 10:34:44   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   30 Oct 2001 16:11:00   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.1   23 Oct 2001 16:54:38   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:21:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
    Transfer necessary data from the Find Layaway service to the Customer
    service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2496281226848818618L;

    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       the cargo being passed from the Find Layaway service to the Find Customer
       service
    **/
    protected LayawayCargo layawayCargo = null;

    //----------------------------------------------------------------------
    /**
       This method saves the LayawayCargo of the bus.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        layawayCargo = (LayawayCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       This method stores attributes from the LayawayCargo to the
       CustomerMainCargo of the bus.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();
        cargo.setOperator(layawayCargo.getOperator());
        cargo.setLinkDoneSwitch(CustomerMainCargo.LINK);
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_LAYAWAY);
        cargo.setFindOnlyMode(true);
        cargo.setRegister(layawayCargo.getRegister());
        if (layawayCargo.getSeedLayawayTransaction() != null)
        {
            cargo.setTransactionID(layawayCargo.getSeedLayawayTransaction().getTransactionID());
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
    {
        String strResult = new String("Class:  " + getClass().getName() +
                                      " (Revision " +
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
