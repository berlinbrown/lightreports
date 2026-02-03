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

import com.toolkit.util.Print;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility interface for taking an input HTML document, formatting the content and then ensuring
 * that is properly formed for XHTMLRenderer and TagSoup.
 *
 * <p>APPLICATION LEVEL
 *
 * @author Berlin
 * @version $Revision: 1.0 $
 */
public interface XHTMLRendererText {

  /**
   * Set the original input text document.
   *
   * @param text
   */
  public void setText(final String text);

  /**
   * Return the original text document.
   *
   * @return String
   */
  public String getText();

  /**
   * Parse the input document and return the formatted text.
   *
   * @return String
   */
  public String parse();

  /**
   * Implementation Routine setBaseRenderer.
   *
   * @param base XHTMLRendererBase
   */
  public void setBaseRenderer(final XHTMLRendererBase base);

  /**
   * Implementation Routine getBaseRenderer.
   *
   * @return XHTMLRendererBase
   */
  public XHTMLRendererBase getBaseRenderer();

  ///////////////////////////////////////////////////////////////////////////
  // XHtml Tuple Non-Static Inner Classes
  // GroupPosTuple and TextTuple.
  ///////////////////////////////////////////////////////////////////////////

  /**
   * Simple tuple data structure, <Group Start, End Positions>
   *
   * @author Berlin
   * @version $Revision: 1.0 $
   */
  public final class GroupPosTuple {

    private final int start;
    private final int end;

    /**
     * Constructor for GroupPosTuple.
     *
     * @param s int
     * @param e int
     */
    public GroupPosTuple(final int s, final int e) {
      start = s;
      end = e;
    }

    /**
     * Implementation Routine getStart.
     *
     * @return int
     */
    public final int getStart() {
      return this.start;
    }

    /**
     * Implementation Routine getEnd.
     *
     * @return int
     */
    public final int getEnd() {
      return this.end;
    }

    /**
     * Implementation Routine toString.
     *
     * @return String
     */
    public String toString() {
      return "<<GroupPosTuple start=" + start + " end=" + end + ">>";
    }
  } // End of Class

  /**
   * Simple tuple data structure, <text>,<List of Group Positions>
   *
   * @author Berlin
   * @version $Revision: 1.0 $
   */
  public final class TextTuple {

    private String text = "";
    private final List pos_tuples = new ArrayList();

    /**
     * Constructor for TextTuple.
     *
     * @param doc String
     * @param s int
     * @param e int
     */
    public TextTuple(final String doc, final int s, final int e) {
      this(doc);
      // Set the first group pos tuple.
      this.addPosTuple(s, e);
    }

    /** Constructor for TextTuple. */
    public TextTuple() {}

    /**
     * Constructor for TextTuple.
     *
     * @param doc String
     */
    public TextTuple(final String doc) {
      if (doc == null) {
        text = "";
      } else {
        text = doc;
      }
    }

    /**
     * Implementation Routine toString.
     *
     * @return String
     */
    public String toString() {
      return this.text;
    }

    /**
     * Implementation Routine getTuples.
     *
     * @return List
     */
    public List getTuples() {
      return pos_tuples;
    }

    /**
     * @return the text
     */
    public final String getText() {
      return text;
    }

    /**
     * Implementation Routine setText.
     *
     * @param txt String
     */
    public final void setText(String txt) {
      this.text = txt;
    }

    /**
     * Implementation Routine addPosTuple.
     *
     * @param s int
     * @param e int
     * @return GroupPosTuple
     */
    public final GroupPosTuple addPosTuple(final int s, final int e) {
      final GroupPosTuple tuple = new GroupPosTuple(s, e);
      pos_tuples.add(tuple);
      return tuple;
    }
  } // End of Class

  /**
   * @author Berlin
   */
  public final class TextUtils {

    /**
     * Implementation Routine removeHtmlSection.
     *
     * @param maindoc String
     * @param repl String
     * @param startTag String
     * @param endTag String
     * @return String
     */
    public static final String removeHtmlSection(
        final String maindoc, final String repl, final String startTag, final String endTag) {

      // Compile regular expression
      final StringBuffer track_pattern_buf = new StringBuffer();
      track_pattern_buf.append(startTag);
      track_pattern_buf.append("(.*?)");
      track_pattern_buf.append(endTag);

      // final Pattern pattern = Pattern.compile(track_pattern_buf.toString());
      final Pattern pattern = new_pattern(track_pattern_buf.toString());

      final int lorig = maindoc.length();
      // Replace all occurrences of pattern in input
      final Matcher matcher = pattern.matcher(maindoc);
      final String output = matcher.replaceAll(repl);
      final int lnew = output.length();
      if (lnew == lorig) {
        // Qualify when a text change has been made
        Print.println(
            "<Searching for tags for further processing, failed [["
                + startTag
                + "]]> diff:"
                + (lorig - lnew));
      }
      return output;
    }

    /**
     * Implementation Routine new_pattern.
     *
     * @param pattern String
     * @return Pattern
     */
    public static final Pattern new_pattern(final String pattern) {
      return Pattern.compile(
          pattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    }

    /**
     * Implementation Routine findMatch.
     *
     * @param text String
     * @param pattern_str String
     * @param group int
     * @param total_groups int
     * @return TextTuple
     */
    public static final TextTuple findMatch(
        final String text, final String pattern_str, int group, int total_groups) {

      final StringBuffer res = new StringBuffer();
      final Pattern pattern = new_pattern(pattern_str);
      final Matcher match = pattern.matcher(text);

      final TextTuple tuple = new TextTuple();
      while (match.find()) {
        if (match.groupCount() >= total_groups) {
          res.append(match.group(group));
          tuple.addPosTuple(match.start(group), match.end(group));
        }
      } // End of While
      tuple.setText(res.toString());
      return tuple;
    }
  }
} // End of the Class //
