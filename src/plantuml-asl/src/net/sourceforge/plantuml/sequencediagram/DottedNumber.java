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
package net.sourceforge.plantuml.sequencediagram;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DottedNumber {

	private final List<Integer> nums;
	private final List<String> separators;

	private DottedNumber(List<Integer> nums, List<String> separators) {
		this.nums = nums;
		this.separators = separators;
	}

	public static DottedNumber create(String value) {
		final Pattern p = Pattern.compile("(\\d+)|(\\D+)");
		final Matcher m = p.matcher(value);
		final List<Integer> nums = new ArrayList<Integer>();
		final List<String> separators = new ArrayList<String>();
		while (m.find()) {
			final String part = m.group();
			if (isDigit(part.charAt(0))) {
				nums.add(Integer.parseInt(part));
			} else {
				separators.add(part);
			}
		}
		return new DottedNumber(nums, separators);
	}

	private static boolean isDigit(final char c) {
		return c >= '0' && c <= '9';
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nums.size(); i++) {
			sb.append(nums.get(i));
			if (i < separators.size()) {
				sb.append(separators.get(i));
			}
		}
		return sb.toString();
	}

	public void incrementMinor(int step) {
		final int last = nums.size() - 1;
		final int newValue = nums.get(last) + step;
		nums.set(last, newValue);
	}

	public void incrementIntermediate() {
		final int intermediate = nums.size() == 1 ? 0 : nums.size() - 2;
		incrementIntermediate(intermediate);
	}

	public void incrementIntermediate(int position) {
		final int intermediate = position;
		final int newValue = nums.get(intermediate) + 1;
		for (int i = intermediate + 1; i < nums.size(); i++) {
			nums.set(i, 1);
		}
		nums.set(intermediate, newValue);
	}

	public String format(DecimalFormat format) {
		if (nums.size() == 1 && separators.size() == 0) {
			return format.format(nums.get(0));
		}
		return "<b>" + toString() + "</b>";
	}

}
