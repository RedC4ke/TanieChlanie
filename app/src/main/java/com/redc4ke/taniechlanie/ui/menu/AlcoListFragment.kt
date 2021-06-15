package com.redc4ke.taniechlanie.ui.menu

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.menu.AlcoListAdapter
import com.redc4ke.taniechlanie.databinding.FragmentAlcoListBinding
import com.redc4ke.taniechlanie.ui.MainActivity
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import com.redc4ke.taniechlanie.ui.base.BaseListFragment
import java.io.Serializable
import java.text.Normalizer
import java.util.*


class AlcoListFragment() : BaseFragment<FragmentAlcoListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAlcoListBinding
        get() = FragmentAlcoListBinding::inflate
    private lateinit var mainActivity: MainActivity
    private lateinit var alAdapter: AlcoListAdapter
    private var list = listOf<AlcoObject>()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = requireActivity() as MainActivity
        alAdapter = AlcoListAdapter(listOf(), mainActivity, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = parentFragment?.parentFragment as BaseListFragment<*>
        parent.alcoObjectList.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                list = it
                alAdapter.update(it)
            }
        })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        super.onViewCreated(view, savedInstanceState)

        binding.alcoListRV.run {
            this.adapter = alAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onStart() {
        super.onStart()

        mainActivity.supportActionBar!!.show()
        setSearch(alAdapter)

        binding.alcolistRequestFAB.setOnClickListener {
            findNavController().navigate(R.id.request_dest)
        }
    }

    fun onItemClick(cardView: View, alcoObject: AlcoObject) {
        setTransitions(null, null)
        mainActivity.currentFragment = 99
        val rowAlcoholDetailsTransitionName =
            getString(R.string.row_alcohol_details_transition_name)
        val extras = FragmentNavigatorExtras(
            cardView to rowAlcoholDetailsTransitionName
        )
        val directions = AlcoListFragmentDirections.openDetails(alcoObject)
        findNavController().navigate(directions, extras)
    }

    private fun setSearch(adapter: AlcoListAdapter) {
        val textChangedListener: TextWatcher =
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = Normalizer.normalize(
                        s?.toString()?.toLowerCase(Locale.ROOT), Normalizer.Form.NFD
                    )
                    val filteredList: MutableList<AlcoObject> = mutableListOf()
                    list.forEach {
                        val name = Normalizer.normalize(
                            it.name.toLowerCase(Locale.ROOT), Normalizer.Form.NFD
                        )
                        if (name.contains(text)) filteredList.add(it)
                    }
                    adapter.update(filteredList as List<AlcoObject>)
                    Log.d("menuFilter", "$list")
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        binding.alcoListSearchET.addTextChangedListener(textChangedListener)

        //Set animation
        val searchBar = binding.alcoListSearchBarCV
        binding.alcoListRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 10 && searchBar.visibility == View.VISIBLE) {
                    searchBar.visibility = View.GONE
                } else if (dy < -10 && searchBar.visibility == View.GONE) {
                    searchBar.visibility = View.VISIBLE
                }
            }
        })
    }
}




