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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.real.RealUtils;
import net.sourceforge.plantuml.sequencediagram.Englober;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.ParticipantEnglober;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class Englobers {

	private final List<Englober> englobers = new ArrayList<Englober>();

	public Englobers(TileArguments tileArguments) {
		Englober pending = null;
		for (Participant p : tileArguments.getLivingSpaces().participants()) {
			final ParticipantEnglober englober = tileArguments.getLivingSpaces().get(p).getEnglober();
			if (englober == null) {
				pending = null;
				continue;
			}
			assert englober != null;
			if (pending != null && englober == pending.getParticipantEnglober()) {
				pending.add(p);
				continue;
			}
			pending = Englober.createTeoz(englober, p, tileArguments, tileArguments.getSkinParam()
					.getCurrentStyleBuilder());
			englobers.add(pending);
		}
	}

	public int size() {
		return englobers.size();
	}

	public double getOffsetForEnglobers(StringBounder stringBounder) {
		double result = 0;
		for (Englober englober : englobers) {
			final double height = englober.getPreferredHeight();
			if (height > result) {
				result = height;
			}
		}
		return result;
	}

	public void addConstraints(StringBounder stringBounder) {
		Englober last = null;
		for (Englober current : englobers) {
			current.addInternalConstraints();
			if (last != null) {
				last.addConstraintAfter(current);
			}
			last = current;
		}
	}

	public void drawEnglobers(UGraphic ug, double height, Context2D context) {
		for (Englober englober : englobers) {
			englober.drawEnglober(ug, height, context);
		}
	}

	public Real getMinX(StringBounder stringBounder) {
		if (size() == 0) {
			throw new IllegalStateException();
		}
		final List<Real> all = new ArrayList<Real>();
		for (Englober englober : englobers) {
			all.add(englober.getMinX(stringBounder));
		}
		return RealUtils.min(all);
	}

	public Real getMaxX(StringBounder stringBounder) {
		if (size() == 0) {
			throw new IllegalStateException();
		}
		final List<Real> all = new ArrayList<Real>();
		for (Englober englober : englobers) {
			all.add(englober.getMaxX(stringBounder));
		}
		return RealUtils.max(all);
	}

}
