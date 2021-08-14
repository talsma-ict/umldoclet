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
package net.sourceforge.plantuml.nwdiag.next;

import java.util.Objects;

public class NBar implements Staged {

	private NBox parent;
	private NStage start;
	private NStage end;

	@Override
	public String toString() {
		return start + "->" + end;
	}

	public final NBox getParent() {
		return parent;
	}

	public final void setParent(NBox parent) {
		this.parent = parent;
	}

	@Override
	public final NStage getStart() {
		return start;
	}

	@Override
	public final NStage getEnd() {
		return end;
	}

	public void addStage(NStage stage) {
		Objects.requireNonNull(stage);
		if (start == null && end == null) {
			this.start = stage;
			this.end = stage;
		} else {
			this.start = NStage.getMin(this.start, stage);
			this.end = NStage.getMax(this.end, stage);
		}
	}

	@Override
	public int getNWidth() {
		return 1;
	}

	@Override
	public boolean contains(NStage stage) {
		return stage.compareTo(start) >= 0 && stage.compareTo(end) <= 0;
	}

}
