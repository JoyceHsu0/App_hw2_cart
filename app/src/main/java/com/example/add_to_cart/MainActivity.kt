package com.example.add_to_cart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    //參考來源:老師的ch07程式碼
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val quantity = findViewById<TextView>(R.id.quantity)
        val add = findViewById<Button>(R.id.add)
        val sub = findViewById<Button>(R.id.sub)
        var cnt = quantity.text.toString().toInt()
        val id = "1"
        var pname=""
        var pprice=""
        var pimg=""

        /*--調整商品數量的按鈕和顯示  start--*/
        add.setOnClickListener {
            cnt = quantity.text.toString().toInt()+1
            quantity.text=(cnt).toString()
        }
        sub.setOnClickListener {
            if(cnt > 0) {
                cnt = quantity.text.toString().toInt()-1
                quantity.text=(cnt).toString()
            }
            else quantity.text="0"
        }
        /*--調整商品數量的按鈕和顯示  end--*/

        /*-- Get的方式 => 串接自己開發的資料庫 start--*/
        val client = OkHttpClient()
        val urlBuilder = "https://s0854006.lionfree.net/app/product-details.php".toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("id", "${id}") //暫時設定只讀取第一個商品

        val url = urlBuilder?.build().toString()
        val request = Request.Builder()
            .url(url)
            .build()

        //API裡面資料皆是JSON格式，要解析JSON格式
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    println("-----${responseBody}")

                    val jsonObject = JSONObject(responseBody)
                    pname = jsonObject.getString("p_name")
                    pprice = jsonObject.getString("price")
                    pimg = jsonObject.getString("image")

                    runOnUiThread {

                        findViewById<TextView>(R.id.p_name).text = pname
                        findViewById<TextView>(R.id.p_price).text = "${pprice} 元"
                        Picasso.get()
                            .load(pimg)
                            .into(findViewById<ImageView>(R.id.img))

                    }
                } else {
                    println("Request failed")
                    runOnUiThread {

                        findViewById<TextView>(R.id.p_name).text = "資料錯誤"
                    }

                }
            }
        })
        /*-- Get的方式 => 串接自己開發的資料庫 end--*/

        /*-- Post的方式 => 寫入自己開發的資料庫 start--*/
        val put_into_cart = findViewById<Button>(R.id.add_to_cart)
        //加入購物車功能
        put_into_cart.setOnClickListener{
            if(cnt==0) {
                Toast.makeText(this@MainActivity, "數量不可為0", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                val postID = id
                println("postID-------${postID}")
                val postNum= cnt
                println("postNum-------${postNum}")
                val postName = pname
                val postPrice = pprice
                val postImg = pimg
                println(pimg)
                //----------------
                val clientPost = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("p_id", "${postID}") //設定參數，送值
                    .add("p_num", "${postNum}")
                    .add("p_name", "${postName}")
                    .add("p_price", "${postPrice}")
                    .add("p_img", "${postImg}")
                    .build()
                val requestPost = Request.Builder()
                    .url("https://s0854006.lionfree.net/app/cart.php") //POST API的位置
                    .post(requestBody)
                    .build()

                clientPost.newCall(requestPost).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        // Handle the response body
                        println("POST-------${responseBody}")
                    }
                })
            }

        }
        /*-- Post的方式 => 寫入自己開發的資料庫 end--*/

        //直接購買功能 (尚未完成)
        val buy_now = findViewById<Button>(R.id.buy_now)
        buy_now.setOnClickListener{
            if(cnt==0) {
                Toast.makeText(this@MainActivity, "數量不可為0", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                val postID = id
                println("postID-------${postID}")
                val postNum= cnt
                println("postNum-------${postNum}")
                val postName = pname
                val postPrice = pprice
                val postImg = pimg
                //----------------
                val clientPost = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("p_id", "${postID}") //設定參數，送值
                    .add("p_num", "${postNum}")
                    .add("p_name", "${postName}")
                    .add("p_price", "${postPrice}")
                    .add("p_img", "${postImg}")
                    .build()
                val requestPost = Request.Builder()
                    .url("https://s0854006.lionfree.net/app/cart.php") //POST API的位置
                    .post(requestBody)
                    .build()

                clientPost.newCall(requestPost).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        // Handle the response body
                        println("POST-------${responseBody}")
                    }
                })
            }

            val intent = Intent(this@MainActivity, CartActivity::class.java)
            startActivityForResult(intent,1)
        }




    }
}