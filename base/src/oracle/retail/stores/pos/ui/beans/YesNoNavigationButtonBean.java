/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/YesNoNavigationButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:30:59 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:27:05 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:16:09 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/08 22:14:54  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:19:12   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ui.behavior.YesNoActionListener;

//-------------------------------------------------------------------------
/**
   This class contains one constant that forces the button bar to be
   horizontal.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------
public class YesNoNavigationButtonBean extends NavigationButtonBean 
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Constant for the YESNO action.
    **/
    public static final String YESNO  = "YesNo";
    
    //------------------------------------------------------------------------------
    /**
    *   Default constructor.
    */
    //------------------------------------------------------------------------------
    public YesNoNavigationButtonBean()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    /**
       Creates an empty NavigationButtonBean. 
       @param actions two dimensional list of buttions 
    */
    //--------------------------------------------------------------------------
    public YesNoNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    //---------------------------------------------------------------------
    /**
        Adds (actually sets) the yesno listener on the Yes/No button.
        @Param listener the YesNo Action Listener
    **/
    //---------------------------------------------------------------------
    public void addYesNoActionListener(YesNoActionListener listener)
    {
        try
        {
            getUIAction(YESNO).setActionListener(listener); 
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.YesNoNavigationButtonBean.class);
            logger.warn( "YesNoNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
        }
    }
    
    //---------------------------------------------------------------------
    /**
        Removes (actually resets) the yes no listener on the Yes/No button.
        @Param listener the yesno Action Listener
    **/
    //---------------------------------------------------------------------
    public void removeYesNoActionListener(YesNoActionListener listener)
    {
        try
        {
            getUIAction(YESNO).resetActionListener(); 
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.YesNoNavigationButtonBean.class);
            logger.warn( "YesNoNavigationButtonBean.addValidateActionListener() did not find the NEXT action.");
        }
    }

}
