package com.example.storyapp.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapp.TestCoroutineRule
import com.example.storyapp.ViewModel.ViewModelLogin
import com.example.storyapp.data.UserModel
import com.example.storyapp.data.UserPreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ViewModelLoginTest{

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var userPreference: UserPreference

    @Mock
    private lateinit var loginViewModel: ViewModelLogin

    @Before
    fun setUp() {
        loginViewModel = ViewModelLogin(userPreference)
    }


    @Test
    fun `when login called should not throw error`() = testCoroutineRule.runBlockingTest {
        loginViewModel.login()
        Mockito.verify(userPreference).login()
    }

    @Test
    fun `when saveUser called should not throw error`() = testCoroutineRule.runBlockingTest {
        loginViewModel.saveUser(UserModel(token = "dummyToken", isLogin = true))
        Mockito.verify(userPreference).saveUser(UserModel(token = "dummyToken", isLogin = true))
    }

}