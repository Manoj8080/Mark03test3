package com.nukkadshops.mark03.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentMatchers;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Handler;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.nukkadshops.mark03.models.CancelRequest;
import com.nukkadshops.mark03.models.CancelResponse;
import com.nukkadshops.mark03.models.StatusRequest;
import com.nukkadshops.mark03.models.StatusResponse;
import com.nukkadshops.mark03.models.UploadRequest;
import com.nukkadshops.mark03.models.UploadResponse;
import com.nukkadshops.mark03.repository.PaymentRepository;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.sdk.PaymentState;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class PaymentViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private PaymentViewModel vm;
    private PaymentRepository mockRepo;
    private Handler mockHandler;
    private Call dummyCall;

    @Before
    public void setup() throws Exception {

        // -------------- REAL CONFIG -----------------
        PaymentConfig cfg = new PaymentConfig(
                "https://example.test/",
                29610,
                1013483,
                "1221258",
                "a4c9741b-2889",
                "tony",
                5
        );

        // ----------- MOCK HANDLER (NO LOOPER) -------
        mockHandler = mock(Handler.class);

        // ----------- CREATE VIEWMODEL ---------------
        vm = new PaymentViewModel(cfg);
        vm.setHandler(mockHandler);     // IMPORTANT

        // ----------- MOCK REPOSITORY ----------------
        mockRepo = mock(PaymentRepository.class);

        Field repoField = PaymentViewModel.class.getDeclaredField("repo");
        repoField.setAccessible(true);
        repoField.set(vm, mockRepo);

        // ----------- DUMMY RETROFIT CALL ------------
        dummyCall = mock(Call.class);
    }

    // ------------------------------------------------------------
    // UPLOAD SUCCESS
    // ------------------------------------------------------------
    @Test
    public void test_upload_success() {

        UploadRequest req = new UploadRequest(
                "TN1", 1, "CARD", "100.00",
                "user1", 29610, "a4c9741b",
                "1221258", 1013483, 15
        );

        UploadResponse mockResp = new UploadResponse();
        mockResp.ptr = 555L;

        doAnswer(inv -> {
            Callback<UploadResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(mockResp));
            return null;
        }).when(mockRepo).upload(any(UploadRequest.class), any());

        vm.uploadPayment(req);

        assertEquals(PaymentState.UPLOAD_DONE, vm.state.getValue());
        assertEquals(mockResp, vm.uploadResponse.getValue());
        assertEquals(555L, (long) vm.ptr.getValue());

        verify(mockHandler).post(any(Runnable.class));
    }

    // ------------------------------------------------------------
    // UPLOAD FAILURE
    // ------------------------------------------------------------
    @Test
    public void test_upload_failure() {

        doAnswer(inv -> {
            Callback<UploadResponse> cb = inv.getArgument(1);
            cb.onFailure(dummyCall, new Throwable("Network error"));
            return null;
        }).when(mockRepo).upload(any(UploadRequest.class), any());

        UploadRequest req = new UploadRequest(
                "TN1", 1, "CARD", "100.00",
                "user1", 29610, "a4c9741b",
                "1221258", 1013483, 15
        );

        vm.uploadPayment(req);
        assertEquals(PaymentState.ERROR, vm.state.getValue());
    }

    // ------------------------------------------------------------
    // STATUS SUCCESS
    // ------------------------------------------------------------
    @Test
    public void test_status_success() {

        vm.startStatusPolling(12345L);

        StatusResponse mockResp = new StatusResponse();
        mockResp.rm = "Approved";

        doAnswer(inv -> {
            Callback<StatusResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(mockResp));
            return null;
        }).when(mockRepo).status(any(StatusRequest.class), any());

        StatusRequest req = new StatusRequest(
                29610, "a4c9741b", "1221258",
                1013483, 12345L
        );

        vm.checkStatus(req);

        assertEquals(PaymentState.CHECK_DONE, vm.state.getValue());
        assertEquals(mockResp, vm.statusResponse.getValue());

        verify(mockHandler).removeCallbacks(any(Runnable.class));
    }

    // ------------------------------------------------------------
    // STATUS FAILURE
    // ------------------------------------------------------------
    @Test
    public void test_status_failure() {

        doAnswer(inv -> {
            Callback<StatusResponse> cb = inv.getArgument(1);
            cb.onFailure(dummyCall, new Throwable("Fail"));
            return null;
        }).when(mockRepo).status(any(StatusRequest.class), any());

        StatusRequest req = new StatusRequest(
                29610, "a4c9741b", "1221258",
                1013483, 12345L
        );

        vm.checkStatus(req);

        assertEquals(PaymentState.ERROR, vm.state.getValue());
    }

    // ------------------------------------------------------------
    // CANCEL SUCCESS
    // ------------------------------------------------------------
    @Test
    public void test_cancel_success() {

        CancelResponse mockResp = new CancelResponse();

        doAnswer(inv -> {
            Callback<CancelResponse> cb = inv.getArgument(1);
            cb.onResponse(dummyCall, Response.success(mockResp));
            return null;
        }).when(mockRepo).cancel(any(CancelRequest.class), any());

        CancelRequest req = new CancelRequest(
                "1221258", 1013483, 29610,
                "a4c9741b", 555L, "100.00", true
        );

        vm.cancelPayment(req);

        assertEquals(PaymentState.CANCEL_DONE, vm.state.getValue());
        assertEquals(mockResp, vm.cancelResponse.getValue());
    }

    // ------------------------------------------------------------
    // CANCEL FAILURE
    // ------------------------------------------------------------
    @Test
    public void test_cancel_failure() {

        doAnswer(inv -> {
            Callback<CancelResponse> cb = inv.getArgument(1);
            cb.onFailure(dummyCall, new Throwable("Fail"));
            return null;
        }).when(mockRepo).cancel(any(CancelRequest.class), any());

        CancelRequest req = new CancelRequest(
                "1221258", 1013483, 29610,
                "a4c9741b", 555L, "100.00", true
        );

        vm.cancelPayment(req);

        assertEquals(PaymentState.ERROR, vm.state.getValue());
    }
}
