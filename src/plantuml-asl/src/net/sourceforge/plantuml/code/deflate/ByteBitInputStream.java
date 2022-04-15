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
package net.sourceforge.plantuml.code.deflate;

/* 
 * Simple DEFLATE decompressor
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/simple-deflate-decompressor
 * https://github.com/nayuki/Simple-DEFLATE-decompressor
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


/**
 * A stream of bits that can be read. Because they come from an underlying byte stream,
 * the total number of bits is always a multiple of 8. The bits are read in little endian.
 * Mutable and not thread-safe.
 */
public final class ByteBitInputStream implements BitInputStream {
	
	/*---- Fields ----*/
	
	// The underlying byte stream to read from (not null).
	private InputStream input;
	
	// Either in the range [0x00, 0xFF] if bits are available, or -1 if end of stream is reached.
	private int currentByte;
	
	// Number of remaining bits in the current byte, always between 0 and 7 (inclusive).
	private int numBitsRemaining;
	
	
	
	/*---- Constructor ----*/
	
	/**
	 * Constructs a bit input stream based on the specified byte input stream.
	 * @param in the byte input stream (not {@code null})
	 * @throws NullPointerException if the input stream is {@code null}
	 */
	public ByteBitInputStream(InputStream in) {
		input = Objects.requireNonNull(in);
		currentByte = 0;
		numBitsRemaining = 0;
	}
	
	
	
	/*---- Methods ----*/
	
	public int getBitPosition() {
		if (numBitsRemaining < 0 || numBitsRemaining > 7)
			throw new IllegalStateException();
		return (8 - numBitsRemaining) % 8;
	}
	
	
	public int readByte() throws IOException {
		currentByte = 0;
		numBitsRemaining = 0;
		return input.read();
	}
	
	
	public int read() throws IOException {
		if (currentByte == -1)
			return -1;
		if (numBitsRemaining == 0) {
			currentByte = input.read();
			if (currentByte == -1)
				return -1;
			numBitsRemaining = 8;
		}
		if (numBitsRemaining <= 0)
			throw new IllegalStateException();
		numBitsRemaining--;
		return (currentByte >>> (7 - numBitsRemaining)) & 1;
	}
	
	
	public int readNoEof() throws IOException {
		int result = read();
		if (result == -1)
			throw new EOFException();
		return result;
	}
	
	
	public void close() throws IOException {
		input.close();
		currentByte = -1;
		numBitsRemaining = 0;
	}
	
}
