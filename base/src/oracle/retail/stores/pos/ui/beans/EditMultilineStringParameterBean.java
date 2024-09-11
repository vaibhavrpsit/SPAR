/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditMultilineStringParameterBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:48 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   07/09/09 - XbranchMerge asinton_bug-8669502 from
 *                         rgbustores_13.1x_branch
 *    asinton   07/08/09 - Changed the max field length to 950 for parameters
 *                         with higher max lengths allowed.
 *    vigopina  03/24/09 - Changes made to display the correct label.
 *    mahising  02/21/09 - Fixed text area of email security parameter value
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/10/2006 2:40:22 PM   Brett J. Larsen CR
 *         10543 - adding support for min/max length limits for multiline
 *         string parameters (e.g. gift footer)
 *
 *         v7x -> 360commerce merge
 *    3    360Commerce 1.2         3/31/2005 4:27:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
 *
 *
 *    6    .v7x      1.2.1.2     6/30/2006 2:31:18 PM   Michael Wisbauer Added
 *         String validator to multi-sting bean and xml files
 *    5    .v7x      1.2.1.1     6/21/2006 2:55:56 PM   Deepanshu       CR
 *         10543: Reverted the fix done for this CR
 *    4    .v7x      1.2.1.0     6/20/2006 11:49:43 PM  Nageshwar Mishra CR
 *         10543: Added the max length according to the Printing parameter. 
 *
 *   Revision 1.6  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.5  2004/05/07 19:54:56  awilliam
 *   @scr 4057 Wrong <arg> for invalid data error for multi string parameter editor
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.1   Sep 15 2003 16:03:06   dcobb
 * Migrate to JDK 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 14 2002 18:17:30   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:38   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:54:04   msg
 * Initial revision.
 *
 *    Rev 1.2   15 Apr 2002 09:34:18   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.1   11 Apr 2002 09:41:32   dfh
 * updates to insert a blank when 2 return chars are in a row for spacing a multi line input string (legal statement)
 * Resolution for POS SCR-1514: Carriage returns are not being saved for legal statements
 *
 *    Rev 1.0   Mar 18 2002 11:55:04   msg
 * Initial revision.
 *
 *    Rev 1.7   11 Mar 2002 18:38:06   baa
 * allow 40 chars on multiline fields, acct no set to 34
 * Resolution for POS SCR-1541: TenderStoreBankAccount number parameter max should be 34
 *
 *    Rev 1.6   Mar 04 2002 14:15:36   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.5   Mar 01 2002 21:33:38   dfh
 * allows up to 880 chars or 21 lines up to 40 chars each -
 * uses fixed courier font to better line up text (40 chars across)
 * Resolution for POS SCR-1414: Layaway does not support multi-line legal statement
 *
 *    Rev 1.4   18 Feb 2002 19:40:38   baa
 * set labels for required fields
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.3   16 Feb 2002 10:17:12   baa
 * fix required field logic
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.2   15 Feb 2002 16:33:26   baa
 * ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   13 Feb 2002 17:34:30   baa
 * fix ui boxes
 * Resolution for POS SCR-1240: VisaRefPhoneNumber parameter value field box too small to see data
 *
 *    Rev 1.0   31 Jan 2002 13:43:20   KAC
 * Initial revision.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java import
import java.awt.GridBagLayout;
import java.io.Serializable;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
    This bean enables the editing of a parameter that has a string value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class EditMultilineStringParameterBean
extends ValidatingBean  // CycleRootPanel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int VALUE_LABEL = 4;
    public static final int MIN_LABEL = 2;
    public static final int MAX_LABEL = 3;

    public static String[] labelText =
        {
            "Parameter Group:", "Parameter Name:", 
            "Minimum Allowed Length:", "Maximum Allowed Length:","Parameter Value:"
        };

        public static String[] labelTags =
        {
            "ParameterGroupLabel", "ParameterNameLabel",
            "MinimumAllowedLengthLabel", "MaximumAllowedLengthLabel", "ParameterValueLabel"
        };
        
    protected JLabel[] labels = null;

    protected MultilineStringParameterBeanModel beanModel =
        new MultilineStringParameterBeanModel();

    protected JLabel valueLabel = null;

    protected JLabel parameterGroupField = null;

    protected JLabel parameterNameField = null;
    
    protected JLabel parameterMinField = null;

    protected JLabel parameterMaxField = null;    
    
   // for debuging purposes only
   // protected JLabel rulerLabel = null;
   // protected JLabel parameterRulerField = null;

    /** The component containing the textual value of the parameter  **/
    protected ConstrainedTextAreaField valueTextAreaField = null;

    /** Scrolling capabilities for the parameter's value.  **/
    protected JScrollPane valueScrollPane = null;

    protected Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.EditMultilineStringParameterBean.class);

    /** The system dependent end of line.  **/
    protected static final String EOL = System.getProperty("line.separator");

    //--------------------------------------------------------------------------
    /**
        Constructs bean.
    **/
    //--------------------------------------------------------------------------
    public EditMultilineStringParameterBean()
    {
        super();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the class.
     */
    //--------------------------------------------------------------------------
    protected void initialize()
    {
        setName("EditMultilineStringParameterBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the components.
     */
    //--------------------------------------------------------------------------
    protected void initComponents()
    {
        labels = new JLabel[labelText.length];

        // create the display labels
        for(int i=0; i<labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }

        // create non-enterable display fields
        parameterGroupField =
            uiFactory.createLabel("parameterGroup", null, UI_LABEL);

        parameterNameField  =
            uiFactory.createLabel("parameterName", null, UI_LABEL);
        
        parameterMinField   = uiFactory.createLabel("parameterMin", null, UI_LABEL);
        parameterMaxField   = uiFactory.createLabel("parameterMax", null, UI_LABEL);


        // for debuging purposes only
        // parameterRulerField  =
        //    uiFactory.createLabel("1........10........20........30........40", null, UI_LABEL);


        // Create the scroll area

        valueScrollPane =
            uiFactory.createConstrainedTextAreaFieldPane
                         ("valueTextAreaField",
                          "1",
                          "950",
                          "30",
                          "true",
                          "true",
                          JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        // create the enterable fields
        valueTextAreaField =
            (ConstrainedTextAreaField)valueScrollPane.getViewport().getView();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the layout and lays out the components.
     */
    //--------------------------------------------------------------------------
    protected void initLayout()
    {
        setLayout(new GridBagLayout());
        JComponent[] fields =
        {
            parameterGroupField,
            parameterNameField,
            parameterMinField,
            parameterMaxField,
            valueScrollPane //, parameterRulerField
        };

        UIUtilities.layoutDataPanel(this,labels, fields,false);

    }

    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        valueTextAreaField.addFocusListener(this);
    }
     
    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before leaving bean
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        valueTextAreaField.removeFocusListener(this);
    } 
     
    //--------------------------------------------------------------------------
    /**
        Overrides JPanel setVisible() method to request focus. <P>
    **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(valueTextAreaField);
        }
    }

    //--------------------------------------------------------------------------
    /**
        Returns the POSBaseBeanModel associated with this bean.
        @return the POSBaseBeanModel associated with this bean.
    **/
    //--------------------------------------------------------------------------

    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return(beanModel);
    }

    //--------------------------------------------------------------------------
    /**
        Returns the value text field.
        @return the value text field.
    **/
    //--------------------------------------------------------------------------
    public ConstrainedTextAreaField getValueTextAreaField()
    {
        return(valueTextAreaField);
    }

    //--------------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    //--------------------------------------------------------------------------
    public void updateModel()
    {
        String tok = "\n\n";
        char blank = ' ';
        StringBuffer sb = new StringBuffer(valueTextAreaField.getText());
        int ind = sb.toString().indexOf(tok);
        while (ind > -1) // found double return chars, insert blank between them
        {
            sb.insert(ind + 1, blank);
            ind = sb.toString().indexOf(tok, ind + 2);
        }

        String text = new String(sb.toString());
        StringTokenizer tokenizer = new StringTokenizer(text, "\n\r\f");
        int size = tokenizer.countTokens();
        String[] allLines = new String[size];

        for (int i = 0; i < size; i++)
        {
            allLines[i] = tokenizer.nextToken();
        }
        beanModel.setAllLines(allLines);
    }

    //--------------------------------------------------------------------------
    /**
        Sets the model data into the bean fields. <P>
        @param model the bean model
    **/
    //--------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the EditMultilineStringParameterBean model to null.");
        }
        else if (model instanceof MultilineStringParameterBeanModel)
        {
            beanModel = (MultilineStringParameterBeanModel)model;
            updateBean();
        }
        else
        {
            logger.warn(
                         "EditMultilineStringParameterBean.setModel does not handle " + beanModel.getClass().getName() + "");
        }
    }

    //--------------------------------------------------------------------------
    /**
        Updates the bean, populated with data from the model. <P>
    **/
    //--------------------------------------------------------------------------
    public void updateBean()
    {
        String group = beanModel.getParameterGroup();
        String name = beanModel.getParameterName();
        parameterGroupField.setText(retrieveText(group,group));
        parameterNameField.setText(retrieveText(name,name));

        if (beanModel.getMinValue() < 0)
        {
            parameterMinField.setText("");
            labels[MIN_LABEL].setText("");
        }
        else
        {
            parameterMinField.setText(String.valueOf(beanModel.getMinValue()));
            labels[MIN_LABEL].setText(retrieveText(labelTags[MIN_LABEL], labels[MIN_LABEL]));
            
        }
        if (beanModel.getMaxValue() < 0)
        {    
            parameterMaxField.setText("");
            labels[MAX_LABEL].setText("");
        }
        else
        {
            parameterMaxField.setText(String.valueOf(beanModel.getMaxValue()));
            labels[MAX_LABEL].setText(retrieveText(labelTags[MAX_LABEL], labels[MAX_LABEL]));
        }

        //valueTextAreaField.setText(beanModel.getValue());
        Serializable[] lines = beanModel.getAllLines();
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < lines.length; i++)
        {
            if (i == (lines.length - 1))
            {
                buf.append(lines[i].toString());
            }
            else
            {
                buf.append(lines[i].toString()).append(EOL);
            }
        }
        
        valueTextAreaField.setText(buf.toString());
        valueTextAreaField.setEditable(true);
    }

    //---------------------------------------------------------------------
    /**
       Updates fields based on properties.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labels[i]));
        }
        valueTextAreaField.setLabel(labels[2]);
    }                                   // end updatePropertyFields()

    //--------------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //--------------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            "Class: EditMultilineStringParameterBean (Revision " +
            getRevisionNumber() + ") @" + hashCode();
        return(strResult);
    }

    //--------------------------------------------------------------------------
    /**
        Returns the revision number. <P>
        @return String representation of revision number
    **/
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    /**
        Displays the bean in it's own frame. <p>
        @param args command line arguments
    **/
    //--------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        EditMultilineStringParameterBean bean =
            new EditMultilineStringParameterBean();
        UIUtilities.doBeanTest(bean);
    }
}
