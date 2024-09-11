/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/GiftCardInquiryLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/04/07 21:10:08  lzhao
 *   @scr 3872: gift card redeem and revise gift card activation
 *
 *   Revision 1.3  2004/02/12 16:51:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:16:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:28:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;
// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.inquiry.giftcardinquiry.InquiryCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the modify item service to the cargo used in the gift card inquiry service. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GiftCardInquiryLaunchShuttle /*extends FinancialCargoShuttle*/ implements ShuttleIfc
{                                       // begin class GiftCardInquiryLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4309761993061654375L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.GiftCardInquiryLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       transaction
    **/
    protected GiftCardIfc giftCard;

    //----------------------------------------------------------------------
    /**
       Loads cargo from modify item service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the gift card to inquire upon
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

        ItemCargo cargo = (ItemCargo) bus.getCargo();
        giftCard = cargo.getGiftCard();

    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads data into gift card inquiry service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the gift card to inquire upon
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

        InquiryCargo cargo = (InquiryCargo) bus.getCargo();
        cargo.setGiftCard(giftCard);

    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.  <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  GiftCardInquiryLaunchShuttle (Revision " +
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

}                                       // end class GiftCardInquiryLaunchShuttle
