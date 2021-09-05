package com.elifbesik.proje451
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase.openOrCreateDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_notlar.*
import java.io.ByteArrayOutputStream
class NotlarFragment : Fragment() {
     var secilenGorsel : Uri?= null
     var secilenBitmap : Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notlar, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            ekle(it)
        }
        imageView.setOnClickListener {
            gorselSec(it)
        }
    }


    fun ekle(view:View){
        //sqlite kaydetme
        val kitapAdi = kitapAdiText.text.toString()
        val yazarAdi =yazarAdiText.text.toString()
        val Notlar =notlarText.text.toString()

        if(secilenBitmap!=null){//bitmapi veriye çevirme işlemleri
            val kucukBitmap=kucukBitmapOlustur(secilenBitmap!!,300)
            val outputStream =ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi=outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar (id INTEGER PRIMARY KEY ,kitapadi VARCHAR,yazaradi VARCHAR, notlar VARCHAR,gorsel BLOB)")

                    val sqlString ="INSERT INTO kitaplar (kitapadi,yazaradi,notlar,gorsel) VALUES (?,?,?,?)"
                    val statement =database.compileStatement(sqlString)
                    statement.bindString(1,kitapAdi)
                    statement.bindString(2,yazarAdi)
                    statement.bindString(3,Notlar)
                    statement.bindBlob(4,byteDizisi)
                    statement.execute()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
            val action= NotlarFragmentDirections.actionNotlarFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)
        }

    }


    fun gorselSec(view:View){
        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                 //izin verilmedi,izin istememizgerekiyor
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                //izin verilmiş,tekrar istemeden galeriye git
                val galeriIntent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
          }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==1){
            if(grantResults.size>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //izni aldık
                val galeriIntent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2 && resultCode == Activity.RESULT_OK && data !=null){
            secilenGorsel= data.data
            try {
                context?.let {
                    if(secilenGorsel !=null){
                        if(Build.VERSION.SDK_INT>=28){
                            val source =  ImageDecoder.createSource(it.contentResolver,secilenGorsel!!)
                            secilenBitmap =ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap=MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun kucukBitmapOlustur(kullanicininSectigi:Bitmap,maxBoyut:Int) :Bitmap{

        var width =kullanicininSectigi.width
        var height =kullanicininSectigi.height

        val bitmapOrani :Double=width.toDouble()/ height.toDouble()

        if(bitmapOrani>1){
            //görsel yatay iken
            width=maxBoyut
            val kucultulmusHeight=width/bitmapOrani
            height=kucultulmusHeight.toInt()
        }else{
            //görsel dikey iken
            height=maxBoyut
            val kucultulmusWidth =height * bitmapOrani
            width=kucultulmusWidth.toInt()
        }
        return Bitmap.createScaledBitmap(kullanicininSectigi,width,height,true)

    }
}