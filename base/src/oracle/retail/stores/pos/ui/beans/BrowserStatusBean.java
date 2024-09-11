/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BrowserStatusBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:19:51 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:09:37 PM  Robert Pearse   
 *
 *  Revision 1.5  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.BrowserStatusListener;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

//-------------------------------------------------------------------------
/**
 *  This bean is the presentation of the status area; it contains information
    such as the Cashier, Operator, and Customer Names; the online/offline
    state, time and register number.
 */
//-------------------------------------------------------------------------
public class BrowserStatusBean extends BaseBeanAdapter implements BrowserStatusListener
{
    /**
        Status of the browser
    **/
    protected JLabel status;   
    protected JPanel statusPanel;   

    //-------------------------------------------------------------------------
    /**
     * Constructor
     */
    //-------------------------------------------------------------------------
    public BrowserStatusBean()
    {
        super();
        initialize();
    }

    //-------------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //-------------------------------------------------------------------------
    protected void initialize()
    {
        UIFactory.getInstance().configureUIComponent(this, "BrowserStatusPanel");
                                       
        //statusPanel = new JPanel();
        
        //set up the status label with a blue lettering
        //and the background to light gray
        status = new JLabel("TESTING", SwingConstants.LEFT);
        UIFactory.getInstance().configureUIComponent(status, "BrowserStatusPanel.label");
  
        /*
        create a new GridBagLayout for the status label
        and create a new GridBagConstraints variable for
        setting constraints for components in the GridBagLayout
        */
        //GridBagLayout gridbag = new GridBagLayout();
        //GridBagConstraints c = new GridBagConstraints();  
        //statusPanel.setLayout(gridbag);
        
        //set the constraints for the status label
        //c.anchor = GridBagConstraints.CENTER;
        //c.fill = GridBagConstraints.HORIZONTAL;
        //c.gridheight = GridBagConstraints.REMAINDER;
        //c.weightx = 1.0;
        //c.weighty = 0.0;
        status.setEnabled(true);
        status.setVisible(true);
        //gridbag.setConstraints(status, c);
        
        //add the status label to the statusPanel
        //statusPanel.add(status);
        //statusPanel.setVisible(true);
        
        //set up the constraints for the statusPanel
        //set it to span the width of the window and
        //only the height needed to display the text
        //anchor it to the lower left side of the containment
        //panel.
        //c.anchor = GridBagConstraints.SOUTHWEST;
        //c.gridwidth = GridBagConstraints.REMAINDER;
        //c.gridheight = GridBagConstraints.RELATIVE;
        //c.weightx = 1.0;
        //c.weighty = 0.0;
        //statusPanel.setVisible(true);
        //statusPanel.setSize(new Dimension(494, 25));
        //gridbag.setConstraints(statusPanel, c);
        //add(statusPanel);
        add(status);
        
        
    
    }

    //------------------------------------------------------------------------
    /**
     * Sets the bean fields from the model
     * @param model UIModelIfc 
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
    }

    
    //-------------------------------------------------------------------------
    /**
        This method is required by the ActionListener Interface.  It will be
        called by the Timer object at regular intervals to update the time
        and date on the screen.
        @param e event information.
     */
    //-------------------------------------------------------------------------
    public void actionPerformed(ActionEvent e)
    {
        
    }
    public void setStatus(JLabel newStatus)
    {   
        this.status.setText(newStatus.getText());
    }
    
    //-------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        
        BrowserStatusBean bean = new BrowserStatusBean();            
        
        UIUtilities.doBeanTest(bean);
    }
}
