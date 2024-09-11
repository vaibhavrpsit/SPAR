/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/ItemInquiryReturnShuttle.java /main/12 2012/10/04 09:56:50 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      10/03/12 - set itemFromWebStore when going through different
 *                         flow.
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:41 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:26  mcs
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
 *    Rev 1.1   Jan 09 2004 12:54:28   lzhao
 * set transaction back, remove comments, add date
 * Resolution for 3666: Eltronic Journal for Gift Card Issue  and Reload not Correct
 * 
 *    Rev 1.0   Aug 29 2003 15:59:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Oct 14 2002 16:09:44   DCobb
 * Added alterations service to item inquiry service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * 
 *    Rev 1.0   Apr 29 2002 15:21:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:18   msg
 * Initial revision.
 * 
 *    Rev 1.2   Feb 05 2002 16:42:28   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.1   25 Oct 2001 17:41:24   baa
 * cross store inventory feature
 * Resolution for POS SCR-230: Cross Store Inventory
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry;

// foundation imports
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the Item Inquiry service back to
    the Modify Item service.
    @version $KW; $Ver; $EKW;
**/
//--------------------------------------------------------------------------
public class ItemInquiryReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5555241803980173156L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$KW; $Ver; $EKW;";
    // Child service's cargo
    protected ItemInquiryCargo itemInquiryCargo = null;

    //----------------------------------------------------------------------
    /**
        This shuttle copies information from the Item Inquiry service back
        to the Modify Item service.
        <P>
        @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        itemInquiryCargo = (ItemInquiryCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Copies the new item to the cargo for the Modify Item service.
        <P>
        @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        PLUItemIfc item = itemInquiryCargo.getPLUItem();
        InquiryOptionsCargo cargo = (InquiryOptionsCargo) bus.getCargo();
        if (itemInquiryCargo.getModifiedFlag() && item != null)
        {
            cargo.setPLUItem(itemInquiryCargo.getPLUItem());
            cargo.setItemQuantity(itemInquiryCargo.getItemQuantity());
            cargo.setModifiedFlag(itemInquiryCargo.getModifiedFlag());
            cargo.setItemSerial(itemInquiryCargo.getItemSerial());
            cargo.setTransaction(itemInquiryCargo.getTransaction());
            if (itemInquiryCargo.isItemFromWebStore())
            {
                cargo.setItemFromWebStore(true);
            }
        }
        else
        {
            if (itemInquiryCargo.getTransaction() != null)
            {
                SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)itemInquiryCargo.getTransaction();
                if (cargo.getTransaction() == null)
                {
                    cargo.setTransaction(transaction);
                }
                else if (transaction.getAgeRestrictedDOB() != null)
                {
                    ((SaleReturnTransactionIfc)cargo.getTransaction()).
                        setAgeRestrictedDOB(transaction.getAgeRestrictedDOB());
                }
            }
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
        ItemInquiryReturnShuttle obj = new ItemInquiryReturnShuttle();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
