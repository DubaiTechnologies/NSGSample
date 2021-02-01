package dt.demo.nsgsample

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.nsg.nsgdtlibrary.Classes.util.NSGIMapFragmentActivity

class MapActivity : AppCompatActivity(), NSGIMapFragmentActivity.FragmentToActivity {


    private var navigationFragment: NSGIMapFragmentActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNSG()
        listeners()
    }

    private fun setupNSG() {
        navigationFragment = NSGIMapFragmentActivity(
                Data.NSG_MAP_BASE_MAP_FILE,
                Data.sourceData,
                Data.destData,
                Data.routeCoordinates,
                Data.NSG_MAP_DEVIATION_BUFFER,
                Data.nsgUrl,
                Data.nsgAuthKey,
                Data.destCoordinates,
                false
            )

        navigationFragment?.let { navigationFragment ->
            replaceFragment(supportFragmentManager, navigationFragment, R.id.map_container, false)
        }

    }

    private fun listeners() {
        findViewById<Button>(R.id.stopBtn).setOnClickListener {
            stopMap()
        }
    }

    private fun stopMap() {
        navigationFragment?.stopNavigation() ?: showMessage("Couldn't stop navigation")
        finish()
    }

    protected fun replaceFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment, containerId: Int,
        shouldAddToBackStack: Boolean
    ) {
        val tag = fragment.javaClass.name
        val ft = fragmentManager.beginTransaction()
        if (fragmentManager.findFragmentByTag(tag) != null && fragmentManager.findFragmentByTag(tag)!!.isVisible) {
            return
        }
        try {
            if (shouldAddToBackStack)
                ft.addToBackStack(tag)
            else {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            ft.replace(containerId, fragment, tag)
                    .commit()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun communicate(comm: String?, alertType: Int): String {

        if (alertType == Data.MAP_READY) {
            // showMessage("Map loaded")
            if (navigationFragment != null)
            // showMessage("Starting navigation")
                navigationFragment?.startNavigation()
            return ""
        }

        return ""
    }

    override fun onDestroy() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        navigationFragment?.let {navigationFragment ->
            fragmentTransaction.remove(navigationFragment).commitNowAllowingStateLoss()
        }
        navigationFragment = null
        super.onDestroy()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

    }

}