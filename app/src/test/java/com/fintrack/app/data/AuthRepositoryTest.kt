package com.fintrack.app.data

import com.fintrack.app.data.network.ApiResponse
import com.fintrack.app.data.request.LoginRequest
import com.fintrack.app.data.request.RegisterRequest
import com.fintrack.app.data.response.BaseResponse
import com.fintrack.app.data.response.LoginResponse
import com.fintrack.app.data.response.User
import com.fintrack.app.service.AuthApiService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepositoryTest {

    // --- Mocks ---
    @Mock private lateinit var firebaseAuth: FirebaseAuth
    @Mock private lateinit var authResultTask: Task<AuthResult>
    @Mock private lateinit var createUserTask: Task<AuthResult>
    @Mock private lateinit var authResult: AuthResult
    @Mock private lateinit var firebaseUser: FirebaseUser
    @Mock private lateinit var idTokenTask: Task<GetTokenResult>
    @Mock private lateinit var getTokenResult: GetTokenResult

    // --- Test Components ---
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: AuthApiService
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("http://192.168.100.7:3000"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)

        repository = AuthRepository(apiService, firebaseAuth)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // =================================================================
    // == PENGUJIAN UNTUK SET NEW PASSWORD ==
    // =================================================================
    @Test
    fun `setNewPassword success - should emit Loading and then Success`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val newPassword = "newPassword123"

        // 1. Siapkan response sukses dari server
        val expectedResponse = BaseResponse("Password reset successfully")
        val responseJson = Gson().toJson(expectedResponse)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        // ACT
        val results = repository.setNewPassword(email, newPassword).toList()

        // ASSERT
        // Verifikasi alur state
        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResponse.Loading)
        assertTrue(results[1] is ApiResponse.Success)

        // Verifikasi data response
        val successData = (results[1] as ApiResponse.Success).data
        assertEquals("Password reset successfully", successData.message)

        // Verifikasi request yang dikirim ke server
        val request = mockWebServer.takeRequest()
        val requestBody = JSONObject(request.body.readUtf8())
        assertEquals("/api/auth/set-new-password", request.path)
        assertEquals(email, requestBody.getString("email"))
        assertEquals(newPassword, requestBody.getString("new_password"))
    }

    @Test
    fun `setNewPassword failed - api returns error`() = runTest {
        // ARRANGE
        val email = "test@example.com"
        val newPassword = "newPassword123"

        // 1. Siapkan response error dari server
        val errorJson = "{\"message\":\"Invalid session or token\"}"
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody(errorJson))

        // ACT
        val results = repository.setNewPassword(email, newPassword).toList()

        // ASSERT
        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResponse.Loading)
        assertTrue(results[1] is ApiResponse.Error)

        val errorData = (results[1] as ApiResponse.Error)
        assertEquals("Invalid session or token", errorData.errorMessage)
    }


    // =================================================================
    // == TES LAINNYA (LOGIN, REGISTER, DLL.) ==
    // =================================================================

    @Test
    fun `register success - should emit Loading and then Success`() = runTest {
        val registerRequest = RegisterRequest("New User", "mindnotfound5@gmail.com", "password123")
        whenever(firebaseAuth.createUserWithEmailAndPassword(registerRequest.email, registerRequest.password)).thenReturn(createUserTask)
        whenever(createUserTask.isSuccessful).thenReturn(true)
        whenever(createUserTask.isComplete).thenReturn(true)
        whenever(createUserTask.result).thenReturn(authResult)
        whenever(authResult.user).thenReturn(firebaseUser)

        val expectedResponse = BaseResponse("User registered successfully")
        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(Gson().toJson(expectedResponse)))

        val results = repository.register(registerRequest).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResponse.Loading)
        assertTrue(results[1] is ApiResponse.Success)
    }

    @Test
    fun `login success - should emit Loading and then Success`() = runTest {
        val loginRequest = LoginRequest("test@example.com", "password123")

        whenever(firebaseAuth.signInWithEmailAndPassword(loginRequest.email, loginRequest.password)).thenReturn(authResultTask)
        whenever(authResultTask.isSuccessful).thenReturn(true)
        whenever(authResultTask.isComplete).thenReturn(true)
        whenever(authResultTask.result).thenReturn(authResult)
        whenever(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseUser.isEmailVerified).thenReturn(true)
        whenever(firebaseUser.getIdToken(true)).thenReturn(idTokenTask)
        whenever(idTokenTask.isSuccessful).thenReturn(true)
        whenever(idTokenTask.isComplete).thenReturn(true)
        whenever(idTokenTask.result).thenReturn(getTokenResult)
        whenever(getTokenResult.token).thenReturn("dummy_token")

        val expectedUser = User("uid123", "Test User", "test@example.com", "email", true)
        val expectedResponse = LoginResponse("Login successful", expectedUser)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(Gson().toJson(expectedResponse)))

        val results = repository.login(loginRequest).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResponse.Loading)
        assertTrue(results[1] is ApiResponse.Success)
    }
}
