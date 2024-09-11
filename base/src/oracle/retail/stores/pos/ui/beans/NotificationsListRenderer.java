/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NotificationsListRenderer.java /main/1 2014/05/16 14:33:38 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  05/15/14 - notifications updates
 *    abondala  05/14/14 - notifications requirement
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.UIManager;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.storeservices.entities.notifications.Notification;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
   This is the renderer for the Notifications list.
   $Revision: /main/1 $
   @since 14.1
*/
//----------------------------------------------------------------------------
public class NotificationsListRenderer extends AbstractListRenderer

{
    private static final long serialVersionUID = -4379386801172856856L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    /** the default weights that layout the first display line */
    public static int[] LINE_WEIGHTS = {90,10};

    /** the default weights that layout the first display line */
    public static int[] LINE_WIDTHS = {1,1};

    public static int[] LINE_HEIGHTS = {1,1};
    
    public static int NOTIFICATION     = 0;
    public static int DATE             = 1;
    public static int MAX_FIELDS       = 2;
    
    private final int maxCharsBeforeLineBrk = 95;


    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.NotificationsListRenderer.class);


    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public NotificationsListRenderer()
    {
        super();
        setName("NotificationsListRenderer");

        firstLineWeights = LINE_WEIGHTS;
        firstLineWidths  = LINE_WIDTHS;
        firstLineHeights = LINE_HEIGHTS;
        
        // look up the label weights
        setFirstLineWeights("notificationsListRendererWeights");
        setFirstLineWidths("notificationsListRendererWidths");
        setFirstLineHeights("notificationsListRendererHeights");
        
        setMsgBackGroundClrProp("notificationsBackground");
        setMsgTextClrProp("notificationsText");
        setMsgFontProp("notificationsFont");

        fieldCount = MAX_FIELDS;
        lineBreak  = DATE;

        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
    protected void initOptions()
    {
        labels[NOTIFICATION].setHorizontalAlignment(JLabel.LEFT);
        labels[NOTIFICATION].setName("NOTIFICATION");
        labels[DATE].setHorizontalAlignment(JLabel.LEFT);
        labels[DATE].setName("DATE");
    }
    
    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
        Notification notification = (Notification)value;
        
        setNotificationData(notification.getNotification());

        if(notification.getNotificationDate() != null)
        {
            StringBuilder sb = new StringBuilder();
            DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
            sb.append(dateTimeService.formatDate(notification.getNotificationDate(), LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT));
            sb.append(" ");
            sb.append(dateTimeService.formatTime(notification.getNotificationDate(), LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT));
            labels[DATE].setText(sb.toString());
        }
        else
        {
            labels[DATE].setText("");
        }
    }
    
    /**
     * @param String notification
     */
    protected void setNotificationData(String message)
    {
        StringBuilder screenMessage = new StringBuilder();
        HashMap<String, String> map = addLineBreaks(message, maxCharsBeforeLineBrk);
        
        int msgLinesCount = Integer.parseInt((String)map.get("COUNT"));
        
        if (lineHeight == 0)
        {
            lineHeight = labels[NOTIFICATION].getFont().getSize() + 4;
        }
        int labelHeight = msgLinesCount * lineHeight;
        Dimension dim = UIUtilities.sizeFromWeight(lineWidth, msgLinesCount, labelHeight);
        if (dim != null)
        {
            labels[NOTIFICATION].setPreferredSize(dim);
        }
        
        screenMessage.append("<html><body>").append((String)map.get("MSG")).append("</body></html>");
        labels[NOTIFICATION].setText(screenMessage.toString());
        labels[NOTIFICATION].setBackground(UIManager.getColor(getMsgBackGroundClrProp()));
        labels[NOTIFICATION].setForeground(UIManager.getColor(getMsgTextClrProp()));
        labels[NOTIFICATION].setFont(UIManager.getFont(getMsgFontProp()));
    }
       
    
   //---------------------------------------------------------------------
    /**
     *  Update the fields based on the properties
     */
    //---------------------------------------------------------------------
    protected void setPropertyFields()  { }
    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the properties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        this.props = props;
    }


    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return Object the prototype renderer
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
         return null;
    }


    //---------------------------------------------------------------------
    /**
       main entrypoint - starts the part when it is run as an application
       @param args String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        NotificationsListRenderer bean = new NotificationsListRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}