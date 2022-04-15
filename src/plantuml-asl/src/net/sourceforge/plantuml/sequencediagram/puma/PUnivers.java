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
package net.sourceforge.plantuml.sequencediagram.puma;

import java.util.ArrayList;
import java.util.Collection;

public class PUnivers {

	private final Collection<PSegment> all = new ArrayList<>();
	private final Collection<FixedLink> links = new ArrayList<>();

	public PSegment createPSegment(double minsize) {
		final PSegment result = new PSegment(minsize);
		all.add(result);
		return result;
	}

	public void addFixedLink(PSegment segment1, double position1, PSegment segment2, double position2) {
		final FixedLink link = new FixedLink(new SegmentPosition(segment1, position1), new SegmentPosition(segment2,
				position2));
		links.add(link);

	}

	public void solve() {
		boolean changed = false;
		do {
			changed = false;
			for (FixedLink link : links) {
				if (link.pushIfNeed()) {
					changed = true;
				}
			}
		} while (changed);

	}
}
