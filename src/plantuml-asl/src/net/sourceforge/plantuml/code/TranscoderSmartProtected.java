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
package net.sourceforge.plantuml.code;

import java.io.IOException;

public class TranscoderSmartProtected implements Transcoder {

	// Legacy encoder
	private final Transcoder oldOne = TranscoderImpl.utf8(new AsciiEncoder(), new ArobaseStringCompressor(),
			new CompressionHuffman());
	private final Transcoder zlib = TranscoderImpl.utf8(new AsciiEncoder(), new ArobaseStringCompressor(),
			new CompressionZlib());
	private final Transcoder hexOnly = TranscoderImpl.utf8(new AsciiEncoderHex(), new ArobaseStringCompressor(),
			new CompressionNone());
	private final Transcoder zip = TranscoderImpl.utf8(new AsciiEncoder(), new ArobaseStringCompressor(),
			new CompressionZip());

	public String decode(String code) throws NoPlantumlCompressionException {
		// Work in progress
		// See https://github.com/plantuml/plantuml/issues/117

		if (code.startsWith("~0"))
			return decodeZlib(code.substring(2));

		if (code.startsWith("~1"))
			return decodeHuffman(code.substring(2));

		if (code.startsWith("~h"))
			return hexOnly.decode(code.substring(2));

		if (code.startsWith("~zip~"))
			return zip.decode(code.substring(5));

		return decodeZlib(code);
	}

	private String decodeZlib(String code) {
		try {
			return zlib.decode(code);
		} catch (Exception ex) {
			return textProtectedDeflate2(code);
		}
	}

	private String decodeHuffman(String code) {
		try {
			return oldOne.decode(code);
		} catch (Exception ex) {
			return textProtectedHuffman(code);
		}
	}

	private String textProtectedHuffman(String code) {
		final StringBuilder result = new StringBuilder();
		appendLine(result, "@startuml");
		appendLine(result, "legend");
		appendLine(result, "The plugin you are using seems to generated a bad URL.");
		appendLine(result, "This URL does not look like HUFFMAN data.");
		appendLine(result, "");
		appendLine(result, "See https://plantuml.com/pte");
		appendLine(result, "");
		appendLine(result, "You may contact the PlantUML team at plantuml@gmail.com");
		appendLine(result,
				"But you should also probably contact the plugin authors you are currently using and send them this image");
		appendLine(result, "");
		appendLine(result, "For the record, here is your data:");
		appendLine(result, "");
		appendURL(result, code);
		appendLine(result, "endlegend");
		appendLine(result, "@enduml");

		return result.toString();
	}

	private String textProtectedDeflate2(String code) {
		final StringBuilder result = new StringBuilder();
		final String codeshort = code.length() > 30 ? code.substring(0, 30) + "..." : code;
		appendLine(result, "@startuml");
		appendLine(result, "legend");
		appendLine(result, "The plugin you are using seems to generated a bad URL.");
		appendLine(result, "This URL does not look like DEFLATE data.");
		appendLine(result, "It looks like your plugin is using HUFFMAN encoding.");
		appendLine(result, "");
		appendLine(result,
				"This means you have now to add an header ~1 to your data. For example, you have to change:");
		appendLine(result, "http://www.plantuml.com/plantuml/png/" + codeshort);
		appendLine(result, "to");
		appendLine(result, "http://www.plantuml.com/plantuml/png/~1" + codeshort);
		appendLine(result, "");
		appendLine(result, "It will work this way");
		appendLine(result, "You may contact the PlantUML team at plantuml@gmail.com");
		appendLine(result,
				"But you should also probably contact the plugin authors you are currently using and send them this image");
		appendLine(result, "");
		appendLine(result, "For the record, here is your data:");
		appendLine(result, "");
		appendURL(result, code);
		appendLine(result, "endlegend");
		appendLine(result, "@enduml");

		return result.toString();
	}

	private void appendURL(StringBuilder result, String url) {
		while (url.length() > 80) {
			appendLine(result, url.substring(0, 80));
			url = url.substring(80);
		}
		if (url.length() > 0) {
			appendLine(result, url);
		}
	}

	private void appendLine(StringBuilder sb, String s) {
		sb.append(s);
		sb.append("\n");
	}

	public String encode(String text) throws IOException {
		// Right now, we still use the legacy encoding.
		// This will be changed in the incoming months
		return zlib.encode(text);
	}
}
