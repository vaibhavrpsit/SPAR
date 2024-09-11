/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SearchNavigationButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:29:52 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:06 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:06 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:12:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:32   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;



import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.ValidateActionListener;

//-------------------------------------------------------------------------
/**
   This class contains one constant that forces the button bar to be
   horizontal.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
public class SearchNavigationButtonBean extends NavigationButtonBean 
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";
    /**
        Constant for the SEARCH action.
    **/
    public static final String SEARCH  = "Search";
    
    //------------------------------------------------------------------------------
    /**
    *   Default constructor.
    */
    //------------------------------------------------------------------------------
    public SearchNavigationButtonBean()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    /**
       Creates an empty NavigationButtonBean. 
       @param actions two dimensional list of buttions 
    */
    //--------------------------------------------------------------------------
    public SearchNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the validation listener on the Next button.
        @Param listener the Validate Action Listener
    **/
    //---------------------------------------------------------------------
    public void addValidateActionListener(ValidateActionListener listener)
    {
        try
        {
            getUIAction(SEARCH).setActionListener(listener); 
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SearchNavigationButtonBean.class);
            logger.warn( "SearchNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
        }
    }
    
    //---------------------------------------------------------------------
    /**
        Removes (actually resets) the validation listener on the Next button.
        @Param listener the Validate Action Listener
    **/
    //---------------------------------------------------------------------
    public void removeValidateActionListener(ValidateActionListener listener)
    {
        try
        {
            getUIAction(SEARCH).resetActionListener(); 
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SearchNavigationButtonBean.class);
            logger.warn( "SearchNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
        }
    }
}
