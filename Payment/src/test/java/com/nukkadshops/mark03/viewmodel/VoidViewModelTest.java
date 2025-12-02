package com.nukkadshops.mark03.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.nukkadshops.mark03.models.VoidRequest;
import com.nukkadshops.mark03.models.VoidResponse;
import com.nukkadshops.mark03.repository.PaymentRepository;
import com.nukkadshops.mark03.sdk.PaymentConfig;
import com.nukkadshops.mark03.sdk.VoidState;

public class VoidViewModelTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private VoidViewModel vm;
    private PaymentRepository mockRepo;
    private Call<VoidResponse> mockCall;

    // ⭐ Added missing callback holder
    private Callback<VoidResponse> capturedCallback;

    @Before
    public void setup() throws Exception {

        // Mock PaymentConfig = prevents Retrofit crash
        PaymentConfig cfg = mock(PaymentConfig.class);
        when(cfg.getBaseUrl()).thenReturn("https://dummy/");
        when(cfg.getMerchantId()).thenReturn(101);
        when(cfg.getClientId()).thenReturn(99);
        when(cfg.getStoreId()).thenReturn("store1");
        when(cfg.getSecurityToken()).thenReturn("token");
        when(cfg.getUserId()).thenReturn("user1");
        when(cfg.getTimeoutInSeconds()).thenReturn(5);

        vm = new VoidViewModel(cfg);

        // Mock Repository
        mockRepo = mock(PaymentRepository.class);
        Field repoField = VoidViewModel.class.getDeclaredField("repo");
        repoField.setAccessible(true);
        repoField.set(vm, mockRepo);

        mockCall = mock(Call.class);
    }

    // ----------------------------------------------------------
    // SUCCESS CASE
    // ----------------------------------------------------------
    @Test
    public void test_void_success() {

        VoidRequest req = new VoidRequest(
                "TN1", 1, "CARD", "100.00",
                "user1", 101, "token",
                "store1", 99, 1, 123L
        );

        VoidResponse mockResp = new VoidResponse();

        // Capture callback BUT do not trigger it yet
        doAnswer(inv -> {
            capturedCallback = inv.getArgument(1);
            return null;
        }).when(mockRepo).voidTransaction(eq(req), any());

        // Call ViewModel method
        vm.voidPayment(req);

        // Check initial PROCESSING state
        assertEquals(VoidState.PROCESSING, vm.state.getValue());

        // Now manually trigger repository success
        capturedCallback.onResponse(mockCall, Response.success(mockResp));

        // Final assertions after success
        assertEquals(mockResp, vm.res1.getValue());
        assertEquals(VoidState.APPROVED, vm.state.getValue());
    }

    // ----------------------------------------------------------
    // FAILURE CASE
    // ----------------------------------------------------------
    @Test
    public void test_void_failure() {

        VoidRequest req = new VoidRequest(
                "TN1", 1, "CARD", "100.00",
                "user1", 101, "token",
                "store1", 99, 1, 123L
        );

        doAnswer(inv -> {
            Callback<VoidResponse> cb = inv.getArgument(1);
            cb.onFailure(mockCall, new Throwable("Network error"));
            return null;
        }).when(mockRepo).voidTransaction(eq(req), any());

        vm.voidPayment(req);

        assertEquals(VoidState.ERROR, vm.state.getValue());
    }
}
