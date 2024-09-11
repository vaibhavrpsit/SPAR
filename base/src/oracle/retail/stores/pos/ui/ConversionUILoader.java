/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/ConversionUILoader.java /main/13 2014/02/12 18:01:31 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  02/11/14 - corrected the intended file from screen to beans
 *    arabalas  01/31/14 - released the stream handles
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *    cgreene   09/10/12 - Popup menu implementation
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:12 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:10  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.0   Aug 29 2003 16:09:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 16 2002 09:45:24   baa
 * upgrade pos to use foundation button spec an field spec classes
 * Resolution for POS SCR-1769: 5.2 UI defects resulting from change to java 1.4
 * 
 *    Rev 1.0   Apr 29 2002 14:44:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:51:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:40   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:16:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.manager.gui.AssignmentSpec;
import oracle.retail.stores.foundation.manager.gui.BeanSpec;
import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.foundation.manager.gui.ConnectionSpec;
import oracle.retail.stores.foundation.manager.gui.DefaultScreenSpec;
import oracle.retail.stores.foundation.manager.gui.DisplaySpec;
import oracle.retail.stores.foundation.manager.gui.OverlayScreenSpec;
import oracle.retail.stores.foundation.manager.gui.PropertyTrio;
import oracle.retail.stores.foundation.manager.gui.TemplateSpec;
import oracle.retail.stores.foundation.manager.gui.UIConfigIfc;
import oracle.retail.stores.foundation.manager.gui.UIConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.loader.BuilderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.CatalogIfc;
import oracle.retail.stores.foundation.manager.gui.loader.LoaderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.NullBuilder;
import oracle.retail.stores.foundation.manager.gui.loader.SpecIfc;
import oracle.retail.stores.foundation.manager.gui.loader.UILoaderIfc;
import oracle.retail.stores.foundation.manager.gui.loader.UIXMLLoader;
import oracle.retail.stores.foundation.manager.gui.loader.XMLOverlayScreenSpecBuilder;
import oracle.retail.stores.foundation.manager.xml.InvalidXmlException;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.foundation.utility.xml.XMLUtility;

/**
 * The ConversionUILoader converts a 2.0 UI XML file (quarryScreens.xml) into
 * Foundation 4.0 XML format. This class writes out two files: screens.cnv and
 * beans.cnv. The statements in these files can be used to build 4.0 style UI
 * Config XML files.
 * <P>
 * The ConversionUILoader extends the UIXMLLoader and specifies a builder for
 * the ButtonSpec object.
 * <P>
 * 
 * @version $Revision: /main/13 $
 */
public class ConversionUILoader extends UIXMLLoader implements LoaderIfc, UILoaderIfc, UIConstantsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3634953082997781402L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Store instance of logger here
     */
    protected static final Logger logger = Logger.getLogger(ConversionUILoader.class);

    /**
     * Define button spec name here
     */
    protected static final String BUTTON_SPEC_NAME = "BUTTON";

    /**
     * Define Field spec name here
     */
    protected static final String FIELD_SPEC_NAME = "FIELD";

    /**
     * Define button spec name here
     */
    protected static final String FORM_SPEC_NAME = "FORM";

    /**
     * Define button spec name here
     */
    protected static final String MENU_SPEC_NAME = "MENU";

    /**
     * Define button spec name here
     */
    protected static final String DIALOG_SPEC_NAME = "DIALOG";

    /**
     * Define button spec name here
     */
    protected static final String TABLE_SPEC_NAME = "TABLE";

    /**
     * Define button spec name here
     */
    protected static final String LOGO_SPEC_NAME = "LOGO";

    /**
     * Default constructor Sets up builder table and element groups.
     */
    public ConversionUILoader()
    {
    }

    /**
     * Assigns builder classes to element names for all of the known UI xml
     * elements
     */
    protected void setupBuilderTable()
    {
        // This will setup all of the default assignments
        super.setupBuilderTable();

        // Add or override any assignments that are specific to POS

        builderTable.put(BUTTON_SPEC_NAME, new XMLButtonSpecBuilder());
        builderTable.put(FIELD_SPEC_NAME, new XMLFieldSpecBuilder());
        builderTable.put(BEAN_SPEC_NAME, new POSBeanSpecBuilder());

        // Add builders for the Forms etc.
        XMLOverlayScreenSpecBuilder overlayBuilder = new XMLPOSScreenSpecBuilder(this);
        NullBuilder nullBuilder = new NullBuilder();
        XMLValueSpecBuilder valueBuilder = new XMLValueSpecBuilder();

        builderTable.put(FORM_SPEC_NAME, overlayBuilder);
        builderTable.put(MENU_SPEC_NAME, overlayBuilder);

        // Dialogs are not really overlays - they need their own builder
        builderTable.put(DIALOG_SPEC_NAME, overlayBuilder);

        builderTable.put(TABLE_SPEC_NAME, overlayBuilder);
        builderTable.put(LOGO_SPEC_NAME, overlayBuilder);
        builderTable.put("DEFAULT_BEHAVIOR", nullBuilder);
        builderTable.put("FRAME", nullBuilder);
        builderTable.put("LABEL", valueBuilder);
        builderTable.put("P", nullBuilder);
        builderTable.put("ARG", nullBuilder);

    }

    /**
     * Reads in the specifications.
     * <P>
     * Checks to see if the specs are stored in one big file or one file per
     * cataloged specification type. The data source is either a root directory
     * or a file name.
     * <P>
     * Searches for the Screens in screenFilename Searches for the Displays in
     * displayFilename Searches for the Templates in templateFilename Searches
     * for the Beans in beanFilename
     * 
     * @param ds String data source location
     */
    public void readSpecs(String ds) throws ConfigurationException
    {
        Collection<List<Object>> specs = new HashSet<List<Object>>();

        if (ds == null)
        {
            logger.fatal("Data source attribute not set in ");
        }
        else
        {
            // These specifications are in one big file.
            String[] specDataSources = new String[1];
            specDataSources[0] = ds;
            specs = getSpecs(specDataSources);

            // Convert the specifications
            processSpecs(specs);

            // Verify all specifications
            Collection<String> errors = new HashSet<String>();

            if (!errors.isEmpty())
            {
                logger.fatal("Errors encountered loading the screen configuration file");
                Iterator<String> iter = errors.iterator();
                String nextError = null;
                while (iter.hasNext())
                {
                    nextError = iter.next();
                    logger.error(nextError);
                }

            }
        }
    }

    /**
     * Processes the collection of XML elements
     * 
     * @param specs The collection of XML specifications
     */
    protected void processSpecs(Collection<List<Object>> specs) throws ConfigurationException
    {
        // Replace XMLDocuments with atomic elements
        specs = condense(specs);
        ArrayList<List<Object>> elements = new ArrayList<List<Object>>();
        elements.addAll(specs);
        Element nextNode = null;
        CatalogIfc catalog = null;
        String nextNodeName = null;
        SpecIfc processedSpec = null;

        for (int i = 0; i < elements.size(); i++)
        {
            nextNode = (Element) elements.get(i);
            nextNodeName = nextNode.getTagName();
            if (!nextNodeName.equals(INCLUDE))
            {
                processedSpec = (SpecIfc) loadSpec(nextNode);

                // Add the specs to the appropriate catalog
                try
                {
                    if (processedSpec instanceof DefaultScreenSpec)
                    {
                        catalog = getCatalog(DEFAULT_SCREEN_SPEC_NAME);
                        addSpecToCatalog(catalog, processedSpec);
                    }
                    if (processedSpec instanceof OverlayScreenSpec)
                    {
                        catalog = getCatalog(OVERLAY_SCREEN_SPEC_NAME);
                        addSpecToCatalog(catalog, processedSpec);
                    }
                    if (processedSpec instanceof TemplateSpec)
                    {
                        catalog = getCatalog(TEMPLATE_SPEC_NAME);
                        addSpecToCatalog(catalog, processedSpec);
                    }
                    if (processedSpec instanceof DisplaySpec)
                    {
                        catalog = getCatalog(DISPLAY_SPEC_NAME);
                        addSpecToCatalog(catalog, processedSpec);
                    }
                    if (processedSpec instanceof BeanSpec)
                    {
                        catalog = getCatalog(BEAN_SPEC_NAME);
                        addSpecToCatalog(catalog, processedSpec);
                    }
                }
                catch (ConfigurationException ce)
                {
                    throw new ConfigurationException(
                            "The processing of specifications was halted while processing specification "
                                    + processedSpec.getSpecName() + " on XML element " + nextNodeName + " because ", ce);
                }
            }
        }
    }

    /**
     * Recursive routine to process each element.
     * 
     * @param newNode Node that is ready to be processed
     * @return Object processed node
     */
    protected Object visitNode(Element newNode) throws ConfigurationException
    {
        String nodeName = newNode.getNodeName();

        // Lookup the builder for this node.
        BuilderIfc builder = builderTable.get(nodeName);

        // Check for the case where the builder is missing
        if (builder == null)
        {
            builder = defaultBuilder;
        }

        // Instantiate the object through the builder
        if (builder == defaultBuilder)
        {
            defaultBuilder.setNodeName(nodeName);
        }
        builder.create();

        // Set all attributes
        setAttributes(newNode, builder);

        Object newObject = builder.getResult();

        // Now, look for the children. It's possible for a
        // node to have no children.

        Element[] children = XMLUtility.getChildElements(newNode);
        Object childObject = null;
        Element childElement = null;

        for (int index = 0; index < children.length; index++)
        {
            childElement = children[index];

            childObject = visitNode(childElement);
            builder.addChild(newObject, childElement.getNodeName(), childObject);
        }
        return newObject;
    }

    /**
     * This method extracts the screen specifications (which were built up from
     * the old format xml file) from the catalog and creates a 4.0 style XML
     * document for the screen specs. It then writes the document to the
     * screens.cnv file.
     */
    protected void outputScreenSpecs()
    {
        CatalogIfc catalog = null;
        Collection<SpecIfc> specs = null;
        Document doc = null;

        catalog = getCatalog(OVERLAY_SCREEN_SPEC_NAME);
        specs = catalog.getSpecs();
        try
        {
            doc = XMLUtility.createDocument("SCREENS",
                    "classpath://oracle/retail/stores/pos/config/defaults/posui.dtd", true);
            Element screensElem = doc.getDocumentElement();
            Element elem = null;
            OverlayScreenSpec screenSpec = null;
            String specName = null;

            Iterator<SpecIfc> iter = specs.iterator();
            while (iter.hasNext())
            {
                screenSpec = (OverlayScreenSpec) iter.next();
                specName = screenSpec.getSpecName();
                elem = doc.createElement(OVERLAY_SCREEN_SPEC_NAME);
                elem.setAttribute("specName", specName);

                // Create each child element
                Iterator<AssignmentSpec> assignments = screenSpec.getAssignments().iterator();
                boolean promptAndResponsePanelNotFound = true;
                boolean globalNavigationPanelNotFound = true;

                while (assignments.hasNext())
                {
                    Element assignElem = doc.createElement(ASSIGNMENT_SPEC_NAME);
                    AssignmentSpec aSpec = assignments.next();
                    assignElem.setAttribute("areaName", aSpec.getAreaName());
                    assignElem.setAttribute("beanSpecName", aSpec.getBeanSpecName());

                    if (aSpec.getAreaName().equals("PromptAndResponsePanel"))
                    {
                        promptAndResponsePanelNotFound = false;
                    }
                    if (aSpec.getAreaName().equals("GlobalNavigationPanel"))
                    {
                        globalNavigationPanelNotFound = false;
                    }

                    // Create bean property elements
                    Map<String,PropertyTrio> propList = aSpec.getBeanProperties();
                    boolean respFieldNotFound = true;
                    if (propList != null)
                    {
                        Iterator<PropertyTrio> props = propList.values().iterator();
                        while (props.hasNext())
                        {
                            Element propElem = doc.createElement(BEAN_PROPERTY_NAME);
                            PropertyTrio prop = props.next();
                            propElem.setAttribute("propName", prop.getPropName());
                            propElem.setAttribute("propValue", prop.getPropValue());
                            assignElem.appendChild(propElem);
                            if (prop.getPropName().equals("responseField"))
                            {
                                respFieldNotFound = false;
                            }
                        }

                        if (aSpec.getAreaName().equals("PromptAndResponsePanel") && respFieldNotFound)
                        {
                            Element propElem = doc.createElement(BEAN_PROPERTY_NAME);
                            propElem.setAttribute("propName", "responseField");
                            propElem.setAttribute("propValue", "javax.swing.JTextField");
                            assignElem.appendChild(propElem);
                            propElem = doc.createElement(BEAN_PROPERTY_NAME);
                            propElem.setAttribute("propName", "enterData");
                            propElem.setAttribute("propValue", "false");
                            assignElem.appendChild(propElem);
                        }

                        if (aSpec.getAreaName().equals("GlobalNavigationPanel")
                                && screenSpec.getDefaultScreenSpecName().equals("DefaultValidatingSpec"))
                        {
                            Element propElem = doc.createElement(BEAN_PROPERTY_NAME);
                            propElem.setAttribute("propName", "manageNextButton");
                            propElem.setAttribute("propValue", "false");
                            assignElem.appendChild(propElem);
                        }

                        elem.appendChild(assignElem);
                    }
                    else
                    {
                        if (aSpec.getAreaName().equals("WorkPanel")
                                || aSpec.getAreaName().equals("LocalNavigationPanel"))
                        {
                            elem.appendChild(assignElem);
                        }
                    }
                }

                // If there is no prompt and response assignment, we need one
                // for
                // the default response field.
                if (promptAndResponsePanelNotFound)
                {
                    Element assignElem = doc.createElement("ASSIGNMENT");
                    assignElem.setAttribute("areaName", "PromptAndResponsePanel");
                    assignElem.setAttribute("beanSpecName", "PromptAndResponsePanelSpec");
                    Element propElem = doc.createElement(BEAN_PROPERTY_NAME);
                    propElem.setAttribute("propName", "responseField");
                    propElem.setAttribute("propValue", "javax.swing.JTextField");
                    assignElem.appendChild(propElem);
                    propElem = doc.createElement(BEAN_PROPERTY_NAME);
                    propElem.setAttribute("propName", "enterData");
                    propElem.setAttribute("propValue", "false");
                    assignElem.appendChild(propElem);
                    elem.appendChild(assignElem);
                }

                // If there is not global navibation assignment and the this is
                // a validating
                // screen, then add one and add the do not manage next button
                // property.
                if (globalNavigationPanelNotFound
                        && screenSpec.getDefaultScreenSpecName().equals("DefaultValidatingSpec"))
                {
                    Element assignElem = doc.createElement("ASSIGNMENT");
                    assignElem.setAttribute("areaName", "GlobalNavigationPanel");
                    assignElem.setAttribute("beanSpecName", "GlobalNavigationPanelSpec");
                    Element propElem = doc.createElement(BEAN_PROPERTY_NAME);
                    propElem.setAttribute("propName", "manageNextButton");
                    propElem.setAttribute("propValue", "false");
                    assignElem.appendChild(propElem);
                    elem.appendChild(assignElem);
                }

                // Create each child element
                Iterator<ConnectionSpec> connections = screenSpec.getConnections().iterator();
                while (connections.hasNext())
                {
                    Element connectElem = doc.createElement("CONNECTION");
                    ConnectionSpec cSpec = connections.next();

                    connectElem.setAttribute("sourceBeanSpecName", cSpec.getSourceBeanSpecName());
                    connectElem.setAttribute("targetBeanSpecName", cSpec.getTargetBeanSpecName());
                    connectElem.setAttribute("listenerPackage", cSpec.getListenerPackage());
                    connectElem.setAttribute("listenerInterfaceName", cSpec.getListenerInterfaceName());

                    elem.appendChild(connectElem);
                }

                elem.setAttribute("defaultScreenSpecName", screenSpec.getDefaultScreenSpecName());
                screensElem.appendChild(elem);
            }
            String start = XMLUtility.buildXMLString(doc);
            UICFGReformater formater = new UICFGReformater(start);
            String screens = formater.getFormatedString();

            // Write output to a screens.xml file
            File currentDirectory = new File(System.getProperty("user.dir"));
            File tempFile = new File(currentDirectory, "screens.cnv");
            try (FileOutputStream fileOutStream = new FileOutputStream(tempFile);)
            {
                try (DataOutputStream dataOutStream = new DataOutputStream(fileOutStream);)
                {
                    // Write out the formatted xml
                    dataOutStream.writeBytes(screens);
                }
            }
        }
        catch (InvalidXmlException exc)
        {
            logger.error("XML ERROR: " + exc.getMessage());
        }
        catch (IOException ioexc)
        {
            logger.error("Error writing file " + ioexc.getMessage());
        }
    }

    /**
     * This method extracts the bean specifications (which were built up from
     * the old format xml file) from the catalog and creates a 4.0 style XML
     * document for the bean specs. It then writes the document to the beans.cnv
     * file.
     */
    protected void outputBeanSpecs()
    {
        CatalogIfc catalog = null;
        Collection<SpecIfc> specs = null;
        Document doc = null;

        catalog = getCatalog(BEAN_SPEC_NAME);
        specs = catalog.getSpecs();
        try
        {
            doc = XMLUtility.createDocument("BEANS", "classpath://oracle/retail/stores/pos/config/defaults/posui.dtd",
                    true);
            Element beansElement = doc.getDocumentElement();
            Element beanElem = null;
            BeanSpec beanSpec = null;
            String specName = null;

            Iterator<SpecIfc> iter = specs.iterator();
            while (iter.hasNext())
            {
                beanSpec = (BeanSpec)iter.next();
                specName = beanSpec.getSpecName();
                beanElem = doc.createElement("BEAN");
                beanElem.setAttribute("specName", specName);
                beanElem.setAttribute("configuratorPackage", "oracle.retail.stores.pos.ui");
                beanElem.setAttribute("configuratorClassName", "POSBeanConfigurator");
                beanElem.setAttribute("beanPackage", beanSpec.getPackage());
                beanElem.setAttribute("beanClassName", beanSpec.getJavaClassName());

                // Create each child button

                ButtonSpec[] buttons = beanSpec.getButtons();
                for (int i = 0; i < buttons.length; i++)
                {
                    Element buttonElem = doc.createElement("BUTTON");
                    buttonElem.setAttribute(UIConfigIfc.ACTIONNAME, buttons[i].getActionName());
                    buttonElem.setAttribute(UIConfigIfc.KEYNAME, buttons[i].getKeyName());
                    if (buttons[i].isEnabled())
                    {
                        buttonElem.setAttribute(UIConfigIfc.ENABLED, Boolean.TRUE.toString());
                    }
                    else
                    {
                        buttonElem.setAttribute(UIConfigIfc.ENABLED, Boolean.FALSE.toString());
                    }

                    String actionListenerName = buttons[i].getActionListenerName();
                    if (actionListenerName != null)
                    {
                        buttonElem.setAttribute("actionListener", actionListenerName);
                    }
                    String label = buttons[i].getLabel();
                    if (label != null)
                    {
                        buttonElem.setAttribute(UIConfigIfc.LABEL, label);
                    }

                    String iconName = buttons[i].getIconName();
                    if (iconName != null)
                    {
                        buttonElem.setAttribute(UIConfigIfc.ICON, iconName);
                    }
                    beanElem.appendChild(buttonElem);
                }

                // Create each property
                Map<String,PropertyTrio> propList = beanSpec.getBeanProperties();
                if (propList != null)
                {
                    Iterator<PropertyTrio> props = propList.values().iterator();
                    while (props.hasNext())
                    {
                        Element propElem = doc.createElement(BEAN_PROPERTY_NAME);
                        PropertyTrio prop = props.next();
                        propElem.setAttribute("propName", prop.getPropName());
                        propElem.setAttribute("propValue", prop.getPropValue());
                        beanElem.appendChild(propElem);
                    }
                }
                beansElement.appendChild(beanElem);
            }
            String start = XMLUtility.buildXMLString(doc);
            UICFGReformater formater = new UICFGReformater(start);
            String screens = formater.getFormatedString();

            // Write output to a screens.xml file
            File currentDirectory = new File(System.getProperty("user.dir"));
            File tempFile = new File(currentDirectory, "beans.cnv");
            try (FileOutputStream fileOutStream = new FileOutputStream(tempFile);)
            {
                try (DataOutputStream dataOutStream = new DataOutputStream(fileOutStream);)
                {
                    // Write out the formatted xml
                    dataOutStream.writeBytes(screens);
                }
            }
        }
        catch (InvalidXmlException exc)
        {
            logger.error("XML ERROR: " + exc.getMessage());
        }
        catch (IOException ioexc)
        {
            logger.error("Error writing file: " + ioexc.getMessage());
        }
    }

    /**
     * The main method is entry point for this class. The element in the args
     * array must contain the name of the file to convert.
     * 
     * @param args the command line parameters; the first element contains the
     *            conversion file name.
     */
    public static void main(String[] args)
    {
        // Check the number of arguments. Must have a data source
        if (args.length < 1)
        {
            System.out.println("Usage: ConversionUILoader <dataSourceName>");
        }
        else
        {
            ConversionUILoader testLoader = new ConversionUILoader();

            try
            {
                testLoader.readSpecs(args[0]);
                testLoader.outputScreenSpecs();
                testLoader.outputBeanSpecs();
            }
            catch (ConfigurationException ce)
            {
                logger.fatal("Error reading specifications");
                logger.fatal(Util.throwableToString(ce));
                ce.printStackTrace();
            }

        }
    }

}
