/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
Copyright (c) 1998-2001  360Commerce, Inc.    All Rights Reserved.

    $Log:
     3    360Commerce 1.2         3/31/2005 4:28:13 PM   Robert Pearse   
     2    360Commerce 1.1         3/10/2005 10:21:45 AM  Robert Pearse   
     1    360Commerce 1.0         2/11/2005 12:11:08 PM  Robert Pearse   
    $
    Revision 1.5  2004/08/02 22:28:49  rzurga
    @scr Cannot Invoke Authorize Charge CPOI screen for GC or Credit tenders
    Add functionality to verify that the CPOI (Form device) is online

    Revision 1.4  2004/04/09 13:59:07  cdb
    @scr 4206 Cleaned up class headers for logs and revisions.


* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.device;

import jpos.JposException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceActionGroupIfc;

//--------------------------------------------------------------------------
/**
The <code>FormActionGroup</code> defines the Form specific
device operations available to POS applications.
<p>
@version $KW=@(#); $Ver=pos_4.5.0:43; $EKW;
@see com.extendyourstore.pos.device.FormActionGroupIfc
**/
//--------------------------------------------------------------------------
public interface MAXFormActionGroupIfc extends DeviceActionGroupIfc
{
    public static final String TYPE = "FormActionGroupIfc";
    
    
    //--------------------------------------------------------------------- 
    /**
    Start the Form synchronously. This call will block while the form is
    rendered.
    @param formName the String name of the form for data
    @param isStored boolean for whether the form is store on the Checkmate
    or needs to be downloaded
    @param isGraphicImage boolean to display a form and end form immediately
    @exception Exception can be thrown
    **/
    //--------------------------------------------------------------------- 
    public void startForm(
        String formName, boolean isStored, boolean isGraphicImage)
        throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    Start the Form asynchronously. This will start the form on a separate
    thread. This method returns immediately.
    @param formName the String name of the form for data
    @param isStored boolean for whether the form is store on the Checkmate
    or needs to be downloaded
    @param isGraphicImage boolean to display a form and end form immediately
    @exception Exception can be thrown
    **/
    //--------------------------------------------------------------------- 
    public void startFormAsync(
        String formName, boolean isStored, boolean isGraphicImage)
        throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    End a form.
    @exception JposException can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  void endForm()
        throws DeviceException;

    //--------------------------------------------------------------------- 
    /**
    Clear screen - use THDLineDisplay.clearScreen() instead.
    @exception Exception can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  void clearFormScreen() throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    Store the forms at initializion
    @exception JposException can be thrown
    **/
    //--------------------------------------------------------------------- 
    public void initializeForms()
        throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    End capturing the signature capture data and get data.
    @return byte[] the signature capture data
    @exception JposException can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  byte[] getRawSigData()
        throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    Set display text.
    @param row int for row to display text at
    @param col int for column to display text at
    @param text the String to display
    @exception Exception can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  void displayTextAt(int row, int col, String text)
        throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    Display rolling text.
    @param text the String to display
    @exception JposException can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  void displayRollingText(String text)
        throws DeviceException;
    
    
    //--------------------------------------------------------------------- 
    /**
    Set rolling text area.
    @param start int for row to start rolling text
    @param end int for row to end rolling text
    @exception Exception can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  void setRollingTextArea(int start, int end)
        throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    Reset the textVector..
    @exception Exception can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  void resetTextVector() throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    Store a form on the device.
    @param name the form name to store
    @exception JposException can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  void storeForm(String name)
        throws DeviceException;
    
    //--------------------------------------------------------------------- 
    /**
    Display cash back keypad.
    @param string1 String to display on line 1
    @param string2 String to display on line 2
    @param timeout in milliseconds
    @return String the cash back amount or a zero-length String if cancelled
    @exception JposException can be thrown
    **/
    //--------------------------------------------------------------------- 
    public  String displayCashBackKeypad(
        String string1, String string2, int timeout)
        throws DeviceException;
    
    //--------------------------------------------------------------------------
    /**
    set the flag indicating successful device initialization
    */
    //--------------------------------------------------------------------------
    public void setSuccessfulInitialization(Boolean bSuccess)
        throws DeviceException;
    
    //--------------------------------------------------------------------------
    /**
    get the flag indicating successful device initialization
    */
    //--------------------------------------------------------------------------
    public Boolean getSuccessfulInitialization()
        throws DeviceException;
    
    /**
    Determines if Form is online. <P>
    @return Boolean indicator that Form is online.
    **/
    //--------------------------------------------------------------------------
    public Boolean isFormOnline() throws DeviceException;
}
