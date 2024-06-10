package com.example.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.MyStoriesPagingAdapter
import com.example.storyapp.databinding.FragmentDiscoverBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.detail.StoryDetailActivity
import com.example.storyapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment() {

    private lateinit var binding: FragmentDiscoverBinding
    private lateinit var storyAdapter: MyStoriesPagingAdapter
    private lateinit var discoverViewModel: DiscoverViewModel

    companion object {
        fun newInstance(refresh: Boolean): DiscoverFragment {
            val fragment = DiscoverFragment()
            val args = Bundle()
            args.putBoolean("refresh", refresh)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
        setupObservers()

        lifecycleScope.launch {
            discoverViewModel.getStories(requireContext()).collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = MyStoriesPagingAdapter { story, itemBinding ->
            val intent = Intent(requireContext(), StoryDetailActivity::class.java).apply {
                putExtra("story", story)
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(itemBinding.storyPicture, "shared_element_photo"),
                Pair(itemBinding.storyTitle, "story_name"),
                Pair(itemBinding.storyDescription, "story_description")
            )

            startActivity(intent, options.toBundle())
        }
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { storyAdapter.retry() }
        )

        storyAdapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility =
                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE
            binding.list.visibility =
                if (loadState.source.refresh is LoadState.Loading) View.GONE else View.VISIBLE
            binding.errorText.visibility =
                if (loadState.source.refresh is LoadState.Error) View.VISIBLE else View.GONE
        }
    }

    private fun setupViewModel() {
        val storyRepository = Injection.provideStoryRepository(requireContext())
        discoverViewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(storyRepository = storyRepository)
            )[DiscoverViewModel::class.java]
    }

    private fun setupObservers() {
        discoverViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.list.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        discoverViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.errorText.visibility = if (errorMessage != null) View.VISIBLE else View.GONE
            binding.errorText.text = errorMessage
        }

        discoverViewModel.empty.observe(viewLifecycleOwner) { isEmpty ->
            binding.emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }
}
