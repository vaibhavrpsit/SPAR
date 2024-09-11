/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmailDetailBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
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
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:32   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:54:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:10   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 23 2002 17:40:26   mpm
 * UI fixes.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;



import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *    The display text bean presents generic functionality to display text.
 *    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class EmailDetailBean  extends DisplayTextBean
{
    private EmailDetailBeanModel beanModel = null;
    private boolean dirtyModel = false;

    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
     *    Default constructor.
     */
    public EmailDetailBean()
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
        super.initialize();
        setName("EmailDetailBean");
    }

    //---------------------------------------------------------------------
    /**
       This returns a EmailDetailBeanModel
       @return A EmailDetailBeanModel
       @see oracle.retail.stores.pos.ui.beans.EmailDetailBeanModel
    */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        EmailDetailBeanModel beanModel=new EmailDetailBeanModel();
        beanModel.setEmailDetail(getEmailDetail());
    }

    //---------------------------------------------------------------------
    /**
     * Return the text to be displayed
     * @return String
     */
    //---------------------------------------------------------------------
    public String getEmailDetail()
    {
        return(displayTextArea.getText());
    }

    //---------------------------------------------------------------------
    /**
     * Set the text to be displayed
     * @param String emessage to be displayed
     */
    //---------------------------------------------------------------------
    public void setEmailDetail(String value)
    {
        setDisplayText(value);
    }

    //---------------------------------------------------------------------
    /**
       This bean requires a EmailDetailBeanModel as its model.
       @param beanModel A EmailDetailBeanModel
       @see oracle.retail.stores.pos.ui.beans.EmailDetailBeanModel
    */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set EmailDetailBean model to null.");
        }
        EmailDetailBeanModel oldValue = beanModel;
        if (model instanceof EmailDetailBeanModel)
        {
            beanModel = (EmailDetailBeanModel)model;
            dirtyModel = true;
            firePropertyChange("model", oldValue, beanModel);
        }
    }

    //---------------------------------------------------------------------
    /**
     * Update the model if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(dirtyModel)
        {
            setDisplayText(beanModel.getEmailDetail());
            dirtyModel = false;
        }
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EmailDetailBean (Revision " +
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
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        EmailDetailBean bean = new EmailDetailBean();
        bean.setEmailDetail(bean.getTestText());
        bean.activateListeners();

        UIUtilities.doBeanTest(bean);
    }
}
