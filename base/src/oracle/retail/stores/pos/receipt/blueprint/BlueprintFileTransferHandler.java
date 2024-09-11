/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/blueprint/BlueprintFileTransferHandler.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/13/10 - added support for blueprints in zip files
 *    abondala  01/03/10 - update header date
 *    cgreene   04/17/09 - corrected possible null reference to blueprinted
 *                         document manager
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt.blueprint;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.filetransfer.DefaultFileTransferHandler;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;

/**
 * This file transfer handler puts BPT files in the directory specified in
 * BlueprintDocumentManager.xml
 *
 * @author cgreene
 * @since 13.1
 */
public class BlueprintFileTransferHandler extends DefaultFileTransferHandler
{
    private static final Logger logger = Logger.getLogger(BlueprintFileTransferHandler.class);

    /**
     * Constructor
     *
     * @throws TechnicianNotFoundException
     */
    public BlueprintFileTransferHandler() throws TechnicianNotFoundException
    {
        String receiptsPath = getBlueprintedDocumentManager().getReceiptsPath();
        StringTokenizer tokens = new StringTokenizer(receiptsPath, ";");
        while (tokens.hasMoreTokens())
        {
            directory = new File(tokens.nextToken());
            if (directory.isFile())
            {
                if (!tokens.hasMoreTokens())
                {
                    logger.error("No receipts directory found for file transfer handler.");
                }
                directory = null;
                continue;
            }

            if (!directory.exists())
            {
                if (!directory.mkdirs())
                {
                    logger.error("Unable to create directory " + directory);
                }
            }
            break;
        }
    }

    /**
     * Performs the actual work once the file is received by saving the file
     * to the directory given in the BlueprintDocumentManager.xml. Then the
     * {@link BlueprintedDocumentManager} is asked to remove any cached value
     * where it may have read that file already so that it is forced to read it
     * again.
     * 
     * @see oracle.retail.stores.foundation.manager.filetransfer.DefaultFileTransferHandler#handle(java.lang.String, byte[], java.lang.String)
     */
    public void handle(String fileType, byte[] contents, String fileName) throws IOException
    {
        super.handle(fileType, contents, fileName);
        Object blueprint = getBlueprintedDocumentManager().removeCachedBlueprint(fileName);
        if (blueprint == null)
        {
            logger.error("Unable to remove new blueprint from BlueprintedDocumentManager's cache");
        }
    }

    /**
     * Return local manager mapped to {@link PrintableDocumentManagerIfc#TYPE}
     * cast as a {@link BlueprintedDocumentManager}.
     *
     * @return
     */
    protected BlueprintedDocumentManager getBlueprintedDocumentManager()
    {
        return (BlueprintedDocumentManager)Gateway.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);
    }
}
