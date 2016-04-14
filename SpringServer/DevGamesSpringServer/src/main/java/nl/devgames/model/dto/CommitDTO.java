package nl.devgames.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import nl.devgames.model.Commit;
import nl.devgames.model.Model;

public class CommitDTO extends ModelDTO<CommitDTO, Commit> {

	@Override
	public Commit toModel() {
		
	}
	@Override
	public boolean isValid() {
		return false;
		
	}
	@Override
	public CommitDTO createFromSjonObject(JsonObject object) {
		return new Gson().fromJson(object, CommitDTO.class)
	}
	
}
