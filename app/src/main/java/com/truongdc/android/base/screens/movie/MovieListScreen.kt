package com.truongdc.android.base.screens.movie

//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.truongdc.android.base.R
import com.truongdc.android.base.components.LoadingContent
import com.truongdc.android.base.screens.movie_detail.MovieDetailActivity
import com.truongdc.android.base.utils.extensions.showToast
import com.truongdc.android.base.utils.uistate.collectErrorEffect
import com.truongdc.android.base.utils.uistate.collectErrorResponseEffect
import com.truongdc.android.base.utils.uistate.collectLoadingWithLifecycle
import com.truongdc.android.base.utils.uistate.collectWithLifecycle
import com.truongdc.android.core.model.Movie
import com.truongdc.android.core.utils.Constant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MovieListScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: MovieListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.collectLoadingWithLifecycle()
    val uiState by viewModel.collectWithLifecycle()
    with(viewModel) {
        collectErrorEffect {
            context.showToast("Throwable: ${it.message}")
        }
        collectErrorResponseEffect {
            context.showToast("ErrorResponse: ${it.messages}")
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Movie list", overflow = TextOverflow.Ellipsis, color = Black)
            }, colors = TopAppBarDefaults.topAppBarColors(White))
        },
    ) {
        LaunchedEffect(key1 = Unit) {
            viewModel.requestMovie()
        }
        LoadingContent(isLoading = isLoading) {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(uiState.mListMovie) { movie ->
                    MovieItem(movie = movie, onClickItem = { movieId ->
                        val intent = Intent(context, MovieDetailActivity::class.java)
                        intent.putExtra("MOVIE_ID", movieId)
                        context.startActivity(intent)
                    })
                }
            }
        }
    }
}

@Composable
private fun MovieItem(movie: Movie, onClickItem: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .background(White)
            .fillMaxWidth(),
        onClick = {
            onClickItem(movie.id)
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.Unspecified,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row {
                Image(
                    painter = rememberAsyncImagePainter(Constant.BASE_URL_IMAGE + movie.urlImage),
                    contentDescription = null,
                    modifier = Modifier
                        .width(80.dp)
                        .height(70.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = movie.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(text = movie.overView, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = movie.vote.toString())
                Spacer(modifier = Modifier.size(6.dp))
                Image(painter = painterResource(id = R.drawable.ic_star), contentDescription = null)
            }
        }
    }
}