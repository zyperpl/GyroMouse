package pl.zyper.gyroscopemouse

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO: show logo and stuff
        startApp(null);
    }

    fun startApp(view: View?) {
        HubActivity.start(this)
    }
}
