/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/XMLPOSScreenSpecBuilder.java /main/11 2012/10/16 17:37:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *    cgreene   09/10/12 - Popup menu implementation
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:55 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:46 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:52:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:09:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 16 2002 09:45:28   baa
 * upgrade pos to use foundation button spec an field spec classes
 * Resolution for POS SCR-1769: 5.2 UI defects resulting from change to java 1.4
 * 
 *    Rev 1.1   28 May 2002 12:21:56   vxs
 * Removed unncessary concatenations from logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 14:45:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:51:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 19 2002 10:28:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 21 2001 11:33:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

import oracle.retail.stores.foundation.manager.gui.AssignmentSpec;
import oracle.retail.stores.foundation.manager.gui.BeanSpec;
import oracle.retail.stores.foundation.manager.gui.ConnectionSpec;
import oracle.retail.stores.foundation.manager.gui.OverlayScreenSpec;
import oracle.retail.stores.foundation.manager.gui.PropertyTrio;
import oracle.retail.stores.foundation.manager.gui.loader.BuilderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.CatalogIfc;
import oracle.retail.stores.foundation.manager.gui.loader.LoaderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.UILoaderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.XMLOverlayScreenSpecBuilder;

import org.apache.log4j.Logger;

/**
 * This class screen definitions in the 2.0 Style XML Document into 4.0 UI
 * configuration constructs.
 * 
 * @version $Revision: /main/11 $
 */
public class XMLPOSScreenSpecBuilder extends XMLOverlayScreenSpecBuilder implements BuilderIfc
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Store instance of logger here
     */
    protected static final Logger logger = Logger.getLogger(XMLPOSScreenSpecBuilder.class);
    protected String            localBeanSpecName                   = "GenericBeanSpecName";

    protected AssignmentSpec    promptAndResponsePanelAssignment    = null;
    protected AssignmentSpec    workPanelAssignment                 = null;
    protected AssignmentSpec    statusPanelAssignment               = null;
    protected AssignmentSpec    globalNavigationPanelAssignment     = null;
    protected BeanSpec          localNavigationBeanSpec             = null;

    protected CatalogIfc        beanCatalog                         = null;
    protected OverlayScreenSpec screenSpec                          = null;
    protected LoaderIfc         loader                              = null;

    protected static final String BUTTON_STATES                     = "buttonStates";
    protected static final String ACTION_NAME_DELIMITER             = ",";

    /**
     * Default constructor
     */
    public XMLPOSScreenSpecBuilder(LoaderIfc loader)
    {
        super();
        this.loader = loader;
    }

    /**
     * Create the 4.0 Screen Specification.
     */
    public void create()
    {
        // Create the overlay spec
        super.create();

        screenSpec = (OverlayScreenSpec) result;
        screenSpec.setDefaultScreenSpecName("EYSPOSDefaultSpec");

        promptAndResponsePanelAssignment    = createAssignment("PromptAndResponsePanel", "PromptAndResponsePanelSpec");
        statusPanelAssignment               = createAssignment("StatusPanel", "StatusPanelSpec");
        localNavigationBeanSpec             = null;
        workPanelAssignment                 = null;
        globalNavigationPanelAssignment     = null;

        beanCatalog = loader.getCatalog(UILoaderIfc.BEAN_SPEC_NAME);
    }

    /**
     * Create Assignments
     * 
     * @param areaName The area name
     * @param beanSpecName The bean name
     * @return the assignment specification
     */
    protected AssignmentSpec createAssignment(String areaName, String specName)
    {
        AssignmentSpec newAssignment = new AssignmentSpec();
        newAssignment.setAreaName(areaName);
        newAssignment.setBeanSpecName(specName);
        screenSpec.addAssignmentSpec(newAssignment);
        return newAssignment;
    }

    /**
     * Processes nested objects
     * 
     * @param parent The parent object
     * @param childName The name of the child object
     * @param child The child object
     */
    public void addChild(Object parent, String childName, Object child)
    {
        // Trap buttons and add them to a new task bar bean

        if (childName.equals("BUTTON"))
        {
            if (localNavigationBeanSpec == null)
            {
                localNavigationBeanSpec = new BeanSpec();
                localNavigationBeanSpec.setSpecName(localBeanSpecName+"ButtonSpec");
                localNavigationBeanSpec.setBeanPackage("oracle.retail.stores.pos.ui.beans");
                localNavigationBeanSpec.setBeanClassName("NavigationButtonBean");
                beanCatalog.addSpec(localNavigationBeanSpec);

                // Create and add an assignment for the task bar
                AssignmentSpec newAssignment = new AssignmentSpec();
                newAssignment.setAreaName("LocalNavigationPanel");
                newAssignment.setBeanSpecName(localBeanSpecName+"ButtonSpec");
                ((OverlayScreenSpec) parent).addAssignmentSpec(newAssignment);
            }
            localNavigationBeanSpec.addButton(child);
        }
        if (childName.equals("FIELD"))
        {
            // Set up the bean property name.
            screenSpec.setDefaultScreenSpecName("DefaultValidatingSpec");
            String name        = "RequiredValidatingFields";
            String[] attribute = (String[])child;
            if (attribute[1].equals("false"))
            {
                name = "OptionalValidatingFields";
            }
            updateProperty(workPanelAssignment, name, attribute[0]);
        }
    }

    /**
     * Sets an attribute. Check for the screen text attribute and call the
     * setScreenText method explicitly.
     * 
     * @param attrName The name of the attribute to be set.
     * @param attrValue The value of the attribute to be set.
     */
    public void setAttribute(String attrName, String attrValue)
    {
        OverlayScreenSpec screenSpec = (OverlayScreenSpec) result;

        // Cancel enable and the other enable attributes should be properties of the button bar
        // Create a new assignment and set the name of the default button bar

        // Add the assignment to the screen.

        // Check for enablexxx attributes
        int enableIndex = attrName.indexOf("Enable");
        if (enableIndex > 0 && enableIndex < attrName.length())
        {
            // Set the cancel key off if necessary
            String buttonName  = attrName.substring(0, enableIndex);
            if (buttonName.equalsIgnoreCase("cancel") && attrValue.equals("false"))
            {
                updateGlobalButtonState(buttonName, attrValue);
            }

            if (buttonName.equalsIgnoreCase("undo") && attrValue.equals("false"))
            {
                updateGlobalButtonState(buttonName, attrValue);
            }

            if (buttonName.equalsIgnoreCase("enter") && attrValue.equals("true"))
            {
                updateGlobalButtonState(buttonName, attrValue);
            }
        }
        else if (attrName.equals("screenID"))
        {
            screenSpec.setSpecName(attrValue);
        }
        else if (attrName.equals("description"))
        {
            PropertyTrio propTrio = new PropertyTrio();
            propTrio.setPropName("screenName");
            propTrio.setPropValue(attrValue);
            statusPanelAssignment.addBeanProperty(propTrio);

            // Use the description as the name of the bean spec.
            localBeanSpecName = reformatSpecName(attrValue);
        }
        else if (attrName.equals("panelBean") ||
            attrName.equals("formBean") ||
            attrName.equals("tableBean"))
        {
            // Use the bean as the class - separate out the package name.
            int lastIndex = attrValue.lastIndexOf('.');
            String packageName = attrValue.substring(0,lastIndex);
            String className = attrValue.substring(lastIndex + 1);

            boolean createSpecs = !(className.startsWith("Empty"));

            if (createSpecs)
            {
                String workBeanSpecName = replace("Bean", "Spec", className);
                BeanSpec beanSpec = new BeanSpec();
                beanSpec.setSpecName(workBeanSpecName);
                beanSpec.setBeanPackage(packageName);
                beanSpec.setBeanClassName(className);

                // Add the bean spec to the catalog
                beanCatalog.addSpec(beanSpec);

                // Assign the true bean spec name
                if (workPanelAssignment == null)
                {
                    // The bean spec name "InputSpec" is not valid.
                    if (!workBeanSpecName.equals("InputSpec"))
                    {
                        workPanelAssignment = createAssignment("WorkPanel", workBeanSpecName);
                    }
                }

                if (attrName.equals("formBean"))
                {
                    updateGlobalButtonState("Next", "true");
                    updateGlobalButtonState("Clear", "true");
                    addNextAndClearConnections();
                }
            }
        }
        else
        if (attrName.equals("promptText"))
        {
            PropertyTrio propTrio = new PropertyTrio();
            propTrio.setPropName(attrName);
            propTrio.setPropValue(attrValue);
            promptAndResponsePanelAssignment.addBeanProperty(propTrio);
        }
        else
        if (attrName.equals("enabled"))
        {
            if (attrValue.equals("true"))
            {
                screenSpec.setDefaultScreenSpecName("ResponseEntryScreenSpec");
            }
        }
        else
        if (attrName.equals("responseType"))
        {
            PropertyTrio propTrio = new PropertyTrio();
            propTrio.setPropName("responseField");
            propTrio.setPropValue(attrValue);
            promptAndResponsePanelAssignment.addBeanProperty(propTrio);
        }
        else
        {
            logger.warn( "Attribute " + attrName + "," + attrValue + " not handled for " + screenSpec.getSpecName() + "");
        }

        // Deal with validation listener as well.
    }

    /**
     * Updates a bean property on the assignment spec.
     * 
     * @param attrName The name of the attribute to be set.
     * @param attrValue The value of the attribute to be set.
     */
    protected void updateProperty(AssignmentSpec spec, String attrName, String attrValue)
    {
        // Get the value from the assignment if it exists.
        StringBuffer value = new StringBuffer();
        Map<String,PropertyTrio> map = spec.getBeanProperties();

        if (!(map == null))
        {
            PropertyTrio propTrio = map.get(attrName);
            if (propTrio != null)
            {
                value.append(propTrio.getPropValue());
                value.append(",");
            }
        }

        // Add the field to the value, create a new prop trio, and
        // update the work panel assignment.
        value.append(attrValue);
        PropertyTrio propTrio = new PropertyTrio();
        propTrio.setPropName(attrName);
        propTrio.setPropValue(value.toString());
        spec.addBeanProperty(propTrio);
    }

    /**
     * This method modifies the value of buttonStates property.
     * 
     * @param actionName an action names.
     * @return boolValue "true" or "false"
     */
    protected void updateGlobalButtonState(String actionName, String boolValue)
    {
        // if the assignment does not exist, create it.
        if (globalNavigationPanelAssignment == null)
        {
            globalNavigationPanelAssignment =
                createAssignment("GlobalNavigationPanel", "GlobalNavigationPanelSpec");
        }

        // Get the property, if it exists, from the map.
        String property  = null;
        Map<String,PropertyTrio> map = globalNavigationPanelAssignment.getBeanProperties();
        if (map != null)
        {
            PropertyTrio propTrio = map.get(BUTTON_STATES);
            if (propTrio != null)
            {
                property = propTrio.getPropValue();
            }
        }

        // If the property is null, initialize the action and state arrays;
        // otherwise parse the property into the arrays.
        String[] action = new String[5];
        String[] state  = new String[5];
        if (property == null)
        {
            action[0] = "Help";
            action[1] = "Clear";
            action[2] = "Cancel";
            action[3] = "Undo";
            action[4] = "Next";
            state[0]   = "true";
            state[1]   = "false";
            state[2]   = "true";
            state[3]   = "true";
            state[4]   = "false";
        }
        else
        {
            StringTokenizer st = new StringTokenizer(property, ACTION_NAME_DELIMITER);
            ArrayList<String> nameList = new ArrayList<String>();
            while (st.hasMoreTokens())
            {
                nameList.add(st.nextToken());
            }

            String[] nameArray = new String[nameList.size()];
            nameList.toArray(nameArray);
            for(int i = 0; i < nameArray.length; i++)
            {
                // Get the star and end of the action and the enable value
                int startName  = 0;
                int endName    = nameArray[i].indexOf("[");
                int startBool  = endName + 1;
                int endBool    = nameArray[i].indexOf("]");

                // Get the name, the string value and boolean enable value
                action[i] = nameArray[i].substring(startName, endName);
                state[i]  = nameArray[i].substring(startBool, endBool);
            }
        }

        // Find the action and replace the state value
        for(int i = 0; i < action.length; i++)
        {
            if (action[i].equalsIgnoreCase(actionName))
            {
                state[i] = boolValue;
            }
        }

        // Build the property string.
        StringBuffer newProperty = new StringBuffer();
        for(int i = 0; i < action.length; i++)
        {
            newProperty.append(action[i]);
            newProperty.append('[');
            newProperty.append(state[i]);
            newProperty.append(']');
            if (i < action.length - 1)
            {
                newProperty.append(',');
            }
        }

        // Update the spec.
        PropertyTrio propTrio = new PropertyTrio();
        propTrio.setPropName(BUTTON_STATES);
        propTrio.setPropValue(newProperty.toString());
        globalNavigationPanelAssignment.addBeanProperty(propTrio);
    }

    /**
     * Adds the clear and next connects to the screen spec.
     */
    protected void addNextAndClearConnections()
    {
        // If there are no connections, add the validating bean connections.
        Collection<ConnectionSpec> cnct = screenSpec.getConnections();
        if (cnct.isEmpty())
        {
            ConnectionSpec connection = new ConnectionSpec();
            connection.setSourceBeanSpecName("GlobalNavigationPanelSpec");
            connection.setTargetBeanSpecName(workPanelAssignment.getBeanSpecName());
            connection.setListenerPackage("oracle.retail.stores.pos.ui.behavior");
            connection.setListenerInterfaceName("ValidateActionListener");
            screenSpec.addConnectionSpec(connection);

            connection = new ConnectionSpec();
            connection.setSourceBeanSpecName("GlobalNavigationPanelSpec");
            connection.setTargetBeanSpecName(workPanelAssignment.getBeanSpecName());
            connection.setListenerPackage("oracle.retail.stores.pos.ui.behavior");
            connection.setListenerInterfaceName("ClearActionListener");
            screenSpec.addConnectionSpec(connection);
        }
    }

    /**
     * Removes " ", "." and replaces "%" from the string.
     * 
     * @param attrValue The value of the attribute to be set.
     * @return the reformated string
     */
    protected String reformatSpecName(String attrValue)
    {
        String ret = null;
        ret = replace(" ", "", attrValue);
        ret = replace(".", "", ret);
        ret = replace("-", "", ret);
        ret = replace("%", "Percent", ret);

        return ret;
    }

    /**
     * Replaces the first parameter with the second in the thrid parameter.
     * 
     * @param current String to be replaced
     * @param replacement replacement String to be replaced
     * @param string contians characters to be replaced.
     * @return replacement
     */
    protected String replace(String current, String replacement, String string)
    {
        // Set up variables used in the loops.
        int startIndex = 0;
        int index = 0;
        int found = 0;
        boolean firstLoop = true;
        StringBuffer ret = new StringBuffer();
        ArrayList<String> list = new ArrayList<String>();

        // Create an list of all the segment
        while(index >= 0)
        {
            index = string.indexOf(current, startIndex);
            if (index == -1)
            {
                index = string.length();
            }
            else
            {
                found++;
            }

            // Get this part of the string.
            String segment = string.substring(startIndex, index);
            list.add(segment);

            // Increment the start index
            startIndex = index + current.length();

            // If start index is past the end of the string,
            // quit.
            if (startIndex > string.length() - 1)
            {
                index = -1;
            }
        }

        // Put the string back together with the replacement.
        for(int i = 0; i < list.size(); i++)
        {
            ret.append(list.get(i));
            if (found > 0)
            {
                ret.append(replacement);
                found--;
            }
        }

        return ret.toString();
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * @return String a representation of the class.
     */
    @Override
    public String toString()
    {
        StringBuffer tmpString = new StringBuffer("XMLPOSScreenSpecBuilder@");
        tmpString.append(hashCode());
        return tmpString.toString();
    }

}
