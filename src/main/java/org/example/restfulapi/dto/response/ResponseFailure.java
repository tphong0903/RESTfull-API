package org.example.restfulapi.dto.response;

import org.springframework.http.HttpStatusCode;

public class ResponseFailure extends ResponseSucces{
    public ResponseFailure(HttpStatusCode status, String message) {
        super(status, message);
    }
}
