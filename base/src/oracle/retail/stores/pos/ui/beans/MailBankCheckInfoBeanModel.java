/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MailBankCheckInfoBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/06 16:26:57  aschenk
 *   @scr 4647 - The Country and State/Region data fields both contained a value of "Other" which was removed when collecting Customer information for Mail Check tenders.
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 05 2004 14:29:10   bjosserand
 * Mail Bank Check.
 * 
 *    Rev 1.0   Aug 29 2003 16:11:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Mar 21 2003 10:58:46   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.3   Feb 19 2003 13:50:40   crain
 * Replaced abbreviations
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.2   Jan 21 2003 15:20:54   crain
 * Changed to accomodate business customer
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.1   Sep 18 2002 17:15:32   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:49:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:10   msg
 * Initial revision.
 * 
 *    Rev 1.3   Jan 19 2002 10:30:56   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.2   26 Oct 2001 12:40:40   jbp
 * added email address for special order customer info.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   Dec 03 2001 16:26:24   dfh
 * updates local journal string for changes to customer data, should these changes need to be journaled
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.0   Sep 21 2001 11:36:36   msg
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:17:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.utility.CountryIfc;

//----------------------------------------------------------------------------
/**
    This is the model used to pass customer information
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class MailBankCheckInfoBeanModel extends CustomerInfoBeanModel
{
    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // Flag indicating a customer is linked from and Add or Find
     protected boolean custLinked = false;
    // Flag indicating a customer is linked
    protected boolean transLinked = false;
    // Flag indicating something was changed on the screen
    protected boolean changeState = false;
    // Flag indicating transaction is from layaway
    protected boolean layawayFlag = false;
    // Journal string for updated customer fields
    protected String journalString = "";

    //---------------------------------------------------------------------
    /**
        MailBankCheckInfoBeanModel constructor.
     */
    //---------------------------------------------------------------------
    public MailBankCheckInfoBeanModel()
    {
        super();
        this.setContactInfoOnly(true);
    }

    //----------------------------------------------------------------------------
    /**
       Get flag that shows whether or not data was updated
       @return whether or not data was updated
    **/
    //----------------------------------------------------------------------------
    public boolean getChangeState()
    {
        return changeState;
    }

    //----------------------------------------------------------------------------
    /**
       Get the value of the transLinked field
       @return the value of transLinked
       **/
    //----------------------------------------------------------------------------
    public boolean isTransLinked()
    {
        return transLinked;
    }

    //----------------------------------------------------------------------------
    /**
       Sets the transLinked field
       @param the value to be set for transLinked
    **/
    //----------------------------------------------------------------------------
    public void setTransLinked(boolean linked)
    {
        transLinked = linked;
    }

    //----------------------------------------------------------------------------
    /**
       Sets the value that shows whether or not data was updated
       @param the value representing whether or not data was updated
    **/
   
    //----------------------------------------------------------------------------
    public void setChangeState(boolean changed)
    {
        changeState = changed;
    }

    //---------------------------------------------------------------------
    /**
     * Gets the Country Names property values.
     * @return The country name list.
     * 
     */
    public String[] getCountryNames()
    {
  
        CountryIfc[] countryList = super.getCountries();
        int length = countryList.length;
        
        int xxIndex = -1;
        for (int i=0;i<length;i++)
        {    
            if (countryList[i].getCountryCode().equals("XX"))
                xxIndex = i;
        }
        
        String[] completeCountryNames = super.getCountryNames();
        if (xxIndex == -1)
            return (completeCountryNames);
        
        String[] countryNames = new String[length-1];

        //index for new Country name array
        int j = 0;
        for (int i=0;i < length; i++)
        {    
            if (xxIndex != i)
            {
                countryNames[j] = completeCountryNames[i];
                j++;
            }
        }
        
        return countryNames;
    }
    
    //----------------------------------------------------------------------------
    /**
       Get the value of the layawayFlag field
       @return the value of layawayFlag
       **/
    //----------------------------------------------------------------------------
    public boolean isLayawayFlag()
    {
        return layawayFlag;
    }

    //----------------------------------------------------------------------------
    /**
       Sets the layawayFlag field
       @param the value to be set for layawayFlag
    **/
    //----------------------------------------------------------------------------
    public void setLayawayFlag(boolean layaway)
    {
        layawayFlag = layaway;
    }

    //-------------------------------------------------------------------------
    /**
       Get the value of the journalString field
       @return the value of journalString
    **/
    //-------------------------------------------------------------------------
    public String getJournalString()
    {
        return journalString;
    }
    //----------------------------------------------------------------------------
    /**
       Sets the journalString field
       @param the value to be set for journalString
    **/
    //----------------------------------------------------------------------------
    public void setJournalString(String value)
    {
        journalString = value;
    }

    //----------------------------------------------------------------------------
    /**
       Converts to a string representing the data in this Object
       @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer(super.toString());

        buff.append("Class: MailBankCheckInfoBeanModel Revision: " + revisionNumber + "\n");
        buff.append("changed state [" + changeState + "]\n");
        buff.append("layaway flag [" + layawayFlag + "]\n");
        return (buff.toString());
    }
    /**
     * @return
     */
    public boolean isCustLinked()
    {
        return custLinked;
    }

    /**
     * @param b
     */
    public void setCustLinked(boolean b)
    {
        custLinked = b;
    }

}
