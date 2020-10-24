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
package net.sourceforge.plantuml.picoweb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.NoPlantumlCompressionException;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;

public class PicoWebServer implements Runnable {

	private final Socket connect;

	public PicoWebServer(Socket c) {
		this.connect = c;
	}

	public static void main(String[] args) throws IOException {
		startServer(8080);
	}

	public static void startServer(final int port) throws IOException {
		final ServerSocket serverConnect = new ServerSocket(port);
		while (true) {
			final PicoWebServer myServer = new PicoWebServer(serverConnect.accept());
			final Thread thread = new Thread(myServer);
			thread.start();
		}
	}

	public void run() {
		BufferedReader in = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
			out = new BufferedOutputStream(connect.getOutputStream());

			final String first = in.readLine();
			if (first == null) {
				return;
			}

			final StringTokenizer parse = new StringTokenizer(first);
			final String method = parse.nextToken().toUpperCase();

			if (method.equals("GET")) {
				final String path = parse.nextToken();
				if (path.startsWith("/plantuml/png/")) {
					sendDiagram(out, path, "image/png", FileFormat.PNG);
					return;
				}
				if (path.startsWith("/plantuml/svg/")) {
					sendDiagram(out, path, "image/svg+xml", FileFormat.SVG);
					return;
				}
			}
			write(out, "HTTP/1.1 302 Found");
			write(out, "Location: /plantuml/png/oqbDJyrBuGh8ISmh2VNrKGZ8JCuFJqqAJYqgIotY0aefG5G00000");
			write(out, "");
			out.flush();

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				connect.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void sendDiagram(BufferedOutputStream out, String path, final String mime, final FileFormat format)
			throws NoPlantumlCompressionException, IOException {
		final int x = path.lastIndexOf('/');
		final String compressed = path.substring(x + 1);
		final Transcoder transcoder = TranscoderUtil.getDefaultTranscoderProtected();
		final String source = transcoder.decode(compressed);
		final byte[] fileData = getData(source, format);
		write(out, "HTTP/1.1 200 OK");
		write(out, "Cache-Control: no-cache");
		write(out, "Server: PlantUML PicoWebServer");
		write(out, "Date: " + new Date());
		write(out, "Content-type: " + mime);
		write(out, "Content-length: " + fileData.length);
		write(out, "");
		out.flush();
		out.write(fileData);
		out.flush();
	}

	private byte[] getData(String source, FileFormat format) throws IOException {
		final SourceStringReader ssr = new SourceStringReader(source);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		ssr.outputImage(os, new FileFormatOption(format));
		os.close();
		return os.toByteArray();
	}

	private void write(OutputStream os, String s) throws IOException {
		s = s + "\r\n";
		os.write(s.getBytes("UTF-8"));
	}

}
