package arnodenhond.astroclocklite

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        setContentView(R.layout.activity_main)
        doClock()
    }

    private fun doClock() {
        val iv : ImageView = findViewById(R.id.clock)
        val sharedPref = getPreferences( Context.MODE_PRIVATE)
        val latitude: Double = sharedPref.getFloat("lat",0f).toDouble()
        val longitude: Double = sharedPref.getFloat("lon",0f).toDouble()
        iv.setImageBitmap(null)
        iv.setImageBitmap(
            BitmapMaker(
                this@MainActivity,
                800,
                latitude,
                longitude,
                0
            ).makeBitmap()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val mi = MenuInflater(this)
        mi.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title!!.equals("Location")) {
            showDialog(1);
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateDialog(id: Int): Dialog {
        val  builder : AlertDialog.Builder = AlertDialog.Builder(this)
        val layout : View = View.inflate(this, R.layout.location, null )
//        val latET : EditText = layout.findViewById(R.id.latet);
//        val lonET : EditText = layout.findViewById(R.id.lonet);
        val locationET : EditText = layout.findViewById(R.id.location)
        val sharedPref = getPreferences( Context.MODE_PRIVATE)
//        latET.setText(sharedPref.getFloat("lat",0f).toString())
//        lonET.setText(sharedPref.getFloat("lon",0f).toString())
        builder.setTitle("Location")
        builder.setView(layout);
        val gc : Geocoder = Geocoder(this)
        val ctx : Context = this;
        builder.setNeutralButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
            val list = gc.getFromLocationName(locationET.text.toString(),1)

            if (list?.size==1) {
                val address : Address? = list?.get(0)
                Toast.makeText(ctx,"Latitude: "+address?.latitude + ", Longitude: "+address?.longitude,Toast.LENGTH_SHORT).show()
                with(sharedPref.edit()) {
                    putFloat("lat", address?.latitude!!.toFloat());
                    putFloat("lon", address?.longitude!!.toFloat());
//                putFloat("lat", latET.text.toString().toFloat())
//                putFloat("lon", lonET.text.toString().toFloat())
                    apply()
                }
                doClock()
            } else {
                Toast.makeText(ctx, "not found", Toast.LENGTH_SHORT).show()
            }
        }
        )
        return builder.create();
        //return super.onCreateDialog(id)
    }

    private fun hideSystemBars() {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

//        val windowInsetsController =
//            ViewCompat.getWindowInsetsController(window.decorView) ?: return
//        // Configure the behavior of the hidden system bars
//        windowInsetsController.systemBarsBehavior =
//            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        // Hide both the status bar and the navigation bar
//        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
    }
}