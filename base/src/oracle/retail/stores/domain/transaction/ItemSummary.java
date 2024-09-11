/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/ItemSummary.java /main/15 2013/07/11 12:22:00 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  07/10/13 - Fix to display first item description for each
 *                         suspended txns
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       04/08/09 - localize store location name when retrieving
 *                         transaction header centrally.
 *
 * ===========================================================================
 * $Log:
 *  6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *       Upgrade StringBuffer to StringBuilder
 *  5    360Commerce 1.4         5/12/2006 5:26:36 PM   Charles D. Baker
 *       Merging with v1_0_0_53 of Returns Managament
 *  4    360Commerce 1.3         1/22/2006 11:41:57 AM  Ron W. Haight   Removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:28:34 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:32 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse
 * $
 * Revision 1.5  2004/09/23 00:30:51  kmcbride
 * @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 * Revision 1.4  2004/03/04 20:48:14  baa
 * @scr 3561Returns add units sold
 *
 * Revision 1.3  2004/02/12 17:14:42  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 23:28:51  bwf
 * @scr 0 Organize imports.
 *
 * Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Dec 29 2003 15:33:58   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;
// foundation imports
import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedText;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
    This class is used to summarize item information for use in transaction
    history retrievals.
    @version $Revision: /main/15 $
**/
//----------------------------------------------------------------------------
public class ItemSummary implements ItemSummaryIfc
{                                       // begin class ItemSummary
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 268584518779967801L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/15 $";
    /**
        item identifier
    **/
    protected String itemID = "";
    /**
        item identifier used at the point-of-sale
    **/
    protected String posItemID = "";
    /**
        description
    **/
    protected LocalizedTextIfc description = new LocalizedText();
    /**
        line item sequence number
    **/
    protected int lineItemSequenceNumber = -1;

    /**
        units sold
    **/
    protected BigDecimal unitsSold = BigDecimal.ONE;

    //----------------------------------------------------------------------------
    /**
        Constructs ItemSummary object. <P>
    **/
    //----------------------------------------------------------------------------
    public ItemSummary()
    {                                   // begin ItemSummary()
    }                                   // end ItemSummary()

    //---------------------------------------------------------------------
    /**
       Initializes the object with values.
       @param pItemID item identifier
       @param pPosItemID item as identified by the point-of-sale
       @param pDescription item description
    **/
    //---------------------------------------------------------------------
    public void initialize(String pItemID,
                           String pPosItemID,
                           String pDescription)
    {                                   // begin initialize()
        setItemID(pItemID);
        setPosItemID(pPosItemID);
        LocalizedTextIfc localizedDescription = new LocalizedText();
        localizedDescription.initialize(LocaleMap.getSupportedLocales(), pDescription);
        setLocalizedDescription(localizedDescription);
    }                                   // end initialize()

    //---------------------------------------------------------------------
    /**
       Initializes the object with values.
       @param pItemID item identifier
       @param pPosItemID item as identified by the point-of-sale
       @param sequenceNumber line item sequence number
    **/
    //---------------------------------------------------------------------
    public void initialize(String pItemID,
                           String pPosItemID,
                           int sequenceNumber)
    {                                   // begin initialize()
        setItemID(pItemID);
        setPosItemID(pPosItemID);
        setLineItemSequenceNumber(sequenceNumber);
    }                                   // end initialize()

    //---------------------------------------------------------------------
    /**
       Initializes the object with values.
       @param pItemID item identifier
       @param pPosItemID item as identified by the point-of-sale
       @param sequenceNumber line item sequence number
       @param units unit sold
       @deprecated as of 14.0.  Use initialize with an additional arg pDescription instead
    **/
    //---------------------------------------------------------------------
    public void initialize(String pItemID,
                           String pPosItemID,
                           int sequenceNumber,
                           BigDecimal units)
    {
        setItemID(pItemID);
        setPosItemID(pPosItemID);
        setLineItemSequenceNumber(sequenceNumber);
        setUnitsSold(units);
    }
    
    //---------------------------------------------------------------------
    /**
       Initializes the object with values.
       @param pItemID item identifier
       @param pPosItemID item as identified by the point-of-sale
       @param sequenceNumber line item sequence number
       @param units unit sold
       @param pDescription item description
    **/
    //---------------------------------------------------------------------
    public void initialize(String pItemID,
                           String pPosItemID,
                           int sequenceNumber,
                           BigDecimal units,String pDescription)
    {
        setItemID(pItemID);
        setPosItemID(pPosItemID);
        setLineItemSequenceNumber(sequenceNumber);
        setUnitsSold(units);
        LocalizedTextIfc localizedDescription = new LocalizedText();
        localizedDescription.initialize(LocaleMap.getSupportedLocales(), pDescription);
        setLocalizedDescription(localizedDescription);
    }

    //----------------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        ItemSummary c = new ItemSummary();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return((Object) c);
    }                                   // end clone()

    //----------------------------------------------------------------------------
    /**
        Sets attributes in clone of this object. <P>
        @param newClass new instance of object
    **/
    //----------------------------------------------------------------------------
    public void setCloneAttributes(ItemSummary newClass)
    {                                   // begin setCloneAttributes()
        newClass.setItemID(getItemID());
        newClass.setPosItemID(getPosItemID());
        newClass.setLocalizedDescription((LocalizedTextIfc)getLocalizedDescription().clone());
        newClass.setLineItemSequenceNumber(getLineItemSequenceNumber());
        newClass.setUnitsSold(getUnitsSold());
    }                                   // end setCloneAttributes()

    //----------------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //----------------------------------------------------------------------------
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof ItemSummary)
        {                                   // begin compare objects

            ItemSummary c = (ItemSummary) obj;      // downcast the input object

            // compare all the attributes of ItemSummary
            if (Util.isObjectEqual(getItemID(), c.getItemID()) &&
                Util.isObjectEqual(getPosItemID(), c.getPosItemID()) &&
                Util.isObjectEqual(getLocalizedDescription(), c.getLocalizedDescription()) &&
                getLineItemSequenceNumber() == c.getLineItemSequenceNumber())
            {
                isEqual = true;             // set the return code to true
            }
            else
            {
                isEqual = false;            // set the return code to false
            }
        }                                   // end compare objects
        else
        {
            isEqual = false;
        }
        return(isEqual);
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves item identifier. <P>
        @return item identifier
    **/
    //----------------------------------------------------------------------------
    public String getItemID()
    {                                   // begin getItemID()
        return(itemID);
    }                                   // end getItemID()

    //----------------------------------------------------------------------------
    /**
        Sets item identifier. <P>
        @param value  item identifier
    **/
    //----------------------------------------------------------------------------
    public void setItemID(String value)
    {                                   // begin setItemID()
        itemID = value;
    }                                   // end setItemID()

    //----------------------------------------------------------------------------
    /**
        Retrieves item identifier used at the point-of-sale. <P>
        @return item identifier used at the point-of-sale
    **/
    //----------------------------------------------------------------------------
    public String getPosItemID()
    {                                   // begin getPosItemID()
        return(posItemID);
    }                                   // end getPosItemID()

    //----------------------------------------------------------------------------
    /**
        Sets item identifier used at the point-of-sale. <P>
        @param value  item identifier used at the point-of-sale
    **/
    //----------------------------------------------------------------------------
    public void setPosItemID(String value)
    {                                   // begin setPosItemID()
        posItemID = value;
    }                                   // end setPosItemID()

    //----------------------------------------------------------------------------
    /**
        Retrieves description. <P>
        @return description
        @deprecated As of 13.1 Use {@link #getDescription(Locale)}
    **/
    //----------------------------------------------------------------------------
    public String getDescription()
    {                                   // begin getDescription()
    	return getDescription(LocaleMap.getLocale(LocaleMap.DEFAULT));
    }                                   // end getDescription()

    //----------------------------------------------------------------------------
    /**
        Sets description. <P>
        @param value  description
        @deprecated As of 13.1 Use {@link #setDescription(Locale, String)}
    **/
    //----------------------------------------------------------------------------
    public void setDescription(String value)
    {                                   // begin setDescription()
    	setDescription(LocaleMap.getLocale(LocaleMap.DEFAULT), value);
    }                                   // end setDescription()

    //----------------------------------------------------------------------------
    /**
        Retrieves description of the locale. <P>
        @return description of the locale
    **/
    //----------------------------------------------------------------------------
    public String getDescription(Locale locale)
    {                                   // begin getDescription()
        return description.getText(LocaleMap.getBestMatch(locale));
    }                                   // end getDescription()

    //----------------------------------------------------------------------------
    /**
        Sets description of the locale. <P>
        @param value  description of the locale
    **/
    //----------------------------------------------------------------------------
    public void setDescription(Locale locale, String value)
    {                                   // begin setDescription()
        description.putText(LocaleMap.getBestMatch(locale), value);
    }                                   // end setDescription()

    //----------------------------------------------------------------------------
    /**
        Retrieves localized description. <P>
        @return localized description
    **/
    //----------------------------------------------------------------------------
    public LocalizedTextIfc getLocalizedDescription()
    {                                   // begin getDescription()
        return(description);
    }                                   // end getDescription()

    //----------------------------------------------------------------------------
    /**
        Sets localized description. <P>
        @param value  localized description
    **/
    //----------------------------------------------------------------------------
    public void setLocalizedDescription(LocalizedTextIfc value)
    {                                   // begin setDescription()
        description = value;
    }                                   // end setDescription()

    //---------------------------------------------------------------------
    /**
        Sets line item sequence number.
        @param value line item sequence number
    **/
    //---------------------------------------------------------------------
    public void setLineItemSequenceNumber(int value)
    {                                   // begin setLineItemSequenceNumber()
        lineItemSequenceNumber = value;
    }                                   // end setLineItemSequenceNumber()

    //---------------------------------------------------------------------
    /**
        Returns line item sequence number.
        @return line item sequence number
    **/
    //---------------------------------------------------------------------
    public int getLineItemSequenceNumber()
    {                                   // begin getLineItemSequenceNumber()
        return(lineItemSequenceNumber);
    }                                   // end getLineItemSequenceNumber()

    //----------------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuilder strResult =
          Util.classToStringHeader("ItemSummary",
                                    getRevisionNumber(),
                                    hashCode())
                 .append(Util.formatToStringEntry("itemID", itemID))
                 .append(Util.formatToStringEntry("posItemID", posItemID))
                 .append(Util.formatToStringEntry("description",
                                                  description))
                 .append(Util.formatToStringEntry("lineItemSequenceNumber",
                                                  lineItemSequenceNumber));

        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //----------------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()


    //----------------------------------------------------------------------------
    /**
        ItemSummarymain method. <P>
        @param String args[]  command-line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        ItemSummary c = new ItemSummary();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
    //----------------------------------------------------------------------------
    /**
        Units sold of this item. <P>
        @return int units sold
    **/
    //----------------------------------------------------------------------------
    public BigDecimal getUnitsSold()
    {
        return unitsSold;
    }

    //----------------------------------------------------------------------------
    /**
        Set units Sold <P>
        @param int units sold
    **/
    //----------------------------------------------------------------------------
    public void setUnitsSold(BigDecimal i)
    {
        unitsSold = i;
    }

}                                       // end class ItemSummary
