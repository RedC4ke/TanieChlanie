package com.redc4ke.taniechlanie.ui.menu

import android.app.SearchManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.menu.AlcoListAdapter
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.AlcoViewModel
import com.redc4ke.taniechlanie.ui.MainActivity
import java.text.Normalizer
import kotlin.collections.ArrayList



class MenuFragment : Fragment() {

    private lateinit var vm: AlcoViewModel
    private lateinit var vmData: ArrayList<AlcoObject>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlcoListAdapter

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)

        //Change tolbar text
        val actionBar: Toolbar? = requireActivity().findViewById(R.id.toolbar) as Toolbar
        actionBar!!.title = "Wybierz z listy aby zacząć:"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        //Transitions handling
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up_menu)
        exitTransition = inflater.inflateTransition(R.transition.slide_down_menu)
        reenterTransition = inflater.inflateTransition(R.transition.slide_up_menu)
        allowEnterTransitionOverlap = false
        allowReturnTransitionOverlap = false

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)
        recyclerView = rootView.findViewById(R.id.alcoList_RV) as RecyclerView

        //Connection between this fragment and activity, send a reference to the activity
        //and retrieve data from FireBase tasks
        val act = activity as MainActivity
        act.menuFrag = this
        vm = act.vm

        //Set up the recycler view
        vmData = vm.getAll().value as ArrayList<AlcoObject>
        adapter = AlcoListAdapter(vmData, this)
        adapter.update(vm)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        //Search setup
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchManager: SearchManager =
                activity?.getSystemService(android.content.Context
                        .SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = searchItem.actionView as SearchView

        searchView.queryHint = "Szukaj..."

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.setIconifiedByDefault(true)

        val queryTextListener: SearchView.OnQueryTextListener = object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        val text = Normalizer.normalize(newText?.toLowerCase(), Normalizer.Form.NFD)
                        val filteredList: MutableList<AlcoObject> = mutableListOf()
                        vmData.forEach {
                            val name = Normalizer.normalize(it.name?.toLowerCase(), Normalizer.Form.NFD)
                            if (name.contains(text)) filteredList.add(it)
                        }
                        adapter.setFilter(filteredList as ArrayList<AlcoObject>)

                        return true
                    }
        }

        searchView.setOnQueryTextListener(queryTextListener)
    }

    fun onItemClick(cardView: View, alcoObject: AlcoObject) {
        val rowAlcoholDetailsTransitionName = getString(R.string.row_alcohol_details_transition_name)
        val extras = FragmentNavigatorExtras(cardView
                to rowAlcoholDetailsTransitionName)
        val directions = MenuFragmentDirections.openDetails(alcoObject)
        findNavController().navigate(directions, extras)
    }

    fun updateRV(vm: AlcoViewModel) {
        //Update RV data
        adapter.update(vm)
    }

}




