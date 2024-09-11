/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/GiftCertificateRedeemReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:15 PM  Robert Pearse   
 *
 *Revision 1.4  2004/09/23 00:07:16  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.3  2004/07/22 22:38:40  bwf
 *@scr 3676 Add tender display to ingenico.
 *
 *Revision 1.2  2004/04/29 23:48:50  crain
 *@scr 4553 Redeem Gift Certificate
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
 This shuttle copies information from the cargo used
 in the gift certificate redeem service to the cargo used in the redeem service. <p>
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class GiftCertificateRedeemReturnShuttle implements ShuttleIfc
{                                       
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1458467735250882198L;

    /**
     * gift certificate redeem cargo
     */
    protected RedeemCargo giftCertificateRedeemCargo = null;
    
    //----------------------------------------------------------------------
    /**
     Loads cargo from gift certificate redeem service. <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   
        giftCertificateRedeemCargo = (RedeemCargo) bus.getCargo();
    }                                   

    //----------------------------------------------------------------------
    /**
     Loads cargo for redeem service. <P>
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   
        RedeemCargo cargo = (RedeemCargo) bus.getCargo();
        
        if ( giftCertificateRedeemCargo.getTenderADO() != null )
        {  
            cargo.setTenderADO(giftCertificateRedeemCargo.getTenderADO());
            cargo.setLineDisplayTender(giftCertificateRedeemCargo.getLineDisplayTender());
            cargo.setTenderAttributes(giftCertificateRedeemCargo.getTenderAttributes());
        }
    }                                   
}
