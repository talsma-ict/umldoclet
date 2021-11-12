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
package net.sourceforge.plantuml.code;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

public class TranscoderImpl implements Transcoder {

	static enum Format {
		UTF8, UPF9;
	}

	private final Compression compression;
	private final URLEncoder urlEncoder;
	private final StringCompressor stringCompressor;
	private final Format format;

	private TranscoderImpl(URLEncoder urlEncoder, StringCompressor stringCompressor, Compression compression,
			Format format) {
		this.compression = compression;
		this.urlEncoder = urlEncoder;
		this.stringCompressor = stringCompressor;
		this.format = format;
	}

	public static Transcoder utf8(URLEncoder urlEncoder, StringCompressor stringCompressor, Compression compression) {
		return new TranscoderImpl(urlEncoder, stringCompressor, compression, Format.UTF8);
	}

	public static Transcoder upf9(URLEncoder urlEncoder, StringCompressor stringCompressor, Compression compression) {
		return new TranscoderImpl(urlEncoder, stringCompressor, compression, Format.UPF9);
	}

	public String encode(String text) throws IOException {
		final String stringAnnoted = stringCompressor.compress(text);
		final byte[] data;
		if (format == Format.UTF8)
			data = stringAnnoted.getBytes(UTF_8);
		else
			data = Upf9Encoder.getBytes(stringAnnoted);

		final byte[] compressedData = compression.compress(data);

		return urlEncoder.encode(compressedData);
	}

	public String decode(String code) throws NoPlantumlCompressionException {
		try {
			final byte compressedData[] = urlEncoder.decode(code);
			final ByteArray data = compression.decompress(compressedData);
			final String string = format == Format.UTF8 ? data.toUFT8String() : data.toUPF9String();
			return stringCompressor.decompress(string);
		} catch (Exception e) {
			// System.err.println("Cannot decode string");
			throw new NoPlantumlCompressionException(e);
		}
	}

}
