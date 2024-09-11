/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.0  27/06/2013     Initial Draft:Jyoti Rawal, Fix for Bug 6224 Credit/Debit Tender- Delete Button is not showing on Tender Screen

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.behavior;
import javax.swing.JList;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.pos.ui.behavior.AbstractListAdapter;


//------------------------------------------------------------------------------
/**
 *  This is an adapter that connects the LayoutBean with the navigation
 *  button bar. If the Selection in the list is an authorizable
 *     Tender, the delete button will be disabled.
 *      @version $Revision: 1.1 $
 */
//------------------------------------------------------------------------------
public class MAXDeleteTenderListAdapter extends AbstractListAdapter
{
    /** revision number  */
    public static final String revisionNumber = "$Revision: 1.1 $";

            
        /** string value to disable buttons */
        public static String NO_SELECTION = 
            "Clear[false]";
        
        /** string value to enable buttons */
        public static String DELETE_SELECTION = 
            "Clear[true]";

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
                
            
           // check for a valid selection
        if(selection != -1)
        {
        	if (list.getModel().getElementAt(selection) instanceof MAXTenderChargeIfc)
			{
				MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc)list.getModel().getElementAt(selection);
				String cardType = tenderCharge.getCardType();
				if (cardType.startsWith("C-"))
				{
					result = NO_SELECTION;
				}
			} else
            if (list.getModel().getElementAt(selection) instanceof AuthorizableTenderIfc)
            {
                AuthorizableTenderIfc authTender = 
                            (AuthorizableTenderIfc)list.getModel().getElementAt(selection);
    
                // If it is an approved Tender, we will not allow deletion of the tender
                if(authTender.getAuthorizationStatus() == AuthorizableTenderIfc.AUTHORIZATION_STATUS_APPROVED)
                {
                	// Commented As per Hypercity Requirement
                    // result = NO_SELECTION;
                
                	// Modified to Enable DELETE button when Credit/Debit
                	// tenderlineItem is selected in BalanceDue screen   
                    result = DELETE_SELECTION;
                
                }
                
                else // Its not an authorizable tender, so enable the delete button
                {
                    result = DELETE_SELECTION;
                }
            }
                        
        }
        else 
        {
            
            result = NO_SELECTION;
            
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
 *  Returns the revision number of the class.
 *  @return String representation of revision number
 */
    public String getRevisionNumber()
    {                                   
        // return string
        return(revisionNumber);
    }                                   

}

