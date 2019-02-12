package ca.ggolda.guessayear.activities

import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.ggolda.guessayear.R
import kotlinx.android.synthetic.main.activity_main.*
import android.text.Editable
import android.text.TextWatcher
import java.util.*
import android.app.AlertDialog
import android.media.MediaPlayer
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.widget.Button
import ca.ggolda.guessayear.data.DummyDataGen
import ca.ggolda.guessayear.data.FigureModel
import kotlinx.android.synthetic.main.dialog_result.view.*


class MainActivity : AppCompatActivity() {

    private val aliveCODE: Int = 9999
    private val startYEAR: Int = 1
    val maxYEAR: Int = 2019
    val minYEAR: Int = -2000

    var curYEAR: Int = 1
    var curScore: Int = 0


    private lateinit var figuresList: List<FigureModel>
    private var displayIndex: Int = 0
    private var scrollableRange: Int = 0
    var totalListItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Generate Data
        figuresList = DummyDataGen.genDummyList()
        totalListItems = figuresList.size

        // Set Year, Era, and Score TextViews
        edt_year.setText("$startYEAR")
        setEraTextView()
        txt_score.text = "$curScore"

        // Set New Quiz Item
        setNewQuizItem()

        // Set ScrollView OnScrollChangeListener
        scroll_years.viewTreeObserver.addOnScrollChangedListener({
            val scrollX = scroll_years.scrollX // For HorizontalScrollView
            // Change Year Based on Scroll Position
            Log.e("Scroll (X, width)", "($scrollX, $scrollableRange)")

            val positionToYear = ((scrollX.toFloat() / scrollableRange.toFloat()) * (maxYEAR - minYEAR) + minYEAR).toInt()

            setYearFromScroll(positionToYear)

        })

        // Set YearText OnChangeListener
        edt_year.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val yearString = s.toString()
                var yearInt: Int

                yearInt = if (yearString != "") {
                    yearString.toInt()
                } else if (yearString == "0") {
                    // If 0 entered, change to 1 or -1 (no zero-year)
                    edt_year.setText("1")

                    if (txt_era.text == "AD") {
                        1
                    } else {
                        -1
                    }

                } else {
                    0 // As there is no zero-year, pass 0 for an empty EditText
                }

                // Make yearInt negative if in BC
                if (txt_era.text == "BC") {
                    yearInt *= -1
                }

                // Set year from EditText int
                setYearFromText(yearInt)

            }
        })


        // Set Guess ("Confirm") Button OnClickListener
        btn_guess.setOnClickListener { guessPress() }
        txt_era.setOnClickListener { changeEra() }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Get scrollableRange by subtracting screen width from total scrollview width
        scrollableRange = scroll_years.getChildAt(0).width - scroll_years.width
        // Set ScrollView to curYEAR
        setScrollFromYear()

    }

    private fun setYearFromText(yearInt: Int) {
        if (yearInt != 0) {
            if (yearInt in minYEAR..maxYEAR) {
                if (yearInt != curYEAR) {
                    curYEAR = yearInt
                    setScrollFromYear()
                }
            } else if (yearInt > maxYEAR) {
                if (curYEAR != maxYEAR) {
                    curYEAR = maxYEAR
                    setScrollFromYear()
                }

            } else if (yearInt < minYEAR) {
                if (curYEAR != minYEAR) {
                    curYEAR = minYEAR
                    setScrollFromYear()
                }
            }
        } else {
            if (txt_era.text == "AD") {
                edt_year.hint = "" + curYEAR
            } else {
                edt_year.hint = "" + curYEAR * -1
            }
        }
    }

    private fun setScrollFromYear() {
        // Convert curYEAR to position in scrollableRange, then scrollTo(position)
        // note + 1 added to curYEAR in "AD" condition to account for no year-zero
        val scrollPosFromYear: Int =
                if (curYEAR > 0) {
                    ((((curYEAR + 1) - minYEAR).toFloat() / (maxYEAR - minYEAR).toFloat()) * scrollableRange).toInt()
                } else {
                    (((curYEAR - minYEAR).toFloat() / (maxYEAR - minYEAR).toFloat()) * scrollableRange).toInt()
                }

        scroll_years.scrollTo(scrollPosFromYear, 0)
    }

    private fun setYearFromScroll(setYear: Int) {

        Log.e("ScrollViewToYear", "$setYear")

        var tempYear = setYear

        if (tempYear > maxYEAR) {
            tempYear = maxYEAR
        } else if (tempYear < minYEAR) {
            tempYear = minYEAR
        }

        if (curYEAR != tempYear) {
            curYEAR = tempYear

            if (tempYear > 0) {
                edt_year.setText("" + tempYear)
                setEra("AD")
            } else if (setYear < 0) {
                edt_year.setText("" + tempYear * -1)
                setEra("BC")
            } else if (tempYear == 0) {
                edt_year.setText("1")
            }
        }

    }

    private fun changeEra() {
        if (txt_era.text == "AD") {
            if (curYEAR > 0) {
                curYEAR *= -1
            }
            txt_era.text = "BC"
        } else {
            if (curYEAR < 0) {
                curYEAR *= -1
            }
            txt_era.text = "AD"
        }
        setScrollFromYear()
    }

    private fun setEra(era: String) {
        if (era == "AD") {
            txt_era.text = "AD"
        } else {
            txt_era.text = "BC"
        }
    }

    private fun setNewQuizItem() {
        // Pick Random Item From List
        displayIndex = grabRandomFromList().index

        // Set Components
        img_figure.setImageDrawable(ResourcesCompat.getDrawable(resources, resIdByName(figuresList[displayIndex].imgSrc, "drawable"), null))
        txt_figure_name.text = figuresList[displayIndex].name
    }

    private fun guessPress() {
        val birthYear: Int = figuresList[displayIndex].birthYr
        val deathYear: Int = figuresList[displayIndex].deathYr

        if (curYEAR in birthYear..deathYear) {
            showDialog(figuresList[displayIndex], true)
        } else {
            showDialog(figuresList[displayIndex], false)
        }


    }

    fun setEraTextView() {
        if (curYEAR >= 0) {
            txt_era.text = "AD"
        } else {
            txt_era.text = "BC"
        }
    }


    private fun grabRandomFromList() = object {
        // Get Random Item based on available range
        var r = Random()
        val randInt = r.nextInt(totalListItems)
        val index: Int = randInt
    }

    private fun resIdByName(resIdName: String?, resType: String): Int {
        resIdName?.let {
            return resources.getIdentifier(it, resType, packageName)
        }
        throw Resources.NotFoundException()
    }

    private fun addScore() {
        curScore += 1
        txt_score.text = "$curScore"
    }

    private fun clearScore() {
        curScore = 0
        txt_score.text = "$curScore"
    }


    private fun showDialog(item: FigureModel, isCorrect: Boolean) {
        val view = layoutInflater.inflate(R.layout.dialog_result, null)
        var resultsDialog = AlertDialog.Builder(this)
                .setView(view)
                .create()

        resultsDialog.setCanceledOnTouchOutside(false)

        val dialogLayout = view.lyt_result_dialog
        val resultCorrect = view.txt_result
        val figureName = view.txt_results_name
        val figureDescription = view.txt_result_description
        val birthYear = view.txt_results_birthyr
        val deathYear = view.txt_results_deathyr

        if (isCorrect) {
            resultCorrect.text = "CORRECT!"
            figureName.text = item.name
            figureDescription.text = item.figureDescription

            if (item.birthYr < 0) {
                val tempInt = item.birthYr * -1
                birthYear.text = "" + tempInt + "BC"
            } else {
                birthYear.text = "" + item.birthYr
            }

            if (item.deathYr == aliveCODE) {
                deathYear.text = "PRESENT"
            } else {
                if (item.deathYr < 0) {
                    val tempInt = item.deathYr * -1
                    deathYear.text = "" + tempInt + "BC"
                } else {
                    deathYear.text = "" + item.deathYr
                }
            }

            addScore()

            dialogLayout.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorCorrect))

            val mp = MediaPlayer.create(this, R.raw.correct)
            mp.start()


        } else {
            resultCorrect.text = "WRONG!"
            figureName.text = item.name
            figureDescription.text = item.figureDescription

            if (item.birthYr < 0) {
                val tempInt = item.birthYr * -1
                birthYear.text = "" + tempInt + "BC"
            } else {
                birthYear.text = "" + item.birthYr
            }

            if (item.deathYr == 9999) {
                deathYear.text = "PRESENT"
            } else {
                if (item.deathYr < 0) {
                    val tempInt = item.deathYr * -1
                    deathYear.text = "" + tempInt + "BC"
                } else {
                    deathYear.text = "" + item.deathYr
                }
            }

            clearScore()

            dialogLayout.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorIncorrect))

            val mp = MediaPlayer.create(this, R.raw.incorrect)
            mp.start()

        }


        val okBtn = view.findViewById(R.id.btn_ok) as Button
        okBtn.setOnClickListener {
            if (isCorrect) {
                // Set new item since they were right!
                setNewQuizItem()
            }
            resultsDialog.dismiss()
        }

        resultsDialog.show()

    }


}

