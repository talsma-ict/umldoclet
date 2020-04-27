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
package net.sourceforge.plantuml.graph;

import java.util.Random;

public class Oven {

	final private double temp;
	final private CostComputer costComputer;

	public Oven(double temp, CostComputer costComputer) {
		this.temp = temp;
		this.costComputer = costComputer;
	}

	public Board longTic(int nbTic, Board board, Random rnd) {
		double best = costComputer.getCost(board);
		Board bestBoard = board.copy();
		for (int i = 0; i < nbTic; i++) {
			final double current = tic(board, rnd);
			// Log.println("current=" + current + " best=" + best);
			if (current < best) {
				best = current;
				bestBoard = board.copy();
			}

		}
		return bestBoard;
	}

	public double tic(Board board, Random rnd) {
		// Log.println("Oven::tic");
		final double costBefore = costComputer.getCost(board);
		final Move move = null; // board.getRandomMove(rnd);
		board.applyMove(move);
		final double costAfter = costComputer.getCost(board);
		final double delta = costAfter - costBefore;
		// Log.println("delta=" + delta);
		if (delta <= 0) {
			return costAfter;
		}
		assert delta > 0;
		assert costAfter > costBefore;
		// Log.println("temp=" + temp);
		if (temp > 0) {
			final double probability = Math.exp(-delta / temp);
			final double dice = rnd.nextDouble();
			// Log.println("probability=" + probability + " dice=" +
			// dice);
			if (dice < probability) {
				// Log.println("We keep it");
				return costAfter;
			}
		}
		// Log.println("Roolback");
		board.applyMove(move.getBackMove());
		assert costBefore == costComputer.getCost(board);
		return costBefore;

	}
}
