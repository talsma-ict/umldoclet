/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2023, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
/*******************************************************************************
 * Copyright (c) 2016 EclipseSource.
 *
 * Distributed under MIT license
 * See https://github.com/ralfstx/minimal-json/blob/master/LICENSE
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package net.sourceforge.plantuml.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;


/**
 * Enables human readable JSON output by inserting whitespace between values.after commas and
 * colons. Example:
 *
 * <pre>
 * jsonValue.writeTo(writer, PrettyPrint.singleLine());
 * </pre>
 */
public class PrettyPrint extends WriterConfig {

  private final char[] indentChars;

  protected PrettyPrint(char[] indentChars) {
    this.indentChars = indentChars;
  }

  /**
   * Print every value on a separate line. Use tabs (<code>\t</code>) for indentation.
   *
   * @return A PrettyPrint instance for wrapped mode with tab indentation
   */
  public static PrettyPrint singleLine() {
    return new PrettyPrint(null);
  }

  /**
   * Print every value on a separate line. Use the given number of spaces for indentation.
   *
   * @param number
   *          the number of spaces to use
   * @return A PrettyPrint instance for wrapped mode with spaces indentation
   */
  public static PrettyPrint indentWithSpaces(int number) {
    if (number < 0) {
      throw new IllegalArgumentException("number is negative");
    }
    char[] chars = new char[number];
    Arrays.fill(chars, ' ');
    return new PrettyPrint(chars);
  }

  /**
   * Do not break lines, but still insert whitespace between values.
   *
   * @return A PrettyPrint instance for single-line mode
   */
  public static PrettyPrint indentWithTabs() {
    return new PrettyPrint(new char[] {'\t'});
  }

  @Override
  protected JsonWriter createWriter(Writer writer) {
    return new PrettyPrintWriter(writer, indentChars);
  }

  private static class PrettyPrintWriter extends JsonWriter {

    private final char[] indentChars;
    private int indent;

    private PrettyPrintWriter(Writer writer, char[] indentChars) {
      super(writer);
      this.indentChars = indentChars;
    }

    @Override
    protected void writeArrayOpen() throws IOException {
      indent++;
      writer.write('[');
      writeNewLine();
    }

    @Override
    protected void writeArrayClose() throws IOException {
      indent--;
      writeNewLine();
      writer.write(']');
    }

    @Override
    protected void writeArraySeparator() throws IOException {
      writer.write(',');
      if (!writeNewLine()) {
        writer.write(' ');
      }
    }

    @Override
    protected void writeObjectOpen() throws IOException {
      indent++;
      writer.write('{');
      writeNewLine();
    }

    @Override
    protected void writeObjectClose() throws IOException {
      indent--;
      writeNewLine();
      writer.write('}');
    }

    @Override
    protected void writeMemberSeparator() throws IOException {
      writer.write(':');
      writer.write(' ');
    }

    @Override
    protected void writeObjectSeparator() throws IOException {
      writer.write(',');
      if (!writeNewLine()) {
        writer.write(' ');
      }
    }

    private boolean writeNewLine() throws IOException {
      if (indentChars == null) {
        return false;
      }
      writer.write('\n');
      for (int i = 0; i < indent; i++) {
        writer.write(indentChars);
      }
      return true;
    }

  }

}
