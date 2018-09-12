/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.skcraft.launcher.auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skcraft.launcher.auth.AuthenticationException;
import com.skcraft.launcher.auth.LoginService;
import com.skcraft.launcher.auth.Session;
import com.skcraft.launcher.auth.UserType;
import com.skcraft.launcher.util.HttpRequest;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class YggdrasilLoginService
implements LoginService {
    private final URL authUrl;

    public YggdrasilLoginService(@NonNull URL authUrl) {
        if (authUrl == null) {
            throw new NullPointerException("authUrl");
        }
        this.authUrl = authUrl;
    }

    @Override
    public List<? extends Session> login(String agent, String id, String password) throws IOException, InterruptedException, AuthenticationException {
        AuthenticatePayload payload = new AuthenticatePayload(new Agent(agent), id, password);
        HttpRequest request = HttpRequest.post(this.authUrl).bodyJson(payload).execute();
        if (request.getResponseCode() != 200) {
            ErrorResponse error = request.returnContent().asJson(ErrorResponse.class);
            throw new AuthenticationException(error.getErrorMessage(), error.getErrorMessage());
        }
        AuthenticateResponse response = request.returnContent().asJson(AuthenticateResponse.class);
        return response.getAvailableProfiles();
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class Profile
    implements Session {
        @JsonProperty(value="id")
        private String uuid;
        private String name;
        private boolean legacy;
        @JsonIgnore
        private final Map<String, String> userProperties = Collections.emptyMap();
        @JsonBackReference
        private AuthenticateResponse response;

        @JsonIgnore
        @Override
        public String getSessionToken() {
            return String.format("token:%s:%s", this.getAccessToken(), this.getUuid());
        }

        @JsonIgnore
        @Override
        public String getClientToken() {
            return this.response.getClientToken();
        }

        @JsonIgnore
        @Override
        public String getAccessToken() {
            return this.response.getAccessToken();
        }

        @JsonIgnore
        @Override
        public UserType getUserType() {
            return this.legacy ? UserType.LEGACY : UserType.MOJANG;
        }

        @Override
        public boolean isOnline() {
            return true;
        }

        @Override
        public String getUuid() {
            return this.uuid;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public boolean isLegacy() {
            return this.legacy;
        }

        @Override
        public Map<String, String> getUserProperties() {
            return this.userProperties;
        }

        public AuthenticateResponse getResponse() {
            return this.response;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLegacy(boolean legacy) {
            this.legacy = legacy;
        }

        public void setResponse(AuthenticateResponse response) {
            this.response = response;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Profile)) {
                return false;
            }
            Profile other = (Profile)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$uuid = this.getUuid();
            String other$uuid = other.getUuid();
            if (this$uuid == null ? other$uuid != null : !this$uuid.equals(other$uuid)) {
                return false;
            }
            String this$name = this.getName();
            String other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                return false;
            }
            if (this.isLegacy() != other.isLegacy()) {
                return false;
            }
            Map<String, String> this$userProperties = this.getUserProperties();
            Map<String, String> other$userProperties = other.getUserProperties();
            if (this$userProperties == null ? other$userProperties != null : !this$userProperties.equals(other$userProperties)) {
                return false;
            }
            AuthenticateResponse this$response = this.getResponse();
            AuthenticateResponse other$response = other.getResponse();
            if (this$response == null ? other$response != null : !this$response.equals(other$response)) {
                return false;
            }
            return true;
        }

        public boolean canEqual(Object other) {
            return other instanceof Profile;
        }

        public int hashCode() {
            int PRIME = 31;
            int result = 1;
            String $uuid = this.getUuid();
            result = result * 31 + ($uuid == null ? 0 : $uuid.hashCode());
            String $name = this.getName();
            result = result * 31 + ($name == null ? 0 : $name.hashCode());
            result = result * 31 + (this.isLegacy() ? 1231 : 1237);
            Map<String, String> $userProperties = this.getUserProperties();
            result = result * 31 + ($userProperties == null ? 0 : $userProperties.hashCode());
            AuthenticateResponse $response = this.getResponse();
            result = result * 31 + ($response == null ? 0 : $response.hashCode());
            return result;
        }

        public String toString() {
            return "YggdrasilLoginService.Profile(uuid=" + this.getUuid() + ", name=" + this.getName() + ", legacy=" + this.isLegacy() + ", userProperties=" + this.getUserProperties() + ")";
        }
    }

    private static class ErrorResponse {
        private String error;
        private String errorMessage;
        private String cause;

        public String getError() {
            return this.error;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public String getCause() {
            return this.cause;
        }

        public void setError(String error) {
            this.error = error;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ErrorResponse)) {
                return false;
            }
            ErrorResponse other = (ErrorResponse)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$error = this.getError();
            String other$error = other.getError();
            if (this$error == null ? other$error != null : !this$error.equals(other$error)) {
                return false;
            }
            String this$errorMessage = this.getErrorMessage();
            String other$errorMessage = other.getErrorMessage();
            if (this$errorMessage == null ? other$errorMessage != null : !this$errorMessage.equals(other$errorMessage)) {
                return false;
            }
            String this$cause = this.getCause();
            String other$cause = other.getCause();
            if (this$cause == null ? other$cause != null : !this$cause.equals(other$cause)) {
                return false;
            }
            return true;
        }

        public boolean canEqual(Object other) {
            return other instanceof ErrorResponse;
        }

        public int hashCode() {
            int PRIME = 31;
            int result = 1;
            String $error = this.getError();
            result = result * 31 + ($error == null ? 0 : $error.hashCode());
            String $errorMessage = this.getErrorMessage();
            result = result * 31 + ($errorMessage == null ? 0 : $errorMessage.hashCode());
            String $cause = this.getCause();
            result = result * 31 + ($cause == null ? 0 : $cause.hashCode());
            return result;
        }

        public String toString() {
            return "YggdrasilLoginService.ErrorResponse(error=" + this.getError() + ", errorMessage=" + this.getErrorMessage() + ", cause=" + this.getCause() + ")";
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    private static class AuthenticateResponse {
        private String accessToken;
        private String clientToken;
        @JsonManagedReference
        private List<Profile> availableProfiles;
        private Profile selectedProfile;

        public String getAccessToken() {
            return this.accessToken;
        }

        public String getClientToken() {
            return this.clientToken;
        }

        public List<Profile> getAvailableProfiles() {
            return this.availableProfiles;
        }

        public Profile getSelectedProfile() {
            return this.selectedProfile;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setClientToken(String clientToken) {
            this.clientToken = clientToken;
        }

        public void setAvailableProfiles(List<Profile> availableProfiles) {
            this.availableProfiles = availableProfiles;
        }

        public void setSelectedProfile(Profile selectedProfile) {
            this.selectedProfile = selectedProfile;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof AuthenticateResponse)) {
                return false;
            }
            AuthenticateResponse other = (AuthenticateResponse)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$accessToken = this.getAccessToken();
            String other$accessToken = other.getAccessToken();
            if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) {
                return false;
            }
            String this$clientToken = this.getClientToken();
            String other$clientToken = other.getClientToken();
            if (this$clientToken == null ? other$clientToken != null : !this$clientToken.equals(other$clientToken)) {
                return false;
            }
            List<Profile> this$availableProfiles = this.getAvailableProfiles();
            List<Profile> other$availableProfiles = other.getAvailableProfiles();
            if (this$availableProfiles == null ? other$availableProfiles != null : !this$availableProfiles.equals(other$availableProfiles)) {
                return false;
            }
            Profile this$selectedProfile = this.getSelectedProfile();
            Profile other$selectedProfile = other.getSelectedProfile();
            if (this$selectedProfile == null ? other$selectedProfile != null : !this$selectedProfile.equals(other$selectedProfile)) {
                return false;
            }
            return true;
        }

        public boolean canEqual(Object other) {
            return other instanceof AuthenticateResponse;
        }

        public int hashCode() {
            int PRIME = 31;
            int result = 1;
            String $accessToken = this.getAccessToken();
            result = result * 31 + ($accessToken == null ? 0 : $accessToken.hashCode());
            String $clientToken = this.getClientToken();
            result = result * 31 + ($clientToken == null ? 0 : $clientToken.hashCode());
            List<Profile> $availableProfiles = this.getAvailableProfiles();
            result = result * 31 + ($availableProfiles == null ? 0 : $availableProfiles.hashCode());
            Profile $selectedProfile = this.getSelectedProfile();
            result = result * 31 + ($selectedProfile == null ? 0 : $selectedProfile.hashCode());
            return result;
        }

        public String toString() {
            return "YggdrasilLoginService.AuthenticateResponse(accessToken=" + this.getAccessToken() + ", clientToken=" + this.getClientToken() + ", availableProfiles=" + this.getAvailableProfiles() + ", selectedProfile=" + this.getSelectedProfile() + ")";
        }
    }

    private static class AuthenticatePayload {
        private final Agent agent;
        private final String username;
        private final String password;

        @ConstructorProperties(value={"agent", "username", "password"})
        public AuthenticatePayload(Agent agent, String username, String password) {
            this.agent = agent;
            this.username = username;
            this.password = password;
        }

        public Agent getAgent() {
            return this.agent;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof AuthenticatePayload)) {
                return false;
            }
            AuthenticatePayload other = (AuthenticatePayload)o;
            if (!other.canEqual(this)) {
                return false;
            }
            Agent this$agent = this.getAgent();
            Agent other$agent = other.getAgent();
            if (this$agent == null ? other$agent != null : !this$agent.equals(other$agent)) {
                return false;
            }
            String this$username = this.getUsername();
            String other$username = other.getUsername();
            if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
                return false;
            }
            String this$password = this.getPassword();
            String other$password = other.getPassword();
            if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
                return false;
            }
            return true;
        }

        public boolean canEqual(Object other) {
            return other instanceof AuthenticatePayload;
        }

        public int hashCode() {
            int PRIME = 31;
            int result = 1;
            Agent $agent = this.getAgent();
            result = result * 31 + ($agent == null ? 0 : $agent.hashCode());
            String $username = this.getUsername();
            result = result * 31 + ($username == null ? 0 : $username.hashCode());
            String $password = this.getPassword();
            result = result * 31 + ($password == null ? 0 : $password.hashCode());
            return result;
        }

        public String toString() {
            return "YggdrasilLoginService.AuthenticatePayload(agent=" + this.getAgent() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ")";
        }
    }

    private static class Agent {
        private final String name;
        private final int version = 1;

        @ConstructorProperties(value={"name"})
        public Agent(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public int getVersion() {
            this.getClass();
            return 1;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Agent)) {
                return false;
            }
            Agent other = (Agent)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$name = this.getName();
            String other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                return false;
            }
            if (this.getVersion() != other.getVersion()) {
                return false;
            }
            return true;
        }

        public boolean canEqual(Object other) {
            return other instanceof Agent;
        }

        public int hashCode() {
            int PRIME = 31;
            int result = 1;
            String $name = this.getName();
            result = result * 31 + ($name == null ? 0 : $name.hashCode());
            result = result * 31 + this.getVersion();
            return result;
        }

        public String toString() {
            return "YggdrasilLoginService.Agent(name=" + this.getName() + ", version=" + this.getVersion() + ")";
        }
    }

}

