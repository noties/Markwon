package io.noties.markwon.app.samples.image

import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.request.LoadRequest
import coil.request.RequestDisposable
import coil.transform.RoundedCornersTransformation
import io.noties.markwon.Markwon
import io.noties.markwon.app.R
import io.noties.markwon.app.sample.Tags
import io.noties.markwon.app.sample.ui.MarkwonRecyclerViewSample
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.sample.annotations.MarkwonArtifact
import io.noties.markwon.sample.annotations.MarkwonSampleInfo
import okhttp3.OkHttpClient

@MarkwonSampleInfo(
  id = "20200803132053",
  title = "Coil inside RecyclerView",
  description = "Display images via Coil plugin in `RecyclerView`",
  artifacts = [MarkwonArtifact.IMAGE_COIL, MarkwonArtifact.RECYCLER],
  tags = [Tags.rendering, Tags.recyclerView, Tags.image]
)
class CoilRecyclerViewSample : MarkwonRecyclerViewSample() {
  override fun render() {
    val md = """
      # H1
      ## H2
      ### H3
      #### H4
      ##### H5
      
      > a quote
      
      + one
      - two
      * three
      
      1. one
      1. two
      1. three
      
      ---
      
      # Images
      
      ![img](https://picsum.photos/id/237/1024/800)
    """.trimIndent()

    val markwon = Markwon.builder(context)
      .usePlugin(CoilImagesPlugin.create(
        object : CoilImagesPlugin.CoilStore {
          override fun load(drawable: AsyncDrawable): LoadRequest {
            return LoadRequest.Builder(context)
              .transformations(
                RoundedCornersTransformation(14F)
              )
              .data(drawable.destination)
              .placeholder(R.drawable.ic_image_gray_24dp)
              .build()
          }

          override fun cancel(disposable: RequestDisposable) {
            disposable.dispose()
          }
        },
        ImageLoader.Builder(context)
          .okHttpClient(OkHttpClient())
          // this line of code makes unit tests fail
//          .placeholder(R.drawable.ic_image_gray_24dp)
          .build()))
      .build()

    val adapter = MarkwonAdapter.createTextViewIsRoot(R.layout.adapter_node)

    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.adapter = adapter

    adapter.setMarkdown(markwon, md)
    adapter.notifyDataSetChanged()
  }
}