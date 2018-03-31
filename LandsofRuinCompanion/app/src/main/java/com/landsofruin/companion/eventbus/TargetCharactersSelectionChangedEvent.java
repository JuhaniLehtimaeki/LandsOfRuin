package com.landsofruin.companion.eventbus;

import com.landsofruin.companion.state.CharacterState;

import java.util.List;

public class TargetCharactersSelectionChangedEvent {
	private List<CharacterState> characters;

	public TargetCharactersSelectionChangedEvent(List<CharacterState> characters) {
		this.characters = characters;
	}

	public List<CharacterState> getCharacters() {
		return characters;
	}
}
