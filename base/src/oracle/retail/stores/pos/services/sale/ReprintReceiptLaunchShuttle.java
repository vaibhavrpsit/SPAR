/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ReprintReceiptLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:43 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.2   Nov 07 2003 12:37:48   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;
//java imports
// foundation imports
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.reprintreceipt.ReprintReceiptCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies the contents of one abstract financial cargo to
    ReprintReceiptCargo. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReprintReceiptLaunchShuttle implements ShuttleIfc
{                                       // begin class ReprintReceiptLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 304253887819752270L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
         Sale cargo
     **/
    protected SaleCargoIfc saleCargo;
    //----------------------------------------------------------------------
    /**
        Copies information from the cargo used in the service.  <P>
        @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        // get cargo reference and extract attributes
        saleCargo = (SaleCargoIfc) bus.getCargo();

    }
    //----------------------------------------------------------------------
    /**
        Copies information to the cargo used in the service. <P>
        @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin load()
        // get cargo reference and set attributes
        WorkstationIfc  worksation = saleCargo.getRegister().getWorkstation();
        
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
        cargo.setRegisterID(worksation.getWorkstationID());
        cargo.setBusinessDate(saleCargo.getStoreStatus().getBusinessDate());
        cargo.setOperator(saleCargo.getOperator());
        cargo.setTrainingModeFlag(worksation.isTrainingMode());
        cargo.setLastReprintableTransactionID(saleCargo.getLastReprintableTransactionID());
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult =
          Util.classToStringHeader("ReprintReceiptLaunchShuttle",
                                   revisionNumber,
                                   hashCode()).toString();
        // pass back result
        return(strResult);
    }                                   // end toString()


}                                       // end class ReprintReceiptLaunchShuttle
