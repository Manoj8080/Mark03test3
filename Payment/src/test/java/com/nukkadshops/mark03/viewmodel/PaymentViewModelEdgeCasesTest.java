package com.nukkadshops.mark03.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.nukkadshops.mark03.models.*;
import com.nukkadshops.mark03.repository.PaymentRepository;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.sdk.PaymentState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class PaymentViewModelEdgeCasesTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    PaymentViewModel vm;
    PaymentRepository mockRepo;
    Handler mockHandler;
    Call dummyCall;

    PaymentConfig cfg;

    @Before
    public void setup() {
        cfg = new PaymentConfig(
                "https://8138c749bc9d.ngrok-free.app/", 111, 222, "333",
                "token", "user", 5
        );

        mockRepo = mock(PaymentRepository.class);
        mockHandler = mock(Handler.class);
        dummyCall = mock(Call.class);

        vm = new PaymentViewModel(cfg, mockHandler, mockRepo);
    }

    // --------------------------------------------------------------------
    // A. UPLOAD EDGE CASES
    // --------------------------------------------------------------------

    @Test
    public void upload_null_response_body() {

        doAnswer(inv -> {
            Callback<UploadResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(null));
            return null;
        }).when(mockRepo).upload(any(), any());

        vm.uploadPayment(new UploadRequest("TN1", 1, "CARD", "100.00",
                "user1", 29610, "a4c9741b",
                "1221258", 1013483, 15));

        assertEquals(PaymentState.UPLOAD_DONE, vm.state.getValue());
        assertNull(vm.uploadResponse.getValue());
        verify(mockHandler, never()).post(any());
    }

    @Test
    public void upload_success_but_ptr_is_zero() {

        UploadResponse resp = new UploadResponse();
        resp.ptr = 0; // invalid

        doAnswer(inv -> {
            Callback<UploadResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(resp));
            return null;
        }).when(mockRepo).upload(any(), any());

        vm.uploadPayment(new UploadRequest("TN1", 1, "CARD", "100.00",
                "user1", 29610, "a4c9741b",
                "1221258", 1013483, 15));

        assertEquals(0L, (long) vm.ptr.getValue());
        verify(mockHandler).post(any()); // it still polls
    }

    @Test
    public void upload_repo_throws_exception() {

        doThrow(new RuntimeException("boom"))
                .when(mockRepo).upload(any(), any());

        try {
            vm.uploadPayment(new UploadRequest("TN1", 1, "CARD", "100.00",
                    "user1", 29610, "a4c9741b",
                    "1221258", 1013483, 15));
        } catch (Exception ignored) {}

        assertEquals(PaymentState.UPLOADING, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // B. STATUS EDGE CASES
    // --------------------------------------------------------------------

    @Test
    public void status_null_body() {

        doAnswer(inv -> {
            Callback<StatusResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(null));
            return null;
        }).when(mockRepo).status(any(), any());

        vm.checkStatus(new StatusRequest(29610, "a4c9741b", "1221258",
                1013483, 12345L));

        assertNull(vm.statusResponse.getValue());
        assertEquals(PaymentState.CHECK_DONE, vm.state.getValue());
        verify(mockHandler, never()).removeCallbacks(any());
    }

    @Test
    public void status_null_rm_no_crash() {

        StatusResponse resp = new StatusResponse();
        resp.rm = null;

        doAnswer(inv -> {
            Callback<StatusResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(resp));
            return null;
        }).when(mockRepo).status(any(), any());

        vm.checkStatus(new StatusRequest(29610, "a4c9741b", "1221258",
                1013483, 12345L));

        assertNotNull(vm.statusResponse.getValue());
        verify(mockHandler, never()).removeCallbacks(any());
    }

    @Test
    public void status_whitespace_rm() {

        StatusResponse resp = new StatusResponse();
        resp.rm = "   ";

        doAnswer(inv -> {
            Callback<StatusResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(resp));
            return null;
        }).when(mockRepo).status(any(), any());

        vm.checkStatus(new StatusRequest(29610, "a4c9741b", "1221258",
                1013483, 12345L));

        verify(mockHandler, never()).removeCallbacks(any());
    }

    @Test
    public void status_final_approved_case_insensitive() {

        StatusResponse resp = new StatusResponse();
        resp.rm = "ApPrOvEd";

        doAnswer(inv -> {
            Callback<StatusResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(resp));
            return null;
        }).when(mockRepo).status(any(), any());

        vm.checkStatus(new StatusRequest(29610, "a4c9741b", "1221258",
                1013483, 12345L));

        verify(mockHandler).removeCallbacks(any());
        assertEquals(PaymentState.CHECK_DONE, vm.state.getValue());
    }

    @Test
    public void status_error_response_body() {

        doAnswer(inv -> {
            Callback<StatusResponse> cb = inv.getArgument(1);

            // simulation: HTTP error, but response.body() = null
            Response<StatusResponse> errorResp =
                    Response.error(500, okhttp3.ResponseBody.create(null, ""));

            cb.onResponse(dummyCall, errorResp);
            return null;
        }).when(mockRepo).status(any(), any());

        vm.checkStatus(new StatusRequest(29610, "a4c9741b", "1221258",
                1013483, 12345L));

        assertNull(vm.statusResponse.getValue());
        assertEquals(PaymentState.CHECK_DONE, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // C. CANCEL EDGE CASES
    // --------------------------------------------------------------------

    @Test
    public void cancel_null_body() {

        doAnswer(inv -> {
            Callback<CancelResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(null));
            return null;
        }).when(mockRepo).cancel(any(), any());

        vm.cancelPayment(new CancelRequest("1221258", 1013483, 29610,
                "a4c9741b", 555L, "100.00", true));

        assertNull(vm.cancelResponse.getValue());
        assertEquals(PaymentState.CANCEL_DONE, vm.state.getValue());
    }

    @Test
    public void cancel_failure_throwable_null() {

        doAnswer(inv -> {
            Callback<CancelResponse> cb = inv.getArgument(1);
            cb.onFailure(dummyCall, null);
            return null;
        }).when(mockRepo).cancel(any(), any());

        vm.cancelPayment(new CancelRequest("1221258", 1013483, 29610,
                "a4c9741b", 555L, "100.00", true));

        assertEquals(PaymentState.ERROR, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // D. POLLING EDGE CASES
    // --------------------------------------------------------------------

    @Test
    public void polling_start_twice_removes_previous() {

        vm.startStatusPolling(111);
        vm.startStatusPolling(222);

        verify(mockHandler, times(2)).post(any());
        verify(mockHandler, times(1)).removeCallbacks(any());
    }

    @Test
    public void stop_polling_when_null_does_not_crash() {
        vm.stopStatusPolling(); // poller == null
        assertTrue(true); // no crash
    }

    @Test
    public void onCleared_stops_polling() {

        vm.startStatusPolling(999);

        vm.onCleared();

        verify(mockHandler).removeCallbacks(any());
    }

    @Test
    public void polling_negative_ptr() {

        vm.startStatusPolling(-55);

        verify(mockHandler).post(any());
    }
}
