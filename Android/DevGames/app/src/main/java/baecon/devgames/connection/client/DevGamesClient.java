package baecon.devgames.connection.client;

import java.util.List;
import java.util.Map;

import baecon.devgames.model.Commit;
import baecon.devgames.model.Project;
import baecon.devgames.model.User;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DevGamesClient {

    @GET("/login")
    Map<String, String> login(@Query("user") String username,
                              @Query("pass") String password);

    @GET("/user")
    User getCurrentUser();

    @GET("/user/{uuid}")
    User getUserById(@Path("uuid") String uuid);

    @GET("/user/{uuid}/projects")
    List<Project> getProjectsOfUser(@Path("uuid") String uuid);

    @GET("/user/{uuid}/commits")
    List<Commit> getCommitsOfUser(@Path("uuid") String uuid);
}
