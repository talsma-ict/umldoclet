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
package net.sourceforge.plantuml.code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CompressionZip implements Compression {
	// ::remove file when __CORE__

	public byte[] compress(byte[] in) {
		throw new UnsupportedOperationException();
	}

	public ByteArray decompress(byte[] input) throws NoPlantumlCompressionException {
		try {
			try (final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(input))) {
				final ZipEntry ent = zis.getNextEntry();
				final String name = ent.getName();
				final byte[] buffer = new byte[10_000];

				try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					int len;
					while ((len = zis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
						if (baos.size() > 200_000)
							throw new NoPlantumlCompressionException("Zip error");
					}
					return ByteArray.from(baos.toByteArray());
				}
			}
		} catch (IOException e) {
			throw new NoPlantumlCompressionException(e);
		}

	}

}
