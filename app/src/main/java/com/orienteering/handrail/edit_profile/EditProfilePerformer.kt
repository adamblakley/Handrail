package com.orienteering.handrail.edit_profile

import android.net.Uri
import com.orienteering.handrail.httprequests.IOnFinishedListener
import com.orienteering.handrail.httprequests.StatusResponseEntity
import com.orienteering.handrail.interactors.UserInteractor
import com.orienteering.handrail.models.User
import com.orienteering.handrail.utilities.App
import com.orienteering.handrail.utilities.ImageSelect
import com.orienteering.handrail.utilities.MultipartBodyFactory
import com.orienteering.handrail.utilities.PermissionManager
import retrofit2.Response
import java.text.SimpleDateFormat

class EditProfilePerformer(editProfileView : IEditProfileContract.IEditProfileView, userInteractor: UserInteractor, imageSelect: ImageSelect) : IEditProfileContract.IEditProfilePerformer {

    var editProfileView : IEditProfileContract.IEditProfileView?
    var userInteractor : UserInteractor
    var multipartBodyFactory : MultipartBodyFactory
    var imageSelect : ImageSelect
    var getEditUserOnFinishedListener : GetEditUserOnFinishedListener
    var putEditUserOnFinsihedListener : PutEditUserOnFinishedListener

    lateinit var myUser : User
    var imageUri : Uri? = null

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

    override fun putDataOnServer(user : User) {
        if (user!=null) {
            if (user.userDob.equals("dummy")){
                user.userDob=myUser.userDob
            }
            if (imageUri != null) {
                val imageMultipartBodyPart = imageUri?.let { multipartBodyFactory.createImageMultipartBody(imageSelect.activity, it) }
                userInteractor.update(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), user, imageMultipartBodyPart, putEditUserOnFinsihedListener)
            } else {
                userInteractor.update(App.sharedPreferences.getLong(App.SharedPreferencesUserId, 0), user, putEditUserOnFinsihedListener)
            }
        }
    }

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

class GetEditUserOnFinishedListener(editProfilePerformer : IEditProfileContract.IEditProfilePerformer, editProfileView : IEditProfileContract.IEditProfileView) : IOnFinishedListener<User> {

    lateinit var user : User
    private var editProfileView : IEditProfileContract.IEditProfileView
    private var editProfilePerformer : IEditProfileContract.IEditProfilePerformer

    init{
        this.editProfileView = editProfileView
        this.editProfilePerformer = editProfilePerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<User>>) {

        if(response.isSuccessful){
            if (response.body() != null) {
                user = response.body()!!.entity!!
                editProfilePerformer.setUser(user)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                val dateformatted = sdf.parse(user.userDob)
                val dateFormatter = SimpleDateFormat("dd-MM-YYYY")
                val date: String = dateFormatter.format(dateformatted)

                editProfileView.fillInformation(user.userFirstName,user.userLastName,user.userEmail,user.userBio,date)

                for (photo in user.userPhotographs!!){
                    if (photo.active==true){
                        editProfileView.setupImage(photo.photoPath)
                        break
                    }
                }
            } else {
                editProfileView?.onGetResponseError()
            }
        } else {
            editProfileView?.onGetResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (editProfileView!=null){
            editProfileView?.onGetResponseFailure(t)
        }
    }
}

class PutEditUserOnFinishedListener(editProfilePerformer : IEditProfileContract.IEditProfilePerformer, editProfileView : IEditProfileContract.IEditProfileView) : IOnFinishedListener<User> {

    lateinit var user : User
    private var editProfileView : IEditProfileContract.IEditProfileView
    private var editProfilePerformer : IEditProfileContract.IEditProfilePerformer

    init{
        this.editProfileView = editProfileView
        this.editProfilePerformer = editProfilePerformer
    }

    override fun onFinished(response: Response<StatusResponseEntity<User>>) {

        if(response.isSuccessful){
            if (response.body() != null) {
                editProfileView.onUpdateResponseSuccess()
            } else {
                editProfileView.onUpdateResponseError()
            }
        } else  if (response.code()==206){
                editProfileView?.onUpdatePartialResponseError()
        } else {
                editProfileView?.onUpdateResponseError()
        }
    }

    override fun onFailure(t: Throwable) {
        if (editProfileView!=null){
            editProfileView.onUpdateResponseFailure(t)
        }
    }
}