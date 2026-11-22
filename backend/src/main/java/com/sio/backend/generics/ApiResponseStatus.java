package com.sio.backend.generics;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Outcome of a REST response in the SIO system.
 * <ul>
 *   <li>{@link #SUCCESS}: operation completed successfully.</li>
 *   <li>{@link #FAIL}: invalid request or validation failure (4xx).</li>
 *   <li>{@link #ERROR}: unexpected internal server error (5xx).</li>
 * </ul>
 */
@Schema(description = "Outcome status of the API response (SUCCESS, FAIL, ERROR)")
public enum ApiResponseStatus {

    @Schema(description = "Operation completed successfully")
    SUCCESS,

    @Schema(description = "Critical system or server-side error")
    ERROR,

    @Schema(description = "Request failure caused by the client (e.g. validation or missing data)")
    FAIL
}
