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

    // --- Mocks untuk Firebase dan Tasks ---
    @Mock private lateinit var firebaseAuth: FirebaseAuth
    @Mock private lateinit var authResultTask: Task<AuthResult>
    @Mock private lateinit var createUserTask: Task<AuthResult>
    @Mock private lateinit var authResult: AuthResult
    @Mock private lateinit var firebaseUser: FirebaseUser
    @Mock private lateinit var idTokenTask: Task<GetTokenResult>
    @Mock private lateinit var getTokenResult: GetTokenResult

    // --- Komponen untuk diuji ---
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: AuthApiService
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
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
    // == PENGUJIAN UNTUK REGISTRASI (SIGN UP) ==
    // =================================================================

    @Test
    fun `register success - should emit Loading and then Success`() = runTest {
        // ARRANGE
        val registerRequest = RegisterRequest("New User", "new@example.com", "password123")

        // 1. Mock panggilan Firebase untuk membuat user (berhasil)
        whenever(firebaseAuth.createUserWithEmailAndPassword(registerRequest.email, registerRequest.password))
            .thenReturn(createUserTask)
        whenever(createUserTask.isSuccessful).thenReturn(true)
        whenever(createUserTask.isComplete).thenReturn(true)
        whenever(createUserTask.result).thenReturn(authResult)
        whenever(authResult.user).thenReturn(firebaseUser) // Pastikan user tidak null

        // 2. Mock panggilan API ke backend Anda (berhasil)
        val expectedResponse = BaseResponse("User registered successfully")
        val responseJson = Gson().toJson(expectedResponse)
        mockWebServer.enqueue(MockResponse().setResponseCode(201).setBody(responseJson))

        // ACT
        val results = repository.register(registerRequest).toList()

        // ASSERT
        // Verifikasi urutan state: Loading -> Success
        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResponse.Loading)
        assertTrue(results[1] is ApiResponse.Success)

        // Verifikasi isi data sukses
        val successData = (results[1] as ApiResponse.Success).data
        assertEquals("User registered successfully", successData.message)

        // Verifikasi bahwa panggilan ke backend sudah benar
        val request = mockWebServer.takeRequest()
        assertEquals("/api/auth/register", request.path)
    }

    @Test
    fun `register failed - firebase throws UserCollisionException for existing email`() = runTest {
        // ARRANGE
        val registerRequest = RegisterRequest("Existing User", "test@example.com", "password123")
        val errorMessage = "The email address is already in use by another account."
        val exception = FirebaseAuthUserCollisionException("ERROR_EMAIL_ALREADY_IN_USE", errorMessage)

        // 1. Mock panggilan Firebase (gagal karena email sudah ada)
        whenever(firebaseAuth.createUserWithEmailAndPassword(registerRequest.email, registerRequest.password))
            .thenThrow(exception)

        // ACT
        val results = repository.register(registerRequest).toList()

        // ASSERT
        // Verifikasi urutan state: Loading -> Error
        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResponse.Loading)
        assertTrue(results[1] is ApiResponse.Error)

        // Verifikasi pesan error
        val errorResult = (results[1] as ApiResponse.Error).errorMessage
        assertEquals(errorMessage, errorResult)
    }


    // =================================================================
    // == PENGUJIAN UNTUK LOGIN (SUDAH ADA SEBELUMNYA) ==
    // =================================================================

    @Test
    fun `login success - should emit Loading and then Success`() = runTest {
        // ARRANGE
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
        val responseJson = Gson().toJson(expectedResponse)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(responseJson))

        // ACT
        val results = repository.login(loginRequest).toList()

        // ASSERT
        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResponse.Loading)
        assertTrue(results[1] is ApiResponse.Success)
    }
}
