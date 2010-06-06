/********************************************************************
 *
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php

 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Date: 1/5/2009
 * Main Description: Light is a simple text editor in clojure
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 *********************************************************************/
package com.light.pdf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.util.XRLog;

public class XHTMLRendererBase {
        
    private String htmlContent;

    // TODO, fix this system of key/value replacement, right now not very useful.
    private String replKey = "";
    private String replVal = "";

    private Object applicationObject;

    public final String getHtmlContent() {
        return htmlContent;
    }

    public final void setHtmlContent(final String data) {
        this.htmlContent = data;
    }

    /**
     * Set any object type to later be used by the parser class.  This can be
     * used to pass Session, HttpServletResponse objects or any other type.
     * @param obj
     */
    public final void setApplicationObject(final Object obj) {
        applicationObject = obj;
    }
    public final Object getApplicationObject() {
        return applicationObject;
    }

    private static final String toHexString(byte[] bytes) {

        char[] ret = new char[bytes.length * 2];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            int c = (int) bytes[i];
            if (c < 0) {
                c += 0x100;
            }
            ret[j++] = Character.forDigit(c / 0x10, 0x10);
            ret[j++] = Character.forDigit(c % 0x10, 0x10);

        }
        return new String(ret);
    }


    /**
     * Utility function, generate a unique key identifier, but shorten
     * to use as a filename.
     */
    public static final String getShortenUniqueKey() {
        final String val = (getUniqueKey() != null) ?  getUniqueKey() : "valid_filename_key" ;
        // take the first 10 characters
        if (val.length() >= 10) {
            return val.substring(0, 9);
        } else {
            return "";
        }
    }

    /**
     * Utility function, generate a unique key identifier.
     */
    public static final String getUniqueKey() {

        String digest = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            String timeVal = "" + (System.currentTimeMillis() + 1);
            String localHost = "";;
            try {
                localHost = InetAddress.getLocalHost().toString();
            } catch (UnknownHostException e) {
                // If an error, we can use other values.
                println("Warn: getUniqueKey(), Error trying to get localhost" + e.getMessage());
            }
            String randVal = "" + new Random().nextInt();
            String val = timeVal + localHost + randVal;
            md.reset();
            md.update(val.getBytes());

            // Generate the digest.

            digest = toHexString(md.digest());
        } catch(NoSuchAlgorithmException e) {
            println("Warn: getUniqueKey() " + e);
        } // End of the Try - Catch
        return digest;
    }

    /**
     * System out, debugging message.
     */
    public static final void println(final String message) {
        final String msg = "<XHTMLRenderer HTMLToPDF Message> (" + datestr() + ") " + message;
        System.out.println(msg);
    }

    public static final String datestr() {
        return "" + (new Date());
    }

    ///////////////////////////////////////////////////////
    // Key Value Replace, can be used to replace values in the target document.
    ///////////////////////////////////////////////////////
    public void setKeyValue(final String replKey, final String val) {

        // Don't set for non null values.
        if (val != null) {
            this.replKey = replKey;
            this.replVal = val;
        }
    }

    public String getReplKey() {
        return replKey;
    }

    public void setReplKey(String replKey) {
        this.replKey = replKey;
    }

    public String getReplVal() {
        return replVal;
    }

    public void setReplVal(String replVal) {
        this.replVal = replVal;
    }

    public static final void setXhtmlProperties() {

        System.setProperty("xr.util-logging.loggingEnabled", "true");
        System.setProperty("xr.load.xml-reader", "org.ccil.cowan.tagsoup.Parser");
        System.setProperty("xr.util-logging.loggingEnabled", "true");
        System.setProperty("xr.util-logging.java.util.logging.ConsoleHandler.level", "INFO");
        System.setProperty("xr.util-logging.level", "INFO");
        System.setProperty("org.xhtmlrenderer.minium.quality", "highest");
        XRLog.setLoggingEnabled(true);
        //*******************************************************
    }

    /**
     * In the event of an invalid document, return a dummy XHTML document, collect a valid
     * HTML document when the user visits this filter.
     */
    public static final String dummyXhtmlDocument(final String msg) {
        final StringBuffer buf = new StringBuffer();
        buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
        buf.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        buf.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        buf.append("<head>");
        buf.append(" <style type=\"text/css\">");
        buf.append("   .error_text {");
        buf.append("        font-family: arial,helvetica,verdana,geneva,sans-serif; font-size: 11pt; color: #700; padding: 16px; margin-left: 20px; margin-top: 20px;");
        buf.append("        border: 1px solid #888; background-color: #f8f8f8");
        buf.append("   }");
        buf.append(" </style>");
        buf.append("</head><body>");

        // Build readable error message
        buf.append("<div class=\"error_text\">");
        buf.append(" A System Error Occurred While Trying to Create PDF Document. (" + datestr() +  ") <br /> ERR: " + msg);
        buf.append("</div>");
        buf.append("</body>");
        buf.append("</html>");
        return buf.toString();
    }


    /**
     * Write the XHTML/HTML content to the outputstream.  The outputstream might be a File output stream,
     * Servlet output stream or any other form of stream.
     *
     * @param doc
     * @param os
     * @throws Exception
     */
    public final void writeITextOutputStream(final Document doc, final OutputStream os) 
            throws Exception {
        //ITextRenderer renderer = new ITextRenderer();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(doc, null);
        renderer.layout();
        println("Sending output stream response to client");
        renderer.createPDF(os);
        os.flush();
        os.close();
    }

    public final byte [] writeITextByteArray(final Document doc) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.writeITextOutputStream(doc, baos);
        return baos.toByteArray();
    }

    /**
     * Write the XHTML/HTML content to a file.
     *
     * @param doc
     * @param os
     * @throws Exception
     */
    private final void writeITextFile(final Document doc, final String filepath) throws Exception {

        final FileOutputStream stream = new FileOutputStream(filepath);
        final byte [] data = this.writeITextByteArray(doc);
        try {
            if (stream != null) {
                stream.write(data);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Main method that invokes xhtmlrenderer, uses the input document to output the PDF document to a stream.
     *
     * Here is an example code snippet for loading a html file and outputting to PDF.
     *
     * <code>
     *      final XHTMLRendererBase base = new XHTMLRendererBase();
     *      final String origHtmlData = XHTMLRendererBase.loadFile("Servlet_Test.html");
     *      base.parseDocumentFile("test_output3.pdf",
     *                  BuilderTrackParserFactory.create(BuilderTrackParserFactory.SCOREBOARD, origHtmlData, true));
     * </code>
     *
     * @param os
     * @param parser
     * @throws Exception
     */
    public final void parseDocumentStream(final OutputStream os, final LightHTMLRendererText parser) throws Exception {

        if (parser != null) {
            parser.setBaseRenderer(this);
        }

        String formattedHtmlDoc = "";
        if (parser != null) {
            // It is OK to have a null parser.
            println("Renderer Parse Text Handler Found, attempting to parse document");
            formattedHtmlDoc = parser.parse();
        } else {
            formattedHtmlDoc = this.getHtmlContent();
        }

        if ((formattedHtmlDoc == null)|| (formattedHtmlDoc.length() == 0)) {
            // Invalid input document, fall back on the dummy xhtml document
            println("<ERR parseDocumentStream>: Renderer Parse Text Handler Failed, falling back to the dummy document");
            formattedHtmlDoc = dummyXhtmlDocument("[Invalid Document at parseDocumentStream.  Input document is empty]");
        }

        println("<parseDocumentStream> Attempting to parse document from stream, len=" + formattedHtmlDoc.length());
        XMLResource xmlResouce = XMLResource.load(new ByteArrayInputStream(formattedHtmlDoc.getBytes()));
        println("<parseDocumentStream> After load document, calling getDocument");
        final Document doc = xmlResouce.getDocument();
        println("Creating iText Object");
        this.writeITextOutputStream(doc, os);
        println("Done with xhtmlrenderer 'createPDF' (trace-vers8)");
    }

    public static final String loadFile(final String path) {
        
        StringBuffer html = new StringBuffer();
        FileInputStream stream;
        try {
            stream = new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("<LoadFile> path=" + path + " err=" + e1.getMessage());
        }
        try {
            stream = new FileInputStream(path);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String data = "";
            do {
                data = reader.readLine();
                if (data != null) {
                    html.append(data);
                }
            } while(data != null);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("<LoadFile> path=" + path + " err=" + e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        } // End of try catch finally
        return html.toString();
    }

    public final void parseDocumentFile(final String path, final LightHTMLRendererText parser) throws Exception {
        
        final FileOutputStream stream = new FileOutputStream(path);
        if (parser != null) {        	
            parser.setBaseRenderer(this);
        } 
        this.parseDocumentStream(stream, parser);
    }

} // End of Class
