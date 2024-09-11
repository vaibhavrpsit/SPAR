/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StatusRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:42 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:30:10 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:25:28 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:14:24 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 17 2003 13:35:38   rsachdeva
 * Status Color
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 * 
 *    Rev 1.0   Aug 29 2003 16:12:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Apr 09 2003 17:50:30   baa
 * I18n database conversion
 * Resolution for POS SCR-1866: I18n Database  support
 * 
 *    Rev 1.2   Aug 14 2002 18:18:50   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 05 2002 17:58:52   baa
 * code conversion and reduce number of color settings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:36   msg
 * Initial revision.
 * 
 *    Rev 1.3   Feb 27 2002 21:25:58   mpm
 * Continuing work on internationalization
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.UIManager;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//-------------------------------------------------------------------------
/**
   This is the renderer for the Status Table.  It displays
   the Status of devices and data managers in tabular form.

   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------
public class StatusRenderer extends AbstractListRenderer
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static int NAME   = 0;
    public static int STATUS = 1;

    public static int MAX_FIELDS = 2;

    public static int[] STATUS_WEIGHTS = {60,40};

    public static final String DEFAULT_ONLINE = "Online";
    public static final String DEFAULT_OFFLINE = "Offline";

    protected String offlineText = DEFAULT_OFFLINE;

    protected String onlineText = DEFAULT_ONLINE;

    /**
       online status text color 
    **/
    public static final String ONLINE_COLOR = "onlineStatusText";
    /**
       offline status text color 
    **/
    public static final String OFFLINE_COLOR = "offlineStatusText";

    //--------------------------------------------------------------------------
    /**
     *  Default constructor
     */
    public StatusRenderer()
    {
        super();
        setName("StatusRenderer");

        // set default in case lookup fails
        firstLineWeights = STATUS_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("statusRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = STATUS;

        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        labels[NAME].setHorizontalAlignment(JLabel.LEFT);

        GridBagLayout layout           = (GridBagLayout)getLayout();
        GridBagConstraints constraints = layout.getConstraints(labels[NAME]);
        constraints.insets.left        = 10;

        layout.setConstraints(labels[NAME], constraints);
    }

    //--------------------------------------------------------------------------
    /**
     * sets the visual components of the cell
     *  @param data Object
     */
    public void setData(Object value)
    {
        StatusDisplayObject statusLine = (StatusDisplayObject)value;
        labels[NAME].setText("");
        labels[STATUS].setText("");
        
        if(statusLine != null)
        {
            labels[NAME].setText(UIUtilities.retrieveCommonText(statusLine.getObjectName()));

            switch(statusLine.getObjectStatus())
            {
            case StatusDisplayObject.STATUS_ONLINE:
                Color colorGreen = UIManager.getColor(ONLINE_COLOR);
                labels[STATUS].setForeground(colorGreen);
                labels[STATUS].setText(onlineText);
                break;

            case StatusDisplayObject.STATUS_OFFLINE:
                Color colorRed = UIManager.getColor(OFFLINE_COLOR);
                labels[STATUS].setForeground(colorRed);
                labels[STATUS].setText(offlineText);
                break;
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Update the fields based on the properties.
     */
    protected void setPropertyFields()
    {
        onlineText = UIUtilities.retrieveCommonText("Online", "Online");
        offlineText = UIUtilities.retrieveCommonText("Offline", "Offline");
    }


    //--------------------------------------------------------------------------
    /**
     *    Creates a prototype data object used to size the renderer. If
     *    the renderer does not have a variable height (optional display
     *    lines), assigning a prototype will make rendering more efficient.
     *    Objects returned by this method should have all values set to their
     *    maximum length.
     *
     *    @return a populated data object
     */
    public Object createPrototype()
    {
        StatusDisplayObject statusObject =
            new StatusDisplayObject("Device with long name",
                                    StatusDisplayObject.STATUS_OFFLINE);

        return statusObject;
    }

    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()

        // result string
        String strResult = new String("Class:  StatusRenderer (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
        // pass back result
        return(strResult);
    }                                  // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                  // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
       main entrypoint - starts the part when it is run as an application
       @param args String[]
    **/
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new StatusRenderer());
    }
}
