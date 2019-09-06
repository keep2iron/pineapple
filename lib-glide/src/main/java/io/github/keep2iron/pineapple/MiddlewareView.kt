package io.github.keep2iron.pineapple

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 *
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/06/25 20:23
 */
open class MiddlewareView : AppCompatImageView {

  constructor(context: Context) : super(context)
  constructor(
    context: Context,
    attrs: AttributeSet
  ) : super(context, attrs)

  constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int
  ) : super(context, attrs, defStyle)

  override fun onVisibilityAggregated(isVisible: Boolean) {
    super.onVisibilityAggregated(isVisible)
    if (drawable != null) {
      drawable.setVisible(true, false)
    }
  }

}