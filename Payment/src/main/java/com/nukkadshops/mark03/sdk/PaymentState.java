package com.nukkadshops.mark03.sdk;

public enum PaymentState {
    // Initial
    IDLE,

    // Upload Flow
    UPLOADING,
    UPLOAD_DONE,

    // Status Check
    CHECKING,
    CHECK_DONE,

    // Cancel Flow
    CANCELING,
    CANCEL_DONE,

    // Void Flow
    VOIDING,
    VOID_DONE,

    // Final outcomes
    SUCCESS,
    FAILED,
    CANCELLED,
    TIMEOUT,

    // Errors
    ERROR;
}
