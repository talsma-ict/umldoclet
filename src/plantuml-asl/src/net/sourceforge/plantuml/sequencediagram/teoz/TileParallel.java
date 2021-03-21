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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.real.RealUtils;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TileParallel extends CommonTile {

	public TileParallel(StringBounder stringBounder) {
		super(stringBounder);
	}

	private final List<Tile> tiles = new ArrayList<Tile>();

	@Override
	public void callbackY_internal(double y) {
		for (Tile tile : tiles) {
			tile.callbackY(y);
		}
	}

	public void add(Tile tile) {
		this.tiles.add(tile);
	}

	public void drawU(UGraphic ug) {
		final double yPointAll = getContactPointRelative();
		for (Tile tile : tiles) {
			final double yPoint = tile.getContactPointRelative();
			tile.drawU(ug.apply(UTranslate.dy(yPointAll - yPoint)));
		}
	}

	public double getContactPointRelative() {
		double result = 0;
		for (Tile tile : tiles) {
			result = Math.max(result, tile.getContactPointRelative());
		}
		return result;
	}

	public double getZZZ() {
		double result = 0;
		for (Tile tile : tiles) {
			result = Math.max(result, tile.getZZZ());
		}
		return result;
	}

	public double getPreferredHeight() {
		return getContactPointRelative() + getZZZ();
	}

	public void addConstraints() {
		for (Tile tile : tiles) {
			tile.addConstraints();
		}
	}

	public Real getMinX() {
		return RealUtils.min(new AbstractCollection<Real>() {
			public Iterator<Real> iterator() {
				return new Iterator<Real>() {
					private final Iterator<Tile> source = tiles.iterator();

					public boolean hasNext() {
						return source.hasNext();
					}

					public Real next() {
						return source.next().getMinX();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			public int size() {
				return tiles.size();
			}
		});
	}

	public Real getMaxX() {
		return RealUtils.max(new AbstractCollection<Real>() {
			public Iterator<Real> iterator() {
				return new Iterator<Real>() {
					private final Iterator<Tile> source = tiles.iterator();

					public boolean hasNext() {
						return source.hasNext();
					}

					public Real next() {
						return source.next().getMaxX();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			public int size() {
				return tiles.size();
			}
		});
	}

	public Event getEvent() {
		return null;
	}

	public boolean matchAnchorV1(String anchor) {
		for (Tile tile : tiles) {
			if (tile.matchAnchorV1(anchor)) {
				return true;
			}
		}
		return false;
	}

}
