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
package net.sourceforge.plantuml.anim;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.ugraphic.MinMax;

public class Animation {

	private final List<AffineTransformation> all;

	private Animation(List<AffineTransformation> all) {
		if (all.size() == 0) {
			throw new IllegalArgumentException();
		}
		this.all = all;
	}

	public static Animation singleton(AffineTransformation affineTransformation) {
		if (affineTransformation == null) {
			return null;
		}
		return new Animation(Collections.singletonList(affineTransformation));
	}

	public static Animation create(List<String> descriptions) {
		final List<AffineTransformation> all = new ArrayList<AffineTransformation>();
		for (String s : descriptions) {
			final AffineTransformation tmp = AffineTransformation.create(s);
			if (tmp != null) {
				all.add(tmp);
			}
		}
		return new Animation(all);
	}

	public Collection<AffineTransformation> getAll() {
		return Collections.unmodifiableCollection(all);
	}

	public void setDimension(Dimension2D dim) {
		for (AffineTransformation affineTransform : all) {
			affineTransform.setDimension(dim);
		}

	}

	public AffineTransformation getFirst() {
		return all.get(0);
	}

	public MinMax getMinMax(Dimension2D dim) {
		MinMax result = MinMax.getEmpty(false);
		for (AffineTransformation affineTransform : all) {
			final MinMax m = affineTransform.getMinMax(dim);
			result = result.addMinMax(m);
		}
		return result;
	}

}
