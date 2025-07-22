package com.example.qrscanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.qrscanner.data.MainDb
import com.example.qrscanner.data.Product
import com.example.qrscanner.ui.theme.QRScannerTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mainDb: MainDb
    var counter = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val productStateList = mainDb.dao.getAllProducts().collectAsState(initial = emptyList())
            val coroutineScope = rememberCoroutineScope()

            QRScannerTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
                    ) {
                        items(productStateList.value){ product: Product ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                            ) {
                                Text(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp),
                                    text = product.name ,
                                    textAlign = TextAlign.Center)
                            }
                        }
                    }
                    Button(
                        onClick = {
                            scan()
                        }
                    ) {
                        Text(text = "Scan")
                    }
                }
            }
        }
    }

    private val scanLauncher = registerForActivityResult(ScanContract()) {
            result ->
        if (result.contents == null){
            Toast.makeText(this, "Scan data is null",Toast.LENGTH_SHORT).show()

        }else{
            CoroutineScope(Dispatchers.IO).launch {
                val productByQr = mainDb.dao.getProductByQr(result.contents)
                if (productByQr == null){
                    mainDb.dao.insertProduct(Product(
                        null, "Product - ${counter ++}",
                        result.contents
                    ))
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Saved",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Duplicated item",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun scan(){

        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
        options.setPrompt("Scan a barcode")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        scanLauncher.launch(options)


    }
}




