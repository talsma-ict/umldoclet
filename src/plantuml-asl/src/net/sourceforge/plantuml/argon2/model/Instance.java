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
/* 	This file is taken from
	https://github.com/andreas1327250/argon2-java

	Original Author: Andreas Gadermaier <up.gadermaier@gmail.com>
 */
package net.sourceforge.plantuml.argon2.model;

import static net.sourceforge.plantuml.argon2.Constants.ARGON2_SYNC_POINTS;

import net.sourceforge.plantuml.argon2.Argon2;

public class Instance {

	public Block[] memory;
	private int version;
	private int iterations;
	private int segmentLength;
	private int laneLength;
	private int lanes;

	private Argon2Type type;

	public Instance(Argon2 argon2) {
		this.version = argon2.getVersion();
		this.iterations = argon2.getIterations();
		this.lanes = argon2.getLanes();
		this.type = argon2.getType();

		/* 2. Align memory size */
		/* Minimum memoryBlocks = 8L blocks, where L is the number of lanes */
		int memoryBlocks = argon2.getMemory();

		if (memoryBlocks < 2 * ARGON2_SYNC_POINTS * argon2.getLanes()) {
			memoryBlocks = 2 * ARGON2_SYNC_POINTS * argon2.getLanes();
		}

		this.segmentLength = memoryBlocks / (argon2.getLanes() * ARGON2_SYNC_POINTS);
		this.laneLength = segmentLength * ARGON2_SYNC_POINTS;
		/* Ensure that all segments have equal length */
		memoryBlocks = segmentLength * (argon2.getLanes() * ARGON2_SYNC_POINTS);

		initMemory(memoryBlocks);
	}

	private void initMemory(int memoryBlocks) {
		this.memory = new Block[memoryBlocks];

		for (int i = 0; i < memory.length; i++) {
			memory[i] = new Block();
		}
	}

	public void clear() {
		for (Block b : memory) {
			b.clear();
		}

		memory = null;
	}

	public Block[] getMemory() {
		return memory;
	}

	public int getVersion() {
		return version;
	}

	public int getIterations() {
		return iterations;
	}

	public int getSegmentLength() {
		return segmentLength;
	}

	public int getLaneLength() {
		return laneLength;
	}

	public int getLanes() {
		return lanes;
	}

	public Argon2Type getType() {
		return type;
	}
}
