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
package net.sourceforge.plantuml.geom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dijkstra {

	final private double basic[][];
	final private double dist[];
	final private int previous[];
	final private Set<Integer> q = new HashSet<Integer>();
	final private int size;

	public Dijkstra(int size) {
		this.size = size;
		this.basic = new double[size][size];
		this.dist = new double[size];
		this.previous = new int[size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.basic[i][j] = i == j ? 0 : Double.MAX_VALUE;
			}
		}
	}

	public void addLink(int n1, int n2, double d) {
// Log.println("Adding " + n1 + " " + n2 + " " + d);
		if (n1 == n2) {
			throw new IllegalArgumentException();
		}
		basic[n1][n2] = d;
		basic[n2][n1] = d;

	}

	private void init() {
		for (int i = 0; i < size; i++) {
			this.dist[i] = Double.MAX_VALUE;
			this.previous[i] = -1;
			this.q.add(i);
		}
		this.dist[0] = 0;
	}

	private void computePrevious() {
		init();
		while (q.size() > 0) {
			final int u = smallest();
			if (dist[u] == Double.MAX_VALUE) {
				return;
			}
			q.remove(u);
			for (int v = 0; v < size; v++) {
				if (basic[u][v] == Double.MAX_VALUE) {
					continue;
				}
				final double alt = dist[u] + basic[u][v];
				if (alt < dist[v]) {
					dist[v] = alt;
					previous[v] = u;
				}
			}
		}
	}

	public List<Integer> getBestPath() {
		final List<Integer> result = new ArrayList<Integer>();
		computePrevious();
		int u = size - 1;
		while (previous[u] >= 0) {
			result.add(0, u);
			u = previous[u];
		}
		result.add(0, 0);
		return Collections.unmodifiableList(result);
	}

	private int smallest() {
		int result = -1;
		for (Integer i : q) {
			if (result == -1 || dist[i] < dist[result]) {
				result = i;
			}
		}
		return result;
	}

	public final int getSize() {
		return size;
	}

}
