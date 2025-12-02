package com.nukkadshops.mark03.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.nukkadshops.mark03.models.VoidRequest;
import com.nukkadshops.mark03.models.VoidResponse;
import com.nukkadshops.mark03.repository.PaymentRepository;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.sdk.VoidState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VoidViewModelEdgeCasesTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private VoidViewModel vm;
    private PaymentRepository mockRepo;
    private Call<VoidResponse> mockCall;

    private Callback<VoidResponse> capturedCb;

    @Before
    public void setup() throws Exception {

        // Mocked config (not used heavily)
        PaymentConfig cfg = mock(PaymentConfig.class);
        when(cfg.getBaseUrl()).thenReturn("http://dummy/");

        vm = new VoidViewModel(cfg);

        // Replace repository with mock
        mockRepo = mock(PaymentRepository.class);
        Field repoField = VoidViewModel.class.getDeclaredField("repo");
        repoField.setAccessible(true);
        repoField.set(vm, mockRepo);

        mockCall = mock(Call.class);
    }

    private VoidRequest sampleReq() {
        return new VoidRequest("TN1", 1, "CARD", "100.00",
                "user", 101, "token", "store", 99, 1, 123L);
    }

    // --------------------------------------------------------------------
    // SUCCESS WITHOUT CALLBACK TRIGGERED YET
    // --------------------------------------------------------------------

    @Test
    public void initial_state_is_idle() {
        assertEquals(VoidState.IDLE, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 1: SUCCESS WITH NULL BODY
    // --------------------------------------------------------------------

    @Test
    public void void_null_response_body() {

        // Capture only
        doAnswer(inv -> {
            capturedCb = inv.getArgument(1);
            return null;
        }).when(mockRepo).voidTransaction(any(), any());

        vm.voidPayment(sampleReq());

        assertEquals(VoidState.PROCESSING, vm.state.getValue());

        // Trigger success with NULL body
        capturedCb.onResponse(mockCall, Response.success(null));

        assertNull(vm.res1.getValue());
        assertEquals(VoidState.APPROVED, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 2: HTTP ERROR CODE BUT RESPONSE BODY IS NULL
    // --------------------------------------------------------------------

    @Test
    public void void_http_error_but_framework_calls_onResponse() {

        doAnswer(inv -> {
            capturedCb = inv.getArgument(1);
            return null;
        }).when(mockRepo).voidTransaction(any(), any());

        vm.voidPayment(sampleReq());

        Response<VoidResponse> errorResp = Response.error(
                500,
                okhttp3.ResponseBody.create(null, "")
        );

        capturedCb.onResponse(mockCall, errorResp);

        assertNull(vm.res1.getValue());
        assertEquals(VoidState.APPROVED, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 3: MALFORMED RESPONSE BODY (Still non-null)
    // --------------------------------------------------------------------

    @Test
    public void void_malformed_response_no_fields() {

        VoidResponse resp = new VoidResponse(); // empty object

        doAnswer(inv -> {
            capturedCb = inv.getArgument(1);
            return null;
        }).when(mockRepo).voidTransaction(any(), any());

        vm.voidPayment(sampleReq());

        capturedCb.onResponse(mockCall, Response.success(resp));

        assertEquals(resp, vm.res1.getValue());
        assertEquals(VoidState.APPROVED, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 4: FAILURE WITH NORMAL THROWABLE
    // --------------------------------------------------------------------

    @Test
    public void void_failure_normal() {

        doAnswer(inv -> {
            Callback<VoidResponse> cb = inv.getArgument(1);
            cb.onFailure(mockCall, new Throwable("x"));
            return null;
        }).when(mockRepo).voidTransaction(any(), any());

        vm.voidPayment(sampleReq());

        assertEquals(VoidState.ERROR, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 5: FAILURE WITH NULL THROWABLE
    // --------------------------------------------------------------------

    @Test
    public void void_failure_with_null_throwable() {

        doAnswer(inv -> {
            Callback<VoidResponse> cb = inv.getArgument(1);
            cb.onFailure(mockCall, null); // null throwable
            return null;
        }).when(mockRepo).voidTransaction(any(), any());

        vm.voidPayment(sampleReq());

        assertEquals(VoidState.ERROR, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 6: Repository throws exception BEFORE callback
    // --------------------------------------------------------------------

    @Test
    public void void_repository_throws_exception() {

        doThrow(new RuntimeException("boom"))
                .when(mockRepo).voidTransaction(any(), any());

        try {
            vm.voidPayment(sampleReq());
        } catch (Exception ignored) {}

        // viewmodel never gets callback → stays PROCESSING
        assertEquals(VoidState.PROCESSING, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 7: Double void call (second call overrides state)
    // --------------------------------------------------------------------

    @Test
    public void void_called_twice_last_one_wins() {

        // First call
        doAnswer(inv -> {
            capturedCb = inv.getArgument(1);
            return null;
        }).when(mockRepo).voidTransaction(any(), any());

        vm.voidPayment(sampleReq());
        assertEquals(VoidState.PROCESSING, vm.state.getValue());

        // Simulate success
        capturedCb.onResponse(mockCall, Response.success(new VoidResponse()));
        assertEquals(VoidState.APPROVED, vm.state.getValue());

        // Second call
        vm.voidPayment(sampleReq());
        assertEquals(VoidState.PROCESSING, vm.state.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 8: Response body reused across multiple calls
    // --------------------------------------------------------------------

    @Test
    public void void_response_should_refresh_each_time() {

        VoidResponse resp1 = new VoidResponse();
        VoidResponse resp2 = new VoidResponse();

        doAnswer(inv -> {
            Callback<VoidResponse> cb = inv.getArgument(1);
            cb.onResponse(mockCall, Response.success(resp1));
            return null;
        }).doAnswer(inv -> {
            Callback<VoidResponse> cb = inv.getArgument(1);
            cb.onResponse(mockCall, Response.success(resp2));
            return null;
        }).when(mockRepo).voidTransaction(any(), any());

        vm.voidPayment(sampleReq());
        assertEquals(resp1, vm.res1.getValue());

        vm.voidPayment(sampleReq());
        assertEquals(resp2, vm.res1.getValue());
    }

    // --------------------------------------------------------------------
    // EDGE CASE 9: VoidRequest mismatch → callback never triggered
    // --------------------------------------------------------------------

    @Test
    public void void_request_mismatch_no_callback() {

        // only match specific request
        VoidRequest request = sampleReq();
        VoidRequest different = new VoidRequest(
                "X", 1, "CARD", "100", "u",
                1, "t", "s", 9, 0, 10L
        );

        doAnswer(inv -> {
            capturedCb = inv.getArgument(1);
            return null;
        }).when(mockRepo).voidTransaction(eq(different), any());

        vm.voidPayment(request);

        // callback never triggered → still PROCESSING
        assertEquals(VoidState.PROCESSING, vm.state.getValue());
        assertNull(vm.res1.getValue());
    }
}
