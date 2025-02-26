package com.example.myapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.myapp.databinding.ActivityStatisticAnalysisBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class StatisticAnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticAnalysisBinding
    private lateinit var viewModel: NoteViewModel
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("USER_ID", -1)

        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        // 默认显示统计数据
        loadStatisticsData()
    }

    private fun loadStatisticsData() {
        // 获取所有笔记数据
        viewModel.loadNotes(userId).observe(this, Observer { notes ->
            // 生成随机笔记并与现有笔记合并
            val randomNotes = generateRandomNotes(userId)

            val allNotes = notes + randomNotes  // 合并现有笔记和随机生成的笔记
            //val allNotes = notes
            // 按日期统计笔记数量
            val dailyData = getDailyNoteCount(allNotes)
            updateBarChart(dailyData)
        })
    }

    private fun generateRandomNotes(userId: Int): List<Note> {
        // 模拟生成 30 条笔记
        val notes = mutableListOf<Note>()
        val calendar = Calendar.getInstance()

        for (i in 0 until 30) {
            // 随机生成一个日期
            calendar.add(Calendar.DAY_OF_YEAR, -Random().nextInt(30)) // 随机生成过去 30 天内的日期
            val timestamp = calendar.timeInMillis
            val title = "Note $i"
            val content = "Content for note $i"
            val category = "Work"
            val isFavorite = Random().nextBoolean()

            notes.add(Note(
                userId = userId,
                title = title,
                content = content,
                timestamp = timestamp,
                category = category,
                isFavorite = isFavorite
            ))

            // 重置日期为今天
            calendar.timeInMillis = System.currentTimeMillis()
        }
        return notes
    }

    private fun getDailyNoteCount(notes: List<Note>): List<DailyNoteCount> {

        // 按日期分组统计
        val dailyCounts = mutableMapOf<String, Int>()

        // 格式化日期为 yyyy-MM-dd
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (note in notes) {
            val date = dateFormat.format(Date(note.timestamp))
            dailyCounts[date] = dailyCounts.getOrDefault(date, 0) + 1
        }

        // 返回按日期排序的结果
        return dailyCounts.entries.sortedBy { it.key }.map {
            DailyNoteCount(it.key, it.value)
        }
    }

    private fun updateBarChart(data: List<DailyNoteCount>) {
        // 转换数据为 BarEntry 格式，X 轴为日期，Y 轴为数量
        val entries = data.mapIndexed { index, dailyNoteCount ->
            // X 轴为日期，Y 轴为笔记数量
            BarEntry(index.toFloat(), dailyNoteCount.count.toFloat())
        }

        // 设置数据集
        val dataSet = BarDataSet(entries, "笔记数量")
        dataSet.color = resources.getColor(android.R.color.holo_blue_light) // 设置柱状图颜色

        // 创建 BarData 并设置给图表
        val barData = BarData(dataSet)
        binding.barChart.data = barData

        // 配置图表显示
        binding.barChart.invalidate() // 刷新图表

        // 设置 X 轴为日期
        binding.barChart.xAxis.valueFormatter = DateValueFormatter(data.map { it.date })
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM // 日期显示在底部
        binding.barChart.xAxis.granularity = 1f // 设置 X 轴的最小跨度为 1（避免重复日期）
        binding.barChart.xAxis.labelRotationAngle = -45f // 设置标签旋转角度，避免重叠
    }
}

// 数据模型，用于存储日期和对应数量
data class DailyNoteCount(
    val date: String,  // 日期（格式：yyyy-MM-dd）
    val count: Int     // 对应日期的笔记数量
)

// X 轴日期格式化
class DateValueFormatter(private val dates: List<String>) :
    ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value.toInt() < dates.size) {
            dates[value.toInt()]
        } else {
            ""
        }
    }
}