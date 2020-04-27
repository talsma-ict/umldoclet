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
package net.sourceforge.plantuml.preproc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringLocated;
import net.sourceforge.plantuml.utils.StartUtils;

public class StartDiagramExtractReader implements ReadLine {

	private final ReadLine raw;
	private boolean finished = false;

	public static StartDiagramExtractReader build(FileWithSuffix f2, StringLocated s, String charset) {
		return new StartDiagramExtractReader(getReadLine(f2, s, charset), f2.getSuffix());
	}

	public static StartDiagramExtractReader build(URL url, StringLocated s, String uid, String charset) {
		return new StartDiagramExtractReader(getReadLine(url, s, charset), uid);
	}

	public static StartDiagramExtractReader build(InputStream is, StringLocated s, String desc) {
		return new StartDiagramExtractReader(getReadLine(is, s, desc), null);
	}

	private StartDiagramExtractReader(ReadLine raw, String suf) {
		int bloc = 0;
		String uid = null;
		if (suf != null && suf.matches("\\d+")) {
			bloc = Integer.parseInt(suf);
		} else {
			uid = suf;
		}
		if (bloc < 0) {
			bloc = 0;
		}
		this.raw = raw;
		StringLocated s = null;
		try {
			while ((s = raw.readLine()) != null) {
				if (StartUtils.isArobaseStartDiagram(s.getString()) && checkUid(uid, s)) {
					if (bloc == 0) {
						return;
					}
					bloc--;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.error("Error " + e);
		}
		finished = true;
	}

	private boolean checkUid(String uid, StringLocated s) {
		if (uid == null) {
			return true;
		}
		if (s.toString().matches(".*id=" + uid + "\\W.*")) {
			return true;
		}
		return false;
	}

	private static ReadLine getReadLine(FileWithSuffix f2, StringLocated s, String charset) {
		try {
			final Reader tmp1 = f2.getReader(charset);
			if (tmp1 == null) {
				return new ReadLineSimple(s, "Cannot open " + f2.getDescription());
			}
			return new UncommentReadLine(ReadLineReader.create(tmp1, f2.getDescription()));
		} catch (IOException e) {
			return new ReadLineSimple(s, e.toString());
		}
	}

	private static ReadLine getReadLine(InputStream is, StringLocated s, String description) {
		return new UncommentReadLine(ReadLineReader.create(new InputStreamReader(is), description));
	}

	private static ReadLine getReadLine(URL url, StringLocated s, String charset) {
		try {
			if (charset == null) {
				Log.info("Using default charset");
				return new UncommentReadLine(ReadLineReader.create(new InputStreamReader(url.openStream()),
						url.toString()));
			}
			Log.info("Using charset " + charset);
			return new UncommentReadLine(ReadLineReader.create(new InputStreamReader(url.openStream(), charset),
					url.toString()));
		} catch (IOException e) {
			return new ReadLineSimple(s, e.toString());
		}
	}

	static public boolean containsStartDiagram(FileWithSuffix f2, StringLocated s, String charset) throws IOException {
		final ReadLine r = getReadLine(f2, s, charset);
		return containsStartDiagram(r);
	}

	static public boolean containsStartDiagram(URL url, StringLocated s, String charset) throws IOException {
		final ReadLine r = getReadLine(url, s, charset);
		return containsStartDiagram(r);
	}

	static public boolean containsStartDiagram(InputStream is, StringLocated s, String description) throws IOException {
		final ReadLine r = getReadLine(is, s, description);
		return containsStartDiagram(r);
	}

	private static boolean containsStartDiagram(final ReadLine r) throws IOException {
		try {
			StringLocated s = null;
			while ((s = r.readLine()) != null) {
				if (StartUtils.isArobaseStartDiagram(s.getString())) {
					return true;
				}
			}
		} finally {
			if (r != null) {
				r.close();
			}
		}
		return false;
	}

	public StringLocated readLine() throws IOException {
		if (finished) {
			return null;
		}
		final StringLocated result = raw.readLine();
		if (result != null && StartUtils.isArobaseEndDiagram(result.getString())) {
			finished = true;
			return null;
		}
		return result;
	}

	public void close() throws IOException {
		raw.close();
	}

}
