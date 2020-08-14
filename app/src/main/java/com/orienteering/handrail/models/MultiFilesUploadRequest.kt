package com.orienteering.handrail.models

import okhttp3.MultipartBody

class MultiFilesUploadRequest(positions: MutableList<Int>, files : MutableList<MultipartBody.Part>) {

    var positions :MutableList<Int>
    var files : MutableList<MultipartBody.Part>

    init{
        this.positions=positions
        this.files=files
    }
}