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

    val startYEAR: Int = 1
    val maxYEAR: Int = 2019
    val minYEAR: Int = -2000
    var curYEAR: Int = 1
    private val aliveCODE: Int = 9999

    private lateinit var figuresList: List<FigureModel>
    var totalListItems: Int = 0
    var displayIndex: Int = 0
    var scrollWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Generate Data
        figuresList = DummyDataGen.genDummyList()
        totalListItems = figuresList.size


        // Set New Item
        setNewItem()

        // Set Year and Era Views
        edt_year.setText("" + startYEAR)
        setEraTextView()





        // Set ScrollView OnScrollChangeListener
        scroll_years.viewTreeObserver.addOnScrollChangedListener({
            val scrollX = scroll_years.scrollX // For HorizontalScrollView
            // Change Year Based on Scroll Position
            Log.e("Scroll (X, width)", "($scrollX, $scrollWidth)")

            val positionToYear = (scrollX.toFloat() / scrollWidth.toFloat()) * (maxYEAR - minYEAR) + minYEAR

            Log.e("ScrollViewToYear", "$positionToYear")

            var scrollYearSet = positionToYear.toInt()

            if (scrollYearSet > 0) {
                edt_year.setText("" + scrollYearSet)
                setEra("AD")
            } else if (scrollYearSet < 0) {
                edt_year.setText("" + scrollYearSet * -1)
                setEra("BC")
            } else if (scrollYearSet == 0) {
                edt_year.setText("1")
            }



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

                if (txt_era.text == "BC") {
                    yearInt *= -1
                }

                // Set SeekBar if differs from TextView
                if (yearInt != 0) {
                    if (yearInt in minYEAR..maxYEAR) {
                        if (yearInt != curYEAR) {
                            curYEAR = yearInt
                        }
                    } else if (yearInt > maxYEAR) {
                        yearInt = maxYEAR
                        if (yearInt != curYEAR) {
                            curYEAR = yearInt
                        }

                    } else if (yearInt < minYEAR) {
                        yearInt = minYEAR
                        if (yearInt != curYEAR) {
                            curYEAR = yearInt
                        }
                    }
                } else {
                    if (curYEAR > 0) {
                        edt_year.hint = "" + curYEAR
                    } else {
                        edt_year.hint = "" + curYEAR * -1
                    }
                }
            }
        })


        // Set Guess ("Confirm") Button OnClickListener
        btn_guess.setOnClickListener { guessPress() }
        txt_era.setOnClickListener { changeEra() }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Get ScrollView Width
        scrollWidth = scroll_years.getChildAt(0).width - scroll_years.width
        Log.e("scrollWidth)", "($scrollWidth)")

        val initScrollPos = (((curYEAR - minYEAR).toFloat() / (maxYEAR - minYEAR).toFloat()) * scrollWidth).toInt()
        scroll_years.scrollTo(initScrollPos, 0)

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
    }

    private fun setEra(era: String) {
        if (era == "AD") {
            txt_era.text = "AD"
        } else {
            txt_era.text = "BC"
        }
    }

    private fun setNewItem() {
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

            dialogLayout.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorCorrect))

            val mp = MediaPlayer.create (this, R.raw.correct)
            mp.start ()


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

            dialogLayout.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorIncorrect))

            val mp = MediaPlayer.create (this, R.raw.incorrect)
            mp.start ()

        }


        val okBtn = view.findViewById(R.id.btn_ok) as Button
        okBtn.setOnClickListener {
            if (isCorrect) {
                // Set new item since they were right!
                setNewItem()
            }
            resultsDialog.dismiss()
        }

        resultsDialog.show()

    }


}

