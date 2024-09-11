/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/TenderReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:33 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:51:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Nov 04 2003 11:22:02   epd
 * Updates for repackaging
 * 
 *    Rev 1.2   Oct 23 2003 17:24:44   epd
 * Updated to use renamed ADO packages
 * 
 *    Rev 1.1   Oct 17 2003 13:00:50   epd
 * Updated for new ADO tender service
 * 
 *    Rev 1.0   Aug 29 2003 16:03:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:12:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 05 2002 16:42:56   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.0   15 Jan 2002 18:49:46   cir
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Sep 21 2001 11:32:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

// foundation imports
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the Tender service to the cargo used in the POS service. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderReturnShuttle implements ShuttleIfc
{                                       // begin class TenderReturnShuttle()
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5098513208980341450L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       transaction
    **/
    protected RetailTransactionIfc transaction;
    /**
       store financial status
    **/
    protected StoreStatusIfc storeStatus;
    /**
       register financial status
    **/
    protected RegisterIfc register;

    //----------------------------------------------------------------------
    /**
       Loads cargo from tender service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()

        TenderCargo cargo = (TenderCargo) bus.getCargo();
        transaction = (RetailTransactionIfc)((ADO)cargo.getCurrentTransactionADO()).toLegacy();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads cargo for Pickup service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()

        PickupOrderCargo cargo = (PickupOrderCargo) bus.getCargo();
        cargo.setTransaction((OrderTransactionIfc) transaction);
    }                                   // end unload()

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
        String strResult = new String("Class:  TenderReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class TenderReturnShuttle
