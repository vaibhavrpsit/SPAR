/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/OrderSearchKey.java /main/15 2012/07/18 11:19:13 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/18/12 - rename itemID to itemNumber
 *    sgu       07/18/12 - add clone, equal, and toString for itemID and
 *                         cardData
 *    sgu       07/17/12 - add order summary search by card token or masked
 *                         number
 *    sgu       07/17/12 - add item serach criteria
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:53 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:42:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:50:44   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:10:12   msg
 * Initial revision.
 *
 *    Rev 1.2   29 Nov 2001 07:05:40   mpm
 * Continuing order modifications.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   26 Nov 2001 14:18:10   mpm
 * Added initiating channel attribute.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 15:56:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.order.Order;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.utility.AbstractRoutable;
import oracle.retail.stores.domain.utility.CardDataIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class handles the attributes for an order search key. <P>
         @see oracle.retail.stores.domain.arts.OrderReadDataTransaction
     @version $Revision: /main/15 $
**/
//----------------------------------------------------------------------------
public class OrderSearchKey extends AbstractRoutable implements EYSDomainIfc
{                                       // begin class OrderSearchKey
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 964781697476029997L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/15 $";
    /**
        order reference
    **/
    protected OrderIfc order = null;
    /**
        customer reference
    **/
    protected CustomerIfc customer = null;

    /**
        card data
     **/
    protected CardDataIfc cardData = null;

    /**
        store identifier
    **/
    protected String storeID = "";
    /**
        begin date
    **/
    protected EYSDate beginDate = null;
    /**
        end date
    **/
    protected EYSDate endDate = null;

    /**
     * item Number
     */
    protected String itemNumber = null;

    /**
        array of statuses
    **/
    protected int[] statuses = null;
    /**
        initiating channel
    **/
    protected int initiatingChannel = OrderConstantsIfc.ORDER_CHANNEL_WEB;
    /**
        training mode status
    **/
    protected boolean trainingMode = false;

    //---------------------------------------------------------------------
    /**
        Constructs OrderSearchKey object. <P>
    **/
    //---------------------------------------------------------------------
    public OrderSearchKey()
    {                                   // begin OrderSearchKey()
    }                                   // end OrderSearchKey()

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        OrderSearchKey c = new OrderSearchKey();

        // set values
        if (order != null)
        {
            c.setOrder((OrderIfc) order.clone());
        }
        if (customer != null)
        {
            c.setCustomer((CustomerIfc) customer.clone());
        }
        if (storeID != null)
        {
            c.setStoreID(new String(storeID));
        }
        if (beginDate != null)
        {
            c.setBeginDate((EYSDate) beginDate.clone());
        }
        if (endDate != null)
        {
            c.setEndDate((EYSDate) endDate.clone());
        }
        if (itemNumber != null)
        {
            c.setItemNumber(itemNumber);
        }
        if (cardData != null)
        {
            c.setCardData((CardDataIfc)cardData.clone());
        }
        if (statuses != null)
        {
            int numStatuses = statuses.length;
            int[] newArray = new int[numStatuses];
            for (int i = 0; i < numStatuses; i++)
            {
                    newArray[i] = statuses[i];
            }
            c.setStatuses(newArray);
        }
        c.setInitiatingChannel(getInitiatingChannel());
        c.setTrainingMode(isTrainingMode());

        // pass back Object
        return((Object) c);
    }                                   // end clone()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = true;
        OrderSearchKey c = (OrderSearchKey) obj;          // downcast the input object

        // compare all the attributes of OrderSearchKey
        if (Util.isObjectEqual(order, c.getOrder()) &&
            Util.isObjectEqual(customer, c.getCustomer()) &&
            Util.isObjectEqual(storeID, c.getStoreID()) &&
            Util.isObjectEqual(beginDate, c.getBeginDate()) &&
            Util.isObjectEqual(endDate, c.getEndDate()) &&
            Util.isObjectEqual(statuses, c.getStatuses()) &&
            Util.isObjectEqual(itemNumber, c.getItemNumber()) &&
            Util.isObjectEqual(cardData, c.getCardData()) &&
            getInitiatingChannel() == c.getInitiatingChannel() &&
            isTrainingMode() == c.isTrainingMode())

        {
            isEqual = true;             // set the return code to true
        }
        else
        {
            isEqual = false;            // set the return code to false
        }
        return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves order reference. <P>
        @return order reference
    **/
    //----------------------------------------------------------------------------
    public OrderIfc getOrder()
    {                                   // begin getOrder()
        return(order);
    }                                   // end setOrder()

    //----------------------------------------------------------------------------
    /**
        Sets order reference. <P>
        @param value  order reference
    **/
    //----------------------------------------------------------------------------
    public void setOrder(OrderIfc value)
    {                                   // begin setOrder()
        order = value;
    }                                   // end setOrder()

    //----------------------------------------------------------------------------
    /**
        Retrieves customer reference. <P>
        @return customer reference
    **/
    //----------------------------------------------------------------------------
    public CustomerIfc getCustomer()
    {                                   // begin getCustomer()
        return(customer);
    }                                   // end setCustomer()

    //----------------------------------------------------------------------------
    /**
        Sets customer reference. <P>
        @param value  customer reference
    **/
    //----------------------------------------------------------------------------
    public void setCustomer(CustomerIfc value)
    {                                   // begin setCustomer()
        customer = value;
    }                                   // end setCustomer()

    //----------------------------------------------------------------------------
    /**
        Retrieves card data. <P>
        @return card data
    **/
    //----------------------------------------------------------------------------
    public CardDataIfc getCardData()
    {
        return cardData;
    }

    //----------------------------------------------------------------------------
    /**
        Sets card data. <P>
        @param value  card data
    **/
    //----------------------------------------------------------------------------
    public void setCardData(CardDataIfc cardData)
    {
        this.cardData = cardData;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves store identifier. <P>
        @return store identifier
    **/
    //----------------------------------------------------------------------------
    public String getStoreID()
    {                                   // begin getStoreID()
        return(storeID);
    }                                   // end setStoreID()

    //----------------------------------------------------------------------------
    /**
        Sets store identifier. <P>
        @param value  store identifier
    **/
    //----------------------------------------------------------------------------
    public void setStoreID(String value)
    {                                   // begin setStoreID()
        storeID = value;
    }                                   // end setStoreID()

    //----------------------------------------------------------------------------
    /**
        Retrieves begin date. <P>
        @return begin date
    **/
    //----------------------------------------------------------------------------
    public EYSDate getBeginDate()
    {                                   // begin getBeginDate()
        return(beginDate);
    }                                   // end setBeginDate()

    //----------------------------------------------------------------------------
    /**
        Sets begin date. <P>
        @param value  begin date
    **/
    //----------------------------------------------------------------------------
    public void setBeginDate(EYSDate value)
    {                                   // begin setBeginDate()
        beginDate = value;
    }                                   // end setBeginDate()

    //----------------------------------------------------------------------------
    /**
        Retrieves end date. <P>
        @return end date
    **/
    //----------------------------------------------------------------------------
    public EYSDate getEndDate()
    {                                   // begin getEndDate()
        return(endDate);
    }                                   // end setEndDate()

    //----------------------------------------------------------------------------
    /**
        Sets end date. <P>
        @param value  end date
    **/
    //----------------------------------------------------------------------------
    public void setEndDate(EYSDate value)
    {                                   // begin setEndDate()
        endDate = value;
    }                                   // end setEndDate()


    //----------------------------------------------------------------------------
    /**
     * Retrieves item ID
     * @return item ID
     */
    //----------------------------------------------------------------------------
    public String getItemNumber()
    {
        return itemNumber;
    }

    //----------------------------------------------------------------------------
    /**
     * Sets item ID
     * @param itemID
     */
    //----------------------------------------------------------------------------
    public void setItemNumber(String itemNumber)
    {
        this.itemNumber = itemNumber;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves array of statuses. <P>
        @return array of statuses
    **/
    //----------------------------------------------------------------------------
    public int[] getStatuses()
    {                                   // begin getStatuses()
        return(statuses);
    }                                   // end setStatuses()

    //----------------------------------------------------------------------------
    /**
        Sets array of statuses. <P>
        @param value  array of statuses
    **/
    //----------------------------------------------------------------------------
    public void setStatuses(int[] value)
    {                                   // begin setStatuses()
        statuses = value;
    }                                   // end setStatuses()

    //----------------------------------------------------------------------------
    /**
        Retrieves initiating-channel indicator flag. <P>
        @return initiating-channel indicator flag
    **/
    //----------------------------------------------------------------------------
    public int getInitiatingChannel()
    {                                   // begin getInitiatingChannel()
        return(initiatingChannel);
    }                                   // end setInitiatingChannel()

    //----------------------------------------------------------------------------
    /**
        Sets initiating-channel indicator flag. <P>
        @param value  initiating-channel indicator flag
    **/
    //----------------------------------------------------------------------------
    public void setInitiatingChannel(int value)
    {                                   // begin setInitiatingChannel()
        initiatingChannel = value;
    }                                   // end setInitiatingChannel()

    //---------------------------------------------------------------------
    /**
        Sets the training mode status. <P>
        @param value    training mode status
    **/
    //---------------------------------------------------------------------
    public void setTrainingMode(boolean value)
    {
        trainingMode = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns the training mode status. <P>
        @return True, if the transaction is for training, false otherwise
    **/
    //---------------------------------------------------------------------
    public boolean isTrainingMode()
    {
        return(trainingMode);
    }

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuffer strResult = new StringBuffer();
        strResult.append("Class:  OrderSearchKey (Revision ");
        strResult.append(getRevisionNumber());
        strResult.append(") @");
        strResult.append(hashCode());
        strResult.append("\n");
        // add attributes to string
        if (order == null)
        {
            strResult.append("order:                              [null]\n");
        }
        else
        {
            strResult.append(order.toString());
        }
        if (customer == null)
        {
            strResult.append("customer:                              [null]\n");
        }
        else
        {
            strResult.append(customer.toString());
        }
        strResult.append("storeID:                            [").append(storeID).append("]\n");
        if (beginDate == null)
        {
            strResult.append("beginDate:                          [null]\n");
        }
        else
        {
            strResult.append("beginDate:                          [").append(beginDate).append("]\n");
        }
        if (endDate == null)
        {
            strResult.append("endDate:                            [null]\n");
        }
        else
        {
            strResult.append("endDate:                            [").append(endDate).append("]\n");
        }
        if (itemNumber == null)
        {
            strResult.append("itemNumber:                          [null]\n");
        }
        else
        {
            strResult.append("itemNumber:                          [").append(itemNumber).append("]\n");
        }
        if (cardData == null)
        {
            strResult.append("cardData:                          [null]\n");
        }
        else
        {
            strResult.append("cardData:                          [").append(cardData).append("]\n");
        }
        strResult.append(Util.formatToStringEntry("initiatingChannel",
                                                  getInitiatingChannel()))
                 .append(Util.formatToStringEntry("trainingMode",
                                                  isTrainingMode()));
        if (statuses == null)
        {
            strResult.append("statuses:                           [null]\n");
        }
        else
        {
            int numStatuses = statuses.length;
            if (numStatuses == 0)
            {
                strResult.append("statuses:                           [empty]\n");
                strResult.append("    1.                              [empty]\n");
            }
            else
            {
                strResult.append("Statuses:\n");
                for (int i = 0; i < numStatuses; i++)
                {
                    strResult.append("\t").append(i).append(".                              [")
                                     .append(statuses[i]).append("] [")
                                     .append(Order.statusToString(statuses[i]))
                                     .append("]\n");
                }
            }
        }
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        OrderSearchKey main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        OrderSearchKey c = new OrderSearchKey();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class OrderSearchKey
