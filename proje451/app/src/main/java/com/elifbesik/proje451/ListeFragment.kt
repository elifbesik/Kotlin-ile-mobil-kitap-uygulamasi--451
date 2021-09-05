package com.elifbesik.proje451
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liste.*
class ListeFragment : Fragment() {
    var kitapAdiListesi =ArrayList<String>()
    var kitapIdListesi =ArrayList<Int>()
    private lateinit var listeAdapter :ListeRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeAdapter = ListeRecyclerAdapter(kitapAdiListesi,kitapIdListesi)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=listeAdapter

        sqlVeriAlma()
    }

    fun sqlVeriAlma(){
        try {
            activity?.let {
                val database = it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE,null)
                var cursor =database.rawQuery("SELECT *FROM kitaplar ",null)
                val kitapAdiIndex = cursor.getColumnIndex("kitapadi")
                val kitapIdIndex = cursor.getColumnIndex("id")

                kitapAdiListesi.clear()
                kitapIdListesi.clear()

                while(cursor.moveToNext()){
                 kitapAdiListesi.add(cursor.getString(kitapAdiIndex))
                    kitapIdListesi.add(cursor.getInt(kitapIdIndex))

                }
                listeAdapter.notifyDataSetChanged()
                cursor.close()


            }

        }catch (e:Exception){
            
        }
    }
}