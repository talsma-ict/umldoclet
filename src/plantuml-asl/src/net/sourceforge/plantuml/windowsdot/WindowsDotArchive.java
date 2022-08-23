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
package net.sourceforge.plantuml.windowsdot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.plantuml.brotli.BrotliInputStream;
import net.sourceforge.plantuml.log.Logme;

public final class WindowsDotArchive {

	private static WindowsDotArchive singleton = null;

	private Boolean isThereArchive;
	private File exe;

	private WindowsDotArchive() {

	}

	public final synchronized static WindowsDotArchive getInstance() {
		if (singleton == null) {
			singleton = new WindowsDotArchive();
		}
		return singleton;
	}

	final static public String readString(InputStream is) throws IOException {
		int len = readByte(is);
		final StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append((char) readByte(is));

		}
		return sb.toString();
	}

	final static public int readNumber(InputStream is) throws IOException {
		int result = readByte(is);
		result = result * 256 + readByte(is);
		result = result * 256 + readByte(is);
		return result;
	}

	private static int readByte(InputStream is) throws IOException {
		return is.read();
	}

	private static void extract(File dir) throws IOException {
		final InputStream raw = WindowsDotArchive.class.getResourceAsStream("graphviz.dat");
		try (final BrotliInputStream is = new BrotliInputStream(raw)) {
			while (true) {
				final String name = readString(is);
				if (name.length() == 0)
					break;
				final int size = readNumber(is);
				try (final OutputStream fos = new BufferedOutputStream(new FileOutputStream(new File(dir, name)))) {
					for (int i = 0; i < size; i++) {
						fos.write(is.read());
					}
				}
			}
		}
	}

	public synchronized boolean isThereArchive() {
		if (isThereArchive == null)
			try (InputStream raw = WindowsDotArchive.class.getResourceAsStream("graphviz.dat")) {
				isThereArchive = raw != null;
			} catch (Exception e) {
				isThereArchive = false;
			}
		return isThereArchive;
	}

	public synchronized File getWindowsExeLite() {
		if (isThereArchive() == false) {
			return null;
		}
		if (exe == null)
			try {
				final File tmp = new File(System.getProperty("java.io.tmpdir"), "_graphviz");
				tmp.mkdirs();
				extract(tmp);
				exe = new File(tmp, "dot.exe");
			} catch (IOException e) {
				Logme.error(e);
			}
		return exe;
	}

}
