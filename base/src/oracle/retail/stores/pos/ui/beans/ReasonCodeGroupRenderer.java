/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReasonCodeGroupRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:49 mszekely Exp $
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
 3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
 2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse   
 1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse   
 *
Revision 1.3  2004/03/16 17:15:18  build
Forcing head revision
 *
Revision 1.2  2004/02/11 20:56:26  rhafernik
@scr 0 Log4J conversion and code cleanup
 *
Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Sep 05 2002 16:50:26   baa
 * I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing imports
import javax.swing.JLabel;

import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  Contains the visual rendering for the Prescription Selection list
**/
//------------------------------------------------------------------------------
public class ReasonCodeGroupRenderer extends AbstractListRenderer 
{
    public static int GROUP       = 0;
    public static int MODIFY      = 1;
    public static int MAX_FIELDS  = 2;
    
    public static int[] REASON_WEIGHTS = {65,35};
    
    private final static String FILLER = "     ";

    //---------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //---------------------------------------------------------------------
    public ReasonCodeGroupRenderer() 
    {
        super();
        setName("ReasonCodeGroupRenderer");
        
        // set default in case lookup fails
        firstLineWeights = REASON_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("reasonCodeRendererWeights");
        
        fieldCount = MAX_FIELDS;
        lineBreak = MODIFY;
        
        initialize();
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {        
        labels[GROUP].setHorizontalAlignment(JLabel.LEFT);
    }
    

    //---------------------------------------------------------------------
    /**
     * This sets the fields of this Renderer.
     * @param value the ReasonCodeGroupBeanModel to be rendered
     */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
        ReasonCodeGroupBeanModel group = (ReasonCodeGroupBeanModel)value;
        String groupName = group.getGroupName();
        labels[GROUP].setText(FILLER + UIUtilities.retrieveCommonText(groupName,groupName));
        labels[MODIFY].setText("");
    }

    //---------------------------------------------------------------------
    /**
       Sets the format for printing out currency and quantities.
    */
    //---------------------------------------------------------------------
    protected void setPropertyFields()
    {
        // Get the format string spec from the UI model properties.

        if (props != null)
        {
        }
    }

    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TenderLineItemIfc the prototype cell
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        ReasonCodeGroupBeanModel prototype = new ReasonCodeGroupBeanModel();
        
        prototype.setGroupName("XXXXXXXXXXXXXXXXXXXX");

        return prototype;
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     */
    //---------------------------------------------------------------------

    public static void main(java.lang.String[] args) 
    {
        UIUtilities.setUpTest();
        ReasonCodeGroupRenderer renderer = new ReasonCodeGroupRenderer();
        renderer.setData(renderer.createPrototype());
        
        UIUtilities.doBeanTest(renderer);
    }
}
