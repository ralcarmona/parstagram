package com.example.parstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

/**
 * let user create a post
 * */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        queryPosts()
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