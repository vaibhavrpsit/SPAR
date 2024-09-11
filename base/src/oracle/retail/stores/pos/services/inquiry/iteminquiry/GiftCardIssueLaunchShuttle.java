/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/GiftCardIssueLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/08/10 - fix item interactive screen prompts to include item
 *                         # and description
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:01 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     10/31/2005 11:57:05    Deepanshu       CR
 *         6092: Set the Sales Associate in GiftCardCargo
 *    3    360Commerce1.2         3/31/2005 15:28:16     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:54     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:13     Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 19 2003 15:21:44   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.0   Dec 12 2003 14:30:44   lzhao
 * Initial revision.
 *
 *    Rev 1.0   Dec 08 2003 09:11:08   lzhao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the Sale service to the cargo used in the Gift Card Option service. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GiftCardIssueLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9082958892545602692L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.inquiry.iteminquiry.GiftCardIssueLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Gift card cargo
     */
    protected ItemInquiryCargo itemInquiryCardCargo = null;
    //----------------------------------------------------------------------
    /**
       Loads cargo from Sale service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the retail transaction
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
        itemInquiryCardCargo = (ItemInquiryCargo)bus.getCargo();

   }  // end load()

    //----------------------------------------------------------------------
    /**
       Loads data into GiftCardIssue service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the retail transaction
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
        GiftCardCargo cargo = (GiftCardCargo) bus.getCargo();

        cargo.setStoreStatus(itemInquiryCardCargo.getStoreStatus());
        cargo.setRegister(itemInquiryCardCargo.getRegister());
        cargo.setOperator(itemInquiryCardCargo.getOperator());
        cargo.setCustomerInfo(itemInquiryCardCargo.getCustomerInfo());
        cargo.setTenderLimits(itemInquiryCardCargo.getTenderLimits());
        cargo.setTransaction((SaleReturnTransactionIfc)itemInquiryCardCargo.getTransaction());
        cargo.setPLUItem(itemInquiryCardCargo.getPLUItem());
        cargo.setSalesAssociate(itemInquiryCardCargo.getSalesAssociate());
        cargo.setGiftCardAmount(itemInquiryCardCargo.getExternalPrice());

   }  // end unload()

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  GiftCardIssueLaunchShuttle (Revision " +
                                      revisionNumber + ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

}                                       // end class GiftCardIssueLaunchShuttle
