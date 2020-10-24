package com.example.riverlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * @Description
 * 代码高手
 */
class MyViewGroup:ViewGroup {
    private val space= 30
    //保存每一行的子控件
    private var totalLineViews = mutableListOf<MutableList<View>>()
    //保存所有行的高度
    private val allLineHeights = mutableListOf<Int>()

    constructor(context: Context, attrs: AttributeSet):super(context,attrs){}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //获取父容器本身的限制尺寸
        val parentWidthSize = MeasureSpec.getSize(widthMeasureSpec)

        //记录当前这一行的宽度和高度
        var currentlineWidth = space
        var currentlineHeight = 0

        //记录当前最大宽度 （最终父容器的宽度）
        var resultWidth =0
        //记录当前的最大高度（最终父容器的高度）
        var resultHeight = space



        //记录当前这一行的所有子视图
        var lineViews = mutableListOf<View>()

        for(i in 0 until childCount){
            val child = getChildAt(i)

            //获取子控件的布局参数 xml中设置的layout_width layout_height
            val lp = child.layoutParams

            //确定子控件的measureSpec
            val widthSpec  = getChildMeasureSpec(widthMeasureSpec,2*space,lp.width)
            val heightSpec = getChildMeasureSpec(heightMeasureSpec,2*space,lp.height)
            child.measure(widthSpec,heightSpec)

            //判断这个子控件在当前行还是下一行
            if (currentlineWidth+child.measuredWidth + space<= parentWidthSize){
                //添加在当前行
                lineViews.add(child)
                //改变当前行的宽度
                currentlineWidth+= child.measuredWidth+space
                //确定高度
                currentlineHeight = Math.max(currentlineHeight,child.measuredHeight)

            }else{
                //添加到下一行
                //先保存上一行的数据
                totalLineViews.add(lineViews)
                //确定当前最大宽度
                resultWidth = Math.max(resultWidth,currentlineWidth)
                //确定最大高度
                resultHeight += currentlineHeight + space
                //保存上一行的高度
                allLineHeights.add(currentlineHeight)
                //重置
                lineViews = mutableListOf()
                lineViews.add(child)
                //重置当前这一行的高度为子控件的高度
                currentlineHeight = child.measuredHeight
                currentlineWidth = space + child.measuredWidth
            }
        }

        //判断是否还有最后一行
        if (lineViews.size>0){
            //把最后一行加进去
            totalLineViews.add(lineViews)
            //确定当前最大宽度
            resultWidth = Math.max(resultWidth,currentlineWidth)
            //确定最大高度
            resultHeight += currentlineHeight + space
            //保存上一行的高度
            allLineHeights.add(currentlineHeight)
        }
        //设置父容器的尺寸
        setMeasuredDimension(resultWidth,resultHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var left = space
        var top= space
        var right=0
        var bottom=0

        val row = totalLineViews.size
        for(i in 0 until row){
            //获取当前行的个数
            val count = totalLineViews[i].size
            //获取当前行所有的子视图
            val lineViews = totalLineViews[i]
            for ( j in 0 until count){
                //取出当前的控件
                var child = lineViews[j]
                right = left+child.measuredWidth
                bottom = top+child.measuredHeight
                child.layout(left,top,right,bottom)
                //确定下一个left
                left += child.measuredWidth + space
            }
            //确定下一行的top
            top += allLineHeights[i]+space
            //下一行的left从头开始
            left = space
        }
    }
}