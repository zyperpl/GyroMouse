package pl.zyper.gyroscopemouse

import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_hub.*

class HubActivity : AppCompatActivity(), ServerScanner.Callback, IPDialogFragment.Listener {

    private var servers : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)

        scan()

        swipeRefresh.setOnRefreshListener { scan() }

        serverList.adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, servers)
        serverList.setOnItemClickListener { _, _, position, _ -> AppActivity.start(this, servers[position]) }

    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(
                    context,
                    HubActivity::class.java
            )
            context.startActivity(intent)
        }
    }

    fun scan() {
        System.out.println("Scanning...")
        ServerScanner(this).execute()
        swipeRefresh.isRefreshing = true
    }

    private fun addIP(ip: String) {
        servers.add(ip)

        (serverList.adapter as BaseAdapter).notifyDataSetChanged()

        if (noServersText?.parent != null) {
            (noServersText?.parent as ViewGroup).removeView(noServersText)
        }
    }

    override fun onNewData(result: String?) {

        if (result == null) {
            swipeRefresh.isRefreshing = false
            Toast.makeText(
                    this,
                    getString(R.string.scan_complete_msg) + " " + getString(R.string.found_msg) + ": " + servers.size,
                    Toast.LENGTH_SHORT
            ).show()
            if (servers.isNotEmpty()) {
                if (noServersText?.parent != null) {
                    (noServersText?.parent as ViewGroup).removeView(noServersText)
                }

            }
        } else {
            System.out.println(getString(R.string.found_msg) + ": " + result)
            if (servers.indexOf(result) == -1) {
                runOnUiThread({
                    addIP(result)
                })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.hub_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        System.out.println("Clicked")
        when (item?.itemId) {
            R.id.menuScan -> {
                scan()
                return true
            }
            R.id.menuAddIP -> {
                IPDialogFragment().show(fragmentManager, IPDialogFragment.tag)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAddIP(dialog: DialogFragment, ip: String) {
        //TODO scan if host really exists
        addIP(ip)
        (serverList.adapter as BaseAdapter).notifyDataSetChanged()
    }

    override fun onCancelIP(dialog: DialogFragment) {
    }
}