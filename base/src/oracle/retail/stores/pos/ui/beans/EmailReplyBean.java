/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmailReplyBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:47 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.1   Sep 16 2003 17:52:26   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:34   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:51:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:18   msg
 * Initial revision.
 * 
 *    Rev 1.3   Feb 05 2002 13:25:28   dfh
 * fix to retrieve the reply text
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.2   Jan 23 2002 17:40:26   mpm
 * UI fixes.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
   The reply text bean presents generic functionality to build a text reply.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class EmailReplyBean extends EmailDetailBean
{
    protected JTextArea emailHeaderArea = null;
    protected JTextArea emailReplyArea = null;

    protected JPanel emailReplyPanel = null;

    protected EmailReplyBeanModel beanModel = new EmailReplyBeanModel();
    protected boolean dirtyModel = false;

    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public EmailReplyBean()
    {
        initialize();
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        // Intialize the panel
        setName("EmailReplyBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    protected void initComponents()
    {
        super.initComponents();

        //initialize HeaderArea
        emailHeaderArea = createTextArea();

        //initialize ReplyArea
        emailReplyArea = createTextArea();
        emailReplyArea.setEditable(true);

        emailReplyArea.setBorder(UIManager.getBorder("EmailReplyBean.replyBorder"));

        emailReplyPanel = new JPanel();
        emailReplyPanel.setOpaque(false);
    }

    protected void initLayout()
    {
        setLayout(new BorderLayout(1,1));

        emailReplyPanel.setLayout(new BorderLayout());
        emailReplyPanel.add(emailHeaderArea, BorderLayout.NORTH);
        emailReplyPanel.add(emailReplyArea, BorderLayout.CENTER);
        emailReplyPanel.add(displayTextArea, BorderLayout.SOUTH);

        displayTextPane.setViewportView(emailReplyPanel);
        add(displayTextPane, BorderLayout.CENTER);
    }

    //--------------------------------------------------------------------------
    /**
     *  Returns the base bean model.
     *
     *  @return POSBaseBeanModel
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }
    
    //---------------------------------------------------------------------
    /**
       This returns a EmailReplyBeanModel
       @return EmailReplyBeanModel
       @see oracle.retail.stores.pos.ui.beans.EmailReplyBeanModel
    */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setEmailReplyHeader(getEmailHeader());
        beanModel.setEmailReply(getEmailReply());
        beanModel.setEmailDetail(getEmailDetail());
    }

    //---------------------------------------------------------------------
    /**
     * Return the text to be displayed
     * @return String
     */
    //---------------------------------------------------------------------
    public String getEmailReply()
    {
        return(emailReplyArea.getText());
    }

    //---------------------------------------------------------------------
    /**
     * Return the text to be displayed
     * @return String
     */
    //---------------------------------------------------------------------
    public String getEmailHeader()
    {
        return(emailHeaderArea.getText());
    }

    //---------------------------------------------------------------------
    /**
     * Set the reply text to be displayed
     * @return none
     */
    //---------------------------------------------------------------------
    public void setEmailReply(String value)
    {
        if (value == null)
            value = new String(" ");

        emailReplyArea.setText(value);
    }

    //---------------------------------------------------------------------
    /**
     * Set the email header text to be displayed
     * @return none
     */
    //---------------------------------------------------------------------
    public void setEmailHeader(String value)
    {
        if (value == null)
            value = new String(" ");

        emailHeaderArea.setText(value);
    }

    //---------------------------------------------------------------------
    /**
       This bean requires a EmailReplyBeanModel as its model.
       @param beanModel A EmailReplyBeanModel
       @see oracle.retail.stores.pos.ui.beans.EmailReplyBeanModel
    */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set EmailReplyBean model to null.");
        }
        EmailReplyBeanModel oldValue = beanModel;
        if (model instanceof EmailReplyBeanModel)
        {
            beanModel = (EmailReplyBeanModel)model;
            dirtyModel = true;
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the model if it has been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(dirtyModel)
        {
            setEmailHeader (beanModel.getEmailReplyHeader());
            setEmailReply (beanModel.getEmailReply());
            setEmailDetail(beanModel.getEmailDetail());
            dirtyModel = false;
        }
    }

    //------------------------------------------------------------------------
    /**
     * Overrides the inherited setVisible() to set the focus on the reply area.
       @param value boolean
     */
    //------------------------------------------------------------------------
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if (value)
        {
            setCurrentFocus(emailReplyArea);
        }
    }
    
    //---------------------------------------------------------------------
    /**
     * Activates this bean.
     */
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        emailReplyArea.addFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
     * Deactivates this bean.
     */
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        emailReplyArea.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
       Returns default reply string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EmailReplyBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     * Make some test string
     * @return String
     */
    //---------------------------------------------------------------------
    public String getTestText()
    {
        String text = new String(
    "Order Number: 0321654\n" +
    "Customer: Smith, John\n" +
    "Date: 05/10/2001\n" +
    "Customer Email: Jsmith@earthlink.net\n" +
        "\n" +
        "To: Sears #515 - Highland Mall\n" +
        "\n" +
        "Subject: Order Number 0321654\n" +
        "\n" +
        "Dear Sir/Madam,\n" +
        "\n" +
        "I would like to know the status of my order.  It is order number 0321654.  It was an order of a pair of Levis and a denim shirt.\n" +
        "\n" +
        "\n" +
        "Sincerely,\n" +
        "John Smith\n");

        return(text);
    }

    //---------------------------------------------------------------------
    /**
     * Make header test string
     * @return String
     */
    //---------------------------------------------------------------------
    public String getTestHeaderText()
    {
        String text = new String(
        "To: JSmith@earthlink.net\n" +
        "From: Sears #515 - Highland Mall\n" +
        "Date: 05/06/2001\n" +
        "\n" +
        "Re: Order Number 0321654\n");

        return(text);
    }

    //---------------------------------------------------------------------
    /**
     * Make reply test string
     * @return String
     */
    //---------------------------------------------------------------------
    public String getTestReplyText()
    {
        String text = new String(
        "Dear Mr. Smith,\n" +
        "Your order has been filled, and is ready for pickup at Sears - Hancock Center\n" +
        "\n" +
        "Thank you\n");

        return(text);
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        EmailReplyBean bean = new EmailReplyBean();
        bean.setEmailHeader(bean.getTestHeaderText());
        bean.setEmailReply(bean.getTestReplyText());
        bean.setEmailDetail(bean.getTestText());

        bean.activateListeners();

        UIUtilities.doBeanTest(bean);
    }
}
