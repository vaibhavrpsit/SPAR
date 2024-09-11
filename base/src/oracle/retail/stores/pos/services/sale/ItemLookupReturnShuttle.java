/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ItemLookupReturnShuttle.java /main/19 2013/04/17 16:44:39 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/23/14 - Set splittedlineitem to null if current selected 
 *                         splitted line items do not exist.
 *    yiqzhao   04/17/13 - Remove the new addPluItem interface which has
 *                         addSelectedForItemSplit flag.
 *    yiqzhao   04/17/13 - Create attribute selectedForItemSplit flag for order
 *                         pickup and shipping.
 *    yiqzhao   04/16/13 - Fix the issue when enter quantity with item id in
 *                         sell item screen.
 *    hyin      10/11/12 - enable WebStore search flow.
 *    yiqzhao   04/16/12 - remove down casting
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    tksharma  09/09/11 - removed vtemker 08/24/11 fix
 *    vtemker   08/24/11 - Fix for incorrect flow in IMEI sale ring
 *    rsnayak   07/11/11 - fix for send at transaction level
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  03/02/10 - Serialisation code changes for layaway transaction
 *    vapartha  02/11/10 - The If condition was misplaced when this code was
 *                         ported from 13x.Made changes for the same.
 *    nkgautam  01/22/10 - fix for imei look up flow
 *    vikini    01/20/10 - Setting undo to cancel
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/14/09 - Serialisation Code changes
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         1/22/2006 11:45:01 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:42:35 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse
 *
 *   Revision 1.11  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.10  2004/08/05 22:17:03  dcobb
 *   @scr 6655 Remove letter checks from shuttles.
 *   Backed out lwalters changes for SCR 1665 and aschenk changes for SCR 3959.
 *   Modified itemcheck service to initialize the modifyFlag to false and set to true when the item is ready to add to the sale.
 *
 *   Revision 1.9  2004/07/06 15:49:31  rsachdeva
 *   @scr 5963 CPOI Line Items Update
 *
 *   Revision 1.8  2004/06/29 16:42:46  lwalters
 *   @scr 1665
 *
 *   Cancel was removing all items in the transaction.
 *   Changed to check the Cancel letter in addition to the Undo.
 *
 *   Revision 1.7  2004/05/14 20:55:52  dfierling
 *   @scr 3830 -  Modification for Alteration printing instructions
 *
 *   Revision 1.6  2004/03/23 22:22:03  aschenk
 *   @scr 3959 - Item (with no price) is no longer added to the Sell Item screen when select Undo in Price Entry screen.
 *
 *   Revision 1.5  2004/02/25 15:50:41  epd
 *   @scr 3561 Updated to repair the addiing of items to a txn
 *
 *   Revision 1.4  2004/02/20 21:08:20  epd
 *   @scr 3561 fixed adding of items to transaction
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
 *    Rev 1.3   Nov 17 2003 09:56:28   jriggins
 * Setting the SaleReturnLineItemIfc instance into the SaleCargo in unload().
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.2   Nov 13 2003 11:09:56   baa
 * sale refactoring
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.1   Nov 07 2003 12:37:18   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the Item Inquiry service back to
    the Modify Item service.
    $Revision: /main/19 $
 **/
//--------------------------------------------------------------------------
public class ItemLookupReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5054526421608617025L;

    /**
        revision number
     **/
    public static final String revisionNumber = "$Revision: /main/19 $";
    /**
        Child service's cargo
     **/
    protected ItemInquiryCargo inquiryCargo = null;

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
        inquiryCargo = (ItemInquiryCargo) bus.getCargo();
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
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        if (inquiryCargo.getModifiedFlag())
        {
            PLUItemIfc pluItem = inquiryCargo.getPLUItem();
            BigDecimal itemQuantity = inquiryCargo.getItemQuantity();

            if (pluItem != null)
            {
                cargo.setTransaction((SaleReturnTransactionIfc)inquiryCargo.getTransaction());

                if (cargo.getTransaction() == null)
                {
                    cargo.initializeTransaction(bus);
                }

                cargo.setPLUItem(pluItem);
                cargo.setItemQuantity(itemQuantity);
                cargo.setItemSerial(inquiryCargo.getItemSerial());
                SaleReturnLineItemIfc  srli = cargo.getTransaction().addPLUItem(cargo.getPLUItem(), cargo.getItemQuantity());
                srli.setPluDataFromCrossChannelSource(inquiryCargo.isItemFromWebStore());

              //Sets the Send enabled flag to true for transaction level send items        
                if (cargo.getTransaction().isTransactionLevelSendAssigned())
                {
                  Vector<AbstractTransactionLineItemIfc> lineItemsVector = null;
                  lineItemsVector = cargo.getTransaction().getItemContainerProxy().getLineItemsVector();

                  SendManagerIfc sendMgr = null;
                  try
                  {
                    sendMgr = (SendManagerIfc) ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
                  }
                  catch (ManagerException e)
                  {
                    // default to product version
                    sendMgr = new SendManager();
                  }

                  for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
                  {
                    srli = (SaleReturnLineItemIfc) i.next();
                    if (sendMgr.checkValidSendItem(srli))
                    {
                      srli.setItemSendFlag(true);
                      // this value is always 1 since multiple sends are not allowed
                      srli.setSendLabelCount(1);
                    }

                  }

                }

                //Do not take account of serial number for Layaway initiate when item IMEI is scanned instead of Item Number.
                if(cargo.getTransaction() instanceof LayawayTransaction
                        &&  cargo.getTransaction().getTransactionStatus() == LayawayConstantsIfc.STATUS_NEW)
                {
                    srli.setItemSerial(null);
                    srli.setItemIMEINumber(null);
                }
                else
                {
                    srli.setItemSerial(cargo.getItemSerial());
                    srli.setItemIMEINumber(inquiryCargo.getItemIMEINumber());
                }

                if (pluItem.isAlterationItem())
                {
                    srli.setAlterationItemFlag(true);
                }
                cargo.setLineItem(srli);
            }

        }
            // this case is used when transaction sequence number is generated, but then Esc is used
        else if (inquiryCargo.getPLUItem() == null && inquiryCargo.getTransaction() != null)
            {
                cargo.setTransaction((SaleReturnTransactionIfc)inquiryCargo.getTransaction());
            }

        // get the transaction if it was created for the date.
        if (inquiryCargo.getTransaction() != null &&
                ((SaleReturnTransactionIfc)inquiryCargo.getTransaction()).getAgeRestrictedDOB() != null)
        {
            cargo.setTransaction((SaleReturnTransactionIfc)inquiryCargo.getTransaction());
        }
        
        if ( cargo.getTransaction() != null )
        {
            List<SaleReturnLineItemIfc> splittedLineItems = getSplittedLineItems(cargo.getTransaction().getLineItemsVector());
            if ( splittedLineItems.size() > 1 )
            {
                cargo.setSplittedLineItems(splittedLineItems.toArray(new SaleReturnLineItemIfc[splittedLineItems.size()]));
            }
            else
            {
                cargo.setSplittedLineItems(null);
            }
        }
    }

    protected List<SaleReturnLineItemIfc>  getSplittedLineItems(Vector<AbstractTransactionLineItemIfc> lineItems )
    {
        List<SaleReturnLineItemIfc>  srlis = new ArrayList<SaleReturnLineItemIfc>();
        for (AbstractTransactionLineItemIfc lineItem : lineItems)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
            if (srli.isSelectedForItemSplit())
            {
                srlis.add(srli);
                srli.setSelectedForItemSplit(false);
            }
        }
        return srlis;
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
        String strResult = new String("Class:  InquiryOptionsReturnShuttle (Revision " +
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
        ItemLookupReturnShuttle obj = new ItemLookupReturnShuttle();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
