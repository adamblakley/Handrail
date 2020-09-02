package com.orienteering.handrail.services


import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.models.PasswordUpdateRequest
import com.orienteering.handrail.models.User
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import junit.framework.Assert.*
import org.junit.Test
import org.junit.After
import org.junit.Before
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates


/**
 * Class test for UserService
 *
 */
internal class UserServiceTest {


    lateinit var mockWebServer : MockWebServer
    lateinit var serviceBuilder : Retrofit
    lateinit var service : UserService
    var sendResponseTime by Delegates.notNull<Long>()
    var receiveResponseTime by Delegates.notNull<Long>()

    /**
     * execute before, setup variables
     *
     */
    @Before
    fun setup(){
        mockWebServer = MockWebServer()
        mockWebServer.start()

        serviceBuilder = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/").toString())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = serviceBuilder.create(UserService::class.java)

        sendResponseTime  = 0
        receiveResponseTime  = 5001
    }

    /**
     * cleanup, retire mockwebserver
     *
     */
    @After
    fun cleanup(){
        mockWebServer.shutdown()
    }

    /**
     * Test UserService read()
     *
     */
    @Test
    fun read() {
        val response = MockResponse().setResponseCode(200).setBody(body)
        mockWebServer.enqueue(response)
        lateinit var actual : StatusResponseEntity<User>

        val request = service.read(1)
        var user = User("Email","FirstName","LastName","2020-06-19T14:27:28.054+00:00","Bio")
        var expected = StatusResponseEntity<User>(true,"User Find Successful",user)
        user.userId=1

        request.enqueue(object : Callback<StatusResponseEntity<User>>{
            override fun onFailure(call: Call<StatusResponseEntity<User>>, t: Throwable) {
                assertFalse(false)
            }
            override fun onResponse(call: Call<StatusResponseEntity<User>>, response: Response<StatusResponseEntity<User>>) {
                actual = response.body()!!
                assertEquals(expected.entity?.userId, actual.entity?.userId)
                assertEquals(expected.entity?.userFirstName, actual.entity?.userFirstName)
                assertEquals(expected.entity?.userLastName, actual.entity?.userLastName)
                assertEquals(expected.entity?.userEmail, actual.entity?.userEmail)
                assertEquals(expected.entity?.userDob, actual.entity?.userDob)
                assertEquals(expected.entity?.userBio, actual.entity?.userBio)
            }
        })
    }

    /**
     * test userservice update
     *
     */
    @Test
    fun update(){
        val response = MockResponse().setResponseCode(200).setBody(body2)
        mockWebServer.enqueue(response)
        lateinit var actual : StatusResponseEntity<User>
        val user = User("Email2","FirstName2","LastName2","2019-06-19T14:27:28.054+00:00","Bio2")
        val request = service.update(1,user)
        var expected = StatusResponseEntity<User>(true,"User Find Successful",user)
        request.enqueue(object : Callback<StatusResponseEntity<User>>{
            override fun onFailure(call: Call<StatusResponseEntity<User>>, t: Throwable) {
                assertFalse(false)
            }
            override fun onResponse(call: Call<StatusResponseEntity<User>>, response: Response<StatusResponseEntity<User>>) {
                actual = response.body()!!
                assertEquals(expected.entity?.userId, actual.entity?.userId)
                assertEquals(expected.entity?.userFirstName, actual.entity?.userFirstName)
                assertEquals(expected.entity?.userLastName, actual.entity?.userLastName)
                assertEquals(expected.entity?.userEmail, actual.entity?.userEmail)
                assertEquals(expected.entity?.userDob, actual.entity?.userDob)
                assertEquals(expected.entity?.userBio, actual.entity?.userBio)
            }
        })

    }


    /**
     * test userservice update password
     *
     */
    @Test
    fun updatePassword(){
        val response = MockResponse().setResponseCode(200).setBody(body2)
        mockWebServer.enqueue(response)
        lateinit var actual : StatusResponseEntity<User>
        val user = User("Email","FirstName","LastName","2020-06-19T14:27:28.054+00:00","Bio")
        val pwupdaterquest = PasswordUpdateRequest("password","password2")
        val request = service.updatePassword(1,pwupdaterquest)
        var expected = StatusResponseEntity<User>(true,"User Find Successful",user)
        request.enqueue(object : Callback<StatusResponseEntity<User>>{
            override fun onFailure(call: Call<StatusResponseEntity<User>>, t: Throwable) {
                assertFalse(false)
            }
            override fun onResponse(call: Call<StatusResponseEntity<User>>, response: Response<StatusResponseEntity<User>>) {
                actual = response.body()!!
                assertEquals(expected.entity?.userId, actual.entity?.userId)
                assertEquals(expected.entity?.userFirstName, actual.entity?.userFirstName)
                assertEquals(expected.entity?.userLastName, actual.entity?.userLastName)
                assertEquals(expected.entity?.userEmail, actual.entity?.userEmail)
                assertEquals(expected.entity?.userDob, actual.entity?.userDob)
                assertEquals(expected.entity?.userBio, actual.entity?.userBio)
            }
        })
    }

    @Test
    fun updatePasswordTime(){
        val myService = ServiceFactory.makeService(UserService::class.java)
        val pwupdaterquest = PasswordUpdateRequest("password","password2")
        val request = myService.updatePassword(1,pwupdaterquest)

        request.enqueue(object : Callback<StatusResponseEntity<User>>{
            override fun onFailure(call: Call<StatusResponseEntity<User>>, t: Throwable) {
                assertFalse(true)
            }
            override fun onResponse(call: Call<StatusResponseEntity<User>>, response: Response<StatusResponseEntity<User>>) {
                sendResponseTime = response.raw().sentRequestAtMillis
                receiveResponseTime = response.raw().receivedResponseAtMillis
                val actualTime = receiveResponseTime-sendResponseTime
                assertTrue(actualTime<=5000)
            }
        })
    }

    @Test
    fun updateTime(){
        val myService = ServiceFactory.makeService(UserService::class.java)
        val user = User("Email2","FirstName2","LastName2","2019-06-19T14:27:28.054+00:00","Bio2")

        val request = myService.update(1,user)
        request.enqueue(object : Callback<StatusResponseEntity<User>>{
            override fun onFailure(call: Call<StatusResponseEntity<User>>, t: Throwable) {
                assertFalse(true)
            }
            override fun onResponse(call: Call<StatusResponseEntity<User>>, response: Response<StatusResponseEntity<User>>) {
                sendResponseTime = response.raw().sentRequestAtMillis
                receiveResponseTime = response.raw().receivedResponseAtMillis
                val actualTime = receiveResponseTime-sendResponseTime
                assertTrue(actualTime<=5000)
            }
        })
    }

    @Test
    fun readTime(){
        val myService = ServiceFactory.makeService(UserService::class.java)

        val request = myService.read(1)
        request.enqueue(object : Callback<StatusResponseEntity<User>>{
            override fun onFailure(call: Call<StatusResponseEntity<User>>, t: Throwable) {
                assertFalse(true)
            }
            override fun onResponse(call: Call<StatusResponseEntity<User>>, response: Response<StatusResponseEntity<User>>) {
                sendResponseTime = response.raw().sentRequestAtMillis
                receiveResponseTime = response.raw().receivedResponseAtMillis
                val actualTime = receiveResponseTime-sendResponseTime
                assertTrue(actualTime<=5000)
            }
        })
    }

    // JSON variables
    val body = "{\n" +
            "    \"status\": true,\n" +
            "    \"message\": \"User Find Successful\",\n" +
            "    \"entity\": {\n" +
            "        \"userId\": 1,\n" +
            "        \"userEmail\": \"Email\",\n" +
            "        \"userFirstName\": \"FirstName\",\n" +
            "        \"userLastName\": \"LastName\",\n" +
            "        \"userDob\": \"2020-06-19T14:27:28.054+00:00\",\n" +
            "        \"userBio\": \"Bio\",\n" +
            "        \"userPhotographs\": []\n" +
            "    }\n" +
            "}"

    val body2 = "{\n" +
            "    \"status\": true,\n" +
            "    \"message\": \"User Update Successful\",\n" +
            "    \"entity\": {\n" +
            "        \"userId\": 1,\n" +
            "        \"userEmail\": \"Email2\",\n" +
            "        \"userFirstName\": \"FirstName2\",\n" +
            "        \"userLastName\": \"LastName2\",\n" +
            "        \"userDob\": \"2019-06-19T14:27:28.054+00:00\",\n" +
            "        \"userBio\": \"Bio2\",\n" +
            "        \"userPhotographs\": []\n" +
            "    }\n" +
            "}"

}