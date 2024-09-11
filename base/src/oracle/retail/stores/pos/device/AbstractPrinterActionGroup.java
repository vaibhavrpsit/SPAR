/* ===========================================================================
* Copyright (c) 2009, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/AbstractPrinterActionGroup.java /main/16 2014/02/05 15:03:00 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  01/31/14 - released the stream handles
 *    abondala  09/04/13 - initialize collections
 *    sgu       05/30/13 - support multiple copies of receipt
 *    blarsen   10/03/12 - Changed cached FOP printer to a hash map. This
 *                         supports MPOS' multi-network-printer feature. Each
 *                         MPOS device can be assigned a unique network
 *                         printer.
 *    blarsen   08/30/12 - Merge project Echo (MPOS) into Trunk.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    mjwallac  05/02/12 - Fortify: fix redundant null checks, part 4
 *    cgreene   12/20/11 - refactor some actiongroup settings into print
 *                         manager
 *    cgreene   12/19/11 - update scripting constants
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    cgreene   10/26/10 - refactor alwaysPrintLineFeed into
 *                         AbstractPrinterGroup
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    12/16/09 - cleanup
 *    acadar    12/15/09 - set frankingCapable to false by default
 *    acadar    12/15/09 - cleanup and refactoring of PdfReceiptPrinter's use
 *                         in POSPrinterActionGroup
 *    acadar    12/14/09 - cleanup
 *    acadar    12/14/09 - made printNormal an abstract method
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.XMLManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc;
import oracle.retail.stores.foundation.manager.xml.InvalidXmlException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.dtd.DeviceScriptIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.ReflectionUtility;
import oracle.retail.stores.foundation.utility.xml.XMLUtility;
import oracle.retail.stores.manager.device.FoReceiptPrinter;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedDocumentManager;
import oracle.retail.stores.pos.receipt.blueprint.BlueprintedReceipt;
import oracle.retail.stores.printing.DefaultIppFactory;
import oracle.retail.stores.printing.IppFactoryIfc;
import oracle.retail.stores.printing.PrintRequestAttributesIfc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * AbstractPrinterActionGroup defines the Printer specific device operations
 * available to POS applications.
 */
public abstract class AbstractPrinterActionGroup extends POSDeviceActionGroup
    implements PrinterActionGroupIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -9070542490763838714L;

	/**
	 * number of characters in a receipt print line
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
	 */
    protected int receiptLineSize = 40;

    /**
     * Reference to manager with the configuration settings for the receipts.
     */
    protected transient BlueprintedDocumentManager receiptManager;

    /**
     * Char widths
     */
    protected Map<String,Integer> characterWidths = new HashMap<String,Integer>(2);

    /**
     * Always print line feeds at the end of lines (don't rely on the printer's
     * wrap capability to "make" line feeds)
     */
    protected boolean alwaysPrintLineFeeds = false;

    /**
     * franking capable flag
     */
    protected boolean frankingCapable = false;

    /** FO Receipt Printer for clean receipt/IPP */
    protected Map<String, FoReceiptPrinter> fopReceiptPrinters = new ConcurrentHashMap<String, FoReceiptPrinter>();

    /** Clean receipt/IPP configuration file for fop
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    protected String eReceiptFopConfig;

    /** Clean Receipt/IPP Configuration file for xml
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    protected String eReceiptXmlConfig;

    /** The directory containing (*.xml) files for clean receipt/ipp templates
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    protected String xmlFilesDirectory;

    /** The directory containing (*.fo) files for clean receipt/ipp templates
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    protected String fopFilesDirectory;

    /** EReceipt/IPP attributes
     * @deprecated as of 13.4. Use configurations in DeviceContext.xml instead.
     */
    protected PrintRequestAttributesIfc settings;

    /** Factory Instance used for clean receipt/ipp
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    private IppFactoryIfc factoryInstance;

    /** factory class name
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    private String factoryClassName;

    /** Configuration file for the fop fonts
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    private String fopFontConfigFile;

    /**  Mime type for fo rendering*/
    protected String foMimeType = "application/pdf";

    /** File Extension for fo files used by the ereceipt/ipp **/
    protected String foFileExtension = "pdf";

    /**
     * Prints the document
     *
     * @param EYSPrintableTransactionIfc
     * @throws DeviceException if error occurs
     */
    public abstract void printDocument(EYSPrintableDocumentIfc document) throws DeviceException;

    /**
     * Prints fiscal receipt
     *
     * @param EYSPrintableTransactionIfc
     * @throws DeviceException if error occurs
     */
    public void printFiscalReceipt(EYSPrintableDocumentIfc document) throws DeviceException
    {
        // Do nothing
    }

    /**
     * Prints X report from fiscal printer
     *
     * @param EYSPrintableDocumentIfc
     * @throws DeviceException if error occurs
     */
    public void printFiscalXReport() throws DeviceException
    {
        //Do nothing
    }

    /**
     * Prints X report from the fiscal printer.
     *
     * @throws DeviceException if error occurs
     */
    public void printFiscalZReport() throws DeviceException
    {
        //Do nothing
    }

    /**
     * Sets fiscal printer date.
     *
     * @param EYSPrintableDocumentIfc
     * @throws DeviceException if error occurs
     */
    public void setFiscalPrinterDate() throws DeviceException
    {
        //Do nothing
    }

    /**
     * PrintNormal
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#printNormal
     */
    public abstract void printNormal(int station, String data) throws DeviceException;

    /**
     * Gets the Printer attributes
     * @return
     * @deprecated as of 13.4. Use configurations in DeviceContext.xml instead.
     */
    public abstract PrintRequestAttributesIfc getSettings();

    /**
     * @return the foFileExtension
     */
    public String getFoFileExtension()
    {
        return foFileExtension;
    }

    /**
     * @param foFileExtension the foFileExtension to set
     */
    public void setFoFileExtension(String foFileExtension)
    {
        this.foFileExtension = foFileExtension;
    }

    /**
     * @return the fopFontConfigFile
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    public String getFopFontConfigFile()
    {
        return fopFontConfigFile;
    }

    /**
     * @param fopFontConfigFile the fopFontConfigFile to set
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    public void setFopFontConfigFile(String fopFontConfigFile)
    {
        this.fopFontConfigFile = fopFontConfigFile;
    }

    /**
     * Creates an IppFactory instance
     * @return
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
     public IppFactoryIfc getFactoryInstance()
    {
       if(factoryInstance == null)
       {
            try
            {
                factoryInstance = (IppFactoryIfc)Class.forName(factoryClassName).newInstance();
                factoryInstance.setFontConfig(this.fopFontConfigFile);

             }
             catch (Exception e)
             {
               logger.error("Cannot create instance for: " + factoryClassName, e);
                //return default factory
               factoryInstance = new DefaultIppFactory();
            }
       }
        return factoryInstance;
    }

    /**
     *  Sets the name of the factory class
     * @param factory
     */
    public void setFactoryClassName(String factory)
    {
        this.factoryClassName = factory;
    }

    /**
     * @param settings the settings to set
     * @deprecated as of 13.4. Use configurations in DeviceContext.xml instead.
     */
    public void setSettings(PrintRequestAttributesIfc settings)
    {
        this.settings = settings;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.device.PrinterActionGroupIfc#getCharWidths()
     */
    public Map<String,Integer> getCharWidths()
    {
        return characterWidths;
    }

    /**
     * This method converts the string defined in posdevices.xml into a hash map
     * of UnicodeBlock names and their printed widths.
     *
     * @see oracle.retail.stores.pos.device.POSPrinterActionGroupIfc#setCharacterWidths
     */
    public void setCharacterWidths(String charWidths)
    {
        parseTokenString(characterWidths, charWidths);
    }

    /**
     * Retrieves the number of characters on a slip print line. This must be set
     * to a valid value for the hardware in use.
     *
     * @return number of receipt line characters
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    public int getReceiptLineSize()
    {
        return receiptLineSize;
    }

    /**
     * Sets the number of characters on a receipt print line. This must be set
     * to a valid value for the hardware in use.
     *
     * @param value number of receipt line characters
     * @deprecated as of 13.4. See configuration in DeviceContext.xml instead.
     */
    public void setReceiptLineSize(int value)
    {
        receiptLineSize = value;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.device.PrinterActionGroupIfc#isFrankingCapable()
     */
    public Boolean isFrankingCapable() throws DeviceException
    {
        return this.frankingCapable? Boolean.TRUE : Boolean.FALSE;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.device.PrinterActionGroupIfc#setFrankingCapable(boolean)
     */
    public void setFrankingCapable(boolean frankingCapable)
    {
        this.frankingCapable = frankingCapable;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.device.PrinterActionGroupIfc#isAlwaysPrintLineFeeds()
     */
    public boolean isAlwaysPrintLineFeeds()
    {
        return alwaysPrintLineFeeds;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.device.PrinterActionGroupIfc#setAlwaysPrintLineFeeds(boolean)
     */
    public void setAlwaysPrintLineFeeds(boolean alwaysPrintLineFeeds)
    {
        this.alwaysPrintLineFeeds = alwaysPrintLineFeeds;
    }

    /**
     * This is a utility function to parse token paired values from a string specified in the posdevices.xml
     *
     * This is used for the franking start lines  and the character widths specifications
     *
     * @param mappedValues
     * @param tokenizedValues
     */
    protected static void parseTokenString(Map<String, Integer> mappedValues, String tokenizedValues)
    {
        StringTokenizer tokenizer = new StringTokenizer(tokenizedValues, ",");
        while (tokenizer.hasMoreTokens())
        {
            String keyValuePair = tokenizer.nextToken();
            StringTokenizer keyValuePairTokenizer = new StringTokenizer(keyValuePair, "=");

            String key = keyValuePairTokenizer.nextToken();
            String value = keyValuePairTokenizer.nextToken();

            mappedValues.put(key, Integer.valueOf(value));
        }
    }

    /**
     * Prints a report
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#printReceipt
     */
    public  void printReport(String reportType, Serializable rptObj) throws DeviceException
    {
        //do nothing
    }

    /**
     * CutPaper
     *
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#cutPaper
     */
    public void cutPaper(int percentage) throws DeviceException
    {
        //do nothing
    }

    /**
     * @return the fopConfig
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    public String getEReceiptFopConfig()
    {
        return eReceiptFopConfig;
    }

    /**
     * @param fopConfig
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    public void setEReceiptFopConfig(String fopConfig)
    {
        this.eReceiptFopConfig = fopConfig;
    }

    /**
     * @return the xmlConfig
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    public String getEReceiptXmlConfig()
    {
        return eReceiptXmlConfig;
    }

    /**
     * Sets the xmlConfiguration file
     * @param xmlConfig
     * @deprecated as of 14.0. This configuration has moved to {@link BlueprintedDocumentManager}.
     */
    public void setEReceiptXmlConfig(String xmlConfig)
    {
        this.eReceiptXmlConfig = xmlConfig;
    }

    /**
     * @return the foMimeType
     */
    public String getFoMimeType()
    {
        return foMimeType;
    }

    /**
     * @param foMimeType the foMimeType to set
     */
    public void setFoMimeType(String foMimeType)
    {
        this.foMimeType = foMimeType;
    }

    /**
     * Configures this manager per the script file name specified.
     */
    protected void configure(String configScript)
    {
        //  Local variables
        Document xmlConfig = null;
        Element xmlRoot = null;

        //  Get XMLManager
        XMLManagerIfc xmlMgr =
            (XMLManagerIfc) Gateway.getDispatcher().getManager(
                XMLManagerIfc.TYPE);

        if (xmlMgr != null)
        {
            //  Get XML configuration file
            xmlConfig = xmlMgr.getXMLTree(configScript);

            if (xmlConfig != null)
            {
                //  Get the document element for the XML file that will be
                //  used for searching the file structure
                xmlRoot = xmlConfig.getDocumentElement();

                // delegate with element parameter
                configure(xmlRoot, configScript);

            }
            else
            {
                //  If no XML configuration file is found, log error
                logger.error(
                        getClass().getName() +  ".configure(String) config script file not found: " + configScript);
            }
        }
        else
            //  If no XMLManager is found, log error
        {
            logger.error(getClass().getName() +  ".configure(String): XMLManager not found.");
        }
    }

    /**
     * Configure xml
     * @param xmlRoot
     * @param configScript
     */
    protected void configure(Element xmlRoot, String configScript)
    {
        Element[] dmProps = XMLUtility.getChildElements(xmlRoot, DeviceScriptIfc.ELEM_PROPERTY);

        for (int y = 0; y < dmProps.length; y++)
        {
            try
            {
                String dmPropName = dmProps[y].getAttribute(DeviceScriptIfc.ATTR_PROPNAME);
                String dmPropValue = dmProps[y].getAttribute(DeviceScriptIfc.ATTR_PROPVALUE);
                String dmPropType = dmProps[y].getAttribute(DeviceScriptIfc.ATTR_PROPTYPE);
                if (logger.isDebugEnabled())
                    logger.debug("set prop " + dmPropName + " to " + dmPropValue);
                ReflectionUtility.setProperty(this, dmPropName, dmPropType, dmPropValue);
            }
            catch (Exception e)
            {
                logger.error("Error reading config script " + configScript, e);
            }
        }
    }

    /**
     * Set the receipt printer onto the document being printed. Optionally
     * apply blueprint-specific settings if the doc is a
     * {@link BlueprintedReceipt}.
     *
     * @param document
     * @param receiptPrinter
     *
     * @see #getReceiptLineSize()
     * @see #alwaysPrintLineFeeds()
     * @see #getCharWidths()
     */
    protected void applyReceiptPrinter(EYSPrintableDocumentIfc document, ReceiptPrinterIfc receiptPrinter)
    {
        document.setPrinter(receiptPrinter);
        if (document instanceof BlueprintedReceipt)
        {
            ((BlueprintedReceipt)document).setAlwaysPrintLineFeeds(isAlwaysPrintLineFeeds());
            ((BlueprintedReceipt)document).setCharWidths(getCharWidths());
        }
    }

    /**
     * Lazily initialize the reference to the {@link BlueprintedDocumentManager}
     * from the {@link Dispatcher} and return.
     *
     * @return
     */
    protected BlueprintedDocumentManager getReceiptManager()
    {
        if (receiptManager == null)
        {
            receiptManager = (BlueprintedDocumentManager)Dispatcher.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
        }
        return receiptManager;
    }

    /**
     * Gets the XML Document object for the IPP template for the specified
     * receipt type.
     *
     * @param documentType
     * @return Document
     */
    protected Document getXMLTemplate(String documentType)
    {
        return getXMLTemplate(documentType, false);
    }

    /**
     * Gets the XML Document object for the IPP template for the specified
     * receipt type.
     *
     * @param documentType
     * @param ereceipt
     * @return Document
     */
    protected Document getXMLTemplate(String documentType, boolean ereceipt)
    {

        logger.debug("getting XML template for: " + documentType);

        // get name of file for bean from resource bundle

        String filePath = getReceiptManager().getIppXMLTemplate(documentType);
        if (ereceipt)
        {
            filePath = getReceiptManager().getEReceiptXMLTemplate(documentType);
        }

        Document doc = loadTemplateDocument(filePath);
        return doc;
    }

    /**
     * Returns the FOP template
     * @return Source
     */
    protected Document getFOPTemplate(String documentType) 
    {
        return getFOPTemplate(documentType, false);
    }

    /**
     * Returns the FOP template
     * @return Source
     */
    protected Document getFOPTemplate(String documentType, boolean ereceipt) 
    {
        logger.debug("getting FOP template for: " + documentType);
        // get name of file for bean from resource bundle

        String filePath = getReceiptManager().getIppFOPTemplate(documentType);
        if (ereceipt)
        {
            filePath = getReceiptManager().getEReceiptFOPTemplate(documentType);
        }

        Document doc = loadTemplateDocument(filePath);
        return doc;
    }
    
    /**
     * Load XML or FOP template document
     * @param filePath the file path to the document
     * @return the template document
     */
    protected Document loadTemplateDocument(String filePath)
    {
        File file = new File(filePath);

        InputStream is = null;

        Document doc = null;
        try
        {
            if (file.exists())
            {
                logger.debug("Found File at " + filePath);
                is = new FileInputStream(file);
            }
            else
            {
                // try classpath
                logger.debug("File not found at " + filePath + ". Loading from classpath.");
                is = getClass().getResourceAsStream(filePath);
            }

            doc = XMLUtility.getDocument(is, /*validate*/false, /*namespaceAware*/true, /*exceptOnWarning*/true);
        }
        catch (FileNotFoundException fe)
        {
            logger.error("Template not found ", fe);
        }
        catch (InvalidXmlException xmle)
        {
            logger.error("Template invalid ", xmle);
        }
        finally
        {
            if(is!=null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    logger.error("Unable to release stream", e);
                }
            }
        }
        return doc;
    }

    /**
     * Lookup and return the specified printer.
     * If the printer isn't already cached, create it, cache it and return it.
     *
     * @param printerID the bean id for the printer
     */
    protected FoReceiptPrinter lookupFopReceiptPrinter(String printerID)
    {
        FoReceiptPrinter fopReceiptPrinter = fopReceiptPrinters.get(printerID);
        if (fopReceiptPrinter == null)
        {
            fopReceiptPrinter = (FoReceiptPrinter) BeanLocator.getDeviceBean(printerID);
            if (fopReceiptPrinter != null)
            {
                fopReceiptPrinters.put(printerID, fopReceiptPrinter);
            }
        }

        return fopReceiptPrinter;
    }

}
