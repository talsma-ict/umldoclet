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

import java.io.ByteArrayOutputStream;

/* 
 * Simple DEFLATE decompressor
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/simple-deflate-decompressor
 * https://github.com/nayuki/Simple-DEFLATE-decompressor
 */

import java.io.Closeable;
import java.io.IOException;

public class OutputStreamProtected implements Closeable {
    // ::remove folder when __HAXE__

	public static final int MAX_OUTPUT_SIZE = 1 * 1024 * 1024;

	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private int counter = 0;

	public void close() throws IOException {
		baos.close();
	}

	public byte[] toByteArray() {
		return baos.toByteArray();
	}

	public void write(int b) throws IOException {
		this.counter++;
		baos.write(b);
		if (counter > MAX_OUTPUT_SIZE) {
			throw new IOException("Too big");
		}

	}

}
