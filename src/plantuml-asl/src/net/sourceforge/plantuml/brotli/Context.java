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
/* Copyright 2015 Google Inc. All Rights Reserved.

   https://github.com/google/brotli/blob/master/LICENSE

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package net.sourceforge.plantuml.brotli;

/**
 * Common context lookup table for all context modes.
 */
final class Context {

  static final int[] LOOKUP = new int[2048];

  private static final String UTF_MAP = "         !!  !                  \"#$##%#$&'##(#)#+++++++++"
      + "+((&*'##,---,---,-----,-----,-----&#'###.///.///./////./////./////&#'# ";
  private static final String UTF_RLE = "A/*  ':  & : $  \u0081 @";

  private static void unpackLookupTable(int[] lookup, String map, String rle) {
    // LSB6, MSB6, SIGNED
    for (int i = 0; i < 256; ++i) {
      lookup[i] = i & 0x3F;
      lookup[512 + i] = i >> 2;
      lookup[1792 + i] = 2 + (i >> 6);
    }
    // UTF8
    for (int i = 0; i < 128; ++i) {
      lookup[1024 + i] = 4 * (map.charAt(i) - 32);
    }
    for (int i = 0; i < 64; ++i) {
      lookup[1152 + i] = i & 1;
      lookup[1216 + i] = 2 + (i & 1);
    }
    int offset = 1280;
    for (int k = 0; k < 19; ++k) {
      int value = k & 3;
      int rep = rle.charAt(k) - 32;
      for (int i = 0; i < rep; ++i) {
        lookup[offset++] = value;
      }
    }
    // SIGNED
    for (int i = 0; i < 16; ++i) {
      lookup[1792 + i] = 1;
      lookup[2032 + i] = 6;
    }
    lookup[1792] = 0;
    lookup[2047] = 7;
    for (int i = 0; i < 256; ++i) {
      lookup[1536 + i] = lookup[1792 + i] << 3;
    }
  }

  static {
    unpackLookupTable(LOOKUP, UTF_MAP, UTF_RLE);
  }
}
