/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package net.sourceforge.plantuml.hector2.layering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.hector2.MinMax;
import net.sourceforge.plantuml.hector2.mpos.MutationLayer;
import net.sourceforge.plantuml.hector2.mpos.MutationLayerMove;

public class Layer {

	private final int id;
	private final Map<IEntity, Integer> entities = new HashMap<IEntity, Integer>();

	public Layer(int id) {
		this.id = id;
	}

	public Layer duplicate() {
		final Layer result = new Layer(id);
		result.entities.putAll(this.entities);
		return result;
	}

	public List<MutationLayer> getPossibleMutations() {
		final List<MutationLayer> result = new ArrayList<MutationLayer>();
		for (Map.Entry<IEntity, Integer> ent : entities.entrySet()) {
			final IEntity entity = ent.getKey();
			final int longitude = ent.getValue();
			if (isLongitudeFree(longitude + 2)) {
				result.add(new MutationLayerMove(this, entity, longitude + 2));
			}
			if (isLongitudeFree(longitude - 2)) {
				result.add(new MutationLayerMove(this, entity, longitude - 2));
			}
		}
		return Collections.unmodifiableList(result);
	}

	private boolean isLongitudeFree(int longitude) {
		return entities.values().contains(longitude) == false;
	}

	public void put(IEntity ent, int longitude) {
		if (entities.containsKey(ent) == false) {
			throw new IllegalArgumentException();
		}
		this.entities.put(ent, longitude);
	}

	public void add(IEntity ent) {
		final int pos = entities.size() * 2;
		this.entities.put(ent, pos);
	}

	public Collection<IEntity> entities() {
		return Collections.unmodifiableCollection(entities.keySet());
	}

	public int getLongitude(IEntity ent) {
		return entities.get(ent);
	}

	public MinMax getMinMaxLongitudes() {
		return MinMax.from(entities.values());
	}

	@Override
	public String toString() {
		return "layer " + id + " " + entities;
	}

	public final int getId() {
		return id;
	}

}
