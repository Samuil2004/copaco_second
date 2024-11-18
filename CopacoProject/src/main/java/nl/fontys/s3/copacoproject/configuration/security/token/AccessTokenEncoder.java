package nl.fontys.s3.copacoproject.configuration.security.token;

public interface AccessTokenEncoder {
    String encode(AccessToken accessToken);
}
