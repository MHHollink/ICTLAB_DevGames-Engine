package baecon.devgames.connection.client;

import java.util.List;
import java.util.Map;

import baecon.devgames.connection.client.dto.CommitDTO;
import baecon.devgames.connection.client.dto.ProjectDTO;
import baecon.devgames.connection.client.dto.UserDTO;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DevGamesClient {

    @POST("/login")
    Map<String, String> login(@Query("user") String username,
                              @Query("pass") String password);

    @GET("/user")
    UserDTO getCurrentUser();

    @GET("/user/{id}")
    UserDTO getUser(@Path("id") Long uuid);

    @GET("/user/{id}/projects")
    List<ProjectDTO> getProjectsOfUser(@Path("id") String uuid);

    @GET("/user/{id}/commits")
    List<CommitDTO> getCommitsOfUser(@Path("id") String uuid);

    /**
     * Changes one or more attributes of the currently logged in user.
     *
     * example : GCM key
     *
     * @param user
     *         The user to save.
     *
     * @return an HTTP response of the request.
     */
    @PUT("/team/member")
    Response changeOwnUser(@Body UserDTO user);

}
