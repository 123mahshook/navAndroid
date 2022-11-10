package com.filepicker.animals.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.filepicker.animals.R
import com.filepicker.animals.model.Animal
import com.filepicker.animals.view_model.ListviewModel
import kotlinx.android.synthetic.main.fragment_list.*


class ListFragment : Fragment() {

    private lateinit var viewModel:ListviewModel
    private val listAdapter=AnimalListAdapter(arrayListOf())

    private val  animalListDataObserver=Observer<List<Animal>>{
        list->
        list?.let{
           animalList.visibility=View.VISIBLE
            listAdapter.updateAnimalList(it)
        }
    }

    private  val  loadingLiveDataObserver=Observer<Boolean>{
        isloading->
        loadingView.visibility=if(isloading)View.VISIBLE else View.GONE
        if (isloading){
            textView.visibility=View.GONE
            animalList.visibility=View.GONE
        }
    }

    private val errorLiveDataObserver= Observer<Boolean> {
        isError->
        textView.visibility=if(isError) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProviders.of(this).get(ListviewModel::class.java)
        viewModel.animals.observe(this,animalListDataObserver)
        viewModel.loading.observe(this,loadingLiveDataObserver)
        viewModel.loadError.observe(this,errorLiveDataObserver)
        viewModel.refresh()

        animalList.apply {
            layoutManager=GridLayoutManager(context,2)
            adapter=listAdapter
        }


        refreshLayout.setOnRefreshListener {
            animalList.visibility=View.GONE
            textView.visibility=View.GONE
            loadingView.visibility=View.VISIBLE
            viewModel.refresh()
            refreshLayout.isRefreshing=false
        }
    }

   /* override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonDetail.setOnClickListener{
            val action=ListFragmentDirections.actionDetail()
            Navigation.findNavController(it).navigate(action)
        }
    }*/
}