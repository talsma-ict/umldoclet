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
package net.sourceforge.plantuml.picoweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

public class ReceivedHTTPRequest {

	private static final String CONTENT_LENGTH_HEADER = "content-length: ";

	private String method;

	private String path;

	private byte[] body;

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public byte[] getBody() {
		return body;
	}

	public static ReceivedHTTPRequest fromStream(InputStream in) throws IOException {
		final ReceivedHTTPRequest request = new ReceivedHTTPRequest();

		final String requestLine = readLine(in);

		final StringTokenizer tokenizer = new StringTokenizer(requestLine);
		if (tokenizer.countTokens() != 3) {
			throw new BadRequest400("Bad request line");
		}

		request.method = tokenizer.nextToken().toUpperCase();
		request.path = tokenizer.nextToken();

		// Headers
		int contentLength = 0;

		while (true) {
			String line = readLine(in);
			if (line.isEmpty()) {
				break;
			} else if (line.toLowerCase().startsWith(CONTENT_LENGTH_HEADER)) {
				contentLength = parseContentLengthHeader(line);
			}
		}

		request.body = readBody(in, contentLength);
		return request;
	}

	private static int parseContentLengthHeader(String line) throws IOException {
		int contentLength;

		try {
			contentLength = Integer.parseInt(line.substring(CONTENT_LENGTH_HEADER.length()).trim());
		} catch (NumberFormatException e) {
			throw new BadRequest400("Invalid content length");
		}

		if (contentLength < 0) {
			throw new BadRequest400("Negative content length");
		}

		return contentLength;
	}

	private static byte[] readBody(InputStream in, int contentLength) throws IOException {
		if (contentLength == 0) {
			return new byte[0];
		}

		final byte[] body = new byte[contentLength];
		int n = 0;
		int offset = 0;

		// java.io.InputStream.readNBytes() can replace this from Java 9
		while (n < contentLength) {
			int count = in.read(body, offset + n, contentLength - n);
			if (count < 0) {
				throw new BadRequest400("Body too short");
			}
			n += count;
		}
		return body;
	}

	private static String readLine(InputStream in) throws IOException {
		final StringBuilder builder = new StringBuilder();

		while (true) {
			int c = in.read();
			if (c == -1 || c == '\n') {
				break;
			}
			builder.append((char) c);
		}

		if (builder.length() > 0 && builder.charAt(builder.length() - 1) == '\r') {
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}
}
