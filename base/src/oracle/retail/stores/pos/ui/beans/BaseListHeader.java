/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BaseListHeader.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:34 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:47:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:52:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:29:16   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Dec 10 2001 19:24:26   cir
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.EYSPOSUIDefaultsIfc;
import oracle.retail.stores.pos.ui.EYSUIFactory;

//------------------------------------------------------------------------------
/**
 *  Base class that implements an EYS POS list header object.
 */
//------------------------------------------------------------------------------
public class BaseListHeader extends JPanel
{
    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
 
    /** the text for the header labels */
    protected String[] labelText = {"Header labels not set"};
    
    /** the sizes for the header labels */
    protected int[] labelSize = {500};

//------------------------------------------------------------------------------
/**
 *     Default constructor.
 */
    public BaseListHeader() 
    {
        super();
        initialize();
        configure();
    }

//------------------------------------------------------------------------------
/**
 *     Initialize the class.
 */
    protected void initialize() 
    {       
        setName("BaseListHeader");
    }
        
//------------------------------------------------------------------------------
/**
 *  Configures the labels for display.
 */
    protected void configure()
    {
        setLayout(new GridBagLayout());
        setBackground(EYSPOSUIDefaultsIfc.HeaderBackground);
        
        GridBagConstraints gbc = new GridBagConstraints();       
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(1, 1, 0, 0);
        
        for(int i=0; i<labelText.length; i++)
        {
            if(i > 0)
            {
                gbc.insets = new Insets(1, 0, 0, 0);
            }
            add(EYSUIFactory.createHeaderLabel(labelText[i], labelSize[i]), gbc);
        }
    }

//------------------------------------------------------------------------------
/**
 *  Sets the size for each label.
 *  @param aList an int array of label sizes
 */
    public void setLabelSize(int[] aList)
    {
        labelSize = aList;
    }

//------------------------------------------------------------------------------
/**
 *  Sets the text for the labels.
 *  @param aList a string array of label text
 */
    public void setLabelText(String[] aList)
    {
        labelText = aList;
    }
    
//------------------------------------------------------------------------------
/**
 *    Returns default display string. <P>
 *    @return String representation of object
 */
    public String toString()
    {
        String strResult = new String("Class: BaseListHeader (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

//------------------------------------------------------------------------------
/**
 *    Retrieves the Team Connection revision number.
 *    @return String representation of revision number
 */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

//------------------------------------------------------------------------------
/**
 *     Entry point for testing.
 *     @param args java.lang.String[]
 */
    public static void main(java.lang.String[] args) 
    {
        JFrame frame = new JFrame();
        frame.setSize(520, 50);
        
        BaseListHeader header = new BaseListHeader();
        
        frame.getContentPane().add(header);
        frame.setVisible(true);
    }

}
