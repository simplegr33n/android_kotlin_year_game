package ca.ggolda.guessayear.activities

import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.ggolda.guessayear.R
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.SeekBar
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

    val maxYEAR: Int = 2019
    val minYEAR: Int = -2000
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
        edt_year.setText("" + skbr_year.progress)
        setEraTextView()





        // Set ScrollView OnScrollChangeListener
        scroll_years.viewTreeObserver.addOnScrollChangedListener({
            val scrollX = scroll_years.scrollX // For HorizontalScrollView
            // Change Year Based on Scroll Position
            Log.e("Scroll (X, width)", "($scrollX, $scrollWidth)")

            val positionToYear = (scrollX.toFloat() / scrollWidth.toFloat()) * (maxYEAR - minYEAR) + minYEAR

            Log.e("ScrollViewToYear", "$positionToYear")

            val scrollYearSet = positionToYear.toInt()

            skbr_year.progress = scrollYearSet

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
                        if (skbr_year.progress != 1) {
                            skbr_year.progress = 1
                        }
                        1
                    } else {
                        if (skbr_year.progress != -1) {
                            skbr_year.progress = -1
                        }
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
                        if (yearInt != skbr_year.progress) {
                            skbr_year.progress = yearInt
                        }
                    } else if (yearInt > maxYEAR) {
                        yearInt = maxYEAR
                        if (yearInt != skbr_year.progress) {
                            skbr_year.progress = yearInt
                        }

                    } else if (yearInt < minYEAR) {
                        yearInt = minYEAR
                        if (yearInt != skbr_year.progress) {
                            skbr_year.progress = yearInt
                        }
                    }
                } else {
                    if (skbr_year.progress > 0) {
                        edt_year.hint = "" + skbr_year.progress
                    } else {
                        edt_year.hint = "" + skbr_year.progress * -1
                    }
                }
            }
        })

        // Set SeekBar OnChangeListener
        skbr_year.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            internal var progressChangedValue = skbr_year.progress

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress

                if (progress == 0) {
                    if (txt_era.text == "AD") {
                        skbr_year.progress = 1
                    } else {
                        skbr_year.progress = -1
                    }
                } else {
                    var normalizedProg = progress

                    // Normalize for TextView
                    if (normalizedProg < 0) {
                        normalizedProg *= -1
                    }
                    if (edt_year.text.toString() != "" + normalizedProg) {
                        edt_year.setText("" + normalizedProg)
                    }
                    setEraTextView()
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub -- play sound?
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Event on LetGo -- might not need
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

    }

    private fun changeEra() {
        if (txt_era.text == "AD") {
            if (skbr_year.progress > 0) {
                skbr_year.progress = skbr_year.progress * -1
            }
            txt_era.text = "BC"
        } else {
            if (skbr_year.progress < 0) {
                skbr_year.progress = skbr_year.progress * -1
            }
            txt_era.text = "AD"
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

        if (skbr_year.progress in birthYear..deathYear) {
            showDialog(figuresList[displayIndex], true)
        } else {
            showDialog(figuresList[displayIndex], false)
        }


    }

    fun setEraTextView() {
        if (skbr_year.progress >= 0) {
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

