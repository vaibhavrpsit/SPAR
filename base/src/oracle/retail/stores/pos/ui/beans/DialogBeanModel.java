/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DialogBeanModel.java /main/20 2013/06/12 08:24:08 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/11/13 - Modified to fix interaction between Webstore and SIM
 *                         item lookup flows.
 *    mchellap  02/13/13 - Modified gift receipt printing dialog message
 *    hyin      09/10/12 - add search webstore button.
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    cgreene   08/23/11 - removed deprecated methods
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         12/19/2007 8:46:25 AM  Manikandan Chellapan
 *      PAPB FR27 Bulk Checkin-4
 * 3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:20:57 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:35 PM  Robert Pearse   
 *
 *Revision 1.7  2004/05/26 16:37:47  lzhao
 *@scr 4670: add capture customer and bill addr. same as shipping for send
 *
 *Revision 1.6  2004/05/13 19:38:40  jdeleau
 *@scr 4862 Support timeout for all screens in the return item flow.
 *
 *Revision 1.5  2004/04/27 17:24:31  cdb
 *@scr 4166 Removed unintentional null pointer exception potential.
 *
 *Revision 1.4  2004/03/16 17:15:22  build
 *Forcing head revision
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:26  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 31 2003 12:58:38   nrao
 * Added new buttons and dialog screen for Instant Credit Enrollment.
 * 
 *    Rev 1.0   Aug 29 2003 16:10:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:49:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:08   msg
 * Initial revision.
 * 
 *    Rev 1.4   Mar 08 2002 07:50:14   mpm
 * Corrected problem with expired-card dialog.
 * Resolution for POS SCR-1505: Expired Card screen has <ARG> in the Heading
 *
 *    Rev 1.3   Jan 19 2002 10:29:52   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.2   01 Nov 2001 13:59:30   pdd
 * Fixed problem with old letter values for button letters sticking.
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.1   29 Oct 2001 18:17:22   pdd
 * Deprecated setOkLetterName().
 * Added setButtonLetter() and getLetters().
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.0   Sep 21 2001 11:37:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;

import oracle.retail.stores.foundation.manager.gui.DialogScreenIfc;
import oracle.retail.stores.pos.ui.timer.TimerModelIfc;

/**
 * This class is a model that holds the model for the bean that builds Dialog
 * Screens in a generic fashion.
 * 
 * @version $Revision: /main/20 $
 */
public class DialogBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = 693264490472925613L;
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/20 $";
    /**
     * The id used to locate the screen text in the properties object.
     */
    protected String fieldResourceID = "";
    /**
     * The banner color indicates the severity of the error.
     */
    protected Color fieldBannerColor = null;
    /**
     * Field type.
     */
    protected int fieldType = -1;
    /**
     * An array of aguments; replaces <Arg> tags in the properties text.
     */
    protected String[] fieldArgs = null;
    /**
     * Title text
     */
    protected String fieldTitle = "";
    /**
     * Title tag
     */
    protected String fieldTitleTag = "";
    /**
     * The Screen name.
     */
    protected String fieldDescription = "";
    /**
     * Used to set the letter name to be sent when a button is pressed on a
     * dialog.
     * 
     * @see oracle.retail.stores.pos.ui.beans.DialogBeanModel.setButtonLetter()
     */
    protected String[] letters = { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
    /**
     * When this member is true, it indicates that the UI generated the error
     * screen. When false, it indicates that the business logic generted the
     * error screen
     */
    protected boolean uiGeneratedError = false;
    /**
     * When this member is true, it indicates that the user pressed the cancel
     * button and UI the bean must handle the Yes/No key press. When false, it
     * indicates that the business logic generted the error screen
     */
    protected boolean uiGeneratedCancel = false;
    /**
     * When this member is true, it indicates that the user pressed the help
     * button and UI the bean must handle the enter key press. When false, it
     * indicates that the business logic generted the error screen
     */
    protected boolean uiGeneratedHelp = false;
    /**
     * When the error screen has been generated by the UI, this model will be
     * used to redisplay the screen on which the error occurred.
     */
    protected POSBaseBeanModel formModel = null;
    /**
     * When the error screen has been generated by the UI, this string will be
     * used to redisplay the screen on which the error occurred.
     */
    protected String formScreenSpecName = null;
    /**
     * TimerModel used for automatic logoff after a timeout
     */
    protected TimerModelIfc timerModel = null;

    /**
     * Default constructor.
     */
    public DialogBeanModel()
    {
    }

    /**
     * Constructor initializes fieldArgs with String[].
     * 
     * @param String[] args - the arguments for the model
     */
    public DialogBeanModel(String[] args)
    {
        fieldArgs = args;
    }

    /**
     * Get the value of the ResourceID field
     * 
     * @return the value of ResourceID
     */
    public String getResourceID()
    {
        return fieldResourceID;
    }

    /**
     * Get the value of the BannerColor field
     * 
     * @return the value of BannerColor
     */
    public Color getBannerColor()
    {
        return fieldBannerColor;
    }

    /**
     * Get the value of the Type field
     * 
     * @return the value of Type
     */
    public int getType()
    {
        return fieldType;
    }

    /**
     * Get the value of the Args field
     * 
     * @return the value of Args
     */
    public String[] getArgs()
    {
        return fieldArgs;
    }

    /**
     * Get the value of the Title field
     * 
     * @return the value of Title
     */
    public String getTitle()
    {
        return fieldTitle;
    }

    /**
     * Gets the value of the Title tag field
     * 
     * @return the value of Title tag
     */
    public String getTitleTag()
    {
        return fieldTitleTag;
    }

    /**
     * Get the value of the Description field
     * 
     * @return the value of Description
     */
    public String getDescription()
    {
        return fieldDescription;
    }

    /**
     * Sets the ResourceID field
     * 
     * @param the value to be set for ResourceID
     */
    public void setResourceID(String resourceID)
    {
        fieldResourceID = resourceID;
    }

    /**
     * Sets the BannerColor field
     * 
     * @param the value to be set for BannerColor
     */
    public void setBannerColor(Color bannerColor)
    {
        fieldBannerColor = bannerColor;
    }

    /**
     * Sets the Type field
     * 
     * @param the value to be set for Type
     */
    public void setType(int type)
    {
        fieldType = type;
    }

    /**
     * Sets the Args field
     * 
     * @param the value to be set for Args
     */
    public void setArgs(String[] args)
    {
        fieldArgs = args;
    }

    /**
     * Sets the Title field
     * 
     * @param title String value to be set
     */
    public void setTitle(String title)
    {
        fieldTitle = title;
    }

    /**
     * Sets the Title tag field
     * 
     * @param value String value to be set
     */
    public void setTitleTag(String value)
    {
        fieldTitleTag = value;
    }

    /**
     * Sets the Description field
     * 
     * @param description String value to be set
     */
    public void setDescription(String description)
    {
        fieldDescription = description;
    }

    /**
     * Sets the letter for a given button.
     * 
     * @param buttonId the id of the button. See {@link DialogScreenIfc}.
     * @param letterName the letter that should be mailed.
     */
    public void setButtonLetter(int buttonId, String letterName)
    {
        letters[buttonId] = letterName;
    }

    /**
     * Gets the letters array.
     * 
     * @return String[] of letters.
     */
    public String[] getLetters()
    {
        return letters;
    }

    /**
     * Sets the uiGeneratedError field
     * 
     * @param uiGeneratedError indicates if the UI or Business logic generated
     *            the error.
     */
    public void setUiGeneratedError(boolean uiGeneratedError)
    {
        this.uiGeneratedError = uiGeneratedError;
    }

    /**
     * Gets the getUiGeneratedError field
     * 
     * @return indicates if the UI or Business logic generated the error.
     */
    public boolean getUiGeneratedError()
    {
        return uiGeneratedError;
    }

    /**
     * Sets the uiGeneratedCancel field
     * 
     * @param uiGeneratedCancel indicates if the UI or Business logic generated
     *            the error.
     */
    public void setUiGeneratedCancel(boolean uiGeneratedCancel)
    {
        this.uiGeneratedCancel = uiGeneratedCancel;
    }

    /**
     * Gets the getUiGeneratedCancel field
     * 
     * @return indicates if the UI or Business logic generated the error.
     */
    public boolean getUiGeneratedCancel()
    {
        return uiGeneratedCancel;
    }

    /**
     * Sets the uiGeneratedHelp field
     * 
     * @param uiGeneratedHelp indicates if the UI or Business logic generated
     *            the error.
     */
    public void setUiGeneratedHelp(boolean uiGeneratedHelp)
    {
        this.uiGeneratedHelp = uiGeneratedHelp;
    }

    /**
     * Gets the getuiGeneratedHelp field
     * 
     * @return indicates if the UI or Business logic generated the error.
     */
    public boolean getUiGeneratedHelp()
    {
        return uiGeneratedHelp;
    }

    /**
     * Sets the formModel field
     * 
     * @param formModel the model of the form that generated the UI error.
     */
    public void setFormModel(POSBaseBeanModel formModel)
    {
        this.formModel = formModel;
    }

    /**
     * Gets the formModel field
     * 
     * @return the model of the form that generated the UI error.
     */
    public POSBaseBeanModel getFormModel()
    {
        return formModel;
    }

    /**
     * Sets the formScreenSpecName field
     * 
     * @param specID the id of the form that generated the UI error.
     */
    public void setFormScreenSpecName(String specID)
    {
        formScreenSpecName = specID;
    }

    /**
     * Gets the formScreenSpecName field
     * 
     * @return the id of the form that generated the UI error.
     */
    public String getFormScreenSpecName()
    {
        return formScreenSpecName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder(100);

        buff.append("Class: DialogBeanModel Revision: ").append(revisionNumber).append("\n");
        buff.append("ResourceID [").append(fieldResourceID).append("]\n");
        buff.append("BannerColor [").append(fieldBannerColor).append("]\n");
        buff.append("Type                   [").append(fieldType).append("]\n");
        buff.append("Title                  [").append(fieldTitle).append("]\n");
        buff.append("Desc                   [").append(fieldDescription).append("]\n");
        buff.append("Args                   [").append(fieldArgs).append("]\n");
        buff.append("UiGeneratedError       [").append(uiGeneratedError).append("]\n");
        buff.append("UiGeneratedCancel      [").append(uiGeneratedCancel).append("]\n");
        buff.append("UiGeneratedHelp        [").append(uiGeneratedHelp).append("]\n");

        buff.append("Letters                [");
        if (letters != null)
        {
            for (int i = 0; i < letters.length; i++)
            {
                buff.append(" ").append(letters[i]);
            }
        }
        else
        {
            buff.append("No Letters Defined");
        }
        buff.append("]");

        return (buff.toString());
    }
}
