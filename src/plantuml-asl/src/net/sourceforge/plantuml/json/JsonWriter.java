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


class JsonWriter {

  private static final int CONTROL_CHARACTERS_END = 0x001f;

  private static final char[] QUOT_CHARS = {'\\', '"'};
  private static final char[] BS_CHARS = {'\\', '\\'};
  private static final char[] LF_CHARS = {'\\', 'n'};
  private static final char[] CR_CHARS = {'\\', 'r'};
  private static final char[] TAB_CHARS = {'\\', 't'};
  // In JavaScript, U+2028 and U+2029 characters count as line endings and must be encoded.
  // http://stackoverflow.com/questions/2965293/javascript-parse-error-on-u2028-unicode-character
  private static final char[] UNICODE_2028_CHARS = {'\\', 'u', '2', '0', '2', '8'};
  private static final char[] UNICODE_2029_CHARS = {'\\', 'u', '2', '0', '2', '9'};
  private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                            'a', 'b', 'c', 'd', 'e', 'f'};

  protected final Writer writer;

  JsonWriter(Writer writer) {
    this.writer = writer;
  }

  protected void writeLiteral(String value) throws IOException {
    writer.write(value);
  }

  protected void writeNumber(String string) throws IOException {
    writer.write(string);
  }

  protected void writeString(String string) throws IOException {
    writer.write('"');
    writeJsonString(string);
    writer.write('"');
  }

  protected void writeArrayOpen() throws IOException {
    writer.write('[');
  }

  protected void writeArrayClose() throws IOException {
    writer.write(']');
  }

  protected void writeArraySeparator() throws IOException {
    writer.write(',');
  }

  protected void writeObjectOpen() throws IOException {
    writer.write('{');
  }

  protected void writeObjectClose() throws IOException {
    writer.write('}');
  }

  protected void writeMemberName(String name) throws IOException {
    writer.write('"');
    writeJsonString(name);
    writer.write('"');
  }

  protected void writeMemberSeparator() throws IOException {
    writer.write(':');
  }

  protected void writeObjectSeparator() throws IOException {
    writer.write(',');
  }

  protected void writeJsonString(String string) throws IOException {
    int length = string.length();
    int start = 0;
    for (int index = 0; index < length; index++) {
      char[] replacement = getReplacementChars(string.charAt(index));
      if (replacement != null) {
        writer.write(string, start, index - start);
        writer.write(replacement);
        start = index + 1;
      }
    }
    writer.write(string, start, length - start);
  }

  private static char[] getReplacementChars(char ch) {
    if (ch > '\\') {
      if (ch < '\u2028' || ch > '\u2029') {
        // The lower range contains 'a' .. 'z'. Only 2 checks required.
        return null;
      }
      return ch == '\u2028' ? UNICODE_2028_CHARS : UNICODE_2029_CHARS;
    }
    if (ch == '\\') {
      return BS_CHARS;
    }
    if (ch > '"') {
      // This range contains '0' .. '9' and 'A' .. 'Z'. Need 3 checks to get here.
      return null;
    }
    if (ch == '"') {
      return QUOT_CHARS;
    }
    if (ch > CONTROL_CHARACTERS_END) {
      return null;
    }
    if (ch == '\n') {
      return LF_CHARS;
    }
    if (ch == '\r') {
      return CR_CHARS;
    }
    if (ch == '\t') {
      return TAB_CHARS;
    }
    return new char[] {'\\', 'u', '0', '0', HEX_DIGITS[ch >> 4 & 0x000f], HEX_DIGITS[ch & 0x000f]};
  }

}
