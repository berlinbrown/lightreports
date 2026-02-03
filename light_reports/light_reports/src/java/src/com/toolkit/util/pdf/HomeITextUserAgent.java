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
 * <p>Date: 1/5/2009, 5/5/2010 7/15/2009 - Added Clojure 1.0, other performance fixes and cleanups.
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
 * <p>Metrics: (as of 7/15/2009) Light Log Viewer consists of 6500 lines of Clojure code, and
 * contains wrapper code around the Java source. There are 2000+ lines of Java code in the Java
 * library for Light Log Viewer.
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

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.PDFAsImage;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.swing.NaiveUserAgent;
import org.xhtmlrenderer.util.XRLog;

/**
 * XHTMLRenderer resource callback. Sub class of the XHTMLRenderer User Agent.
 *
 * @author Berlin Brown
 * @version 1.0 on 2/1/2009
 */
public class HomeITextUserAgent extends NaiveUserAgent {

  private static final int IMAGE_CACHE_CAPACITY = 32;

  private SharedContext _sharedContext;

  private ITextOutputDevice _outputDevice;

  /** Set the main base URL, to override any sub-classes attempting to change baseURL. */
  private String coreBaseURL = ".";

  public HomeITextUserAgent(final ITextOutputDevice outputDevice) {
    super(IMAGE_CACHE_CAPACITY);
    _outputDevice = outputDevice;
  }

  public SharedContext getSharedContext() {
    return _sharedContext;
  }

  public void setSharedContext(SharedContext sharedContext) {
    _sharedContext = sharedContext;
  }

  /**
   * URL relative to which URIs are resolved.
   *
   * @param url A URI which anchors other, possibly relative URIs.
   */
  public void setBaseURL(String url) {
    // IGNORE
  }

  /** Returns the current baseUrl for this class. */
  public String getBaseURL() {
    return this.coreBaseURL;
  }

  /**
   * Attempt to join the base URL and URI. Return null on error.
   *
   * @param uri String
   * @return String
   */
  public String findBaseURLAndURI(final String uri) {

    try {
      URL result = null;
      result = new URL(new URL(this.getBaseURL()), uri);
      return result.toString();
    } catch (MalformedURLException e1) {
      XRLog.exception(
          "The default NaiveUserAgent cannot resolve the URL "
              + uri
              + " with base URL "
              + this.getBaseURL());
    }
    return null;
  }

  /**
   * Resolves the URI; if absolute, leaves as is, if relative, returns an absolute URI based on the
   * baseUrl for the agent.
   *
   * @param uriDef String
   * @return A URI as String, resolved, or null if there was an exception (for example if the URI is
   *     malformed).
   * @see org.xhtmlrenderer.extend.UserAgentCallback#resolveURI(String)
   */
  public String resolveURI(final String uriDef) {

    if (uriDef == null) {
      return null;
    }

    System.out.println("<resolveURI 149> Base URL : " + this.getBaseURL());
    final String uri = uriDef;
    if (this.getBaseURL() == null) {
      try {

        final URL result = new URL(uri);
        final String externalForm = result.toExternalForm();
        setBaseURL(externalForm);

      } catch (MalformedURLException e) {

        try {
          setBaseURL(new File(".").toURI().toURL().toExternalForm());
        } catch (Exception e1) {
          XRLog.exception(
              "The default NaiveUserAgent doesn't know how to resolve the base URL for " + uri);
          return null;
        } // End of try catch
      } // End of if resolve URI
    } // End of if

    return this.findBaseURLAndURI(uri);
  }

  private void scaleToOutputResolution(Image image) {
    float factor = _sharedContext.getDotsPerPixel();
    image.scaleAbsolute(image.getPlainWidth() * factor, image.getPlainHeight() * factor);
  }

  /**
   * Implementation Routine getImageResource.
   *
   * @param uri String
   * @return ImageResource
   * @see org.xhtmlrenderer.extend.UserAgentCallback#getImageResource(String)
   */
  public ImageResource getImageResource(String uri) {

    ImageResource resource = null;
    uri = resolveURI(uri);
    resource = (ImageResource) _imageCache.get(uri);

    if (resource == null) {
      InputStream is = resolveAndOpenStream(uri);
      if (is != null) {
        try {
          URL url = new URL(uri);
          if (url.getPath() != null && url.getPath().toLowerCase().endsWith(".pdf")) {

            PdfReader reader = _outputDevice.getReader(url);
            PDFAsImage image = new PDFAsImage(url);
            Rectangle rect = reader.getPageSizeWithRotation(1);
            image.setInitialWidth(rect.width() * _outputDevice.getDotsPerPoint());
            image.setInitialHeight(rect.height() * _outputDevice.getDotsPerPoint());
            resource = new ImageResource(image);
          } else {
            Image image = Image.getInstance(url);
            scaleToOutputResolution(image);
            resource = new ImageResource(new ITextFSImage(image));
          }
          _imageCache.put(uri, resource);

        } catch (IOException e) {
          XRLog.exception("Can't read image file; unexpected problem for URI '" + uri + "'", e);
        } catch (BadElementException e) {
          XRLog.exception("Can't read image file; unexpected problem for URI '" + uri + "'", e);
        }
      }
    }
    if (resource == null) {
      resource = new ImageResource(null);
    }
    return resource;
  }

  /** Get the main base URL, to override any sub-classes attempting to change baseURL. */
  public String getCoreBaseURL() {
    return coreBaseURL;
  }

  /** Set the main base URL, to override any sub-classes attempting to change baseURL. */
  public void setCoreBaseURL(String coreBaseURL) {
    this.coreBaseURL = coreBaseURL;
  }
} // End of the Class //
