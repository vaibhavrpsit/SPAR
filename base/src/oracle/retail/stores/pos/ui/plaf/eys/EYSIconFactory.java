/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSIconFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:34 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *  $
 *  Revision 1.3  2004/02/12 16:52:14  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:45:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 13:59:44   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

// java imports
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.plaf.UIResource;

import oracle.retail.stores.foundation.utility.Util;
//------------------------------------------------------------------------------
/**
 *    Implements a radio button icon ui of fix size
 *      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class EYSIconFactory
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // the radio button icon
    private static Icon radioButtonIcon;

    //------------------------------------------------------------------------------
    /**
    *    Retrieves radio button object
    *   @returns  Icon the  icon object
    */
    //------------------------------------------------------------------------------
    public static Icon getRadioButtonIcon()
    {
        if (radioButtonIcon == null)
        {
            radioButtonIcon = new RadioButtonIcon();
        }
        return radioButtonIcon;
    }
    //------------------------------------------------------------------------------
    /**
    *    Radio Button Icon class
    */
    //------------------------------------------------------------------------------
    private static class RadioButtonIcon implements Icon,  UIResource, Serializable
    {
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
        }
            //------------------------------------------------------------------------------
            /**
            *    Returns the Icons width.
            *   @returns int the icon width
            */
            //------------------------------------------------------------------------------
             public int getIconWidth()
        {
            return 13;
        }
            //------------------------------------------------------------------------------
            /**
            *    Returns the Icons heigth
            *   @returns int the icon heigth
            */
            //------------------------------------------------------------------------------
        public int getIconHeight()
        {
            return 13;
        }
    }
    //--------------------------------------------------------------------------
    /**
     *    Retrieves the PVCS revision number.
     *    @return String representation of revision number
     */
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
