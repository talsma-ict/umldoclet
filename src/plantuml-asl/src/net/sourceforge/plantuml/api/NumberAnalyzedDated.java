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
package net.sourceforge.plantuml.api;

import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.prefs.Preferences;

import net.sourceforge.plantuml.Log;

public class NumberAnalyzedDated extends NumberAnalyzed {

	private final AtomicLong created = new AtomicLong();
	private final AtomicLong modified = new AtomicLong();
	private String comment;

	private NumberAnalyzedDated(String name, long nb, long sum, long min, long max, long sumOfSquare, long sliddingSum,
			long created, long modified, String comment) {
		super(name, nb, sum, min, max, sumOfSquare, sliddingSum);
		this.created.set(created);
		this.modified.set(modified);
		this.comment = comment;
	}

	@Override
	public synchronized void reset() {
		super.reset();
		resetCreatedModifiedComment();
	}

	public NumberAnalyzedDated() {
		super();
		resetCreatedModifiedComment();
	}

	public NumberAnalyzedDated(String name) {
		super(name);
		resetCreatedModifiedComment();
	}

	private void resetCreatedModifiedComment() {
		final long now = System.currentTimeMillis();
		this.created.set(now);
		this.modified.set(now);
		this.comment = " ";
	};

	@Override
	public void addValue(long v) {
		super.addValue(v);
		this.modified.set(System.currentTimeMillis());
	}

	@Override
	public void add(NumberAnalyzed other) {
		super.add(other);
		this.modified.set(System.currentTimeMillis());
	}

	@Override
	protected String getSavedSupplementatyData() {
		return longToString(created.get()) + ";" + longToString(modified.get()) + ";" + comment;
	}

	public static NumberAnalyzedDated load(String name, Preferences prefs) {
		final String value = prefs.get(name + ".saved", "");
		if (value.length() == 0) {
			Log.info("Cannot load " + name);
			return null;
		}
		try {
			final StringTokenizer st = new StringTokenizer(value, ";");
			return new NumberAnalyzedDated(name, Long.parseLong(st.nextToken(), 36),
					Long.parseLong(st.nextToken(), 36), Long.parseLong(st.nextToken(), 36), Long.parseLong(
							st.nextToken(), 36), Long.parseLong(st.nextToken(), 36),
					Long.parseLong(st.nextToken(), 36), Long.parseLong(st.nextToken(), 36), Long.parseLong(
							st.nextToken(), 36), st.nextToken());
		} catch (Exception e) {
			e.printStackTrace();
			Log.info("Error reading " + value);
			return null;
		}
	}

	final public long getCreationTime() {
		return created.get();
	}

	final public long getModificationTime() {
		return modified.get();
	}

	final public synchronized String getComment() {
		return comment;
	}

	final public synchronized void setComment(String comment) {
		this.comment = comment;
	}

}
