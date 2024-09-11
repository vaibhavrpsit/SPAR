/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryCountBeanModel.java /main/15 2012/12/14 09:46:20 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *     4    360Commerce 1.3         4/25/2007 8:51:27 AM   Anda D. Cadar   I18N
 *           merge
 *     3    360Commerce 1.2         3/31/2005 4:30:15 PM   Robert Pearse   
 *     2    360Commerce 1.1         3/10/2005 10:25:39 AM  Robert Pearse   
 *     1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *    $
 *    Revision 1.5  2004/09/23 00:07:11  kmcbride
 *    @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *    Revision 1.4  2004/05/26 21:22:17  dcobb
 *    @scr 4302 Correct compiler warnings
 *
 *    Revision 1.3  2004/03/16 17:15:18  build
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 20:56:26  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Nov 18 2002 13:42:42   kmorneau
 * added ui functionality for display of expected amounts for non-blind close
 * Resolution for 1824: Blind Close
 * 
 *    Rev 1.0   Apr 29 2002 14:51:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:52   msg
 * Initial revision.
 * 
 *    Rev 1.4   Mar 07 2002 14:53:00   mpm
 * Text externalization for till UI screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   04 Mar 2002 16:21:10   epd
 * Updates to accommodate use of TenderTypeMap class
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   Jan 19 2002 10:32:16   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   02 Jan 2002 15:35:12   epd
 * Updated to include more descriptive info (added several attributes).  Fixed some erroneous comments
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:34:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
     Transports data from the busness logic to the SummaryCountBean. <P>
     @version $Revision: /main/15 $
**/
//--------------------------------------------------------------------------
public class SummaryCountBeanModel implements Serializable
{                                       // begin class SummaryCountBeanModel
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4570778420861477590L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/15 $";
    /**
        the Description of the current tender
    **/
    protected String description = "";
    /**
        the field label
    **/
    protected String label = "";
    /**
        the field label tag
    **/
    protected String labelTag = "";
    /**
        This field is the mapping between tender types and button action
        as defined in tenderuicfg.xml for associated button
    **/
    protected int tenderType = -1;
    /**
        Name of action for button associated with this tender.  Used to map
        tender type to action
    **/
    protected String actionName = "";
    /**
        Displayed in the input area.
    **/
    protected CurrencyIfc amount = null;
    /**
        Negative allowed.
    **/
    protected Boolean negativeAllowed = new Boolean(true);
    /**
        flag to determine whether the entry field is disabled.
    **/
    protected boolean fieldDisabled = true;
    /**
        flag to determine whether the entry field is hidden.
    **/
    protected boolean fieldHidden = false;
    /**
        The amount expected to be in the amount field.
    **/
    protected CurrencyIfc expectedAmount = null;
    /**
        flag to determine whether to display the expected amount
    **/
    protected boolean expectedAmountHidden = false;

    //----------------------------------------------------------------------
    /**
            Constructs SummaryCountBeanModel object.
    **/
    //----------------------------------------------------------------------
    public SummaryCountBeanModel()
    {                                   // begin SummaryCountBeanModel()
    }                                   // end SummaryCountBeanModel()

    //----------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //----------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        SummaryCountBeanModel c = new SummaryCountBeanModel();
        // set values
        if (description != null)
        {
            c.setDescription(new String(description));
        }
        if (label != null)
        {
            c.setLabel(new String(label));
        }
        if (amount != null)
        {
            c.setAmount((CurrencyIfc) amount.clone());
        }
        c.setFieldDisabled(fieldDisabled);
        c.setFieldHidden(fieldHidden);
        // pass back Object
        return((Object) c);
    }                                   // end clone()


    //----------------------------------------------------------------------
    /**
        Determines if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean equals(Object obj)
    {                                   // begin equals()
    	if(obj == this)
    	{
    		return true;
    	}
    	boolean isEqual = false;
    	if(obj instanceof SummaryCountBeanModel)
    	{
    		SummaryCountBeanModel c = (SummaryCountBeanModel) obj;          // downcast the input object
    		// compare all the attributes of SummaryCountBeanModel
    		if (Util.isObjectEqual(description, c.getDescription())
    				&& Util.isObjectEqual(amount, c.getAmount()))
    		{
    			isEqual= true;             // set the return code to true
    		}
    	}
    	return isEqual;
    }                                   // end equals()

    //----------------------------------------------------------------------
    /**
        Returns the currency description. <P>
        @return the currency description
    **/
    //----------------------------------------------------------------------
    public String getDescription()
    {                                   // begin getdescription()
        return(description);
    }                                   // end getdescription()

    //----------------------------------------------------------------------
    /**
        Sets the currency description. <P>
        @param value  the currency description
    **/
    //----------------------------------------------------------------------
    public void setDescription(String value)
    {                                   // begin setdescription()
        description = value;
    }                                   // end setdescription()

    //----------------------------------------------------------------------
    /**
        Returns the text in the prompt area. <P>
        @return the text in the prompt area
    **/
    //----------------------------------------------------------------------
    public String getLabel()
    {                                   // begin getLabel()
        return(label);

    }                                   // end getLabel()

    //----------------------------------------------------------------------
    /**
        Sets the text in the prompt area. <P>
        @param value  the text in the prompt area
    **/
    //----------------------------------------------------------------------
    public void setLabel(String value)
    {                                   // begin setLabel()
        label = value;
    }                                   // end setLabel()

    //----------------------------------------------------------------------
    /**
        Returns the label tag. <P>
        @return label tag
    **/
    //----------------------------------------------------------------------
    public String getLabelTag()
    {                                   // begin getLabelTag()
        return(labelTag);

    }                                   // end getLabelTag()

    //----------------------------------------------------------------------
    /**
        Sets the label tag. <P>
        @param value  label tag
    **/
    //----------------------------------------------------------------------
    public void setLabelTag(String value)
    {                                   // begin setLabelTag()
        labelTag = value;
    }                                   // end setLabelTag()

    //----------------------------------------------------------------------
    /**
        Returns the tender Type. <P>
        @return the tender Type
    **/
    //----------------------------------------------------------------------
    public int getTenderType()
    {                                   // begin getTenderType()
        return(tenderType);

    }                                   // end getTenderType()

    //----------------------------------------------------------------------
    /**
        Returns the action name. <P>
        @return the action name
    **/
    //----------------------------------------------------------------------
    public String getActionName()
    {                                   // begin getActionName()
        return(actionName);

    }                                   // end getActionName()

    //----------------------------------------------------------------------
    /**
        Sets the the tender Type. <P>
        @param value  the tender Type
    **/
    //----------------------------------------------------------------------
    public void setTenderType(int value)
    {                                   // begin setTenderType()
        tenderType = value;
    }                                   // end setTenderType()

    //----------------------------------------------------------------------
    /**
        Sets the the action name. <P>
        @param value  the action name
    **/
    //----------------------------------------------------------------------
    public void setActionName(String value)
    {                                   // begin setActionName()
        actionName = value;
    }                                   // end setActionName()

    //----------------------------------------------------------------------
    /**
        Returns the amount in the input area. <P>
        @return the amount in the input area.
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getAmount()
    {                                   // begin getamount()
        return(amount);
    }                                   // end getamount()

    //----------------------------------------------------------------------
    /**
        Sets the amount in the input area. <P>
        @param value  the amount in the input area.
    **/
    //----------------------------------------------------------------------
    public void setAmount(CurrencyIfc value)
    {                                   // begin setamount()
        amount = value;
    }                                   // end setamount()

    //----------------------------------------------------------------------
    /**
        Gets the negative allowed Boolean. <P>
        @return true if negative is allowed.
    **/
    //----------------------------------------------------------------------
    public Boolean getNegativeAllowed()
    {                                   // begin getNegativeAllowed()
        return(negativeAllowed);
    }                                   // end getNegativeAllowed()

    //----------------------------------------------------------------------
    /**
        Sets the negative allowed Boolean. <P>
    **/
    //----------------------------------------------------------------------
    public void setNegativeAllowed(Boolean value)
    {                                   // begin setNegativeAllowed()
        negativeAllowed = value;
    }                                   // end setNegativeAllowed()

    //----------------------------------------------------------------------
    /**
        Is the field disabled. <P>
        @return true if disabled.
    **/
    //----------------------------------------------------------------------
    public boolean isFieldDisabled()
    {                                   // begin isFieldDisabled()
        return(fieldDisabled);
    }                                   // end isFieldDisabled()

    //----------------------------------------------------------------------
    /**
        Sets the field status. <P>
    **/
    //----------------------------------------------------------------------
    public void setFieldDisabled(boolean value)
    {                                   // begin setFieldDisabled()
        fieldDisabled = value;
    }                                   // end setFieldDisabled()

    //----------------------------------------------------------------------
    /**
        Is the field Hidden. <P>
        @return true if Hidden.
    **/
    //----------------------------------------------------------------------
    public boolean isFieldHidden()
    {                                   // begin isFieldHidden()
        return(fieldHidden);
    }                                   // end isFieldHidden()

    //----------------------------------------------------------------------
    /**
        Sets the field display status. <P>
    **/
    //----------------------------------------------------------------------
    public void setFieldHidden(boolean value)
    {                                   // begin setFieldHidden()
        fieldHidden = value;
    }                                   // end setFieldHidden()

    //----------------------------------------------------------------------
    /**
        Gets the expected amount for this field.
        @return the currency object containing the expected amount
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getExpectedAmount()
    {
        return expectedAmount;
    }

    //----------------------------------------------------------------------
    /**
        Sets the expected amount for the field
        @param amount the expected amount
    **/
    //----------------------------------------------------------------------
    public void setExpectedAmount(CurrencyIfc amount)
    {
        expectedAmount = amount;
    }

    //----------------------------------------------------------------------
    /**
        Is the expected amount to be hidden from the operator.
        @return true if it should be hidden
    **/
    //----------------------------------------------------------------------
    public boolean isExpectedAmountHidden()
    {
        return expectedAmountHidden;
    }

    //----------------------------------------------------------------------
    /**
        Set whether to hide the expected amount.
        @param hidden set to true to hide the expected amount
    **/
    //----------------------------------------------------------------------
    public void setExpectedAmountHidden(boolean hidden)
    {
        expectedAmountHidden = hidden;
    }

    //----------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        String strResult = new String("Class:  SummaryCountBeanModel (Revision " +
                                      getRevisionNumber() +
                                      ") @" +
                                      hashCode());
        // add attributes to string
        strResult += "\ndescription:               [" + description + "]";
        if (amount == null)
        {
            strResult += "\namount:                     [null]";
        }
        else
        {
            strResult += "\namount:                     [" + amount.toString() + "]";
        }
        // pass back result
        return(strResult);
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
        SummaryCountBeanModel main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        SummaryCountBeanModel c = new SummaryCountBeanModel();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SummaryCountBeanModel
