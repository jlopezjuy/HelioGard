package ar.com.heliguard.app.service.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Domain rule violation translated to an RFC 7807 ProblemDetail response.
 */
public class DomainRuleException extends ErrorResponseException {

    private final String errorCode;

    public DomainRuleException(HttpStatusCode status, String errorCode, String detail) {
        super(status, buildProblemDetail(status, errorCode, detail), null);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    private static ProblemDetail buildProblemDetail(HttpStatusCode status, String errorCode, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(errorCode);
        pd.setProperty("errorCode", errorCode);
        pd.setProperty("message", "error." + errorCode);
        return pd;
    }
}
