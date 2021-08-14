/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
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
/* Copyright 2015 Google Inc. All Rights Reserved.

   https://github.com/google/brotli/blob/master/LICENSE

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package net.sourceforge.plantuml.brotli;

import java.nio.ByteBuffer;

/**
 * Collection of static dictionary words.
 *
 * <p>Dictionary content is loaded from binary resource when {@link #getData()} is executed for the
 * first time. Consequently, it saves memory and CPU in case dictionary is not required.
 *
 * <p>One possible drawback is that multiple threads that need dictionary data may be blocked (only
 * once in each classworld). To avoid this, it is enough to call {@link #getData()} proactively.
 */
public final class Dictionary {
  private static volatile ByteBuffer data;

  private static class DataLoader {
    static final boolean OK;

    static {
      boolean ok = true;
      try {
        Class.forName(Dictionary.class.getPackage().getName() + ".DictionaryData");
      } catch (Throwable ex) {
        ok = false;
      }
      OK = ok;
    }
  }

  public static void setData(ByteBuffer data) {
    if (!data.isDirect() || !data.isReadOnly()) {
      throw new BrotliRuntimeException("data must be a direct read-only byte buffer");
    }
    Dictionary.data = data;
  }

  public static ByteBuffer getData() {
    if (data != null) {
      return data;
    }
    if (!DataLoader.OK) {
      throw new BrotliRuntimeException("brotli dictionary is not set");
    }
    /* Might have been set when {@link DictionaryData} was loaded.*/
    return data;
  }
}
