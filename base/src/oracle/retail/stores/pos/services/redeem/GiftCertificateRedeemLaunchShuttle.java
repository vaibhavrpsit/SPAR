/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/GiftCertificateRedeemLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  02/04/10 - Added register in the cargo
 *    abondala  01/03/10 - update header date
 *    sgu       01/08/09 - propagate operator to gift certificate redeem cargo
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:11:15 PM  Robert Pearse
 *
 *Revision 1.2  2004/09/23 00:07:16  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.1  2004/04/26 19:28:40  crain
 *@scr 4553 Redeem Gift Certificate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;


import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
//--------------------------------------------------------------------------
/**
  * This shuttle copies information from the cargo used
  * in the redeem service to the cargo used in the gift certificate redeem service. <p>
  * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GiftCertificateRedeemLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7475772777270568675L;

    /**
     * redeem cargo
     */
    protected RedeemCargo redeemCargo = null;

    //----------------------------------------------------------------------
    /**
     Loads cargo from redeem service. <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        redeemCargo = (RedeemCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     Loads cargo for gift certificate redeem service. <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        RedeemCargo giftCertificateRedeemCargo = (RedeemCargo) bus.getCargo();
        giftCertificateRedeemCargo.setTenderAttributes(redeemCargo.getTenderAttributes());
        giftCertificateRedeemCargo.setRedeemTypeSelected(redeemCargo.getRedeemTypeSelected());
        giftCertificateRedeemCargo.setOperator(redeemCargo.getOperator());
        giftCertificateRedeemCargo.setRegister(redeemCargo.getRegister());
    }
}
