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
package net.sourceforge.plantuml.graph;

import java.util.ArrayList;
import java.util.List;

public class BoardCollection {

	static class Entry implements Comparable<Entry> {
		final private Board board;
		final private double cost;
		private boolean explored;

		public Entry(Board b, CostComputer costComputer) {
			this.board = b;
			if (costComputer == null) {
				this.cost = 0;
			} else {
				this.cost = costComputer.getCost(b);
			}
		}

		public int compareTo(Entry other) {
			return (int) Math.signum(this.cost - other.cost);
		}

		@Override
		public boolean equals(Object obj) {
			final Entry other = (Entry) obj;
			return board.equals(other.board);
		}

		@Override
		public int hashCode() {
			return board.hashCode();
		}

	}

	private final SortedCollection<Entry> all = new SortedCollectionArrayList<Entry>();

	private final CostComputer costComputer;

	public BoardCollection(CostComputer costComputer) {
		this.costComputer = costComputer;
	}

	public int size() {
		return all.size();
	}

	public Board getAndSetExploredSmallest() {
		for (Entry ent : all) {
			if (ent.explored == false) {
				ent.explored = true;
				assert costComputer.getCost(ent.board) == ent.cost;
				// Log.println("Peeking " + ent.cost);
				return ent.board;
			}
		}
		return null;
	}

	public double getBestCost() {
		for (Entry ent : all) {
			return ent.cost;
		}
		return 0;
	}

	public Board getBestBoard() {
		for (Entry ent : all) {
			return ent.board;
		}
		return null;
	}

	public List<Double> getCosts() {
		final List<Double> result = new ArrayList<Double>();
		for (Entry ent : all) {
			result.add(costComputer.getCost(ent.board));
		}
		return result;
	}

	public void add(Board b) {
		all.add(new Entry(b, costComputer));
	}

	public boolean contains(Board b) {
		return all.contains(new Entry(b, null));
	}

}
