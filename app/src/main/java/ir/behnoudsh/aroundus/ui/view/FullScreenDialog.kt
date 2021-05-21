package ir.behnoudsh.aroundus.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import ir.behnoudsh.aroundus.R
import ir.behnoudsh.aroundus.data.room.FoursquarePlace
import kotlinx.android.synthetic.main.dialog_place_details.*

class PlaceDetailsDialog(var model: FoursquarePlace) :
    DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.dialog_place_details, container, false)
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_close.setOnClickListener {
            dismiss()
        }
        webview.settings.setJavaScriptEnabled(true)

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }
        }
        webview.loadUrl(model.link!!)

        btn_map.setOnClickListener() {

            val urlAddress =
                "http://maps.google.com/maps?q=" + model.lat?.toDouble() + "," + model.long?.toDouble() + "(" + model.name + ")&iwloc=A&hl=es"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress))
            startActivity(intent)

        }

        val name = "نام مکان: " + model.name

        val address = "آدرس: " + model.address

        val distance = "فاصله از موقعیت فعلی شما " + model.distance + " متر"

        tv_details.text = name + "\n" + address + "\n" + distance

    }

}
