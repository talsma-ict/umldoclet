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
/* 	This file is taken from
	https://github.com/andreas1327250/argon2-java

	Original Author: Andreas Gadermaier <up.gadermaier@gmail.com>
 */
package net.sourceforge.plantuml.argon2.model;


import static net.sourceforge.plantuml.argon2.Constants.ARGON2_BLOCK_SIZE;
import static net.sourceforge.plantuml.argon2.Constants.ARGON2_QWORDS_IN_BLOCK;

import java.util.Arrays;

import net.sourceforge.plantuml.argon2.Util;

public class Block {

    /* 128 * 8 Byte QWords */
    public long[] v;

    public Block() {
        v = new long[ARGON2_QWORDS_IN_BLOCK];
    }

    public void fromBytes(byte[] input) {
        assert (input.length == ARGON2_BLOCK_SIZE);

        for (int i = 0; i < v.length; i++) {
            byte[] slice = Arrays.copyOfRange(input, i * 8, (i + 1) * 8);
            v[i] = Util.littleEndianBytesToLong(slice);
        }
    }

    public byte[] toBytes() {
        byte[] result = new byte[ARGON2_BLOCK_SIZE];

        for (int i = 0; i < v.length; i++) {
            byte[] bytes = Util.longToLittleEndianBytes(v[i]);
            System.arraycopy(bytes, 0, result, i * bytes.length, bytes.length);
        }

        return result;
    }

    public void copyBlock(Block other) {
        System.arraycopy(other.v, 0, v, 0, v.length);
    }

    public void xor(Block b1, Block b2) {
        for (int i = 0; i < v.length; i++) {
            v[i] = b1.v[i] ^ b2.v[i];
        }
    }

    public void xor(Block b1, Block b2, Block b3) {
        for (int i = 0; i < v.length; i++) {
            v[i] = b1.v[i] ^ b2.v[i] ^ b3.v[i];
        }
    }

    public void xorWith(Block other) {
        for (int i = 0; i < v.length; i++) {
            v[i] = v[i] ^ other.v[i];
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (long value : v) {
            result.append(Util.bytesToHexString(Util.longToLittleEndianBytes(value)));
        }

        return result.toString();
    }

    void clear() {
        Arrays.fill(v, 0);
    }
}

