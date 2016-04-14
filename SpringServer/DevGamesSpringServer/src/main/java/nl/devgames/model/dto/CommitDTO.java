package nl.devgames.model.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import nl.devgames.model.Commit;

public abstract class CommitDTO extends ModelDTO<CommitDTO, Commit> {

	@Override
	public Commit toModel() {
		return new Commit();
	}
	@Override
	public boolean isValid() {
		return false;
		
	}
	@Override
	public CommitDTO createFromJsonObject(JsonObject object) {
		return new Gson().fromJson(object, CommitDTO.class);
	}
	
}
