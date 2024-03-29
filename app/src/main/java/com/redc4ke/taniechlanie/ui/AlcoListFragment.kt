package com.redc4ke.taniechlanie.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.redc4ke.taniechlanie.R
import com.redc4ke.taniechlanie.data.AlcoObject
import com.redc4ke.taniechlanie.data.RequestListener
import com.redc4ke.taniechlanie.data.menu.AlcoListAdapter
import com.redc4ke.taniechlanie.data.viewmodels.AlcoObjectViewModel
import com.redc4ke.taniechlanie.data.viewmodels.FilterViewModel
import com.redc4ke.taniechlanie.databinding.FragmentAlcoListBinding
import com.redc4ke.taniechlanie.ui.base.BaseFragment
import java.text.Normalizer
import java.util.*

// This could be done better and without the need of two separate parent fragments.
// TODO: 24/08/2021

class AlcoListFragment : BaseFragment<FragmentAlcoListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAlcoListBinding
        get() = FragmentAlcoListBinding::inflate
    private lateinit var mainActivity: MainActivity
    private lateinit var alAdapter: AlcoListAdapter
    private lateinit var alcoObjectViewModel: AlcoObjectViewModel
    private lateinit var filterViewModel: FilterViewModel
    private val firestoreRef = FirebaseFirestore.getInstance()

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
        setHasOptionsMenu(true)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        super.onViewCreated(view, savedInstanceState)

        filterViewModel =
            ViewModelProvider(requireActivity())[FilterViewModel::class.java]
        alcoObjectViewModel =
            ViewModelProvider(requireActivity())[AlcoObjectViewModel::class.java]

        filterViewModel.getFilteredList().observe(viewLifecycleOwner, {
            alAdapter.update(it)
        })
        filterViewModel.isActive().observe(viewLifecycleOwner, {
            binding.alcoListFilterIV.visibility = if (it) View.VISIBLE else View.GONE
        })

        alcoObjectViewModel.getAll().observe(viewLifecycleOwner, {
            binding.alcoListPB.visibility = View.GONE
            if (it.isNotEmpty()) {
                binding.alcoListTV.visibility = View.GONE
            } else {
                binding.alcoListTV.visibility = View.VISIBLE
            }
        })

        binding.alcoListRV.run {
            this.adapter = alAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        mainActivity.supportActionBar!!.show()
        setSearch()

        binding.alcolistRequestFAB.setOnClickListener {
            findNavController().navigate(R.id.request_dest)
        }

        val refreshLayout = binding.alcoListSRL
        val searchBar = binding.alcoListSearchBarCV
        val defY = searchBar.y
        val showSearchBar = ObjectAnimator.ofFloat(searchBar, "y", defY)

        refreshLayout.setOnRefreshListener {
            searchBar.visibility = View.GONE
            searchBar.y = -300f
            alcoObjectViewModel.fetch(firestoreRef, object : RequestListener {
                override fun onComplete(resultCode: Int) {
                    refreshLayout.isRefreshing = false
                    if (resultCode != RequestListener.SUCCESS) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_no_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        (requireActivity() as? MainActivity)?.getFirebaseData()
                    }
                    searchBar.visibility = View.VISIBLE
                    showSearchBar.run {
                        duration = 500
                        start()
                    }
                }
            })
        }

        //Filter
        binding.alcoListFilterBT.setOnClickListener {
            findNavController().navigate(R.id.openFilter)
        }
    }

    fun onItemClick(cardView: View, alcoObject: AlcoObject) {
        setTransitions(null, null)
        val rowAlcoholDetailsTransitionName =
            getString(R.string.row_alcohol_details_transition_name)
        val extras = FragmentNavigatorExtras(
            cardView to rowAlcoholDetailsTransitionName
        )
        val directions = AlcoListFragmentDirections.openDetails(alcoObject)
        findNavController().navigate(directions, extras)
    }

    private fun setSearch() {
        val textChangedListener: TextWatcher =
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = Normalizer.normalize(
                        s?.toString()?.lowercase(Locale.ROOT), Normalizer.Form.NFD
                    )
                    filterViewModel.setTextFilter(text)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refreshbt) {
            alcoObjectViewModel.fetch(firestoreRef, object : RequestListener {
                override fun onComplete(resultCode: Int) {
                    if (resultCode != RequestListener.SUCCESS) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_no_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        (requireActivity() as? MainActivity)?.getFirebaseData()
                    }
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }
}






