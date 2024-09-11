/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/blueprint/BlueprintedFiscalReceipt.java /main/6 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    mchellap  10/09/12 - Initialize refund receipts using JPOS fiscal receipt
 *                         type property
 *    mchellap  09/27/12 - Fixed fiscal receipt store copy printing
 *    mchellap  08/30/12 - Fixed fiscal printer franking
 *    mchellap  08/17/12 - Add fiscal line depends on support
 *    mchellap  08/10/12 - Add fiscal printer support
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.FiscalReceiptPrinter;
import oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.receipts.core.MethodResultsCache;
import oracle.retail.stores.receipts.model.AbstractReport;
import oracle.retail.stores.receipts.model.Blueprint;
import oracle.retail.stores.receipts.model.Element;
import oracle.retail.stores.receipts.model.FiscalLine;
import oracle.retail.stores.receipts.model.FiscalLineParameter;
import oracle.retail.stores.receipts.model.Group;
import oracle.retail.stores.receipts.model.ImageElement;
import oracle.retail.stores.receipts.model.Line;
import oracle.retail.stores.receipts.model.LinkReport;
import oracle.retail.stores.receipts.model.MethodElement;
import oracle.retail.stores.receipts.model.Report;
import oracle.retail.stores.receipts.printing.PrintingException;
import jpos.FiscalPrinterConst;
import jpos.JposException;

/**
 * A fiscal receipt that prints based upon instructions gathered from a
 * blueprint and using data from the {@link #parameterBean}.
 * 
 * @since 14.0
 */
public class BlueprintedFiscalReceipt extends BlueprintedReceipt implements EYSPrintableDocumentIfc
{
    /** Key to this class or subclass in the ApplicationContext.xml */
    public static final String BEAN_KEY = "application_BlueprintedFiscalReceipt";

    /** revision number of this class */
    public static final String revisionNumber = "$Revision: /main/6 $";

    public Map<String, Class> primitiveTypeMap = new HashMap<String, Class>(10)
    {

        {
            put("int", Integer.TYPE);
            put("long", Long.TYPE);
            put("double", Double.TYPE);
            put("float", Float.TYPE);
            put("boolean", Boolean.TYPE);
            put("char", Character.TYPE);
            put("byte", Byte.TYPE);
            put("void", Void.TYPE);
            put("short", Short.TYPE);
            put("String", String.class);
        }
    };

    /**
     * Receipt specific header/footer lines.
     */
    protected StringBuilder receiptInfoLines = new StringBuilder();

    private boolean printingInitialized = false;

    /**
     * @return the printingInitialized
     */
    public boolean isPrintingInitialized()
    {
        return printingInitialized;
    }

    /**
     * @param printingInitialized the printingInitialized to set
     */
    public void setPrintingInitialized(boolean printingInitialized)
    {
        this.printingInitialized = printingInitialized;
    }

    /**
     * Convenience constructor. Call {@link #setBlueprint(Blueprint)} before
     * using the {@link #printDocument()} method.
     */
    public BlueprintedFiscalReceipt()
    {
        this(null);
    }

    /**
     * Construct this receipt with the specified blueprint. Sets
     * {@link #alwaysPrintLineFeeds()} to false.
     * 
     * @param blueprint
     */
    public BlueprintedFiscalReceipt(Blueprint blueprint)
    {
        super(blueprint);
        this.blueprint = blueprint;
        line = new StringBuilder(50);
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.receipt.EYSPrintableDocument#printDocument()
     */
    @Override
    public void printDocument() throws DeviceException
    {

        // must be called before printing
        printer.startJob();

        setPrintingInitialized(false);

        // change the character set to one mapped to locale
        printer.switchCharacterSet(blueprint.getReceiptLocale(), blueprint.getDefaultLocale());

        try
        {
            print(blueprint, getParameterBean());
        }
        catch (PrintingException e)
        {
            if (e.getCause() instanceof DeviceException)
            {
                throw (DeviceException)e.getCause();
            }
            else if (e.getCause() instanceof JposException)
            {
                throw new DeviceException(DeviceException.JPOS_ERROR, e.getLocalizedMessage(), e);
            }
            else
            {
                throw new DeviceException(DeviceException.UNKNOWN, "Undetermined Exception", e);
            }
        }
        // Reset the printer mode to MONITOR state
        if (!isPreview())
        {
            if (isPrintingInitialized() && getParameterBean().isTrainingMode())
            {
                getFiscalReceiptPrinter().endTrainingModePrinting();
            }
            else if (isPrintingInitialized() && getFiscalPrinterState() != FiscalPrinterConst.FPTR_PS_MONITOR)
            {
                getFiscalReceiptPrinter().endNonFiscalPrinting();
            }
        }

        printer.endJob();

    }

    /**
     * @throws PrintingException
     */
    @Override
    protected void printBlueprint(Blueprint blueprint) throws PrintingException
    {
        resetColumnIndex();

        for (int copyIndex = 0; copyIndex < blueprint.getCopies(); copyIndex++)
        {
            // for eReceipt, check if more than one copies
            if (blueprint.getCopies() > 1 && getParameterBean() instanceof ReceiptParameterBeanIfc)
            {
                // check if SaleReturnTransaction
                if (((ReceiptParameterBeanIfc)getParameterBean()).getTransaction() instanceof SaleReturnTransactionIfc)
                {
                    ReceiptParameterBeanIfc parameters = (ReceiptParameterBeanIfc)getParameterBean();

                    // check if eReceipt
                    if (parameters.isEreceipt())
                    {
                        // If not customer copy then continue
                        if (copyIndex != ReceiptConstantsIfc.CUSTOMER_COPY_INDEX)
                        {
                            continue;
                        }
                    }
                    // If printing only store copies then continue if customer
                    // copy.
                    else if (parameters.isPrintStoreReceipt() && copyIndex == ReceiptConstantsIfc.CUSTOMER_COPY_INDEX)
                    {
                        continue;
                    }
                }
            }

            for (AbstractReport report : blueprint.getReports())
            {
                if (shouldPrint(report))
                {
                    printReport(report, copyIndex);
                }
            }
        }

        // flush buffer
        if (line.length() > 0)
        {
            flush();
        }
    }

    /**
     * Determine whether to print this report. This method inspects whether the
     * {@link Report#getDependsOn()} is null or not. If non-null, the method is
     * invoked on the {@link #getParameterBean()} and if the result is null or
     * false this method returns false.
     * 
     * @param report
     * @return
     * @throws DeviceException
     */
    protected void printNonFiscalReports(Blueprint blueprint) throws DeviceException
    {
        List<AbstractReport> reports = blueprint.getReports();
        // create a clear cache;
        methodResultsCache = new MethodResultsCache(getParameterBean(), blueprint.getReceiptLocale(),
                blueprint.getDefaultLocale());

        for (AbstractReport report : reports)
        {
            if (report instanceof LinkReport)
            {
                // If its a fiscal report then skip
                if (((LinkReport)report).getReportType() != null
                        && ((LinkReport)report).getReportType().startsWith("Fiscal"))
                {
                    continue;
                }
                try
                {
                    if (report.getDependsOn() != null)
                    {
                        Object results = methodResultsCache.invoke(report.getDependsOn());
                        if (results instanceof Boolean)
                        {
                            if (((Boolean)results).booleanValue())
                            {
                                printReport(report, 1);
                            }
                        }
                    }
                    else
                    {
                        String reportType = ((LinkReport)report).getReportType();
                        if (reportType != null && !reportType.startsWith("Fiscal"))
                        {
                            printReport(report, 1);
                        }
                    }
                }

                catch (Exception e)
                {
                    // do nothing
                }
            }
        }

        // Fiscal printers do not support cut operation, to cut the
        // paper the receipt has to be ended
        if (isPrintingInitialized() && !getParameterBean().isTrainingMode()
                && (getFiscalPrinterState() == FiscalPrinterConst.FPTR_PS_NONFISCAL))
        {
            getFiscalReceiptPrinter().endNonFiscalPrinting();
        }
        else if (!isPreview() && getParameterBean().isTrainingMode())
        {
            getFiscalReceiptPrinter().endTrainingModePrinting();
        }

    }

    /**
     * Print a report once.
     * 
     * @param report
     */
    @Override
    protected void printReport(AbstractReport report, int copyIndex) throws PrintingException
    {
        // Only way to handle cut paper is to start and stop the receipt
        // printing.
        try
        {

            if (!isPrintingInitialized() && getParameterBean().isTrainingMode()
                    && (getFiscalPrinterState() == FiscalPrinterConst.FPTR_PS_MONITOR))
            {
                getFiscalReceiptPrinter().startTrainingModePrinting();
                setPrintingInitialized(true);
            }
            else if (!isPreview() && (getFiscalPrinterState() == FiscalPrinterConst.FPTR_PS_MONITOR))
            {
                getFiscalReceiptPrinter().startNonFiscalPrinting();
                setPrintingInitialized(true);
            }

            if (report instanceof LinkReport)
            {
                printLinkReport((LinkReport)report);
            }
            else
            {
                for (Group group : ((Report)report).getGroups())
                {
                    printGroup(group, copyIndex);
                }
                flush();
            }

            // cut the paper or end
            if (report.isCutPaper())
            {
                // Fiscal printers do not support cut operation, to cut the
                // paper the receipt has to be ended
                if (isPrintingInitialized() && !getParameterBean().isTrainingMode()
                        && (getFiscalPrinterState() == FiscalPrinterConst.FPTR_PS_NONFISCAL))
                {
                    getFiscalReceiptPrinter().endNonFiscalPrinting();
                }
                else if (!isPreview() && getParameterBean().isTrainingMode())
                {
                    getFiscalReceiptPrinter().endTrainingModePrinting();
                }

            }

        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }

        resetColumnIndex();
    }

    /**
     * Returns fiscal printer status
     * 
     * @param report
     */
    protected int getFiscalPrinterState() throws DeviceException
    {
        try
        {
            return getFiscalReceiptPrinter().getPrinter().getPrinterState();
        }
        catch (JposException e)
        {
            throw new DeviceException("Exception while getting fiscal printer status" + e.getCause());
        }
    }

    /**
     * Prints a fiscal receipt
     */
    public void printFiscalReceipt() throws DeviceException
    {
        // must be called before printing
        printer.startJob();

        if (!isPreview())
        {
            try
            {

                // set the printing flag,this will become true if atleaset one
                // fiscal line is present in the bpt.
                setPrintingInitialized(false);
                // The receipt specific header lines like transaction id,
                // customer etc needs to be set before start printing the
                // receipt.
                // Fiscal printers reset the receipt specific headers after
                // printing each receipt.
                getFiscalReceiptPrinter().resetPrinter();
                
                // create a clear cache;
                methodResultsCache = new MethodResultsCache(getParameterBean(), blueprint.getReceiptLocale(),
                        blueprint.getDefaultLocale());

                String javaVendor = System.getProperty("java.vendor");
                if (javaVendor != null)
                {
                    boolean isIBMjre = javaVendor.startsWith("IBM");

                    if (isIBMjre)
                    {
                        java.lang.Compiler.disable();
                    }

                    try
                    {
                        printFiscalBlueprint(blueprint);
                    }
                    finally
                    {
                        if (isIBMjre)
                        {
                            java.lang.Compiler.enable();
                        }
                        methodResultsCache = null;
                    }
                }
            }
            catch (PrintingException e)
            {
               
                if (e.getCause() instanceof DeviceException)
                {
                    throw (DeviceException)e.getCause();
                }
                else if (e.getCause() instanceof JposException)
                {
                    throw new DeviceException(DeviceException.JPOS_ERROR, e.getLocalizedMessage(), e);
                }
                else
                {
                    throw new DeviceException(DeviceException.UNKNOWN, "Undetermined Exception", e);
                }
            }

            // end fiscal receipt printing, this changes the printer mode to
            // MONITOR
            // Reset the printer mode to MONITOR state
            if (getParameterBean().isTrainingMode() && isPrinterInitialized())
            {
                getFiscalReceiptPrinter().endTrainingModePrinting();
            }
            else if (isPrintingInitialized())
            {
                getFiscalReceiptPrinter().endFiscalPrinting();
            }

            // Print non fiscal slips linked to the fiscal receipt
            printNonFiscalReports(blueprint);

        }

        printer.endJob();

    }

    /**
     * Sets receipt specific header details to fiscal printer memory. The memory
     * is reset after printing each fiscal receipt.
     * 
     * @param Blueprint The fiscal blueprint
     */
    protected void setReceiptSpecificHeader(AbstractReport headerReport) throws PrintingException
    {
        // Get the printable header lines from the header blueprint
        setPreview(true);

        String lines = getReportPreview(headerReport);
        try
        {
            if (lines.length() > 0)
                ((FiscalReceiptPrinter)printer).setAdditionalHeaderLines(lines);
        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }
        finally
        {
            setPreview(false);
        }
    }

    /**
     * Prints receipt messages after Totals section. The fiscal printer should
     * be in R
     * 
     * @param Blueprint The fiscal blueprint
     */
    protected void printReceiptMessages(AbstractReport headerReport) throws PrintingException
    {
        // Get the printable header lines from the header blueprint
        setPreview(true);

        String lines = getReportPreview(headerReport);
        try
        {
            if (lines.length() > 0)
            {
                StringTokenizer messages = new StringTokenizer(lines, "\n");

                while (messages.hasMoreTokens())
                {
                    ((FiscalReceiptPrinter)printer).printReceiptMessage(messages.nextToken());
                }

            }
        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }
        finally
        {
            setPreview(false);
        }
    }

    /**
     * Sets receipt specific footer details to fiscal printer memory. The memory
     * is reset after printing each fiscal receipt.
     * 
     * @param Blueprint The fiscal blueprint
     */
    protected void setReceiptSpecificFooter(AbstractReport report) throws PrintingException
    {
        setPreview(true);
        String lines = getReportPreview(report);

        if (lines != null && lines.length() > 200)
        {
            lines = lines.substring(200);
        }
        try
        {
            ((FiscalReceiptPrinter)printer).setAdditionalFooterLines(lines);
        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }
        finally
        {
            setPreview(false);
        }

    }

    /**
     * Generates preview for the given report.
     * 
     * @param Report The fiscal blueprint
     */
    protected String getReportPreview(AbstractReport report) throws PrintingException
    {
        // Do not print the lines to the printer, just get the string
        setPreview(true);
        String preview = "";
        if (getPreviewText().length() > 0)
            previewText.setLength(0);
        methodResultsCache = new MethodResultsCache(getParameterBean(), blueprint.getReceiptLocale(),
                blueprint.getDefaultLocale());

        if (report instanceof LinkReport)
        {
            printLinkReport((LinkReport)report);
        }
        else
        {
            printReport(report, 0);
        }
        methodResultsCache.clear();

        if (getPreviewText().length() > 0)
        {
            preview = getPreviewText().toString();
            previewText.setLength(0);

        }
        setPreview(false);

        return preview;

    }

    /**
     * Print header/footer report once.
     * 
     * @param report
     * @throws PrintingException
     */
    protected void printHeaderFooterReport(AbstractReport report, int copyIndex) throws PrintingException
    {

        if (report instanceof LinkReport)
        {
            printLinkedHeaderFooterReport((LinkReport)report);
        }
        else
        {
            for (Group group : ((Report)report).getGroups())
            {
                printGroup(group, copyIndex);
            }
        }

        resetColumnIndex();
    }

    protected void printLinkedHeaderFooterReport(LinkReport report) throws PrintingException
    {
        String documentType = report.getDocumentType();
        try
        {
            Blueprint anotherBlueprint = documentManager.getBlueprint(documentType, parameterBean.getLocale(),
                    parameterBean.getDefaultLocale());

            // clear cache
            methodResultsCache.clear();
            // print it
            printBlueprint(anotherBlueprint);
            // reset last report
        }
        catch (Exception e)
        {
            throwPrintingException(e);
        }
    }

    /**
     * Print the blueprint.
     * 
     * @param blueprint
     * @throws PrintingException
     */
    protected void printFiscalBlueprint(Blueprint blueprint) throws PrintingException
    {
        resetColumnIndex();
        for (int copyIndex = 0; copyIndex < blueprint.getCopies(); copyIndex++)
        {
            for (AbstractReport report : blueprint.getReports())
            {
                if (shouldPrint(report))
                {
                    printFiscalReport(report, copyIndex);
                }
            }
        }
    }

    /**
     * Print a report once.
     * 
     * @param report
     */
    protected void printFiscalReport(AbstractReport report, int copyIndex) throws PrintingException
    {
        if (report instanceof LinkReport)
        {
            if ("FiscalHeader".equals(((LinkReport)report).getReportType()))
            {
                setReceiptSpecificHeader(report);
            }
            else if ("FiscalFooter".equals(((LinkReport)report).getReportType()))
            {
                setReceiptSpecificFooter(report);
            }
            else if ("FiscalReceiptMessage".equals(((LinkReport)report).getReportType()))
            {
                printReceiptMessages(report);
            }
            printFiscalLinkReport((LinkReport)report);
        }
        else
        {
            for (Group group : ((Report)report).getGroups())
            {
                printFiscalGroup(group, copyIndex);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.receipts.printing.BlueprintPrinter#printLinkReport
     * (oracle.retail.stores.receipts.model.LinkReport)
     */

    protected void printFiscalLinkReport(LinkReport report) throws PrintingException
    {
        String documentType = report.getDocumentType();

        try
        {
            Blueprint anotherBlueprint = documentManager.getBlueprint(documentType, parameterBean.getLocale(),
                    parameterBean.getDefaultLocale());
            // clear cache
            methodResultsCache.clear();
            // print it
            printFiscalBlueprint(anotherBlueprint);
            // reset last report
        }
        catch (Exception e)
        {
            throwPrintingException(e);
        }

    }

    /**
     * Print a group once.
     * 
     * @param report
     * @param copyIndex
     */
    protected void printFiscalGroup(Group group, int copyIndex) throws PrintingException
    {
        int count = 1;

        // get maximum loop count
        if (methodResultsCache != null)
        {
            try
            {
                count = getMethodResultsCache().cacheFiscalMethodResults(group, charWidths);
            }
            catch (InvocationTargetException e)
            {
                throw new PrintingException(e.getLocalizedMessage(), e);
            }
            catch (IllegalAccessException e)
            {
                throw new PrintingException(e.getLocalizedMessage(), e);
            }
        }

        // must change the fiscal printer mode before printing
        try
        {
            // Initializing the fiscal printer state as late as possible.
            // Initializing printer state automatically prints the header.
            if (!isPrintingInitialized() && getFiscalPrinterState() == FiscalPrinterConst.FPTR_PS_MONITOR)
            {
                if (getParameterBean().isTrainingMode())
                {
                    getFiscalReceiptPrinter().startTrainingModePrinting();
                }
                else
                {
                    try
                    {
                        getFiscalReceiptPrinter().getPrinter().setFiscalReceiptType(FiscalPrinterConst.FPTR_RT_SALES);
                        
                        // For return receipts printer must be initialized for
                        // credit note printing.
                        if (parameterBean instanceof ReceiptParameterBeanIfc)
                        {
                            int txnType = ((ReceiptParameterBeanIfc) parameterBean).getTransactionType();
                            int tenderSign = ((ReceiptParameterBeanIfc) parameterBean).getTotalTenderAmount().signum();

                            // If total is negative print refund receipt (credit
                            // note)
                            if (txnType == TransactionConstantsIfc.TYPE_RETURN
                                    || txnType == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE
                                    || txnType == TransactionConstantsIfc.TYPE_ORDER_CANCEL || tenderSign == -1)
                            {

                                getFiscalReceiptPrinter().getPrinter().setFiscalReceiptType(
                                        FiscalPrinterConst.FPTR_RT_REFUND);

                            }
                        }
                    }
                    catch (JposException e)
                    {
                        throwPrintingException(e);
                    }

                    getFiscalReceiptPrinter().startFiscalPrinting();
                }
                setPrintingInitialized(true);
            }
        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }

        // print this group this many times
        for (int groupIndex = 0; groupIndex < count; groupIndex++)
        {
            List<Line> fiscalLines = group.getLines();

            for (Line line : fiscalLines)
            {
                if (line instanceof FiscalLine)
                {
                    executeFiscalAPI((FiscalLine)line, groupIndex, copyIndex);
                }

            }
        }

    }

    /**
     * Print the specified line. Repeating if an array other than the group
     * iteration array is found.
     * 
     * @param line the line being printed
     * @param groupIndex the current group repetition index
     * @param copyIndex the index of the report copy being printed
     * @return number of lines to skip. See {@link Line#isRepeatNextLineAlso()}.
     * @throws PrintingException
     */
    protected int executeFiscalAPI(FiscalLine fpLine, int groupIndex, int copyIndex) throws PrintingException
    {
        // if this is non-null then this line must repeat
        String arrayId = methodResultsCache.getArrayId(fpLine.getId());
        Object result = methodResultsCache.getMethodResult(arrayId, groupIndex);
        Object[] array = null;
        if (result != null && result.getClass().isArray())
        {
            array = (Object[])result;
        }
        int lineIndex = -1; // index
        int linesToSkip = 0;
        
        // quick return if line depends on element that did not print or
        Object results = methodResultsCache.getMethodResult(fpLine.getDependsOnPresenceOf(), groupIndex);
        if(results != null && Boolean.parseBoolean(results.toString()))
            return 0;

        do
        {
            lineIndex++;
            linesToSkip = doPrintFiscalLine(fpLine, lineIndex, groupIndex, copyIndex, array != null);
        } while (array != null && lineIndex < array.length - 1);
        return linesToSkip;
    }

    /**
     * Print the specified line. Repeating if an array other than the group
     * iteration array is found.
     * 
     * @param line the line being printed
     * @param groupIndex the current group repetition index
     * @param copyIndex the index of the report copy being printed
     * @return number of lines to skip. See {@link Line#isRepeatNextLineAlso()}.
     * @throws PrintingException
     */
    protected int invokeFiscalAPI(FiscalLine command, int groupIndex, int copyIndex) throws PrintingException
    {
        // if this is non-null then this line must repeat
        String arrayId = methodResultsCache.getArrayId(command.getId());
        Object result = methodResultsCache.getMethodResult(arrayId, groupIndex);
        Object[] array = null;
        if (result != null && result.getClass().isArray())
        {
            array = (Object[])result;
        }
        int lineIndex = -1; // index
        int linesToSkip = 0;

        do
        {
            lineIndex++;
            try
            {
                invokeFiscalAPI(command.getClassName(), groupIndex, command);
            }
            catch (ClassNotFoundException e)
            {
                throw new PrintingException("Caught exception while invoking fiscal printing API", e);
            }
        } while (array != null && lineIndex < array.length - 1);
        return linesToSkip;
    }

    public Class[] getParameterTypes(List<FiscalLineParameter> FiscalLineArgs) throws ClassNotFoundException
    {
        ArrayList<Class> list = new ArrayList<Class>();

        for (FiscalLineParameter argument : FiscalLineArgs)
        {

            String type = argument.getType();
            if (primitiveTypeMap.containsKey(type))
            {
                list.add(primitiveTypeMap.get(type));
            }
            else
            {
                list.add(Class.forName(type));
            }
        }
        Class[] parts = new Class[list.size()];
        list.toArray(parts);
        return parts;
    }

    /**
     * @param line
     * @param lineIndex
     * @param groupIndex
     * @param copyIndex
     * @boolean isLineRepeating
     * @throws PrintingException
     */
    private int doPrintFiscalLine(FiscalLine command, int lineIndex, int groupIndex, int copyIndex,
            boolean isLineRepeating) throws PrintingException
    {
        String fpService = command.getClassName();

        try
        {
            invokeFiscalAPI(fpService, groupIndex, command);
        }
        catch (ClassNotFoundException e)
        {
            throw new PrintingException(e);
        }

        int linesSkipped = 0;
        return linesSkipped;
    }

    /**
     * A method to invoke the specified method on the specified object. The
     * class of the object is used to find the method. If perchance the method
     * defines {@link Locale} as a parameter, the locale is retrieved from the
     * object using
     * 
     * @param object
     * @param method
     * @return
     * @throws ClassNotFoundException
     * @throws PrintingException 
     */
    protected Object invokeFiscalAPI(String serviceName, int groupIndex, FiscalLine command)
            throws ClassNotFoundException, PrintingException
    {
        Class<?> clazz = Class.forName(serviceName);
        List<FiscalLineParameter> arguments = command.getParameters();
        Class partTypes[] = getPartTypes(arguments);

        Object argArray[] = null;
        List<Object> argList = new ArrayList<Object>();

        for (FiscalLineParameter parameter : arguments)
        {
            if (parameter.getElements().get(0) instanceof MethodElement)
            {
                Object obj = methodResultsCache.getMethodResult(parameter.getId(), groupIndex);
                argList.add(obj);                
            }
            else
            {
               String staticText = ((Element)parameter.getElements().get(0)).getText();
               argList.add(staticText);
            }

        }

        if (argList.size() > 0)
        {
            argArray = new Object[command.getParameters().size()];
            argList.toArray(argArray);
        }

        // get the method to invoke
        java.lang.reflect.Method refMethod = null;

        // invoke the method for the results
        Object results = null;
        try
        {
            if (refMethod == null)
            {
                refMethod = clazz.getMethod(command.getName(), partTypes);
            }

            // gather results
            if (refMethod != null)
            {
                if (argArray != null)
                {
                    results = refMethod.invoke((FiscalReceiptPrinter)printer, argArray);
                }
                else
                {
                    results = refMethod.invoke((FiscalReceiptPrinter)printer);
                }
            }
        }
        catch (Exception e)
        {
            throwPrintingException(e);
        }
        return results;
    }

    public Class[] getPartTypes(List<FiscalLineParameter> fiscalLineParams) throws ClassNotFoundException
    {
        List<Class> list = new ArrayList<Class>();

        for (FiscalLineParameter argument : fiscalLineParams)
        {

            String type = argument.getType();
            if (primitiveTypeMap.containsKey(type))
            {
                list.add(primitiveTypeMap.get(type));
            }
            else
            {
                list.add(Class.forName(type));
            }
        }
        Class[] parts = new Class[list.size()];
        list.toArray(parts);
        return parts;
    }

    /**
     * Set the blueprint for this document to use.
     * 
     * @param blueprint
     */
    public void setBlueprint(Blueprint blueprint)
    {
        this.blueprint = blueprint;
    }

    /**
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#setCopyTag(int)
     * @deprecated as of 13.0.1. Copies are determined by the blueprint.
     */
    public void setCopyTag(int i)
    {
    }

    /**
     * Override to send the cut command to the buffer. If buffering is diabled,
     * then this method will send 6 line feeds to the printer then calls
     * {@link ReceiptPrinterIfc#cutPaper()}.
     * 
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#cut()
     */
    @Override
    protected void cut() throws PrintingException
    {
        // operation not supported by the fiscal printers
    }

    /**
     * Returns null.
     * 
     * @see oracle.retail.stores.receipts.printing.BlueprintPrinter#getLinkedBlueprint(oracle.retail.stores.receipts.model.LinkReport)
     */
    @Override
    protected InputStream getLinkedBlueprint(LinkReport report) throws IOException
    {
        return null;
    }

    /**
     * Overridden to print the buffered line if it not empty. Flushes the buffer
     * and sets it to {@link ReceiptPrinterIfc#write(String)}.
     * 
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#lineFeed()
     */
    @Override
    protected void lineFeed() throws PrintingException
    {
        if (line.length() > 0)
        {
            if (colIndex != getBlueprintWidth() || alwaysPrintLineFeeds())
            {
                line.append('\n');
                flush();
            }
            // reset index on wrapped line
            if (colIndex <= getBlueprintWidth())
            {
                resetColumnIndex();
            }
            else
            // colIndex is greater than LINE_LENGTH
            {
                colIndex = getBlueprintWidth() - colIndex;
            }
        }
        else
        {
            resetColumnIndex();
        }
    }

    /**
     * Print the specified image element.
     * 
     * @param element
     * @throws DeviceException
     */
    @Override
    protected void printImageElement(ImageElement element) throws PrintingException
    {

        // Fiscal printers do not support image printing, image printing is
        // limited logo on header and footer

    }

    FiscalReceiptPrinter getFiscalReceiptPrinter()
    {
        return (FiscalReceiptPrinter)printer;
    }

}