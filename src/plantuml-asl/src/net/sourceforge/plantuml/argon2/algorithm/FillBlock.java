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
package net.sourceforge.plantuml.argon2.algorithm;

import net.sourceforge.plantuml.argon2.model.Block;

class FillBlock {

    static void fillBlock(Block X, Block Y, Block currentBlock, boolean withXor) {

        Block R = new Block();
        Block Z = new Block();

        R.xor(X, Y);
        Z.copyBlock(R);

        /* Apply Blake2 on columns of 64-bit words: (0,1,...,15) , then
        (16,17,..31)... finally (112,113,...127) */
        for (int i = 0; i < 8; i++) {

            Functions.roundFunction(Z,
                    16 * i, 16 * i + 1, 16 * i + 2,
                    16 * i + 3, 16 * i + 4, 16 * i + 5,
                    16 * i + 6, 16 * i + 7, 16 * i + 8,
                    16 * i + 9, 16 * i + 10, 16 * i + 11,
                    16 * i + 12, 16 * i + 13, 16 * i + 14,
                    16 * i + 15
            );
        }

        /* Apply Blake2 on rows of 64-bit words: (0,1,16,17,...112,113), then
        (2,3,18,19,...,114,115).. finally (14,15,30,31,...,126,127) */
        for (int i = 0; i < 8; i++) {

            Functions.roundFunction(Z,
                    2 * i, 2 * i + 1, 2 * i + 16,
                    2 * i + 17, 2 * i + 32, 2 * i + 33,
                    2 * i + 48, 2 * i + 49, 2 * i + 64,
                    2 * i + 65, 2 * i + 80, 2 * i + 81,
                    2 * i + 96, 2 * i + 97, 2 * i + 112,
                    2 * i + 113
            );

        }

        if (withXor) {
            currentBlock.xor(R, Z, currentBlock);
        } else {
            currentBlock.xor(R, Z);
        }
    }
}
