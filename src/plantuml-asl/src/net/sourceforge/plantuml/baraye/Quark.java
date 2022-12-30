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
package net.sourceforge.plantuml.baraye;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Ident;

public class Quark extends Ident implements Code {

	private final Plasma plasma;
	private /* final */ Quark parent;
	// private final List<String> parts;
	private Object data;

	Quark(Plasma plasma, Quark parent, List<String> parts) {
		super(new ArrayList<String>(parts));
		this.plasma = plasma;
		this.parent = parent;
		if (parent == null) {
			if (parts.size() != 0)
				throw new IllegalStateException();
		} else {
			if (parent.parts.equals(parts.subList(0, parts.size() - 1)) == false)
				throw new IllegalStateException();

		}
		// this.parts = new ArrayList<String>(parts);
	}

	public Quark getParent() {
		return parent;
//		if (parts.size() == 0)
//			return null;
//		return plasma.ensurePresent(parts.subList(0, parts.size() - 1));
	}

	@Override
	public String toString() {
		// return parts.toString() + "(parent=" + parent + ")";
		return parts.toString();
	}

	public List<String> getSignature() {
		return new ArrayList<>(parts);
	}

	public boolean containsLarge(Quark other) {
		return other.parts.size() > this.parts.size() && other.parts.subList(0, this.parts.size()).equals(this.parts);
	}

//	@Override
//	public boolean equals(Object obj) {
//		final Quark other = (Quark) obj;
//		if (this.plasma != other.plasma)
//			throw new IllegalArgumentException();
//		return this.parts.equals(other.parts);
//	}
//
//	@Override
//	public int hashCode() {
//		return parts.hashCode();
//	}

	public boolean startsWith(Quark other) {
		if (other.parts.size() > this.parts.size())
			return false;

		for (int i = 0; i < other.parts.size(); i++)
			if (other.parts.get(i).equals(this.parts.get(i)) == false)
				return false;

		return true;
	}

	public String toString(String sep) {
		if (sep == null)
			sep = ".";

		final StringBuilder sb = new StringBuilder();
		for (String s : parts) {
			if (sb.length() > 0)
				sb.append(sep);

			sb.append(s);
		}
		return sb.toString();
	}

	public String getName() {
		if (parts.size() == 0)
			return "";

		return parts.get(parts.size() - 1);
	}

	public boolean isRoot() {
		return parts.size() == 0;
	}

	public int getDepth() {
		return parts.size();
	}

//	public int size() {
//		return parts.size();
//	}

	public final Plasma getPlasma() {
		return plasma;
	}

	public final Object getData() {
		return data;
	}

	public final void setData(Object data) {
		this.data = data;
	}

	@Override
	public Ident eventuallyRemoveStartingAndEndingDoubleQuote(String format) {
		return this;
		// throw new UnsupportedOperationException();
	}

	public Quark childIfExists(String name) {
		final List<String> sig = new ArrayList<>(getSignature());
		sig.add(name);
		return plasma.getIfExists(sig);
	}

	public Quark parse(Quark path) {
		final List<String> sig = new ArrayList<>(getSignature());
		sig.addAll(path.getSignature());
		return plasma.ensurePresent(sig);
	}

	public Quark child(String name) {
		return plasma.parse(this, name);
	}

	public int countChildren() {
		return plasma.countChildren(this);
	}

	public List<Quark> getChildren() {
		return plasma.getChildren(this);
	}

	public void moveTo(Quark dest) {
		plasma.moveAllTo(this, dest);
	}

	public void internalMove(Quark src, Quark dest) {
		System.err.print("Intermal move from " + this + " to ");
		if (src.getDepth() + 1 != dest.getDepth())
			throw new UnsupportedOperationException("to be finished");
		final List<String> previous = this.getSignature();
		parts.clear();
		parts.addAll(dest.getSignature());
		parts.addAll(previous.subList(src.getDepth(), previous.size()));
		this.parent = plasma.ensurePresent(parts.subList(0, parts.size() - 1));
		System.err.println(toString());
	}

}
