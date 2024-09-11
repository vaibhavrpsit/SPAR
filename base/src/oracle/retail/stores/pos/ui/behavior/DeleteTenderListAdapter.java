/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/DeleteTenderListAdapter.java /main/11 2013/09/06 14:08:53 rahravin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *	  rahravin  08/29/13 - Enable delete in splitting a tender
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Oct 21 2003 10:25:10   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

// javax imports

import javax.swing.JList;

import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;


//------------------------------------------------------------------------------
/**
 *  This is an adapter that connects the LayoutBean with the navigation
 *  button bar. If the Selection in the list is an authorizable
 *     Tender, the delete button will be disabled.
 *      @version $Revision: /main/11 $
 */
//------------------------------------------------------------------------------
public class DeleteTenderListAdapter extends AbstractListAdapter
{
    /** revision number  */
    public static final String revisionNumber = "$Revision: /main/11 $";

            
        /** string value to disable buttons */
        public static String NO_SELECTION = 
            "Clear[false]";
        
        /** string value to enable buttons */
        public static String DELETE_SELECTION = 
            "Clear[true]";
        
        public int index = -1;
//------------------------------------------------------------------------------
/**
 *  Builds a button state string based on the selections in the list.
 *  @param list the JList that triggered the event
 */
    public String determineButtonState(JList list)
    {
       // default is to disable buttons
        String result = DELETE_SELECTION;
        // make sure that we have a valid object
        if (list == null)
        {
            return result;
        }
        // get the selection
        int selection = list.getSelectedIndex();
        
        //Adjust selection when we delete an item line
        if(selection != -1)
        {
            setIndex(selection);
        }
        if(selection == -1 && list.getModel() != null && list.getModel().getSize() > 0)
        {
            selection = getIndex();
            if(selection >= list.getModel().getSize())
            {
                selection = list.getModel().getSize() - 1;
                setIndex(selection);
            }
            list.setSelectedIndex(selection);
        }

        // check for a valid selection
        if(selection != -1)
        {
            if(list.getModel().getElementAt(selection) instanceof AuthorizableTenderIfc)
            {
                AuthorizableTenderIfc authTender = 
                         (AuthorizableTenderIfc)list.getModel().getElementAt(selection);
                // If it is an approved Tender, we will not allow deletion of the tender
                if(authTender.getAuthorizationStatus() == AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED)
                {
                    result = NO_SELECTION;
                }
                else // Its not an authorizable tender, so enable the delete button
                {
                    result = DELETE_SELECTION;
                }
            }      
        }
        else
        {
            if(list.getModel() != null && list.getModel().getSize() > 0)
            {
                result = DELETE_SELECTION;
            }
            else
            {
                result = NO_SELECTION;
            }
        }

        return result;
    }
    
        //------------------------------------------------------------------------------
/**
 *  Returns a string representation of this object.
 *  @return String representation of object
 */
    public String toString()
    {                                   
        // result string
        String strResult = new String("Class:  DeleteTenderListAdapter (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   

//------------------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Sets the index value depending upon the selection.
     * 
     * @param int value the SelectedIndex
     */

    public void setIndex(int value)
    {
        index = value;
    }

    /**
     * Gets the index value based on the selection performed.
     * 
     * @return int index the Index at that stage.
     */
    public int getIndex()
    {
        return index;
    }
}

