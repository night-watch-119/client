package com.sleep119.app

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsFragment : Fragment() {

    private lateinit var datePickerBtn: Button
    private lateinit var analyticsPopupBtn: Button
    private lateinit var barChart: BarChart
    private lateinit var durationBtnGroup: RadioGroup

    private lateinit var user: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_analytics, container, false)

        datePickerBtn = view.findViewById(R.id.date_picker_btn)
        datePickerBtn.setOnClickListener {
            showDatePickerDialog()
        }

        analyticsPopupBtn = view.findViewById(R.id.analytics_popup_btn)
        analyticsPopupBtn.setOnClickListener { it ->
            var popupMenu = PopupMenu(super.requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.analytics_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                analyticsPopupBtn.text = "$it"
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

        durationBtnGroup = view.findViewById(R.id.duration_btn_group)
        barChart = view.findViewById(R.id.barchart)

        // 곡률 설정
        val barChartRender =
            CustomBarChartRender(barChart, barChart.animator, barChart.viewPortHandler)
        barChartRender.setRadius(20)
        barChart.renderer = barChartRender

        durationBtnGroup.setOnCheckedChangeListener{ _, checkedId ->
            // if (checkedId == R.id.duration_one_week_btn)
            initBarChart()
            setData()
        }
        initBarChart()
        setData()
        barChart.invalidate()
        return view
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            super.requireContext(),
            { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                onDateSelected(year, month, dayOfMonth)
            },
            initialYear,
            initialMonth,
            initialDay
        )

        datePickerDialog.show()
    }

    private fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = formatDate(year, month, dayOfMonth)
        datePickerBtn?.text = "$selectedDate"
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun initBarChart() {
        // 설명 라벨 설정
        barChart.description.isEnabled = false
        // 범주 설정
        barChart.legend.isEnabled = false

        // X, Y 바의 애니메이션 효과
        barChart.animateY(400, Easing.EaseOutCirc)

        // 바텀 좌표 값
        val xAxis: XAxis = barChart.xAxis
        // x축 위치 설정
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // 그리드 선 수평 거리 설정
        xAxis.granularity = 1f
        // x축 텍스트 컬러 설정
        xAxis.textColor = Color.BLACK
        // x축 선 설정 (default = true)
        xAxis.setDrawAxisLine(false)
        // 격자선 설정 (default = true)
        xAxis.setDrawGridLines(false)

        // 좌측 축 삭제
        barChart.axisLeft.isEnabled = false

        val rightAxis: YAxis = barChart.axisRight
        // y축 최댓값 설정
        rightAxis.axisMaximum = 100f
        // 수평선 점선 설정
        rightAxis.enableGridDashedLine(0.9f, 0.9f, 0f)
        rightAxis.gridColor = Color.parseColor("#7E9AFC")
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false)
        // 우측 텍스트 컬러 설정
        rightAxis.textColor = Color.parseColor("#7E9AFC")
    }

    // 차트 데이터 설정
    private fun setData() {
        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false)

        val valueList = ArrayList<BarEntry>()
        val title = "수면 점수"

        // 임의 데이터
        for (i in 0 until 5) {
            valueList.add(BarEntry(i.toFloat(), i * 20f ))
        }

        val barDataSet = BarDataSet(valueList, title)
        // 바 색상 설정 (ColorTemplate.LIBERTY_COLORS)
        barDataSet.setColors(Color.parseColor("#7E9AFC"))
        // 그래프 위 데이터 표시 설정
        barDataSet.setDrawValues(false)
        val data = BarData(barDataSet)
        data.barWidth = 0.15f
        barChart.data = data
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnalyticsFragment()
    }
}