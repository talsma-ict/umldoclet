/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2024, Arnaud Roques
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
package net.sourceforge.plantuml.elk.proxy.graph.util;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;

public class ElkGraphUtil {
    // ::remove folder when __HAXE__

	public static ElkLabel createLabel(ElkEdge edge) {
		return new ElkLabel(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createLabel", edge.obj));
	}

	public static ElkLabel createLabel(ElkNode node) {
		return new ElkLabel(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createLabel", node.obj));
	}

	public static ElkNode createNode(ElkNode root) {
		return new ElkNode(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createNode", root.obj));
	}

	public static ElkEdge createSimpleEdge(ElkNode node1, ElkNode node2) {
		return new ElkEdge(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createSimpleEdge", node1.obj,
				node2.obj));
	}

	public static ElkNode createGraph() {
		return new ElkNode(Reflect.callStatic("org.eclipse.elk.graph.util.ElkGraphUtil", "createGraph"));
	}

}
