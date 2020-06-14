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
package net.sourceforge.plantuml.code;

import java.io.IOException;

public class TranscoderImpl implements Transcoder {

	private final Compression compression;
	private final URLEncoder urlEncoder;
	private final StringCompressor stringCompressor;

//	private TranscoderImpl() {
//		this(new AsciiEncoder(), new StringCompressorNone(), new CompressionHuffman());
//	}
//
//	private TranscoderImpl(Compression compression) {
//		this(new AsciiEncoder(), new StringCompressorNone(), compression);
//	}
//
//	private TranscoderImpl(URLEncoder urlEncoder, Compression compression) {
//		this(urlEncoder, new ArobaseStringCompressor(), compression);
//	}

	public TranscoderImpl(URLEncoder urlEncoder, StringCompressor stringCompressor, Compression compression) {
		this.compression = compression;
		this.urlEncoder = urlEncoder;
		this.stringCompressor = stringCompressor;
	}

	public String encode(String text) throws IOException {

		final String stringAnnoted = stringCompressor.compress(text);

		final byte[] data = stringAnnoted.getBytes("UTF-8");
		final byte[] compressedData = compression.compress(data);

		return urlEncoder.encode(compressedData);
	}

	public String decode(String code) throws IOException {
		final byte compressedData[] = urlEncoder.decode(code);
		final byte data[] = compression.decompress(compressedData);

		return stringCompressor.decompress(new String(data, "UTF-8"));
	}

}
