package com.plethub.paaro.core.usermanagement.request;

import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.usermanagement.enums.ResponseStatus;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.apimodels.PasswordResetRequestModel;
import com.plethub.paaro.core.usermanagement.apimodels.UserUpdateRequestModel;
import com.plethub.paaro.core.infrastructure.utils.RestApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserDetailsRequest {

    @Autowired
    private RestApiClient restApiClient;

    @Value("${user.service.url:http://localhost:8011}")
    private String url;

    private Logger logger = LoggerFactory.getLogger(UserDetailsRequest.class);

    public ManagedUserModelApi getManagedUserDetailsByEmail(String username) throws Exception {

        try {
            ManagedUserModelApi managedUserModelApi = restApiClient.apiGetAndGetClass(url+"/user/getDetailsByemail?email="+username,ManagedUserModelApi.class,
                    null,null, true);

            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occured while fetching user details due to ", e);
            return new ManagedUserModelApi(null,null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured due to " + e.getCause().toString()));
        }
    }
    public ManagedUserModelApi getManagedUserDetailsEntity(String username) throws Exception {

        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi userModelApi = restApiClient.apiGetAndGetClass(url+"/user/getDetailsByUsername/"+username,ManagedUserModelApi.class,
                    null,headers, true);

            return userModelApi;
        } catch (Exception e) {
            logger.error("Error occured while fetching user details with entity due to ", e);
            return new ManagedUserModelApi(new ManagedUser(),null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured due to " + e.getCause().toString()));
        }
    }

    public ManagedUserModelApi addManagedUser(ManagedUser managedUser){

        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi managedUserModelApi = restApiClient.apiPostAndGetClass(url+"/user/createUser",ManagedUserModelApi.class,managedUser,headers, false);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occured while adding user due to ", e);
            return new ManagedUserModelApi(managedUser,null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured due to " + e.getCause().toString()));
        }

    }

    public ManagedUserModelApi deactivateUser(String emial) {
        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi userModelApi = restApiClient.apiGetAndGetClass(url+"/user/deactivateUser?email="+emial,ManagedUserModelApi.class,
                    null,headers, true);

            return userModelApi;
        } catch (Exception e) {
            logger.error("Error occured while deactivating user due to ", e);
            return new ManagedUserModelApi(new ManagedUser(),null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured due to " + e.getCause().toString()));
        }
    }
    public ManagedUserModelApi activateUser(String emial) {
        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi userModelApi = restApiClient.apiGetAndGetClass(url+"/user/activateUser?email="+emial,ManagedUserModelApi.class,
                    null,headers, true);

            return userModelApi;
        } catch (Exception e) {
            logger.error("Error occured while activating user due to ", e);
            return new ManagedUserModelApi(new ManagedUser(),null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured due to " + e.getCause().toString()));
        }
    }
    public ManagedUserModelApi resetUserPassword(PasswordResetRequestModel resetRequestModel) {
        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi managedUserModelApi = restApiClient.apiPostAndGetClass(url+"/user/reset_user_password",ManagedUserModelApi.class,resetRequestModel,headers, true);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occured while resetting user password due to ", e);
            return new ManagedUserModelApi(null,null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured due to " + e.getCause().toString()));
        }
    }

    public ManagedUserModelApi updateUserDetails(UserUpdateRequestModel resetRequestModel) {
        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi managedUserModelApi = restApiClient.apiPostAndGetClass(url+"/user/updateUser",ManagedUserModelApi.class,resetRequestModel,headers, true);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occured while resetting user password due to ", e);
            return new ManagedUserModelApi(null,null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured due to " + e.getCause().toString()));
        }
    }

    public ManagedUserModelApi createAdminUser(ManagedUser managedUser){

        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi managedUserModelApi = restApiClient.apiPostAndGetClass(url+"/user/createAdminUser",ManagedUserModelApi.class,managedUser,headers, true);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occured while adding admin user due to ", e);
            return new ManagedUserModelApi(managedUser,null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured while creating admin user"));
        }

    }
    public ManagedUserModelApi logout(){

        HashMap<String,String> headers = new HashMap<>();

        try {
            ManagedUserModelApi managedUserModelApi = restApiClient.apiGetAndGetClass(url+"/user/logout",ManagedUserModelApi.class,null,headers, true);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occured while logging out user due to ", e);
            return new ManagedUserModelApi(null,null, ResponseStatus.SYSTEM_ERROR,String.format("Error occured while logging out user"));
        }

    }
}
