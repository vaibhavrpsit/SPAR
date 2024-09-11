/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/CaptureCustomerInfoLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:48 PM  Robert Pearse   
 * $
 * Revision 1.5  2004/09/23 00:07:10  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.4  2004/07/13 14:07:02  khassen
 * @scr 6193 - Checked/added non-null customer in Transaction object.
 *
 * Revision 1.3  2004/06/18 14:43:55  khassen
 * @scr 5684 - Feature enhancements for capture customer use case: needed to set the transaction in cargo.
 *
 * Revision 1.2  2004/06/14 23:35:26  lzhao
 * @scr 4670: fix shipping charge calculation.
 *
 * Revision 1.1.2.1  2004/06/04 19:17:31  aachinfiev
 * Merge from HEAD (June 4, 2004) onto rediron_POSInvIntegration branch
 *
 * Revision 1.1  2004/06/02 19:06:51  lzhao
 * @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 * Revision 1.1  2004/05/26 16:37:47  lzhao
 * @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;


import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.pos.services.tender.capturecustomerinfo.CaptureCustomerInfoCargo;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
 * This shuttle is used to go to the customer service. $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class CaptureCustomerInfoLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7574298528577519673L;

    /** revision number * */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "CaptureCustomerInfoLaunchShuttle";
 
    protected ItemCargo itemCargo;
    
    //----------------------------------------------------------------------
    /**
     * Load a copy of ItemCargo into the shuttle for use in unload().
     * <p>
     * 
     * @param bus
     *            the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        itemCargo = (ItemCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Make a CaptureCustomerInfoCargo and populate it.
     * <p>
     * 
     * @param bus
     *            the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
 
        cargo.setTenderType(TransactionConstantsIfc.TYPE_SEND);
        if (itemCargo.getTransaction() != null)
        {
            cargo.setTransaction(itemCargo.getTransaction());
            cargo.setCustomer(itemCargo.getTransaction().getCustomer());
        }
    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class:  CaptureCustomerInfoLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
