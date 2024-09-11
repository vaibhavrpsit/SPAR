/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/GiftCardLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 |    4    360Commerce 1.3         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |         31482 - Updated the journalResponse method of GetResponseSite to
 |         intelligently journal entries with the appropriate journal type
 |         (Trans or Not Trans). Code Review by Tony Zgarba.
 |    3    360Commerce 1.2         3/31/2005 4:28:17 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:21:54 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:11:14 PM  Robert Pearse   
 |   $
 |   Revision 1.6  2004/09/23 00:07:12  kmcbride
 |   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |   Revision 1.5  2004/06/29 19:59:03  lzhao
 |   @scr 5477: add gift card inquiry in training mode.
 |
 |   Revision 1.4  2004/04/07 21:10:09  lzhao
 |   @scr 3872: gift card redeem and revise gift card activation
 |
 |   Revision 1.3  2004/02/12 16:50:26  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:51:10  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 |   updating to pvcs 360store-current
 | 
 |    Rev 1.0   Aug 29 2003 15:59:44   CSchellenger
 | Initial revision.
 | 
 |    Rev 1.0   Apr 29 2002 15:21:32   msg
 | Initial revision.
 | 
 |    Rev 1.0   Mar 18 2002 11:33:06   msg
 | Initial revision.
 | 
 |    Rev 1.1   25 Oct 2001 17:41:08   baa
 | cross store inventory feature
 | Resolution for POS SCR-230: Cross Store Inventory
 |
 |    Rev 1.0   24 Oct 2001 18:20:30   baa
 | Initial revision.
 | Resolution for POS SCR-230: Cross Store Inventory
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.inquiry.giftcardinquiry.InquiryCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the Inquiry Options service back to
    the Modify Item service.
    @version $KW; $Ver; $EKW;
**/
//--------------------------------------------------------------------------
public class GiftCardLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6504052437805596262L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$KW; $Ver; $EKW;";

    /** Parent service's cargo **/
    protected InquiryOptionsCargo optionsCargo = null;

    //----------------------------------------------------------------------
    /**
        This shuttle copies information from the GiftCard Inquiry service back
        to the Inquiry Options Item service.
        <P>
        @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        optionsCargo = (InquiryOptionsCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Copies the new giftcard to the cargo for the Inquiry options service.
        <P>
        @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
            InquiryCargo cargo = (InquiryCargo) bus.getCargo();
            cargo.setGiftCard(optionsCargo.getGiftCard());
            cargo.setRegister(optionsCargo.getRegister());
            cargo.setTransactionInProgress(optionsCargo.getTransaction() != null);
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
        String strResult = new String("Class:  ItemInquiryReturnShuttle (Revision " +
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
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Main to run a test..
        <P>
        @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        GiftCardLaunchShuttle obj = new GiftCardLaunchShuttle();

        // output toString()
        System.out.println(obj.toString());
    }
}
