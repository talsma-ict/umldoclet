/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.code.deflate;

/* 
 * Simple DEFLATE decompressor
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/simple-deflate-decompressor
 * https://github.com/nayuki/Simple-DEFLATE-decompressor
 */

import java.io.IOException;
import java.util.Objects;

/**
 * Stores a finite recent history of a byte stream. Useful as an implicit
 * dictionary for Lempel-Ziv schemes. Mutable and not thread-safe.
 */
final class ByteHistory {

	/*---- Fields ----*/

	// Circular buffer of byte data.
	private byte[] data;

	// Index of next byte to write to, always in the range [0, data.length).
	private int index;

	/*---- Constructor ----*/

	/**
	 * Constructs a byte history of the specified size, initialized to zeros.
	 * 
	 * @param size the size, which must be positive
	 * @throws IllegalArgumentException if size is zero or negative
	 */
	public ByteHistory(int size) {
		if (size < 1)
			throw new IllegalArgumentException("Size must be positive");
		data = new byte[size];
		index = 0;
	}

	/*---- Methods ----*/

	/**
	 * Appends the specified byte to this history. This overwrites the byte value at
	 * {@code size} positions ago.
	 * 
	 * @param b the byte value to append
	 */
	public void append(int b) {
		if (index < 0 || index >= data.length)
			throw new IllegalStateException();
		data[index] = (byte) b;
		index = (index + 1) % data.length;
	}

	/**
	 * Copies {@code len} bytes starting at {@code dist} bytes ago to the specified
	 * output stream and also back into this buffer itself.
	 * <p>
	 * Note that if the length exceeds the distance, then some of the output data
	 * will be a copy of data that was copied earlier in the process.
	 * </p>
	 * 
	 * @param dist the distance to go back, in the range [1, size]
	 * @param len  the length to copy, which must be at least 0
	 * @param out  the output stream to write to (not {@code null})
	 * @throws NullPointerException     if the output stream is {@code null}
	 * @throws IllegalArgumentException if the length is negative, distance is not
	 *                                  positive, or distance is greater than the
	 *                                  buffer size
	 * @throws IOException              if an I/O exception occurs
	 */
	public void copy(int dist, int len, OutputStreamProtected out) throws IOException {
		Objects.requireNonNull(out);
		if (len < 0 || dist < 1 || dist > data.length)
			throw new IllegalArgumentException();

		int readIndex = (index - dist + data.length) % data.length;
		if (readIndex < 0 || readIndex >= data.length)
			throw new IllegalStateException();

		for (int i = 0; i < len; i++) {
			byte b = data[readIndex];
			readIndex = (readIndex + 1) % data.length;
			out.write(b);
			append(b);
		}
	}

}
