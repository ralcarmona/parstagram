package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File

/**
 * let user create a post
 * */

class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //1.setting the description of the post
        //2.a button to launch the camera
        //3.an image view to show the picture the user has taken
        //4.a button to send and post to our parse server
        findViewById<Button>(R.id.btnSubmit).setOnClickListener{
            val description = findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null)
            {
                submitPost(description, user, photoFile!!)
            }else{
                // TODO:
            }

        }
        findViewById<Button>(R.id.btnTakePicture).setOnClickListener{
            onLaunchCamera()
        }
        //queryPosts()
    }
    //sends object to our parser
    fun submitPost(description : String, user: ParseUser, file: File){
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground{ exception ->
            if (exception != null ){
                Log.e(TAG, "There was an error saving Post")
                exception.printStackTrace()
                Toast.makeText(this, "Error Saving your Post", Toast.LENGTH_SHORT).show()
            }else {
                Log.e(TAG, "Successfully saved Post")
                //TODO: Reset description field
                //TODO: reset image Preview
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i(TAG, "request code : $requestCode")
            if (resultCode == RESULT_OK) {
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            } else {
                Toast.makeText(this, "Picture wasnt taken", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    //query for all the post
    fun queryPosts(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        //find all Post objects
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null){
                    //something as gone wrong
                    Log.e(TAG,"Error fetching post")
                }else{
                    if (posts != null){
                        for (post in posts){
                            Log.i(TAG,"Post " + post.getDescription() + " username: " + post.getUser()?.username)
                        }
                    }
                }
            }

        })
    }
    companion object{
        const val TAG = "Main activity"
    }

}