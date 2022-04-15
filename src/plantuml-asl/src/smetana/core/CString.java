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

package smetana.core;

import java.util.ArrayList;
import java.util.List;

import h.ST_refstr_t;

public class CString extends UnsupportedC implements __ptr__ {

	private static int UID = 100;

	private final Throwable creation = new Throwable();
	private final List<Character> data2;
	private final int currentStart;

	private final int uid;

	public boolean isSameThan(CString other) {
		if (this.data2 != other.data2) {
			throw new UnsupportedOperationException();
		}
		return this.currentStart == other.currentStart;

	}

	public CString(String string) {
		this(null, 0);
		for (int i = 0; i < string.length(); i++) {
			data2.add(string.charAt(i));
		}
		data2.add('\0');
	}

	public CString duplicate() {
		// return this;

		return new CString(new ArrayList<>(this.data2), currentStart);

		// final CString result = new CString(this.data.size());
		// for (int i = 0; i < result.data.size(); i++) {
		// result.data.set(i, this.data.get(i));
		// }
		// return result;
	}

	public CString strdup() {
		return duplicate();
	}

	public static CString gmalloc(int nbytes) {
		return new CString(nbytes);
	}

	public CString(int size) {
		this(null, 0);
		for (int i = 0; i < size; i++) {
			data2.add('\0');
		}
	}

	private CString(List<Character> data2, int currentStart) {
		if (data2 == null)
			this.data2 = new ArrayList<>();
		else
			this.data2 = data2;
		this.currentStart = currentStart;
		this.uid = UID;
		UID += 2;
		creation.fillInStackTrace();
	}

	public __ptr__ getTheField(OFFSET bytes) {
		JUtils.LOG("CString::addVirtualBytes " + bytes);
		JUtils.LOG("AM " + this);
		throw new UnsupportedOperationException();
	}

	private ST_refstr_t parent;

	public ST_refstr_t getParent() {
		if (parent != null) {
			return parent;
		}
		throw new UnsupportedOperationException();
	}

	public void setParent(ST_refstr_t struct) {
//		if (parent != null && parent != struct) {
//			throw new IllegalStateException();
//		}
		if (struct == null) {
			throw new IllegalStateException();
		}
		this.parent = struct;
	}

	public CString plus_(int pointerMove) {
		return new CString(data2, currentStart + pointerMove);
	}

	public int comparePointer(__ptr__ other) {
		final CString this2 = (CString) other;
		if (this.data2 != this2.data2) {
			throw new IllegalArgumentException();
		}
		return this.currentStart - this2.currentStart;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (Character c : getData()) {
			if (c == '\0') {
				sb.append("(0)");
			} else {
				sb.append(c);
			}
		}
		return "CString:" + sb;
	}

	private List<Character> getData() {
		return data2.subList(currentStart, data2.size());
	}

	public String getContent() {
		final StringBuilder sb = new StringBuilder();
		for (Character c : getData()) {
			if (c == '\0') {
				return sb.toString();
			} else {
				sb.append(c);
			}
		}
		throw new UnsupportedOperationException();
	}

	public char charAt(int i) {
		if (i >= getData().size()) {
			throw new UnsupportedOperationException();
			// return '\0';
		}
		return data2.get(currentStart + i);
		// when i<0
		// return data2.subList(currentStart, data2.size()).get(i);
	}

	public char setCharAt(int i, char c) {
		getData().set(i, c);
		return c;
	}

	public int length() {
		int len = 0;
		for (Character c : getData()) {
			if (c == '\0') {
				return len;
			}
			len++;
		}
		throw new IllegalStateException();
	}

	public int strcmp(CString other) {
		for (int i = 0; i < data2.size() - currentStart; i++) {
			final int diff = this.charAt(i) - other.charAt(i);
			if (this.charAt(i) == '\0' || diff != 0) {
				return diff;
			}
		}
		throw new IllegalStateException();
	}

	public int strcmp(CString other, int num) {
		for (int i = 0; i < data2.size() - currentStart && i < num; i++) {
			final int diff = this.charAt(i) - other.charAt(i);
			if (this.charAt(i) == '\0' || diff != 0) {
				return diff;
			}
		}
		return 0;
	}

	public void copyFrom(CString source, int nb) {
		for (int i = 0; i < source.length() + 1 && i < nb; i++) {
			setCharAt(i, source.charAt(i));
		}
	}

	public CString strchr(char c) {
		for (int i = currentStart; i < data2.size(); i++) {
			if (data2.get(i) == c) {
				return new CString(data2, i);
			}
		}
		return null;
	}

	public boolean isSame(CString other) {
		return getContent().equals(other.getContent());
	}

	public int getUid() {
		return uid;
	}

}
