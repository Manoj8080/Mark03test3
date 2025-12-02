package com.nukkadshops.mark03.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.nukkadshops.mark03.models.*;
import com.nukkadshops.mark03.network.ApiClient;
import com.nukkadshops.mark03.network.ApiService;
import com.nukkadshops.mark03.sdk.PaymentConfig;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Mockito.*;

public class PaymentRepositoryTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private PaymentRepository repository;
    private ApiService mockApiService;

    private Call<UploadResponse> mockUploadCall;
    private Call<StatusResponse> mockStatusCall;
    private Call<CancelResponse> mockCancelCall;
    private Call<VoidResponse> mockVoidCall;

    // -------------------------------------------------------
    //  SETUP
    // -------------------------------------------------------
    @Before
    public void setUp() throws Exception {

        PaymentConfig dummyConfig = mock(PaymentConfig.class);
        when(dummyConfig.getBaseUrl()).thenReturn("https://dummy.com/");
        when(dummyConfig.getTimeoutInSeconds()).thenReturn(10);

        repository = new PaymentRepository(dummyConfig);

        mockApiService = mock(ApiService.class);

        mockUploadCall = mock(Call.class);
        mockStatusCall = mock(Call.class);
        mockCancelCall = mock(Call.class);
        mockVoidCall = mock(Call.class);

        // Inject ApiService into repository
        Field field = PaymentRepository.class.getDeclaredField("apiService");
        field.setAccessible(true);
        field.set(repository, mockApiService);
    }

    // -------------------------------------------------------
    //  Constructor Exception Test
    // -------------------------------------------------------
    @Test(expected = RuntimeException.class)
    public void test_constructor_apiClient_exception() {
        PaymentConfig config = mock(PaymentConfig.class);

        try (MockedStatic<ApiClient> mocked = mockStatic(ApiClient.class)) {
            mocked.when(() -> ApiClient.getClient(config))
                    .thenThrow(new RuntimeException("Boom"));

            new PaymentRepository(config);
        }
    }

    // -------------------------------------------------------
    //  Upload Tests
    // -------------------------------------------------------
    @Test
    public void test_upload_success() {

        UploadRequest req = new UploadRequest("TN1", 1, "CARD", "100.00",
                "user1", 101, "token", "store1", 99, 15);

        Callback<UploadResponse> callback = mock(Callback.class);
        UploadResponse mockResponse = new UploadResponse();

        when(mockApiService.uploadResponseCall(req))
                .thenReturn(mockUploadCall);

        doAnswer(invocation -> {
            Callback<UploadResponse> cb = invocation.getArgument(0);
            cb.onResponse(mockUploadCall, Response.success(mockResponse));
            return null;
        }).when(mockUploadCall).enqueue(any());

        repository.upload(req, callback);

        verify(callback).onResponse(eq(mockUploadCall), any());
    }

    @Test
    public void test_upload_failure() {

        UploadRequest req = new UploadRequest("TN1", 1, "CARD", "100.00",
                "user1", 101, "token", "store1", 99, 15);

        Callback<UploadResponse> callback = mock(Callback.class);

        when(mockApiService.uploadResponseCall(req))
                .thenReturn(mockUploadCall);

        doAnswer(invocation -> {
            Callback<UploadResponse> cb = invocation.getArgument(0);
            cb.onFailure(mockUploadCall, new Throwable("Network error"));
            return null;
        }).when(mockUploadCall).enqueue(any());

        repository.upload(req, callback);

        verify(callback).onFailure(eq(mockUploadCall), any());
    }

    @Test(expected = NullPointerException.class)
    public void test_upload_null_call_object() {

        UploadRequest req = mock(UploadRequest.class);
        when(mockApiService.uploadResponseCall(req)).thenReturn(null);

        repository.upload(req, mock(Callback.class));
    }

    @Test
    public void test_upload_null_callback_should_not_crash() {

        UploadRequest req = mock(UploadRequest.class);

        when(mockApiService.uploadResponseCall(req))
                .thenReturn(mockUploadCall);

        doNothing().when(mockUploadCall).enqueue(null);

        repository.upload(req, null);
    }

    @Test
    public void test_upload_api_throws_before_enqueue() {

        UploadRequest req = mock(UploadRequest.class);
        Callback<UploadResponse> cb = mock(Callback.class);

        when(mockApiService.uploadResponseCall(req))
                .thenThrow(new RuntimeException("Crash"));

        try {
            repository.upload(req, cb);
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------
    //  Status Tests
    // -------------------------------------------------------
    @Test
    public void test_status_success() {

        StatusRequest req = new StatusRequest(101, "token", "store1", 99, 12345L);
        Callback<StatusResponse> callback = mock(Callback.class);

        StatusResponse mockResponse = new StatusResponse();

        when(mockApiService.statusResponseCall(req))
                .thenReturn(mockStatusCall);

        doAnswer(invocation -> {
            Callback<StatusResponse> cb = invocation.getArgument(0);
            cb.onResponse(mockStatusCall, Response.success(mockResponse));
            return null;
        }).when(mockStatusCall).enqueue(any());

        repository.status(req, callback);
        verify(callback).onResponse(eq(mockStatusCall), any());
    }

    @Test
    public void test_status_failure() {

        StatusRequest req = new StatusRequest(101, "token", "store1", 99, 12345L);
        Callback<StatusResponse> callback = mock(Callback.class);

        when(mockApiService.statusResponseCall(req))
                .thenReturn(mockStatusCall);

        doAnswer(invocation -> {
            Callback<StatusResponse> cb = invocation.getArgument(0);
            cb.onFailure(mockStatusCall, new Throwable("Error"));
            return null;
        }).when(mockStatusCall).enqueue(any());

        repository.status(req, callback);

        verify(callback).onFailure(eq(mockStatusCall), any());
    }

    @Test(expected = NullPointerException.class)
    public void test_status_null_call_object() {

        StatusRequest req = mock(StatusRequest.class);
        when(mockApiService.statusResponseCall(req)).thenReturn(null);

        repository.status(req, mock(Callback.class));
    }

    @Test
    public void test_status_api_throws_before_enqueue() {

        StatusRequest req = mock(StatusRequest.class);
        Callback<StatusResponse> cb = mock(Callback.class);

        when(mockApiService.statusResponseCall(req))
                .thenThrow(new RuntimeException("Crash"));

        try {
            repository.status(req, cb);
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------
    //  Cancel Tests
    // -------------------------------------------------------
    @Test
    public void test_cancel_success() {

        CancelRequest req = new CancelRequest("store1", 99, 101, "token",
                12345L, "100.00", true);

        Callback<CancelResponse> callback = mock(Callback.class);
        CancelResponse mockResponse = new CancelResponse();

        when(mockApiService.cancelResponseCall(req))
                .thenReturn(mockCancelCall);

        doAnswer(invocation -> {
            Callback<CancelResponse> cb = invocation.getArgument(0);
            cb.onResponse(mockCancelCall, Response.success(mockResponse));
            return null;
        }).when(mockCancelCall).enqueue(any());

        repository.cancel(req, callback);
        verify(callback).onResponse(eq(mockCancelCall), any());
    }

    @Test
    public void test_cancel_failure() {

        CancelRequest req = new CancelRequest("store1", 99, 101, "token",
                12345L, "100.00", true);

        Callback<CancelResponse> callback = mock(Callback.class);

        when(mockApiService.cancelResponseCall(req))
                .thenReturn(mockCancelCall);

        doAnswer(invocation -> {
            Callback<CancelResponse> cb = invocation.getArgument(0);
            cb.onFailure(mockCancelCall, new Throwable("Fail"));
            return null;
        }).when(mockCancelCall).enqueue(any());

        repository.cancel(req, callback);

        verify(callback).onFailure(eq(mockCancelCall), any());
    }

    @Test(expected = NullPointerException.class)
    public void test_cancel_null_call_object() {
        CancelRequest req = mock(CancelRequest.class);
        when(mockApiService.cancelResponseCall(req)).thenReturn(null);
        repository.cancel(req, mock(Callback.class));
    }

    @Test
    public void test_cancel_enqueue_throws() {

        CancelRequest req = mock(CancelRequest.class);
        Callback<CancelResponse> cb = mock(Callback.class);

        when(mockApiService.cancelResponseCall(req))
                .thenReturn(mockCancelCall);

        doThrow(new RuntimeException("enqueue error"))
                .when(mockCancelCall).enqueue(cb);

        try {
            repository.cancel(req, cb);
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------
    //  Void Transaction Tests
    // -------------------------------------------------------
    @Test
    public void test_void_success() {

        VoidRequest req = new VoidRequest("TN1", 1, "CARD", "100.00",
                "user1", 101, "token", "store1", 99, 1, 98765L);

        Callback<VoidResponse> callback = mock(Callback.class);
        VoidResponse mockResponse = new VoidResponse();

        when(mockApiService.voidResponseCall(req)).thenReturn(mockVoidCall);

        doAnswer(invocation -> {
            Callback<VoidResponse> cb = invocation.getArgument(0);
            cb.onResponse(mockVoidCall, Response.success(mockResponse));
            return null;
        }).when(mockVoidCall).enqueue(any());

        repository.voidTransaction(req, callback);

        verify(callback).onResponse(eq(mockVoidCall), any());
    }

    @Test
    public void test_void_failure() {

        VoidRequest req = new VoidRequest("TN1", 1, "CARD", "100.00",
                "user1", 101, "token", "store1", 99, 1, 98765L);

        Callback<VoidResponse> callback = mock(Callback.class);

        when(mockApiService.voidResponseCall(req)).thenReturn(mockVoidCall);

        doAnswer(invocation -> {
            Callback<VoidResponse> cb = invocation.getArgument(0);
            cb.onFailure(mockVoidCall, new Throwable("Error"));
            return null;
        }).when(mockVoidCall).enqueue(any());

        repository.voidTransaction(req, callback);

        verify(callback).onFailure(eq(mockVoidCall), any());
    }

    @Test(expected = NullPointerException.class)
    public void test_void_null_call_object() {
        VoidRequest req = mock(VoidRequest.class);
        when(mockApiService.voidResponseCall(req)).thenReturn(null);
        repository.voidTransaction(req, mock(Callback.class));
    }

    @Test
    public void test_void_enqueue_throws() {

        VoidRequest req = mock(VoidRequest.class);
        Callback<VoidResponse> cb = mock(Callback.class);

        when(mockApiService.voidResponseCall(req)).thenReturn(mockVoidCall);

        doThrow(new RuntimeException("enqueue error"))
                .when(mockVoidCall).enqueue(cb);

        try {
            repository.voidTransaction(req, cb);
        } catch (Exception ignored) {}
    }
}
