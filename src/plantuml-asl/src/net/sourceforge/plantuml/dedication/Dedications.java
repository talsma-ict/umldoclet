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
package net.sourceforge.plantuml.dedication;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.SignatureUtils;

public class Dedications {

	private static final Map<String, Dedication> normal = new HashMap<String, Dedication>();
	private static final Map<String, Dedication> crypted = new HashMap<String, Dedication>();

	static {
		addNormal("Write your own dedication!", "dedication");
		addNormal("linux_china", "linux_china");
		addNormal("ARKBAN", "arkban");
		addNormal("Boundaries allow discipline to create true strength", "boundaries");
		addCrypted("0", "pOhci6rKgPXw32AeYXhOpSY0suoauHq5VUSwFqHLHsLYgSO6WaJ7BW5vtHBAoU6ePbcW7d8Flx99MWjPSKQTDm00");
		addCrypted("1", "LTxN3hdnhSJ515qcA7IQ841axt4GXfUd3n2wgNirYCdLnyX2360Gv1OEOnJ1-gwFzRW5B3HAqLBkR6Ge0WW_Z000");
		addCrypted("2", "lZqLduj4j1yRqSfAvkhbqVpqK8diklatiFeenDUXSdna9bKYQTzdS264YfUBScUVDYCp2Vcq04updoN98RwxE000");
	}

	private static void addNormal(String sentence, String name) {
		normal.put(keepLetter(sentence), new Dedication(name));
	}

	private static void addCrypted(String name, String contentKey) {
		crypted.put(contentKey, new Dedication(name));
	}

	private Dedications() {
	}

	public static Dedication get(String line) {
		final String keepLetter = keepLetter(line);
		final Dedication result = normal.get(keepLetter);
		if (result != null) {
			return result;
		}
		for (Map.Entry<String, Dedication> ent : crypted.entrySet()) {
			final Dedication dedication = ent.getValue();
			InputStream is = null;
			try {
				is = dedication.getInputStream(keepLetter);
				final String signature = SignatureUtils.getSignatureSha512(is);
				if (signature.equals(ent.getKey())) {
					return dedication;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return null;
	}

	public static String keepLetter(String s) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
