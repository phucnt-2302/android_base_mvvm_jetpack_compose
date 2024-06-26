package com.truongdc.android.base.ui.screens.movie_detail

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.truongdc.android.base.base.BaseFragment
import com.truongdc.android.base.databinding.FragmentMovieDetailBinding
import com.truongdc.android.base.utils.uistate.collectError
import com.truongdc.android.base.utils.uistate.collectErrorResponse
import com.truongdc.android.base.utils.uistate.collectEvent
import com.truongdc.android.base.utils.uistate.collectLoading
import com.truongdc.android.base.utils.uistate.render
import com.truongdc.android.base.utils.uistate.uiStateDiffRender
import com.truongdc.android.base.utils.extensions.loadImageCircleWithUrl
import com.truongdc.android.base.utils.extensions.loadImageWithUrl
import com.truongdc.android.base.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MovieDetailFragment :
    BaseFragment<FragmentMovieDetailBinding>(FragmentMovieDetailBinding::inflate) {

    private val viewModel: MovieDetailViewModel by viewModels()

    private val render = uiStateDiffRender {
        MovieDetailViewModel.UiState::movie { movie ->
            movie?.let { mMovie ->
                with(binding) {
                    imageBackDrop.loadImageWithUrl(mMovie.backDropImage)
                    imageMovie.loadImageCircleWithUrl(mMovie.urlImage)
                    textTitle.text = mMovie.title
                    textDescription.text = mMovie.overView
                    textRatting.text = mMovie.vote.toString()
                    textTotalReview.text = mMovie.voteCount.toString()
                }
            }
        }
    }

    override fun initView() {
        binding.apply {
            buttonImageBack.setOnClickListener {
                viewModel.sendEvents(MovieDetailViewModel.Event.BackToList)
            }
        }
    }

    override fun initData() {
        with(viewModel) {
            arguments?.run {
                val mMovieId = getInt(ARGUMENT_MOVIE_ID, -1)
                requestMovie(mMovieId)
            }

            render(
                lifecycleOwner = viewLifecycleOwner,
                render = render
            )

            collectEvent(lifecycle) { event ->
                return@collectEvent when (event) {
                    MovieDetailViewModel.Event.BackToList -> requireActivity().finish()
                }
            }

            collectLoading(lifecycle) { isLoading ->
                binding.progressCircular.isVisible = isLoading
            }

            collectError(lifecycle) {
                requireContext().showToast("Throwable: ${it.message}")
            }

            collectErrorResponse(lifecycle) {
                requireContext().showToast("ErrorResponse: ${it.messages}")
            }
        }
    }

    companion object {
        private const val ARGUMENT_MOVIE_ID = "ARGUMENT_MOVIE_ID"
        fun newInstance(movieId: Int) = MovieDetailFragment().apply {
            arguments = bundleOf(ARGUMENT_MOVIE_ID to movieId)
        }
    }
}
