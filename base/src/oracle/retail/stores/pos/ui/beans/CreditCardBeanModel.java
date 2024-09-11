/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CreditCardBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:20:26 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse   
 *
 * Revision 1.5  2004/07/16 20:28:08  dcobb
 * @scr 3948 House Account Enrollment
 * undeprecating.
 *
 * Revision 1.4  2004/03/16 17:15:22  build
 * Forcing head revision
 *
 * Revision 1.3  2004/03/16 17:15:17  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:26  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 01 2003 17:38:22   epd
 * deprecated
 * 
 *    Rev 1.0   Aug 29 2003 16:09:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jan 21 2003 18:16:46   vxs
 * New method added fillUsingMSRModel()
 * Resolution for POS SCR-1936: Swipe Credit/Debit/Gift Card Tender Anytime
 * 
 *    Rev 1.1   Aug 07 2002 19:34:14   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports
import oracle.retail.stores.foundation.manager.device.MSRModel;

//----------------------------------------------------------------------------
/**
    Data transport between the bean and the application for credit card data
**/
//----------------------------------------------------------------------------
public class CreditCardBeanModel extends POSBaseBeanModel
{
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected String cardNumber = "";
    protected String expirationDate = "";
    protected boolean cardSwiped = false;
    protected MSRModel msrModel = null;

    /** Flag to indicate whether to allow editing of card number/expiration date **/
    protected boolean editable = true; // not debit screen

    /** format used to display  date **/
    protected String format= "";  

    //---------------------------------------------------------------------------
    /**
        Get the value of the CreditCard field
        @return the value of CreditCard
    **/
    //---------------------------------------------------------------------------
    public String getCardNumber()
    {
        return cardNumber;
    }

    //---------------------------------------------------------------------------
    /**
        Get the value of the ExpirationDate field
        @return the value of ExpirationDate
    **/
    //---------------------------------------------------------------------------
    public String getExpirationDate()
    {
        return expirationDate;
    }
   //---------------------------------------------------------------------------
    /**
        Get the dateFormat for the expiration date field
        @return the date format
    **/
    //---------------------------------------------------------------------------
    public String getDateFormat()
    {
        return format;
    }

    //---------------------------------------------------------------------------
    /**
        Set the format of the ExpirationDate field
        @return the format of ExpirationDate
    **/
    //---------------------------------------------------------------------------
    public void setDateFormat(String value)
    {
       format = value;
    }
    //---------------------------------------------------------------------------
    /**
        Get the value of the CardSwiped field
        @return the value of CardSwiped
    **/
    //---------------------------------------------------------------------------
    public boolean isCardSwiped()
    {
        return cardSwiped;
    }

    //---------------------------------------------------------------------------
    /**
        Sets the Card number field
        @param number value to be set for cardNumber
    **/
    //---------------------------------------------------------------------------
    public void setCardNumber(String number)
    {
        cardNumber = number;
    }

    //---------------------------------------------------------------------------
    /**
        Sets the ExpirationDate field
        @param date value to be set for ExpirationDate
    **/
    //---------------------------------------------------------------------------
    public void setExpirationDate(String date)
    {
        expirationDate = date;
    }

    //---------------------------------------------------------------------------
    /**
        Sets the CardSwiped field
        @param swiped value to be set for CardSwiped
    **/
    //---------------------------------------------------------------------------
    public void setCardSwiped(boolean swiped)
    {
        cardSwiped = swiped;
    }

    //---------------------------------------------------------------------
    /**
       Gets the editable flag.
       @return the editable flag value
    **/
    //---------------------------------------------------------------------
    public boolean getEditable()
    {
        return editable;
    }

    //---------------------------------------------------------------------
    /**
       Sets the editable flag.
       @param  boolean  true - can edit, false - cannot edit
    **/
    //---------------------------------------------------------------------
    public void setEditable(boolean value)
    {
        editable = value;
    }

    //---------------------------------------------------------------------
    /**
       Gets the MSR model
       @return MSRModel 
    **/
    //---------------------------------------------------------------------
    public MSRModel getMSRModel()
    {
        return msrModel;
    }

    //---------------------------------------------------------------------
    /**
       Sets the MSR model
       @param  model MSRModel
    **/
    //---------------------------------------------------------------------
    public void setMSRModel(MSRModel model)
    {
        msrModel = model;
    }

    //---------------------------------------------------------------------
    /**
       Sets self attributes using the supplied MSRModel
       @param model MSRModel
    **/
    //---------------------------------------------------------------------
    public void fillUsingMSRModel(MSRModel model)
    {
        setCardNumber(model.getAccountNumber());
        setCardSwiped(true);
        setExpirationDate(model.getExpirationDate());
        setMSRModel(model);
    }
    
    //---------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //---------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: CreditCardBeanModel Revision: "
                    + revisionNumber
                    + "\n");
        buff.append("CardNumber     [" + cardNumber + "]\n");
        buff.append("ExpirationDate [" + expirationDate + "]\n");
        buff.append("CardSwiped     [" + cardSwiped + "]\n");
        buff.append("MSRModel       [" + msrModel + "]\n");

        return(buff.toString());
    }
}
