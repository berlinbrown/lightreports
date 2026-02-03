/**
 * Copyright (c) 2006-2007 Berlin Brown and botnode.com All Rights Reserved
 *
 * <p>http://www.opensource.org/licenses/bsd-license.php
 *
 * <p>All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>* Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. * Neither the name of the Botnode.com
 * (Berlin Brown) nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * <p>Date: 1/5/2009 7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
 *
 * <p>Main Description: Light Log Viewer is a tool for making it easier to search log files. Light
 * Log Viewer adds some text highlighting, quick key navigation to text files, simple graphs and
 * charts for monitoring logs, file database to quickly navigate to files of interest, and HTML to
 * PDF convert tool. Light Log was developed with a combination of Clojure 1.0, Java and Scala with
 * use of libs, SWT 3.4, JFreeChart, iText.
 *
 * <p>Quickstart : the best way to run the Light Log viewer is to click on the win32 batch script
 * light_logs.bat (you may need to edit the Linux script for Unix/Linux environments). Edit the
 * win32 script to add more heap memory or other parameters.
 *
 * <p>The clojure source is contained in : HOME/src/octane The java source is contained in :
 * HOME/src/java/src
 *
 * <p>To build the java source, see : HOME/src/java/build.xml and build_pdf_gui.xml
 *
 * <p>Additional Development Notes: The SWT gui and other libraries are launched from a dynamic
 * classloader. Clojure is also started from the same code, and reflection is used to dynamically
 * initiate Clojure. See the 'start' package. The binary code is contained in the octane_start.jar
 * library.
 *
 * <p>Home Page: http://code.google.com/p/lighttexteditor/
 *
 * <p>Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package com.toolkit.util.pdf;

import com.toolkit.util.Print;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.util.XRLog;

/**
 * XHTML Renderer base class that contains the base functionality for converting XHTML to PDF
 * documents.
 *
 * <p>APPLICATION LEVEL
 *
 * <p>file:/C:/usr/local/htdocs/
 *
 * @author Berlin
 * @version $Revision: 1.0 $
 */
public class XHTMLRendererBase {

  private static final boolean _DEBUG_ = false;
  private String htmlContent;

  private String replKey = "";
  private String replVal = "";

  private Object applicationObject;

  /**
   * Implementation Routine getHtmlContent.
   *
   * @return String
   */
  public String getHtmlContent() {
    return htmlContent;
  }

  /**
   * Implementation Routine setHtmlContent.
   *
   * @param data String
   */
  public void setHtmlContent(final String data) {
    this.htmlContent = data;
  }

  /**
   * Set any object type to later be used by the parser class. This can be used to pass Session,
   * HttpServletResponse objects or any other type.
   *
   * @param obj
   */
  public void setApplicationObject(final Object obj) {
    applicationObject = obj;
  }

  /**
   * Implementation Routine getApplicationObject.
   *
   * @return Object
   */
  public Object getApplicationObject() {
    return applicationObject;
  }

  /**
   * Implementation Routine toHexString.
   *
   * @param bytes byte[]
   * @return String
   */
  private static String toHexString(final byte[] bytes) {

    final char[] ret = new char[bytes.length * 2];
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
   * Utility function, generate a unique key identifier, but shorten to use as a filename.
   *
   * @return String
   */
  public static String getShortenUniqueKey() {
    final String val = (getUniqueKey() != null) ? getUniqueKey() : "valid_filename_key";
    // take the first 10 characters
    if (val.length() >= 10) {
      return val.substring(0, 9);
    } else {
      return "";
    }
  }

  /**
   * Utility function, generate a unique key identifier.
   *
   * @return String
   */
  public static String getUniqueKey() {

    String digest = "";
    try {
      final MessageDigest md = MessageDigest.getInstance("MD5");

      final String timeVal = "" + (System.currentTimeMillis() + 1);
      String localHost = "";
      try {
        localHost = InetAddress.getLocalHost().toString();
      } catch (UnknownHostException e) {
        // If an error, we can use other values.
        println("Warn: getUniqueKey(), Error trying to get localhost" + e.getMessage());
      }
      final String randVal = "" + new Random().nextInt();
      final String val = timeVal + localHost + randVal;
      md.reset();
      md.update(val.getBytes());

      // Generate the digest.

      digest = toHexString(md.digest());
    } catch (NoSuchAlgorithmException e) {
      println("Warn: getUniqueKey() " + e);
    } // End of the Try - Catchs

    return digest;
  }

  /**
   * System out, debugging message.
   *
   * @param message String
   */
  public static void println(final String message) {
    final String msg = "<XHTMLRenderer HTMLToPDF Message> (" + datestr() + ") " + message;
    Print.println(msg);
    trace(msg);
  }

  /**
   * Implementation Routine trace.
   *
   * @param msg String
   */
  public static void trace(final String msg) {
    Print.println("com.toolkit.util.pdf.XHTMLRendererBase" + msg);
  }

  /**
   * Implementation Routine datestr.
   *
   * @return String
   */
  public static String datestr() {
    return "" + (new Date());
  }

  ///////////////////////////////////////////////////////
  // Key Value Replace, can be used to replace values in the target document.
  ///////////////////////////////////////////////////////
  /**
   * Implementation Routine setKeyValue.
   *
   * @param replKey String
   * @param val String
   */
  public void setKeyValue(final String replKey, final String val) {

    // Don't set for non null values.
    if (val != null) {
      this.replKey = replKey;
      this.replVal = val;
    }
  }

  /**
   * Implementation Routine getReplKey.
   *
   * @return String
   */
  public String getReplKey() {
    return replKey;
  }

  /**
   * Implementation Routine setReplKey.
   *
   * @param replKey String
   */
  public void setReplKey(final String replKey) {
    this.replKey = replKey;
  }

  /**
   * Implementation Routine getReplVal.
   *
   * @return String
   */
  public String getReplVal() {
    return replVal;
  }

  /**
   * Implementation Routine setReplVal.
   *
   * @param replVal String
   */
  public void setReplVal(final String replVal) {
    this.replVal = replVal;
  }

  /** Implementation Routine setXhtmlProperties. */
  public static void setXhtmlProperties() {

    System.setProperty("xr.util-logging.loggingEnabled", "true");
    System.setProperty("xr.load.xml-reader", "org.ccil.cowan.tagsoup.Parser");
    System.setProperty("xr.util-logging.loggingEnabled", "true");
    System.setProperty("xr.util-logging.java.util.logging.ConsoleHandler.level", "INFO");
    System.setProperty("xr.util-logging.level", "INFO");
    System.setProperty("org.xhtmlrenderer.minium.quality", "highest");

    XRLog.setLoggingEnabled(true);
    // *******************************************************
  }

  /**
   * In the event of an invalid document, return a dummy XHTML document, collect a valid HTML
   * document when the user visits this filter.
   *
   * @param msg String
   * @return String
   */
  public static String dummyXhtmlDocument(final String msg) {

    final StringBuffer buf = new StringBuffer();
    buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
    buf.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    buf.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    buf.append("<head>");
    buf.append(" <style type=\"text/css\">");
    buf.append("   .error_text {");
    buf.append(
        "     	font-family: arial,helvetica,verdana,geneva,sans-serif; font-size: 11pt; color:"
            + " #700; padding: 16px; margin-left: 20px; margin-top: 20px;");
    buf.append("     	border: 1px solid #888; background-color: #f8f8f8");
    buf.append("   }");
    buf.append(" </style>");
    buf.append("</head><body>");

    // Build readable error message
    buf.append("<div class=\"error_text\">");
    buf.append(
        " A System Error Occurred While Trying to Create PDF Document. ("
            + datestr()
            + ") <br /> ERR: "
            + msg);
    buf.append("</div>");
    buf.append("</body>");
    buf.append("</html>");
    return buf.toString();
  }

  /**
   * Write the XHTML/HTML content to the outputstream. The outputstream might be a File output
   * stream, Servlet output stream or any other form of stream.
   *
   * @param doc
   * @param os
   * @throws Exception
   */
  public void writeITextOutputStream(final Document doc, final OutputStream os) throws Exception {

    final ITextRenderer renderer = new ITextRenderer();
    this.writeITextOutputStream(doc, os, renderer);
  }

  /**
   * Write the XHTML/HTML content to the outputstream. The outputstream might be a File output
   * stream, Servlet output stream or any other form of stream.
   *
   * @param doc
   * @param os
   * @throws Exception
   */
  public void writeITextOutputStream(
      final Document doc, final OutputStream os, final ITextRenderer itextRenderer)
      throws Exception {

    final ITextRenderer renderer = itextRenderer;
    renderer.setDocument(doc, null);
    renderer.layout();
    println("Sending output stream response to client");
    renderer.createPDF(os);
    os.flush();
    os.close();
  }

  /**
   * Implementation Routine writeITextByteArray.
   *
   * @param doc Document
   * @return byte[]
   * @throws Exception
   */
  public byte[] writeITextByteArray(final Document doc) throws Exception {

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.writeITextOutputStream(doc, baos);
    return baos.toByteArray();
  }

  /**
   * Write the XHTML/HTML content to a file.
   *
   * @param doc
   * @param filepath String
   * @throws Exception
   */
  private void writeITextFile(final Document doc, final String filepath) throws Exception {

    final FileOutputStream stream = new FileOutputStream(filepath);
    final byte[] data = this.writeITextByteArray(doc);
    try {
      if (stream != null) {
        stream.write(data);
      }
    } finally {
      if (stream != null) {
        stream.close();
      }
    } // End of the try - finally //
  }

  /**
   * Main method that invokes xhtmlrenderer, uses the input document to output the PDF document to a
   * stream.
   *
   * <p>Here is an example code snippet for loading a html file and outputting to PDF.
   *
   * @param os
   * @param parser
   * @throws Exception
   */
  public void parseDocumentStream(final OutputStream os, final XHTMLRendererText parser)
      throws Exception {

    this.parseDocumentStream(os, parser, new File(".").toURI().toURL().toExternalForm());
  }

  /**
   * Main method that invokes xhtmlrenderer, uses the input document to output the PDF document to a
   * stream.
   *
   * <p>Here is an example code snippet for loading a html file and outputting to PDF.
   *
   * @param os
   * @param parser
   * @throws Exception
   */
  public void parseDocumentStream(
      final OutputStream os, final XHTMLRendererText parser, final String baseURL)
      throws Exception {

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

    if ((formattedHtmlDoc == null) || (formattedHtmlDoc.length() == 0)) {
      // Invalid input document, fall back on the dummy xhtml document
      println(
          "<[ERROR] parseDocumentStream>: Renderer Parse Text Handler Failed, falling back to the"
              + " dummy document");
      formattedHtmlDoc = dummyXhtmlDocument("[Invalid Document at parseDocumentStream]");
    }

    if ((formattedHtmlDoc == null) || (formattedHtmlDoc.length() == 0)) {
      println("Invalid input document");
    } else {
      println(
          "<[INFO] parseDocumentStream>: Renderer Parse Text Handler - document size : "
              + formattedHtmlDoc.length());
    }
    println(
        "<parseDocumentStream> Attempting to parse document from stream, len="
            + formattedHtmlDoc.length());
    final XMLResource xmlResouce =
        XMLResource.load(new ByteArrayInputStream(formattedHtmlDoc.getBytes()));
    final Document doc = xmlResouce.getDocument();
    this.writeITextOutputStream(doc, os, new ITextRenderer(baseURL));
    println("Done with xhtmlrenderer - parseDocumentStream");
  }

  /**
   * Implementation Routine loadFile.
   *
   * @param path String
   * @return String
   */
  public static String loadFile(final String path) {

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
      } while (data != null);
    } catch (Exception e) {
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

  /**
   * Implementation Routine parseDocumentFile.
   *
   * @param path String
   * @param parser XHTMLRendererText
   * @throws Exception
   */
  public void parseDocumentFile(final String path, final XHTMLRendererText parser)
      throws Exception {

    final FileOutputStream stream = new FileOutputStream(path);
    if (parser != null) {
      parser.setBaseRenderer(this);
    }
    this.parseDocumentStream(stream, parser);
  }

  /**
   * Implementation Routine parseDocumentFile.
   *
   * @param path String
   * @param parser XHTMLRendererText
   * @throws Exception
   */
  public void parseDocumentFile(
      final String path, final XHTMLRendererText parser, final String baseURL) throws Exception {

    final FileOutputStream stream = new FileOutputStream(path);
    if (parser != null) {
      parser.setBaseRenderer(this);
    }
    this.parseDocumentStream(stream, parser, baseURL);
  }
} // End of Class
