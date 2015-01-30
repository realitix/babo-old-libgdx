/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.baboviolent.game.ai.fsm;

import com.baboviolent.game.ai.AiBabo;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/** @author davebaol */
public enum AiBaboState implements State<AiBabo> {

	THINK() {
		@Override
		public void update (AiBabo ai) {
			ai.getStateMachine().changeState(SEEK);
		}

		// On va chercher le babo le plus proche
		@Override
		public void enter(AiBabo ai) {
		}

		@Override
		public void exit(AiBabo arg0) {
		}

		@Override
		public boolean onMessage(AiBabo arg0, Telegram arg1) {
			return false;
		}
	},
	
	SEEK() {
		@Override
		public void update (AiBabo ai) {
			if(ai.reachedDestination()) {
				ai.getStateMachine().changeState(THINK);
			}
		}

		// On va chercher le babo le plus proche
		@Override
		public void enter(AiBabo ai) {
			ai.seekClosestBabo();
		}

		@Override
		public void exit(AiBabo arg0) {
		}

		@Override
		public boolean onMessage(AiBabo arg0, Telegram arg1) {
			return false;
		}
	}

}
