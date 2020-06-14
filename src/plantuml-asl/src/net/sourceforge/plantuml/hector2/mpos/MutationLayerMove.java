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
package net.sourceforge.plantuml.hector2.mpos;

import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.hector2.layering.Layer;

public class MutationLayerMove implements MutationLayer {

	private final Layer layer;
	private final IEntity entity;
	private final int newLongitude;

	public MutationLayerMove(Layer layer, IEntity entity, int newLongitude) {
		this.layer = layer;
		this.entity = entity;
		this.newLongitude = newLongitude;
	}

	public Layer mute() {
		final Layer result = layer.duplicate();
		result.put(entity, newLongitude);
		return result;
	}

	public Layer getOriginal() {
		return layer;
	}

	@Override
	public String toString() {
		return "{" + layer.getId() + "} " + entity + " moveto " + newLongitude;
	}
}
