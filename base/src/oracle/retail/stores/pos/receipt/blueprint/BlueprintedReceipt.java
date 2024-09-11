/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/blueprint/BlueprintedReceipt.java /main/38 2014/02/28 15:38:11 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/28/14 - add support for printing normal elements as an image
 *    mkutiana  09/05/13 - implemented slipLineSize for Franking bpts
 *    abhinavs  08/01/13 - Fix to print store receipt correctly when email
 *                         option is selected
 *    yiqzhao   07/05/13 - Print store copy if possible when Email print is
 *                         selected.
 *    mchellap  06/18/13 - Do not cut paper for previews
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   07/05/12 - include checking for XmlReceiptPrinter when
 *                         constructing PDFs
 *    mchellap  05/09/12 - VLR receipt printing changes
 *    icole     08/11/11 - Added original exception for non-Jpos exception.
 *    icole     08/11/11 - Changed code to handel no-jpos exception to prevent
 *                         NPE in DeviceException.
 *    cgreene   03/22/11 - XbranchMerge cgreene_124_receipt_quick_wins_part2
 *                         from main
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    vtemker   03/08/11 - Print Preview Quickwin for Reports
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    jkoppolu  10/27/10 - BUG#1038, Customer copy of alteration receipt is not
 *                         getting printed when eReceipt is enabled.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   10/16/09 - XbranchMerge cgreene_ereceiptescapecodes from
 *                         rgbustores_13.1x_branch
 *    cgreene   10/16/09 - avoid printing JPOS escape codes to the eReceipt pdf
 *    cgreene   05/13/09 - removed unused constant
 *    cgreene   03/10/09 - remember paper cut so it can be reset after printing
 *                         link report
 *    cgreene   02/20/09 - flush buffer after printing escape codes
 *    blarsen   02/18/09 - fixing formatting math to compensate for having a
 *                         mix of characters (single and double width) being
 *                         sent to the printer
 *    blarsen   02/16/09 - reworking when line feeds are appended onto lines -
 *                         I18N fonts/new printers are behaving differently in
 *                         this area
 *    acadar    02/12/09 - use default locale for date/time printing in the
 *                         receipts
 *    cgreene   01/13/09 - multiple send and gift receipt changes. deleted
 *                         SendGiftReceipt
 *    cgreene   12/11/08 - is buffering is off then add 6 line feeds and flush
 *                         the buffer
 *    arathore  11/24/08 - updated for ereceipt.
 *    arathore  11/20/08 - updated for ereceipt.
 *    arathore  11/20/08 - updated for ereceipt feature.
 *    cgreene   11/17/08 - moved receiptLogoFileName to ReceiptPrinter
 *    cgreene   11/10/08 - convert to extend BlueprintPrinter and implement
 *                         EYSPrintableDocumentIfc
 *    cgreene   11/10/08 - update checkDependsOn to disqualify empty arrays
 *    cgreene   11/03/08 - fix some HousePaymentReceipt formatting and
 *                         implement blueprint copies
 *    cgreene   10/30/08 - change lineFeed to flush in printBlueprint
 *    cgreene   10/28/08 - add array length check to see if element should
 *                         print
 *    cgreene   10/17/08 - do not cut paper on last report in linkreport
 *    cgreene   10/13/08 - update to dependsOnPresenceOf method
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.StringTokenizer;

import jpos.JposException;
import jpos.POSPrinterConst;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.ReceiptPrinter;
import oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc;
import oracle.retail.stores.manager.device.XmlReceiptPrinter;
import oracle.retail.stores.pos.device.POSPrinterActionGroupIfc;
import oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc;
import oracle.retail.stores.pos.receipt.FrankingReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptConstantsIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.receipts.formatting.FormatUtils;
import oracle.retail.stores.receipts.model.AbstractReport;
import oracle.retail.stores.receipts.model.Blueprint;
import oracle.retail.stores.receipts.model.Group;
import oracle.retail.stores.receipts.model.ImageElement;
import oracle.retail.stores.receipts.model.Line;
import oracle.retail.stores.receipts.model.LinkReport;
import oracle.retail.stores.receipts.printing.BlueprintPrinter;
import oracle.retail.stores.receipts.printing.PrintingException;

/**
 * A receipt that prints based upon instructions gathered from a blueprint and
 * using data from the {@link #parameterBean}.
 *
 * @author cgreene
 * @since 13.1
 */
public class BlueprintedReceipt extends BlueprintPrinter
    implements EYSPrintableDocumentIfc
{
    /** Key to this class or subclass in the ApplicationContext.xml */
    public static final String BEAN_KEY = "application_BlueprintedReceipt";

     /**
     * The current receipt blueprint being printed.
     */
    protected Blueprint blueprint;

    /**
     * Reference is useful for gathering linked blueprints.
     */
    protected BlueprintedDocumentManager documentManager;

    /**
     * The entire line is buffered since some JPOS drivers do not support
     * calls to printNormal that do not line feed.
     */
    protected StringBuilder line;
    
    /**
     * The entire text to be printed is appended together
     */
    protected StringBuilder previewText;

    /**
     * Flag if this is a preview.
     */
    protected boolean preview = false;
    
    /**
     * Data wrapper object
     */
    protected PrintableDocumentParameterBeanIfc parameterBean;

    /**
     * printer interface
     */
    protected ReceiptPrinterIfc printer = null;

    /**
     * A printer has been provided
     */
    protected boolean printerInitialized;

    /**
     * Whether or not to print tax on an individual tax jurisdiction basis.
     */
    protected boolean printItemTax = false;
    
    /**
     * Fixed length receipt properties
     */
    protected boolean repeatHeader = false;

    protected boolean repeatFooter = false;
    
    protected boolean fixedLengthReceipt = false;
    
    protected String headerBlueprints = "";

    protected String footerBlueprints = "";
    
    /**
     * retail transaction
     */
    protected TenderableTransactionIfc transaction;

    /**
     * Convenience constructor. Call {@link #setBlueprint(Blueprint)} before
     * using the {@link #printDocument()} method.
     */
    public BlueprintedReceipt()
    {
        this(null);
    }

    /**
     * Construct this receipt with the specified blueprint. Sets
     * {@link #alwaysPrintLineFeeds()} to false.
     *
     * @param blueprint
     */
    public BlueprintedReceipt(Blueprint blueprint)
    {
        super(POSPrinterActionGroupIfc.LINE_LENGTH_DEFAULT,false,null);
        this.blueprint = blueprint;
        line = new StringBuilder(50);
        previewText = new StringBuilder("");
    }

    /**
     * Get the parameter bean.
     *
     * @returns {@link #parameterBean}
     */
    public PrintableDocumentParameterBeanIfc getParameterBean()
    {
        return parameterBean;
    }

    /**
     * Tell whether or not to print detailed tax info in the subtotals and for
     * each line item.
     *
     * @return true or false
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#getPrintItemTax()
     */
    public boolean getPrintItemTax()
    {
        return printItemTax;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#getTransaction()
     */
    public TenderableTransactionIfc getTransaction()
    {
        return transaction;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#isExchangeTransactionType(oracle.retail.stores.domain.transaction.TenderableTransactionIfc)
     * @deprecated As of release 12.0, moved to
     *             oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc
     */
    public boolean isExchangeTransactionType(TenderableTransactionIfc transaction)
    {
        return false;
    }

    /**
     * Returns whether a printer has been provided.
     *
     * @return true if a printer has been provided, false otherwise.
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#isPrinterInitialized()
     */
    public boolean isPrinterInitialized()
    {
        return printerInitialized;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#printDocument()
     */
    public void printDocument() throws DeviceException
    {
        previewText = new StringBuilder("");
        // must be called before printing
        printer.startJob();
        // change the character set to one mapped to locale
        printer.switchCharacterSet(blueprint.getReceiptLocale(), blueprint.getDefaultLocale());        
        // set fixed length receipt flag
        printer.setFixedLengthReceipt(isFixedLengthReceipt());
        
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
            else if(e.getCause() instanceof JposException)
            {
                throw new DeviceException(DeviceException.JPOS_ERROR, e.getLocalizedMessage(), e);
            }
            else
            {
                throw new DeviceException(DeviceException.UNKNOWN, "Undetermined Exception",e);
            }
        }
        printer.endJob();
    }
    
    /**
     * Prints a fiscal receipt
     */
    public void printFiscalReceipt() throws DeviceException
    {
        // Do nothing
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
     * Set the documentManager that this can get other blueprints from.
     *
     * @param documentManager
     */
    public void setDocumentManager(BlueprintedDocumentManager documentManager)
    {
        this.documentManager = documentManager;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#setParameterBean(oracle.retail.stores.pos.receipt.PrintableDocumentParameterBeanIfc)
     */
    public void setParameterBean(PrintableDocumentParameterBeanIfc parameterBean)
    {
        this.parameterBean = parameterBean;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#setPrinter(oracle.retail.stores.foundation.manager.ifc.device.ReceiptPrinterIfc)
     */
    public void setPrinter(ReceiptPrinterIfc printer)
    {
        this.printer = printer;
        
        //Set the width of the printer to this blueprintedReceipt, based on if Franking or receipt printing
        if (getParameterBean() instanceof FrankingReceiptParameterBeanIfc )
        {
            setBlueprintWidth(printer.getSlipLineSize());
        }
        else
        {
            setBlueprintWidth(printer.getReceiptLineSize());
        }
        setPrinterInitialized(true);
    }

    /**
     * Sets whether a printer has been provided.
     *
     * @param value true if a printer has been provided, false otherwise.
     */
    public void setPrinterInitialized(boolean printerInitialized)
    {
        this.printerInitialized = printerInitialized;
    }

    /**
     * Set whether or not to print detailed tax info in the subtotals and for
     * each line item.
     *
     * @param value
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocumentIfc#setPrintItemTax(boolean)
     */
    public void setPrintItemTax(boolean value)
    {
        this.printItemTax = value;
    }

    /**
     * Override to send the cut command to the buffer. If buffering is disabled,
     * then this method will send 6 line feeds to the printer then calls
     * {@link ReceiptPrinterIfc#cutPaper()}.
     *
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#cut()
     */
    @Override
    protected void cut() throws PrintingException
    {
        if (!isPreview())
        {
            if (printer.isPrintBufferingEnabled())
            {
                // feed lines then cut 90%
                line.append(ReceiptPrinter.escape("90fP"));
            }
            else
            {
                lineFeed();
                lineFeed();
                lineFeed();
                lineFeed();
                lineFeed();
                lineFeed();
                flush();
                try
                {
                    printer.cutPaper();
                }
                catch (DeviceException e)
                {
                    throwPrintingException(e);
                }

            }
        }
    }

    /**
     * Send the {@link #line} to print and sets it to zero length.
     * 
     * @throws PrintingException
     */
    protected void flush() throws PrintingException
    {
        try
        {
            // Either print or just preview.
            if (isPreview())
            {
                previewText.append(printer.getPrintString(line.toString(), getBlueprintWidth()));
            }
            else
            {
                printer.write(line.toString());
            }       
        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }    
        // clear line
        line.setLength(0);
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
            else // colIndex is greater than LINE_LENGTH
            {
                colIndex = getBlueprintWidth() - colIndex;
            }
        }
        else
        {
            resetColumnIndex();
            try
            {
                printer.write("\n");
            }
            catch (DeviceException e)
            {
                throwPrintingException(e);
            }
        }
    }

    /**
     * Overridden to be able to escape text as needed and buffer lines.
     *
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#print(java.lang.String)
     */
    @Override
    protected void print (String text) throws PrintingException
    {
        // print a escape sequences desired by the blueprint designer
        if (escapeSequence != null &&
                // only print escape codes if not printing the ereceipt pdf
                parameterBean instanceof ReceiptParameterBeanIfc &&
                !(printer instanceof XmlReceiptPrinter) && 
                !((ReceiptParameterBeanIfc)parameterBean).isEreceipt())
        {
            StringBuilder escapedText = new StringBuilder(text);
            StringTokenizer tokens = new StringTokenizer(escapeSequence, "\\|");
            int width = FormatUtils.getPrintedWidth(text, getCharWidths());
            colIndex += width;
            while (tokens.hasMoreTokens())
            {
                String escapeCode = tokens.nextToken();
                colIndex -= escapeCode.length(); 
                escapedText.insert(0, ReceiptPrinter.escape(escapeCode));
            }
            line.append(escapedText.toString());
            // revert text to normal
            line.append(ReceiptPrinter.escape("N"));
            // need to flush line buffer after this escape code prints to reset
            // next elements so they don't get same escape 
            flush();
        }
        else
        {
            colIndex += FormatUtils.getPrintedWidth(text, getCharWidths());
            line.append(text);
        }
    }

    /**
     * Overridden to flush to line before printing.
     *
     * @see oracle.retail.stores.pos.receipt.EYSPrintableDocument#printBarcode(java.lang.String)
     * @throws PrintingException
     */
    @Override
    protected void printBarcode(String id) throws PrintingException
    {
        flush();
        try
        {
            printer.printBarcode128_Parsed(id);
        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }
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
            //for eReceipt, check if more than one copies
            if(blueprint.getCopies() > 1 && getParameterBean() instanceof ReceiptParameterBeanIfc)
            {
                //check if SaleReturnTransaction
                if(((ReceiptParameterBeanIfc)getParameterBean()).getTransaction() instanceof SaleReturnTransactionIfc )
                {
                    ReceiptParameterBeanIfc parameters =(ReceiptParameterBeanIfc)getParameterBean();
                    //check if eReceipt
                    if(parameters.isEreceipt())
                    {
                        if(copyIndex != ReceiptConstantsIfc.CUSTOMER_COPY_INDEX) //If not customer copy then continue.
                        {
                            continue;
                        }
                    }
                    //If printing only store copies then continue if customer copy.
                    else if(parameters.isPrintStoreReceipt() && copyIndex == ReceiptConstantsIfc.CUSTOMER_COPY_INDEX)
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
    
    @Override
    /**
    @throws PrintingException
     */
    protected void printGroup(Group group, int copyIndex) throws PrintingException
    {
        int count = 1;

        // get maximum loop count
        if (methodResultsCache != null)
        {
            try
            {
                count = getMethodResultsCache().cacheMethodResults(group, charWidths);
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
        
        String blueprint = group.getParent().getParent().getId();
        // print this group this many times
        for (int groupIndex = 0; groupIndex < count; groupIndex++)
        {
            try
            {
                initializeGroupPrinting(blueprint);
                List<Line> lines = group.getLines();
                for (int i = 0; i < lines.size(); i++)
                {
                    int skip = printLine(lines.get(i), groupIndex, copyIndex);
                    if (skip > 0)
                    {
                        i = i + skip;
                    }
                }
                finalizeGroupPrinting(blueprint);
            }
            catch (DeviceException e)
            {
                throwPrintingException(e);
            }
        }
    }

    public void initializeGroupPrinting(String blurprintName) throws DeviceException
    {
        if (isHeaderBlueprint(blurprintName))
        {
            printer.beginHeaderPrinting(repeatHeader);
        }
        else if (isFooterBlueprint(blurprintName))
        {
            printer.beginFooterPrinting(repeatFooter);
        }
        else
        {
            printer.beginBlueprintGroupPrinting();
        }
    }

    public void finalizeGroupPrinting(String blurprintName) throws DeviceException
    {
        if (isHeaderBlueprint(blurprintName))
        {
            printer.endHeaderPrinting();
        }
        else if (isFooterBlueprint(blurprintName))
        {
            printer.endFooterPrinting();
        }
        else
        {
            printer.endBlueprintGroupPrinting();
        }
    }

    protected boolean isHeaderBlueprint(String blurprintName)
    {
        if (headerBlueprints.indexOf(blurprintName) >= 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean isFooterBlueprint(String blurprintName)
    {
        if (footerBlueprints.indexOf(blurprintName) >= 0)
        {
            return true;
        }
        else
        {
            return false;
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
        printImageElement(element.getFileName(), POSPrinterConst.PTR_BM_CENTER);
    }

    /**
     * Print the image.
     *
     * @param element
     * @throws DeviceException
     */
    protected void printImageElement(String fileName, int justification) throws PrintingException
    {
        flush();
        try
        {
            printer.printBitmap(fileName, justification);
        }
        catch (DeviceException e)
        {
            throwPrintingException(e);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.receipts.printing.BlueprintPrinter#println(java.lang.String)
     */
    @Override
    protected void println(String text)
    {
        if ("".equals(text))
        {
            line.append(" \n");            
        }
        else
        {
            line.append(text);          
            line.append('\n');
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.receipts.printing.BlueprintPrinter#printLinkReport(oracle.retail.stores.receipts.model.LinkReport)
     */
    @Override
    protected void printLinkReport(LinkReport report) throws PrintingException
    {
        String documentType = report.getDocumentType();
        AbstractReport lastReport = null;
        boolean cutPaper = true;
        try
        {
            Blueprint anotherBlueprint = documentManager.getBlueprint(documentType, parameterBean.getLocale(), parameterBean.getDefaultLocale());
            // don't cut the paper on the last report in this blueprint, this link report will cut it
            List<AbstractReport> reports = anotherBlueprint.getReports();
            lastReport = reports.get(reports.size() - 1);
            cutPaper = lastReport.isCutPaper();
            lastReport.setCutPaper(false);
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
        finally
        {
            if (lastReport != null)
            {
                lastReport.setCutPaper(cutPaper);
            }
        }
    }

    /**
     * Unwrap an exception and throw it as a PrintingException
     * @param e
     * @throws PrintingException always
     */
    protected void throwPrintingException(Exception e) throws PrintingException
    {
        if (e.getCause() != null)
        {
            throw new PrintingException(e.getLocalizedMessage(), e.getCause());
        }
        throw new PrintingException(e.getLocalizedMessage(), e);
    }
    
    /**
     * Get the previewText
     * 
     * @return previewText
     */
    public StringBuilder getPreviewText()
    {
        return previewText;
    }

    /**
     * Check if the preview flag is set.
     * 
     * @return preview
     */
    public boolean isPreview()
    {
        return preview;
    }

    /**
     * Set the preview flag
     * 
     * @param preview
     */
    public void setPreview(boolean preview)
    {
        this.preview = preview;
    }
    
    /**
     * @return the repeatHeader
     */
    public boolean isRepeatHeader()
    {
        return repeatHeader;
    }

    /**
     * @param repeatHeader the repeatHeader to set
     */
    public void setRepeatHeader(boolean repeatHeader)
    {
        this.repeatHeader = repeatHeader;
    }

    /**
     * @return the repeatFooter
     */
    public boolean isRepeatFooter()
    {
        return repeatFooter;
    }

    /**
     * @param repeatFooter the repeatFooter to set
     */
    public void setRepeatFooter(boolean repeatFooter)
    {
        this.repeatFooter = repeatFooter;
    }

    /**
     * @return the headerBlueprints
     */
    public String getHeaderBlueprints()
    {
        return headerBlueprints;
    }

    /**
     * @param headerBlueprints the headerBlueprints to set
     */
    public void setHeaderBlueprints(String headerBlueprints)
    {
        this.headerBlueprints = headerBlueprints;
    }

    /**
     * @return the footerBlueprints
     */
    public String getFooterBlueprints()
    {
        return footerBlueprints;
    }

    /**
     * @param footerBlueprints the footerBlueprints to set
     */
    public void setFooterBlueprints(String footerBlueprints)
    {
        this.footerBlueprints = footerBlueprints;
    }
    

    /**
     * @return the fixedLengthReceipt
     */
    public boolean isFixedLengthReceipt()
    {
        return fixedLengthReceipt;
    }

    /**
     * @param fixedLengthReceipt the fixedLengthReceipt to set
     */
    public void setFixedLengthReceipt(boolean fixedLengthReceipt)
    {
        this.fixedLengthReceipt = fixedLengthReceipt;
    }
    
}
