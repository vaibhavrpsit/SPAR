/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectReasonBeanModel.java /main/15 2012/12/14 09:46:20 abhinavs Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:12:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 21 2003 12:28:22   HDyer
 * Removed use of SelectReasonBeanModel and instead standardize on the use of ReasonBeanModel.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// java imports
import java.util.Vector;

import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
     This is the bean model for the SelectReasonBean. <P>
     @see oracle.retail.stores.pos.ui.beans.SelectReasonBean
     @version $Revision: /main/15 $
     @deprecated as of Release 6.0. Use
     {@link oracle.retail.stores.pos.ui.beans.ReasonBeanModel} instead.
**/
//----------------------------------------------------------------------------
public class SelectReasonBeanModel extends POSBaseBeanModel
{                                       // begin class SelectReasonBeanModel
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/15 $";
    /**
        list of reasons to display
    **/
    protected Vector reasonList = new Vector();
    /**
        reason selected by operator
    **/
    protected String selectedReason = null;
    /**
        indicates item was selected
    **/
    protected boolean selected = false;

    //---------------------------------------------------------------------
    /**
        Constructs SelectReasonBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public SelectReasonBeanModel()
    {                                   // begin SelectReasonBeanModel()
    }                                   // end SelectReasonBeanModel()

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //---------------------------------------------------------------------
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
        SelectReasonBeanModel c = new SelectReasonBeanModel();
        // set values
        if (reasonList != null)
        {
            c.setReasonList((Vector) reasonList.clone());
        }
        if (selectedReason != null)
        {
            c.setSelectedReason(new String(selectedReason));
        }
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

        if(obj == this)
        {
            return true;
    	}
    	boolean isEqual = false;
    	if(obj instanceof SelectReasonBeanModel)
    	{
    		SelectReasonBeanModel c = (SelectReasonBeanModel) obj;          // downcast the input object
    		// compare all the attributes of SelectReasonBeanModel
    		if (Util.isObjectEqual(reasonList, c.getReasonList()) &&
    				Util.isObjectEqual(selectedReason, c.getSelectedReason()))
    		{
    			isEqual= true;             // set the return code to true
    		}
    	}
    	return isEqual;
    }                                   // end equals()

    //----------------------------------------------------------------------------
    /**
        Retrieves list of reasons to display. <P>
        @return list of reasons to display
    **/
    //----------------------------------------------------------------------------
    public Vector getReasonList()
    {                                   // begin getReasonList()
        return(reasonList);
    }                                   // end getReasonList()

    //----------------------------------------------------------------------------
    /**
        Sets list of reasons to display. <P>
        @param value  list of reasons to display
    **/
    //----------------------------------------------------------------------------
    public void setReasonList(Vector value)
    {                                   // begin setReasonList()
        reasonList = value;
    }                                   // end setReasonList()

    //----------------------------------------------------------------------------
    /**
        Adds to reason list. <P>
        @param reason to be added
    **/
    //----------------------------------------------------------------------------
    public void addReason(String value)
    {                                   // begin addReason()
        reasonList.addElement(value);
    }                                   // end addReason()

    //----------------------------------------------------------------------------
    /**
        Retrieves reason selected by operator. <P>
        @return reason selected by operator this is normally a key which you
        can use to retrieve the i18n text from the common bundle
    **/
    //----------------------------------------------------------------------------
    public String getSelectedReason()
    {                                   // begin getSelectedReason()
        return(selectedReason);
    }                                   // end getSelectedReason()

    //----------------------------------------------------------------------------
    /**
        Sets reason selected by operator. <P>
        @param value  reason selected by operator
    **/
    //----------------------------------------------------------------------------
    public void setSelectedReason(String value)
    {                                   // begin setSelectedReason()
        selectedReason = value;
    }                                   // end setSelectedReason()
    //----------------------------------------------------------------------------
    /**
        Sets reason selected by index. <P>
        @param int reason selected by index
    **/
    //----------------------------------------------------------------------------
    public void setSelectedReason(int index)
    {                                   // begin setSelectedReason()
        if (reasonList == null)
            selectedReason = null;
        else
            selectedReason = (String)reasonList.get(index);
    }                                   // end setSelectedReason()

    //----------------------------------------------------------------------------
    /**
        Retrieves item-selected indicator. <P>
        @return item-selected indicator
    **/
    //----------------------------------------------------------------------------
    public boolean getSelected()
    {                                   // begin getSelected()
        return(selected);
    }                                   // end getSelected()

    //----------------------------------------------------------------------------
    /**
        Sets item-selected indicator. <P>
        @param value  item-selected indicator
    **/
    //----------------------------------------------------------------------------
    public void setSelected(boolean value)
    {                                   // begin setSelected()
        selected = value;
    }                                   // end setSelected()

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
        strResult.append("Class:  SelectReasonBeanModel (Revision ")
                 .append(getRevisionNumber()).append(") @")
                 .append(hashCode())
                 .append(Util.EOL);
        // add attributes to string
        if (reasonList == null)
        {
            strResult.append("reasonList:                           [null]")
                     .append(Util.EOL);
        }
        else
        {
            int size = reasonList.size();
            if (size == 0)
            {
                strResult.append("reasonList:                           [empty]")
                         .append(Util.EOL);
            }
            else
            {
                strResult.append("Reason list:").append(Util.EOL);
            }
            for (int i = 0; i < size; i++)
            {
                strResult.append("  ").append(i + 1).append(".  [")
                         .append(reasonList.elementAt(i))
                         .append("]").append(Util.EOL);
            }
        }
        if (selectedReason == null)
        {
            strResult.append("selectedReason:                       [null]")
                     .append(Util.EOL);
        }
        else
        {
            strResult.append("selectedReason:                       [")
                     .append(getSelectedReason()).append("]")
                     .append(Util.EOL);
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
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        SelectReasonBeanModel main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        SelectReasonBeanModel c = new SelectReasonBeanModel();
        c.addReason("Customer Request");
        c.addReason("Operations Request");
        c.setSelectedReason("Operations Request");
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class SelectReasonBeanModel
