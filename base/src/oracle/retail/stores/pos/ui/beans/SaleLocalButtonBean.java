/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SaleLocalButtonBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:12:00   CSchellenger
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

import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;

//-------------------------------------------------------------------------
/**
   This class contains one constant that forces the button bar to be
   horizontal.
   @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
*/
//-------------------------------------------------------------------------
public class SaleLocalButtonBean extends NavigationButtonBean 
                                 implements LocalButtonListener
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //------------------------------------------------------------------------------
    /**
    *   Default constructor.
    */
    //------------------------------------------------------------------------------
    public SaleLocalButtonBean()
    {
        super();
    }
    
    //--------------------------------------------------------------------------
    /**
       Creates an empty NavigationButtonBean. 
       @param actions two dimensional list of buttions 
    */
    //--------------------------------------------------------------------------
    public SaleLocalButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    //---------------------------------------------------------------------
    /**
        Finds the button using the action name and sets the enable state.
        @param actionName the action name of the button.
        @param enable true if enabled.
     */
    //---------------------------------------------------------------------
    public void enableButton(String actionName, boolean enable)
    {
        try
        {
            getUIAction(actionName).setEnabled(enable); 
        }
        catch(ActionNotFoundException e)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.SaleLocalButtonBean.class);
            logger.warn( "SaleLocalButtonBean.enbleButton() did not find the " + actionName + " action.");
        }
    }
}
