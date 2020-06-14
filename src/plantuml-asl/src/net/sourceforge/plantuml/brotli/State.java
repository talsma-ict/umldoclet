/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package net.sourceforge.plantuml.brotli;

import java.io.InputStream;

final class State {
  byte[] ringBuffer;
  byte[] contextModes;
  byte[] contextMap;
  byte[] distContextMap;
  byte[] output;
  byte[] byteBuffer;  // BitReader

  short[] shortBuffer; // BitReader

  int[] intBuffer;  // BitReader
  int[] rings;
  int[] blockTrees;
  int[] hGroup0;
  int[] hGroup1;
  int[] hGroup2;

  long accumulator64;  // BitReader: pre-fetched bits.

  int runningState;  // Default value is 0 == Decode.UNINITIALIZED
  int nextRunningState;
  int accumulator32;  // BitReader: pre-fetched bits.
  int bitOffset;  // BitReader: bit-reading position in accumulator.
  int halfOffset;  // BitReader: offset of next item in intBuffer/shortBuffer.
  int tailBytes;  // BitReader: number of bytes in unfinished half.
  int endOfStreamReached;  // BitReader: input stream is finished.
  int metaBlockLength;
  int inputEnd;
  int isUncompressed;
  int isMetadata;
  int literalBlockLength;
  int numLiteralBlockTypes;
  int commandBlockLength;
  int numCommandBlockTypes;
  int distanceBlockLength;
  int numDistanceBlockTypes;
  int pos;
  int maxDistance;
  int distRbIdx;
  int trivialLiteralContext;
  int literalTreeIndex;
  int literalTree;
  int j;
  int insertLength;
  int contextMapSlice;
  int distContextMapSlice;
  int contextLookupOffset1;
  int contextLookupOffset2;
  int treeCommandOffset;
  int distanceCode;
  int numDirectDistanceCodes;
  int distancePostfixMask;
  int distancePostfixBits;
  int distance;
  int copyLength;
  int copyDst;
  int maxBackwardDistance;
  int maxRingBufferSize;
  int ringBufferSize;
  int expectedTotalSize;
  int outputOffset;
  int outputLength;
  int outputUsed;
  int bytesWritten;
  int bytesToWrite;

  InputStream input; // BitReader

  State() {
    this.ringBuffer = new byte[0];
    this.rings = new int[10];
    this.rings[0] = 16;
    this.rings[1] = 15;
    this.rings[2] = 11;
    this.rings[3] = 4;
  }
}
