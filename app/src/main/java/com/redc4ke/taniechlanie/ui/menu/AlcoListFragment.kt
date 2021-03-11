package com.redc4ke.taniechlanie.ui.menu

import android.app.SearchManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.menu.AlcoListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.databinding.FragmentAlcoListBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import java.io.Serializable
import java.text.Normalizer
import java.util.*


class MenuFragment(
    private var list: List<AlcoObject>
) : BaseFragment<FragmentAlcoListBinding>(), Serializable {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAlcoListBinding
        get() = FragmentAlcoListBinding::inflate
    private lateinit var alcoObjectViewModel: AlcoObjectViewModel
    private var vmData: List<AlcoObject>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlcoListAdapter
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        mainActivity = requireActivity() as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.alcoListRV
        //Connection between this fragment and activity, send a reference to the activity
        //and retrieve data from FireBase tasks
        val act = activity as MainActivity
        act.menuFrag = this
        alcoObjectViewModel = ViewModelProvider(act).get(AlcoObjectViewModel::class.java)
        //Set up the recycler view
        vmData = alcoObjectViewModel.getAll().value
        adapter = AlcoListAdapter(vmData!!, mainActivity)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        alcoObjectViewModel.getAll().observe(viewLifecycleOwner, {
            vmData = it
        })

        //Change toolbar text
        val actionBar: Toolbar = requireActivity().findViewById(R.id.toolbar) as Toolbar
        actionBar.title = "Wybierz z listy aby zacząć:"
    }

    override fun onResume() {
        super.onResume()
        setTransitions(R.transition.slide_up_menu, R.transition.slide_down_menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        //Search setup
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchManager: SearchManager =
            activity?.getSystemService(
                android.content.Context
                    .SEARCH_SERVICE
            ) as SearchManager
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.queryHint = "Szukaj..."

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.setIconifiedByDefault(true)

        val queryTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val text = Normalizer.normalize(
                        newText?.toLowerCase(Locale.ROOT), Normalizer.Form.NFD
                    )
                    val filteredList: MutableList<AlcoObject> = mutableListOf()
                    vmData?.forEach {
                        val name = Normalizer.normalize(
                            it.name.toLowerCase(Locale.ROOT), Normalizer.Form.NFD
                        )
                        if (name.contains(text)) filteredList.add(it)
                    }
                    adapter.update(filteredList as List<AlcoObject>)
                    Log.d("menuFilter", "$vmData")

                    return true
                }
            }

        searchView.setOnQueryTextListener(queryTextListener)
    }

    fun onItemClick(cardView: View, alcoObject: AlcoObject) {
        setTransitions(null, null)
        mainActivity.currentFragment = 99
        val rowAlcoholDetailsTransitionName =
            getString(R.string.row_alcohol_details_transition_name)
        val extras = FragmentNavigatorExtras(
            cardView
                    to rowAlcoholDetailsTransitionName
        )
        val directions = MenuFragmentDirections.openDetails(alcoObject, this)
        findNavController().navigate(directions, extras)
    }

    fun updateRV(l: List<AlcoObject>) {
        //Update RV data
        adapter.update(l)
    }

}




