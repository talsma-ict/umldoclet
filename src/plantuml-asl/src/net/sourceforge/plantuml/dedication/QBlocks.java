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
package net.sourceforge.plantuml.dedication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QBlocks {

	private final List<QBlock> all = new ArrayList<QBlock>();

	private QBlocks() {

	}

	public static QBlocks readFrom(InputStream source, int size) throws IOException {
		final QBlocks result = new QBlocks();
		while (true) {
			final QBlock block = QBlock.read(source, size);
			if (block == null) {
				return result;
			}
			result.all.add(block);
		}
	}

	public QBlocks change(BigInteger E, BigInteger N) {
		final QBlocks result = new QBlocks();
		for (QBlock rsa : all) {
			result.all.add(rsa.change(E, N));
		}
		return result;
	}

	public void writeTo(OutputStream os, int size) throws IOException {
		for (QBlock rsa : all) {
			rsa.write(os, size);
		}
	}

//	public String encodeAscii() {
//		final StringBuilder sb = new StringBuilder();
//		final AsciiEncoder encoder = new AsciiEncoder();
//		for (QBlock rsa : all) {
//			sb.append(encoder.encode(rsa.getDataRaw()));
//			sb.append("!");
//		}
//		return sb.toString();
//	}

//	public static QBlocks descodeAscii(String s) {
//		final QBlocks result = new QBlocks();
//		final AsciiEncoder encoder = new AsciiEncoder();
//		for (String bl : s.split("!")) {
//			final BigInteger bigInteger = new BigInteger(encoder.decode(bl));
//			result.all.add(new QBlock(bigInteger));
//
//		}
//		return result;
//	}
}
