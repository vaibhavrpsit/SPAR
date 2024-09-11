/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlternateDataInputBean.java
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  Standard bean that displays a gridded set of data input
 *  components and labels. It wrap labels to better fit the
 *  grid layout.
 */
//------------------------------------------------------------------------------
public class AlternateDataInputBean extends DataInputBean
{

    /**
	 *  serialVersionUID
	 */
	private static final long serialVersionUID = -8547288495657426304L;

	//--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public AlternateDataInputBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures the bean. The configurator calls this after it has
     *  configured the fields.
     */
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        if (labels != null && components != null)
        {
        	// use alternalte layout which is more suitable for lable wrapping
        	UIUtilities.alternateLayoutDataPanel(this, labels, components, true);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the text on the specified component label.
     *  @param pos the location of the label in the label array
     *  @param newText the new text for the label
     */
    public void setLabelText(int pos, String newText)
    {
    	super.setLabelText(pos,newText);
        if (labels != null && labels.length > pos)
        {
            UIUtilities.wrapJLabelText(labels[pos]);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Updates the screen components with data from the bean model.
     */
    protected void updateBean()
    {
    	super.updateBean();

    	 // loop through the components and wrap its label
    	 // if the component is a JLabel
        for (int i = 0; i < components.length; i++)
        {
            JComponent c = components[i];

            if (c instanceof JLabel)
            {
                UIUtilities.wrapJLabelText((JLabel) c);
            }
        }
    }
}
