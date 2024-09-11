/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/LayawaySummaryEntry.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       10/30/08 - check in after refresh
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    4    360Commerce 1.3         4/25/2007 10:00:56 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:17 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:52:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:01:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:21:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 16:14:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:37:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;
// foundation imports
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This class describes a layaway summary entry.  It consists of minimal
     information which would be displayed on a search screen. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class LayawaySummaryEntry
implements LayawaySummaryEntryIfc
{                                       // begin class LayawaySummaryEntry
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 752050947379432155L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        layaway identifier
    **/
    protected String layawayID = "";
    /**
        description (usually description of first item)
    **/
    protected LocalizedTextIfc descriptions = DomainGateway.getFactory().getLocalizedText();
    /**
        layaway status
    **/
    protected int status = LayawayConstantsIfc.STATUS_UNDEFINED;
    /**
        expiration date
    **/
    protected EYSDate expirationDate = null;
    /**
        balance due
    **/
    protected CurrencyIfc balanceDue = null;
    /**
        initial transaction identifier
    **/
    protected TransactionIDIfc initialTransactionID = null;
    /**
        initial transaction business date
    **/
    protected EYSDate initialTransactionBusinessDate = null;

    /**
     * The locale requestor
     */
    protected LocaleRequestor localeRequestor = null;

    //----------------------------------------------------------------------------
    /**
        Constructs LayawaySummaryEntry object. <P>
    **/
    //----------------------------------------------------------------------------
    public LayawaySummaryEntry()
    {                                   // begin LayawaySummaryEntry()
        balanceDue = DomainGateway.getBaseCurrencyInstance();
        localeRequestor = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }                                   // end LayawaySummaryEntry()

    //----------------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        LayawaySummaryEntry c = new LayawaySummaryEntry();

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
    public void setCloneAttributes(LayawaySummaryEntry newClass)
    {                                   // begin setCloneAttributes()
        newClass.setLayawayID(getLayawayID());
        newClass.setLocalizedDescriptions((LocalizedTextIfc)getLocalizedDescriptions().clone());
        newClass.setStatus(getStatus());
        if (initialTransactionID != null)
        {
            newClass.setInitialTransactionID
              ((TransactionIDIfc) initialTransactionID.clone());
        }
        if (initialTransactionBusinessDate != null)
        {
            newClass.setInitialTransactionBusinessDate
              ((EYSDate) initialTransactionBusinessDate.clone());
        }
        if (expirationDate != null)
        {
            newClass.setExpirationDate((EYSDate) getExpirationDate().clone());
        }
        if (balanceDue != null)
        {
            newClass.setBalanceDue((CurrencyIfc) getBalanceDue().clone());
        }
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
        if (obj instanceof LayawaySummaryEntry)
        {                                   // begin compare objects

            LayawaySummaryEntry c = (LayawaySummaryEntry) obj;      // downcast the input object

            // compare all the attributes of LayawaySummaryEntry
            if (Util.isObjectEqual(getLayawayID(), c.getLayawayID()) &&
                Util.isObjectEqual(getLocalizedDescriptions(), c.getLocalizedDescriptions()) &&
                getStatus() == c.getStatus() &&
                Util.isObjectEqual(getExpirationDate(), c.getExpirationDate()) &&
                Util.isObjectEqual(getBalanceDue(), c.getBalanceDue()) &&
                Util.isObjectEqual(getInitialTransactionID(),
                                   c.getInitialTransactionID()) &&
                Util.isObjectEqual(getInitialTransactionBusinessDate(),
                                   c.getInitialTransactionBusinessDate()))
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
        Retrieves layaway identifier. <P>
        @return layaway identifier
    **/
    //----------------------------------------------------------------------------
    public String getLayawayID()
    {                                   // begin getLayawayID()
        return(layawayID);
    }                                   // end getLayawayID()

    //----------------------------------------------------------------------------
    /**
        Sets layaway identifier. <P>
        @param value  layaway identifier
    **/
    //----------------------------------------------------------------------------
    public void setLayawayID(String value)
    {                                   // begin setLayawayID()
        layawayID = value;
    }                                   // end setLayawayID()

    //----------------------------------------------------------------------------
    /**
        Retrieves description (usually description of first item). <P>
        @return description (usually description of first item)
        @deprecated As of 13.1 Use {@link LayawaySummaryEntry#getDescription(Locale)}
    **/
    //----------------------------------------------------------------------------
    public String getDescription()
    {                                   // begin getDescription()
        return getDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
    }                                   // end getDescription()

    //----------------------------------------------------------------------------
    /**
        Retrieves description of the given locale(usually description of first item). <P>
        @param locale the locale of the description
        @return description (usually description of first item)
    **/
    //----------------------------------------------------------------------------
    public String getDescription(Locale locale)
    {
    	return descriptions.getText(LocaleMap.getBestMatch(locale));
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves localized descriptions (usually descriptions of first item). <P>
        @return localized descriptions (usually descriptions of first item)
    **/
    //----------------------------------------------------------------------------
    public LocalizedTextIfc getLocalizedDescriptions()
    {
    	return descriptions;
    }

    //----------------------------------------------------------------------------
    /**
        Sets description (usually description of first item). <P>
        @param value  description (usually description of first item)
        @deprecated As of 13.1 Use {@link LayawaySummaryEntry#setDescription(Locale, String)}
    **/
    //----------------------------------------------------------------------------
    public void setDescription(String value)
    {                                   // begin setDescription()
        setDescription(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE), value);
    }                                   // end setDescription()

    //----------------------------------------------------------------------------
    /**
        Sets description (usually description of first item). <P>
        @param locale the locale of the description
        @param value  description (usually description of first item)
    **/
    //----------------------------------------------------------------------------
    public void setDescription(Locale locale, String value)
    {
    	descriptions.putText(LocaleMap.getBestMatch(locale), value);
    }

    //----------------------------------------------------------------------------
    /**
        Sets localized descriptions (usually descriptions of first item). <P>
        @param value  localized description (usually descriptions of first item)
    **/
    //----------------------------------------------------------------------------
    public void setLocalizedDescriptions(LocalizedTextIfc value)
    {
    	descriptions = value;
    }

    //----------------------------------------------------------------------------
    /**
        Retrieves layaway status. <P>
        @return layaway status
    **/
    //----------------------------------------------------------------------------
    public int getStatus()
    {                                   // begin getStatus()
        return(status);
    }                                   // end getStatus()

    //----------------------------------------------------------------------------
    /**
        Sets layaway status. <P>
        @param value  layaway status
    **/
    //----------------------------------------------------------------------------
    public void setStatus(int value)
    {                                   // begin setStatus()
        status = value;
    }                                   // end setStatus()

    //----------------------------------------------------------------------------
    /**
        Retrieves expiration date. <P>
        @return expiration date
    **/
    //----------------------------------------------------------------------------
    public EYSDate getExpirationDate()
    {                                   // begin getExpirationDate()
        return(expirationDate);
    }                                   // end getExpirationDate()

    //----------------------------------------------------------------------------
    /**
        Sets expiration date. <P>
        @param value  expiration date
    **/
    //----------------------------------------------------------------------------
    public void setExpirationDate(EYSDate value)
    {                                   // begin setExpirationDate()
        expirationDate = value;
    }                                   // end setExpirationDate()

    //----------------------------------------------------------------------------
    /**
        Retrieves balance due. <P>
        @return balance due
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getBalanceDue()
    {                                   // begin getBalanceDue()
        return(balanceDue);
    }                                   // end getBalanceDue()

    //----------------------------------------------------------------------------
    /**
        Sets balance due. <P>
        @param value  balance due
    **/
    //----------------------------------------------------------------------------
    public void setBalanceDue(CurrencyIfc value)
    {                                   // begin setBalanceDue()
        balanceDue = value;
    }                                   // end setBalanceDue()

    //----------------------------------------------------------------------------
    /**
        Retrieves initial transaction identifier. <P>
        @return initial transaction identifier
    **/
    //----------------------------------------------------------------------------
    public TransactionIDIfc getInitialTransactionID()
    {                                   // begin getInitialTransactionID()
        return(initialTransactionID);
    }                                   // end getInitialTransactionID()

    //----------------------------------------------------------------------------
    /**
        Sets initial transaction identifier. <P>
        @param value  initial transaction identifier
    **/
    //----------------------------------------------------------------------------
    public void setInitialTransactionID(TransactionIDIfc value)
    {                                   // begin setInitialTransactionID()
        initialTransactionID = value;
    }                                   // end setInitialTransactionID()

    //----------------------------------------------------------------------------
    /**
        Retrieves initial transaction business date. <P>
        @return initial transaction business date
    **/
    //----------------------------------------------------------------------------
    public EYSDate getInitialTransactionBusinessDate()
    {                                   // begin getInitialTransactionBusinessDate()
        return(initialTransactionBusinessDate);
    }                                   // end getInitialTransactionBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets initial transaction business date. <P>
        @param value  initial transaction business date
    **/
    //----------------------------------------------------------------------------
    public void setInitialTransactionBusinessDate(EYSDate value)
    {                                   // begin setInitialTransactionBusinessDate()
        initialTransactionBusinessDate = value;
    }                                   // end setInitialTransactionBusinessDate()

    //---------------------------------------------------------------------
    /**
        Gets the Locale Requestor.
        @return The locale requestor
    **/
    //---------------------------------------------------------------------
    public LocaleRequestor getLocaleRequestor()
    {
    	return localeRequestor;
    }

    //---------------------------------------------------------------------
    /**
        Sets the Locale Requestor
        @param value locale requestor
    **/
    //---------------------------------------------------------------------
    public void setLocaleRequestor(LocaleRequestor value)
    {
    	localeRequestor = value;
    }

    //---------------------------------------------------------------------
    /**
        Returns string representation of status value. <P>
        @return string representation of status value
    **/
    //---------------------------------------------------------------------
    public String statusToString()
    {                                   // begin statusToString()
        StringBuffer strResult = new StringBuffer();
        // attempt to use descriptor
        try
        {
            if (status == LayawayConstantsIfc.STATUS_UNDEFINED)
            {
                strResult.append("Undefined");
            }
            else
            {
                strResult.append(LayawayConstantsIfc.STATUS_DESCRIPTORS[status]);
            }
        }
        // if out of bounds, build special message
        catch (ArrayIndexOutOfBoundsException e)
        {
            strResult.append("Invalid [").append(status).append("]");
        }

        return(strResult.toString());
    }                                   // end statusToString()

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
          Util.classToStringHeader("LayawaySummaryEntry",
                                    getRevisionNumber(),
                                    hashCode());
        strResult.append(Util.formatToStringEntry("layawayID",
                                                  getLayawayID()))
                 .append(Util.formatToStringEntry("description",
                                                  getLocalizedDescriptions()))
                 .append(Util.formatToStringEntry("status",
                                                  statusToString()))
                 .append(Util.formatToStringEntry("expiration date",
                                                  getExpirationDate()))
                 .append(Util.formatToStringEntry("balance due",
                                                  getBalanceDue()))
                 .append(Util.formatToStringEntry("initial transaction ID",
                                                  getInitialTransactionID()))
                 .append(Util.formatToStringEntry("initial transaction business date",
                                                  getInitialTransactionBusinessDate().toString()));
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
        return(revisionNumber);
    }                                   // end getRevisionNumber()


    //----------------------------------------------------------------------------
    /**
        LayawaySummaryEntrymain method. <P>
        @param String args[]  command-line parameters
    **/
    //----------------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        LayawaySummaryEntry c = new LayawaySummaryEntry();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class LayawaySummaryEntry
