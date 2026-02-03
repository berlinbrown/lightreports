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

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.octane.util.StringUtils;
import com.toolkit.util.Print;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserInterface;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFontContext;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElementFactory;
import org.xhtmlrenderer.pdf.ITextTextRenderer;
import org.xhtmlrenderer.pdf.PDFEncryption;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.ViewportBox;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.util.Configuration;

/**
 * XHTMLRenderer main class for invoking the parser.
 *
 * @author Berlin Brown
 * @version 1.0 on 2/1/2009
 */
public class ITextRenderer {

  public static final String DEFAULT_BASE_URL = "file:///usr/local/htdocs/";

  // These two defaults combine to produce an effective resolution of 96 px to
  // the inch
  public static final float DEFAULT_DOTS_PER_POINT = 20f * 4f / 3f;
  public static final int DEFAULT_DOTS_PER_PIXEL = 20;

  private SharedContext _sharedContext;
  private ITextOutputDevice _outputDevice;

  private Document _doc;
  private Box _root;

  private float _dotsPerPoint;

  private com.lowagie.text.Document _pdfDoc;
  private PdfWriter _writer;

  private PDFEncryption _pdfEncryption;

  /** Constructor for ITextRenderer. */
  public ITextRenderer() {
    this(DEFAULT_DOTS_PER_POINT, DEFAULT_DOTS_PER_PIXEL);
  }

  /**
   * Constructor for ITextRenderer.
   *
   * @param dotsPerPoint float
   * @param dotsPerPixel int
   */
  public ITextRenderer(final float dotsPerPoint, final int dotsPerPixel) {
    this(dotsPerPoint, dotsPerPixel, DEFAULT_BASE_URL);
  }

  /** Constructor for ITextRenderer. */
  public ITextRenderer(final String baseurl) {
    this(DEFAULT_DOTS_PER_POINT, DEFAULT_DOTS_PER_PIXEL, baseurl);
  }

  /**
   * @param dotsPerPoint
   * @param dotsPerPixel
   * @param coreBaseURL
   */
  public ITextRenderer(
      final float dotsPerPoint, final int dotsPerPixel, final String coreBaseURLIn) {

    final String coreBaseURL =
        StringUtils.isEmpty(coreBaseURLIn) ? DEFAULT_BASE_URL : coreBaseURLIn;

    _dotsPerPoint = dotsPerPoint;
    _outputDevice = new ITextOutputDevice(_dotsPerPoint);

    final HomeITextUserAgent userAgent = new HomeITextUserAgent(_outputDevice);
    userAgent.setCoreBaseURL(coreBaseURL);
    _sharedContext = new SharedContext(userAgent);
    _sharedContext.setBaseURL(coreBaseURL);

    userAgent.setSharedContext(_sharedContext);
    _outputDevice.setSharedContext(_sharedContext);

    ITextFontResolver fontResolver = new ITextFontResolver(_sharedContext);
    _sharedContext.setFontResolver(fontResolver);

    ITextReplacedElementFactory replacedElementFactory =
        new ITextReplacedElementFactory(_outputDevice);
    _sharedContext.setReplacedElementFactory(replacedElementFactory);

    _sharedContext.setTextRenderer(new ITextTextRenderer());
    _sharedContext.setDPI(72 * _dotsPerPoint);
    _sharedContext.setDotsPerPixel(dotsPerPixel);
    _sharedContext.setPrint(true);
    _sharedContext.setInteractive(false);

    userAgent.setBaseURL(coreBaseURL);
    _sharedContext.setBaseURL(coreBaseURL);
  }

  /**
   * Implementation Routine getFontResolver.
   *
   * @return ITextFontResolver
   */
  public ITextFontResolver getFontResolver() {
    return (ITextFontResolver) _sharedContext.getFontResolver();
  }

  /**
   * Implementation Routine loadDocument.
   *
   * @param uri String
   * @return Document
   */
  private Document loadDocument(final String uri) {
    return _sharedContext.getUac().getXMLResource(uri).getDocument();
  }

  /**
   * Implementation Routine setDocument.
   *
   * @param uri String
   */
  public void setDocument(final String uri) {
    setDocument(loadDocument(uri), uri);
  }

  /**
   * Implementation Routine setDocument.
   *
   * @param doc Document
   * @param url String
   */
  public void setDocument(final Document doc, final String url) {
    setDocument(doc, url, new XhtmlNamespaceHandler());
  }

  /**
   * Implementation Routine setDocument.
   *
   * @param file File
   * @throws IOException
   */
  public void setDocument(final File file) throws IOException {

    File parent = file.getParentFile();
    Print.println("<ITextRender, setDocument> : " + parent.getAbsolutePath());
    setDocument(
        loadDocument(file.toURI().toURL().toExternalForm()),
        (parent == null ? "" : parent.toURI().toURL().toExternalForm()));
  }

  /**
   * Implementation Routine setDocument.
   *
   * @param doc Document
   * @param url String
   * @param nsh NamespaceHandler
   */
  public void setDocument(final Document doc, final String url, final NamespaceHandler nsh) {
    Print.println("<ITextRenderer> setDocument, url=" + url);
    _doc = doc;
    getFontResolver().flushFontFaceFonts();
    _sharedContext.reset();
    if (Configuration.isTrue("xr.cache.stylesheets", true)) {
      _sharedContext.getCss().flushStyleSheets();
    } else {
      _sharedContext.getCss().flushAllStyleSheets();
    }
    _sharedContext.setBaseURL(url);
    _sharedContext.setNamespaceHandler(nsh);
    _sharedContext
        .getCss()
        .setDocumentContext(
            _sharedContext, _sharedContext.getNamespaceHandler(), doc, new NullUserInterface());
    getFontResolver().importFontFaces(_sharedContext.getCss().getFontFaceRules());
  }

  /**
   * Implementation Routine getPDFEncryption.
   *
   * @return PDFEncryption
   */
  public PDFEncryption getPDFEncryption() {
    return _pdfEncryption;
  }

  /**
   * Implementation Routine setPDFEncryption.
   *
   * @param pdfEncryption PDFEncryption
   */
  public void setPDFEncryption(final PDFEncryption pdfEncryption) {
    _pdfEncryption = pdfEncryption;
  }

  /** Implementation Routine layout. */
  public void layout() {
    LayoutContext c = newLayoutContext();
    BlockBox root = BoxBuilder.createRootBox(c, _doc);
    root.setContainingBlock(new ViewportBox(getInitialExtents(c)));
    root.layout(c);
    Dimension dim = root.getLayer().getPaintingDimension(c);
    root.getLayer().trimEmptyPages(c, dim.height);
    root.getLayer().layoutPages(c);
    _root = root;
  }

  /**
   * Implementation Routine getInitialExtents.
   *
   * @param c LayoutContext
   * @return Rectangle
   */
  private Rectangle getInitialExtents(final LayoutContext c) {

    PageBox first = Layer.createPageBox(c, "first");
    return new Rectangle(0, 0, first.getContentWidth(c), first.getContentHeight(c));
  }

  /**
   * Implementation Routine newRenderingContext.
   *
   * @return RenderingContext
   */
  private RenderingContext newRenderingContext() {
    RenderingContext result = _sharedContext.newRenderingContextInstance();
    result.setFontContext(new ITextFontContext());

    result.setOutputDevice(_outputDevice);

    _sharedContext.getTextRenderer().setup(result.getFontContext());

    result.setRootLayer(_root.getLayer());

    return result;
  }

  /**
   * Implementation Routine newLayoutContext.
   *
   * @return LayoutContext
   */
  private LayoutContext newLayoutContext() {
    LayoutContext result = _sharedContext.newLayoutContextInstance();
    result.setFontContext(new ITextFontContext());

    _sharedContext.getTextRenderer().setup(result.getFontContext());

    return result;
  }

  /**
   * Implementation Routine createPDF.
   *
   * @param os OutputStream
   * @throws DocumentException
   */
  public void createPDF(final OutputStream os) throws DocumentException {
    createPDF(os, true);
  }

  /**
   * Implementation Routine writeNextDocument.
   *
   * @throws DocumentException
   */
  public void writeNextDocument() throws DocumentException {
    writeNextDocument(0);
  }

  /**
   * Implementation Routine writeNextDocument.
   *
   * @param initialPageNo int
   * @throws DocumentException
   */
  public void writeNextDocument(final int initialPageNo) throws DocumentException {
    List pages = _root.getLayer().getPages();

    RenderingContext c = newRenderingContext();
    c.setInitialPageNo(initialPageNo);
    PageBox firstPage = (PageBox) pages.get(0);
    com.lowagie.text.Rectangle firstPageSize =
        new com.lowagie.text.Rectangle(
            0, 0, firstPage.getWidth(c) / _dotsPerPoint, firstPage.getHeight(c) / _dotsPerPoint);

    _outputDevice.setStartPageNo(_writer.getPageNumber());

    _pdfDoc.setPageSize(firstPageSize);
    _pdfDoc.newPage();

    writePDF(pages, c, firstPageSize, _pdfDoc, _writer);
  }

  /** Implementation Routine finishPDF. */
  public void finishPDF() {
    if (_pdfDoc != null) {
      _pdfDoc.close();
    }
  }

  /**
   * <B>NOTE:</B> Caller is responsible for cleaning up the OutputStream if something goes wrong.
   *
   * @param os OutputStream
   * @param finish boolean
   * @throws DocumentException
   */
  public void createPDF(final OutputStream os, final boolean finish) throws DocumentException {
    List pages = _root.getLayer().getPages();

    RenderingContext c = newRenderingContext();
    PageBox firstPage = (PageBox) pages.get(0);
    com.lowagie.text.Rectangle firstPageSize =
        new com.lowagie.text.Rectangle(
            0, 0, firstPage.getWidth(c) / _dotsPerPoint, firstPage.getHeight(c) / _dotsPerPoint);

    com.lowagie.text.Document doc = new com.lowagie.text.Document(firstPageSize, 0, 0, 0, 0);
    PdfWriter writer = PdfWriter.getInstance(doc, os);
    if (_pdfEncryption != null) {
      // writer.setEncryption(true, _pdfEncryption.getUserPassword(),
      // _pdfEncryption.getOwnerPassword(),
      // _pdfEncryption.getAllowedPrivileges());
    }
    doc.open();

    if (!finish) {
      _pdfDoc = doc;
      _writer = writer;
    }

    writePDF(pages, c, firstPageSize, doc, writer);

    if (finish) {
      doc.close();
    }
  }

  /**
   * Implementation Routine writePDF.
   *
   * @param pages List
   * @param c RenderingContext
   * @param firstPageSize com.lowagie.text.Rectangle
   * @param doc com.lowagie.text.Document
   * @param writer PdfWriter
   * @throws DocumentException
   */
  private void writePDF(
      final List pages,
      final RenderingContext c,
      final com.lowagie.text.Rectangle firstPageSize,
      final com.lowagie.text.Document doc,
      final PdfWriter writer)
      throws DocumentException {
    _outputDevice.setRoot(_root);

    _outputDevice.start(_doc);
    _outputDevice.setWriter(writer);
    _outputDevice.initializePage(writer.getDirectContent(), firstPageSize.height());

    _root.getLayer().assignPagePaintingPositions(c, Layer.PAGED_MODE_PRINT);

    int pageCount = _root.getLayer().getPages().size();
    c.setPageCount(pageCount);
    for (int i = 0; i < pageCount; i++) {
      PageBox currentPage = (PageBox) pages.get(i);
      c.setPage(i, currentPage);
      paintPage(c, currentPage);
      _outputDevice.finishPage();
      if (i != pageCount - 1) {
        PageBox nextPage = (PageBox) pages.get(i + 1);
        com.lowagie.text.Rectangle nextPageSize =
            new com.lowagie.text.Rectangle(
                0, 0, nextPage.getWidth(c) / _dotsPerPoint, nextPage.getHeight(c) / _dotsPerPoint);
        doc.setPageSize(nextPageSize);
        doc.newPage();
        _outputDevice.initializePage(writer.getDirectContent(), nextPageSize.height());
      }
    }

    _outputDevice.finish(c, _root);
  }

  /**
   * Implementation Routine paintPage.
   *
   * @param c RenderingContext
   * @param page PageBox
   */
  private void paintPage(final RenderingContext c, final PageBox page) {

    page.paintBackground(c, 0, Layer.PAGED_MODE_PRINT);
    page.paintMarginAreas(c, 0, Layer.PAGED_MODE_PRINT);
    page.paintBorder(c, 0, Layer.PAGED_MODE_PRINT);

    Shape working = _outputDevice.getClip();
    Rectangle content = page.getPrintClippingBounds(c);
    _outputDevice.clip(content);

    int top = -page.getPaintingTop() + page.getMarginBorderPadding(c, CalculatedStyle.TOP);

    int left = page.getMarginBorderPadding(c, CalculatedStyle.LEFT);

    _outputDevice.translate(left, top);
    _root.getLayer().paint(c);
    _outputDevice.translate(-left, -top);

    _outputDevice.setClip(working);
  }

  /**
   * Implementation Routine getOutputDevice.
   *
   * @return ITextOutputDevice
   */
  public ITextOutputDevice getOutputDevice() {
    return _outputDevice;
  }

  /**
   * Implementation Routine getSharedContext.
   *
   * @return SharedContext
   */
  public SharedContext getSharedContext() {
    return _sharedContext;
  }

  /**
   * Implementation Routine exportText.
   *
   * @param writer Writer
   * @throws IOException
   */
  public void exportText(final Writer writer) throws IOException {
    RenderingContext c = newRenderingContext();
    c.setPageCount(_root.getLayer().getPages().size());
    _root.exportText(c, writer);
  }

  /**
   * @author Berlin
   */
  private static final class NullUserInterface implements UserInterface {
    /**
     * Implementation Routine isHover.
     *
     * @param e Element
     * @return boolean
     * @see org.xhtmlrenderer.extend.UserInterface#isHover(Element)
     */
    public boolean isHover(final Element e) {
      return false;
    }

    /**
     * Implementation Routine isActive.
     *
     * @param e Element
     * @return boolean
     * @see org.xhtmlrenderer.extend.UserInterface#isActive(Element)
     */
    public boolean isActive(final Element e) {
      return false;
    }

    /**
     * Implementation Routine isFocus.
     *
     * @param e Element
     * @return boolean
     * @see org.xhtmlrenderer.extend.UserInterface#isFocus(Element)
     */
    public boolean isFocus(final Element e) {
      return false;
    }
  }
} // End of the Class //
