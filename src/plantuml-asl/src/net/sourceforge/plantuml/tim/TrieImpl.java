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
package net.sourceforge.plantuml.tim;

import java.util.HashMap;
import java.util.Map;

public class TrieImpl implements Trie {

	private final Map<Character, TrieImpl> brothers = new HashMap<Character, TrieImpl>();

	public void add(String s) {
		if (s.indexOf('\0') != -1) {
			throw new IllegalArgumentException();
		}
		addInternal(this, s + "\0");
	}

	private static void addInternal(TrieImpl current, String s) {
		if (s.length() == 0) {
			throw new UnsupportedOperationException();
		}
		while (s.length() > 0) {
			final Character added = s.charAt(0);
			final TrieImpl child = current.getOrCreate(added);
			s = s.substring(1);
			current = child;
		}
	}

	public boolean remove(String s) {
		return removeInternal(this, s + "\0");
	}

	private static boolean removeInternal(TrieImpl current, String s) {
		if (s.length() <= 1) {
			throw new UnsupportedOperationException();
		}
		while (s.length() > 0) {
			final Character first = s.charAt(0);
			final TrieImpl child = current.brothers.get(first);
			if (child == null) {
				return false;
			}
			s = s.substring(1);
			if (s.length() == 1) {
				assert s.charAt(0) == '\0';
				return child.brothers.remove('\0') != null;
			}
			current = child;
		}
		throw new IllegalStateException();
	}

	private TrieImpl getOrCreate(Character added) {
		TrieImpl result = brothers.get(added);
		if (result == null) {
			result = new TrieImpl();
			brothers.put(added, result);
		}
		return result;
	}

	public String getLonguestMatchStartingIn(String s) {
		return getLonguestMatchStartingIn(this, s);
	}

	private static String getLonguestMatchStartingIn(TrieImpl current, String s) {
		final StringBuilder result = new StringBuilder();
		while (current != null) {
			if (s.length() == 0) {
				if (current.brothers.containsKey('\0')) {
					return result.toString();
				} else {
					return "";
				}
			}
			final TrieImpl child = current.brothers.get(s.charAt(0));
			if (child == null || child.brothers.size() == 0) {
				if (current.brothers.containsKey('\0')) {
					return result.toString();
				} else {
					return "";
				}
			}
			result.append(s.charAt(0));
			current = child;
			s = s.substring(1);
		}
		return "";

	}

}
