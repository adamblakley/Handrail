package com.orienteering.handrail.edit_profile

import android.net.Uri
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.UserInteractor
import com.orienteering.handrail.models.User
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.image_utilities.ImageSelect
import com.orienteering.handrail.image_utilities.MultipartBodyFactory
import com.orienteering.handrail.permissions.PermissionManager
import retrofit2.Response
import java.text.SimpleDateFormat

/**
 * Presenter class implements logic for retieval and update of user profile information. Handles data binding.
 *
 * @constructor
 *
 * @param editProfileView
 * @param userInteractor
 * @param imageSelect
 */
class EditProfilePresenter(editProfileView : IEditProfileContract.IEditProfileView, userInteractor: UserInteractor, imageSelect: ImageSelect) : IEditProfileContract.IEditProfilePresenter {

    private var editProfileView : IEditProfileContract.IEditProfileView?
    private var userInteractor : UserInteractor
    private var multipartBodyFactory : MultipartBodyFactory
    var imageSelect : ImageSelect
    private var getEditUserOnFinishedListener : GetEditUserOnFinishedListener
    private var putEditUserOnFinsihedListener : PutEditUserOnFinishedListener
    // user for upload
    private lateinit var myUser : User
    // image uri to associate to user profile picture
    var imageUri : Uri? = null

    /**
     * Initiate view, interactor, multipartbody factory, imageselect and onfinished listeners
     */
    init{
        this.editProfileView = editProfileView
        this.userInteractor = userInteractor
        this.imageSelect = imageSelect
        this.getEditUserOnFinishedListener = GetEditUserOnFinishedListener(this, editProfileView)
        this.putEditUserOnFinsihedListener = PutEditUserOnFinishedListener(this,editProfileView)
        this.multipartBodyFactory = MultipartBodyFactory(imageSelect)
    }

    override fun onDestroy() {
        this.editProfileView=null
    }

    /**
     * Create user and utilize multipartbody factory to create multipartbody.part to send as user profile
     * utilize user interactor to put user data on server, determine if profile photo requires update and if so, request appropriate function
     * @param user
     */
    override fun putDataOnServer(user : User) {
        if (user!=null) {
            if (user.userDob.equals("dummy")){
                user.userDob=myUser.userDob
            }
            if (imageUri != null) {
                val imageMultipartBodyPart = imageUri?.let { multipartBodyFactory.createImageMultipartBody(imageSelect.activity, it,user.userFirstName) }
                userInteractor.update(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), user, imageMultipartBodyPart, putEditUserOnFinsihedListener)
            } else {
                userInteractor.update(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), user, putEditUserOnFinsihedListener)
            }
        }
    }

    /**
     * Use interactor to retreive user from server
     *
     */
    override fun getDataFromServer() {
        userInteractor.read(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0),getEditUserOnFinishedListener)
    }

    override fun selectImage() {
        if (PermissionManager.checkPermission(imageSelect.activity, imageSelect.context,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PermissionManager.MULTIPLE_REQUEST_CODES
            )
        ) {
            imageUri=imageSelect.selectImage()
        }
    }

    override fun setUser(user: User) {
        this.myUser=user
    }

    override fun setImage(imageUri: Uri) {
        this.imageUri=imageUri
    }
}

/**
 * on finished listener for getting user information, determines if result is successful, binds user and calls presenter information
 * on failure results in error handling by view
 * @constructor
 *
 * @param editProfilePresenter
 * @param editProfileView
 */
class GetEditUserOnFinishedListener(editProfilePresenter : IEditProfileContract.IEditProfilePresenter, editProfileView : IEditProfileContract.IEditProfileView) :
    IOnFinishedListener<User> {

    lateinit var user : User
    private var editProfileView : IEditProfileContract.IEditProfileView = editProfileView
    private var editProfilePresenter : IEditProfileContract.IEditProfilePresenter = editProfilePresenter

    override fun onFinished(response: Response<StatusResponseEntity<User>>) {

        // create date string from user dob pulled from user class inside body
        if(response.isSuccessful){
            if (response.body() != null) {
                user = response.body()!!.entity!!
                editProfilePresenter.setUser(user)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                val dateformatted = sdf.parse(user.userDob)
                val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
                val date: String = dateFormatter.format(dateformatted)
                // fill view information with user data
                editProfileView.fillInformation(user.userFirstName,user.userLastName,user.userEmail,user.userBio,date)
                // determine active photo by searching for active==true
                for (photo in user.userPhotographs!!){
                    if (photo.active==true){
                        editProfileView.setupImage(photo.photoPath)
                        break
                    }
                }
            } else {
                editProfileView.onGetResponseError()
            }
        } else {
            editProfileView.onGetResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (editProfileView!=null){
            editProfileView.onGetResponseFailure(t)
        }
    }
}

/**
 * on finished listener handles update of user response, on success relays to view.
 * on failure or error, determines correct view method to inform user
 *
 * @constructor
 *
 * @param editProfilePresenter
 * @param editProfileView
 */
class PutEditUserOnFinishedListener(editProfilePresenter : IEditProfileContract.IEditProfilePresenter, editProfileView : IEditProfileContract.IEditProfileView) :
    IOnFinishedListener<User> {

    lateinit var user : User
    private var editProfileView : IEditProfileContract.IEditProfileView = editProfileView
    private var editProfilePresenter : IEditProfileContract.IEditProfilePresenter = editProfilePresenter

    override fun onFinished(response: Response<StatusResponseEntity<User>>) {

        if(response.isSuccessful){
            if (response.body() != null) {
                editProfileView.onUpdateResponseSuccess()
            } else {
                editProfileView.onUpdateResponseError()
            }
            // if code is 206, only image upload has failed. other data update sucessful.
        } else  if (response.code()==206){
            editProfileView.onUpdatePartialResponseError()
        } else {
            editProfileView.onUpdateResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        editProfileView.onUpdateResponseFailure(t)
    }
}