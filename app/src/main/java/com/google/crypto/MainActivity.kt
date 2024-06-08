package com.google.crypto

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.crypto.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvAdapter: RvAdapter
    private lateinit var data: ArrayList<Modal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        data = ArrayList()
        rvAdapter = RvAdapter(this, data)
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = rvAdapter

        binding.search1.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val filterdata=ArrayList<Modal>()
                for(item in data){
                    if(item.name.lowercase(Locale.getDefault()).contains(p0.toString().lowercase(Locale.getDefault()))){
                        filterdata.add(item)
                    }

                }
                if(filterdata.isEmpty()){
                    Toast.makeText(this@MainActivity, "No data available", Toast.LENGTH_SHORT).show()
                }
                else{
                    rvAdapter.changeData(filterdata)
                }
            }

        })

        fetchApiData()
    }
//     DA49F3B5-5348-4DE1-8FD7-AF4900C41CF8

    private fun fetchApiData() {
        val url ="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = @SuppressLint("NotifyDataSetChanged")
        object : JsonObjectRequest(Method.GET, url, null,
            Response.Listener { response ->
                binding.progressBar.isVisible=false
                try {
                    val dataArray = response.getJSONArray("data")
                    for (i in 0 until dataArray.length()) {
                        val dataObject = dataArray.getJSONObject(i)
                        val symbol = dataObject.getString("symbol")
                        val name = dataObject.getString("name")
                        val quote = dataObject.getJSONObject("quote")
                        val USD = quote.getJSONObject("USD")
                        val price =
                            String.format("$"+"%.2f",       USD.getDouble("price"))

                        data.add(Modal(name, symbol, price))
                    }
                    rvAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Network error: $it", Toast.LENGTH_LONG).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-CMC_PRO_API_KEY"] = "cb01e1dc-dd85-47b1-9e6c-c209a5abe101"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }
}
